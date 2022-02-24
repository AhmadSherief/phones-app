package com.jumia.phonesapp.exceptions;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GeneralException {

	@JsonProperty("Message")
	private String errorMessage;

	public GeneralException(String errorMessage) {
		super();
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	
}
