package net.kaczmarzyk.spring.data.jpa.domain;

import net.kaczmarzyk.spring.data.jpa.Customer;
import net.kaczmarzyk.spring.data.jpa.IntegrationTestBase;
import org.junit.Before;
import org.junit.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static net.kaczmarzyk.spring.data.jpa.CustomerBuilder.customer;
import static org.assertj.core.api.Assertions.assertThat;

public class InTheFutureTest extends IntegrationTestBase {

	Customer homerSimpson;
	Customer margeSimpson;
	Customer moeSzyslak;

	private static final OffsetDateTime FUTURE_DATE_TIME = OffsetDateTime.now().plusDays(10);
	private static final OffsetDateTime PAST_DATE_TIME = OffsetDateTime.now().minusHours(10);

	@Before
	public void initData() {
		homerSimpson = customer("Homer", "Simpson")
				.registrationDate(FUTURE_DATE_TIME.getYear(), FUTURE_DATE_TIME.getMonth().getValue(), FUTURE_DATE_TIME.getDayOfMonth())
				.nextSpecialOffer(FUTURE_DATE_TIME)
				.birthDate(PAST_DATE_TIME.toLocalDate())
				.lastOrderTime(FUTURE_DATE_TIME.toLocalDateTime())
				.build(em);
		margeSimpson = customer("Marge", "Simpson")
				.registrationDate(FUTURE_DATE_TIME.getYear(), FUTURE_DATE_TIME.getMonth().getValue() + 2, FUTURE_DATE_TIME.getDayOfMonth())
				.nextSpecialOffer(PAST_DATE_TIME)
				.birthDate(FUTURE_DATE_TIME.toLocalDate())
				.lastOrderTime(FUTURE_DATE_TIME.toLocalDateTime().plusMonths(2))
				.build(em);
		moeSzyslak = customer("Moe", "Szyslak")
				.registrationDate(PAST_DATE_TIME.getYear(), PAST_DATE_TIME.getMonth().getValue(), PAST_DATE_TIME.getDayOfMonth())
				.nextSpecialOffer(FUTURE_DATE_TIME.plusDays(30))
				.birthDate(FUTURE_DATE_TIME.toLocalDate().plusDays(10))
				.lastOrderTime(PAST_DATE_TIME.toLocalDateTime())
				.build(em);
	}

	@Test
	public void filtersInTheFutureByDate() {
		//given
		InTheFuture<Customer> inTheFuture = new InTheFuture<>(queryCtx, "registrationDate", null, defaultConverter);

		//when
		List<Customer> result = customerRepo.findAll(inTheFuture);

		//then
		assertThat(result)
				.hasSize(2)
				.containsOnly(homerSimpson, margeSimpson);
	}

	@Test
	public void filtersInTheFutureByCalendar() {
		//given
		InTheFuture<Customer> inTheFuture = new InTheFuture<>(queryCtx, "registrationCalendar", null, defaultConverter);

		//when
		List<Customer> result = customerRepo.findAll(inTheFuture);

		//then
		assertThat(result)
				.hasSize(2)
				.containsOnly(homerSimpson, margeSimpson);
	}

	@Test
	public void filtersInTheFutureByLocalDate() {
		//given
		InTheFuture<Customer> inTheFuture = new InTheFuture<>(queryCtx, "birthDate", null, defaultConverter);

		//when
		List<Customer> result = customerRepo.findAll(inTheFuture);

		//then
		assertThat(result)
				.hasSize(2)
				.containsOnly(moeSzyslak, margeSimpson);
	}

	@Test
	public void filtersInTheFutureByLocalDateTime() {
		//given
		InTheFuture<Customer> inTheFuture = new InTheFuture<>(queryCtx, "lastOrderTime", null, defaultConverter);

		//when
		List<Customer> result = customerRepo.findAll(inTheFuture);

		//then
		assertThat(result)
				.hasSize(2)
				.containsOnly(homerSimpson, margeSimpson);
	}

	@Test
	public void filtersInTheFutureByOffsetDateTime() {
		//given
		InTheFuture<Customer> inTheFuture = new InTheFuture<>(queryCtx, "dateOfNextSpecialOffer", null, defaultConverter);

		//when
		List<Customer> result = customerRepo.findAll(inTheFuture);

		//then
		assertThat(result)
				.hasSize(2)
				.containsOnly(homerSimpson, moeSzyslak);
	}

	@Test
	public void filtersInTheFutureByInstant() {
		//given
		InTheFuture<Customer> inTheFuture = new InTheFuture<>(queryCtx, "dateOfNextSpecialOfferInstant", null, defaultConverter);

		//when
		List<Customer> result = customerRepo.findAll(inTheFuture);

		//then
		assertThat(result)
				.hasSize(2)
				.containsOnly(homerSimpson, moeSzyslak);
	}

	@Test
	public void filtersInTheFutureByTimestamp() {
		//given
		InTheFuture<Customer> inTheFuture = new InTheFuture<>(queryCtx, "dateOfNextSpecialOfferTimestamp", null, defaultConverter);

		//when
		List<Customer> result = customerRepo.findAll(inTheFuture);

		//then
		assertThat(result)
				.hasSize(2)
				.containsOnly(homerSimpson, moeSzyslak);
	}

}
