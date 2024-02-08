package com.tesi.unical.service.informationSchema;

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

    public List<String> getSchemas() {
        return this.informationSchemaRepository.getSchemas();
    }

    public List<MetaDataDTO> getChildrenMetaData(String schema, String table) {
        return this.informationSchemaRepository.getChildrenMetaData(schema,table);
    }

    public List<MetaDataDTO> getParentsMetaData(String schema, String table) {
        return this.informationSchemaRepository.getParentsMetaData(schema,table);
    }

    public List<String> getColumnNamesByTable(String schema, String table) {
        return this.informationSchemaRepository.getColumnNamesByTable(schema,table);
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

    public List<String> getForeignKeys(String schema, String table) {
        return this.informationSchemaRepository.getForeignKeys(schema,table);
    }

    public List<String> getChildren(String schema, String table) {
        return this.informationSchemaRepository.getChildren(schema,table);
    }

    public void checkTable(String schema, String table) {
        List<String> tablesList = this.getAllTables(schema);
        if(!tablesList.contains(table))
            throw new RuntimeException("La tabella non esiste");
    }
}
