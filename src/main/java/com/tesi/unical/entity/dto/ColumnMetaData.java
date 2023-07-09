package com.tesi.unical.entity.dto;

public interface ColumnMetaData {

    String getColumnName();

    String getTableName();

    String getTableSchema();

    String getCharMaxLength();

    String getDataType();

}