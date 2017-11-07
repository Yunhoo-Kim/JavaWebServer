package data.views;

import annotations.ContentType;
import annotations.URLAnnotation;
import collog.Collog;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import data.FileWriteHandler;
import helper.Helper;
import org.json.simple.JSONObject;

import java.io.IOException;

@URLAnnotation("data/input/replica/")
public class DataReplicaInputHandler implements HttpHandler {

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
             *      "shard" : "shard number",
             *
             *}
             *
             */

            /**
             * ToDo: Json format check
             */

//            Collog.getInstance().addSlave(json);

            /**
             * Send response to client.
             */
            int shard = Integer.parseInt(json.get("shard").toString());
            json.remove("shard");
            if(!json.containsKey("@timestamp")){
                json.put("@timestamp", ((double)System.currentTimeMillis())/1000);
            }
            byte[] response = Helper.decodeToStr(json).getBytes();
            Helper.responseToClient(httpExchange, response);
            (new FileWriteHandler()).replicaWrite(json, shard);

        }else if(method.equalsIgnoreCase("OPTIONS")){
            Helper.optionsResponse(httpExchange);
        }
    }

}
