package data.views;

import annotations.URLAnnotation;
import collog.Collog;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import data.FileWriteHandler;
import helper.Helper;
import org.json.simple.JSONObject;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;

@URLAnnotation("data/get/shard/")
public class AllocationShardFromDataNodeHandler implements HttpHandler{

     @Override
     public void handle(HttpExchange httpExchange) throws IOException {
         String method = httpExchange.getRequestMethod();

         if(method.equalsIgnoreCase("GET")){
             byte[] response = Collog.getInstance().getSlaveTable().toString().getBytes();
             Helper.responseToClient(httpExchange, response);

         }else if(method.equalsIgnoreCase("POST")){

             String request_body = Helper.getRequestBody(httpExchange.getRequestBody());

             JSONObject json = Helper.encodeToJson(request_body);


             /**
              * ToDo: Json format check
              *
              * 오버노드에서 파일 읽고 데이터 리턴
              *
              */
             String file_name = String.format("data/%d/data.txt",json.get("shard"));
             File file = new File(file_name);

             if(!file.exists()){
                Helper.responseToClient(httpExchange, "".getBytes());
             }else {
                 Scanner scanner = new Scanner(new FileReader(file_name));
                 String response = "";

                 while (scanner.hasNext()) {
                     response += scanner.nextLine() + "\n";
                 }

                 byte[] file_bytes = response.getBytes(Charset.forName("UTF-8"));
                 Helper.responseToClient(httpExchange, file_bytes);

             }
         }else if(method.equalsIgnoreCase("OPTIONS")){
             Helper.optionsResponse(httpExchange);
         }
     }
}
