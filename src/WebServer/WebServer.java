package WebServer;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import WebServer.ServerThread;


public class WebServer {
//	private static ArrayList<Class<?>> getClassesForPackage(String pkg) {
//	    String pkgname = pkg;
//	    ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
//	    // Get a File object for the package
//	    File directory = null;
//	    String fullPath;
//	    String relPath = pkgname.replace('.', '/');
//	    System.out.println("ClassDiscovery: Package: " + pkgname + " becomes Path:" + relPath);
//	    URL resource = ClassLoader.getSystemClassLoader().getResource(relPath);
//	    System.out.println("ClassDiscovery: Resource = " + resource);
//	    if (resource == null) {
//	        throw new RuntimeException("No resource for " + relPath);
//	    }
//	    fullPath = resource.getFile();
//	    System.out.println("ClassDiscovery: FullPath = " + resource);
//
//	    try {
//	        directory = new File(resource.toURI());
//	    } catch (URISyntaxException e) {
//	        throw new RuntimeException(pkgname + " (" + resource + ") does not appear to be a valid URL / URI.  Strange, since we got it from the system...", e);
//	    } catch (IllegalArgumentException e) {
//	        directory = null;
//	    }
//	    System.out.println("ClassDiscovery: Directory = " + directory);
//
//	    if (directory != null && directory.exists()) {
//	        // Get the list of the files contained in the package
//	        String[] files = directory.list();
//	        for (int i = 0; i < files.length; i++) {
//	            // we are only interested in .class files
//	            if (files[i].endsWith(".class")) {
//	                // removes the .class extension
//	                String className = pkgname + '.' + files[i].substring(0, files[i].length() - 6);
//	                System.out.println("ClassDiscovery: className = " + className);
//	                try {
//	                    classes.add(Class.forName(className));
//	                } 
//	                catch (ClassNotFoundException e) {
//	                    throw new RuntimeException("ClassNotFoundException loading " + className);
//	                }
//	            }
//	        }
//	    }
//	    return classes;
//	}
	public static void main(String argv[]) throws Exception{
		
//		ArrayList<Class<?>> views_list = getClassesForPackage("api.views");
//		// Retreive classes in package api view and find url
//		for(Class<?> cls : views_list){
//			URLAnnotation a = cls.getAnnotation(URLAnnotation.class);
//			
//			System.out.println(a.value());
//		}
		
		ServerSocket listenSocket = new ServerSocket(8888);
		System.out.println("WebServer Socket Created!");
		Socket connectionSocket;
		Thread a;
		while((connectionSocket = listenSocket.accept()) != null){
			a = new Thread(new ServerThread(connectionSocket));
			a.start();
		}
	}
}
