package WebServer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
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

import javax.activation.MimetypesFileTypeMap;

public class ServerThread implements Runnable {
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
			StringTokenizer tokenizedLine = new StringTokenizer(
					requestMessageLine);

			ArrayList<Class<?>> classes = getClassesForPackage("api.views");
			String method = tokenizedLine.nextToken();
			String url = tokenizedLine.nextToken();

			url = url.substring(1);
			System.out.println("URL is : " + url);
			for (Class<?> cls : classes) {
				URLAnnotation url_ano = cls.getAnnotation(URLAnnotation.class);
				if (url_ano.value().equals(url)) {
					Method[] methods = cls.getDeclaredMethods();
					for (Method m : methods) {
						Annotation an = m.getAnnotation(URLMethod.class);
						URLMethod url_method = (URLMethod) an;
						Object o = cls.newInstance();
						System.out.println("URL Method " + url_method.value());
						String ret = (String) m.invoke(o);
						System.out.println("Return " + ret);
						Annotation content_an = m
								.getAnnotation(ContentType.class);
						ContentType content_ano = (ContentType) content_an;
						String content_type = content_ano.value();
						int numOfBytes = (int) ret.length();
						outToClient
								.writeBytes("HTTP/1.0 200 Document Follows \r\n");
						outToClient.writeBytes("Content-Type: " + content_type + ";charset=utf-8"
								+ "\r\n");
						outToClient.writeBytes("Content-Length: " + numOfBytes
								+ "\r\n");
						outToClient.writeBytes("\r\n");
						outToClient.write(ret.getBytes(), 0, numOfBytes);
//						outToClient.writeUTF(ret);
						outToClient.close();
					}
				}
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
			// // 파일의 바이트수를 찾아온다.
			// int numOfBytes = (int) file.length();
			//
			// // 파일을 스트림을 읽어들일 준비를 한다.
			// FileInputStream inFile = new FileInputStream(fileName);
			// byte[] fileInBytes = new byte[numOfBytes];
			// inFile.read(fileInBytes);
			//
			// // 정상적으로 처리가 되었음을 나타내는 200 코드를 출력한다.
			// mimeType = "application/json";
			// outToClient
			// .writeBytes("HTTP/1.0 200 Document Follows \r\n");
			// outToClient
			// .writeBytes("Content-Type: " + mimeType + "\r\n");
			//
			// // 출력할 컨텐츠의 길이를 출력
			// outToClient.writeBytes("Content-Length: " + numOfBytes
			// + "\r\n");
			// outToClient.writeBytes("\r\n");
			//
			// // 요청 파일을 출력한다.
			// outToClient.write(fileInBytes, 0, numOfBytes);
			// inFile.close();
			// } else {
			// // 파일이 존재하지 않는다는 에러인 404 에러를 출력하고 접속을 종료한다.
			// System.out
			// .println("Requested File Not Found : " + fileName);
			//
			// outToClient.writeBytes("HTTP/1.0 404 Not Found \r\n");
			// outToClient.writeBytes("Connection: close\r\n");
			// outToClient.writeBytes("\r\n");
			// }
			// } else {
			// // 잘못된 요청임을 나타내는 400 에러를 출력하고 접속을 종료한다.
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
