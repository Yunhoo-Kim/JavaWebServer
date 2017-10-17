package api.views;

import WebServer.ContentType;
import WebServer.URLAnnotation;
import WebServer.URLMethod;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

@URLAnnotation("_meta/")
public class MetaView implements HttpHandler {
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

		}
	}
	
}
