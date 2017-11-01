package master.views;

import annotations.ContentType;
import annotations.URLAnnotation;
import collog.Collog;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import helper.Helper;

import master.DataNodeManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@URLAnnotation("master/data/search/")
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
             *      "type" : "search", "range", "aggreation", "max", "min"
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

            String type = json.get("type").toString();

            ArrayList<JSONObject> res_arr = (new DataNodeManager()).sendSearchRequest(json);
//            res_arr.toArray()
            if(type.equalsIgnoreCase("count")){
                Iterator<JSONObject> iter1 = res_arr.iterator();
                Map<String, Integer> res_map = new HashMap<>();

                while (iter1.hasNext()) {
                    JSONObject _d = iter1.next();
                    Iterator<String> i = _d.keySet().iterator();
                    String key = i.next();
                    int cnt = res_map.getOrDefault(key, 0);
                    cnt++;
                    res_map.put(key, cnt);
                }

                JSONObject data = new JSONObject();
                for( Map.Entry<String, Integer> entry : res_map.entrySet() ) {
                    String key = entry.getKey();
                    Integer value = entry.getValue();
                    data.put(key, value);
                }

                JSONObject res = new JSONObject();
                res.put("key",json.get("key").toString());
                res.put("results",data);

                byte[] response = Helper.decodeToStr(res).getBytes();
                Helper.responseToClient(httpExchange, response);


            }else if(type.equalsIgnoreCase("search")) {
                JSONObject res = new JSONObject();
                res.put("result", res_arr);

                byte[] response = Helper.decodeToStr(res).getBytes();
                Helper.responseToClient(httpExchange, response);
            }


        } else if (method.equalsIgnoreCase("OPTIONS")) {
            Helper.optionsResponse(httpExchange);
        }
    }

}
