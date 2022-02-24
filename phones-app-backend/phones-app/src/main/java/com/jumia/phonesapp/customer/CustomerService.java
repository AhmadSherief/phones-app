package com.jumia.phonesapp.customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jumia.phonesapp.phone.Phone;
import com.jumia.phonesapp.phone.PhoneService;

@Service
public class CustomerService {

	@Autowired
	private Connection connection;

	@Autowired
	private QueryBuilderService queryBuilderService;
	
	@Autowired
	private PhoneService phoneService;
	
	
	public CustomerService(QueryBuilderService queryBuilderService, Connection connection, PhoneService phoneService) {
		this.queryBuilderService = queryBuilderService;
		this.connection = connection;
		this.phoneService = phoneService;
	}

	/***
	 * 
	 * @param count -> whether the purpose of the function is to count or get records of data matching the input filter
	 * @param code -> country code
	 * @param valid -> state of number
	 * @param page -> page number
	 * @param pageSize -> page size
	 * @return List of Phone objects that match the input filter
	 * @throws SQLException 
	 */
	public List<Phone> getCustomers(boolean count,String code, ValidOption valid, int page, int pageSize) throws SQLException {
		SimpleEntry<String, List<String>> entry = this.queryBuilderService.build(count,code, valid, page, pageSize);
		

		String query = entry.getKey();
		List<String> params = entry.getValue();
		List<Phone> result = new ArrayList<>();

		PreparedStatement pstmt = this.connection.prepareStatement(query);
		for (int i = 0; i < params.size(); i++) {
			pstmt.setString(i + 1, params.get(i));
		}
		ResultSet rs = pstmt.executeQuery();
		// loop through the result set
		while (rs.next()) {
			result.add(phoneService.getPhonefromFullNumber(rs.getString("phone")));
		}

		return result;

	}
	
	/***
	 * 
	 * @param count    -> whether the purpose of the function is to count or get
	 *                 records of data matching the input filter
	 * @param code     -> country code
	 * @param valid    -> state of number
	 * @param page     -> page number
	 * @param pageSize -> page size
	 * @return Total count of records that match the input filter
	 * 
	 *         It is possible to combine this method with getCustomers, but this is
	 *         better for readability
	 * @throws SQLException
	 */
	public int getCount(boolean count,String code, ValidOption valid, int page, int pageSize) throws SQLException {

		SimpleEntry<String, List<String>> entry = this.queryBuilderService.build(count,code, valid, page, pageSize);

		String query = entry.getKey();
		List<String> params = entry.getValue();
		int totalCount = 0;

		PreparedStatement pstmt = this.connection.prepareStatement(query);
		for (int i = 0; i < params.size(); i++) {
			pstmt.setString(i + 1, params.get(i));
		}
		ResultSet rs = pstmt.executeQuery();
		// loop through the result set
			while (rs.next()) {
				totalCount = rs.getInt(1);
			}
		

		return totalCount;

	}
	
	
}
