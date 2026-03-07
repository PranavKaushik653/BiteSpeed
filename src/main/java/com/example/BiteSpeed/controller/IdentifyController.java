package com.example.BiteSpeed.controller;

import com.example.BiteSpeed.dto.IdentifyRequest;
import com.example.BiteSpeed.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class IdentifyController {

    @Autowired
    private ContactService contactService;

    @GetMapping
    public String sayHello() {
        return "Hello World!";
    }

    @PostMapping("/api/identify")
    public ResponseEntity<?> identify (@RequestBody IdentifyRequest request) {
        return ResponseEntity.ok(contactService.identify(request));
    }
}
