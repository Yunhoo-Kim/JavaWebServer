package master;

import collog.Collog;
import logging.Logging;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MasterMetaStorage {


    private static MasterMetaStorage instance = null;
    private ArrayList<String> searchable_fields = new ArrayList<>();
    private ArrayList<JSONObject> dashboard_datas = new ArrayList<>();

    public static MasterMetaStorage getInstance() {
        if(instance==null){
            instance = new MasterMetaStorage();
            instance.searchable_fields.add("@timestamp"); // intialize searchable fields
        }

        return instance;
    }

    public ArrayList<Integer> unallocation_shards = new ArrayList<>();
    public ArrayList<Integer> unallocation_replica_shards = new ArrayList<>();

    private MasterMetaStorage() {
        this.initUnallocationShard();
    }

    public void initUnallocationShard(){
        /*
        shards information initializing
         */
        if(Collog.getInstance().isElectioning())
            return;
        Logging.logger.info("initialize");
        int num_of_shards = Collog.getInstance().getShards();
        for(int i=0;i<num_of_shards;i++){
            unallocation_shards.add(i);
            unallocation_replica_shards.add(i);
        }
        this.readMetaFile();

    }
    public void removeUnallocationShard(Integer a){
        unallocation_shards.remove(a);
    }

    public ArrayList<Integer> getUnallocationShards() {
        return unallocation_shards;
    }

    public void removeUnallocationReplicaShard(Integer a){
        unallocation_replica_shards.remove(a);
    }

    public ArrayList<Integer> getUnallocationReplicaShards() {
        return unallocation_replica_shards;
    }

    public void saveMetaInfo() {
        try {
            FileWriter writer = new FileWriter("src/master/meta.json");
            writer.write(this.getMetaData().toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<JSONObject> getDashboardDatas() {
        return dashboard_datas;
    }

    public void addFields(String field){
        this.searchable_fields.add(field);
        this.saveMetaInfo();
    }

    public void removeFields(String field){
        this.searchable_fields.remove(field);
        this.saveMetaInfo();
    }

    public void addDashBoardData(JSONObject json){
//        this.searchable_fields.add(field);
        this.dashboard_datas.add(json);
        this.saveMetaInfo();
    }

    public void removeDashBoardData(JSONObject json){
        this.dashboard_datas.remove(json);
        this.saveMetaInfo();
    }


    public JSONObject getMetaData(){
        /**
         * we have to add slave info to json
         */
        JSONObject json = new JSONObject();
        json.put("searchable_fields", this.searchable_fields);
        json.put("dashboards",this.dashboard_datas);
        json.put("shards", Collog.getInstance().getSlaveTable());
        return json;
    }

    public void readMetaFile(){
        JSONObject json = null;

        try {
            JSONParser parser = new JSONParser();
            FileReader reader = new FileReader("src/master/meta.json");

            Object obj = parser.parse(reader);
            json = (JSONObject) obj;

            if(json.containsKey("dashboards"))
                this.dashboard_datas = (ArrayList<JSONObject>)json.getOrDefault("dashboards",new ArrayList<JSONObject>());

            if(json.containsKey("searchable_fields")) {
                Iterator<String> iter = ((ArrayList<String>) json.get("searchable_fields")).iterator();
                while(iter.hasNext()){
                    String data = iter.next();
                    if(!this.searchable_fields.contains(data))
                        this.searchable_fields.add(data);
                }
//                this.searchable_fields.addAll((ArrayList<String>) json.get("searchable_fields"));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void comparisonKeys(JSONObject json){
        /**
         * This method for maintaining searchable fields from input data
         */
        Set new_fields = json.keySet();
        Set<String> keySets = new HashSet<String>(this.searchable_fields);

        int b_l = new_fields.size();

        boolean after = new_fields.removeAll(keySets);
        int a_l = new_fields.size();

        if(a_l != 0) {
            Iterator<String> a = new_fields.iterator();
            while (a.hasNext()) {
                String s = a.next();
                this.addFields(s);

            }
        }

    }
}
