package com.tesi.unical.service.informationSchema;

import com.tesi.unical.entity.dto.ColumnMetaData;
import com.tesi.unical.entity.dto.MetaDataDTO;
import com.tesi.unical.entity.dto.TableMetaData;
import com.tesi.unical.repository.InformationSchemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public List<String> columnsType(String schema, String table) {
        List<String> result = new ArrayList<>();
        List<ColumnMetaData> columnMetaData = this.getColumnMetaDataByTable(schema, table);
        for(ColumnMetaData dto : columnMetaData) {
            result.add(dto.getDataType());
        }
        return result;
    }

    public void checkTable(String schema, String table) {
        List<String> tablesList = this.getAllTables(schema);
        if(!tablesList.contains(table))
            throw new RuntimeException("La tabella non esiste");
    }
}
