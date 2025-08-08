//package com.contexio.dam.controller;
//
//import java.util.List;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//
//import com.contexio.dam.dto.CustomerDTO;
//import com.contexio.dam.dto.LoginDTO;
//import com.contexio.dam.service.LoginService;
//
//import jakarta.servlet.http.HttpServletRequest;
//
//@Controller
//public class LoginController {
//    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
//    private final LoginService loginService;
//
//    @Autowired
//    public LoginController(LoginService loginService) {
//        this.loginService = loginService;
//    }
//
//    @PostMapping("/dashboard")
//    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO, HttpServletRequest request) {
//        LOGGER.info("Login attempt for email: {}", loginDTO.getCorporateEmail());
//
//        List<CustomerDTO> customers = loginService.loginDetails(loginDTO);
//        System.out.println("customers"+customers);
//
//        if (customers.isEmpty()) {
//            LOGGER.warn("Invalid login attempt for email: {}", loginDTO.getCorporateEmail());
//            return ResponseEntity.status(401).body("Invalid email or password");
//        }
//
//        CustomerDTO customer = customers.get(0);
//        System.out.println("customer"+customer);
//        
//        String customerStatus = customer.getStatus();
//        LOGGER.info("Customer status: {}", customerStatus);
//
////        if (customerStatus == null) {
////            LOGGER.warn("Customer status is NULL for email: {}", loginDTO.getCorporateEmail());
////            return ResponseEntity.status(401).body("Invalid status");
////        }
//
//        // Check if the user is inactive
//        if ("inactive".equalsIgnoreCase(customer.getStatus())) {
//            LOGGER.warn("Inactive account login attempt: {}", loginDTO.getCorporateEmail());
//            return ResponseEntity.status(403).body("Account is inactive, please contact support");
//        }
//
//        // Store user session data
//        request.getSession().setAttribute("USER_SESSION_DATA", customer);
//        LOGGER.info("User logged in successfully: {}", customer.getCorporateEmail());
//
//        return ResponseEntity.ok(customer);
//    }
//}
