package master;

import collog.Collog;

import java.util.ArrayList;

public class MasterMetaStorage {
    private static MasterMetaStorage instance = new MasterMetaStorage();

    public static MasterMetaStorage getInstance() {
        return instance;
    }

    private ArrayList<Integer> unallocation_shards = new ArrayList<>();

    private MasterMetaStorage() {
        /*
        shards information initializing
         */
        int num_of_shards = Collog.getInstance().getShards();
        for(int i=0;i<num_of_shards;i++){
            unallocation_shards.add(i);
        }

    }
}
