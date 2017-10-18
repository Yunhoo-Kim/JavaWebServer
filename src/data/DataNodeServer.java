package data;

import annotations.URLAnnotation;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import helper.Helper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.Executors;


public class DataNodeServer implements Runnable {
    /**
     * MasterServer is a WebServer for Master
     * This can register and unregister data node.
     * Also this can get data from various input module.
     *
     */
    private int port = 0;

    public DataNodeServer(int port){
        this.port = port;
    }

    @Override
    public void run() {
        ArrayList<Class<?>> views_list = Helper.getClassesForPackage("data.views");
        // Retreive classes in package api view and find url
        InetSocketAddress addr = new InetSocketAddress(port);
        HttpServer web_server = null;



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
            (new MasterManager()).registerToMaster();


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

