package top.corz.mini.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;
import top.corz.mini.entity.TopUser;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

	@RequestMapping("/login.json")
	public Object login(@RequestBody TopUser user) {
		log.info(JSONObject.toJSONString(user));
		return user;
	}
}
