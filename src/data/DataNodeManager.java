package data;

import collog.Collog;
import org.json.simple.JSONObject;
import webclient.WebClient;
import java.io.FileOutputStream;
import java.io.ByteArrayInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class DataNodeManager {

    public void sendShardRequestToDataNode(int node_id, JSONObject body) throws Exception{
        JSONObject json = Collog.getInstance().getSlave(node_id);

        WebClient wcli = new WebClient();
        String url = String.format("http://%s:%s/data/get/shard/",json.get("ip").toString(),json.get("port").toString());

        String file_name = String.format("data/%d/data.txt",body.get("shard"));
        JSONObject req_data = new JSONObject();
        req_data.put("shard",json.get("shard"));

        try{
            String response = wcli.sendPostRequestWithJson(url,req_data.toString());

            FileOutputStream os = new FileOutputStream(file_name);
            ByteArrayInputStream is = new ByteArrayInputStream(response.getBytes(Charset.forName("UTF-8")));
            StringBuilder builder = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = in.readLine()) != null) {
                    builder.append(line + "\n");
//                    Logging.logger.info(line);
                }
            }
            byte[] bytes = builder.toString().getBytes(Charset.forName("UTF-8"));
            os.write(bytes);
            os.flush();
            os.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
