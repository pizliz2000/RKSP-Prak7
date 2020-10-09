package server;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;

public class ClientHandler implements Runnable {
    private static Socket clientDialog;

    public ClientHandler(Socket client) {
        this.clientDialog = client;
    }

    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        BufferedOutputStream dataOut = null;
        String fileRequested = null;
        String fio = "Job by: Gorelkin Alexandr Sergeevich;";
        String group = "Number of group: IKBO-16-18;";
        String number = "Number of work: 7;";
        String text = "Text of work: Determine the percentage of lowercase and uppercase letters to the total number of characters in it;";

        try {
            // канал чтения из сокета
            in = new BufferedReader(new InputStreamReader(clientDialog.getInputStream()));
            // канал записи в сокет (для HEADER)
            out = new PrintWriter(clientDialog.getOutputStream());
            // канал записи в сокет (для данных)
            dataOut = new BufferedOutputStream(clientDialog.getOutputStream());


            // первая строка запроса
            String input = in.readLine();
            // разбираем запрос по токенам
            StringTokenizer parse = new StringTokenizer(input);
            String method = parse.nextToken().toUpperCase(); // получаем HTTP метод от клиента
            // текст запроса от клиента
            fileRequested = parse.nextToken().toLowerCase();




            System.out.println("Method: " + method);



            // пока поддерживаем GET and HEAD запросы
            if (method.equals("GET") || method.equals("HEAD")) {

                String content = getContentType(fileRequested);
                String body = getBody(fio, group,  number,  text, fileRequested.substring(1));

                if (method.equals("GET")) {
                    // GET method - возвращаем ответ

                    // шлем HTTP Headers
                    out.println("HTTP/1.1 200 OK");
                    out.println("Server: Java HTTP Server : 1.0");
                    out.println("Date: " + new Date());
                    out.println("Content-type: " + content);
                    //Длина ответа - эхо запроса без первого "/"
                    out.println("Content-length: " + body.length());
                    out.println(); // Пустая строка между headers и содержимым!
                    out.flush();

                    dataOut.write(body.getBytes(), 0, body.length());

                    dataOut.flush();
                }

                System.out.println("Ответ отослан: " + body);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    double countString = 0;
//                    for(int y = 0; y < clientCommand.length(); y++){
//        if(Character.isLowerCase(clientCommand.charAt(y)) || Character.isUpperCase(clientCommand.charAt(y))){
//            countString=countString+1;
//        }
//    }
//    double percentString=(countString/clientCommand.length())*100;


    // Возвращаем поддерживываемый  MIME Types
    private String getContentType(String fileRequested) {
            return "text/html";
    }

    private String getBody(String fio, String group, String number, String text, String request) {
        double countString = 0;
                    for(int y = 0; y < request.length(); y++){
        if(Character.isLowerCase(request.charAt(y)) || Character.isUpperCase(request.charAt(y))){
            countString=countString+1;
        }
    }
    String percentString= String.valueOf((countString/request.length())*100);


        return "<h1>"+ fio + "\n" + group + "\n" + number + "\n" + text + "\n" +"Procent:"+ percentString + "</h1>";
    }
}