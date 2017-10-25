package data;

import collog.Collog;
import helper.Helper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import webclient.WebClient;

import java.util.ArrayList;

public class MasterManager {
    /**
     * This class for commutication with master server
     */

    public void registerToMaster() {
        Collog collog = Collog.getInstance();
        String url = String.format("http://%s:%s/master/node/register/", collog.getMasterIp(), collog.getMasterPort());

        try {
            (new WebClient()).sendPostRequestWithJson(url, (new DataNodeMetaStorage()).getMyInfo().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unregisterToMaster() {
        Collog collog = Collog.getInstance();
        String url = String.format("http://%s:%s/master/node/remove/", collog.getMasterIp(), collog.getMasterPort());

        try {
            (new WebClient()).sendPostRequestWithJson(url, (new DataNodeMetaStorage()).getMyInfo().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void syncShardsInfoWithMaster(){
        Collog collog = Collog.getInstance();
        String url = String.format("http://%s:%s/master/node/", collog.getMasterIp(), collog.getMasterPort());
        try {
            String response = (new WebClient()).sendGetRequest(url);
            JSONObject json = Helper.encodeToJson(response);
            ArrayList<JSONObject> shards = (ArrayList<JSONObject>)json.get("shards");
            collog.updateSlaveTable(shards);
        } catch (Exception e) {
            e.printStackTrace();
        }
        (new DataNodeMetaStorage()).saveMetaInfo();
    }
}
