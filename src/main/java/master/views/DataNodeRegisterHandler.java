package master.views;

import annotations.ContentType;
import annotations.URLAnnotation;
import collog.Collog;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import helper.Helper;

import master.MasterServer;
import master.ShardsAllocator;
import org.json.simple.JSONObject;
import java.io.*;

@URLAnnotation("master/node/register/")
public class DataNodeRegisterHandler implements HttpHandler {

    @ContentType("application/json")
    public byte[] getResponse(){
        String a = "{'abc':'abc'}";
        return a.getBytes();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
//        System.out.println("Post input");
        String method = httpExchange.getRequestMethod();
//        System.out.println("Method : " + method);
        if(method.equalsIgnoreCase("GET")){
            byte[] response = this.getResponse();
            Helper.responseToClient(httpExchange, response);

        }else if(method.equalsIgnoreCase("POST")){
//            System.out.println("Post input");
            /**
             * Read request body from client
             */
            String request_body = Helper.getRequestBody(httpExchange.getRequestBody());

            JSONObject json = Helper.encodeToJson(request_body);
            /**
             * data node register body structure
             *{
             *      "node_id" : "node_indentification",
             *      "ip" : "node_ip",
             *      "port" : "port",
             *      "new" : "whether it is first time to register or not",
             *      "having_shards" : [0,1,2] # List of Shards number data node have,
             *}
             *
             */

            /**
             * ToDo: Json format check
             */

            Collog.getInstance().addSlave(json);


            /**
             * Send response contain slave tables to client.
             */
//            JSONObject data = new JSONObject()
            byte[] response = Collog.getInstance().getSlaveTable().toString().getBytes();
            Helper.responseToClient(httpExchange, response);

            /**
             * ToDo: reallocation shards to slave
             */
            (new ShardsAllocator()).allocateShards();


            if(Collog.getInstance().getSlaveTable().size() == 1) {
                MasterServer.runInputModule();
            }
//            byte[] response = Collog.getInstance().getSlaveTable().toString().getBytes();
//            Helper.responseToClient(httpExchange, response);

        }else if(method.equalsIgnoreCase("OPTIONS")){
            Helper.optionsResponse(httpExchange);
        }
    }

}
