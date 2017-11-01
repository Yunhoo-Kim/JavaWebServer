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
        switch (json.get("type").toString()){
            case "search":
                response = (new FileSearchHandler()).search(json,shard);
                break;
            case "count":
                response = (new FileSearchHandler()).countSearch(json,shard);
                break;
            case "max":
                response = (new FileSearchHandler()).maxSearch(json,shard);
                break;
            case "min":
                response = (new FileSearchHandler()).minSearch(json,shard);
                break;
            default:
                break;
        }
    }

    public ArrayList<JSONObject> getResponse(){
        return response;
    }
}