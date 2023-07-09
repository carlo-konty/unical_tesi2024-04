package com.tesi.unical.util;

import com.tesi.unical.entity.dto.MetaDataDTO;
import com.tesi.unical.service.informationSchema.InformationSchemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RelationshipCheck {

    @Autowired
    private InformationSchemaService informationSchemaService;

    private String schema = "migration";

    public List<String> oneToMany(String table) {
        List<MetaDataDTO> metaDataList = this.informationSchemaService.getDBMetaData(schema,table);
        List<String> oneToMany = new ArrayList<>();
        for(MetaDataDTO metaDataDTO : metaDataList) {
            oneToMany.add(metaDataDTO.getFkTableName());
        }
        return oneToMany;
    }





}