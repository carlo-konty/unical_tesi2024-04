package com.tesi.unical.util;

import com.tesi.unical.entity.dto.ColumnMetaData;
import com.tesi.unical.entity.dto.MetaDataDTO;
import com.tesi.unical.service.informationSchema.InformationSchemaService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.FileWriter;
import java.sql.*;
import java.util.*;

@Service
@Slf4j
public class MigrationService {

    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.password}")
    private String psw;
    @Value("${spring.datasource.username}")
    private String user;

    @Autowired
    private InformationSchemaService informationSchemaService;

    //conosco i tipi delle colonne grazie a ColumnMetaData (andrebbero mappati per castati correttamente)
    private List<String> columnsType(String schema, String table) {
        List<String> result = new ArrayList<>();
        List<ColumnMetaData> columnMetaData = this.informationSchemaService.getColumnMetaDataByTable(schema, table);
        for(ColumnMetaData dto : columnMetaData) {
            result.add(dto.getDataType());
        }
        return result;
    }

    public String migrateReference(String schema, String table) {
        return null;
    }

    /*
    il servizio serve per migrare secondo la metodologia embedding, cioè inseriamo tutte le relazioni come array di
    json all'interno del documento che stiamo creando
     */
    public String migrateEmbedding(String schema, String table) { //embedding
        //check sulle possibili tabelle
        this.checkTable(schema,table);
        //inizializzazione variabili
        Connection connection;
        String query;
        List<JSONObject> mainTableJsonList;
        Map<String,List<JSONObject>> foreignKeys = new HashMap<>();
        //recupero metadati
        List<ColumnMetaData> columnMetaData = this.informationSchemaService.getColumnMetaDataByTable(schema, table);
        List<MetaDataDTO> metaDataDTOList = this.informationSchemaService.getDBMetaData(schema,table);
        String primaryKey = this.informationSchemaService.getPrimaryKey(schema,table);
        try {
            //get connection
            connection = DriverManager.getConnection(url,user,psw);
            query = QueryBuilder.selectAll(schema,table,columnMetaData);
            log.info(query);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            //creare una lista di json e scrivere nel file dopo il ciclo per rimanere nell'ordine n^2
            //
            //
            mainTableJsonList = fillJsonList(resultSet,columnMetaData);
            //
            //
            //creare le liste delle tabelle collegate se serve
            if(metaDataDTOList != null && !metaDataDTOList.isEmpty()) {
                log.info("String metadata size: {}",metaDataDTOList.size());
                for(MetaDataDTO dto : metaDataDTOList) {
                    query = QueryBuilder.join(schema,table,dto);
                    resultSet = statement.executeQuery(query);
                    log.info(query);
                    String fkTableName = dto.getFkTableName();
                    List<JSONObject> fkResultSet = fillJsonListByColumnName(resultSet,this.informationSchemaService.getColumnMetaDataByTable(schema,dto.getFkTableName()));
                    log.info("fkTableName: {}",fkTableName);
                    log.info("fkResultSet size: {}",fkResultSet.size());
                    foreignKeys.put(fkTableName,fkResultSet);
                }
            }
            //crea se serve il json finale
            //
            this.embeddedJson(primaryKey,mainTableJsonList,foreignKeys);
        } catch (Exception e) {
            return e.getMessage();
        }
        //scrive su file
        //
        //
        return this.write(table,mainTableJsonList).toString();
    }

    private Boolean embeddedJson(String primaryKey, List<JSONObject> mainTableJsonList, Map<String,List<JSONObject>> foreignKeys) {
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

    private Boolean write(String table,List<JSONObject> listToWrite) {
        try {
            FileWriter writer = new FileWriter("C:\\Users\\Giuseppe\\OneDrive\\Desktop\\" + table + ".txt");
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

    private List<JSONObject> fillJsonList(ResultSet resultSet, List<ColumnMetaData> columnMetaDataList) {
        List<JSONObject> result = new LinkedList<>();
        try {
            while(resultSet.next()) {
                Object column;
                JSONObject json = new JSONObject();
                for(int i=0; i<columnMetaDataList.size(); i++) {
                    column = resultSet.getObject(i+1);
                    json.put(columnMetaDataList.get(i).getColumnName(),column);
                }
                result.add(json);
            }
        } catch (Exception e) {
            log.error("ERROR FETCHING ROW");
        }
        return result;
    }

    private List<JSONObject> fillJsonListByColumnName(ResultSet resultSet, List<ColumnMetaData> columnMetaDataList) {
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

    private void checkTable(String schema, String table) {
        List<String> tablesList = this.informationSchemaService.getAllTables(schema);
        if(!tablesList.contains(table))
            throw new RuntimeException("La tabella non esiste");
    }
}