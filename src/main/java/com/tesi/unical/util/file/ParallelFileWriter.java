package com.tesi.unical.util.file;

import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ParallelFileWriter implements Runnable {

    private final String filePath;
    private final List<JSONObject> data;

    public ParallelFileWriter(String filePath, List<JSONObject> data) {
        this.filePath = filePath;
        this.data = data;
    }

    @Override
    public void run() {
        try {
            FileWriter fileWriter = new FileWriter(filePath, true);
            for(JSONObject json : data) {
                fileWriter.write(json.toString() + "\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
