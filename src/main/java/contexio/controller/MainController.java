package com.contexio.dam.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.contexio.dam.dto.CustomerDTO;
import com.contexio.dam.dto.LoginDTO;
import com.contexio.dam.service.LoginService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class MainController {
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    @GetMapping("/welcome")
    public String greeting() {
        return "base/dashboardPage";
    }

    @GetMapping("/login")
    public String login(){
    	return "base/login";
    }
    
    
    @GetMapping("/session-id")
    @ResponseBody
    public ResponseEntity<?> getCustomerId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("customer_id") == null) {
            return ResponseEntity.status(401).body("{\"error\": \"Customer ID not found in session\"}");
        }

        String customerId = (String) session.getAttribute("customer_id");

        // Logging customer ID for debugging
        LOGGER.info("Retrieved Customer ID from session: {}", customerId);

        return ResponseEntity.ok().body("{\"customerId\": \"" + customerId + "\"}");
    }

}
