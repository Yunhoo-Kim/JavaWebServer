package WebServer;

import java.io.File;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.Executors;

import annotations.URLAnnotation;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class WebServer {
    private static ArrayList<Class<?>> getClassesForPackage(String pkg) {
        /**
         * Get all classes in package named pkg
         */

        String pkgname = pkg;
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        // Get a File object for the package
        File directory = null;
        String fullPath;
        String relPath = pkgname.replace('.', '/');
//	    System.out.println("ClassDiscovery: Package: " + pkgname + " becomes Path:" + relPath);
        URL resource = ClassLoader.getSystemClassLoader().getResource(relPath);
//	    System.out.println("ClassDiscovery: Resource = " + resource);
        if (resource == null) {
            throw new RuntimeException("No resource for " + relPath);
        }
        fullPath = resource.getFile();
        System.out.println("ClassDiscovery: FullPath = " + resource);

        try {
            directory = new File(resource.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(pkgname + " (" + resource + ") does not appear to be a valid URL / URI.  Strange, since we got it from the system...", e);
        } catch (IllegalArgumentException e) {
            directory = null;
        }
        System.out.println("ClassDiscovery: Directory = " + directory);

        if (directory != null && directory.exists()) {
            // Get the list of the files contained in the package
            String[] files = directory.list();
            for (int i = 0; i < files.length; i++) {
                // we are only interested in .class files
                if (files[i].endsWith(".class")) {
                    // removes the .class extension
                    String className = pkgname + '.' + files[i].substring(0, files[i].length() - 6);
                    System.out.println("ClassDiscovery: className = " + className);
                    try {
                        classes.add(Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("ClassNotFoundException loading " + className);
                    }
                }
            }
        }
        return classes;
    }

    public static void main(String argv[]) throws Exception {

        ArrayList<Class<?>> views_list = getClassesForPackage("api.views");
        // Retreive classes in package api view and find url
        InetSocketAddress addr = new InetSocketAddress(8888);
        HttpServer web_server = HttpServer.create(addr, 0);
        /**
         * Register Contexts in web server
         */
        for (Class<?> cls : views_list) {
            URLAnnotation a = cls.getAnnotation(URLAnnotation.class);
            web_server.createContext("/" + a.value(), (HttpHandler) cls.newInstance());
            System.out.println(a.value());
        }

        web_server.setExecutor(Executors.newCachedThreadPool());
        web_server.start();


    }
}
