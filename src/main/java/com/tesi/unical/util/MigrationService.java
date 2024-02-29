package com.tesi.unical.util;

import com.tesi.unical.entity.dto.MetaDataDTO;
import com.tesi.unical.entity.json.MigrationInfo;
import com.tesi.unical.entity.json.RelationshipInfo;
import com.tesi.unical.service.informationSchema.InformationSchemaServiceInterface;
import com.tesi.unical.util.file.FileUtils;
import com.tesi.unical.util.file.JsonUtils;
import com.tesi.unical.util.interfaces.MigrationInterface;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;
import java.util.Date;

@Service
@Slf4j
public class MigrationService implements MigrationInterface {

    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.password}")
    private String psw;
    @Value("${spring.datasource.username}")
    private String user;

    @Autowired
    private InformationSchemaServiceInterface informationSchemaService;
    @Autowired
    private RelationshipService relationshipService;

    ////////////////////////////////////// reference ///////////////////////////////////////////////////////////////////////////////

    public boolean migrateReference(String schema, String table, Long limit, Long offset) {
        //check sulle possibili tabelle
        log.info("\n##############################\n" +
                " ######      START       #####\n" +
                " ###### {} ######\n" +
                "##############################\n",new Timestamp(new Date().getTime()));
        this.informationSchemaService.checkTable(schema,table);
        //inizializzazione variabili
        Connection connection;
        String query;
        List<JSONObject> mainTableJsonList;
        //recupero metadati
        List<String> columnNamesByTable = this.informationSchemaService.getColumnNamesByTable(schema, table);
        List<MetaDataDTO> parents = this.informationSchemaService.getParentsMetaData(schema,table);
        Map<String,List<JSONObject>> parentDocuments = new HashMap<>();
        Map<String,List<String>> parentColumns = new HashMap<>();
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
            query = QueryBuilder.offset(query,offset);
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
                    parentColumns.put(parentName,this.informationSchemaService.getColumnNamesByTable(schema,parentName));
                    List<JSONObject> fkResultSet = JsonUtils.createDocumentListByColumnName(
                            resultSet,
                            this.informationSchemaService.getColumnNamesByTable(schema,parentName)
                    );
                    parentDocuments.put(parentName,fkResultSet);
                }
            }
            connection.close();
            List<String> foreignKeys = this.informationSchemaService.getForeignKeys(schema,table);
            log.info("fk: {}",foreignKeys);
            JsonUtils.referenceJson(foreignKeys,mainTableJsonList,parentDocuments,parentColumns);
        } catch (Exception e) {
            return false;
        }
        return FileUtils.write(table,mainTableJsonList);
    }

    ///////////////////////////////////////  albero //////////////////////////////////////////////////////////////

    private List<JSONObject> migrateTreeImpl(String schema, String root, int limit) throws Exception {
        log.info("\n##############################\n" +
                " ######      START       #####\n" +
                " ###### {} ######\n" +
                "##############################\n",new Timestamp(new Date().getTime()));
        Connection connection = DriverManager.getConnection(url,user,psw);
        String query;
        Statement statement = connection.createStatement();
        ResultSet resultSet;
        Map<String,List<JSONObject>> childrenMap = new HashMap<>();
        Map<Integer,List<MigrationInfo>> migrationTree = getChildren(schema,root,new HashMap<>(),0,limit);
        if(migrationTree.isEmpty()) {
            query = QueryBuilder.selectAll(schema,root);
            resultSet = statement.executeQuery(query);
            List<String> columns = informationSchemaService.getColumnNamesByTable(schema,root);
            return JsonUtils.createDocumentListByColumnName(resultSet,columns);
        }
        int index = migrationTree.size() - 1;
        //
        for(int i=index; i>=0; i--) {
            List<MigrationInfo> migrationInfoList = migrationTree.get(i);
            for(MigrationInfo migrationInfo : migrationInfoList) {
                String parent = migrationInfo.getParent();
                String child = migrationInfo.getChild();
                //
                List<JSONObject> parentDoc = childrenMap.get(parent);
                List<JSONObject> childDoc = childrenMap.get(child);
                //
                if(Utils.isCollectionEmpty(childDoc)) { //
                    query = QueryBuilder.join(schema,parent,child,migrationInfo.getJoinKey());
                    resultSet = statement.executeQuery(query);
                    List<String> childColumns = informationSchemaService.getColumnNamesByTable(schema,child);
                    childDoc = JsonUtils.createDocumentListByColumnName(resultSet, childColumns);
                }
                if(Utils.isCollectionEmpty(parentDoc)) {
                    List<String> parentColumns = informationSchemaService.getColumnNamesByTable(schema,parent);
                    query = QueryBuilder.selectAll(schema,parent);
                    resultSet = statement.executeQuery(query);
                    parentDoc = JsonUtils.createDocumentListByColumnName(resultSet,parentColumns);
                    JsonUtils.embeddedJson(migrationInfo.getJoinKey(), parentDoc, child, childDoc);
                } else {
                    JsonUtils.embeddedJson(migrationInfo.getJoinKey(), parentDoc, child, childDoc);
                }
                childrenMap.put(parent,parentDoc);
            }
        }
        log.info("after {}",childrenMap.get(root));
        return childrenMap.get(root);
    }

    public boolean migrateTree(String schema, String table, int limit) throws Exception {
        return FileUtils.write(table,migrateTreeImpl(schema,table,limit));
    }


    ////////////////////////////////////// embedding /////////////////////////////////////////////////////////////////////////////


    public boolean migrateEmbedding(String schema, String table, Long limit, Long offset) { //embedding
        log.info("\n##############################\n" +
                " ######      START       #####\n" +
                " ###### {} ######\n" +
                "##############################\n",new Timestamp(new Date().getTime()));
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
            query = QueryBuilder.offset(query,offset);
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
            return false;
        }
        //scrive su file
        //
        //
        return FileUtils.write(table, parentDocuments);
    }

    //////////////////////////////////// COUNT ////////////////////////////////////////////////////////////////////////////////////

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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getRelationship(String schema, String table1, String table2, String pk) {
        Connection connection;
        String query;
        int left = 0, middle = 0, right = 0;
        try {
            connection = DriverManager.getConnection(url,user,psw);
            if(!Utils.isNull(pk)) {
                    query = QueryBuilder.join(schema, table1, table2, pk);
                    query = QueryBuilder.count(query);
                    log.info(query);
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(query);
                    resultSet.next();
                    middle = resultSet.getInt(1);
                    resultSet = statement.executeQuery(QueryBuilder.leftJoin(query));
                    resultSet.next();
                    left = resultSet.getInt(1);
                    resultSet = statement.executeQuery(QueryBuilder.rightJoin(query));
                    resultSet.next();
                    right = resultSet.getInt(1);
            }
            connection.close();
        } catch (Exception e ){
            return e.getMessage();
        }
        return this.relationshipService.getRelationshipType(left,middle,right);
    }

    ////////////////// GET RELATIONSHIP_TYPE ////////////////////////////////////

    ///////// GET CHILDREN RICORSIVO //////////////////////////////////////////////////////////////////////////////////

    private Map<Integer,List<MigrationInfo>> getChildren(String schema, String root, Map<Integer,List<MigrationInfo>> relationships, Integer height, Integer limit) {
        List<String> children = this.informationSchemaService.getChildren(schema,root);
        String pk = this.informationSchemaService.getPrimaryKey(schema,root);
        List<MigrationInfo> list = new ArrayList<>();
        for(String child : children) {
            MigrationInfo migrationInfo = MigrationInfo.builder()
                    .parent(root)
                    .child(child)
                    .joinKey(pk)
                    .relationType(getRelationship(schema,root,child,pk)).build();
            list.add(migrationInfo);
        }
        log.info("relationships: {}",children);
        if(height == limit || Utils.isCollectionEmpty(children))
            return relationships;
        else {
            relationships.put(height, list);
            height = height + 1;
            for(String child : children) {
                return getChildren(schema,child,relationships,height,limit);
            }
        }
        return relationships;
    }

    public Map<Integer,List<MigrationInfo>> childrenNoLoop(String schema, String root,int limit) {
        return getChildren(schema,root,new HashMap<>(),0,limit);
    }

    public Map<Integer,List<MigrationInfo>> childrenNoLoop(String schema, String root) {
        return getChildren(schema,root,new HashMap<>(),0,100);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public List<RelationshipInfo> getRelationInfo(String schema, String table, int type) {
        if(type == 1) {
            List<MetaDataDTO> children = this.informationSchemaService.getChildrenMetaData(schema, table);
            List<RelationshipInfo> res = new LinkedList<>();
            for(MetaDataDTO dto : children) {
                res.add(RelationshipInfo.builder()
                        .table(dto.getFkTableName())
                        .relationType(this.getRelationship(schema,table,dto.getFkTableName(),dto.getFkColumnName()))
                        .build()
                );
            }
            return res;
        } else if(type == 2) {
            List<MetaDataDTO> parents = this.informationSchemaService.getParentsMetaData(schema,table);
            List<RelationshipInfo> res = new LinkedList<>();
            for(MetaDataDTO dto : parents) {
                res.add(RelationshipInfo.builder()
                        .table(dto.getReferencedTableName())
                        .relationType(this.getRelationship(schema,table,dto.getReferencedTableName(),dto.getReferencedColumnName()))
                        .build()
                );
            }
            return res;
        }
        return null;
    }


}

