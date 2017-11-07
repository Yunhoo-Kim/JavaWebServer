package collog;


import WebServer.WebServer;
import com.sun.net.httpserver.HttpServer;
import data.DataNodeServer;
import data.ElectionWoker;
import heartbeat.HeartBeatManager;
import heartbeat.HeartBeatWorker;
import helper.Helper;
import logging.Logging;
import master.MasterMetaStorage;
import master.MasterServer;
import master.ShardsAllocator;
import org.apache.log4j.BasicConfigurator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import queue.HeartBeatQueue;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Collog {
    /**
     * d
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
    public HeartBeatQueue heartbeat_queue = null;
    public Map<Integer, Long> heartbeat_map = new HashMap<>();
    private boolean is_electioning = false;
    private Thread election_thread;
    public HeartBeatManager heartbeat_thread;
    public Timer timer = null;
    public HttpServer http_server;
    /*
    Data node List
     */
    ArrayList<JSONObject> slave_table = new ArrayList<>();

    private Collog() {
        BasicConfigurator.configure();

        try {
            this.id = (int) (System.currentTimeMillis() / 1000);
            this.readProperties();

            if (this.webservice) {
                this.webservice_thread = new Thread(new WebServer(this.webservice_port));
                this.webservice_thread.start();
            }
            if (this.is_master) {
                this.master_thread = new Thread(new MasterServer(this.port));
                this.master_thread.start();
//                MasterMetaStorage.getInstance();

            } else {
                this.data_thread = new Thread(new DataNodeServer(this.port));
                this.data_thread.start();
            }

            this.timer = new Timer();
            long delay = 2000;
            long period = 3000;
            this.heartbeat_thread = new HeartBeatManager(this.is_master);
            this.timer.scheduleAtFixedRate(this.heartbeat_thread, delay, period);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Collog getInstance() {
        if (instance == null) {
            System.out.println("#############Create Collog Instance#################");
            instance = new Collog();
            if (instance.is_master) {
                instance.heartbeat_queue = new HeartBeatQueue();
                Thread heartbeat_worker = new Thread(new HeartBeatWorker());
                heartbeat_worker.start();
            }
        }
        return instance;
    }

    private void readProperties() throws IOException {
        Properties properties = new Properties();
        FileInputStream in = new FileInputStream("src/main/java/properties/settings.properties");
        properties.load(new InputStreamReader(in, "UTF-8"));
        in.close();

        this.setProperties(properties);
    }

    private void setProperties(Properties properties) {
        this.is_master = Boolean.parseBoolean(properties.getProperty("master", "true"));
        this.port = Integer.parseInt(properties.getProperty("port"));

        if (this.is_master) {
            this.input_module = properties.getProperty("input_module");
            this.webservice = Boolean.parseBoolean(properties.getProperty("webservice", "false"));

            if (this.webservice) {
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


    public void addSlave(JSONObject json) {
        this.slave_table.add(json);
    }

    public void removeSlave(int id) {
        Iterator<JSONObject> iter = this.slave_table.iterator();

        while (iter.hasNext()) {
            JSONObject temp = iter.next();
            this.heartbeat_map.remove(id);
            if (Integer.parseInt(temp.get("node_id").toString()) == id) {
                this.slave_table.remove(temp);
                try {
                    for (Integer i : (ArrayList<Integer>) temp.get("shards")) {
//                Iterator<Long> iter_2 = ((ArrayList<Long>)temp.get("shards")).iterator();
//                while(iter_2.hasNext()){
                        MasterMetaStorage.getInstance().unallocation_shards.add(i);

                    }
                } catch (ClassCastException e) {
//                    e.printStackTrace();
                    for (Long l : (ArrayList<Long>) temp.get("shards")) {
//                Iterator<Long> iter_2 = ((ArrayList<Long>)temp.get("shards")).iterator();
//                while(iter_2.hasNext()){
                        MasterMetaStorage.getInstance().unallocation_shards.add(Integer.valueOf(l.intValue()));

                    }

                }
                (new ShardsAllocator()).allocateShards();
                break;
            }
        }
    }

    public JSONObject getSlave(int node_id) {
        Iterator<JSONObject> iter = this.slave_table.iterator();
        JSONObject temp = null;
        while (iter.hasNext()) {
            temp = iter.next();
            if (Integer.parseInt(temp.get("node_id").toString()) == node_id) {
                return temp;
            }
        }

        return temp;
    }

    public JSONObject getSlaveHasShard(int shard) {
        Iterator<JSONObject> iter = this.slave_table.iterator();
        JSONObject temp = null;
        while (iter.hasNext()) {
            temp = iter.next();
            if (((ArrayList<Integer>) (temp.get("shards"))).contains(shard) || ((ArrayList<Integer>) (temp.get("replica_shards"))).contains(shard)) {
                return temp;
            }
        }

        return temp;
    }

    public ArrayList<JSONObject> getSlaveTable() {
        return slave_table;
    }

    public void updateSlaveTable(ArrayList<JSONObject> table) {
        this.slave_table = table;
    }

    public static void main(String[] args) {
        Collog.getInstance();


    }


    public int getShards() {
        return this.shards;
    }

    public String getMasterIp() {
        return master_ip;
    }

    public String getMasterPort() {
        return master_port;
    }

    public String getMyIP() {
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "127.0.0.1";
    }

    public int getPort() {
        return this.port;
    }

    public int getId() {
        return this.id;
    }

    public void startElection() {
        if (this.is_electioning)
            return;
        this.is_electioning = true;
        this.timer.cancel();
        this.election_thread = new Thread(new ElectionWoker());
        this.election_thread.start();
    }

    public void setElection(boolean flag) {
        this.is_electioning = flag;
    }

    public void completeElection(JSONObject data) {
        String ip = data.get("ip").toString();
        String port = data.get("port").toString();
        this.master_ip = ip;
        this.master_port = port;
        this.election_thread.interrupt();
        this.timer = new Timer();
        long delay = 2000;
        long period = 3000;
        this.heartbeat_thread = new HeartBeatManager(this.is_master);
        this.timer.scheduleAtFixedRate(this.heartbeat_thread, delay, period);
        this.is_electioning = false;
    }

    public void runAsMaster() {
        Logging.logger.info("node " + this.id + " is new master");
        this.is_master = true;
        this.data_thread.interrupt();
        this.http_server.stop(0);
        this.timer.cancel();
        this.master_thread = new Thread(new MasterServer(this.port));
        this.master_thread.start();
        this.removeSlave(this.id);
        this.timer = new Timer();
        long delay = 2000;
        long period = 3000;
        this.heartbeat_thread = new HeartBeatManager(this.is_master);
        this.timer.scheduleAtFixedRate(this.heartbeat_thread, delay, period);
        this.is_electioning = false;

//        this.heartbeat_thread.
    }

    public boolean isElectioning() {
        return this.is_electioning;
    }

    public boolean hasShard(int node_id, int shard_num) {
        return ((ArrayList<Integer>) this.getSlave(node_id).get("shards")).contains(shard_num);
    }

    public int getTcp_port(){
        return this.tcp_port;
    }

    public int getUdp_port(){
        return this.udp_port;
    }

    public String getInput_module(){
        return this.input_module;
    }

    public String getFile_name(){
        return this.file_name;
    }
}
