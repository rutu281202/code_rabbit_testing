package com.contexio.dam.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
 
@Configuration
@ConfigurationProperties("map")
public class DBConfiguration {

	public String driverClassName;
	public String dbUrl;
	public String url2;
	public String dbUser;
	public String dbPassword;

 
	public String getDriverClassName() {
		return driverClassName;
	}
	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}
	public String getDbUrl() {
		return dbUrl;
	}
	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}
	public String getUrl2() {
		return url2;
	}
	public void setUrl2(String url2) {
		this.url2 = url2;
	}
	public String getDbUser() {
		return dbUser;
	}
	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}
	public String getDbPassword() {
		return dbPassword;
	}
	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}
	@Profile("dev")
	@Bean
	public String devDataConnection()
	{
		System.out.println("DB Conn -- profile Dev ");
		System.out.println("driverName :"+driverClassName+" : url :"+dbUrl+" : userName :"+dbUser+ " : password :"+dbPassword );
		return "DB Connection For Dev";
	}
	
	@Profile("aws")
	@Bean
	public String awsDataConnection()
	{
		System.out.println("DB Conn -- profile Dev ");
		System.out.println("driverName :"+driverClassName+" : url :"+dbUrl+" : userName :"+dbUser+ " : password :"+dbPassword );
		return "DB Connection For Dev";
	}
	
	@Profile("test")
	@Bean
	public String testDataConnection()
	{
		System.out.println("DB Conn -- profile Test ");
		System.out.println("driverName :"+driverClassName+" : url :"+dbUrl+" : userName :"+dbUser+ " : password :"+dbPassword );
		return "DB Connection For Test - low cost instance";
	}
	@Profile("qa")
	@Bean
	public String qaDataConnection()
	{
		System.out.println("DB Conn -- profile QA ");
		System.out.println("driverName :"+driverClassName+" : url :"+dbUrl+" : userName :"+dbUser+ " : password :"+dbPassword );
		return "DB Connection For Dev";
	}
	@Profile("prod")
	@Bean
	public String prodDataConnection()
	{
		System.out.println("DB Conn -- profile Prod ");
		System.out.println("driverName :"+driverClassName+" : url :"+dbUrl+" : userName :"+dbUser+ " : password :"+dbPassword );
		return "DB Connection For High Maintanance Instance";
	}
 
}