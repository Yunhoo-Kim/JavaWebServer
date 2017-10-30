package data;

import org.json.simple.JSONObject;

import java.util.ArrayList;

public class SearchThreadDataNode implements Runnable {

    private volatile ArrayList<JSONObject> response ;
    private JSONObject json;
    private int shard;

    public SearchThreadDataNode(JSONObject json, int shard){
        this.json = json;
        this.shard = shard;
    }

    public void run(){
        response = (new FileSearchHandler()).search(json,shard);
    }

    public ArrayList<JSONObject> getResponse(){
        return response;
    }
}