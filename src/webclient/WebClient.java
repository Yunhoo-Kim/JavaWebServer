package webclient;

import helper.Helper;
import org.json.simple.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;

public class WebClient {
    public String sendGetRequest(String url) throws Exception{
        URL _url = new URL(url);
        HttpURLConnection con = (HttpURLConnection)_url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "CollogClient");

        int responseCode = con.getResponseCode();
        if(responseCode != 200){
            throw new Exception();
        }
        String response = Helper.getRequestBody(con.getInputStream());
        return response;
    }
    public String sendPostRequestWithJson(String url, String json) throws Exception{
        URL _url = new URL(url);
        HttpURLConnection con = (HttpURLConnection)_url.openConnection();

        con.setRequestMethod("POST");
//        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);
        con.setRequestProperty("User-Agent", "CollogClient");
        con.setRequestProperty("Content-Type","application/json");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Length",String.valueOf(json.length()));
        con.getOutputStream().write(json.getBytes("UTF8"));

        int responseCode = con.getResponseCode();

        if(responseCode != 200){
            throw new Exception();
        }
        String response = Helper.getRequestBody(con.getInputStream());
        return response;
    }
}
