package com.tesi.unical.controller;

import com.tesi.unical.service.informationSchema.InformationSchemaService;
import com.tesi.unical.util.MigrationService;
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
    public ResponseEntity<?> migration(@RequestParam("schema") String schema, @RequestParam("table") String table, @RequestParam("param") Long param) {
        try {
            log.info("migration: {}",param);
            if (param.equals(1L)) {
                log.info("embedding");
                return ResponseEntity.ok(this.migrationService.migrateEmbedding(schema,table));
            }
            else if (param.equals(2L)){
                log.info("referencing");
                return ResponseEntity.ok(this.migrationService.migrateReference(schema,table));
            }
            else
                return ResponseEntity.ok("Wrong code");
        } catch (Exception e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @GetMapping("/embedding")
    public ResponseEntity<String> embeddingMigration(@RequestParam("schema") String schema, @RequestParam("table") String table) {
        try {
            return ResponseEntity.ok(this.migrationService.migrateEmbedding(schema, table));
        } catch (Exception e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @GetMapping("/reference")
    public ResponseEntity<String> referenceMigration(@RequestParam("schema") String schema, @RequestParam("table") String table) {
        try {
            return ResponseEntity.ok(this.migrationService.migrateReference(schema, table));
        } catch (Exception e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @GetMapping("/thread")
    public ResponseEntity<String> thread() {
        try {
            return ResponseEntity.ok(this.migrationService.testThreadResultSet("migration","customers"));
        } catch (Exception e) {
            return ResponseEntity.ok(e.getMessage());
        }
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