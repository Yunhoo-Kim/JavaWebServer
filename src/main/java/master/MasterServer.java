package master;


import annotations.URLAnnotation;
import collog.Collog;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import helper.Helper;
import master.inputmodule.*;
import scala.actors.threadpool.Arrays;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.Executors;


public class MasterServer implements Runnable {
    /**
     * MasterServer is a WebServer for Master
     * This can register and unregister data node.
     * Also this can get data from various input module.
     *
     */
    private int port = 0;

    public MasterServer(int port){
        this.port = port;
    }

    @Override
    public void run() {
        ArrayList<Class<?>> views_list = Helper.getClassesForPackage("master.views");
        // Retreive classes in package api view and find url
        InetSocketAddress addr = new InetSocketAddress(port);
        HttpServer web_server = null;
        MasterMetaStorage.getInstance(); //Initialize Master Meta store

        try {
            web_server = HttpServer.create(addr, 0);


            /**
             * Register Contexts in web server
             */

            for (Class<?> cls : views_list) {
                URLAnnotation a = cls.getAnnotation(URLAnnotation.class);
                web_server.createContext("/" + a.value(), (HttpHandler) cls.newInstance());
                System.out.println(a.value());
            }
            web_server.setExecutor(Executors.newFixedThreadPool(30));
            web_server.start();

            //TODO modulizing
//            runInputModule();
            Collog.getInstance().http_server = web_server;


        } catch (IOException e) {
            e.printStackTrace();
        }catch(InstantiationException e){
            e.printStackTrace();
        }
        catch(IllegalAccessException e){
            e.printStackTrace();
        }
    }

    /**
     * TODO 적절한 곳으로 옮겨줘야 할 부분
     */
    public static void runInputModule() {
        LineListener<String> listener = new LineListener<String>() {
            @Override
            public void handle(String data) {
                try {
                    (new DataInputManager()).inputDataRequestToMaster(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void errorHandle(String data) {

            }
        };
        switch (Collog.getInstance().getInput_module()){
            case "tcp":
                TcpInputModule tcpmodule = new TcpInputModule(Collog.getInstance().getTcp_port(),listener);
                new Thread(tcpmodule).start();
                break;
            case "upd":
                UdpInputModule udpmodule = new UdpInputModule(Collog.getInstance(),listener);
                new Thread(udpmodule).start();
                break;
            case "file":
                FileInputModule filemodule = new FileInputModule(Collog.getInstance().getFile_name(),listener);
                new Thread(filemodule).start();
                break;
            case "kafka":
                KafkaInputModule kafkaInputModule = new KafkaInputModule(Arrays.asList(Collog.getInstance().getTopics()),listener);
                new Thread(kafkaInputModule).start();
                break;
            default:
                break;
        }
    }
}

