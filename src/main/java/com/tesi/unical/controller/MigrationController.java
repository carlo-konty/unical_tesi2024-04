package com.tesi.unical.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tesi.unical.service.informationSchema.InformationSchemaService;
import com.tesi.unical.util.MigrationService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/migration")
@Slf4j
public class MigrationController {

    @Autowired
    private InformationSchemaService informationSchemaService;

    @Autowired
    private MigrationService migrationService;

    @GetMapping()
    public ResponseEntity<?> migration(@RequestParam("schema") String schema, @RequestParam("table") String table, @RequestParam("param") Long param, @RequestParam("limit") Long limit) {
        try {
            log.info("migration: {}",param);
            if(limit==null || limit.equals(0L)) {
                return ResponseEntity.badRequest().body("400");
            }
            if (param.equals(1L)) {
                log.info("embedding");
                return ResponseEntity.ok(this.migrationService.migrateEmbedding(schema,table,limit));
            }
            else if (param.equals(2L)){
                log.info("referencing");
                return ResponseEntity.ok(this.migrationService.migrateReference(schema,table,limit));
            }
            else
                return ResponseEntity.badRequest().body("400");
        } catch (Exception e) {
            return ResponseEntity.ok(e.getMessage());
        }
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

    /* test embedding usando la where condition invece che join (piu lento, json leggermente rotto)*/
    @GetMapping("embed")
    public ResponseEntity<?> tst() {
        try {
            return ResponseEntity.ok(this.migrationService.migration("migration","customers"));
        } catch (Exception e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }
}