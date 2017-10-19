package data;

import com.oracle.javafx.jmx.json.JSONWriter;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileWriteHandler {

    public void write(JSONObject json, int shard){
        try {
            String file_name = String.format("data/%d/data.txt",shard);
            File file = new File(file_name);
            if(file.exists()) {
                
                FileWriter writer = new FileWriter(String.format("data/%d/data.txt", shard), true);

                writer.write(json.toString());
                writer.close();
            }else{
                file.getParentFile().mkdirs();
                file.createNewFile();
                FileWriter writer = new FileWriter(String.format("data/%d/data.txt", shard));

                writer.write(json.toJSONString());
                writer.write("\n");
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
