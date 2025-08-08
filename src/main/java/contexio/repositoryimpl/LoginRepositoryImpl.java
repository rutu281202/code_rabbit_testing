package com.contexio.dam.repositoryimpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.contexio.dam.dto.CustomerDTO;
import com.contexio.dam.dto.LoginDTO;
import com.contexio.dam.repository.LoginRepository;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;

@Repository
public class LoginRepositoryImpl implements LoginRepository {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginRepositoryImpl.class);

    @Resource
    private Environment environment;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
    @Autowired
    private HttpSession httpSession; // Inject HttpSession

    private String SQL;

    @Override
    public List<CustomerDTO> loginDetails(LoginDTO login) {
        if (login == null || login.getCorporateEmail() == null || login.getCorporateEmail().trim().isEmpty()) {
            LOGGER.warn("Login attempt with null or empty corporate email"+login);
            return List.of();
        }
        
        List<CustomerDTO> customers = findByCorporateEmail(login.getCorporateEmail());

        if (!customers.isEmpty()) {
            // Store customer ID in session
            httpSession.setAttribute("customer_id", customers.get(0).getCustomerId());
            LOGGER.info("Stored customer_id in session: {}", customers.get(0).getCustomerId());
        }

        
        return customers;
    }

    public List<CustomerDTO> findByCorporateEmail(String corporateEmail) {
        LOGGER.info("Fetching customer details for email: {}", corporateEmail);

        SQL = "SELECT * FROM customer WHERE corporate_email = :corporateEmail";

        return namedParameterJdbcTemplate.query(SQL,
                new MapSqlParameterSource().addValue("corporateEmail", corporateEmail),
                new RowMapper<CustomerDTO>() {
                    @Override
                    public CustomerDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        CustomerDTO user = new CustomerDTO();

                        user.setCustomerId(rs.getString("customer_id"));
                        user.setCustomerFirstName(rs.getString("customer_first_name"));
                        user.setCustomerLastName(rs.getString("customer_last_name"));
                        user.setCustomerContactNo(rs.getString("customer_contact_no"));
                        user.setCorporateEmail(rs.getString("corporate_email"));
                        user.setEncryptedPassword(rs.getString("encrypted_password")); // Store hashed password
                        user.setCompanyName(rs.getString("company_name"));
                        user.setCompanyWebsite(rs.getString("company_website"));
                        user.setCustomerDesignation(rs.getString("customer_designation"));
                        user.setPlanHours(rs.getString("plan_hours"));
                        user.setPremiumHours(rs.getString("premium_hours"));
                        user.setExtraPremiumHours(rs.getString("extra_premium_hours"));
                        user.setLoginCounter(rs.getString("login_counter"));
                        user.setStatus(rs.getString("status"));
                        return user;
                    }
                });
    }

	/*
	 * @Override public boolean existsByCorporateEmail(String corporateEmail) {
	 * String SQL =
	 * "SELECT COUNT(*) FROM customer WHERE corporate_email = :corporateEmail";
	 * Integer count = namedParameterJdbcTemplate.queryForObject(SQL, new
	 * MapSqlParameterSource("corporateEmail", corporateEmail), Integer.class);
	 * return count != null && count > 0; }
	 */
}
