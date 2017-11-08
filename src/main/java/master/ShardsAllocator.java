package master;

import collog.Collog;
import logging.Logging;
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
        this.allocateUnassignedReplicaShards();
        this.moveReplicaShards();
        this.balance();

    }

    private void allocateUnassignedShards(){
        /**
         * Method for assign unassigned shards to Data Node
         */
        //MasterMetaStorage에서 unallocationShard 리스트 복사해옴
        ArrayList<Integer> unassigned_shards = (ArrayList<Integer>) MasterMetaStorage.getInstance().getUnallocationShards().clone();
        ArrayList<JSONObject> data_nodes = Collog.getInstance().getSlaveTable();

        int i = 0;
        int data_nodes_size = data_nodes.size();
        Iterator<Integer> iter = unassigned_shards.iterator();

        while(iter.hasNext()){
            int a = iter.next();
            JSONObject node = data_nodes.get(i % data_nodes_size);
            JSONObject temp = new JSONObject();
            temp.put("shard_number", a);

            //data node에 shard 할당
            try {
                (new DataNodeManager()).sendAllocationRequest(Integer.parseInt(node.get("node_id").toString()),temp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ((ArrayList<Integer>)(node.get("shards"))).add(a);
            MasterMetaStorage.getInstance().removeUnallocationShard(a);

            i++;
        }
    }

    private void moveShards(){
        /**
         *
         */
        ArrayList<JSONObject> data_nodes = Collog.getInstance().getSlaveTable();
        Iterator<JSONObject> iter = data_nodes.iterator();
        ArrayList<JSONObject> over_mean_nodes = new ArrayList<>();
        ArrayList<JSONObject> under_mean_nodes = new ArrayList<>();
        int shards = Collog.getInstance().getShards();
        int nodes_num = data_nodes.size();

//        int mean = shards/nodes_num;
        int mean = (int) Math.round(((double)shards / nodes_num) - 0.3);
        while(iter.hasNext()){
            JSONObject node = iter.next();
            int shard_size_per_node = ((ArrayList<Integer>)node.get("shards")).size();
            if(shard_size_per_node < mean){
                under_mean_nodes.add(node);
            }else if(shard_size_per_node > mean){
                over_mean_nodes.add(node);
            }
        }

        int i=0;
        int j=0;
        iter = over_mean_nodes.iterator();

        while(true){
            int over_size = over_mean_nodes.size();
            int under_size = under_mean_nodes.size();

            if(under_size == 0 || over_size == 0){
                break;
            }

            JSONObject over_node = over_mean_nodes.get(j % over_size);
            JSONObject under_node = under_mean_nodes.get(i % under_size);
            j++;
            i++;

            ArrayList<Integer> over_node_shards = (ArrayList<Integer>)over_node.get("shards");
            ArrayList<Integer> under_node_shards = (ArrayList<Integer>)under_node.get("shards");

            if(over_node_shards.size() == mean){
                over_mean_nodes.remove(over_node);
                over_node = over_mean_nodes.get(j % (over_size - 1));
                over_node_shards = (ArrayList<Integer>)over_node.get("shards");
                j++;
            }

            JSONObject data = new JSONObject();
            int shard_number = over_node_shards.get(0);
            data.put("shard_number", shard_number);
            data.put("node_id",Collog.getInstance().getSlaveHasShard(shard_number).get("node_id").toString());

//            Logging.logger.info("야야야야야야야양야야야야야야야야ㅑㅇ 새로 배치가 되었습니다.!!");
            try {

                (new DataNodeManager()).sendAllocationRequest(Integer.parseInt(under_node.get("node_id").toString()), data);
            } catch (Exception e) {
                e.printStackTrace();
            }

            under_node_shards.add(shard_number);
            over_node_shards.remove(0);

            if(under_node_shards.size() >= mean){
                under_mean_nodes.remove(under_node);
            }
        }


    }

    private void balance(){
        /**
         * Method for balancing among shards
         */
//        ArrayList<JSONObject> data_nodes = Collog.getInstance().getSlaveTable();
//        Iterator<JSONObject> iter = data_nodes.iterator();
//        ArrayList<JSONObject> over_mean_nodes = new ArrayList<>();
//        ArrayList<JSONObject> under_mean_nodes = new ArrayList<>();
//        int shards = Collog.getInstance().getShards();
//        int nodes_num = data_nodes.size();
//
//        int mean = (int) (Math.round((double)shards / nodes_num) - 0.3);
//
//        while(iter.hasNext()){
//            JSONObject node = iter.next();
//            int shard_size_per_node = ((ArrayList<Integer>)node.get("replica_shards")).size();
//            if(shard_size_per_node < mean){
//                under_mean_nodes.add(node);
//            }else if(shard_size_per_node > mean){
//                over_mean_nodes.add(node);
//            }
//        }
//        int i=0;
//        int j=0;
//        iter = over_mean_nodes.iterator();
//
//        while(true){
//            int over_size = over_mean_nodes.size();
//            int under_size = under_mean_nodes.size();
//
//            if(under_size == 0 || over_size == 0){
//                break;
//            }
//
//            JSONObject over_node = over_mean_nodes.get(j % over_size);
//            JSONObject under_node = under_mean_nodes.get(i % under_size);
//            j++;
//            i++;
//
//            ArrayList<Integer> over_node_shards = (ArrayList<Integer>)over_node.get("replica_shards");
//            ArrayList<Integer> under_node_shards = (ArrayList<Integer>)under_node.get("replica_shards");
//
//            if(over_node_shards.size() == mean){
//                over_mean_nodes.remove(over_node);
//                over_node = over_mean_nodes.get(j % (over_size - 1));
//                over_node_shards = (ArrayList<Integer>)over_node.get("replica_shards");
//                j++;
//            }
//            JSONObject data = new JSONObject();
//
//            int shard_number = 0;
//            int index = 0;
//            boolean is_founded = false;
//            int over_node_shards_size = over_node_shards.size();
//            for(int k=1;k<over_node_shards_size;k++){
//                index = over_node_shards_size - k;
//                shard_number = over_node_shards.get(index);
////                Logging.logger.info(shard_number);
//                if(!Collog.getInstance().hasShard(Integer.parseInt(under_node.get("node_id").toString()), shard_number)){
////                    Logging.logger.info("node doesn't have " + shard_number);
//                    is_founded = true;
//                    break;
//                }
//            }
//            data.put("shard_number", shard_number);
//            data.put("node_id",Collog.getInstance().getSlaveHasShard(shard_number).get("node_id").toString());
//
//            try {
//
//                (new DataNodeManager()).sendReplicaAllocationRequest(Integer.parseInt(under_node.get("node_id").toString()), data);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            under_node_shards.add(shard_number);
////            over_node_shards.remove(over_size-1);
//            over_node_shards.remove(index);
//            if(under_node_shards.size() >= mean){
//                under_mean_nodes.remove(under_node);
//            }
//        }


    }

    private void allocateUnassignedReplicaShards(){
        /**
         * Method for assign unassigned shards to Data Node
         */
        ArrayList<Integer> unassigned_shards = (ArrayList<Integer>) MasterMetaStorage.getInstance().getUnallocationReplicaShards().clone();
        ArrayList<JSONObject> data_nodes = Collog.getInstance().getSlaveTable();

        int i = 0;
        int data_nodes_size = data_nodes.size();
        Iterator<Integer> iter = unassigned_shards.iterator();

        while(iter.hasNext()){
//            Logging.logger.info("aaaaa " + iter.next().intValue());
//            Long b = new Long(iter.next());
//            int a = b.intValue();
//            int a = Integer.valueOf(iter.next().intValue());
            int a = iter.next();
            JSONObject node = data_nodes.get(i % data_nodes_size);
            JSONObject temp = new JSONObject();
            temp.put("shard_number", a);

            try {
                (new DataNodeManager()).sendReplicaAllocationRequest(Integer.parseInt(node.get("node_id").toString()),temp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ((ArrayList<Integer>)(node.get("replica_shards"))).add(a);
            MasterMetaStorage.getInstance().removeUnallocationReplicaShard(a);

            i++;
        }
//        for(i = 0; i<data_nodes_size;i++){
//            System.out.println(data_nodes.get(i).toString());
//        }
    }

    private void moveReplicaShards(){
        /**
         *
         */
        ArrayList<JSONObject> data_nodes = Collog.getInstance().getSlaveTable();
        Iterator<JSONObject> iter = data_nodes.iterator();
        ArrayList<JSONObject> over_mean_nodes = new ArrayList<>();
        ArrayList<JSONObject> under_mean_nodes = new ArrayList<>();
        int shards = Collog.getInstance().getShards();
        int nodes_num = data_nodes.size();

//        int mean = shards/nodes_num;
        int mean = (int) Math.round(((double)shards / nodes_num)- 0.3);
//
        while(iter.hasNext()){
            JSONObject node = iter.next();
            int shard_size_per_node = ((ArrayList<Integer>)node.get("replica_shards")).size();
            if(shard_size_per_node < mean){
                under_mean_nodes.add(node);
            }else if(shard_size_per_node > mean){
                over_mean_nodes.add(node);
            }
        }
        int i=0;
        int j=0;
        iter = over_mean_nodes.iterator();

        while(true){
            int over_size = over_mean_nodes.size();
            int under_size = under_mean_nodes.size();

            if(under_size == 0 || over_size == 0){
                break;
            }

            JSONObject over_node = over_mean_nodes.get(j % over_size);
            JSONObject under_node = under_mean_nodes.get(i % under_size);
            j++;
            i++;

            ArrayList<Integer> over_node_shards = (ArrayList<Integer>)over_node.get("replica_shards");
            ArrayList<Integer> under_node_shards = (ArrayList<Integer>)under_node.get("replica_shards");

            if(over_node_shards.size() == mean){
                over_mean_nodes.remove(over_node);
                over_node = over_mean_nodes.get(j % (over_size - 1));
                over_node_shards = (ArrayList<Integer>)over_node.get("replica_shards");
                j++;
            }
            JSONObject data = new JSONObject();

            int shard_number = 0;
            int index = 0;
            boolean is_founded = false;
            int over_node_shards_size = over_node_shards.size();
            for(int k=1;k<over_node_shards_size;k++){
                index = over_node_shards_size - k;
                shard_number = over_node_shards.get(index);
//                Logging.logger.info(shard_number);
                if(!Collog.getInstance().hasShard(Integer.parseInt(under_node.get("node_id").toString()), shard_number)){
//                    Logging.logger.info("node doesn't have " + shard_number);
                    is_founded = true;
                    break;
                }
            }
            data.put("shard_number", shard_number);
            data.put("node_id",Collog.getInstance().getSlaveHasShard(shard_number).get("node_id").toString());

            try {

                (new DataNodeManager()).sendReplicaAllocationRequest(Integer.parseInt(under_node.get("node_id").toString()), data);
            } catch (Exception e) {
                e.printStackTrace();
            }

            under_node_shards.add(shard_number);
//            over_node_shards.remove(over_size-1);
            over_node_shards.remove(index);
            if(under_node_shards.size() >= mean){
                under_mean_nodes.remove(under_node);
            }
        }


    }

}
