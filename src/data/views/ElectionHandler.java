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

import java.io.IOException;

@URLAnnotation("data/election/")
public class ElectionHandler implements HttpHandler {


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
             *      "ip" : "node_ip",
             *      "port" : "port",
             *      "new" : "whether it is first time to register or not",
             *      "having_shards" : [0,1,2] # List of Shards number data node have,
             *}
             *
             */



//            Collog.getInstance().addSlave(json);

            /**
             * Send response to client.
             */
//            Logging.logger.info("###############receive election message###################");
            byte[] response = Helper.decodeToStr(json).getBytes();
            Helper.responseToClient(httpExchange, response);
            Collog.getInstance().startElection();

        }else if(method.equalsIgnoreCase("OPTIONS")){
            Helper.optionsResponse(httpExchange);
        }
    }

}
