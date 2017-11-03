package WebServer;

import annotations.ContentType;
import annotations.URLAnnotation;
import annotations.URLMethod;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ServerThread implements Runnable {
    /*
    Do not use anymore
     */
	private static final String DEFAULT_FILE_PATH = "index.json";
	private Socket connectionSocket;

	public ServerThread(Socket connectionSocket) {
		this.connectionSocket = connectionSocket;
	}

	private static ArrayList<Class<?>> getClassesForPackage(String pkg) {
		String pkgname = pkg;
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		// Get a File object for the package
		File directory = null;
		String fullPath;
		String relPath = pkgname.replace('.', '/');
		System.out.println("ClassDiscovery: Package: " + pkgname
				+ " becomes Path:" + relPath);
		URL resource = ClassLoader.getSystemClassLoader().getResource(relPath);
		System.out.println("ClassDiscovery: Resource = " + resource);
		if (resource == null) {
			throw new RuntimeException("No resource for " + relPath);
		}
		fullPath = resource.getFile();
		System.out.println("ClassDiscovery: FullPath = " + resource);

		try {
			directory = new File(resource.toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(
					pkgname
							+ " ("
							+ resource
							+ ") does not appear to be a valid URL / URI.  Strange, since we got it from the system...",
					e);
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
					String className = pkgname + '.'
							+ files[i].substring(0, files[i].length() - 6);
					System.out.println("ClassDiscovery: className = "
							+ className);
					try {
						classes.add(Class.forName(className));
					} catch (ClassNotFoundException e) {
						throw new RuntimeException(
								"ClassNotFoundException loading " + className);
					}
				}
			}
		}
		return classes;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("WebServer Thread Created");
		BufferedReader inFromClient = null;
		DataOutputStream outToClient = null;

		try {
			inFromClient = new BufferedReader(new InputStreamReader(
					connectionSocket.getInputStream()));
			outToClient = new DataOutputStream(
					connectionSocket.getOutputStream());
			String requestMessageLine = inFromClient.readLine();
			System.out.println("Request message is " + requestMessageLine);
            System.out.println("Request body is " + inFromClient.readLine());
            System.out.println("Request body is " + inFromClient.readLine());
            System.out.println("Request body is " + inFromClient.readLine());
            System.out.println("Request body is " + inFromClient.readLine());
            System.out.println("Request body is " + inFromClient.readLine());
			StringTokenizer tokenizedLine = new StringTokenizer(
					requestMessageLine);

			ArrayList<Class<?>> classes = getClassesForPackage("api.views");
			String method = tokenizedLine.nextToken();
			String url = tokenizedLine.nextToken();

			url = url.substring(1);
			System.out.println("URL is : " + url);
			boolean is_founded = false;
			for (Class<?> cls : classes) {
				URLAnnotation url_ano = cls.getAnnotation(URLAnnotation.class);
				if (url_ano.value().equals(url)) {

					Method[] methods = cls.getDeclaredMethods();

					for (Method m : methods) {
						Annotation an = m.getAnnotation(URLMethod.class);
						URLMethod url_method = (URLMethod) an;
						if(url_method.value().equals(method)) {
							is_founded = true;
							Object o = cls.newInstance();
							System.out.println("URL Method " + url_method.value());
							byte[] ret = (byte[]) m.invoke(o);

							Annotation content_an = m
									.getAnnotation(ContentType.class);
							ContentType content_ano = (ContentType) content_an;
							String content_type = content_ano.value();
							int numOfBytes = (int) ret.length;
							outToClient
									.writeBytes("HTTP/1.0 200 Document Follows \r\n");
							outToClient.writeBytes("Content-Type: " + content_type + ";charset=utf-8"
									+ "\r\n");
							outToClient.writeBytes("Content-Length: " + numOfBytes
									+ "\r\n");
							outToClient.writeBytes("\r\n");
							outToClient.write(ret, 0, numOfBytes);
							outToClient.close();
						}
					}
				}
			}

			if(!is_founded){
				 System.out.println("Bad Request");

				 outToClient.writeBytes("HTTP/1.0 404 Not Found \r\n");
				 outToClient.writeBytes("Connection: close\r\n");
				 outToClient.writeBytes("\r\n");
				 outToClient.close();
			}
			// if (tokenizedLine.nextToken().equals("GET")) {
			// String fileName = tokenizedLine.nextToken();
			// if (fileName.startsWith("/") == true) {
			// if (fileName.length() > 1) {
			// fileName = fileName.substring(1);
			// System.out.println("fileName is " + fileName);
			//
			// } else {
			// fileName = DEFAULT_FILE_PATH;
			// }
			// }
			// File file = new File(fileName);
			//
			// if (file.exists()) {
			// String mimeType = new MimetypesFileTypeMap()
			// .getContentType(file);
			// int numOfBytes = (int) file.length();
			//
			// FileInputStream inFile = new FileInputStream(fileName);
			// byte[] fileInBytes = new byte[numOfBytes];
			// inFile.read(fileInBytes);
			//
			// mimeType = "application/json";
			// outToClient
			// .writeBytes("HTTP/1.0 200 Document Follows \r\n");
			// outToClient
			// .writeBytes("Content-Type: " + mimeType + "\r\n");
			//
			// outToClient.writeBytes("Content-Length: " + numOfBytes
			// + "\r\n");
			// outToClient.writeBytes("\r\n");
			//
			// outToClient.write(fileInBytes, 0, numOfBytes);
			// inFile.close();
			// } else {
			// System.out
			// .println("Requested File Not Found : " + fileName);
			//
			// outToClient.writeBytes("HTTP/1.0 404 Not Found \r\n");
			// outToClient.writeBytes("Connection: close\r\n");
			// outToClient.writeBytes("\r\n");
			// }
			// } else {
			// System.out.println("Bad Request");
			//
			// outToClient.writeBytes("HTTP/1.0 400 Bad Request Message \r\n");
			// outToClient.writeBytes("Connection: close\r\n");
			// outToClient.writeBytes("\r\n");
			// }

			System.out.println("Connection Closed");
		} catch (IOException ioe) {
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				connectionSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
