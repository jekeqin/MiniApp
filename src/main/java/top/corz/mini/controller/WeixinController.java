package top.corz.mini.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import top.corz.mini.entity.MpRequest;
import top.corz.mini.impl.WeixinComponent;

@RestController
@RequestMapping("/weixin")
public class WeixinController {

	@Autowired
	private WeixinComponent weixin;
	
	@RequestMapping("/mini/url/random.json")
	public Object urlRandom(String tag, String mode) {
		return weixin.urlRandomCreate(tag, mode, 0);
	}
	
	@RequestMapping("/mp/article.json")
	public Object mpArticle(String appid, Integer offset) {
		return weixin.mpArticle(appid, offset);
	}
	
	@RequestMapping("/request.json")
	public Object mpRequest(@RequestBody MpRequest obj) {
		return weixin.weixinRequest(obj);
	}
	
	@RequestMapping("/text/check.json")
	public Object checkText(@RequestBody JSONObject json) {
		return weixin.textCheck(json.getString("appid"), json.getString("code"), json.getString("openid"), json.getString("text"));
	}
}
