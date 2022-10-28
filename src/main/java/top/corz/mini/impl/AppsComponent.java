package top.corz.mini.impl;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import top.corz.mini.entity.JsonResult;
import top.corz.mini.entity.TopApp;
import top.corz.mini.plugins.FileCache;
import top.corz.mini.utils.ValueUtils;
import top.corz.mini.utils.VerifyUtils;

@Component
public class AppsComponent {

	public Collection<TopApp> listApp(){
		LinkedHashMap<String, TopApp> map = FileCache.mapGet("apps", TopApp.class);
		return map.values();
	}

	public synchronized JsonResult addApp(TopApp obj) {
		String err = VerifyUtils.verify(obj);
		if( err!=null )
			return JsonResult.Err(err);
			
		obj.setUpdate(System.currentTimeMillis());
		FileCache.mapAdd("apps", obj.getAppid(), obj, TopApp.class);
		return manage();
	}
	
	public synchronized JsonResult delApp(String appid) {
		JSONObject obj = FileCache.mapDel("apps", appid, JSONObject.class);
		if( obj!=null )
			FileCache.mapAdd("apps.dels", obj.getString("appid"), obj, JSONObject.class);
		return manage();
	}
	
	public void locked(String appid, String msg) {
		LinkedHashMap<String, TopApp> map = FileCache.mapGet("apps", TopApp.class);
		TopApp obj = map.get(appid);
		if( obj!=null ) {
			obj.setState(0);
			obj.setNote(msg);
			FileCache.mapAdd("apps", obj.getAppid(), obj, TopApp.class);
		}
	}
	
	public JsonResult manage() {
		return JsonResult.Ok(listApp());
	}
	
	public synchronized TopApp findApp(String appid) {
		if( appid==null || appid.length()==0 )
			return null;
		LinkedHashMap<String, TopApp> map = FileCache.mapGet("apps", TopApp.class);
		if( map!=null )
			return map.get(appid);
		return null;
	}
	
	public synchronized TopApp firstApp(String type, String tag) {
		List<TopApp> list = listApp().stream().filter(o-> 
			o.getType().equalsIgnoreCase(type) && o.getState()==1 && o.tagCompare(tag)
		).toList();
		if( list.size()>0 )
			return list.get(0);
		return null;
	}
	
	public synchronized TopApp randomApp(String type, String tag) {
		List<TopApp> list = listApp().stream().filter(o-> 
			o.getType().equalsIgnoreCase(type) && o.getState()==1 && o.tagCompare(tag)
		).toList();
		if( list.size()>0 )
			return list.get(ValueUtils.random(list.size()));
		return null;
	}
	
	
}
