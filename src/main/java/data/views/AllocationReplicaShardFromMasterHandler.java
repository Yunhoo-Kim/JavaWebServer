package data.views;
import annotations.ContentType;
import annotations.URLAnnotation;
import annotations.URLMethod;
import collog.Collog;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import data.DataNodeManager;
import data.MasterManager;
import helper.Helper;
import org.json.simple.JSONObject;

import java.io.IOException;

@URLAnnotation("data/allocation/replica/")
public class AllocationReplicaShardFromMasterHandler implements HttpHandler {

    @ContentType("application/json")
    @URLMethod("GET")
    public byte[] getResponse(){
        String a = "{'abc':'abc'}";
        return a.getBytes();
    }

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
             *      "shard_number" : "shard number","node_id" : "node_id"
             *}
             *
             */

            /**
             * ToDo: Json format check
             */

//            Collog.getInstance().addSlave(json);

            /**
             * Send response to client.
             */
            byte[] response = Helper.decodeToStr(json).getBytes();
            Helper.responseToClient(httpExchange, response);

            new MasterManager().syncShardsInfoWithMaster();
            if(json.containsKey("node_id")) {
                new DataNodeManager().sendReplicaShardRequestToDataNode(Integer.parseInt(json.get("node_id").toString()), Integer.parseInt(json.get("shard_number").toString()));
            }


        }else if(method.equalsIgnoreCase("OPTIONS")){
            Helper.optionsResponse(httpExchange);
        }
    }

}
