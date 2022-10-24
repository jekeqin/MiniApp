package top.corz.mini.config;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import top.corz.mini.entity.JsonResult;

@Slf4j
@RestControllerAdvice
public class ExceptionResolver {

	/**
	 * RequestHeader 未传
	 * @param e
	 * @return
	 */
	@ExceptionHandler(MissingRequestHeaderException.class)
	public JsonResult headerMissing(MissingRequestHeaderException e)
	{
		return JsonResult.Err("Missing Header: " + e.getHeaderName());
	}
	
	/**
	 * RequestBody 参数未传	
	 * @param req
	 * @param e
	 * @return
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public JsonResult bodyMissing(HttpServletRequest req, HttpMessageNotReadableException e)
	{
		log.warn("warn:{}, {}", req.getRequestURI(), e);
		return JsonResult.Err("Missing Request Body", req.getHeader("Content-Type"));
	}
	
	/**
	 * 不支持的 Content-Type
	 * @param req
	 * @param e
	 * @return
	 */
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public JsonResult contentTypeError(HttpServletRequest req, HttpMediaTypeNotSupportedException e) {
		return JsonResult.Err("Content-Type not supported", req.getHeader("Content-Type"));
	}
	
	/**
	 * 空指针异常拦截处理
	 * @param req
	 * @param e
	 * @return
	 */
	@ExceptionHandler(NullPointerException.class)
	public JsonResult nullException(HttpServletRequest req, NullPointerException e)
	{
		log.error("err:{}, {}", req.getRequestURI(), e);
		return JsonResult.Err("Server NULL Exception", req.getRequestURI());
	}
	
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public JsonResult methodNotSupportedException(HttpServletRequest req, HttpRequestMethodNotSupportedException e){
		return JsonResult.Err("method '"+ req.getMethod() +"' not supported", req.getRequestURI());
	}
	
	@ExceptionHandler(FileSizeLimitExceededException.class)
	public JsonResult fileSizeLimitExceededException(HttpServletRequest req, HttpRequestMethodNotSupportedException e) {
		return JsonResult.Err("文件大小超过限制");
	}
	
	@ExceptionHandler(ClassCastException.class)
	public JsonResult classCastException(HttpServletRequest req, ClassCastException e) {
		log.error("err:{}, {}", req.getRequestURI(), e);
		return JsonResult.Err("Server Error");
	}
	
	@ExceptionHandler(JsonException.class)
	public JsonResult runtimeException(HttpServletRequest req, JsonException e) {
		log.warn("warn:{}, :{}", req.getRequestURI(), e.getJson());
		return e.getJson();
	}
}
