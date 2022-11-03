package top.corz.mini.config;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import lombok.extern.slf4j.Slf4j;
import top.corz.mini.entity.JsonResult;
import top.corz.mini.utils.ApiHolder;
import top.corz.mini.utils.CookieUtils;

@Slf4j
public class WebInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		String token = request.getHeader("token");
		if( token==null )
			token = CookieUtils.getCookie(request, "token");
		
		ApiHolder.setToken(token);
		ApiHolder.setMap("appid", request.getHeader("appid"));
		ApiHolder.setMap("ua", UA(request.getHeader("User-Agent")));
		log.info(request.getRequestURI() + " # {}", ApiHolder.getMap().toJSONString());
		return HandlerInterceptor.super.preHandle(request, response, handler);
	}
	
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
		
		ApiHolder.clear();
	}
	
	protected boolean response(HttpServletResponse response, String msg) throws IOException {
		response.setStatus(HttpStatus.SC_OK);
		response.setContentType("application/json;charset=UTF-8");
		
		response.getWriter().append(JsonResult.Err(msg).toString());
		
		return false;
	}
	
	private String UA(String userAgent) {
		if( userAgent==null || userAgent.length()==0 )
			return "unknow";
		if( userAgent.toLowerCase().indexOf("micromessenger")>0 )
			return "weixin";
		return "html";
	}
}
