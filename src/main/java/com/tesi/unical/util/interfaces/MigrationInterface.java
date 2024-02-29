package com.tesi.unical.util.interfaces;

public interface MigrationInterface {

    boolean migrateReference(String schema, String table, Long limit, Long offset) throws Exception;

    boolean migrateEmbedding(String schema, String table, Long limit, Long offset) throws Exception;

    boolean migrateTree(String schema, String table, int limit) throws Exception;
}
