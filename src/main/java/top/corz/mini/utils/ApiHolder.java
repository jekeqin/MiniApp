package top.corz.mini.utils;

import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;
import top.corz.mini.entity.TopUser;

@Slf4j
public class ApiHolder {

	private static final ThreadLocal<TopUser> _user = new ThreadLocal<>();
	private static final ThreadLocal<String[]> _token = new ThreadLocal<>();
	private static final ThreadLocal<JSONObject> _map = new ThreadLocal<>();
	
	public static final void setUser(TopUser obj) {
		_user.set(obj);
	}
	
	public static final TopUser getUser() {
		return _user.get();
	}
	
	public static final void setToken(String token) {
		if( token==null || token.length()==0 )
			return;

		String[] array = token.split("\\|");
		log.debug(token);
		_token.set(array);
	}
	
	public static final String[] getToken() {
		return _token.get();
	}

	public static final void setMap(String key, String value) {
		JSONObject map = _map.get();
		if( map==null )
			map = new JSONObject();
		map.put(key, value);
		_map.set(map);
	}
	public static final String getAppid() {
		JSONObject map = _map.get();
		if( map!=null )
			return map.getString("appid");
		return null;
	}
	
	public static final JSONObject getMap() {
		return _map.get();
	}
	
	public static final String mapString(String key) {
		JSONObject map = _map.get();
		if( map==null )
			return null;
		return map.getString(key);
	}
	
	public static final void clear() {
		_user.remove();
		_token.remove();
		_map.remove();
	}
}
