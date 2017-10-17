package helper;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
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
//	    System.out.println("ClassDiscovery: Resource = " + resource);
        if (resource == null) {
            throw new RuntimeException("No resource for " + relPath);
        }
        fullPath = resource.getFile();
        System.out.println("ClassDiscovery: FullPath = " + resource);

        try {
            directory = new File(resource.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(pkgname + " (" + resource + ") does not appear to be a valid URL / URI.  Strange, since we got it from the system...", e);
        } catch (IllegalArgumentException e) {
            directory = null;
        }
//        System.out.println("ClassDiscovery: Directory = " + directory);

        if (directory != null && directory.exists()) {
            // Get the list of the files contained in the package
            String[] files = directory.list();
            for (int i = 0; i < files.length; i++) {
                // we are only interested in .class files
                if (files[i].endsWith(".class")) {
                    // removes the .class extension
                    String className = pkgname + '.' + files[i].substring(0, files[i].length() - 6);
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
        JSONObject json = null;
        try {
            json = (JSONObject) parser.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
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
    public static String getPostRequestBody(InputStream is) throws UnsupportedEncodingException, IOException {
        /**
         * It is for retreiving request post data from body and convert to string to return
         */
        InputStreamReader isr = new InputStreamReader(is, "utf-8");
        BufferedReader br = new BufferedReader(isr);
        StringBuilder buf = new StringBuilder();
        int b;
        while ((b = br.read()) != -1) {
            buf.append((char)b);
        }
        br.close();
        isr.close();
        is.close();

        return buf.toString();
    }

    public static void responseToClient(HttpExchange httpExchange, byte[] response) throws IOException{
        /*
        It is to response json data to client
         */
        Headers responseHeaders = httpExchange.getResponseHeaders();
        httpExchange.sendResponseHeaders(200,response.length);
        responseHeaders.set("Content-Type","application/json;charset=utf-8");
        OutputStream responseBody = httpExchange.getResponseBody();
        responseBody.write(response);
        responseBody.close();
    }

}
