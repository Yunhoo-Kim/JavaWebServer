package master.views;

import annotations.URLAnnotation;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import helper.Helper;

import master.MasterMetaStorage;
import org.json.simple.JSONObject;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

@URLAnnotation("master/dashboard/remove/")
public class DashBoardRemoveView implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();

        if(method.equalsIgnoreCase("GET")){
            byte[] response = MasterMetaStorage.getInstance().getDashboardDatas().toString().getBytes();
//            byte[] response = Collog.getInstance().getSlaveTable().toString().getBytes();
            Helper.responseToClient(httpExchange, response);

        }else if(method.equalsIgnoreCase("POST")){
            /**
             * Read request body from client
             */
            String request_body = Helper.getRequestBody(httpExchange.getRequestBody());

            JSONObject json = Helper.encodeToJson(request_body);
            /**
             * {"dashboards" :  [{
             "type" : "count",
             "graph_type" : "line"
             "key" : "key",
             "labels" : "",
             "timestamp_offset" : "timestamp1"
             }]]
             */

            /**
             * Send response to client.
             */

            double id = Double.parseDouble(json.get("id").toString());
            MasterMetaStorage.getInstance().removeDashboard(id);
//            MasterMetaStorage.getInstance().addDashBoardData(json);
            byte[] response = Helper.decodeToStr(json).getBytes();
            Helper.responseToClient(httpExchange, response);
            /**
             * ToDo: Json format check
             */
//            MasterMetaStorage.getInstance().comparisonKeys((JSONObject)json.clone());
//            (new DataNodeManager()).sendDataToDataNodes(json);


        }else if(method.equalsIgnoreCase("OPTIONS")){
            Helper.optionsResponse(httpExchange);
        }
    }



}
