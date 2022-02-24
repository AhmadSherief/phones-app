package com.jumia.phonesapp.customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.AbstractMap.SimpleEntry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jumia.phonesapp.country.Country;
import com.jumia.phonesapp.exceptions.ApiException;

@Service
public class QueryBuilderService {


	@Autowired
	private Map<String, Country> countries;
	
	public QueryBuilderService(Map<String, Country> countries) {
		this.countries = countries;
	}
	
	/***
	 * 
	 * @param count -> whether the purpose of the function is to count or get records of data matching the input filter
	 * @param code -> country code
	 * @param valid -> state of number
	 * @param page -> page number
	 * @param pageSize -> page size
	 * @return -> A simply entry object containing the query to be executed as well as a List containing commands
	 *  		  to replace the '?' found in the query in order of occurrence
	 */
	public SimpleEntry<String, List<String>> build(boolean count, String code, ValidOption valid, int page, int pageSize) {

		// Base Query
		StringBuilder query;
		if(count) {
			query = new StringBuilder("SELECT COUNT(*) FROM customer");
		}
		else {
			query = new StringBuilder("SELECT * FROM customer");
		}

		// Parameters to pass to PreparedStatement
		List<String> params = new ArrayList<>();

		if (!isNullOrEmpty(code)) {
			// validate country code query parameter
			if (!countries.containsKey(code)) {
				throw new ApiException("Country with code: " + code + " was not found");
			}

			if (!isNullOrEmpty(valid)) {

				// add filter by state --> if valid is required pass valid regex only as param 
				//					   --> if invalid is required query for records that match the code regex but not the valid regex 
				switch (valid.name()) {
				case "VALID":
					query.append(" WHERE REGEXP (?,phone)");
					params.add(countries.get(code).getValidRegex());
					break;
				case "INVALID":
					query.append(" WHERE REGEXP (?,phone) AND NOT REGEXP (?,phone)");
					params.add(countries.get(code).getCodeRegex());
					params.add(countries.get(code).getValidRegex());
					break;
				}
			}
			else {
				// add filter by code only
				query.append(" WHERE REGEXP (?,phone)");
				params.add(countries.get(code).getCodeRegex());
			}
		} else if (!isNullOrEmpty(valid)) {

			// case filter by state only
			Country firstCountry = countries.values().stream().findFirst().get();

			switch (valid.name().toUpperCase()) {
			case "VALID":
				query.append(" WHERE REGEXP (?,phone)");
				params.add(firstCountry.getValidRegex());

				countries.values().stream().skip(1).forEach(country -> {
					query.append(" OR REGEXP (?,phone)");
					params.add(country.getValidRegex());
				});
				break;
			case "INVALID":
				query.append(" WHERE (REGEXP (?,phone) AND NOT REGEXP (?,phone))");
				params.add(firstCountry.getCodeRegex());
				params.add(firstCountry.getValidRegex());

				countries.values().stream().skip(1).forEach(country -> {
					query.append(" OR (REGEXP (?,phone) AND NOT REGEXP (?,phone))");
					params.add(country.getCodeRegex());
					params.add(country.getValidRegex());
				});
				break;
			}

		}

		//for pagination
		if(!count) {
			query.append(" LIMIT ? OFFSET ?;");
			params.add(Integer.toString(pageSize));
			params.add(Integer.toString(page * pageSize));
		}
		
//		System.out.println(query.toString());
//		params.stream().forEach(System.out::println);
		
		return new SimpleEntry<String, List<String>>(query.toString(), params);
	}
	
	

	private static boolean isNullOrEmpty(String s) {
		return Objects.isNull(s) || s.length() == 0;
	}

	private static boolean isNullOrEmpty(ValidOption vo) {
		return Objects.isNull(vo) || vo.name().length() == 0;
	}
}
