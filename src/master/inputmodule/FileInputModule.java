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
                if(line == null)
                    break;
                else
                    data += line;

            }

            //data 전송

            bufferedReader.close();

            callBack.onSuccess(data);

        } catch (IOException e) {
            e.printStackTrace();
            callBack.onFailure();
        }

    }

    private String inputFile() {
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
