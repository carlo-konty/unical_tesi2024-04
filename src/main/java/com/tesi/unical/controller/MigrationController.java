package com.tesi.unical.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tesi.unical.entity.json.MigrationBody;
import com.tesi.unical.entity.json.MigrationInfo;
import com.tesi.unical.service.informationSchema.InformationSchemaService;
import com.tesi.unical.util.MigrationService;
import com.tesi.unical.util.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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


    @GetMapping("/test")
    public ResponseEntity test(@RequestParam("limit") Integer limit) {
        try {
            return ResponseEntity.ok("migration");
        } catch (Exception e) {
            return ResponseEntity.ok(e);
        }
    }

    @PostMapping("/tree")
    public ResponseEntity migrationTree(@RequestBody MigrationBody body) {
        try {
            return ResponseEntity.ok(migrationService.migrateTree(body.getSchema(), body.getTable(),body.getLimit().intValue()));
        } catch (Exception e) {
            return ResponseEntity.ok(e);
        }
    }

    @GetMapping("/relation-type")
    public ResponseEntity testRel(@RequestParam("schema") String schema, @RequestParam("table") String table, @RequestParam("type") int type) {
        try {
            return ResponseEntity.ok(migrationService.getRelationInfo(schema,table,type));
        } catch (Exception e) {
            return ResponseEntity.ok(e);
        }
    }

    @GetMapping("/tree")
    public ResponseEntity testLOpp(@RequestParam("schema") String schema, @RequestParam("table") String table) {
        try {
            @Data
            @AllArgsConstructor
             class MigrationMap {
                private int height;
                private List<MigrationInfo> children;
            }
            List<MigrationMap> res = new LinkedList<>();
            Map<Integer,List<MigrationInfo>> map = this.migrationService.childrenNoLoop(schema,table);
            for(Map.Entry entry : map.entrySet()) {
                res.add(new MigrationMap((Integer) entry.getKey(),(List<MigrationInfo>) entry.getValue()));
            }
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.ok(e);
        }
    }


}