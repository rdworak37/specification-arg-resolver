package net.kaczmarzyk.spring.data.jpa.domain;

import net.kaczmarzyk.spring.data.jpa.Customer;
import net.kaczmarzyk.spring.data.jpa.IntegrationTestBase;
import org.junit.Before;
import org.junit.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static net.kaczmarzyk.spring.data.jpa.CustomerBuilder.customer;
import static org.assertj.core.api.Assertions.assertThat;

public class InThePastTest extends IntegrationTestBase {

	Customer homerSimpson;
	Customer margeSimpson;
	Customer moeSzyslak;

	@Before
	public void initData() {
		OffsetDateTime currentDateTime = OffsetDateTime.now();

		homerSimpson = customer("Homer", "Simpson")
				.registrationDate(currentDateTime.getYear(), currentDateTime.getMonth().getValue(), currentDateTime.getDayOfMonth() - 1)
				.nextSpecialOffer(currentDateTime.minusHours(3))
				.birthDate(currentDateTime.toLocalDate().plusDays(5))
				.lastOrderTime(currentDateTime.toLocalDateTime().minusHours(10))
				.build(em);
		margeSimpson = customer("Marge", "Simpson")
				.registrationDate(currentDateTime.getYear(), currentDateTime.getMonth().getValue() - 2, currentDateTime.getDayOfMonth())
				.nextSpecialOffer(currentDateTime.plusMonths(50))
				.birthDate(currentDateTime.toLocalDate().minusDays(5))
				.lastOrderTime(currentDateTime.toLocalDateTime().minusMonths(2))
				.build(em);
		moeSzyslak = customer("Moe", "Szyslak")
				.registrationDate(currentDateTime.getYear(), currentDateTime.getMonth().getValue(), currentDateTime.getDayOfMonth() + 1)
				.nextSpecialOffer(currentDateTime.minusDays(30))
				.birthDate(currentDateTime.toLocalDate().minusDays(10))
				.lastOrderTime(currentDateTime.toLocalDateTime().plusDays(10))
				.build(em);
	}

	@Test
	public void filtersInThePastByDate() {
		//given
		InThePast<Customer> inThePast = new InThePast<>(queryCtx, "registrationDate", null, defaultConverter);

		//when
		List<Customer> result = customerRepo.findAll(inThePast);

		//then
		assertThat(result)
				.hasSize(2)
				.containsOnly(homerSimpson, margeSimpson);
	}

	@Test
	public void filtersInThePastByCalendar() {
		//given
		InThePast<Customer> inThePast = new InThePast<>(queryCtx, "registrationCalendar", null, defaultConverter);

		//when
		List<Customer> result = customerRepo.findAll(inThePast);

		//then
		assertThat(result)
				.hasSize(2)
				.containsOnly(homerSimpson, margeSimpson);
	}

	@Test
	public void filtersInThePastByLocalDate() {
		//given
		InThePast<Customer> inThePast = new InThePast<>(queryCtx, "birthDate", null, defaultConverter);

		//when
		List<Customer> result = customerRepo.findAll(inThePast);

		//then
		assertThat(result)
				.hasSize(2)
				.containsOnly(moeSzyslak, margeSimpson);
	}

	@Test
	public void filtersInThePastByLocalDateTime() {
		//given
		InThePast<Customer> inThePast = new InThePast<>(queryCtx, "lastOrderTime", null, defaultConverter);

		//when
		List<Customer> result = customerRepo.findAll(inThePast);

		//then
		assertThat(result)
				.hasSize(2)
				.containsOnly(homerSimpson, margeSimpson);
	}

	@Test
	public void filtersInThePastByOffsetDateTime() {
		//given
		InThePast<Customer> inThePast = new InThePast<>(queryCtx, "dateOfNextSpecialOffer", null, defaultConverter);

		//when
		List<Customer> result = customerRepo.findAll(inThePast);

		//then
		assertThat(result)
				.hasSize(2)
				.containsOnly(homerSimpson, moeSzyslak);
	}

	@Test
	public void filtersInThePastByInstant() {
		//given
		InThePast<Customer> inThePast = new InThePast<>(queryCtx, "dateOfNextSpecialOfferInstant", null, defaultConverter);

		//when
		List<Customer> result = customerRepo.findAll(inThePast);

		//then
		assertThat(result)
				.hasSize(2)
				.containsOnly(homerSimpson, moeSzyslak);
	}

	@Test
	public void filtersInThePastByTimestamp() {
		//given
		InThePast<Customer> inThePast = new InThePast<>(queryCtx, "dateOfNextSpecialOfferTimestamp", null, defaultConverter);

		//when
		List<Customer> result = customerRepo.findAll(inThePast);

		//then
		assertThat(result)
				.hasSize(2)
				.containsOnly(homerSimpson, moeSzyslak);
	}

	@Test(expected = IllegalArgumentException.class)
	public void throwsExceptionForInvalidClassType() {
		//given
		InThePast<Customer> inThePast = new InThePast<>(queryCtx, "firstName", null, defaultConverter);

		//when then
		customerRepo.findAll(inThePast);
	}
}
