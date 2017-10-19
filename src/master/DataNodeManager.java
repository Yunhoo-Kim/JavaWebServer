package master;

import collog.Collog;
import org.json.simple.JSONObject;
import webclient.WebClient;

import java.util.ArrayList;
import java.util.Iterator;

public class DataNodeManager {
    /**
     * This class is for managing connection with data nodes
     * It can send data to multiple data nodes or specific one
     *
     */


    public void sendReallocationRequest(int node_id, JSONObject body) throws Exception{
        JSONObject json = Collog.getInstance().getSlave(node_id);
        WebClient wcli = new WebClient();
        String url = String.format("http://%s:%s/data/reallocation/", json.get("ip").toString(), json.get("port").toString());
        System.out.println(url);
        wcli.sendPostRequestWithJson(url,body.toString());
    }

    public void sendDataToDataNodes(JSONObject json){
        int shards = Collog.getInstance().getShards();
        int shard = Math.abs(json.hashCode()) % shards;
        System.out.println("Shard number is " + shard);
        json.put("shard",shard);
        Iterator<JSONObject> iter = Collog.getInstance().getSlaveTable().iterator();
        while(iter.hasNext()){
            JSONObject node = iter.next();
            if(((ArrayList<Integer>)node.get("shards")).contains(shard)){
                this.sendDataToDataNode(node, json);
            }
        }
    }

    public void sendDataToDataNode(JSONObject node, JSONObject data){
        System.out.println("Send datatatatatat");
        WebClient wcli = new WebClient();
        String url = String.format("http://%s:%s/data/input/", node.get("ip").toString(), node.get("port").toString());
        System.out.println(url);
        try {
            wcli.sendPostRequestWithJson(url,data.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
