package master;

import collog.Collog;

import java.util.ArrayList;

public class MasterMetaStorage {
    private static MasterMetaStorage instance = null;

    public static MasterMetaStorage getInstance() {
        if(instance==null){
            instance = new MasterMetaStorage();
        }

        return instance;
    }

    private ArrayList<Integer> unallocation_shards = new ArrayList<>();

    private MasterMetaStorage() {
        this.initUnallocationShard();
    }

    public void initUnallocationShard(){
        /*
        shards information initializing
         */
        int num_of_shards = Collog.getInstance().getShards();
        for(int i=0;i<num_of_shards;i++){
            unallocation_shards.add(i);
        }
    }
    public void removeUnallocationShard(Integer a){
        unallocation_shards.remove(a);
    }
    public ArrayList<Integer> getUnallocationShards() {
        return unallocation_shards;
    }
}
