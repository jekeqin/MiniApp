package top.corz.mini.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import top.corz.mini.plugins.TaokeApi;

@RestController
@RequestMapping("/taoke")
public class TaokeController {

	@Autowired
	private TaokeApi taoke;
	
	@RequestMapping("/category.json")
	public Object category() {
		return taoke.category();
	}
	
	@RequestMapping("/query.json")
	public Object query(@RequestBody JSONObject json) {
		return taoke.optionalQuery(json);
	}
}
