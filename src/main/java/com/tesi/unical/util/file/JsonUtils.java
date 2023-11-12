package com.tesi.unical.util.file;

import com.tesi.unical.entity.dto.ColumnMetaData;
import com.tesi.unical.util.Utils;
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

    private ResultSet resultSet;
    private List<ColumnMetaData> columnMetaDataList;

    public JsonUtils(ResultSet resultSet, List<ColumnMetaData> columnMetaDataList) {
        this.columnMetaDataList = columnMetaDataList;
        this.resultSet = resultSet;
    }

    public JSONObject addField(JSONObject json, String key, Object value) {
        return new JSONObject(json.append(key,value));
    }

    public JSONObject removeField(JSONObject json, String key) {
        return new JSONObject(json.remove(key));
    }

    public List<JSONObject> fillJsonListByColumnName(ResultSet resultSet, List<ColumnMetaData> columnMetaDataList) {
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
            log.error(e.getMessage());
        }
        return result;
    }

    public JSONObject fillJsonByFieldsName(List<Object> row, List<ColumnMetaData> columnMetaDataList) {
        if(Utils.isCollectionEmpty(row) || Utils.isCollectionEmpty(columnMetaDataList))
            throw new RuntimeException("row or column empty");
        if(row.size()!=columnMetaDataList.size())
            return null;
        JSONObject json = new JSONObject();
        //viene fatto autoboxing dell'oggetto
        try {
            for(int i=0; i<row.size(); i++) {
                json.append(columnMetaDataList.get(i).getColumnName(),row.get(i));
            }
        } catch (Exception e) {
            return null;
        }
        return json;
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
            Long rowId = 1L;
            while (resultSet.next()) {
                List<Object> row = new LinkedList<>();
                ResultSetMetaData metaData = resultSet.getMetaData();
                for(int i=1; i<=metaData.getColumnCount(); i++) {
                    row.add(resultSet.getObject(i));
                }
                result.put(rowId,row);
                rowId++;
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return result;
    }

    public static List<Object> objectList(ResultSet resultSet) {
        List<Object> result = new LinkedList<>();
        try {
            while (resultSet.next()) {
                List<Object> row = new LinkedList<>();
                ResultSetMetaData metaData = resultSet.getMetaData();
                for(int i=1; i<=metaData.getColumnCount(); i++) {
                    row.add(resultSet.getObject(i));
                }
                result.add(row);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return result;
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