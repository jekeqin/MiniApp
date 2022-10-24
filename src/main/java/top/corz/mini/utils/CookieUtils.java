package top.corz.mini.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtils {

	public static void addCookie(HttpServletResponse resp, String key, String value, Integer expire) {
		Cookie obj = new Cookie(key, value);
		obj.setPath("/");
		if( expire!=null )
			obj.setMaxAge( expire );
		resp.addCookie(obj);
	}
	
	public static String getCookie(HttpServletRequest req, String key) {
		if( req==null || key==null )
			return null;
		Cookie[] array = req.getCookies();
		if( array==null || array.length==0 )
			return null;
		for(Cookie obj : array) {
			if( obj.getName().equalsIgnoreCase(key) )
				return obj.getValue();
		}
		return null;
	}
}
