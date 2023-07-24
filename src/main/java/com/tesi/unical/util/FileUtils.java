package com.tesi.unical.util;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

@Slf4j
public class FileUtils {

    private static final String FILE_PATH = "C:\\Users\\Giuseppe\\OneDrive\\Desktop\\";

    public static Boolean write(String table, List<JSONObject> listToWrite) {
        try {
            String fileName = getFileName(FILE_PATH + table);
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

    public static String fileNameBuilder(String table) {
        return getFileName(FILE_PATH + table);
    }

    private static String getFileName(String fileName) {
        String name = fileName;
        boolean exists = checkFile(fileName + ".txt");
        int i = 1;
        while(exists) {
            if(exists) {
                name = fileName + " (" + i + ")";
            }
            exists = checkFile(name + ".txt");
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
