package com.jumia.phonesapp.phone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.jumia.phonesapp.config.AppTestConfig;
import com.jumia.phonesapp.country.Country;

@SpringBootTest
@Import(AppTestConfig.class)
class PhoneServiceTest {

	@Autowired
	private Map<String, Country> testCountries;

	private PhoneService underTest;

	@BeforeEach
	void setUp() {
		underTest = new PhoneService(testCountries);
	}

	@Test
	void testGetInvalidMoroccoPhonefromNumber() {
		// given
		String fullNumber = "(212) 6007989253";

		// when
		Phone phone = underTest.getPhonefromFullNumber(fullNumber);

		// then
		Phone expected = new Phone("Morocco", false, "212", "6007989253");
		assertThat(phone).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	void testGetInvalidUgandaPhonefromNumber() {
		// given
		String fullNumber = "(256) 7503O6263";

		// when
		Phone phone = underTest.getPhonefromFullNumber(fullNumber);

		// then
		Phone expected = new Phone("Uganda", false, "256", "7503O6263");
		assertThat(phone).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	void testGetInvalidCameroonPhonefromNumber() {
		// given
		String fullNumber = "(237) 6A0311634";

		// when
		Phone phone = underTest.getPhonefromFullNumber(fullNumber);

		// then
		Phone expected = new Phone("Cameroon", false, "237", "6A0311634");
		assertThat(phone).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	void testGetInvalidEthiopiaPhonefromNumber() {
		// given
		String fullNumber = "(251) 9773199405";

		// when
		Phone phone = underTest.getPhonefromFullNumber(fullNumber);

		// then
		Phone expected = new Phone("Ethiopia", false, "251", "9773199405");
		assertThat(phone).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	void testGetInvalidMozambiquePhonefromNumber() {
		// given
		String fullNumber = "(258) 84330678235";

		// when
		Phone phone = underTest.getPhonefromFullNumber(fullNumber);

		// then
		Phone expected = new Phone("Mozambique", false, "258", "84330678235");
		assertThat(phone).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	void testGetValidMoroccoPhonefromNumber() {
		// given
		String fullNumber = "(212) 698054317";

		// when
		Phone phone = underTest.getPhonefromFullNumber(fullNumber);

		// then
		Phone expected = new Phone("Morocco", true, "212", "698054317");
		assertThat(phone).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	void testGetValidUgandaPhonefromNumber() {
		// given
		String fullNumber = "(256) 775069443";

		// when
		Phone phone = underTest.getPhonefromFullNumber(fullNumber);

		// then
		Phone expected = new Phone("Uganda", true, "256", "775069443");
		assertThat(phone).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	void testGetValidCameroonPhonefromNumber() {
		// given
		String fullNumber = "(237) 697151594";

		// when
		Phone phone = underTest.getPhonefromFullNumber(fullNumber);

		// then
		Phone expected = new Phone("Cameroon", true, "237", "697151594");
		assertThat(phone).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	void testGetValidEthiopiaPhonefromNumber() {
		// given
		String fullNumber = "(251) 914701723";

		// when
		Phone phone = underTest.getPhonefromFullNumber(fullNumber);

		// then
		Phone expected = new Phone("Ethiopia", true, "251", "914701723");
		assertThat(phone).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	void testGetValidMozambiquePhonefromNumber() {
		// given
		String fullNumber = "(258) 846565883";

		// when
		Phone phone = underTest.getPhonefromFullNumber(fullNumber);

		// then
		Phone expected = new Phone("Mozambique", true, "258", "846565883");
		assertThat(phone).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	void testGetPhonefromNonExistingCode() {
		// given
		String fullNumber = "(213) 6007989253";

		// when
		// then
		assertThatThrownBy(() -> underTest.getPhonefromFullNumber(fullNumber)).isInstanceOf(EntityNotFoundException.class)
				.hasMessageContaining("Country not found with code: 213");
	}

}
