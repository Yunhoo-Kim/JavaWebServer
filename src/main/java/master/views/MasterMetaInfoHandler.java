package master.views;

import annotations.ContentType;
import annotations.URLAnnotation;
import collog.Collog;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import helper.Helper;

import logging.Logging;
import master.DataNodeManager;
import master.MasterMetaStorage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;

@URLAnnotation("master/meta/")
public class MasterMetaInfoHandler implements HttpHandler {


    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();

        if (method.equalsIgnoreCase("GET")) {
            byte[] response = MasterMetaStorage.getInstance().getMetaData().toString().getBytes();
            Helper.responseToClient(httpExchange, response);


        } else if (method.equalsIgnoreCase("POST")) {
            /**
             * Read request body from client
             */
            String request_body = Helper.getRequestBody(httpExchange.getRequestBody());

            JSONObject json = Helper.encodeToJson(request_body);
            /**
             * data search request body structure like this
             *{
             *      "type" : "search",
             *      "key" : "key",
             *      "value" : "value"
             *}
             *
             */

            /**
             * Send response to client.
             */

            /**
             * ToDo: Json format check
             */

//            JSONArray res_arr = (new DataNodeManager()).sendSearchRequest(json);
//            JSONObject res = new JSONObject();
//            res.put("result", res_arr);

            byte[] response = Helper.decodeToStr(json).getBytes();
            Helper.responseToClient(httpExchange, response);


        } else if (method.equalsIgnoreCase("OPTIONS")) {
            Helper.optionsResponse(httpExchange);
        }
    }

}
