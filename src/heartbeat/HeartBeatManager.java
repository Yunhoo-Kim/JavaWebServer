package heartbeat;

import collog.Collog;
import data.DataNodeMetaStorage;
import data.ElectionWoker;
import helper.Helper;
import logging.Logging;
import org.json.simple.JSONObject;
import webclient.WebClient;

import java.util.*;

public class HeartBeatManager extends TimerTask {
    public boolean is_master = false;

    public HeartBeatManager(boolean is_master) {
        this.is_master = is_master;

        if (is_master) {
            // When node is master

        } else {
            // When node is data

        }
    }

    @Override
    public void run() {
        if (this.is_master) {
            // master
            HashMap<Integer, Long> map = (HashMap<Integer, Long>) ((HashMap<Integer, Long>) Collog.getInstance().heartbeat_map).clone();
            Long before5min = (System.currentTimeMillis() / 1000) - 10;
            for (Map.Entry<Integer, Long> entry : map.entrySet()) {
                if (before5min > entry.getValue()) {
                    Logging.logger.info("data node " + entry.getKey() + "have to be deleted");
                    Collog.getInstance().removeSlave(entry.getKey());
                }
            }
        } else {
            // slave
            String json = (new DataNodeMetaStorage()).getMetaInfo().toString();
            String url = String.format("http://%s:%s/master/heartbeat/", Collog.getInstance().getMasterIp(), Collog.getInstance().getMasterPort());
            try {
                String res = (new WebClient()).sendPostRequestWithJson(url, json);
                JSONObject res_j = Helper.encodeToJson(res);
                Collog.getInstance().updateSlaveTable((ArrayList<JSONObject>) res_j.get("data_nodes"));
            } catch (Exception e) {
                //start Election because of error occurs in master server.
                Collog.getInstance().startElection();
                Logging.logger.info("Election Start");
//                e.printStackTrace();
            }
        }
    }
}
