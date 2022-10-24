package top.corz.mini.impl;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import top.corz.mini.entity.JsonResult;
import top.corz.mini.entity.TopUser;
import top.corz.mini.plugins.FileCache;
import top.corz.mini.utils.ApiHolder;
import top.corz.mini.utils.VerifyUtils;

@Component
public class UserComponent {

	public JsonResult login(TopUser user) {
		String err = VerifyUtils.verify(user, "username", "password");
		if(err!=null)
			return JsonResult.Err(err);
		
		TopUser has = FileCache.getObj("user." + user.getUsername().toLowerCase(), TopUser.class);
		if( has==null )
			return JsonResult.Err("账号不存在");
		if( !has.getPassword().equals(user.getPassword()) )
			return JsonResult.Err("密码错误");
		
		has.setToken(has.newToken());
		FileCache.set("user." + user.getUsername().toLowerCase(), JSONObject.toJSON(has));
		
		return JsonResult.Ok(has);
	}
	
	public JsonResult create(TopUser user) {
		String err = VerifyUtils.verify(user, "username", "password", "nickname");
		if(err!=null)
			return JsonResult.Err(err);
		if( ApiHolder.getUser().getLevel()<1 )
			return JsonResult.Err("无操作权限");
		
		if( FileCache.get("user." + user.getUsername().toLowerCase())!=null )
			return JsonResult.Err("账号已存在");
		
		FileCache.set("user." + user.getUsername().toLowerCase(), JSONObject.toJSON(user));
		return JsonResult.Ok();
	}
	
	public JsonResult modifyPass(String source, String target) {
		if( source==null || target==null )
			return JsonResult.Err("请输入原密码与新密码");
		if( !ApiHolder.getUser().getPassword().equals(source) )
			return JsonResult.Err("原密码错误");
		ApiHolder.getUser().setPassword(target);
		FileCache.set("user." + ApiHolder.getUser().getUsername().toLowerCase(), JSONObject.toJSON(ApiHolder.getUser()));
		return JsonResult.Ok();
	}
}
