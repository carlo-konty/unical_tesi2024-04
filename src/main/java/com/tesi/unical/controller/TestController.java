package com.tesi.unical.controller;

import com.tesi.unical.service.informationSchema.InformationSchemaService;
import com.tesi.unical.util.MigrationService;
import com.tesi.unical.util.RelationshipCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.PreparedStatement;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private InformationSchemaService informationSchemaService;

    @Autowired
    private RelationshipCheck relationshipCheck;

    @Autowired
    private MigrationService migrationService;

    @GetMapping("/stat")
    public ResponseEntity mig() {
        return ResponseEntity.ok(this.migrationService.migrate("migration","customers"));
    }

    @GetMapping()
    public ResponseEntity test() {
        PreparedStatement p;
        String s = null;
        return ResponseEntity.ok("ciao");
    }

    @GetMapping("/rel")
    public ResponseEntity testRel(@RequestParam("table") String table) {
        return ResponseEntity.ok(this.relationshipCheck.oneToMany(table));
    }



}
