package collog;


import WebServer.WebServer;
import org.json.simple.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

public class Collog {
    /**
     * Base class for collog node
     * It can read a properties file and Run master or data node
     * Singleton class
     */

    private static Collog instance;
    private boolean is_master = false;
    private String input_module = null;
    private String tcp_ip = null;
    private int tcp_port = 0;
    private String udp_ip = null;
    private int udp_port = 0;
    private String http_ip = null;
    private int http_port = 0;
    private String file_name = null;
    private String master_ip = null;
    private String master_port = null;
    private int webservice_port = 0;
    private boolean webservice = false;
    private Thread master_thread = null;
    private Thread data_thread = null;
    private Thread webservice_thread = null;

    /*
    Data node List
     */
    ArrayList<JSONObject> slave_table = new ArrayList<JSONObject>();

    private Collog() {
        try {
            this.readProperties();

            if(this.webservice) {
                this.webservice_thread = new Thread(new WebServer(this.webservice_port));
                this.webservice_thread.start();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized Collog getInstance(){
        if(instance == null){
            instance = new Collog();
        }
        return instance;
    }
    private void readProperties() throws IOException {
        Properties properties = new Properties();
        FileInputStream in = new FileInputStream("src/properties/settings.properties");
        properties.load(new InputStreamReader(in, "UTF-8"));
        in.close();

        this.setProperties(properties);
    }

    private void setProperties(Properties properties) {
        this.is_master = Boolean.parseBoolean(properties.getProperty("master", "true"));

        if (this.is_master) {
            this.input_module = properties.getProperty("input_module");
            this.webservice = Boolean.parseBoolean(properties.getProperty("webservice","false"));

            if(this.webservice){
                this.webservice_port = Integer.parseInt(properties.getProperty("webservice_port"));
            }
            switch (this.input_module) {
                case "file":
                    this.file_name = properties.getProperty("file_name");
                    break;
                case "udp":
                    this.udp_port = Integer.parseInt(properties.getProperty("udp_port"));
                    break;
                case "tcp":
                    this.tcp_port = Integer.parseInt(properties.getProperty("tcp_port"));
                    break;
                case "http":
                    this.http_port = Integer.parseInt(properties.getProperty("http_port"));
                    break;
            }


        } else {
            // data node
            this.master_ip = properties.getProperty("master_IP");
            this.master_port = properties.getProperty("master_PORT");


        }
    }


    public void addSlave(JSONObject json){
        this.slave_table.add(json);
    }

    public void removeSlave(int id){
        Iterator<JSONObject> iter = this.slave_table.iterator();

        while(iter.hasNext()){
            JSONObject temp = iter.next();
            if((int)temp.get("node_id") == id){
                this.slave_table.remove(temp);
                break;
            }
        }
    }



    public static void main(String[] args){
        Collog.getInstance();
    }


}
