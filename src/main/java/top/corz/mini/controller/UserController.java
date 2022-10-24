package top.corz.mini.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import top.corz.mini.entity.TopUser;
import top.corz.mini.impl.UserComponent;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserComponent userComponent;

	@RequestMapping("/login.json")
	public Object login(@RequestBody TopUser user) {
		return userComponent.login(user);
	}
}
