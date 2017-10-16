package api.views;

import WebServer.ContentType;
import WebServer.URLAnnotation;
import WebServer.URLMethod;

@URLAnnotation("_meta/")
public class MetaView {
	@URLMethod("GET")
	@ContentType("application/json")
	public String getString(){
		String a = "{'abc':'abc'}";
		return a;
	}
	
}
