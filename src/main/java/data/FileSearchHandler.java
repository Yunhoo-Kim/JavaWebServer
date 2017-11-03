package data;

import helper.Helper;
import logging.Logging;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
//        String key = json.get("key").toString();
//        String value = json.get("value").toString();

        ArrayList<JSONObject> founded = new ArrayList<>();
        String file_name = String.format("data/%d/data.txt", shard);
        File file = new File(file_name);
        if(!file.exists()){
            return founded;
        }
        JSONArray must = (JSONArray) json.getOrDefault("must", new JSONArray());
        JSONArray should = (JSONArray) json.getOrDefault("should", new JSONArray());

        try (Stream<String> lines = Files.lines(Paths.get(file_name))){

            for(String line : (Iterable<String>)lines::iterator){

                JSONObject _j = Helper.encodeToJson(line);
                if(andForSearch(_j, must) || orForSearch(_j, should)){
                    founded.add(_j);
                }
//                if(_j.containsKey(key)){
//                    if(_j.get(key).equals(value)){
//                        founded.add(_j);
//                    }
//                }
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
        String file_name = String.format("data/%d/data.txt", shard);
        File file = new File(file_name);
        if(!file.exists()){
            return founded;
        }
        try (Stream<String> lines = Files.lines(Paths.get(file_name))){

            for(String line : (Iterable<String>)lines::iterator){
                JSONObject _j = Helper.encodeToJson(line);
                JSONObject temp = new JSONObject();
                double time = Double.parseDouble(_j.getOrDefault("@timestamp" ,Double.parseDouble(String.valueOf(System.currentTimeMillis()))).toString());
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


            JSONObject maxValue = new JSONObject();
            maxValue.put(key, 0);

            for(String line : (Iterable<String>)lines::iterator){
                JSONObject _j = Helper.encodeToJson(line);
                double time = Double.parseDouble(_j.getOrDefault("@timestamp" ,Double.parseDouble(String.valueOf(System.currentTimeMillis()))).toString());

                if(_j.containsKey(key) && time1 <= time && time <= time2) {
                    if (Long.parseLong(_j.get(key).toString()) > Long.parseLong(maxValue.get(key).toString())) {
                        maxValue.put(key,Long.parseLong(_j.get(key).toString()));
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
        }catch (NumberFormatException e){
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


            JSONObject minValue = new JSONObject();
            minValue.put(key, 999999999);

            for(String line : (Iterable<String>)lines::iterator){
                JSONObject _j = Helper.encodeToJson(line);
                double time = Double.parseDouble(_j.getOrDefault("@timestamp" ,Double.parseDouble(String.valueOf(System.currentTimeMillis()))).toString());

                if(_j.containsKey(key) && time1 <= time && time <= time2) {
                    if (Long.parseLong(_j.get(key).toString()) < Long.parseLong(minValue.get(key).toString())) {
                        minValue.put(key,Long.parseLong(_j.get(key).toString()));
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
        }catch (NumberFormatException e){
            return founded;
        }
        return founded;
    }

    public boolean andForSearch(JSONObject _j, JSONArray conditions){
        JSONArray new_aa = new JSONArray();

        Iterator<JSONObject> iter = conditions.iterator();
        while(iter.hasNext()){
            JSONObject temp = iter.next();
            String key = temp.get("key").toString();
            String value = temp.get("value").toString();

            if(!_j.containsKey(key)){
                return false;
            }else {
                if(!_j.get(key).equals(value)){
                    return false;
                }
            }
        }

        return true;
    }

    public boolean orForSearch(JSONObject _j, JSONArray conditions){
        JSONArray new_aa = new JSONArray();

        Iterator<JSONObject> iter = conditions.iterator();
        while(iter.hasNext()){
            JSONObject temp = iter.next();
            String key = temp.get("key").toString();
            String value = temp.get("value").toString();

            if(_j.containsKey(key)){
                if(_j.get(key).equals(value)){
                    return true;
                }
            }
        }

        return false;
    }

}
