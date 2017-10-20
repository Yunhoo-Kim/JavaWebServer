package data;

import helper.Helper;
import org.json.simple.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

public class FileSearchHandler {
    public ArrayList<JSONObject> search(JSONObject json,int shard){
        /**
         * This method for Searching file follow input json request like below
         * {
         * "key" : "json key",
         * "value" : "value"
         * }
         *
         * Find the json in data directory and Return the list we found in the file!
         */
        String key = json.get("key").toString();
        String value = json.get("value").toString();

        ArrayList<JSONObject> founded = new ArrayList<>();

        try (Stream<String> lines = Files.lines(Paths.get(String.format("data/%d/data.txt", shard)))){

            for(String line : (Iterable<String>)lines::iterator){

                JSONObject _j = Helper.encodeToJson(line);
                if(_j.containsKey(key)){
                    if(_j.get(key).equals(value)){
                        founded.add(_j);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            return founded;
        } catch(NoSuchFileException e){
            return founded;
        } catch (IOException e) {
            return founded;
        }
        return founded;
    }
}
