package com.tesi.unical.entity.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MigrationBody {

    @JsonProperty("table")
    private String table;

    @JsonProperty("schema")
    private String schema;

    @JsonProperty("param")
    private Long param;

    @JsonProperty("limit")
    private Long limit;

    @JsonProperty("offset")
    private Long offset;
}
