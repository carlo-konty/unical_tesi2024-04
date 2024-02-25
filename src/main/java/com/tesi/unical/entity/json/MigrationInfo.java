package com.tesi.unical.entity.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MigrationInfo {

    @JsonProperty(value = "parent")
    private String parent;
    @JsonProperty(value = "tableName")
    private String child;
    @JsonProperty(value = "joinKey")
    private String joinKey;
    @JsonProperty(value = "relationType")
    private String relationType;

}
