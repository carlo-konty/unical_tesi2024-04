package com.tesi.unical.util;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

@Slf4j
public class FileUtils {

    public static Boolean write(String table, List<JSONObject> listToWrite) {
        try {
            String fileName = getFileName("C:\\Users\\Giuseppe\\OneDrive\\Desktop\\" + table);
            FileWriter writer = new FileWriter(fileName);
            for(JSONObject json : listToWrite) {
                writer.append(json + "\n");
            }
            writer.close();
            return true;
        } catch (Exception e) {
            log.error("Exception: {}",e.getMessage());
            return false;
        }
    }

    private static String getFileName(String fileName) {
        String name = fileName;
        log.info("name: {}",name);
        boolean exists = checkFile(fileName + ".txt");
        int i = 1;
        log.info("exists: {}",exists);
        while(exists) {
            if(exists) {
                name = fileName + " (" + i + ")";
            }
            exists = checkFile(name + ".txt");
            log.info("exists2: {}",exists);
            i++;
        }
        return name + ".txt";
    }

    private static Boolean checkFile(String fileName) {
        File file = new File(fileName);
        if(file.isFile() && !file.isDirectory())
            return true;
        return false;
    }
}
