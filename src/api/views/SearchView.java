package api.views;

import WebServer.ContentType;
import WebServer.URLAnnotation;
import WebServer.URLMethod;

@URLAnnotation("_search/")
public class SearchView {
	@URLMethod("GET")
	@ContentType("application/json")
	public String getString(){
		String a = "{'search':'search'}";
		return a;
	}
	
}
