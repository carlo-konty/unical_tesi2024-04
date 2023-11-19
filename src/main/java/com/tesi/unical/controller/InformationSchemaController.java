package com.tesi.unical.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tesi.unical.service.informationSchema.InformationSchemaServiceInterface;
import com.tesi.unical.util.MigrationService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RestController
@RequestMapping("information-schema")
public class InformationSchemaController {

    @Autowired
    private InformationSchemaServiceInterface informationSchemaService;

    @Autowired
    private MigrationService migrationService;

    @GetMapping("/schemas")
    public ResponseEntity getSchemas() {
        return ResponseEntity.ok(this.informationSchemaService.getSchemas());
    }

    @GetMapping("/tables")
    public ResponseEntity getTableMetaData(@RequestParam("schema") String schema) {
        return ResponseEntity.ok(this.informationSchemaService.getTableMetaDataBySchema(schema));
    }

    @GetMapping("/tables/all")
    public ResponseEntity getAllTables(@RequestParam("schema") String schema) {
        return ResponseEntity.ok(this.informationSchemaService.getAllTables(schema));
    }

    @GetMapping("/columns")
    public ResponseEntity getColumnsMetaData(@RequestParam("schema") String schema, @RequestParam("table") String table) {
        return ResponseEntity.ok(this.informationSchemaService.getColumnMetaDataByTable(schema,table));
    }

    @GetMapping("/relationship")
    public ResponseEntity getDbMetaData(@RequestParam("schema") String schema, @RequestParam("table") String table) {
        return ResponseEntity.ok(this.informationSchemaService.getDBMetaData(schema, table));
    }

    @GetMapping("referential-constraints")
    public ResponseEntity getReferentialConstraintsByTable(@RequestParam("schema") String schema, @RequestParam("table") String table) {
        return ResponseEntity.ok(this.informationSchemaService.getReferentialConstraintsByTable(schema,table));
    }

    @GetMapping("/keys")
    public ResponseEntity getPrimaryKey(@RequestParam("schema") String schema, @RequestParam("table") String table) {
        return ResponseEntity.ok(this.informationSchemaService.getPrimaryKey(schema, table));
    }

    @GetMapping("/count")
    public ResponseEntity<?> count(@RequestParam("schema") String schema, @RequestParam("table") String table, @RequestParam("param") Long param) {
         @Data
         class Count {
            @JsonProperty("count")
            private String count;
            public Count(String count) {
                this.count = count;
            }
        }
        if (param.equals(1L)) {
            try {
                return ResponseEntity.ok(new Count(this.migrationService.countEmbedding(schema, table)));
            } catch (Exception e) {
                return ResponseEntity.ok(e.getMessage());
            }
        } else if(param.equals(2L)) {
            try {
                return ResponseEntity.ok(new Count(this.migrationService.countReference(schema, table)));
            } catch (Exception e) {
                return ResponseEntity.ok(e.getMessage());
            }
        }
        else
            return ResponseEntity.badRequest().body("404");
    }

}