package com.tesi.unical.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tesi.unical.entity.dto.ColumnMetaData;
import com.tesi.unical.entity.dto.MetaDataDTO;
import com.tesi.unical.service.informationSchema.InformationSchemaService;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private RelationshipCheck relationshipCheck;

    //conosco i tipi delle colonne grazie a ColumnMetaData (andrebbero mappati per castati correttamente)
    private List<String> columnsType(String schema, String table) {
        List<String> result = new ArrayList<>();
        List<ColumnMetaData> columnMetaData = this.informationSchemaService.getColumnMetaDataByTable(schema, table);
        for(ColumnMetaData dto : columnMetaData) {
            result.add(dto.getDataType());
        }
        return result;
    }

    //possibile check per evitare di passare tabelle che non esistono o per evitare problemi di sicurezza (sql injection?)
    public String migrate(String schema, String table) { //embedding
        Connection connection;
        PreparedStatement preparedStatement;
        String query;
        File file;
        try {
            connection = DriverManager.getConnection(url,user,psw);
            List<ColumnMetaData> columnMetaData = this.informationSchemaService.getColumnMetaDataByTable(schema, table);
            List<MetaDataDTO> metaDataDTOList = this.informationSchemaService.getDBMetaData(schema,table);
            log.info("cmetadata: {}",columnMetaData.isEmpty());
            query = QueryBuilder.selectAll(schema,table,columnMetaData);
            log.info(query);
            FileWriter writer = new FileWriter("C:\\Users\\Giuseppe\\OneDrive\\Desktop\\user.txt");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            //creare una lista di json e scrivere nel file dopo il ciclo per rimanere nell'ordine n^2
            //
            //
            List<JSONObject> jsonList = fillJsonList(resultSet,columnMetaData);
            //
            //
            //creare le liste delle tabelle collegate se serve
            query = QueryBuilder.join(schema,table,"orders", metaDataDTOList.get(0));
            resultSet = statement.executeQuery(query);
            log.info(query);
            List<ColumnMetaData> secondaTabella = this.informationSchemaService.getColumnMetaDataByTable(schema,"orders");
            columnMetaData.addAll(secondaTabella);
            while(resultSet.next()) {
                Object o;
                JSONObject json = new JSONObject();
                for(int i=0; i<columnMetaData.size(); i++) {
                    o = resultSet.getObject(i+1);
                    json.put(columnMetaData.get(i).getColumnName(),o);
                }
                if(!resultSet.isLast())
                    writer.append(json.toString() + ",\n");
                else
                    writer.append(json.toString());
            }
            writer.close();
        } catch (Exception e) {
            return e.getMessage();
        }
        return "ok";
    }

    private List<JSONObject> fillJsonList(ResultSet resultSet, List<ColumnMetaData> columnMetaDataList) {
        List<JSONObject> result = new ArrayList<>();
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



    //create json (passo il result set e il json da creare)
    //
    private JSONObject embeddingJson(ResultSet resultSet) { //crea l'array di json correlati alla tabella
        return null;
    }

    private JSONObject referenceJson(ResultSet resultSet) { return null; }

    //switch con i tipi più comuni

    //scrive sul file



}