package com.contexio.dam.repository;

import com.contexio.dam.dto.LoginDTO;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.contexio.dam.dto.CustomerDTO;

@Repository
public interface LoginRepository {
	
	List<CustomerDTO> loginDetails(LoginDTO login);
	
	List<CustomerDTO> findByCorporateEmail(String corporateEmail);
	
//	boolean existsByCorporateEmail(String corporateEmail);

}
