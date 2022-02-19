package com.jumia.phonesapp.config;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sqlite.Function;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jumia.phonesapp.country.Country;

@Configuration
public class AppConfig {

	@Value("${spring.datasource.url}")
	private String datasourceUrl;
	
	/*** 
	 * Load the countries data from the countries.json file and expose it in a Map with
	 * format of <country code, country> for fast access.
	 * ***/
	@Bean
	public Map<String,Country> countries() throws StreamReadException, DatabindException, IOException {
		
		//create ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();
        List<Country> countries = Arrays.asList(objectMapper.readValue(new File("src/main/resources/countries.json"), Country[].class));
        return countries.stream().collect(Collectors.toMap(Country::getCode, country->country));
	}

	/*** 
	 * Create a user-defined function 'REGEXP' since SQLite does not provide implementation 
	 * for the regular expression comparison
	 * ***/
	@Bean
	public Connection connect() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(this.datasourceUrl);
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
	
}
