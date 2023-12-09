package com.tesi.unical.util.file;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
public class JsonUtils {

    public static JSONObject addField(JSONObject json, String key, Object value) {
        return new JSONObject(json.append(key,value));
    }

    public static JSONObject removeField(JSONObject json, String key) {
        return new JSONObject(json.remove(key));
    }

    public static List<JSONObject> createDocumentListByColumnName(ResultSet resultSet, List<String> columnMetaDataList) {
        List<JSONObject> result = new LinkedList<>();
        try {
            while(resultSet.next()) {
                Object column;
                JSONObject json = new JSONObject();
                for(String col : columnMetaDataList) {
                    column = resultSet.getObject(col);
                    json.put(col,column);
                }
                result.add(json);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return result;
    }

    public static Boolean embeddedJson(String primaryKey, List<JSONObject> parentDocumentList, Map<String,List<JSONObject>> childrenMap) {
        try {
            for(JSONObject parentDocument : parentDocumentList) {
                for(String key : childrenMap.keySet()) {
                    List<JSONObject> foreignJsonList = childrenMap.get(key);
                    List<JSONObject> referencedJson = new LinkedList<>();
                    for(JSONObject foreignJson : foreignJsonList) {
                        Object pk = parentDocument.get(primaryKey);
                        Object fk = foreignJson.get(primaryKey);
                        JSONObject jsonObject = new JSONObject(foreignJson.toString());
                        jsonObject.remove(primaryKey);
                        if(pk.equals(fk)) {
                            referencedJson.add(jsonObject);
                        }
                    }
                    parentDocument.put(key,referencedJson);
                }
            }
            return true;
        } catch (Exception e) {
            log.error("Error: {}",e.getMessage());
            return false;
        }
    }

    public static Boolean referenceJson(List<String> foreignKeys, List<JSONObject> childDocumentList, Map<String,List<JSONObject>> parentsMap, Map<String,List<String>> parentColumns) {
        try {
            for(JSONObject childDocument : childDocumentList) {
                for(String key : parentsMap.keySet()) {
                    List<JSONObject> parentDocumentList = parentsMap.get(key);
                    List<JSONObject> referencedJson = new LinkedList<>();
                    for(JSONObject parentDocument : parentDocumentList) {
                             for(String fk : foreignKeys) {
                                    if (parentColumns.get(key).contains(fk)) {
                                        Object p = parentDocument.get(fk);
                                        Object f = childDocument.get(fk);
                                        JSONObject jsonObject = new JSONObject(parentDocument.toString());
                                        if (p.equals(f)) {
                                             referencedJson.add(jsonObject);
                                        }
                                    }
                            }
                    }
                    for(JSONObject rj : referencedJson) {
                        childDocument.put(key,rj);
                    }
                }
            }
            return true;
        } catch (Exception e) {
            log.error("Error: {}",e.getMessage());
            return false;
        }
    }

    public static int getCount(ResultSet resultSet) {
        try {
            resultSet.next();
            return resultSet.getInt(1);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}