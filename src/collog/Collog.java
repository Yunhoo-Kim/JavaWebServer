package collog;


import WebServer.WebServer;
import com.sun.net.httpserver.HttpServer;
import data.DataNodeServer;
import helper.Helper;
import master.MasterMetaStorage;
import master.MasterServer;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.stream.Stream;

public class Collog {
    /**
     * Base class for collog node
     * It can read a properties file and Run master or data node
     * Singleton class
     */

    public static Collog instance;
    private boolean is_master = false;
    private int port = 0;
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
    private int shards = 4;
    private int replication = 0;
    private int webservice_port = 0;
    private boolean webservice = false;
    private Thread master_thread = null;
    private Thread data_thread = null;
    private Thread webservice_thread = null;
    public int id;
    public HttpServer server = null;
    /*
    Data node List
     */
    ArrayList<JSONObject> slave_table = new ArrayList<JSONObject>();

    private Collog() {
        try {
            this.id = (int) (System.currentTimeMillis() / 1000);
            this.readProperties();

            if(this.webservice) {
                this.webservice_thread = new Thread(new WebServer(this.webservice_port));
                this.webservice_thread.start();
            }
            if(this.is_master){
                this.master_thread = new Thread(new MasterServer(this.port));
                this.master_thread.start();
//                MasterMetaStorage.getInstance();

            }else{
                this.data_thread = new Thread(new DataNodeServer(this.port));
                this.data_thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Collog getInstance(){
        if(instance == null){
            System.out.println("#############Create Collog Instance#################");
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
        this.port = Integer.parseInt(properties.getProperty("port"));

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
            if(Integer.parseInt(temp.get("node_id").toString()) == id){
                this.slave_table.remove(temp);
                break;
            }
        }
    }

    public JSONObject getSlave(int node_id){
        Iterator<JSONObject> iter = this.slave_table.iterator();
        JSONObject temp = null;
        while(iter.hasNext()){
            temp = iter.next();
            if(Integer.parseInt(temp.get("node_id").toString()) == node_id){
                return temp;
            }
        }

        return temp;
    }

    public ArrayList<JSONObject> getSlaveTable() {
        return slave_table;
    }

    public static void main(String[] args){
        Collog.getInstance();


    }


    public int getShards(){
        return this.shards;
    }

    public String getMasterIp(){
        return master_ip;
    }

    public String getMasterPort(){
        return master_port;
    }

    public String getMyIP(){
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
//        return addr.getHostAddress();

        return "127.0.0.1";
    }

    public int getPort(){
        return this.port;
    }


    public String getInput_module() {
        return input_module;
    }

    public String getTcp_ip() {
        return tcp_ip;
    }

    public int getTcp_port() {
        return tcp_port;
    }

    public String getUdp_ip() {
        return udp_ip;
    }

    public int getUdp_port() {
        return udp_port;
    }

    public String getHttp_ip() {
        return http_ip;
    }

    public int getHttp_port() {
        return http_port;
    }

    public String getFile_name() {
        return file_name;
    }
}
