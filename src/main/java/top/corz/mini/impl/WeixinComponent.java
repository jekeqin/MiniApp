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
import top.corz.mini.utils.EncryUtil;

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
	
	public JsonResult textCheck(String appid, String code, String openid, String text) {
		if( text==null || text.length()==0 || (code==null && openid==null) )
			return JsonResult.Ok();
		
		String md5 = EncryUtil.MD5.encrypt(text);
		JsonResult cache = FileCache.getObj("text.check." + md5, JsonResult.class);
		if( cache!=null )
			return cache;
		
		TopApp app = apps.findApp(appid);
		if( app==null )
			return JsonResult.Err("小程序未登记");

		if( openid==null || openid.length()==0 ) {
			JSONObject co = WeixinApi.Mini.openid(app, code);
			if( co!=null )
				openid = co.getString("openid");
			else
				return JsonResult.Err("校验失败");
		}

		JSONObject json = WeixinApi.Mini.textCheck(app, openid, text);
		if( json==null )
			return JsonResult.Err("校验失败");
			
		JsonResult back = JsonResult.OkExtra(null, text);
		if( json.containsKey("result") ) {
			json = json.getJSONObject("result");
			if( 100 != json.getInteger("label")  )
				back = JsonResult.Err("存在违规内容", text);
		}else {
			return JsonResult.Err(json.getString("errmsg"), text);
		}
		FileCache.set("text.check." + md5, back.toString());
		return back;
	}
	
	public JsonResult weixinRequest(MpRequest obj) {
		return null;
	}
	
}
