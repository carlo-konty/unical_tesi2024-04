package com.tesi.unical.util.interfaces;

import com.tesi.unical.entity.dto.MetaDataDTO;

import java.util.List;

public interface QueryBuilderInterface {

    String selectAll(String schema, String table);

    String join(String table1, String table2, List<MetaDataDTO> list);
}
