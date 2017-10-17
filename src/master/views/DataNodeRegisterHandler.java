package master.views;

import annotations.ContentType;
import annotations.URLAnnotation;
import collog.Collog;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import helper.Helper;

import org.json.simple.JSONObject;
import java.io.*;

@URLAnnotation("_meta/")
public class DataNodeRegisterHandler implements HttpHandler {

    @ContentType("application/json")
    public byte[] getResponse(){
        String a = "{'abc':'abc'}";
        return a.getBytes();
    }
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();

        if(method.equalsIgnoreCase("GET")){
            byte[] response = this.getResponse();
            Headers responseHeaders = httpExchange.getResponseHeaders();
            httpExchange.sendResponseHeaders(200,response.length);
            responseHeaders.set("Content-Type","application/json;charset=utf-8");
            OutputStream responseBody = httpExchange.getResponseBody();
            responseBody.write(response);
            responseBody.close();

        }else if(method.equalsIgnoreCase("POST")){
            /**
             * Read request body from client
             */
            String request_body = Helper.getPostRequestBody(httpExchange.getRequestBody());

            JSONObject json = Helper.encodeToJson(request_body);
            /**
             * data node register body structure
             * {
             *      "node_id" : "node_indentification",
             *      "ip" : "node_ip",
             *      "port" : "port",
             *      "new" : "whether it is first time to register or not",
             *      "having_shards" : [0,1,2] # List of Shards number data node have,
             * }
             *
             */

            /**
             * ToDo: Json format check
             */

            Collog.getInstance().addSlave(json);

            /**
             * Send response to client.
             */
            byte[] response = Helper.decodeToStr(json).getBytes();
            Helper.responseToClient(httpExchange,response);
//            Headers responseHeaders = httpExchange.getResponseHeaders();
//            httpExchange.sendResponseHeaders(200,response.length);
//            responseHeaders.set("Content-Type","application/json;charset=utf-8");
//            OutputStream responseBody = httpExchange.getResponseBody();
//            responseBody.write(response);
//
//            responseBody.close();


        }
    }

}
