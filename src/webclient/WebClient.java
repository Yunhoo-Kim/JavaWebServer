package webclient;

import helper.Helper;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebClient {
    public String sendGetRequest(String url) throws Exception {
        URL _url = new URL(url);
        HttpURLConnection con = (HttpURLConnection) _url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "CollogClient");

        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            throw new Exception();
        }
        String response = Helper.getRequestBody(con.getInputStream());
        return response;
    }

    public String sendPostRequestWithJson(String url, String json) throws Exception {
        URL _url = new URL(url);
        HttpURLConnection con = (HttpURLConnection) _url.openConnection();

        con.setRequestMethod("POST");
        con.setReadTimeout(5000);
        con.setRequestProperty("User-Agent", "CollogClient");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Length", String.valueOf(json.length()));
        con.getOutputStream().write(json.getBytes("UTF8"));

        int responseCode = con.getResponseCode();

        if (responseCode != 200) {
            throw new Exception();
        }
        String response = Helper.getRequestBody(con.getInputStream());
        return response;
    }

    public String sendPostReqeustWithFile(String url, String file_name) throws Exception {

//        File file = new File(file_name);
//
//        if (!file.exists()) {
//            throw new Exception();
//        }
//
//        int numOfBytes = (int) file.length();
//
//
//        URL _url = new URL(url);
//        HttpURLConnection con = (HttpURLConnection) _url.openConnection();
//
//        con.setReadTimeout(10000);
//        con.setConnectTimeout(15000);
//        con.setRequestMethod("POST");
//        con.setUseCaches(false);
//        con.setDoInput(true);
//        con.setDoOutput(true);
//
//        con.setRequestProperty("Connection", "Keep-Alive");
//        con.addRequestProperty("Content-length", reqEntity.getContentLength() + "");
//        con.addRequestProperty(, reqEntity.getContentType().getValue());
//
//        OutputStream os = con.getOutputStream();
//        os.write();
//
//        con.setRequestMethod("POST");
//        con.setReadTimeout(5000);
//        con.setRequestProperty("User-Agent", "CollogClient");
//        con.setRequestProperty("Content-Type", "application/json");
//        con.setDoOutput(true);
//        con.setRequestProperty("Content-Length", String.valueOf(json.length()));
//        con.getOutputStream().write(json.getBytes("UTF8"));
//
//        int responseCode = con.getResponseCode();
//
//        if (responseCode != 200) {
//            throw new Exception();
//        }
//        String response = Helper.getRequestBody(con.getInputStream());
//        return response;
        return file_name;
    }
}
