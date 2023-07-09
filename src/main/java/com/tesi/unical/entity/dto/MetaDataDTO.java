package com.tesi.unical.entity.dto;

public interface MetaDataDTO {

    String getFkConstraintName();

    String getFkTableName();

    String getFkColumnName();

    String getReferencedConstraintSchema();

    String getReferencedConstraintName();

    String getReferencedTableSchema();

    String getReferencedTableName();

    String getReferencedColumnName();

}
