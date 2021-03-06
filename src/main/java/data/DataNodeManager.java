package data;

import collog.Collog;
import logging.Logging;
import master.MasterMetaStorage;
import org.json.simple.JSONObject;
import webclient.WebClient;

import java.io.*;
import java.nio.charset.Charset;
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
            FileOutputStream os = new FileOutputStream(String.format("data/%d/data.txt",shard_num));
            os.write(response.getBytes());
            os.close();
//            (new DataNodeMetaStorage()).saveMetaInfo();
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

        /*
        if file doesn't exist, you have to create file and directory
         */
        File file = new File(shard_file_name);
        if(!file.exists()){
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            String response = wcli.sendPostRequestWithJson(url, req_data.toString());

//            response.getBytes();
            FileOutputStream os = new FileOutputStream(shard_file_name);
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

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendReplicaShardRequestToDataNode(int node_id, int shard_num){
//        (new MasterManager()).syncShardsInfoWithMaster();
        Collog collog = Collog.getInstance();
        JSONObject node = collog.getSlave(node_id);

        WebClient wcli = new WebClient();
        String url = String.format("http://%s:%s/data/get/shard/", node.get("ip").toString(), node.get("port").toString());
        String shard_file_name = String.format("replica/%d/data.txt", shard_num);
        JSONObject req_data = new JSONObject();
        req_data.put("node_id", collog.getId());
        req_data.put("shard",shard_num);


        File file = new File(shard_file_name);
        if(!file.exists()){
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        try {
            String response = wcli.sendPostRequestWithJson(url, req_data.toString());
            FileOutputStream os = new FileOutputStream(shard_file_name);
            ByteArrayInputStream is = new ByteArrayInputStream(response.getBytes(Charset.forName("UTF-8")));
            StringBuilder builder = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = in.readLine()) != null) {
                    builder.append(line + "\n");
                }
            }
            byte[] bytes = builder.toString().getBytes(Charset.forName("UTF-8"));
            os.write(bytes);
            os.flush();
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

//        (new DataNodeMetaStorage()).saveMetaInfo();
    }

}
