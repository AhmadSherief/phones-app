package com.jumia.phonesapp.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jumia.phonesapp.config.AppTestConfig;
import com.jumia.phonesapp.exceptions.ApiException;
import com.jumia.phonesapp.exceptions.GeneralException;
import com.jumia.phonesapp.exceptions.GlobalExceptionHandler;

@Import(AppTestConfig.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
class CustomerControllerTest {

	@Mock
	private CustomerService customerService;

	private MockMvc mockMvc;

	@InjectMocks
	private CustomerController underTest;

	ObjectMapper objectMapper;

	@BeforeEach
	void setUp() throws Exception {
		objectMapper = new ObjectMapper();
		mockMvc = MockMvcBuilders.standaloneSetup(underTest).setControllerAdvice(GlobalExceptionHandler.class).build();
	}

	@Test
	void testVerifyGetCustomersCalledWithDefaultParams() throws Exception {
		// given
		RequestBuilder request = get("/api/jumia/v1/customers/phones");
		// when
		mockMvc.perform(request);
		// then
		verify(customerService).getCustomers(false, null, null, 0, 5);
	}

	@Test
	void testVerifyGetCustomersCalledWithCustomeParams() throws Exception {
		// given
		RequestBuilder request = get("/api/jumia/v1/customers/phones").queryParam("code", "212")
				.queryParam("state", ValidOption.VALID.name()).queryParam("page", "1").queryParam("pageSize", "3");
		// when
		mockMvc.perform(request);
		// then
		verify(customerService).getCustomers(false, "212", ValidOption.VALID, 1, 3);
	}

	@Test
	void testVerifyGetCountCalledWithDefaultParams() throws Exception {
		// given
		RequestBuilder request = get("/api/jumia/v1/customers/phones");
		// when
		mockMvc.perform(request);
		// then
		verify(customerService).getCount(true, null, null, 0, 5);
	}

	@Test
	void testVerifyGetCountCalledWithCustomeParams() throws Exception {
		// given
		RequestBuilder request = get("/api/jumia/v1/customers/phones").queryParam("code", "212")
				.queryParam("state", ValidOption.VALID.name()).queryParam("page", "1").queryParam("pageSize", "3");
		// when
		mockMvc.perform(request);
		// then
		verify(customerService).getCount(true, "212", ValidOption.VALID, 1, 3);
	}

	@Test
	void testVerifyStatus() throws Exception {
		// given
		RequestBuilder request = get("/api/jumia/v1/customers/phones").queryParam("code", "212")
				.queryParam("state", ValidOption.VALID.name()).queryParam("page", "1").queryParam("pageSize", "3");
		// when
		// then
		mockMvc.perform(request).andExpect(status().isOk());
	}

	@Test
	void testVerifyResponseOnInvalidCode() throws Exception {

		// given
		RequestBuilder request = get("/api/jumia/v1/customers/phones").queryParam("code", "213");
		ApiException apiException = new ApiException("Country with code: 213 was not found");
		given(customerService.getCustomers(false, "213", null, 0, 5)).willThrow(apiException);
		// when
		String actual = mockMvc.perform(request).andReturn().getResponse().getContentAsString();
		// then
		String expected = objectMapper.writeValueAsString(new GeneralException(apiException.getMessage()));
		assertThat(actual).isEqualTo(expected);

	}

	@Test
	void testVerifyStatusOnInvalidCode() throws Exception {

		// given
		RequestBuilder request = get("/api/jumia/v1/customers/phones").queryParam("code", "213");
		ApiException apiException = new ApiException("Country with code: 213 was not found");
		given(customerService.getCustomers(false, "213", null, 0, 5)).willThrow(apiException);
		// when
		// then
		mockMvc.perform(request).andExpect(status().isBadRequest());

	}
}
