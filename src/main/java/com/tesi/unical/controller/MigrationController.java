package com.tesi.unical.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tesi.unical.entity.json.MigrationBody;
import com.tesi.unical.service.informationSchema.InformationSchemaService;
import com.tesi.unical.util.MigrationService;
import com.tesi.unical.util.Utils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/migration")
@Slf4j
public class MigrationController {

    @Autowired
    private InformationSchemaService informationSchemaService;

    @Autowired
    private MigrationService migrationService;

    @PostMapping()
    public ResponseEntity<?> migrationPost(@RequestBody MigrationBody body) {
        try {
            if (Utils.isNull(body)) {
                return ResponseEntity.badRequest().body("body empty");
            }
            log.info("migration: {}", body.getParam());
            if (Utils.isNull(body.getLimit()) || body.getLimit().equals(0L)) {
                return ResponseEntity.badRequest().body("400");
            }
            if (body.getParam().equals(1L)) {
                log.info("embedding");
                return ResponseEntity.ok(this.migrationService.migrateEmbedding(
                        body.getSchema(),
                        body.getTable(),
                        body.getLimit(),
                        body.getOffset())
                );
            } else if (body.getParam().equals(2L)) {
                log.info("referencing");
                return ResponseEntity.ok(this.migrationService.migrateReference(
                        body.getSchema(),
                        body.getTable(),
                        body.getLimit(),
                        body.getOffset())
                );
            } else
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
        } else if (param.equals(2L)) {
            try {
                return ResponseEntity.ok(new Count(this.migrationService.countReference(schema, table)));
            } catch (Exception e) {
                return ResponseEntity.ok(e.getMessage());
            }
        } else
            return ResponseEntity.badRequest().body("404");
    }

    /* test embedding usando la where condition invece che join (piu lento, json leggermente rotto)*/
    @GetMapping("embed")
    public ResponseEntity<?> tst() {
        try {
            return ResponseEntity.ok(this.migrationService.migration("migration", "customers"));
        } catch (Exception e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @GetMapping("/test")
    public ResponseEntity test(@RequestParam("limit") Integer limit) {
        try {
            return ResponseEntity.ok(migrationService.migrateEmbeddingRecursive("migration","customers",10000L,0L,10));
        } catch (Exception e) {
            return ResponseEntity.ok(e);
        }
    }

    @GetMapping("/test-loop")
    public ResponseEntity testLoop(@RequestParam("limit") Integer limit) {
        try {
            return ResponseEntity.ok(migrationService.childrenNoLoop("migration","customers",limit));
        } catch (Exception e) {
            return ResponseEntity.ok(e);
        }
    }

    @GetMapping("/test-relation")
    public ResponseEntity testRel(@RequestParam("schema") String schema, @RequestParam("table1") String table1, @RequestParam("table2") String table2, @RequestParam("pk") String pk) {
        try {
            return ResponseEntity.ok(migrationService.getRelationship(schema,table1,table2,pk));
        } catch (Exception e) {
            return ResponseEntity.ok(e);
        }
    }


}