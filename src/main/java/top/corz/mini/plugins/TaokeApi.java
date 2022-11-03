package top.corz.mini.plugins;

import java.util.Arrays;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import top.corz.mini.entity.JsonResult;
import top.corz.mini.utils.ApiHolder;
import top.corz.mini.utils.EncryUtil;
import top.corz.mini.utils.TimeUtils;

@Component
public class TaokeApi {
	// https://open.taobao.com/api.htm?docId=24518&docType=2
	// https://market.m.taobao.com/app/qn/toutiao-new/index-pc.html#/detail/10628875?_k=gpov9a
	
	private static HttpApi http = new HttpApi();
	
	@Value("${taoke.key}")
	private String taokeApiKey;
	@Value("${taoke.secret}")
	private String taokeSecret;
	
	@Value("${taoke.adzone.wxapp}")
	private String adzoneWxapp;
	@Value("${taoke.adzone.html}")
	private String adzoneHtml;
	
	public JsonResult category() {
		JSONArray array = FileCache.getJArray("taoke.item.cats", 86400);
		if( array!=null && array.size()>0 )
			return JsonResult.Ok(array);
		
		JSONObject data = new JSONObject().fluentPut("parent_cid", 0);
		JSONObject json = request(taokeApiKey, taokeSecret, "taobao.itemcats.get", data);
		if( json.containsKey("item_cats") ) {
			array = json.getJSONArray("item_cats");
			FileCache.set("taoke.item.cats", array.toJSONString());
			return JsonResult.Ok(array);
		}
		return JsonResult.ErrExtra(json);
	}
	
	public JsonResult optimusQuery(JSONObject obj) {
		obj.fluentPut("adzone_id", adzone());
		JSONObject json = request(taokeApiKey, taokeSecret, "taobao.tbk.dg.optimus.material", obj);
		if( json.containsKey("result_list") ) {
			return JsonResult.Ok(json.getJSONArray("result_list"));
		}
		return JsonResult.ErrExtra(json);
	}
	
	public JSONObject optionalQuery() {
		JSONObject obj = new JSONObject().fluentPut("adzone_id", adzone());
		return request(taokeApiKey, taokeSecret, "taobao.tbk.dg.material.optional", obj);
	}
	
	public JSONObject getActivity(String materialId) {
		JSONObject obj = new JSONObject().fluentPut("adzone_id", adzone()).fluentPut("activity_material_id", materialId);
		return request(taokeApiKey, taokeSecret, "taobao.tbk.activity.info.get", obj);
	}
	
	private String adzone() {
		if( ApiHolder.mapString("ua").equalsIgnoreCase("weixin") ) {
			return adzoneWxapp;
		}
		return adzoneHtml;
	}
	
	private static final JSONObject request(String apiKey, String apiSecret, String method, JSONObject obj) {
		sign(apiKey, apiSecret, obj, method);
		JSONObject json = http.httpPostForm("https://eco.taobao.com/router/rest", obj);
		return json;
	}

	private static final void sign(String apiKey, String apiSecret, JSONObject obj, String method) {
		obj.putAll(baseJson(apiKey, method));
		String[] keys = obj.keySet().toArray(new String[0]);
		Arrays.sort(keys);
		
		StringBuffer buff = new StringBuffer();
		buff.append(apiSecret);
		for (String k:keys) {
			String v = obj.getString(k);
			if( v!=null && v.length()>0 )
				buff.append(k).append(obj.getString(k));
			else
				obj.remove(k);
		}
		buff.append(apiSecret);
		
		obj.put("sign", EncryUtil.MD5.encrypt(buff.toString()));
	}
	
	private static final JSONObject baseJson(String apiKey, String method) {
		JSONObject obj = new JSONObject();
		obj.put("app_key", apiKey);
		obj.put("format", "json");
		obj.put("method", method);
        obj.put("timestamp", TimeUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        obj.put("v", "2.0");
        obj.put("sign_method", "md5");
        obj.put("simplify", true);
        return obj;
	}
	
}
