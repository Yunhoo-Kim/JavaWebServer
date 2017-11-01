package master.views;

import annotations.ContentType;
import annotations.URLAnnotation;
import collog.Collog;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import data.MasterManager;
import helper.Helper;

import master.DataNodeManager;
import master.MasterMetaStorage;
import master.ShardsAllocator;
import org.json.simple.JSONObject;
import java.io.*;

@URLAnnotation("master/node/remove/")
public class DataNodeRemoveHandler implements HttpHandler {

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
            Helper.responseToClient(httpExchange, response);

        }else if(method.equalsIgnoreCase("POST")){
            /**
             * Read request body from client
             */
            String request_body = Helper.getRequestBody(httpExchange.getRequestBody());
            JSONObject json = Helper.encodeToJson(request_body);

            /**
             * Send response to client.
             */
            byte[] response = json.toString().getBytes();
            Helper.responseToClient(httpExchange, response);

            /**
             * data node register body structure
             *{
             *      "node_id" : "node_indentification",
             *}
             *
             */

            /**
             * ToDo: Json format check
             *
             *
             * 샤드 재분배??,  각 데이터노드 슬레이브 테이블 동기화
             * unassigned로 다시 배정하고 할당 프로세스 반복
             */

            int node_id = Integer.parseInt(json.get("node_id").toString());
            Collog.getInstance().removeSlave(node_id);

            (new MasterMetaStorage()).updateUnallocationShard();
            // 각 데이터 노드들 싱크 맞춰줘야 하는데
            try {
                (new DataNodeManager()).syncSlaveTableInfoWithMaster();
            } catch (Exception e) {
                e.printStackTrace();
            }

            (new ShardsAllocator()).allocateShards();




        }else if(method.equalsIgnoreCase("OPTIONS")){
            Helper.optionsResponse(httpExchange);
        }
    }
}
