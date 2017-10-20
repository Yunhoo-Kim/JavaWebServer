package master.inputmodule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by semaj on 17. 10. 20.
 */

public class FileInputModule {


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
            bufferedReader.close();

            callBack.callback(data);

        } catch (IOException e) {
            e.printStackTrace();
            callBack.onFailure();
        }

    }
}
