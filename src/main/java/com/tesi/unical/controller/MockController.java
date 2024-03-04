package com.tesi.unical.controller;

import com.tesi.unical.service.mock.MockDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mock")
public class MockController {

    @Autowired
    private MockDataService mockDataService;

    @GetMapping()
    public ResponseEntity fill(@RequestParam("row") int row) {
        this.mockDataService.fillDB(row);
        return ResponseEntity.ok("");
    }
}
