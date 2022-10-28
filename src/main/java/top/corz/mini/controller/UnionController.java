package top.corz.mini.controller;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import top.corz.mini.entity.JsonResult;
import top.corz.mini.entity.TopApp;
import top.corz.mini.impl.AppsComponent;
import top.corz.mini.plugins.FileCache;
import top.corz.mini.plugins.WeixinApi;
import top.corz.mini.utils.ApiHolder;

/**
 * 微信小程序推客
 * https://union.weixin.qq.com/
 * @author KZQ
 */
@RestController
@RequestMapping("/union")
public class UnionController {

	@Autowired
	private AppsComponent apps;
	
	@RequestMapping("/category.json")
	public Object category() {
		JSONArray array = FileCache.getJArray("mp.product.cats", 86400);
		if( array!=null )
			return JsonResult.Ok(array);
		TopApp app = apps.findApp(ApiHolder.getAppid());
		if( app==null )
			return JsonResult.Err("APP未注册");
		JSONObject json = WeixinApi.Union.category(app);
		if( json!=null && json.containsKey("productCats") ) {
			FileCache.set("mp.product.cats", json.getJSONArray("productCats").hashCode());
			return JsonResult.Ok(json.get("productCats"));
		}
		return JsonResult.Err("网络异常",json);
	}
	
	public Object query(@RequestBody JSONObject data, Boolean fine) throws UnsupportedEncodingException {
		TopApp app = apps.findApp(ApiHolder.getAppid());
		if( app==null )
			return JsonResult.Err("APP未注册");
		JSONObject json = WeixinApi.Union.queryProduct(fine, app, data);
		if( json!=null && json.containsKey("productList") ) {
			return JsonResult.Ok(json.get("productList"));
		}
		return JsonResult.Err("网络异常",json);
	}
	
	public Object detail(String pid, String appid, String productId) {
		JSONArray array = FileCache.getJArray("mp.product." + appid + "." + productId, 86400);
		if( array!=null )
			return JsonResult.Ok(array);
		
		TopApp app = apps.findApp(ApiHolder.getAppid());
		if( app==null )
			return JsonResult.Err("APP未注册");
		JSONObject json = WeixinApi.Union.queryAssets(app, pid, appid, productId);
		if( json!=null && json.containsKey("list") ) {
			FileCache.set("mp.product." + appid + "." + productId, json.getJSONArray("list"));
			return JsonResult.Ok(json.get("list"));
		}
		return JsonResult.Err("网络异常",json);
	}
}
