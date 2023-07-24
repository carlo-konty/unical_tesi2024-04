package com.tesi.unical.util;

import com.tesi.unical.entity.dto.ColumnMetaData;
import com.tesi.unical.entity.dto.MetaDataDTO;
import com.tesi.unical.service.informationSchema.InformationSchemaService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    private final int NUM_THREAD = 16;

    @Autowired
    private InformationSchemaService informationSchemaService;

    /*questa metodologia prevede che ogni documento sia pari alla tabella, le relazioni sono modellate utilizzando
    il riferimento all'id del bson presente in mongo db
    */
    //todo sarebbe necessario, dopo la migrazione, costruire i riferimenti direttamente in mongo db
    public String migrateReference(String schema, String table) {
        //check sulle possibili tabelle
        this.informationSchemaService.checkTable(schema,table);
        //inizializzazione variabili
        Connection connection;
        String query;
        List<JSONObject> mainTableJsonList;
        //recupero metadati
        List<ColumnMetaData> columnMetaData = this.informationSchemaService.getColumnMetaDataByTable(schema, table);
        try {
            //get connection
            connection = DriverManager.getConnection(url, user, psw);
            query = QueryBuilder.selectAll(schema, table);
            log.info(query);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            //creare una lista di json e scrivere nel file dopo il ciclo per rimanere nell'ordine n^2
            //
            //
            mainTableJsonList = JsonUtils.fillJsonListByColumnName(resultSet, columnMetaData);
            //
            //
        } catch (Exception e) {
            return e.getMessage();
        }
        return FileUtils.write(table,mainTableJsonList).toString();
    }

    /*
    il servizio serve per migrare secondo la metodologia embedding, cioè inseriamo tutte le sottorelazioni come array di
    json all'interno del documento principale che stiamo creando (viene creato un file con il nome della tabella)
     */
    //todo valutare se è possibile migrare direttamente all'interno di mongo db
    public String migrateEmbedding(String schema, String table) { //embedding
        //check sulle possibili tabelle
        this.informationSchemaService.checkTable(schema,table);
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
            query = QueryBuilder.selectAll(schema,table);
            log.info(query);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            //creare una lista di json e scrivere nel file dopo il ciclo per rimanere nell'ordine n^2
            //
            //
            mainTableJsonList = JsonUtils.fillJsonListByColumnName(resultSet,columnMetaData);
            //
            //
            //creare le liste delle tabelle collegate se serve
            if(metaDataDTOList != null && !metaDataDTOList.isEmpty()) {
                log.info("String metadata size: {}",metaDataDTOList.size());
                for(MetaDataDTO dto : metaDataDTOList) {
                    query = QueryBuilder.join2Tables(schema,table,dto);
                    resultSet = statement.executeQuery(query);
                    log.info(query);
                    String fkTableName = dto.getFkTableName();
                    List<JSONObject> fkResultSet = JsonUtils.fillJsonListByColumnName(resultSet,this.informationSchemaService.getColumnMetaDataByTable(schema,dto.getFkTableName()));
                    log.info("fkTableName: {}",fkTableName);
                    log.info("fkResultSet size: {}",fkResultSet.size());
                    foreignKeys.put(fkTableName,fkResultSet);
                }
            }
            //crea se serve il json finale
            //
            JsonUtils.embeddedJson(primaryKey,mainTableJsonList,foreignKeys);
        } catch (Exception e) {
            return e.getMessage();
        }
        //scrive su file
        //
        //
        return FileUtils.write(table,mainTableJsonList).toString();
    }

    public String testCount() {
        Connection connection;
        String query;
        Map<Long,List<Object>> result;
        try {
            connection = DriverManager.getConnection(url,user,psw);
            query = QueryBuilder.countAll("migration","customers");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            result = JsonUtils.extractResultSet(resultSet);
        } catch (Exception e ){
            return e.getMessage();
        }
        return result.toString();
    }

    public String testFetch() {
        Connection connection;
        String query;
        Map<Long,List<Object>> result;
        try {
            connection = DriverManager.getConnection(url,user,psw);
            query = QueryBuilder.selectAll("migration","customers");
            query = QueryBuilder.fetchNRowsOnly(query,10);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            result = JsonUtils.extractResultSet(resultSet);
        } catch (Exception e ){
            return e.getMessage();
        }
        return result.toString();
    }

    public String testThread(String schema, String table) {
        //check sulle possibili tabelle
        this.informationSchemaService.checkTable(schema,table);
        //inizializzazione variabili
        Connection connection;
        String query;
        List<JSONObject> mainTableJsonList;
        //recupero metadati
        List<ColumnMetaData> columnMetaData = this.informationSchemaService.getColumnMetaDataByTable(schema, table);
        try {
            //get connection
            connection = DriverManager.getConnection(url, user, psw);
            query = QueryBuilder.selectAll(schema, table);
            log.info(query);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            //creare una lista di json e scrivere nel file dopo il ciclo per rimanere nell'ordine n^2
            //
            //
            mainTableJsonList = JsonUtils.fillJsonListByColumnName(resultSet, columnMetaData);
            //
            //test thread
            String filePath = FileUtils.fileNameBuilder(table);
            startThread(mainTableJsonList,filePath);
        } catch (Exception e) {
            return e.getMessage();
        }
        return "OK";
    }

    private void startThread(List<JSONObject> list, String filePath) {
        int elementsPerThread = list.size() / NUM_THREAD;
        log.info("el: {}",elementsPerThread);
        int remainingElements = list.size() % NUM_THREAD;
        log.info("re: {}",remainingElements);
        //
        int startIndex = 0;
        log.info("start: {}",startIndex);
        int endIndex = elementsPerThread + (remainingElements > 0 ? 1 : 0);
        log.info("end: {}",endIndex);
        //
        for (int i = 0; i < NUM_THREAD; i++) {
            if(i==NUM_THREAD-1) {
                endIndex--;
            }
            List<JSONObject> subArray = list.subList(startIndex,endIndex);
            Thread thread = new Thread(new ParallelFileWriter(filePath,subArray));
            thread.start();
            //
            startIndex = endIndex;
            log.info("start: {}",startIndex);
            endIndex = startIndex + elementsPerThread + (remainingElements > 0 ? 1 : 0);
            log.info("end: {}",endIndex);
            remainingElements--;
        }
    }




}