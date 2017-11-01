package master;

import collog.Collog;
import com.sun.net.httpserver.Headers;
import helper.Helper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import webclient.WebClient;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Iterator;

public class DataNodeManager {
    /**
     * This class is for managing connection with data nodes
     * It can send data to multiple data nodes or specific one
     *
     */


    public void sendAllocationRequest(int node_id, JSONObject body) throws Exception{
        JSONObject json = Collog.getInstance().getSlave(node_id);
        WebClient wcli = new WebClient();
        String url = String.format("http://%s:%s/data/allocation/", json.get("ip").toString(), json.get("port").toString());
        System.out.println(url);
        wcli.sendPostRequestWithJson(url,body.toString());
    }

    public void sendReplicaAllocationRequest(int node_id, JSONObject body) throws Exception{
        JSONObject json = Collog.getInstance().getSlave(node_id);
        WebClient wcli = new WebClient();
        String url = String.format("http://%s:%s/data/allocation/replica/", json.get("ip").toString(), json.get("port").toString());
        System.out.println(url);
        wcli.sendPostRequestWithJson(url,body.toString());
    }

    public void sendDataToDataNodes(JSONObject json){
        int shards = Collog.getInstance().getShards();
        int shard = Math.abs(json.hashCode()) % shards;

//        System.out.println("Shard number is " + shard);
        json.put("shard", shard);

        Iterator<JSONObject> iter = Collog.getInstance().getSlaveTable().iterator();
        while(iter.hasNext()){
            JSONObject node = iter.next();
            if(((ArrayList<Integer>)node.get("shards")).contains(shard)){
                this.sendDataToDataNode(node, json);
            }
            if(((ArrayList<Integer>)node.get("replica_shards")).contains(shard)){
                this.sendReplicaDataToDataNode(node, json);
            }

        }
    }

    public void sendDataToDataNode(JSONObject node, JSONObject data){
        WebClient wcli = new WebClient();
        String url = String.format("http://%s:%s/data/input/", node.get("ip").toString(), node.get("port").toString());
//        System.out.println(url);
        try {
            wcli.sendPostRequestWithJson(url,data.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void sendReplicaDataToDataNode(JSONObject node, JSONObject data){
        WebClient wcli = new WebClient();
        String url = String.format("http://%s:%s/data/input/replica/", node.get("ip").toString(), node.get("port").toString());
//        System.out.println(url);
        try {
            wcli.sendPostRequestWithJson(url,data.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<JSONObject> sendSearchToDataNode(JSONObject node, JSONObject data){
        System.out.println("Send datatatatatat");
        WebClient wcli = new WebClient();
        String url = String.format("http://%s:%s/data/search/", node.get("ip").toString(), node.get("port").toString());
        System.out.println(url);
        try {
            JSONObject response = Helper.encodeToJson(wcli.sendPostRequestWithJson(url,data.toString()));
            return (ArrayList<JSONObject>)response.get("results");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<JSONObject>();
    }

    public ArrayList<JSONObject> sendSearchRequest(JSONObject json){
        Iterator<JSONObject> iter = Collog.getInstance().getSlaveTable().iterator();
        Thread[] a = new Thread[Collog.getInstance().getSlaveTable().size()];

        SearchThread[] s = new SearchThread[Collog.getInstance().getSlaveTable().size()];

        DataNodeManager manager = new DataNodeManager();
        ArrayList<JSONObject> res = new ArrayList<>();

        int i = 0;
        while(iter.hasNext()){
            JSONObject node = iter.next();
            s[i] = new SearchThread(manager, node, json);
            a[i] = new Thread(s[i]);
            a[i].start();
        }


        for(int j=0;j<a.length;j++){
            try {
                a[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            res.addAll(s[i].getResponse());

        }

        ArrayList<JSONObject> responses = new ArrayList<>();
        responses.addAll(res);

        return responses;


    }

    public class SearchThread implements Runnable{
        private volatile ArrayList<JSONObject> response;
        private DataNodeManager manager;
        private JSONObject node;
        private JSONObject json;

        public SearchThread(DataNodeManager manager, JSONObject node, JSONObject json){
            this.manager = manager;
            this.node = node;
            this.json = json;
        }
        @Override
        public void run() {

            response = manager.sendSearchToDataNode(node,json);
        }

        public ArrayList<JSONObject> getResponse() {
            return response;
        }
    }



}
