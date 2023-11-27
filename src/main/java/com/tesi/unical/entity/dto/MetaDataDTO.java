package com.tesi.unical.entity.dto;

public interface MetaDataDTO {

    String getFkTableName();

    String getFkColumnName();

    String getReferencedTableSchema();

    String getReferencedTableName();

    String getReferencedColumnName();

}
