package api.views;

import annotations.ContentType;
import annotations.URLAnnotation;
import annotations.URLMethod;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import helper.Helper;
import org.json.simple.JSONObject;
import webclient.WebClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.SocketTimeoutException;

@URLAnnotation("_search/")
public class SearchView implements HttpHandler {
    @URLMethod("GET")
    @ContentType("application/json")
    public byte[] getResponse() {
        String a = "{'search':'search'}";
        return a.getBytes();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        if (method.equalsIgnoreCase("GET")) {WebClient cli = new WebClient();

            byte[] response = this.getResponse();
            Helper.responseToClient(httpExchange, response);
        } else if (method.equalsIgnoreCase("POST")) {
            /**
             * Read request body from client
             */
            String json = Helper.getRequestBody(httpExchange.getRequestBody());

            /**
             * Send response to client.
             */
            WebClient cli = new WebClient();

            try {
                json = cli.sendPostRequestWithJson("localhost:8888/_meta/", json);
                byte[] response = json.getBytes();
                Helper.responseToClient(httpExchange, response);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}

// Time out error
//    JSONObject j = new JSONObject();
//            j.put("abc","abc");
//                    String json = j.toString();
//                    try {
//                    json = cli.sendPostRequestWithJson("http://localhost:8888/_meta/", json);
//                    byte[] response = json.getBytes();
//                    Helper.responseToClient(httpExchange, response);
//                    } catch(SocketTimeoutException e) {
//                    System.out.println("timeout!!!!@#$@#$@$@#$@#$@#$@#$");
//                    }catch (Exception e) {
//                    e.printStackTrace();
//                    }
