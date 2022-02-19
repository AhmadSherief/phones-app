package com.jumia.phonesapp.country;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jumia/v1/countries")
public class CountryController {

	@Autowired
	private final CountryService countryService;

	public CountryController(CountryService countryService) {
		this.countryService = countryService;
	}
	
	@CrossOrigin
	@GetMapping(produces = "application/json")
	public ResponseEntity<List<Country>> getCountries() {
		List<Country> result = this.countryService.getCountries();
		return new ResponseEntity<List<Country>>(result, HttpStatus.OK);
	}
}
