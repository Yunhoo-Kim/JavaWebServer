package data;

import collog.Collog;
import logging.Logging;
import org.json.simple.JSONObject;
import webclient.WebClient;

import java.util.ArrayList;
import java.util.Iterator;

public class ElectionWoker implements Runnable {
    @Override
    public void run() {
//        while (!Thread.currentThread().isInterrupted()) {
        ArrayList<JSONObject> data_nodes = Collog.getInstance().getSlaveTable();
        Iterator<JSONObject> iter = data_nodes.iterator();
        int my_id = Collog.getInstance().getId();
        int response_cnt = 0;
        while (iter.hasNext()) {
            JSONObject temp = iter.next();
            int id = Integer.parseInt(temp.get("node_id").toString());
            if (id <= my_id) {
                continue;
            }
            String url = String.format("http://%s:%s/data/election/", temp.get("ip").toString(), temp.get("port").toString());
            try {
                (new WebClient()).sendPostRequestWithJson(url, temp.toString());
                response_cnt++;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (response_cnt == 0) {
            JSONObject req_data = new JSONObject();
            Iterator<JSONObject> iter2 = data_nodes.iterator();
            req_data.put("ip", Collog.getInstance().getMyIP());
            req_data.put("port", Collog.getInstance().getPort());
//                iter = data_nodes.iterator();

            while (iter2.hasNext()) {
                JSONObject temp = iter2.next();
                int id = Integer.parseInt(temp.get("node_id").toString());
                if (id == my_id) {
                    continue;
                }
                String url = String.format("http://%s:%s/data/election/complete/", temp.get("ip").toString(), temp.get("port").toString());
                try {
                    (new WebClient()).sendPostRequestWithJson(url, req_data.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Logging.logger.info("i am bully!!!!" + my_id);
            Collog.getInstance().runAsMaster();
        } else {

        }
//        }
    }
}
