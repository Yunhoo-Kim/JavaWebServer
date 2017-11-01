package master.inputmodule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import collog.Collog;
import master.DataInputManager;

/**
 * Created by semaj on 17. 10. 24.
 */

public class UdpInputModule implements Runnable {
    private int port;
    private LineListener<String> listener;

    public UdpInputModule(Collog instance, LineListener<String> listener) {
        port = instance.getUdp_port();
        this.listener = listener;
    }


    @Override
    public void run() {
        ServerSocket serverSocket;
        try{
            serverSocket = new ServerSocket(port);

            while(true){
                Socket connection = serverSocket.accept();

                new UdpServerThread(connection.getInputStream()).start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    private class UdpServerThread extends Thread{

        private BufferedReader inFromClient;
        private String clientString;

        public UdpServerThread(InputStream inputStream) {
            this.inFromClient = new BufferedReader(new InputStreamReader(inputStream));
        }

        @Override
        public void run() {
            String line = "";
            //연결 끊길때까지 data 받아오기
            try {
                while ((line = inFromClient.readLine()) != null) {
//                    (new DataInputManager()).inputDataRequestToMaster(line);
                    listener.handle(line);
                    System.out.println(line);
                }
                //TODO 모아진 data 보내기 - onSuccess? synchronize 필요
            } catch (Exception e) {
                e.printStackTrace();
                listener.errorHandle(e.getMessage());
            }
        }
    }

}
