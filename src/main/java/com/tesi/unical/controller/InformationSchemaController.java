package com.tesi.unical.controller;

import com.tesi.unical.service.informationSchema.InformationSchemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("information-schema")
public class InformationSchemaController {

    @Autowired
    private InformationSchemaService informationSchemaService;


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

}