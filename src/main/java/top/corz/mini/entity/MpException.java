package top.corz.mini.entity;

import com.alibaba.fastjson.JSONObject;

public class MpException extends RuntimeException {

	private static final long serialVersionUID = -5592624043067470403L;
	
	private JSONObject ext;
	
	public MpException(JSONObject json) {
		this.ext = json;
	}
	
	public JSONObject get() {
		return this.ext;
	}
	
	@Override
	public String toString() {
		if( this.ext!=null )
			return this.ext.toJSONString();
		return super.toString();
	}
}
