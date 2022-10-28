package top.corz.mini.utils;

import lombok.extern.slf4j.Slf4j;
import top.corz.mini.entity.TopUser;

@Slf4j
public class ApiHolder {

	private static final ThreadLocal<TopUser> _user = new ThreadLocal<>();
	private static final ThreadLocal<String[]> _token = new ThreadLocal<>();
	private static final ThreadLocal<String> _appid = new ThreadLocal<>();
	
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
	
	public static final void setAppid(String appid) {
		_appid.set(appid);
	}
	public static final String getAppid() {
		return _appid.get();
	}
	
	public static final void clear() {
		_user.remove();
		_token.remove();
		_appid.remove();
	}
}
