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

@URLAnnotation("data/update/slaveTable/")
public class SlaveTableUpdateHandler implements HttpHandler{

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();

        if(method.equalsIgnoreCase("GET")){

            (new MasterManager()).syncShardsInfoWithMaster();

            byte[] response = Collog.getInstance().getSlaveTable().toString().getBytes();
            Helper.responseToClient(httpExchange, response);

        }else if(method.equalsIgnoreCase("POST")){
            String request_body = Helper.getRequestBody(httpExchange.getRequestBody());
            JSONObject json = Helper.encodeToJson(request_body);



            byte[] response = Helper.decodeToStr(json).getBytes();
            Helper.responseToClient(httpExchange, response);

        }else if(method.equalsIgnoreCase("OPTIONS")){
            Helper.optionsResponse(httpExchange);
        }

    }
}
