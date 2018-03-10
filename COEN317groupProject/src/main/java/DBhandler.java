/**
 * Created by yuxia on 3/5/2018.
 */

package main.java;

import com.google.gson.Gson;
import java.io.FileWriter;
import java.util.List;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.lang.reflect.Type;
import java.util.Map;

import java.io.FileReader;

public class DBhandler {
    java.io.File relationDB;
    Gson gson;
    JsonReader reader;

    DBhandler(java.io.File relationDB) throws java.io.FileNotFoundException {

        this.relationDB = relationDB;
        gson = new Gson();
        try {
            reader = new JsonReader(new FileReader(this.relationDB));
        } catch (Exception e) {
            System.out.println("file not found");
        }
    }

    public void addRelation(String subscriber, String publisher) throws java.io.FileNotFoundException {
        Map<String, List<String>> dbmap = this.readRelation();
        List<String> updatedRelation = dbmap.get(publisher);
        updatedRelation.add(subscriber);
        try {
            gson.toJson(dbmap, new FileWriter(relationDB));
        } catch (Exception e) {
            System.out.println("file not Found");
        }
    }

    public Map readRelation() {
        Type type = new TypeToken<Map<String, List<String>>>(){}.getType();
        Map<String, String> dbmap = gson.fromJson(reader, type);
        return dbmap;
    }
}
