package master;


import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.Executors;

import annotations.URLAnnotation;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import collog.Collog;
import helper.Helper;
import master.inputmodule.FileInputModule;
import master.inputmodule.TcpInputModule;
import master.inputmodule.UdpInputModule;


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
            switch (Collog.getInstance().getInput_module()){
                case "tcp":
                    TcpInputModule tcpmodule = new TcpInputModule(Collog.getInstance().getTcp_port());
                    new Thread(tcpmodule).start();
                    break;
                case "upd":
                    UdpInputModule udpmodule = new UdpInputModule(Collog.getInstance());
                    new Thread(udpmodule).start();
                    break;
                case "file":
                    FileInputModule filemodule = new FileInputModule();
                    new Thread(filemodule).start();
                    break;
                default:
                    break;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }catch(InstantiationException e){
            e.printStackTrace();
        }
        catch(IllegalAccessException e){
            e.printStackTrace();
        }
    }
}

