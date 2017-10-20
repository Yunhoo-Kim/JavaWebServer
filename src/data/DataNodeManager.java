package data;

import collog.Collog;
import org.json.simple.JSONObject;
import webclient.WebClient;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class DataNodeManager {
    /**
     * Delegate connection between data nodes
     */

    public void sendShardRequestToDataNode(JSONObject node, int shard_num){
//        (new MasterManager()).syncShardsInfoWithMaster();
        Collog collog = Collog.getInstance();
//        JSONObject node = collog.getSlave(node_id);

        WebClient wcli = new WebClient();
        String url = String.format("http://%s:%s/data/get/shard/", node.get("ip").toString(), node.get("port").toString());
        String shard_file_name = String.format("data/%d/data.txt",shard_num);
        JSONObject req_data = new JSONObject();
        req_data.put("node_id", collog.getId());
        req_data.put("shard",shard_num);

        try {
            String response = wcli.sendPostRequestWithJson(url, req_data.toString());

//            response.getBytes();
            FileOutputStream os = new FileOutputStream(String.format("data/1/data.txt",shard_num));
            os.write(response.getBytes());
            os.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void sendShardRequestToDataNode(int node_id, int shard_num){
//        (new MasterManager()).syncShardsInfoWithMaster();
        Collog collog = Collog.getInstance();
        JSONObject node = collog.getSlave(node_id);

        WebClient wcli = new WebClient();
        String url = String.format("http://%s:%s/data/get/shard/", node.get("ip").toString(), node.get("port").toString());
        String shard_file_name = String.format("data/%d/data.txt",shard_num);
        JSONObject req_data = new JSONObject();
        req_data.put("node_id", collog.getId());
        req_data.put("shard",shard_num);

        try {
            String response = wcli.sendPostRequestWithJson(url, req_data.toString());

//            response.getBytes();
            FileOutputStream os = new FileOutputStream(String.format("data/1/data.txt",shard_num));
            os.write(response.getBytes());
            os.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void sendShardRequestToDataNode(int shard_num){
        (new MasterManager()).syncShardsInfoWithMaster();
        Collog collog = Collog.getInstance();
//        JSONObject node = collog.getSlave(node_id);

        Iterator<JSONObject> iter = collog.getSlaveTable().iterator();
        while(iter.hasNext()){
            JSONObject node = iter.next();
            if(((ArrayList<Integer>)node.get("shards")).contains(shard_num)){
                this.sendShardRequestToDataNode(node, shard_num);
                break;
            }
        }
    }

}
