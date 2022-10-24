package top.corz.mini.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import top.corz.mini.entity.TopApp;
import top.corz.mini.impl.AppsComponent;

@RestController
@RequestMapping("/app")
public class AppsController {

	@Autowired
	private AppsComponent apps;
	
	@RequestMapping("/list.do")
	public Object list() {
		return apps.manage();
	}
	
	@RequestMapping("/add.do")
	public Object add(@RequestBody TopApp obj) {
		return apps.addApp(obj);
	}
	
	@RequestMapping("/del.do")
	public Object del(String appid) {
		return apps.delApp(appid);
	}
}
