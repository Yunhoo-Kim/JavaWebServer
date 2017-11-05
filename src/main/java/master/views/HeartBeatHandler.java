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

@URLAnnotation("master/heartbeat/")
public class HeartBeatHandler implements HttpHandler {


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
             * heart beat request body structure like this
             *
             */
            JSONObject res = new JSONObject();
            res.put("data_nodes",Collog.getInstance().getSlaveTable());
            byte[] response = Helper.decodeToStr(res).getBytes();
            Helper.responseToClient(httpExchange, response);

            Collog.getInstance().heartbeat_queue.accept(json);



        } else if (method.equalsIgnoreCase("OPTIONS")) {
            Helper.optionsResponse(httpExchange);
        }
    }

}
