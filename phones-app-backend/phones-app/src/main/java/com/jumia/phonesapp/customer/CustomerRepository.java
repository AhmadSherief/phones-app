package com.jumia.phonesapp.customer;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CustomerRepository extends JpaRepository<Customer,Integer>{

//	Function.create(connection, "REGEXP", new Function() {
//		  @Override
//		  protected void xFunc() throws SQLException {
//		    String expression = value_text(0);
//		    String value = value_text(1);
//		    if (value == null)
//		      value = "";
//
//		    Pattern pattern=Pattern.compile(expression);
//		    result(pattern.matcher(value).find() ? 1 : 0);
//		  }
//		});
	
//	phone REGEXP '\\(237\\)'
	
//	regexp(phone, '\\(237\\)') = 1 
	
	@Query(nativeQuery = true,value="SELECT * FROM customer WHERE Ahmad(?,phone);")
	List<Customer> findByPhoneRegex(String regex);
	
//	@Query("SELECT c FROM customers c WHERE c.status = ?1 and u.name = ?2")
//	List<Customer> findCustomerByCodeAndState(Integer status, String name);
}