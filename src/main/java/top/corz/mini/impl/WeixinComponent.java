package top.corz.mini.impl;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import top.corz.mini.entity.JsonResult;
import top.corz.mini.entity.MpException;
import top.corz.mini.entity.MpRequest;
import top.corz.mini.entity.TopApp;
import top.corz.mini.plugins.FileCache;
import top.corz.mini.plugins.WeixinApi;

@Component
public class WeixinComponent {

	@Lazy
	@Autowired
	private AppsComponent apps;
	
	@Autowired
	private Executor executor;
	
	public JsonResult urlRandomCreate(String tag, String mode, int dep) {
		TopApp app = apps.randomApp("mini", tag);
		if( app==null )
			return JsonResult.Err("暂无可用小程序");
		
		try {
			JSONObject json = WeixinApi.Mini.urlCreate(mode, app, app.getPath(), app.getQuery(), null, null, null);
			if( json.containsKey("url_link") )
				return JsonResult.Ok(json.getString("url_link"));
			return JsonResult.Ok(json.getString("openlink"));
		}catch (MpException e) {
			app.setState(0);
			app.setNote(e.toString());
			apps.addApp(app);
			
			if( app.getUrl()!=null )
				return JsonResult.Ok(app.getUrl());
			
			if( dep>=10 )
				return JsonResult.Err("暂无可用小程序");
			return urlRandomCreate(tag, mode, dep + 1);
		}
	}
	
	public JsonResult mpArticle(String appid, Integer offset) {
		if(offset==null)
			offset = 0;
		
		TopApp obj = apps.findApp(appid);
		if( obj==null )
			return JsonResult.Err("小程序未登记");
		if( obj.getMpid()==null ) {
			return JsonResult.Err("未绑定公众号");
		}
		
		final TopApp app = apps.findApp(obj.getMpid());
		if( app==null )
			return JsonResult.Err("公众号未登记");
		
		JSONArray array = FileCache.getJArray("mp.article." + app.getAppid() + "." + offset, 3600);
		if( (array==null || array.size()==0) && offset==0 ) {
			array = WeixinApi.Mp.articleArrayQuery(app, 0, 20, 1);
			if( array!=null && array.size()==20 ) {
				executor.execute(()->{
					for(int i=1;i<3;i++) {
						JSONArray list = WeixinApi.Mp.articleArrayQuery(app, i * 20, 20, 1);
						if(list==null || list.size()<20)
							return;
					}
				});
			}
		}
		
		return JsonResult.Ok(array);
	}
	
	public JsonResult weixinRequest(MpRequest obj) {
		return null;
	}
	
}
