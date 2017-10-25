package data.views;

import annotations.URLAnnotation;
import collog.Collog;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import data.DataNodeMetaStorage;
import data.FileSearchHandler;
import helper.Helper;

import logging.Logging;
import master.DataNodeManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.*;

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

            JSONObject res = new JSONObject();
            if (json.get("type").toString().equals("search")) {
                Logging.logger.info("search request!");
                ArrayList<JSONObject> ress = new ArrayList<>();

                JSONObject my_info = (new DataNodeMetaStorage()).getMetaInfo();
                Logging.logger.info(my_info.toString());
                ArrayList<Long> shards = (ArrayList<Long>) my_info.get("shards");

                Iterator<Long> iter = shards.iterator();
                while(iter.hasNext()) {
                    int a = Integer.valueOf(iter.next().intValue());
                    ress.addAll((new FileSearchHandler()).search(json, a));
                }
                JSONArray res_arr = new JSONArray();
                res_arr.addAll(ress);
                res.put("results", res_arr);
                Logging.logger.info("search request! end");

//            System.out.println("Searching!!!!!!!222222222" + res.toString());


            }else if(json.get("type").toString().equals("count")){
                try {
                    ArrayList<JSONObject> ress = new ArrayList<>();

                    JSONObject my_info = (new DataNodeMetaStorage()).getMetaInfo();
                    Logging.logger.info(my_info.toString());
                    ArrayList<Long> shards = (ArrayList<Long>) my_info.get("shards");

                    Iterator<Long> iter = shards.iterator();
                    while (iter.hasNext()) {
                        int a = Integer.valueOf(iter.next().intValue());
                        ress.addAll((new FileSearchHandler()).countSearch(json, a));
                    }
                    JSONArray res_arr = new JSONArray();
                    res_arr.addAll(ress);
                    res.put("results", res_arr);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            byte[] response = Helper.decodeToStr(res).getBytes();
            Helper.responseToClient(httpExchange, response);
        } else if (method.equalsIgnoreCase("OPTIONS")) {
            Helper.optionsResponse(httpExchange);
        }
    }

}
