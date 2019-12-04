package clients;

import model.MessageDTO;
import programs.clients.ProgramClientChatStart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class SocketClient {

    // поле, содержащее сокет-клиента
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private JsonTransfer transfer;
    public boolean isAvaliavle = false;

    public SocketClient(Scanner scanner) {
        this.transfer = new JsonTransfer();
    }

    // начало сессии - получаем ip-сервера и его порт
    public void startConnection(String ip, int port) {
        try {
            // создаем подключение
            clientSocket = new Socket(ip, port);
            // получили выходной поток
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            // входной поток
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            // запустили слушателя сообщений
            new Thread(receiverMessagesTask).start();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static Map<String, String> params = null;

    public void sendMessage(String message) {
        if (message.equals("Login") || message.equals("Logout") || message.equals("Registration")) {
            message = transfer.prepareJsonObject(message, null);
        } else {
            if (message.equals("Command")) {
                params = CommandLoader.loadCurrentParamd();
                message = transfer.prepareJsonObject("Command", params);
                isAvaliavle = true;
                ProgramClientChatStart.scanner.notify();
                if(message == null){
                    return;
                }
            } else {
                if (message.equals("")) {
                    isAvaliavle = true;
                    ProgramClientChatStart.scanner.notify();
                    return;
                }
                params = new HashMap<>();
                params.put("message", message);
                message = transfer.prepareJsonObject("Message", params);
            }
        }
        this.send(message);
    }

    private void loadCurrentCommand() {
        System.out.println("Введите комманду || доступная getMessage");
    }

    public void send(String message) {
        System.out.println(message);
        out.println(message);
    }

    private Runnable receiverMessagesTask = new Runnable() {
        @Override
        public void run() {
            JsonTransfer transfer = new JsonTransfer();
            Thread.currentThread().setPriority(10);
            while (true) {
                try {
                    String response = in.readLine();
                    if (response == null) {
                        this.stopConnection();
                    }
                    transfer.loadMessageMap(response);
                    if (response != null) {
                        String responseCopy = response;
                        response = transfer.getHeader();
                        if (response.equals("login") | response.equals("registration")) {
                            synchronized (ProgramClientChatStart.scanner) {
                                while (isAvaliavle) {
                                    try {
                                        ProgramClientChatStart.scanner.wait();
                                    } catch (InterruptedException e) {
                                        throw new IllegalStateException(e);
                                    }
                                }
                                this.printResp(response);
                                if (response.equals("login")) this.startLoginProcess(transfer);
                                if (response.equals("registration")) this.startRegistrationProcess(transfer);
                            }
                        } else {
                            if (transfer.getHeader().equals("pagination")) {
                                List<MessageDTO> messages = transfer.getListMessages(responseCopy);
                                for(MessageDTO messageDTO: messages){
                                    System.out.println(messageDTO.getName() + " on " +
                                            new Timestamp(messageDTO.getDateTime()).toLocalDateTime() + " : " + messageDTO.getText());
                                }
                            } else {
                                response = transfer.readMessage();
                                System.out.println(response);
                            }
                            if (!transfer.getHeader().equals("error") | transfer.getHeader().equals("menuItem")
                                    | transfer.equals("Message")) {
                                    synchronized (ProgramClientChatStart.scanner) {
                                        if (!isAvaliavle) {
                                            isAvaliavle = true;
                                            ProgramClientChatStart.scanner.notify();
                                        }
                                    }
                                }

                        }
                    }
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }
        }


        private void startRegistrationProcess(JsonTransfer transfer) {
            System.out.println("Введите логин");
            String login = ProgramClientChatStart.scanner.next();
            System.out.println("Введите пароль");
            String password = ProgramClientChatStart.scanner.next();
            System.out.println("Введите никнейм");
            String nick = ProgramClientChatStart.scanner.next();
            Map<String, String> params = new HashMap<>();
            params.put("login", login);
            params.put("password", password);
            params.put("nick", nick);
            String jsonObject = transfer.prepareJsonObject("registr", params);
            out.println(jsonObject);
            isAvaliavle = true;
        }

        private void printResp(String response) {
            response = transfer.readMessage();
            System.out.println(response);
        }

        private void startLoginProcess(JsonTransfer transfer) {
            System.out.println("Введите логин");
            String login = ProgramClientChatStart.scanner.next();
            System.out.println("Введите пароль");
            String password = ProgramClientChatStart.scanner.next();
            Map<String, String> params = new HashMap<>();
            params.put("login", login);
            params.put("password", password);
            String jsonObject = transfer.prepareJsonObject("Login", params);
            out.println(jsonObject);
        }

        public void stopConnection() {
            try {
                in.close();
                out.close();
                clientSocket.close();
                Thread.currentThread().stop();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    };
}
