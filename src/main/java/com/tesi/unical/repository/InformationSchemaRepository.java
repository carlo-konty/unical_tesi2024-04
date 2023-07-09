package com.tesi.unical.repository;

import com.tesi.unical.entity.InformationSchemaModel;
import com.tesi.unical.entity.dto.ColumnMetaData;
import com.tesi.unical.entity.dto.MetaDataDTO;
import com.tesi.unical.entity.dto.TableMetaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InformationSchemaRepository extends JpaRepository<InformationSchemaModel,String> {

    @Query(value = "SELECT\n" +
            "     KCU1.CONSTRAINT_NAME AS fkConstraintName\n" +
            "     ,KCU1.TABLE_NAME AS fkTableName\n" +
            "     ,KCU1.COLUMN_NAME AS fkColumnName\n" +
            "     ,KCU2.CONSTRAINT_SCHEMA AS referencedConstraintSchema\n" +
            "     ,KCU2.CONSTRAINT_NAME AS referencedConstraintName\n" +
            "     ,KCU2.TABLE_SCHEMA AS referencedTableSchema\n" +
            "     ,KCU2.TABLE_NAME AS referencedTableName\n" +
            "     ,KCU2.COLUMN_NAME AS referencedColumnName\n" +
            "FROM INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS AS RC\n" +
            "\n" +
            "         INNER JOIN INFORMATION_SCHEMA.KEY_COLUMN_USAGE AS KCU1\n" +
            "                    ON KCU1.CONSTRAINT_CATALOG = RC.CONSTRAINT_CATALOG\n" +
            "                        AND KCU1.CONSTRAINT_SCHEMA = RC.CONSTRAINT_SCHEMA\n" +
            "                        AND KCU1.CONSTRAINT_NAME = RC.CONSTRAINT_NAME\n" +
            "\n" +
            "         INNER JOIN INFORMATION_SCHEMA.KEY_COLUMN_USAGE AS KCU2\n" +
            "                    ON KCU2.CONSTRAINT_CATALOG = RC.UNIQUE_CONSTRAINT_CATALOG\n" +
            "                        AND KCU2.CONSTRAINT_SCHEMA = RC.UNIQUE_CONSTRAINT_SCHEMA\n" +
            "                        AND KCU2.CONSTRAINT_NAME = RC.UNIQUE_CONSTRAINT_NAME\n" +
            "                        AND KCU2.ORDINAL_POSITION = KCU1.ORDINAL_POSITION\n" +
            "where kcu2.table_name = :table\n" +
            "and kcu2.table_schema = :schema",nativeQuery = true)
    List<MetaDataDTO> getDbMetaData(@Param("schema") String schema, @Param("table") String table);

    @Query(value = "select tabs.table_name tableName, \n" +
            "\t   tabs.table_schema tableSchema,  \n" +
            "\t   tabs.table_type tableType,\n" +
            "\t   tabs.table_catalog tableCatalog\n" +
            "\t   from information_schema.\"tables\" tabs\n" +
            "\t   where tabs.table_schema = :schema",nativeQuery = true)
    List<TableMetaData> getTableMetaDataBySchema(@Param(value = "schema") String schema);

    @Query(value = "select col.column_name columnName,\n" +
            "\t   col.table_name tableName,\n" +
            "\t   col.table_schema tableSchema,\n" +
            "\t   col.character_maximum_length charMaxLength,\n" +
            "\t   col.data_type dataType\n" +
            "\t   from information_schema.\"columns\" col\n" +
            "\t   where col.table_name = :table\n" +
            "\t   and col.table_schema = :schema",nativeQuery = true)
    List<ColumnMetaData> getColumnMetaDataByTable(@Param(value = "schema") String schema, @Param(value = "table") String table);

    @Query(value = "select t.table_name\n" +
            "from information_schema.\"tables\" t\n" +
            "where t.table_schema = :schema",nativeQuery = true)
    List<String> getAllTables(@Param("schema") String schema);
}