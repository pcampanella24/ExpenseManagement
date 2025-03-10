package com.deltaservices.expensemanagement;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
//@ActiveProfiles("test")
class ExpenseManagementApplicationTests {

	@Autowired
	private Environment env;

	@Test
	void contextLoads() {
		assertTrue(env.acceptsProfiles("test"));
		assertEquals("org.h2.Driver", env.getProperty("spring.datasource.driver-class-name"));
	}
}