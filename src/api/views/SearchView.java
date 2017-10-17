package api.views;

import annotations.ContentType;
import annotations.URLAnnotation;
import annotations.URLMethod;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

@URLAnnotation("_search/")
public class SearchView implements HttpHandler{
	@URLMethod("GET")
	@ContentType("application/json")
	public byte[] getResponse(){
		String a = "{'search':'search'}";
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
			Headers responseHeaders = httpExchange.getResponseHeaders();
			responseHeaders.set("Content-Type", ((ContentType)(MainView.class.getAnnotation(ContentType.class))).value());
			OutputStream responseBody = httpExchange.getResponseBody();
			byte[] response = this.getResponse();
			responseBody.write(response);
			responseBody.close();
		}
	}
	
}
