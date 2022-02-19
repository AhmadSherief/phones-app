package com.jumia.phonesapp.responses;

import java.util.List;

import com.jumia.phonesapp.phone.Phone;

public class PhoneResponse {

	private List<Phone> phones;
	
	private int totalCount;

	
	public PhoneResponse(List<Phone> phones, int totalCount) {
		super();
		this.phones = phones;
		this.totalCount = totalCount;
	}

	public List<Phone> getPhones() {
		return phones;
	}

	public void setPhones(List<Phone> phones) {
		this.phones = phones;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	
	
}
