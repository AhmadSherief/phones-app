package com.jumia.phonesapp.config;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.sqlite.Function;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jumia.phonesapp.country.Country;

@TestConfiguration
public class AppTestConfig {
	
	@Value("classpath:/testCountries.json")
	private Resource countriesResource;
	
	@Value("${spring.datasource.url}")
	private String datasourceUrl;
	

	/*** 
	 * Load the countries data from the countries.json file and expose it in a Map with
	 * format of <country code, country> for fast access.
	 * ***/
	@Bean
	public Map<String,Country> testCountries() throws StreamReadException, DatabindException, IOException {
		
		//create ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();
        List<Country> countries = Arrays.asList(objectMapper.readValue(countriesResource.getInputStream(), Country[].class));
        return countries.stream().collect(Collectors.toMap(Country::getCode, country->country));
	}
	
	/*** 
	 * Create a user-defined function 'REGEXP' since SQLite does not provide implementation 
	 * for the regular expression comparison
	 * ***/
	@Bean
	@Primary
	@Qualifier("testConnection")
	public Connection testConnect() {
		Connection conn = null;
		try {
			conn = testDataSource().getConnection();
			Function.create(conn, "REGEXP", new Function() {
				@Override
				protected void xFunc() throws SQLException {
					String expression = value_text(0);
					String value = value_text(1);
					if (value == null)
						value = "";

					Pattern pattern = Pattern.compile(expression);
					result(pattern.matcher(value).find() ? 1 : 0);
				}
			});
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return conn;
	}
	
	@Bean
	public DataSource testDataSource() {
	    final DriverManagerDataSource dataSource = new DriverManagerDataSource();
	    dataSource.setUrl(datasourceUrl);
	    return dataSource;
	}
}
