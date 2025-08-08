package com.contexio.dam.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.contexio.dam.dto.CustomerDTO;
import com.contexio.dam.dto.LoginDTO;

@Service
public interface LoginService extends UserDetailsService {
	
	public List<CustomerDTO> loginDetails(LoginDTO login);

}
