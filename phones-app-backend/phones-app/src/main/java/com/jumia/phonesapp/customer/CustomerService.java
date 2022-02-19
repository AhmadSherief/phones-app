package com.jumia.phonesapp.customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jumia.phonesapp.country.Country;
import com.jumia.phonesapp.exceptions.ApiException;
import com.jumia.phonesapp.phone.Phone;
import com.jumia.phonesapp.phone.PhoneService;
import com.jumia.phonesapp.responses.PhoneResponse;

@Service
public class CustomerService {

	@Autowired
	private Connection connection;

	@Autowired
	private Map<String, Country> countries;
	
	@Autowired
	private PhoneService phoneService;


	/***
	 * 
	 * @param code -> country code
	 * @param valid -> state of number
	 * @param page -> page number
	 * @param pageSize -> page size
	 * @return phone response which includes the data to be returned as well as the count
	 */
	public PhoneResponse getPhones(String code, ValidOption valid, int page, int pageSize) {
		List<Phone> result = this.getCustomers(false,code, valid, page, pageSize);
		
		// For pagination
		int totalCount = this.getCount(true,code, valid, page, pageSize);
		
		return new PhoneResponse(result, totalCount);
	}

	/***
	 * 
	 * @param count -> whether the purpose of the function is to count or get records of data matching the input filter
	 * @param code -> country code
	 * @param valid -> state of number
	 * @param page -> page number
	 * @param pageSize -> page size
	 * @return List of Phone objects that match the input filter
	 */
	public List<Phone> getCustomers(boolean count,String code, ValidOption valid, int page, int pageSize) {

		SimpleEntry<String, List<String>> entry = queryBuilder(count,code, valid, page, pageSize);

		String query = entry.getKey();
		List<String> params = entry.getValue();
		List<Phone> result = new ArrayList<>();

		try (PreparedStatement pstmt = this.connection.prepareStatement(query);) {
			for (int i = 0; i < params.size(); i++) {
				pstmt.setString(i + 1, params.get(i));
			}
			ResultSet rs = pstmt.executeQuery();
			// loop through the result set
			while (rs.next()) {
				result.add(phoneService.getPhonefromFullNumber(rs.getString("phone")));
			}
			

		} catch (SQLException e) {
			System.out.println("exception here " + e.getMessage());
		}

		return result;

	}
	
	/***
	 * 
	 * @param count -> whether the purpose of the function is to count or get records of data matching the input filter
	 * @param code -> country code
	 * @param valid -> state of number
	 * @param page -> page number
	 * @param pageSize -> page size
	 * @return Total count of records that match the input filter
	 * 
	 * It is possible to combine this method with getCustomers, but this is better for readability 
	 */
	public int getCount(boolean count,String code, ValidOption valid, int page, int pageSize) {

		SimpleEntry<String, List<String>> entry = queryBuilder(count,code, valid, page, pageSize);

		String query = entry.getKey();
		List<String> params = entry.getValue();
		int totalCount = 0;

		try (PreparedStatement pstmt = this.connection.prepareStatement(query);) {
			for (int i = 0; i < params.size(); i++) {
				pstmt.setString(i + 1, params.get(i));
			}
			ResultSet rs = pstmt.executeQuery();
			// loop through the result set
				while (rs.next()) {
					totalCount = rs.getInt(1);
				}
			

		} catch (SQLException e) {
			System.out.println("exception here " + e.getMessage());
		}

		return totalCount;

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
	public SimpleEntry<String, List<String>> queryBuilder(boolean count, String code, ValidOption valid, int page, int pageSize) {

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
				throw new ApiException("Country with code: " + code + " was not found.");
			}

			// add filter by code
			query.append(" WHERE REGEXP (?,phone)");
			params.add(countries.get(code).getCodeRegex());

			if (!isNullOrEmpty(valid)) {

				// add filter by state --> the same state regex will be passed but depending on
				// validity will add or remove the keyword 'NOT'
				switch (valid.name()) {
				case "VALID":
					query.append(" AND REGEXP (?,phone)");

					break;
				case "INVALID":
					query.append(" AND NOT REGEXP (?,phone)");
					break;
				}
				params.add(countries.get(code).getValidRegex());

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
		return new SimpleEntry<String, List<String>>(query.toString(), params);
	}
	
	

	private static boolean isNullOrEmpty(String s) {
		return Objects.isNull(s) || s.length() == 0;
	}

	private static boolean isNullOrEmpty(ValidOption vo) {
		return Objects.isNull(vo) || vo.name().length() == 0;
	}
}
