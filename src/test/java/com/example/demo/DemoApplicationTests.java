package com.example.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.responseschema.validator.Application;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class DemoApplicationTests {

	@Test
	public void contextLoads() {
	}

}
