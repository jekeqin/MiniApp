package top.corz.mini.config;

import java.io.File;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UrlPathHelper;

import lombok.extern.slf4j.Slf4j;
import top.corz.mini.plugins.FileCache;

@Slf4j
@Configuration
public class WebConfigurer implements WebMvcConfigurer {
	
	private static final String[] extFolder = {"html", "files"};
	
	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		// 是否存在尾\来进行匹配  /user和/user/等效的，同样可以进行匹配
		configurer.setUseTrailingSlashMatch(true);
		
		// UrlPathHelper是一个处理url地址的帮助类
		// 就是将// 换成/  所以我们在输入地址栏的时候，//也是没有问题的
		configurer.setUrlPathHelper(new UrlPathHelper());
		
		// 增加前缀
		//configurer.addPathPrefix("/api/admin", HandlerTypePredicate.forAssignableType(AdminController.class));
		configurer.addPathPrefix("/api", HandlerTypePredicate.forAnnotation(Controller.class));
		
		WebMvcConfigurer.super.configurePathMatch(configurer);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		for(String f:extFolder) {
			String folder = FileCache.staticFolder(f);
			if(folder!=null) {
				registry.addResourceHandler("/"+f+"/**").addResourceLocations("file:" + folder);
			}
			log.info(folder);
		}
		registry.addResourceHandler("/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + File.separator + "static" + File.separator);

		WebMvcConfigurer.super.addResourceHandlers(registry);
	}
	
	@Bean
	public WebInterceptor interceptor() {
		return new WebInterceptor();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor())
        	.addPathPatterns("/**/*.web")
			.addPathPatterns("/**/*.do")
			.addPathPatterns("/**/*.dev")
			.addPathPatterns("/**/**")
			
			.excludePathPatterns("/**/*.json")
			.excludePathPatterns("/error")
			.excludePathPatterns("/static/**")
			.excludePathPatterns("/html/**")
			.excludePathPatterns("/images/**")
			;
	}
	
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOriginPatterns("*")
			.allowCredentials(true)
			.allowedMethods("GET", "POST", "HEAD", "OPTIONS", "PATCH", "PUT")
			.maxAge(3600)
			.allowedHeaders("*")
			;
	}
	
}
