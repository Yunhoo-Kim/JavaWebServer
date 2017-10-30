package master.inputmodule;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * Created by semaj on 17. 10. 20.
 */

public class FileInputModule implements Runnable{


    public void inputDataFromFile(String fileName, SimpleCallBack<String> callBack){
        String data ="";

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));

            while(true){
                String line = bufferedReader.readLine();
                if(line == null) { // 파일 끝부분 오면 뒷내용 추가될때까지 대기하도록
                    //아마도 지금까지 읽은 부분만 보내도록 해야할듯?

                    //데이터 전송후 초기화
                    data = "";
                    //읽을 수 있는 상태일때까지 대기
                    while(!bufferedReader.ready())
                        Thread.sleep(500);
                }
                else
                    data += line;

            }

        } catch (IOException e) {
            e.printStackTrace();
            callBack.onFailure();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private String inputFile() {
        System.out.print("Input file name : ");
        Scanner scan = new Scanner(System.in);

        String fileNameTemp = scan.nextLine();

        if(validFileName(fileNameTemp))
            return fileNameTemp;
        else
            return null;
    }

    private boolean validFileName(String fileName){
        return true;
    }

    @Override
    public void run() {

        String fileName;

        while(true){
            //input file
            fileName = inputFile();

            //read file data
            inputDataFromFile(fileName, new SimpleCallBack<String>() {
                @Override
                public void onSuccess(String data) {
                    //String data to JSON data
                }

                @Override
                public void onFailure() {

                }
            });

        }

    }
}
