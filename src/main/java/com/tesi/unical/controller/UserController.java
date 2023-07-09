package com.tesi.unical.controller;

import com.tesi.unical.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "/all")
    public ResponseEntity getAll() {
        return ResponseEntity.ok(this.userService.getAll());
    }
}
