package top.beforedawn.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
class ServiceApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void test1() {
		LocalDate localDate = LocalDate.of(2022, 4, 19);
		LocalDate localDate1 = LocalDate.of(2022, 4, 20);
		LocalDate localDate2 = LocalDate.now();
		System.out.println(localDate.equals(localDate1));
		System.out.println(localDate.equals(localDate2));
	}
}
