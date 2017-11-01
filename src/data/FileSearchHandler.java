package data;

import com.sun.deploy.util.StringUtils;
import helper.Helper;
import org.json.simple.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
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

    public ArrayList<JSONObject> countSearch(JSONObject json, int shard){
        ArrayList<JSONObject> founded = new ArrayList<>();
        String key = json.get("key").toString();
        double time1 = Double.parseDouble(json.get("time1").toString());
        double time2 = Double.parseDouble(json.get("time2").toString());
        try (Stream<String> lines = Files.lines(Paths.get(String.format("data/%d/data.txt", shard)))){

            for(String line : (Iterable<String>)lines::iterator){
                JSONObject _j = Helper.encodeToJson(line);
                JSONObject temp = new JSONObject();
                double time = Double.parseDouble(_j.get("@timestamp").toString());
                if(_j.containsKey(key) && time1 <= time && time <= time2){
                    temp.put(_j.get(key).toString(), 1);
                    founded.add(temp);
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

    public ArrayList<JSONObject> maxSearch(JSONObject json, int shard){
        ArrayList<JSONObject> founded = new ArrayList<>();
        String key = json.get("key").toString();
        double time1 = Double.parseDouble(json.get("time1").toString());
        double time2 = Double.parseDouble(json.get("time2").toString());

        try (Stream<String> lines = Files.lines(Paths.get(String.format("data/%d/data.txt", shard)))){


            Iterator<String> iter = lines.iterator();
            if (!iter.hasNext()){
                return founded;
            }

            JSONObject maxValue = Helper.encodeToJson(iter.next());

            for(String line : (Iterable<String>)lines::iterator){
                JSONObject _j = Helper.encodeToJson(line);
                double time = Double.parseDouble(_j.get("@timestamp").toString());
                if(_j.containsKey(key) && time1 <= time && time <= time2) {
                    if ((Long) _j.get(key) > (Long) maxValue.get(key)) {
                        maxValue = _j;
                    }
                }
            }
            founded.add(maxValue);
        } catch (FileNotFoundException e) {
            return founded;
        } catch(NoSuchFileException e){
            return founded;
        } catch (IOException e) {
            return founded;
        }
        return founded;
    }

    public ArrayList<JSONObject> minSearch(JSONObject json, int shard){
        ArrayList<JSONObject> founded = new ArrayList<>();
        String key = json.get("key").toString();
        double time1 = Double.parseDouble(json.get("time1").toString());
        double time2 = Double.parseDouble(json.get("time2").toString());

        try (Stream<String> lines = Files.lines(Paths.get(String.format("data/%d/data.txt", shard)))){

            Iterator<String> iter = lines.iterator();
            if (!iter.hasNext()){
                return founded;
            }

            JSONObject minValue = Helper.encodeToJson(iter.next());

            for(String line : (Iterable<String>)lines::iterator){
                JSONObject _j = Helper.encodeToJson(line);
                double time = Double.parseDouble(_j.get("@timestamp").toString());
                if(_j.containsKey(key) && time1 <= time && time <= time2){
                    if ((Long) _j.get(key) < (Long) minValue.get(key)) {
                        minValue = _j;
                    }
                }
            }
            founded.add(minValue);

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

