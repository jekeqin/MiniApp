package top.corz.mini.config;

import top.corz.mini.entity.JsonResult;

public class JsonException extends RuntimeException {

	private static final long serialVersionUID = 6131788046181710111L;

	private JsonResult json;
	
	public JsonException(JsonResult json) {
		this.json = json;
	}
	
	public JsonResult getJson() {
		return json;
	}
}
