package com.tesi.unical.util.interfaces;

import com.tesi.unical.entity.dto.ColumnMetaData;
import com.tesi.unical.entity.dto.MetaDataDTO;

import java.util.List;

public interface QueryBuilderInterface {

    String selectAll(String schema, String table, List<ColumnMetaData> columns);

    String join(String schema, String table, MetaDataDTO list);
}
