package master;

import collog.Collog;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class ShardsAllocator {
    public void reallocationShards(JSONObject node_info){
        /**
         * when data note registered or unregistered, we have to reallocat shards
         */

        Collog.getInstance().getSlaveTable();
    }
    public void allocateShards(){

        this.allocateUnassignedShards();
        this.moveShards();
        this.balance();

    }

    private void allocateUnassignedShards(){
        /**
         * Method for assign unassigned shards to Data Node
         */
        ArrayList<Integer> unassigned_shards = (ArrayList<Integer>) MasterMetaStorage.getInstance().getUnallocationShards().clone();
//        ArrayList<Integer>
        ArrayList<JSONObject> data_nodes = Collog.getInstance().getSlaveTable();

        int i = 0;
        int data_nodes_size = data_nodes.size();
        Iterator<Integer> iter = unassigned_shards.iterator();

        while(iter.hasNext()){

            int a = iter.next();

            JSONObject node = data_nodes.get(i % data_nodes_size);
            JSONObject temp = new JSONObject();
            temp.put("shard_number", a);

            try {
                (new DataNodeManager()).sendReallocationRequest(Integer.parseInt(node.get("node_id").toString()),temp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ((ArrayList<Integer>)(node.get("shards"))).add(a);
            MasterMetaStorage.getInstance().removeUnallocationShard(a);

            i++;
        }
        for(i = 0; i<data_nodes_size;i++){
            System.out.println(data_nodes.get(i).toString());
        }
    }

    private void moveShards(){
        /**
         *
         */
    }

    private void balance(){
        /**
         * Method for balancing among shards
         */
    }
}
