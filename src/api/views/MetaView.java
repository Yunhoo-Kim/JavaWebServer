package api.views;

import annotations.ContentType;
import annotations.URLAnnotation;
import annotations.URLMethod;
import collog.Collog;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import helper.Helper;

import org.json.simple.JSONObject;
import java.io.*;

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
//			byte[] response = this.getResponse();
			JSONObject data = new JSONObject();
			data.put("master_ip",Collog.getInstance().getMyIP());
			data.put("master_port",Collog.getInstance().getPort());
			byte[] response = data.toString().getBytes();
			Helper.responseToClient(httpExchange, response);

		}else if(method.equalsIgnoreCase("POST")){
			/**
			 * Read request body from client
			 */
			String json = Helper.getRequestBody(httpExchange.getRequestBody());

			/**
			 * Send response to client.
			 */
			byte[] response = json.getBytes();
			Helper.responseToClient(httpExchange, response);
		}
	}

}
