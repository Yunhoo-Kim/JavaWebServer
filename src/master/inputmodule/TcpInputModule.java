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

            String clientString = "";
            String line = "";

            while(true) {

                Socket connection = serverSocket.accept();
                //여기부터 분기
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//        DataOutputStream outToClient = new DataOutputStream(connection.getOutputStream());
                //소켓 닫는데도 IOException 발생
                inFromClient.close();
                connection.close();

                serverSocket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    class TcpServerThread extends Thread{
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
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
}
