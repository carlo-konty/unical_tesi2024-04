package com.tesi.unical.service.informationSchema;

import com.tesi.unical.entity.dto.ColumnMetaData;
import com.tesi.unical.entity.dto.MetaDataDTO;
import com.tesi.unical.entity.dto.TableMetaData;
import com.tesi.unical.repository.InformationSchemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InformationSchemaService implements InformationSchemaServiceInterface {

    @Autowired
    private InformationSchemaRepository informationSchemaRepository;

    public List<MetaDataDTO> getDBMetaData(String schema, String table) {
        return this.informationSchemaRepository.getDbMetaData(schema,table);
    }

    public List<ColumnMetaData> getColumnMetaDataByTable(String schema, String table) {
        return this.informationSchemaRepository.getColumnMetaDataByTable(schema,table);
    }

    public List<TableMetaData> getTableMetaDataBySchema(String schema) {
        return this.informationSchemaRepository.getTableMetaDataBySchema(schema);
    }

    public List<String> getAllTables(String schema) {
        return this.informationSchemaRepository.getAllTables(schema);
    }

    public String getPrimaryKey(String schema, String table) {
        return this.informationSchemaRepository.getPrimaryKey(schema,table);
    }
}
