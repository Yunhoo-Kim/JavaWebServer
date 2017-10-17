package api.views;

import annotations.ContentType;
import annotations.URLAnnotation;
import annotations.URLMethod;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import helper.Helper;

import org.json.simple.JSONObject;
import java.io.*;

@URLAnnotation("_test/")
public class TestView implements HttpHandler {
    @URLMethod("GET")
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
            Headers responseHeaders = httpExchange.getResponseHeaders();
            httpExchange.sendResponseHeaders(200,response.length);
            responseHeaders.set("Content-Type","application/json;charset=utf-8");
            OutputStream responseBody = httpExchange.getResponseBody();
            responseBody.write(response);
            responseBody.close();

        }else if(method.equalsIgnoreCase("POST")){
            /**
             * Read request body from client
             */
            InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(),"utf-8");
            BufferedReader br = new BufferedReader(isr);
            StringBuilder buf = new StringBuilder();
            int b;
            while((b=br.read()) != -1){
                buf.append((char)b);
            }
            br.close();
            isr.close();
            JSONObject json = Helper.encodeToJson(buf.toString());
            System.out.println(json.toJSONString());

            /**
             * Send response to client.
             */
            byte[] response = Helper.decodeToStr(json).getBytes();
            Headers responseHeaders = httpExchange.getResponseHeaders();
            httpExchange.sendResponseHeaders(200,response.length);
            responseHeaders.set("Content-Type","application/json;charset=utf-8");
            OutputStream responseBody = httpExchange.getResponseBody();
            responseBody.write(response);

            responseBody.close();


        }
    }

}
