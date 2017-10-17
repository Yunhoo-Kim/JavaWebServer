package api.views;

import annotations.ContentType;
import annotations.URLAnnotation;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import helper.Helper;
import org.json.simple.JSONObject;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;

@URLAnnotation("main/")
public class MainView implements HttpHandler{

//    @URLMethod("GET")
    @ContentType("text/html")
    public byte[] getResponse() {
        String fileName = "index.html";
        File file = new File(fileName);
        byte[] fileInBytes = null;

        if (file.exists()) {
            String mimeType = new MimetypesFileTypeMap()
                    .getContentType(file);
            int numOfBytes = (int) file.length();

            FileInputStream inFile = null;
            try {
                inFile = new FileInputStream(fileName);

                fileInBytes = new byte[numOfBytes];

                inFile.read(fileInBytes);
                inFile.close();
            } catch (FileNotFoundException e) {
               e.printStackTrace();
           } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return fileInBytes;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        if(method.equalsIgnoreCase("GET")){
            byte[] response = this.getResponse();
            Headers responseHeaders = httpExchange.getResponseHeaders();
            httpExchange.sendResponseHeaders(200,response.length);
            responseHeaders.set("Content-Type","text/html;charset=utf-8");
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
