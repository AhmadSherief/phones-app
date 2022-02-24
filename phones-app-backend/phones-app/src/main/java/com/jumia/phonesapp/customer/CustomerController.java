package com.jumia.phonesapp.customer;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jumia.phonesapp.phone.Phone;
import com.jumia.phonesapp.responses.PhoneResponse;

@RestController
@RequestMapping("/api/jumia/v1/customers")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	public CustomerController(CustomerService customerService) {
		this.customerService = customerService;
	}

	/***
	 * 
	 * @param code -> country code
	 * @param state -> phone number validity state
	 * @param page -> page number
	 * @param pageSize -> page size
	 * @return -> list of phones that match the input parameters along with their count
	 * @throws SQLException 
	 */
	@CrossOrigin
	@GetMapping(value="/phones",produces = "application/json")
	public ResponseEntity<PhoneResponse> getCustomers(@RequestParam(name = "code", required = false) String code,
			@RequestParam(name = "state", required = false) ValidOption state,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "pageSize", defaultValue = "5") int pageSize) throws SQLException {

		List<Phone> phones = this.customerService.getCustomers(false,code, state, page, pageSize);
		
		int totalCount = this.customerService.getCount(true,code, state, page, pageSize);
		
		PhoneResponse response = new PhoneResponse(phones, totalCount);

		return new ResponseEntity<PhoneResponse>(response, HttpStatus.OK);
	}
	
}
