package com.tesi.unical.service.informationSchema;

import com.tesi.unical.entity.dto.ColumnMetaData;
import com.tesi.unical.entity.dto.MetaDataDTO;
import com.tesi.unical.entity.dto.TableMetaData;

import java.util.List;

public interface InformationSchemaServiceInterface {

    List<MetaDataDTO> getChildrenMetaData(String schema, String table);
    List<MetaDataDTO> getParentsMetaData(String schema, String table);
    List<ColumnMetaData> getColumnMetaDataByTable(String schema, String table);
    List<TableMetaData> getTableMetaDataBySchema(String schema);
    List<String> getAllTables(String schema);
    List<String> getSchemas();
    List<String> columnsType(String schema, String table);
    String getPrimaryKey(String schema, String table);
    void checkTable(String schema, String table);

}
