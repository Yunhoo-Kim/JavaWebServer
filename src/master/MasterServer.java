package master;


import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.Executors;

import annotations.URLAnnotation;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import helper.Helper;


public class MasterServer implements Runnable {
    private int web_service_port = 0;
    public MasterServer(int port){
        this.web_service_port = port;
    }

    @Override
    public void run() {
        ArrayList<Class<?>> views_list = Helper.getClassesForPackage("master.views");
        // Retreive classes in package api view and find url
        InetSocketAddress addr = new InetSocketAddress(web_service_port);
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

