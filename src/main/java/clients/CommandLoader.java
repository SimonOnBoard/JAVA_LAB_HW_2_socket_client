package clients;

import programs.clients.ProgramClientChatStart;

import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.Map;

public class CommandLoader {
    public static Map<String, String> params = null;
    public static Map<String,String> loadCurrentParamd(){
        System.out.println("Введите команды|| доступно get messages");
        String line = ProgramClientChatStart.scanner.nextLine();
        params = new HashMap<>();
        params.put("command",line);
        if(line.equals("get messages")) return startPaginationInfoLoad();
        return null;
    }

    private static Map<String, String> startPaginationInfoLoad() {
        System.out.println("Введите номер страницы");
        Long number = ProgramClientChatStart.scanner.nextLong();
        System.out.println("ВВедите размер страницы");
        Long size = ProgramClientChatStart.scanner.nextLong();
        params.put("page","" + number);
        params.put("size","" + size);
        return params;
    }
}
