package com.jumia.phonesapp.phone;

public class Phone {

	private String Country;
	private boolean valid;
	private String code;
	private String number;
	public Phone(String country, boolean valid, String code, String number) {
		super();
		Country = country;
		this.valid = valid;
		this.code = code;
		this.number = number;
	}
	public String getCountry() {
		return Country;
	}
	public void setCountry(String country) {
		Country = country;
	}
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	
	
	
}
