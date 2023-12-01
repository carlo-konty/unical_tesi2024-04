package com.tesi.unical.util.interfaces;

public interface MigrationInterface {

    String migrateReference(String schema, String table, Long limit);

    String migrateEmbedding(String schema, String table, Long limit);

    String countEmbedding(String schema, String table);

    String countReference(String schema, String table);

}
