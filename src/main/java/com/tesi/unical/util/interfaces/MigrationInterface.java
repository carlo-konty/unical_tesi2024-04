package com.tesi.unical.util.interfaces;

public interface MigrationInterface {

    String migrateReference(String schema, String table, Long limit, Long offset);

    String migrateEmbedding(String schema, String table, Long limit, Long offset);

    String countEmbedding(String schema, String table);

    String countReference(String schema, String table);

}
