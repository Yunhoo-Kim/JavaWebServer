package data;

import collog.Collog;
import webclient.WebClient;

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
}
