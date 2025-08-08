package com.contexio.dam.serviceimpl;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.contexio.dam.dto.CustomerDTO;
import com.contexio.dam.dto.LoginDTO;
import com.contexio.dam.service.LoginService;
import com.contexio.dam.repository.LoginRepository;

@Service
public class LoginServiceImpl implements LoginService, UserDetailsService {

    @Autowired
    private LoginRepository loginRepo;

    @Override
    public List<CustomerDTO> loginDetails(LoginDTO login) {
        return loginRepo.loginDetails(login);
    }

    @Override
    public UserDetails loadUserByUsername(String corporateEmail) throws UsernameNotFoundException {
        // Create a LoginDTO with the provided email
        LoginDTO loginDTO = new LoginDTO();
        
        loginDTO.setCorporateEmail(corporateEmail);
        
        // Fetch customer details using loginRepo
        List<CustomerDTO> customers = loginRepo.loginDetails(loginDTO);

        if (customers.isEmpty()) {
            throw new UsernameNotFoundException("Customer not found with email: " + corporateEmail);
        }

        // Get the first customer record
        CustomerDTO customer = customers.get(0);
        
     // Check if the user is inactive
        if (customer.getStatus() != null && customer.getStatus().equalsIgnoreCase("INACTIVE")) {
            throw new DisabledException("Account is inactive. Please contact support.");
        }

        // Assign a default role (if roles exist in your DB, fetch dynamically)
        Set<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        return new org.springframework.security.core.userdetails.User(
                customer.getCorporateEmail(),
                customer.getEncryptedPassword(),
                authorities
        );
    }
}
