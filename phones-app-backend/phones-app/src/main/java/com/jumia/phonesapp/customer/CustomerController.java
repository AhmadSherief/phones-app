package com.jumia.phonesapp.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jumia.phonesapp.responses.PhoneResponse;

@RestController
@RequestMapping("/api/jumia/v1/customers")
public class CustomerController {

	@Autowired
	private final CustomerService customerService;

	public CustomerController(CustomerService customerService) {
		this.customerService = customerService;
	}

	@CrossOrigin
	@GetMapping(value="/phones",produces = "application/json")
	public ResponseEntity<PhoneResponse> getCustomers(@RequestParam(name = "code", required = false) String code,
			@RequestParam(name = "state", required = false) ValidOption state,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "pageSize", defaultValue = "5") int pageSize) {

		
		PhoneResponse response = this.customerService.getPhones(code, state, page, pageSize);;

		return new ResponseEntity<PhoneResponse>(response, HttpStatus.OK);
	}
	
}
