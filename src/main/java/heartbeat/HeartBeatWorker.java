package heartbeat;

import collog.Collog;
import logging.Logging;
import org.json.simple.JSONObject;

public class HeartBeatWorker implements Runnable {

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Collog collog = Collog.getInstance();
            JSONObject data = collog.heartbeat_queue.release();
            int node_id = Integer.parseInt(data.get("node_id").toString());
            long timestamp = System.currentTimeMillis() / 1000;
            collog.heartbeat_map.put(node_id, timestamp);
        }
    }
}
