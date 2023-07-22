package com.tesi.unical.controller;

import com.tesi.unical.service.informationSchema.InformationSchemaService;
import com.tesi.unical.util.MigrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private InformationSchemaService informationSchemaService;

    @Autowired
    private MigrationService migrationService;

    @GetMapping("/embedding")
    public ResponseEntity embeddingMigration(@RequestParam("schema") String schema, @RequestParam("table") String table) {
        try {
            return ResponseEntity.ok(this.migrationService.migrateEmbedding(schema, table));
        } catch (Exception e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @GetMapping("/reference")
    public ResponseEntity referenceMigration(@RequestParam("schema") String schema, @RequestParam("table") String table) {
        try {
            return ResponseEntity.ok(this.migrationService.migrateReference(schema, table));
        } catch (Exception e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @GetMapping("/count")
    public ResponseEntity count() {
        try {
            return ResponseEntity.ok(this.migrationService.testCount());
        } catch (Exception e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @GetMapping("/fetch")
    public ResponseEntity fetch() {
        try {
            return ResponseEntity.ok(this.migrationService.testFetch());
        } catch (Exception e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }
}