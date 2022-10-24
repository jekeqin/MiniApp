package top.corz.mini.entity;

import com.alibaba.fastjson.JSONObject;

public class MpException extends RuntimeException {

	private static final long serialVersionUID = -5592624043067470403L;
	
	private String appid;
	
	private JSONObject ext;
	
	public MpException(String appid, JSONObject json) {
		this.appid = appid;
		this.ext = json;
	}
	
	public JSONObject get() {
		return this.ext;
	}
	
	@Override
	public String toString() {
		System.out.println(appid + "|" + this.ext.toJSONString());
		if( this.ext!=null )
			return this.ext.toJSONString();
		return super.toString();
	}
}
