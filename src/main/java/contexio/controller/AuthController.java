package com.contexio.dam.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.contexio.dam.service.AuthenticationService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/validate")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
    	
    	System.out.println("email"+email);
        String message = authenticationService.authenticate(email, password);
        
        if (message.equals("success")) {
            return ResponseEntity.ok("Login successful!");
        } else {
            return ResponseEntity.status(401).body(message); // Return simple message
        }
    }
}

