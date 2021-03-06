package data;

import collog.Collog;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class DataNodeMetaStorage {

    public JSONObject getMyInfo() {
        JSONObject info = new JSONObject();
        info.put("node_id", Collog.getInstance().id);
        info.put("ip", Collog.getInstance().getMyIP());
        info.put("port", Collog.getInstance().getPort());
        boolean is_new = false;
        /**
         * Check I have shards are allocated to me or not
         */
//        ArrayList<Integer> list = this.getMyShards();
        ArrayList<Integer> list = new ArrayList<>();
//
        if(list.size()>0){
            is_new = true;
            info.put("shards", list);
            info.put("replica_shards",list);
        }else{
            info.put("shards",list);
            info.put("replica_shards",list);
        }
        info.put("new", is_new);


        return info;
    }

    public ArrayList<Integer> getMyShards() {
        ArrayList<Integer> list = new ArrayList<>();
        try {
            JSONParser parser = new JSONParser();
            FileReader reader = new FileReader("src/main/java/data/meta.json");

            Object obj = parser.parse(reader);
            JSONObject json = (JSONObject) obj;
            if(json.containsKey("shards"))
                return (ArrayList<Integer>) json.get("shards");
            else
                return list;
        } catch (FileNotFoundException e) {
            list = new ArrayList<Integer>();
            return list;
        } catch (IOException e) {
            return list;
        } catch (ParseException e) {
            return list;
        }
    }

    public void saveMetaInfo() {
        try {
            FileWriter writer = new FileWriter("src/main/java/data/meta.json");
            writer.write(this.getMetaInfo().toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getMetaInfo(){
        JSONObject json = null;
//        (new MasterManager()).syncShardsInfoWithMaster();
        json = Collog.getInstance().getSlave(Collog.getInstance().id);
        if (json == null){
            json = this.getMyInfo();
        }
        return json;
    }
}
