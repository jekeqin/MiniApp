package top.corz.mini.entity;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class JsonResult {

	private int code;
	private String msg;
	private Object data;
	private Object extra;
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public Object getExtra() {
		return extra;
	}
	public void setExtra(Object extra) {
		this.extra = extra;
	}

	public JsonResult(int code, String msg, Object data, Object extra) {
		this.code = code;
		this.msg = msg;
		this.data = data;
		this.extra = extra;
	}
	
	public static final JsonResult Ok() {
		return new JsonResult(Code.TRUE.value(), Code.TRUE.msg(), null, null);
	}
	
	public static final JsonResult Ok(Object data) {
		return new JsonResult(Code.TRUE.value(), Code.TRUE.msg(), data, null);
	}

	public static final JsonResult Ok(Object data,String msg, Object extra) {
		return new JsonResult(Code.TRUE.value(), msg, data, extra);
	}
	
	public static final JsonResult OkExtra(Object data, Object extra) {
		return new JsonResult(Code.TRUE.value(), Code.TRUE.msg(), data, extra);
	}
	
	public static final JsonResult OkMsg(Object data, String msg) {
		return new JsonResult(Code.TRUE.value(), msg, data, null);
	}
	
	public static final JsonResult Err(String msg) {
		return new JsonResult(Code.FAIL.value(), msg, null, null);
	}
	
	public static final JsonResult Err(Code code) {
		return new JsonResult(code.value(), code.msg(), null, null);
	}
	
	public static final JsonResult Err(String msg, Object extra) {
		return new JsonResult(Code.FAIL.value(), msg, null, extra);
	}
	
	public static final JsonResult Err(int code, String msg) {
		return new JsonResult(code, msg, null, null);
	}
	
	public static final JsonResult Err(int code, String msg, Object extra) {
		return new JsonResult(code, msg, null, extra);
	}
	
	public static final JsonResult ErrExtra(Object extra) {
		return new JsonResult(Code.FAIL.value(), Code.FAIL.msg(), null, extra);
	}
	
	@JsonIgnore
	public boolean isOk() {
		return this.code == Code.TRUE.value();
	}
	
	@Override
	public String toString() {
		return JSONObject.toJSONString(this);
	}
	
	public static enum Code{
		TRUE(200, "操作成功"),
		AUTH(403, "请登录"),
		FAIL(501, "操作失败");
		
		private int value;
		private String msg;
		
		private Code(int value, String msg) {
			this.value = value;
			this.msg = msg;
		}
		
		public int value() {
			return this.value;
		}
		
		public String msg() {
			return this.msg;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}
	}
	
}
