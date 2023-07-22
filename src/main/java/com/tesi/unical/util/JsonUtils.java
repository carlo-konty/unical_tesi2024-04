package com.tesi.unical.util;

import com.tesi.unical.entity.dto.ColumnMetaData;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
public class JsonUtils {

    public static List<JSONObject> fillJsonListByColumnName(ResultSet resultSet, List<ColumnMetaData> columnMetaDataList) {
        List<JSONObject> result = new LinkedList<>();
        try {
            while(resultSet.next()) {
                Object column;
                JSONObject json = new JSONObject();
                for(ColumnMetaData dto : columnMetaDataList) {
                    column = resultSet.getObject(dto.getColumnName());
                    json.put(dto.getColumnName(),column);
                }
                result.add(json);
            }
        } catch (Exception e) {
            log.error("ERROR FETCHING ROW");
        }
        return result;
    }
    public static Boolean embeddedJson(String primaryKey, List<JSONObject> mainTableJsonList, Map<String,List<JSONObject>> foreignKeys) {
        try {
            for(JSONObject json : mainTableJsonList) {
                for(String key : foreignKeys.keySet()) { //si spera che ci siano poche chiavi e dunque poche dipendenze
                    List<JSONObject> foreignJsonList = foreignKeys.get(key);
                    List<JSONObject> referencedJson = new LinkedList<>();
                    for(JSONObject foreignJson : foreignJsonList) {
                        Object pk = json.get(primaryKey);
                        Object fk = foreignJson.get(primaryKey);
                        log.info("json: {}", foreignJson );
                        JSONObject jsonObject = new JSONObject(foreignJson.toString());
                        jsonObject.remove(primaryKey);
                        if(pk.equals(fk)) {
                            referencedJson.add(jsonObject);
                        }
                    }
                    json.put(key,referencedJson);
                }
            }
            return true;
        } catch (Exception e) {
            log.error("Error: {}",e.getMessage());
            return false;
        }
    }

    public static Map<Long,List<Object>> extractResultSet(ResultSet resultSet) {
        Map<Long,List<Object>> result = new HashMap<>();
        try {
            while (resultSet.next()) {
                List<Object> row = new LinkedList<>();
                ResultSetMetaData metaData = resultSet.getMetaData();
                Long rowId = 1L;
                for(int i=1; i<=metaData.getColumnCount(); i++) {
                    row.add(resultSet.getObject(i));
                }
                result.put(rowId,row);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return result;
    }
}
