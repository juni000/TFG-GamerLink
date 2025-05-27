package com.app.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.app.web.entities.Role;
import com.app.web.enums.RoleList;
import com.app.web.service.UserService;

@SpringBootApplication
public class GamerLinkApplication implements CommandLineRunner{
	
	@Autowired
	private UserService userService;
	
	public static void main(String[] args) {
		SpringApplication.run(GamerLinkApplication.class, args);
	}
	@Override
	public void run(String... args) throws Exception {
		Role adminRole = new Role();
		adminRole.setName(RoleList.ROLE_ADMIN);
		Role userRole = new Role();
		userRole.setName(RoleList.ROLE_USER);
		userService.addRole(adminRole);
		userService.addRole(userRole);
	}

}
