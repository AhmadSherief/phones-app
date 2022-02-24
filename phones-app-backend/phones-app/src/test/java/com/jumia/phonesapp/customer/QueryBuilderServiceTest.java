package com.jumia.phonesapp.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.jumia.phonesapp.config.AppTestConfig;
import com.jumia.phonesapp.country.Country;
import com.jumia.phonesapp.exceptions.ApiException;

@SpringBootTest
@Import(AppTestConfig.class)
class QueryBuilderServiceTest {

	@Autowired
	private Map<String, Country> testCountries;

	@Autowired
	QueryBuilderService underTest;

	@BeforeEach
	void setUp() throws Exception {
		this.underTest = new QueryBuilderService(testCountries);
	}

	@Test
	void testGetDataWithDefaultPagination() {

		// given
		boolean countQuery = false;
		String countryCode = null;
		ValidOption state = null;
		int page = 0;
		int pageSize = 5;

		// when
		SimpleEntry<String, List<String>> actual = underTest.build(countQuery, countryCode, state, page, pageSize);

		// then
		SimpleEntry<String, List<String>> expected = new SimpleEntry<>("SELECT * FROM customer LIMIT ? OFFSET ?;",
				Arrays.asList("5", "0"));
		assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	void testGetTotalCount() {

		// given
		boolean countQuery = true;
		String countryCode = null;
		ValidOption state = null;
		int page = 0;
		int pageSize = 5;

		// when
		SimpleEntry<String, List<String>> actual = underTest.build(countQuery, countryCode, state, page, pageSize);

		// then
		SimpleEntry<String, List<String>> expected = new SimpleEntry<>("SELECT COUNT(*) FROM customer",
				new ArrayList<>());
		assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
	}
	

	@Test
	void testGetTotalInvalidCount() {

		// given
		boolean countQuery = true;
		String countryCode = null;
		ValidOption state = ValidOption.INVALID;
		int page = 0;
		int pageSize = 5;

		// when
		SimpleEntry<String, List<String>> actual = underTest.build(countQuery, countryCode, state, page, pageSize);

		// then
		SimpleEntry<String, List<String>> expected = new SimpleEntry<>(
				"SELECT COUNT(*) FROM customer WHERE (REGEXP (?,phone) AND NOT REGEXP (?,phone)) OR (REGEXP (?,phone) AND NOT REGEXP (?,phone)) OR (REGEXP (?,phone) AND NOT REGEXP (?,phone)) OR (REGEXP (?,phone) AND NOT REGEXP (?,phone)) OR (REGEXP (?,phone) AND NOT REGEXP (?,phone))",
				Arrays.asList("\\(212\\)", "\\(212\\)\\ ?[5-9]\\d{8}$", "\\(256\\)", "\\(256\\)\\ ?\\d{9}$",
						"\\(258\\)", "\\(258\\)\\ ?[28]\\d{7,8}$", "\\(237\\)", "\\(237\\)\\ ?[2368]\\d{7,8}$",
						"\\(251\\)", "\\(251\\)\\ ?[1-59]\\d{8}$"));
		assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
	}
	
	@Test
	void testGetTotalValidCount() {

		// given
		boolean countQuery = true;
		String countryCode = null;
		ValidOption state = ValidOption.VALID;
		int page = 0;
		int pageSize = 5;

		// when
		SimpleEntry<String, List<String>> actual = underTest.build(countQuery, countryCode, state, page, pageSize);

		// then
		SimpleEntry<String, List<String>> expected = new SimpleEntry<>(
				"SELECT COUNT(*) FROM customer WHERE REGEXP (?,phone) OR REGEXP (?,phone) OR REGEXP (?,phone) OR REGEXP (?,phone) OR REGEXP (?,phone)",
				Arrays.asList("\\(212\\)\\ ?[5-9]\\d{8}$", "\\(256\\)\\ ?\\d{9}$", "\\(258\\)\\ ?[28]\\d{7,8}$",
						"\\(237\\)\\ ?[2368]\\d{7,8}$", "\\(251\\)\\ ?[1-59]\\d{8}$"));
		assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	void testGetDataByCodeAndInvalidStateWithPagination() {

		// given
		boolean countQuery = false;
		String countryCode = "256";
		ValidOption state = ValidOption.INVALID;
		int page = 0;
		int pageSize = 20;

		// when
		SimpleEntry<String, List<String>> actual = underTest.build(countQuery, countryCode, state, page, pageSize);

		// then
		SimpleEntry<String, List<String>> expected = new SimpleEntry<>(
				"SELECT * FROM customer WHERE REGEXP (?,phone) AND NOT REGEXP (?,phone) LIMIT ? OFFSET ?;",
				Arrays.asList("\\(256\\)", "\\(256\\)\\ ?\\d{9}$", "20", "0"));
		assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	void testGetCountByCodeAndInvalidState() {

		// given
		boolean countQuery = true;
		String countryCode = "256";
		ValidOption state = ValidOption.INVALID;
		int page = 0;
		int pageSize = 5;

		// when
		SimpleEntry<String, List<String>> actual = underTest.build(countQuery, countryCode, state, page, pageSize);

		// then
		SimpleEntry<String, List<String>> expected = new SimpleEntry<>(
				"SELECT COUNT(*) FROM customer WHERE REGEXP (?,phone) AND NOT REGEXP (?,phone)",
				Arrays.asList("\\(256\\)", "\\(256\\)\\ ?\\d{9}$"));
		assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
	}
	
	@Test
	void testGetCountByCodeAndValidState() {

		// given
		boolean countQuery = true;
		String countryCode = "256";
		ValidOption state = ValidOption.VALID;
		int page = 0;
		int pageSize = 5;

		// when
		SimpleEntry<String, List<String>> actual = underTest.build(countQuery, countryCode, state, page, pageSize);

		// then
		SimpleEntry<String, List<String>> expected = new SimpleEntry<>(
				"SELECT COUNT(*) FROM customer WHERE REGEXP (?,phone)",
				Arrays.asList("\\(256\\)\\ ?\\d{9}$"));
		assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	void testGetDataByValidStateWithPagination() {

		// given
		boolean countQuery = false;
		String countryCode = null;
		ValidOption state = ValidOption.VALID;
		int page = 0;
		int pageSize = 7;

		// when
		SimpleEntry<String, List<String>> actual = underTest.build(countQuery, countryCode, state, page, pageSize);

		// then
		SimpleEntry<String, List<String>> expected = new SimpleEntry<>(
				"SELECT * FROM customer WHERE REGEXP (?,phone) OR REGEXP (?,phone) OR REGEXP (?,phone) OR REGEXP (?,phone) OR REGEXP (?,phone) LIMIT ? OFFSET ?;",
				Arrays.asList("\\(212\\)\\ ?[5-9]\\d{8}$", "\\(256\\)\\ ?\\d{9}$", "\\(258\\)\\ ?[28]\\d{7,8}$",
						"\\(237\\)\\ ?[2368]\\d{7,8}$", "\\(251\\)\\ ?[1-59]\\d{8}$", "7", "0"));
		assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	void testGetDataByInvalidCode() {

		// given
		boolean countQuery = false;
		String countryCode = "213";
		ValidOption state = null;
		int page = 0;
		int pageSize = 5;

		// when
		// then
		assertThatThrownBy(() -> underTest.build(countQuery, countryCode, state, page, pageSize))
				.isInstanceOf(ApiException.class).hasMessageContaining("Country with code: 213 was not found");
	}

	@Test
	void testGetDataByCodeWithPagination() {

		// given
		boolean countQuery = false;
		String countryCode = "258";
		ValidOption state = null;
		int page = 1;
		int pageSize = 2;

		// when
		SimpleEntry<String, List<String>> actual = underTest.build(countQuery, countryCode, state, page, pageSize);

		// then
		SimpleEntry<String, List<String>> expected = new SimpleEntry<>(
				"SELECT * FROM customer WHERE REGEXP (?,phone) LIMIT ? OFFSET ?;",
				Arrays.asList("\\(258\\)", "2", "2"));
		assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
	}
	
	@Test
	void testGetCountByCode() {

		// given
		boolean countQuery = true;
		String countryCode = "212";
		ValidOption state = null;
		int page = 0;
		int pageSize = 5;

		// when
		SimpleEntry<String, List<String>> actual = underTest.build(countQuery, countryCode, state, page, pageSize);

		// then
		SimpleEntry<String, List<String>> expected = new SimpleEntry<>(
				"SELECT COUNT(*) FROM customer WHERE REGEXP (?,phone)",
				Arrays.asList("\\(212\\)"));
		assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	void testGetCountByCodeWithPagination() {

		// given
		boolean countQuery = true;
		String countryCode = "237";
		ValidOption state = null;
		int page = 1;
		int pageSize = 2;

		// when
		SimpleEntry<String, List<String>> actual = underTest.build(countQuery, countryCode, state, page, pageSize);

		// then
		SimpleEntry<String, List<String>> expected = new SimpleEntry<>(
				"SELECT COUNT(*) FROM customer WHERE REGEXP (?,phone)", Arrays.asList("\\(237\\)"));
		assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
	}


	@Test
	void testGetInvalidDataWithPagination() {

		// given
		boolean countQuery = false;
		String countryCode = null;
		ValidOption state = ValidOption.INVALID;
		int page = 2;
		int pageSize = 10;

		// when
		SimpleEntry<String, List<String>> actual = underTest.build(countQuery, countryCode, state, page, pageSize);

		// then
		SimpleEntry<String, List<String>> expected = new SimpleEntry<>(
				"SELECT * FROM customer WHERE (REGEXP (?,phone) AND NOT REGEXP (?,phone)) OR (REGEXP (?,phone) AND NOT REGEXP (?,phone)) OR (REGEXP (?,phone) AND NOT REGEXP (?,phone)) OR (REGEXP (?,phone) AND NOT REGEXP (?,phone)) OR (REGEXP (?,phone) AND NOT REGEXP (?,phone)) LIMIT ? OFFSET ?;",
				Arrays.asList("\\(212\\)", "\\(212\\)\\ ?[5-9]\\d{8}$", "\\(256\\)", "\\(256\\)\\ ?\\d{9}$",
						"\\(258\\)", "\\(258\\)\\ ?[28]\\d{7,8}$", "\\(237\\)", "\\(237\\)\\ ?[2368]\\d{7,8}$",
						"\\(251\\)", "\\(251\\)\\ ?[1-59]\\d{8}$", "10", "20"));
		assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
	}

	
}
