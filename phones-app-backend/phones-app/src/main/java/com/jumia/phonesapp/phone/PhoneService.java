package com.jumia.phonesapp.phone;

import java.util.Map;
import java.util.regex.Pattern;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jumia.phonesapp.country.Country;

@Service
public class PhoneService {

	@Autowired
	private Map<String, Country> countries;
	
	public PhoneService(Map<String, Country> countries) {
		this.countries = countries;
	}

	public Phone getPhonefromFullNumber(String fullNumber) {
		String code = fullNumber.substring(fullNumber.indexOf("(")+1, fullNumber.indexOf(")"));
		String number = fullNumber.substring(fullNumber.indexOf(")")+2, fullNumber.length());
		
		Country country = countries.get(code);
		if(country == null) {
			throw new EntityNotFoundException("Country not found with code: "+code);
		}
		String expression = countries.get(code).getValidRegex();
		Pattern pattern = Pattern.compile(expression);
		
		return new Phone(countries.get(code).getName(), pattern.matcher(fullNumber).find(), code, number);
	}
}
