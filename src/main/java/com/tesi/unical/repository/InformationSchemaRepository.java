package com.tesi.unical.repository;

import com.tesi.unical.entity.InformationSchemaModel;
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
            "and kcu2.table_schema = :schema\n" +
            "and KCU1.COLUMN_NAME = KCU2.COLUMN_NAME",nativeQuery = true)
    List<MetaDataDTO> getChildrenMetaData(@Param("schema") String schema, @Param("table") String table);

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
            "where kcu1.table_name = :table\n" +
            "and kcu1.table_schema = :schema\n" +
            "and KCU1.COLUMN_NAME = KCU2.COLUMN_NAME",nativeQuery = true)
    List<MetaDataDTO> getParentsMetaData(@Param("schema") String schema, @Param("table") String table);

    @Query(value = "select tabs.table_name tableName, \n" +
            "\t   tabs.table_schema tableSchema,  \n" +
            "\t   tabs.table_type tableType,\n" +
            "\t   tabs.table_catalog tableCatalog\n" +
            "\t   from information_schema.tables tabs\n" +
            "\t   where tabs.table_schema = :schema\n" +
            "\t   order by tabs.table_name",nativeQuery = true)
    List<TableMetaData> getTableMetaDataBySchema(@Param(value = "schema") String schema);

    @Query(value = "select col.column_name columnName\n" +
            "\t   from information_schema.columns col\n" +
            "\t   where col.table_name = :table\n" +
            "\t   and col.table_schema = :schema",nativeQuery = true)
    List<String> getColumnNamesByTable(@Param(value = "schema") String schema, @Param(value = "table") String table);

    @Query(value = "select t.table_name\n" +
            "from information_schema.tables t\n" +
            "where t.table_schema = :schema",nativeQuery = true)
    List<String> getAllTables(@Param("schema") String schema);

    @Query(value = "SELECT T2.COLUMN_NAME\n" +
            "FROM INFORMATION_SCHEMA.Table_constraints T1\n" +
            "JOIN INFORMATION_SCHEMA.key_column_usage T2\n" +
            "ON T1.table_name = T2.table_name\n" +
            "WHERE T1.table_schema = :schema\n" +
            "and T1.table_name = :table\n" +
            "and T1.constraint_type = 'PRIMARY KEY'\n" +
            "and t2.position_in_unique_constraint is null",nativeQuery = true)
    String getPrimaryKey(@Param("schema") String schema, @Param("table") String table);

    @Query(value = "select DISTINCT B.COLUMN_NAME\n" +
            "from information_schema.Table_constraints a\n" +
            "join information_schema.key_column_usage b on a.table_name = b.table_name\n" +
            "where a.table_schema = :schema\n" +
            "and a.table_name = :table\n" +
            "and a.constraint_type = 'FOREIGN KEY'\n" +
            "AND B.POSITION_IN_UNIQUE_CONSTRAINT IS NOT NULL\n",nativeQuery = true)
    List<String> getForeignKeys(@Param("schema") String schema, @Param("table") String table);

    @Query(value = "SELECT FK.TABLE_NAME\n" +
            "FROM INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS C\n" +
            " JOIN INFORMATION_SCHEMA.TABLE_CONSTRAINTS FK ON C.CONSTRAINT_NAME = FK.CONSTRAINT_NAME\n" +
            " JOIN INFORMATION_SCHEMA.TABLE_CONSTRAINTS PK ON C.UNIQUE_CONSTRAINT_NAME = PK.CONSTRAINT_NAME\n" +
            "WHERE PK.table_name = :table\n" +
            "AND PK.table_schema = :schema",nativeQuery = true)
    List<String> getChildren(@Param("schema") String schema, @Param("table") String table);

    @Query(value = "select schema_name from information_schema.schemata", nativeQuery = true)
    List<String> getSchemas();
}