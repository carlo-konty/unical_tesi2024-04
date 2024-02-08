package com.tesi.unical.service.informationSchema;

import com.tesi.unical.entity.dto.MetaDataDTO;
import com.tesi.unical.entity.dto.TableMetaData;

import java.util.List;

public interface InformationSchemaServiceInterface {

    List<MetaDataDTO> getChildrenMetaData(String schema, String table);
    List<MetaDataDTO> getParentsMetaData(String schema, String table);
    List<String> getColumnNamesByTable(String schema, String table);
    List<TableMetaData> getTableMetaDataBySchema(String schema);
    List<String> getAllTables(String schema);
    List<String> getSchemas();
    String getPrimaryKey(String schema, String table);
    List<String> getForeignKeys(String schema, String table);
    List<String> getChildren(String schema, String table);
    void checkTable(String schema, String table);

}
