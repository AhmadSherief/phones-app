package com.jumia.phonesapp.country;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CountryService {

	@Autowired
	private Map<String, Country> countries;


	public List<Country> getCountries() {
		return new ArrayList<>(countries.values());
	}
}
