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
//            status == 200 && time == 500

            String type = json.get("type").toString();
            ArrayList<JSONObject> res_arr = (new DataNodeManager()).sendSearchRequest(json);
            JSONObject temp = new JSONObject();
            temp.put("result", res_arr);
//            Logging.logger.info(((JSONArray)res_arr).toJSONString());
            Logging.logger.info(temp.toString());

            if(type.equalsIgnoreCase("count")){

                String key = json.get("key").toString();
                Iterator<JSONObject> iter1 = res_arr.iterator();
                Map<String, Integer> res_map = new HashMap<>();

                while (iter1.hasNext()) {
                    JSONObject _d = iter1.next();
                    Iterator<String> i = _d.keySet().iterator();
                    String _key = i.next();
                    int cnt = res_map.getOrDefault(_key, 0);
                    cnt++;
                    res_map.put(_key, cnt);
                }

                JSONObject data = new JSONObject();
                for( Map.Entry<String, Integer> entry : res_map.entrySet() ) {
                    String _key = entry.getKey();
                    Integer value = entry.getValue();
                    data.put(_key, value);
                }

                JSONObject res = new JSONObject();
                res.put("key", key);
                res.put("results", data);
//                Logging.logger.info("response datat " + res.toString());

                byte[] response = Helper.decodeToStr(res).getBytes();
                Helper.responseToClient(httpExchange, response);


            }else if(type.equalsIgnoreCase("max")){

                String key = json.get("key").toString();
                Iterator<JSONObject> iter = res_arr.iterator();
//                if (!iter.hasNext()){
////                    return new_result;
//                    JSONObject res = new JSONObject();
//                    res.put("result", res_arr);
//
//                    byte[] response = Helper.decodeToStr(res).getBytes();
//                    Helper.responseToClient(httpExchange, response);
//                    return;
//                }

                JSONObject max_value = new JSONObject();
                max_value.put(key, 0);
//                iter.next();

                while(iter.hasNext()){
                    JSONObject temp1 = iter.next();
                    if (Long.parseLong(max_value.get(key).toString()) < (Long.parseLong(temp1.get(key).toString()))){
                        max_value.put(key, (Long.parseLong(temp1.get(key).toString())));
                    }

                }

                JSONObject res = new JSONObject();
                res.put("result", max_value);

                byte[] response = Helper.decodeToStr(res).getBytes();
                Helper.responseToClient(httpExchange, response);
            }
            else if(type.equalsIgnoreCase("min")){

                String key = json.get("key").toString();
                Iterator<JSONObject> iter = res_arr.iterator();


                JSONObject min_value = new JSONObject();
                min_value.put(key, 999999999);

                while(iter.hasNext()){
                    JSONObject temp1 = iter.next();
                    if (Long.parseLong(min_value.get(key).toString()) > (Long.parseLong(temp1.get(key).toString()))){
                        min_value.put(key, (Long.parseLong(temp1.get(key).toString())));
                    }

                }

                JSONObject res = new JSONObject();
                res.put("result", min_value);

                byte[] response = Helper.decodeToStr(res).getBytes();
                Helper.responseToClient(httpExchange, response);
            }


            else if(type.equalsIgnoreCase("search")) {
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
