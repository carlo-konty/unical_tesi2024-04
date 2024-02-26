package com.tesi.unical.entity.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Builder
public class RelationshipInfo {

    @JsonProperty(value = "table")
    private String table;
    @JsonProperty(value = "relationType")
    private String relationType;
}
