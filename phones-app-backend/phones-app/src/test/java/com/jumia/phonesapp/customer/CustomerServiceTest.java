package com.jumia.phonesapp.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import com.jumia.phonesapp.config.AppTestConfig;
import com.jumia.phonesapp.phone.Phone;
import com.jumia.phonesapp.phone.PhoneService;

@SpringBootTest
@Import(AppTestConfig.class)
@ExtendWith(MockitoExtension.class)
@TestPropertySource("classpath:application-test.properties")
class CustomerServiceTest {

//	Already Tested in QueryBuilderServiceTest class
	@Mock
	private QueryBuilderService queryBuilderService;

//	Already Tested in PhoneServiceTest class
	@Mock
	private PhoneService phoneService;

	@Autowired
	@Qualifier("testConnection")
	private Connection testConnection;

	@Autowired
	CustomerService underTest;

	@BeforeEach
	void setUp() throws Exception {
		this.underTest = new CustomerService(queryBuilderService, testConnection, phoneService);
	}

	@Test
	void testGetDataWithDefaultPagination() throws SQLException {
		// given
		boolean countQuery = false;
		String countryCode = null;
		ValidOption state = null;
		int page = 0;
		int pageSize = 5;

		given(queryBuilderService.build(countQuery, countryCode, state, page, pageSize))
				.willReturn(new SimpleEntry<>("SELECT * FROM customer LIMIT ? OFFSET ?;", Arrays.asList("5", "0")));

		given(phoneService.getPhonefromFullNumber("(212) 6007989253"))
				.willReturn(new Phone("Morocco", false, "212", "6007989253"));
		given(phoneService.getPhonefromFullNumber("(212) 698054317"))
				.willReturn(new Phone("Morocco", true, "212", "698054317"));
		given(phoneService.getPhonefromFullNumber("(212) 6546545369"))
				.willReturn(new Phone("Morocco", false, "212", "6546545369"));
		given(phoneService.getPhonefromFullNumber("(212) 6617344445"))
				.willReturn(new Phone("Morocco", false, "212", "6617344445"));
		given(phoneService.getPhonefromFullNumber("(212) 691933626"))
				.willReturn(new Phone("Morocco", true, "212", "691933626"));

		// when
		// then
		List<Phone> expected = Arrays.asList(new Phone[] { new Phone("Morocco", false, "212", "6007989253"),
				new Phone("Morocco", true, "212", "698054317"), new Phone("Morocco", false, "212", "6546545369"),
				new Phone("Morocco", false, "212", "6617344445"), new Phone("Morocco", true, "212", "691933626"), });

		List<Phone> actual = this.underTest.getCustomers(countQuery, countryCode, state, page, pageSize);

		assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	void testGetTotalCount() throws SQLException {
		// given
		boolean countQuery = true;
		String countryCode = null;
		ValidOption state = null;
		int page = 0;
		int pageSize = 5;

		given(queryBuilderService.build(countQuery, countryCode, state, page, pageSize))
				.willReturn(new SimpleEntry<>("SELECT COUNT(*) FROM customer", new ArrayList<>()));

		// when
		// then
		int expected = 41;
		int actual = this.underTest.getCount(countQuery, countryCode, state, page, pageSize);

		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void testGetTotalInvalidCount() throws SQLException {
		// given
		boolean countQuery = true;
		String countryCode = null;
		ValidOption state = ValidOption.INVALID;
		int page = 0;
		int pageSize = 5;

		given(queryBuilderService.build(countQuery, countryCode, state, page, pageSize)).willReturn(new SimpleEntry<>(
				"SELECT COUNT(*) FROM customer WHERE (REGEXP (?,phone) AND NOT REGEXP (?,phone)) OR (REGEXP (?,phone) AND NOT REGEXP (?,phone)) OR (REGEXP (?,phone) AND NOT REGEXP (?,phone)) OR (REGEXP (?,phone) AND NOT REGEXP (?,phone)) OR (REGEXP (?,phone) AND NOT REGEXP (?,phone))",
				Arrays.asList("\\(212\\)", "\\(212\\)\\ ?[5-9]\\d{8}$", "\\(256\\)", "\\(256\\)\\ ?\\d{9}$",
						"\\(258\\)", "\\(258\\)\\ ?[28]\\d{7,8}$", "\\(237\\)", "\\(237\\)\\ ?[2368]\\d{7,8}$",
						"\\(251\\)", "\\(251\\)\\ ?[1-59]\\d{8}$")));

		// when
		// then
		int expected = 14;
		int actual = this.underTest.getCount(countQuery, countryCode, state, page, pageSize);

		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void testGetTotalValidCount() throws SQLException {
		// given
		boolean countQuery = true;
		String countryCode = null;
		ValidOption state = ValidOption.VALID;
		int page = 0;
		int pageSize = 5;

		given(queryBuilderService.build(countQuery, countryCode, state, page, pageSize)).willReturn(new SimpleEntry<>(
				"SELECT COUNT(*) FROM customer WHERE REGEXP (?,phone) OR REGEXP (?,phone) OR REGEXP (?,phone) OR REGEXP (?,phone) OR REGEXP (?,phone)",
				Arrays.asList("\\(212\\)\\ ?[5-9]\\d{8}$", "\\(256\\)\\ ?\\d{9}$", "\\(258\\)\\ ?[28]\\d{7,8}$",
						"\\(237\\)\\ ?[2368]\\d{7,8}$", "\\(251\\)\\ ?[1-59]\\d{8}$")));

		// when
		// then
		int expected = 27;
		int actual = this.underTest.getCount(countQuery, countryCode, state, page, pageSize);

		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void testGetDataByCodeAndValidStateWithPagination() throws SQLException {
		// given
		boolean countQuery = false;
		String countryCode = "256";
		ValidOption state = ValidOption.VALID;
		int page = 0;
		int pageSize = 5;

		given(queryBuilderService.build(countQuery, countryCode, state, page, pageSize))
				.willReturn(new SimpleEntry<>("SELECT * FROM customer WHERE REGEXP (?,phone) LIMIT ? OFFSET ?;",
						Arrays.asList("\\(256\\)\\ ?\\d{9}$", "5", "0")));

		given(phoneService.getPhonefromFullNumber("(256) 775069443"))
				.willReturn(new Phone("Uganda", false, "256", "775069443"));
		given(phoneService.getPhonefromFullNumber("(256) 704244430"))
				.willReturn(new Phone("Uganda", false, "256", "704244430"));
		given(phoneService.getPhonefromFullNumber("(256) 714660221"))
				.willReturn(new Phone("Uganda", false, "256", "714660221"));

		// when
		// then
		List<Phone> expected = Arrays.asList(new Phone[] { new Phone("Uganda", false, "256", "775069443"),
				new Phone("Uganda", false, "256", "704244430"), new Phone("Uganda", false, "256", "714660221") });

		List<Phone> actual = this.underTest.getCustomers(countQuery, countryCode, state, page, pageSize);

		assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	void testGetCountByCodeAndValidState() throws SQLException {
		// given
		boolean countQuery = true;
		String countryCode = "212";
		ValidOption state = ValidOption.VALID;
		int page = 0;
		int pageSize = 5;

		given(queryBuilderService.build(countQuery, countryCode, state, page, pageSize)).willReturn(new SimpleEntry<>(
				"SELECT COUNT(*) FROM customer WHERE REGEXP (?,phone)", Arrays.asList("\\(212\\)\\ ?\\d{9}$")));

		// when
		// then
		int expected = 4;

		int actual = this.underTest.getCount(countQuery, countryCode, state, page, pageSize);

		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void testGetDataByCodeAndInvalidStateWithPagination() throws SQLException {
		// given
		boolean countQuery = false;
		String countryCode = "212";
		ValidOption state = ValidOption.INVALID;
		int page = 2;
		int pageSize = 1;

		given(queryBuilderService.build(countQuery, countryCode, state, page, pageSize)).willReturn(new SimpleEntry<>(
				"SELECT * FROM customer WHERE REGEXP (?,phone) AND NOT REGEXP (?,phone) LIMIT ? OFFSET ?;",
				Arrays.asList("\\(212\\)", "\\(212\\)\\ ?[5-9]\\d{8}$", "1", "2")));

		given(phoneService.getPhonefromFullNumber("(212) 6617344445"))
				.willReturn(new Phone("Morocco", false, "212", "6617344445"));

		// when
		// then
		List<Phone> expected = Arrays.asList(new Phone[] { new Phone("Morocco", false, "212", "6617344445") });

		List<Phone> actual = this.underTest.getCustomers(countQuery, countryCode, state, page, pageSize);

		assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	void testGetDataByCodeWithPagination() throws SQLException {
		// given
		boolean countQuery = false;
		String countryCode = "258";
		ValidOption state = null;
		int page = 1;
		int pageSize = 2;

		given(queryBuilderService.build(countQuery, countryCode, state, page, pageSize))
				.willReturn(new SimpleEntry<>("SELECT * FROM customer WHERE REGEXP (?,phone) LIMIT ? OFFSET ?;",
						Arrays.asList("\\(258\\)", "2", "2")));

		given(phoneService.getPhonefromFullNumber("(258) 849181828"))
				.willReturn(new Phone("Mozambique", true, "258", "849181828"));

		given(phoneService.getPhonefromFullNumber("(258) 84330678235"))
				.willReturn(new Phone("Mozambique", false, "258", "84330678235"));

		// when
		// then
		List<Phone> expected = Arrays.asList(new Phone[] { new Phone("Mozambique", true, "258", "849181828"),
				new Phone("Mozambique", false, "258", "84330678235") });

		List<Phone> actual = this.underTest.getCustomers(countQuery, countryCode, state, page, pageSize);

		assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	void testGetCountByCode() throws SQLException {
		// given
		boolean countQuery = true;
		String countryCode = "258";
		ValidOption state = ValidOption.VALID;
		int page = 0;
		int pageSize = 5;

		given(queryBuilderService.build(countQuery, countryCode, state, page, pageSize)).willReturn(new SimpleEntry<>(
				"SELECT COUNT(*) FROM customer WHERE REGEXP (?,phone)", Arrays.asList("\\(258\\)")));

		// when
		// then
		int expected = 8;

		int actual = this.underTest.getCount(countQuery, countryCode, state, page, pageSize);

		assertThat(actual).isEqualTo(expected);
	}
	
}
