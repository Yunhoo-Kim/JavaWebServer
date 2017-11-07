package api.views;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import annotations.URLAnnotation;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import logging.Logging;

@URLAnnotation("static/")
@SuppressWarnings("restriction")
public class StaticFilesHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String fileId = exchange.getRequestURI().getPath();
        fileId = fileId.substring(1);
        File file = getFile("src/main/resources/" +fileId);

        if (file == null) {
            String response = "Error 404 File not found.";
            exchange.sendResponseHeaders(404, response.length());
            OutputStream output = exchange.getResponseBody();
            output.write(response.getBytes());
            output.flush();
            output.close();
        } else {
            exchange.sendResponseHeaders(200, 0);
            OutputStream output = exchange.getResponseBody();
            FileInputStream fs = new FileInputStream(file);
            final byte[] buffer = new byte[0x10000];
            int count = 0;
            while ((count = fs.read(buffer)) >= 0) {
                output.write(buffer, 0, count);
            }
            output.flush();
            output.close();
            fs.close();
        }
    }

    private File getFile(String fileName) {
        Logging.logger.info(fileName);
        // TODO retrieve the file associated with the id
        File file = new File(fileName);
        if (file.exists()) {
            return file;
        }else{
            return null;
        }
    }
}

