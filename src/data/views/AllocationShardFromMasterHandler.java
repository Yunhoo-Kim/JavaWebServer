package data.views;

import annotations.URLAnnotation;
import collog.Collog;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import data.DataNodeManager;
import data.MasterManager;
import helper.Helper;
import org.json.simple.JSONObject;

import java.io.IOException;


@URLAnnotation("data/allocation/")
public class AllocationShardFromMasterHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();

        if(method.equalsIgnoreCase("GET")){
            byte[] response = Collog.getInstance().getSlaveTable().toString().getBytes();
            Helper.responseToClient(httpExchange, response);

        }else if(method.equalsIgnoreCase("POST")){
            String request_body = Helper.getRequestBody(httpExchange.getRequestBody());

            JSONObject json = Helper.encodeToJson(request_body);

            /**
             * ToDo: Json format check
             */

            // 데이터 노드의 슬레이브 테이블 업데이트
            (new MasterManager()).syncShardsInfoWithMaster();
            if(json.containsKey("node_id")){
                // 각 노드 별 데이터 통신 유도
                try {
                    (new DataNodeManager()).sendShardRequestToDataNode(Integer.parseInt(json.get("node_id").toString()),json);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }else if(method.equalsIgnoreCase("OPTIONS")){
            Helper.optionsResponse(httpExchange);
        }

    }

}
