package data.views;
import annotations.ContentType;
import annotations.URLAnnotation;
import annotations.URLMethod;
import collog.Collog;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import helper.Helper;
import logging.Logging;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Scanner;

@URLAnnotation("data/get/shard/")
public class AllocationShardFromDataNodeHandler implements HttpHandler {

    @ContentType("application/json")
    @URLMethod("GET")
    public byte[] getResponse(){
        String a = "{'abc':'abc'}";
        return a.getBytes();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();

        if(method.equalsIgnoreCase("GET")){
            byte[] response = Collog.getInstance().getSlaveTable().toString().getBytes();
            Helper.responseToClient(httpExchange, response);

        }else if(method.equalsIgnoreCase("POST")){
            /**
             * Read request body from client
             */
            String request_body = Helper.getRequestBody(httpExchange.getRequestBody());

            JSONObject json = Helper.encodeToJson(request_body);
            /**
             * data node register body structure
             *{
             *      "node_id" : "node_indentification",
             *      "shard" : "Shard number to get"
             * }
             *
             */

            /**
             * ToDo: Json format check
             */

//            Collog.getInstance().addSlave(json);

            /**
             * Send response to client.
             */


            String file_name = String.format("data/%d/data.txt", json.get("shard"));
            File file = new File(file_name);

            if(!file.exists()){
                Helper.responseToClient(httpExchange, "".getBytes());

            }else {
                Scanner scanner = new Scanner(new FileReader(file_name));
                String response = "";
                String str;
                while (scanner.hasNextLine()) {
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
