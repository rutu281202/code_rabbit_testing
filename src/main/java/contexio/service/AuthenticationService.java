package com.contexio.dam.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public String authenticate(String email, String password) {
    	System.out.println(email);
    	
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );
            return "success"; // ✅ Successful login
        } catch (DisabledException e) {
            return "Your account is inactive. Please contact support."; // ❌ Inactive account
        } catch (BadCredentialsException e) {
            return "Invalid email or password."; // ❌ Invalid credentials
        } catch (AuthenticationException e) {
            return "Authentication failed. Please try again."; // ❌ Generic error
        }
    }
}
