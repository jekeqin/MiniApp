

package top.corz.mini.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import top.corz.mini.entity.JsonResult;
import top.corz.mini.entity.TopUser;
import top.corz.mini.plugins.FileCache;
import top.corz.mini.utils.ApiHolder;

@Aspect
@Component
public class AopAuthorize {

    //@Pointcut("execution(* we.web.controller.*.*(..))")
	@Pointcut("@annotation(top.corz.mini.entity.Authorize)")
	private void controller() { }
    
    @Around("controller()")
    public Object doAround(ProceedingJoinPoint join) throws Throwable
    {
		if( ApiHolder.getToken()==null )
			return JsonResult.Err(JsonResult.Code.AUTH);
    	
		TopUser user = FileCache.getObj("user." + ApiHolder.getToken()[0], TopUser.class);
		if( user!=null && user.getToken().equals(String.join("|", ApiHolder.getToken())) )
			ApiHolder.setUser(user);
		else {
			return JsonResult.Err(JsonResult.Code.AUTH);
		}
		
		Object obj = join.proceed();
		
		return obj;
    }
}
