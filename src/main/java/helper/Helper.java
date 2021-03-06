package helper;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import logging.Logging;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class Helper {
    public static ArrayList<Class<?>> getClassesForPackage(String pkg) {
        /**
         * Get all classes in package named pkg
         */

        String pkgname = pkg;
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        // Get a File object for the package
        File directory = null;
        String fullPath;
        String relPath = pkgname.replace('.', '/');
//	    System.out.println("ClassDiscovery: Package: " + pkgname + " becomes Path:" + relPath);
        URL resource = ClassLoader.getSystemClassLoader().getResource(relPath);
        String url = String.format("src/main/java/%s",relPath);
//        URL rr = null;
//        try {
//            rr = new URL(url);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
        Logging.logger.info("URL : " + url);
//	    System.out.println("ClassDiscovery: Resource = " + resource);
        if (resource == null) {
            throw new RuntimeException("No resource for " + relPath);
        }
//        fullPath = resource.getFile();
//        System.out.println("ClassDiscovery: FullPath = " + resource);

        try {
//            directory = new File(resource.toURI());
//            directory = new File(rr.toURI());
            directory = new File(url);
//        } catch (URISyntaxException e) {
        } catch (IllegalArgumentException e) {
            directory = null;
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(pkgname + " (" + resource + ") does not appear to be a valid URL / URI.  Strange, since we got it from the system...", e);
        }
//        System.out.println("ClassDiscovery: Directory = " + directory);

        if (directory != null && directory.exists()) {
            // Get the list of the files contained in the package
            String[] files = directory.list();
            for (int i = 0; i < files.length; i++) {
                // we are only interested in .class files
                Logging.logger.info(files[i]);
                if (files[i].endsWith(".java")) {
                    // removes the .class extension
                    String className = pkgname + '.' + files[i].substring(0, files[i].length() - 5);
                    System.out.println("ClassDiscovery: className = " + className);
                    try {
                        classes.add(Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("ClassNotFoundException loading " + className);
                    }
                }
            }
        }
        return classes;
    }

    public static JSONObject encodeToJson(String str) {
        JSONParser parser = new JSONParser();
        JSONObject json = new JSONObject();
        try {
            json = (JSONObject) parser.parse(str);
        } catch (ParseException e) {

        }
        return json;
    }


    public static String decodeToStr(JSONObject msg) {
        StringWriter out = new StringWriter();
        try {
            msg.writeJSONString(out);
            String return_string = out.toString();

            return return_string;
        } catch (IOException e) {

            e.printStackTrace();
        }
        return "";
    }
    public static String getRequestBody(InputStream is) throws UnsupportedEncodingException, IOException {
        /**
         * It is for retreiving request post data from body and convert to string to return
         */
//        InputStreamReader isr = new InputStreamReader(is, "utf-8");
        BufferedReader br = new BufferedReader(new InputStreamReader(is,"utf-8"));
        String input_line;
        StringBuffer response = new StringBuffer();

        while((input_line = br.readLine()) != null){
            response.append(input_line + "\n");
        }

        br.close();
        is.close();
//        Logging.logger.info(response.toString());
//        System.out.println(response.toString());
        return response.toString();
    }
    public static void responseWithErrorCodeToClient(HttpExchange httpExchange, int status_code) throws IOException{
        Headers responseHeaders = httpExchange.getResponseHeaders();
        JSONObject json = new JSONObject();
        json.put("status",status_code);
        byte[] response = json.toString().getBytes();
        httpExchange.sendResponseHeaders(status_code,response.length);
        responseHeaders.set("Content-Type","application/json;charset=utf-8");
        OutputStream responseBody = httpExchange.getResponseBody();
        responseBody.write(response);
        responseBody.close();
    }

    public static void responseToClient(HttpExchange httpExchange, byte[] response) throws IOException{
        /*
        It is to response json data to client
         */
//        Headers responseHeaders = httpExchange.getResponseHeaders();
        httpExchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        httpExchange.getResponseHeaders().set("Content-Type","application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(200, response.length);
        OutputStream responseBody = httpExchange.getResponseBody();
        responseBody.write(response);
        responseBody.close();
    }


    public static void responseToClientHTML(HttpExchange httpExchange, byte[] response) throws IOException{
        /*
        It is to response json data to client
         */
//        Headers responseHeaders = httpExchange.getResponseHeaders();
        httpExchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        httpExchange.getResponseHeaders().set("Content-Type","text/html");
        httpExchange.sendResponseHeaders(200,response.length);
        OutputStream responseBody = httpExchange.getResponseBody();
        responseBody.write(response);
        responseBody.close();
    }

    public static void optionsResponse(HttpExchange httpExchange) throws IOException{
        httpExchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        httpExchange.getResponseHeaders().add("Access-Control-Allow-Methods", "API, CRUNCHIFYGET, GET, POST, PUT, UPDATE, OPTIONS");
        httpExchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
        httpExchange.getResponseHeaders().add("Access-Control-Max-Age", "151200");
        httpExchange.sendResponseHeaders(204, -1);
    }
}
