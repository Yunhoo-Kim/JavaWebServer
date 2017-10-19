package data.views;

import annotations.URLAnnotation;
import collog.Collog;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import data.FileSearchHandler;
import helper.Helper;

import master.DataNodeManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.ArrayList;

@URLAnnotation("data/search/")
public class DataSearchHandler implements HttpHandler {


    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();

        if (method.equalsIgnoreCase("GET")) {
            byte[] response = Collog.getInstance().getSlaveTable().toString().getBytes();
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
            ArrayList<JSONObject> ress = new ArrayList<>();
            System.out.println("Searching!!!!!!!1111111");
            ress.addAll((new FileSearchHandler()).search(json,0));
            ress.addAll((new FileSearchHandler()).search(json,1));
            ress.addAll((new FileSearchHandler()).search(json,2));
            ress.addAll((new FileSearchHandler()).search(json,3));

            JSONArray res_arr = new JSONArray();
            res_arr.addAll(ress);
            JSONObject res = new JSONObject();
            res.put("results", res_arr);
            System.out.println("Searching!!!!!!!222222222" + res.toString());
            byte[] response = Helper.decodeToStr(res).getBytes();
            Helper.responseToClient(httpExchange, response);


        } else if (method.equalsIgnoreCase("OPTIONS")) {
            Helper.optionsResponse(httpExchange);
        }
    }

}
