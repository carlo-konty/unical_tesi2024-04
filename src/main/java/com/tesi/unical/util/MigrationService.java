package com.tesi.unical.util;

import com.tesi.unical.entity.dto.MetaDataDTO;
import com.tesi.unical.service.informationSchema.InformationSchemaServiceInterface;
import com.tesi.unical.util.file.FileUtils;
import com.tesi.unical.util.file.JsonUtils;
import com.tesi.unical.util.file.ParallelFileWriter;
import com.tesi.unical.util.file.ParallelJsonBuilder;
import com.tesi.unical.util.interfaces.MigrationInterface;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class MigrationService implements MigrationInterface {

    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.password}")
    private String psw;
    @Value("${spring.datasource.username}")
    private String user;

    private final int NUM_THREAD = 16;

    @Autowired
    private InformationSchemaServiceInterface informationSchemaService;


    /// START TEST ///
    public boolean migration(String schema, String table) {
        try {
            List<JSONObject> migration = this.testEmbedding(schema,table);
            return this.write(migration,table);
        } catch (Exception e) {
            log.error("error");
            return false;
        }
    }

    private boolean write(List<JSONObject> migration,String table) {
        if(!Utils.isCollectionEmpty(migration)) {
            try {
                return FileUtils.write(table,migration);
            } catch (Exception e) {
                log.error("write error");
            }
        }
        log.error("nothing to migrate");
        return false;
    }

    private List<JSONObject> testEmbedding(String schema, String table) {
        //check sulle possibili tabelle
        this.informationSchemaService.checkTable(schema,table);
        //inizializzazione variabili
        Connection connection;
        String query;
        List<JSONObject> mainTableJsonList;
        //recupero metadati
        List<String> columnMetaData = this.informationSchemaService.getColumnNamesByTable(schema, table);
        List<MetaDataDTO> metaDataDTOList = this.informationSchemaService.getChildrenMetaData(schema,table);
        try {
            //get connection
            connection = DriverManager.getConnection(url, user, psw);
            query = QueryBuilder.selectAll(schema, table);
            log.info(query);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            //creare una lista di json e scrivere nel file
            //
            //
            mainTableJsonList = JsonUtils.createDocumentListByColumnName(resultSet, columnMetaData);
            //
            //
            connection.close();
            //
            //embed
            for(MetaDataDTO metaDataDTO : metaDataDTOList) {
                for(JSONObject json : mainTableJsonList) {
                    this.embed(json,metaDataDTO);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
        return mainTableJsonList;
    }

    private JSONObject embed(JSONObject json,MetaDataDTO metaDataDTO) {
        Connection connection;
        String query;
        List<JSONObject> mainTableJsonList;
        //recupero metadati
        List<String> columnMetaData = this.informationSchemaService.getColumnNamesByTable(metaDataDTO.getReferencedTableSchema(),metaDataDTO.getFkTableName());
        try {
            //get connection
            connection = DriverManager.getConnection(url, user, psw);
            query = QueryBuilder.selectAll(metaDataDTO.getReferencedTableSchema(),metaDataDTO.getFkTableName());
            query = QueryBuilder.where(query);
            query = QueryBuilder.and(query,metaDataDTO.getFkColumnName(),String.valueOf(json.getInt(metaDataDTO.getReferencedColumnName())));
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            //creare una lista di json e scrivere nel file
            //
            //
            mainTableJsonList = JsonUtils.createDocumentListByColumnName(resultSet, columnMetaData);
            //
            //
            if(!Utils.isCollectionEmpty(mainTableJsonList))
                json.append(metaDataDTO.getFkTableName(),mainTableJsonList);
            connection.close();
        } catch (Exception e) {
            log.error("error");
        }
        return json;
    }

    /// END TEST///



    /*questa metodologia prevede che ogni documento sia pari alla tabella, le relazioni sono modellate utilizzando
    il riferimento all'id del bson presente in mongo db
    */
    //todo sarebbe necessario, dopo la migrazione, costruire i riferimenti corretti tramite chiave primaria
    // per ogni riga estratta bisogna trovare le relazioni esterne
    public String migrateReference(String schema, String table, Long limit) {
        //check sulle possibili tabelle
        this.informationSchemaService.checkTable(schema,table);
        //inizializzazione variabili
        Connection connection;
        String query;
        List<JSONObject> mainTableJsonList;
        //recupero metadati
        List<String> columnNamesByTable = this.informationSchemaService.getColumnNamesByTable(schema, table);
        List<MetaDataDTO> parents = this.informationSchemaService.getParentsMetaData(schema,table);
        Map<String,List<JSONObject>> parentDocuments = new HashMap<>();
        try {
            //get connection
            connection = DriverManager.getConnection(url, user, psw);
            query = QueryBuilder.selectAll(schema, table);
            if(!Utils.isNull(limit)) {
                query = QueryBuilder.limit(query, limit);
            }
            else {
                query = QueryBuilder.limit(query, 10000L);
            }
            log.info(query);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            //creare una lista di json e scrivere nel file
            //
            //
            mainTableJsonList = JsonUtils.createDocumentListByColumnName(resultSet, columnNamesByTable);
            //
            //
            if(!Utils.isCollectionEmpty(parents)) {
                for(MetaDataDTO parent : parents) {
                    query = QueryBuilder.join2TableReference(schema,table,parent);
                    resultSet = statement.executeQuery(query);
                    log.info(query);
                    String parentName = parent.getReferencedTableName();
                    List<JSONObject> fkResultSet = JsonUtils.createDocumentListByColumnName(
                            resultSet,
                            this.informationSchemaService.getColumnNamesByTable(schema,parentName)
                    );
                    parentDocuments.put(parentName,fkResultSet);
                }
            }
            connection.close();
            List<String> foreignKeys = this.informationSchemaService.getForeignKeys(schema,table);
            log.info("{}",foreignKeys);
            JsonUtils.referenceJson(foreignKeys,mainTableJsonList,parentDocuments);
        } catch (Exception e) {
            return e.getMessage();
        }
        if(FileUtils.write(table,mainTableJsonList)) {
            return "OK";
        }
        return "KO";
    }

    /*
    il servizio serve per migrare secondo la metodologia embedding, cioè inseriamo tutte le sottorelazioni come array di
    json all'interno del documento principale che stiamo creando (viene creato un file con il nome della tabella)
     */
    //todo valutare se è possibile migrare direttamente all'interno di mongo db
    public String migrateEmbedding(String schema, String table, Long limit) { //embedding
        //check esistenza tabelle
        this.informationSchemaService.checkTable(schema,table);
        //inizializzazione variabili
        Connection connection;
        String query;
        List<JSONObject> parentDocuments;
        Map<String,List<JSONObject>> childrenDocuments = new HashMap<>();
        //recupero metadati
        List<String> metaDataColumns = this.informationSchemaService.getColumnNamesByTable(schema, table);
        List<MetaDataDTO> childrenMetaData = this.informationSchemaService.getChildrenMetaData(schema,table);
        String parentPrimaryKey = this.informationSchemaService.getPrimaryKey(schema,table);
        try {
            //get connection
            connection = DriverManager.getConnection(url,user,psw);
            query = QueryBuilder.selectAll(schema,table);
            if(!Utils.isNull(limit)) {
                query = QueryBuilder.limit(query, limit);
            }
            else {
                query = QueryBuilder.limit(query, 10000L);
            }
            log.info(query);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
             //
            //
            parentDocuments = JsonUtils.createDocumentListByColumnName(resultSet,metaDataColumns);
            //
            //
            //creare le liste delle tabelle figlie se serve
            if(!Utils.isCollectionEmpty(childrenMetaData)) {
                log.info("String metadata size: {}",childrenMetaData.size());
                for(MetaDataDTO child : childrenMetaData) {
                    query = QueryBuilder.join2TablesEmbedding(schema,table,child);
                    resultSet = statement.executeQuery(query);
                    log.info(query);
                    String childrenTableName = child.getFkTableName();
                    List<JSONObject> fkResultSet = JsonUtils.createDocumentListByColumnName(
                            resultSet,
                            this.informationSchemaService.getColumnNamesByTable(schema,child.getFkTableName())
                    );
                    log.info("childrenTableName: {}",childrenTableName);
                    log.info("fkResultSet size: {}",fkResultSet.size());
                    childrenDocuments.put(childrenTableName,fkResultSet);
                }
            }
            connection.close();
            //crea se serve il json finale
            //
            JsonUtils.embeddedJson(parentPrimaryKey, parentDocuments,childrenDocuments);
        } catch (Exception e) {
            return e.getMessage();
        }
        //scrive su file
        //
        //
        return FileUtils.write(table, parentDocuments).toString();
    }

    public String countEmbedding(String schema, String table) {
        Connection connection;
        String query;
        List<MetaDataDTO> metaDataDTOList = this.informationSchemaService.getChildrenMetaData(schema,table);
        Integer count = 0;
        try {
            connection = DriverManager.getConnection(url,user,psw);
            if(!Utils.isCollectionEmpty(metaDataDTOList)) {
                for (MetaDataDTO dto : metaDataDTOList) {
                    query = QueryBuilder.join2TablesEmbedding(schema, table, dto);
                    query = QueryBuilder.count(query);
                    log.info(query);
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(query);
                    resultSet.next();
                    count = count + resultSet.getInt(1);
                }
            }
            else {
                query = QueryBuilder.countAll(schema,table);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                resultSet.next(); count = resultSet.getInt(1);
            }
            connection.close();
        } catch (Exception e ){
            return e.getMessage();
        }
        return count.toString();
    }

    public String countReference(String schema, String table) {
        Connection connection;
        String query;
        List<MetaDataDTO> metaDataDTOList = this.informationSchemaService.getParentsMetaData(schema,table);
        Integer count = 0;
        try {
            connection = DriverManager.getConnection(url,user,psw);
            if(!Utils.isCollectionEmpty(metaDataDTOList)) {
                for (MetaDataDTO dto : metaDataDTOList) {
                    query = QueryBuilder.join2TableReference(schema, table, dto);
                    query = QueryBuilder.count(query);
                    log.info(query);
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(query);
                    resultSet.next();
                    count = count + resultSet.getInt(1);
                }
            }
            else {
                query = QueryBuilder.countAll(schema,table);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                resultSet.next(); count = resultSet.getInt(1);
            }
            connection.close();
        } catch (Exception e ){
            return e.getMessage();
        }
        return count.toString();
    }

    //  test run parallele  //


    //usare thread per eseguire in parallelo diverse estrazioni dal db
    public String testThreadResultSet(String schema, String table) {
        //check sulle possibili tabelle
        this.informationSchemaService.checkTable(schema,table);
        //inizializzazione variabili
        Connection connection;
        String query;
        List<JSONObject> mainTableJsonList;
        //recupero metadati
        List<String> columnMetaData = this.informationSchemaService.getColumnNamesByTable(schema, table);
        try {
            //get connection
            connection = DriverManager.getConnection(url, user, psw);
            query = QueryBuilder.selectAll(schema, table);
            //count query
            String countQuery = QueryBuilder.count(query);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(countQuery);
            //
            int count =  JsonUtils.getCount(resultSet);
            int rowPerThread = count / NUM_THREAD;
            int remainsRow = count % NUM_THREAD;
            //
            //
            ConcurrentHashMap<Long,List<JSONObject>> map = new ConcurrentHashMap<>();
            int limit = rowPerThread + (remainsRow > 0 ? 1 : 0);
            int offset = 0;
            //partenza thread
            for(Long i = 1L; i<=NUM_THREAD; i++) {
              //  String subQuery = QueryBuilder.limit(query,limit,offset);
                Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery("");
                Thread thread = new Thread(new ParallelJsonBuilder(i,rs,map,columnMetaData));
                thread.start();
                //
                offset = offset + limit;
            }
            //
            //
            //write file
            FileUtils.write(table,map);
        } catch (Exception e) {
            return e.getMessage();
        }
        return "OK";
    }

    //funziona ma non bisogna scrivere con più thread sullo stesso file altrimenti casino
    public String testThread(String schema, String table) {
        //check sulle possibili tabelle
        this.informationSchemaService.checkTable(schema,table);
        //inizializzazione variabili
        Connection connection;
        String query;
        List<JSONObject> mainTableJsonList;
        //recupero metadati
        List<String> columnMetaData = this.informationSchemaService.getColumnNamesByTable(schema, table);
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
            mainTableJsonList = JsonUtils.createDocumentListByColumnName(resultSet, columnMetaData);
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