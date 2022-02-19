package com.jumia.phonesapp.country;

public class Country {

	private String code;
	
	private String name;
	
	private String codeRegex;
	
	private String validRegex;

	public Country() {
		super();
	}

	public Country(String name, String code,  String codeRegex, String validRegex) {
		super();
		this.code = code;
		this.name = name;
		this.codeRegex = codeRegex;
		this.validRegex = validRegex;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCodeRegex() {
		return codeRegex;
	}

	public void setCodeRegex(String codeRegex) {
		this.codeRegex = codeRegex;
	}

	public String getValidRegex() {
		return validRegex;
	}

	public void setValidRegex(String validRegex) {
		this.validRegex = validRegex;
	}
	
	
}
