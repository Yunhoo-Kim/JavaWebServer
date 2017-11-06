package master;

import collog.Collog;
import webclient.WebClient;

/**
 * Created by semaj on 17. 11. 1.
 */

public class DataInputManager {

    public void inputDataRequestToMaster(String body) throws Exception{
        String master_ip = Collog.getInstance().getMyIP();
        int master_port = Collog.getInstance().getPort();
        WebClient wcli = new WebClient();
        String url = String.format("http://%s:%s/master/data/input/", master_ip ,master_port);
        System.out.println(url);
        wcli.sendPostRequestWithJson(url, body);
    }
}
