package programs.clients;

import clients.SocketClient;
import com.beust.jcommander.JCommander;
import service.Args;

import java.util.Scanner;

public class ProgramClientChatStart {
    public static Scanner scanner;

    public static void main(String[] args) {
        Args args1 = new Args();
        JCommander jCommander = new JCommander(args1);
        jCommander.parse(args);
        scanner = new Scanner(System.in);
        SocketClient client = new SocketClient(scanner);
        client.startConnection(args1.getAddress(), Integer.parseInt(args1.getPort()));
        Thread.currentThread().setPriority(5);
        while (true) {
            synchronized (scanner) {
                while (!client.isAvaliavle) {
                    try {
                        scanner.wait();
                    } catch (InterruptedException e) {
                        throw new IllegalStateException(e);
                    }
                }
                String message = scanner.nextLine();
                client.isAvaliavle = false;
                client.sendMessage(message);
                if(!client.isAvaliavle){
                    scanner.notify();
                }
            }
        }
    }
}