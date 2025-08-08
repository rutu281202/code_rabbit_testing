package com.contexio.dam.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.jolbox.bonecp.BoneCPConfig;
import com.jolbox.bonecp.BoneCPDataSource;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

@Configuration

@PropertySource("file:D:/IT/Development/Properties/DAM/dam_queries.properties")

@EnableWebSecurity
public class Config {
	
	private UserDetailsService userDetailsService;
	
	private static final Logger logger = LoggerFactory.getLogger(Config.class);
	@Resource
	private Environment environment;

	@Bean(name = "mappingDataSource")
	@Qualifier("mappingDataSource")
	public DataSource mappingDataSource() {
		String trimmedEachEnvironment = "map";
		logger.debug("Setting up the datasource for {}", trimmedEachEnvironment);

		BoneCPConfig bonecpConfig = new BoneCPConfig();
		bonecpConfig.setJdbcUrl(environment.getRequiredProperty(trimmedEachEnvironment + ".dbUrl"));
		bonecpConfig.setUsername(environment.getRequiredProperty(trimmedEachEnvironment + ".dbUser"));
		bonecpConfig.setPassword(environment.getRequiredProperty(trimmedEachEnvironment + ".dbPassword"));
		BoneCPDataSource dataSource = new BoneCPDataSource(bonecpConfig);
		dataSource.setDriverClass(environment.getRequiredProperty(trimmedEachEnvironment + ".driverClassName"));

		return dataSource;
	}

	@Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests((authorize) ->
                        authorize.anyRequest().authenticated()
                ).formLogin(
                        form -> form
                                .loginPage("/login")
                                .loginProcessingUrl("/login")
                                .defaultSuccessUrl("/welcome", true)
                                .permitAll()
                ).logout(
                        logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .permitAll()
                );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
