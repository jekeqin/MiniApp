package top.corz.mini.entity;

import com.alibaba.fastjson.JSONObject;

import lombok.Data;

@Data
public class MpRequest {

	private String appid;
	
	private String url;
	
	private JSONObject data;
	
	private String mode;
	
}
