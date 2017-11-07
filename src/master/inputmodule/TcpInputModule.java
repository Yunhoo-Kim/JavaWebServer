package master.inputmodule;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by semaj on 17. 10. 20.
 */

public class TcpInputModule implements Runnable{

    int port;

    public TcpInputModule(int tcp_port) {
        this.port = tcp_port;
    }

    @Override
    public void run() {
        ServerSocket serverSocket;
        try {
            //서버 소켓 열고
            serverSocket = new ServerSocket(port);

            // 일단 항상 열어놓기
            while(true) {

                Socket connection = serverSocket.accept();
                //여기부터 분기
                new TcpServerThread(connection.getInputStream()).start();

            }
//            serverSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private class TcpServerThread extends Thread{
        private BufferedReader inFromClient;
        private String clientString;

        public TcpServerThread(InputStream inputStream) {
            this.inFromClient = new BufferedReader(new InputStreamReader(inputStream));
        }

        @Override
        public void run() {
            String line = "";
            //연결 끊길때까지 data 받아오기
            try {
                while ((line = inFromClient.readLine()) != null) {
                    clientString += line;
                    System.out.println(line);
                }
                //모아진 data 보내기 - onSuccess? synchronize 필요
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
