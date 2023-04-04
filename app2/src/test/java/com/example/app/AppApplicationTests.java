package com.example.app;

import com.example.app.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AppApplicationTests {

	private UserService userService;
	@Autowired
	public void UserTest(UserService userService){
		this.userService = userService;
	}



	@Test
	public void testFindAll() {
		userService.findAll.forEach().forEach(user -> System.out.println(user.toString()));
	}

}


