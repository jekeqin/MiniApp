package top.corz.mini.plugins;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;
import top.corz.mini.entity.MpException;

@Slf4j
public class WeixinApi {

	private static HttpApi http = new HttpApi();
	
	
	public static class Mini{
		
		/**
		 * 小程序，生成 urlScheme 或 urlLink
		 * @param mode			scheme 或 link，默认scheme
		 * @param accessToken	小程序令牌
		 * @param path			页面路径
		 * @param query			页面参数，可传null
		 * @param env			环境，默认release正式，trial体验，develop开发，可传null
		 * @param expireType	0指定时间失效，1指定天数后失效
		 * @param timeout		type=0时为失效时间秒数，type=1时为天数，最大30天
		 * @return
		 */
		public static final JSONObject urlCreate(String mode, String accessToken, String path, String query, String env, Integer expireType, Integer timeout) {
			String url = "https://api.weixin.qq.com/wxa/" + ("link".equalsIgnoreCase(mode)?"generate_urllink":"generatescheme") + "?access_token=" + accessToken;
			JSONObject data = new JSONObject();
			if( expireType==null || expireType!=0 ) {
				if( timeout==null )
					timeout = 1;
				data.fluentPut("expire_type", 1).fluentPut("expire_interval", timeout);
			}else{
				if( timeout==null )
					timeout = (int) (System.currentTimeMillis()/1000 + 1440);
				data.fluentPut("expire_type", 0).fluentPut("expire_time", timeout);
			}
			
			JSONObject wxa = new JSONObject().fluentPut("path", path);
			if( query!=null )
				wxa.put("query", query);
			if( env!=null )
				wxa.put("env_version", env);
			
			if( "link".equalsIgnoreCase(mode) ) {
				data.putAll(wxa);
			}else {
				data.put("jump_wxa", wxa);
			}
			
			JSONObject json = http.httpPost(url, data.toJSONString());
			if( json==null || json.getIntValue("errcode")!=0 )
				throw new MpException(accessToken, json);
			return json;
		}
		
	}
	
	public static class Mp{
		/**
		 * 获取二维码
		 * @param accessToken
		 * @param actionName		二维码类型，QR_SCENE为临时的整型参数值，QR_STR_SCENE为临时的字符串参数值，QR_LIMIT_SCENE为永久的整型参数值，QR_LIMIT_STR_SCENE为永久的字符串参数值
		 * @param expireSeconds		该二维码有效时间，以秒为单位。 最大不超过2592000（即30天），此字段如果不填，则默认有效期为60秒。
		 * @param sceneStr			场景值ID（字符串形式的ID），字符串类型，长度限制为1到64
		 * @param sceneId			场景值ID，临时二维码时为32位非0整型，永久二维码时最大值为100000（目前参数只支持1--100000）
		 * @return
		 */
		public static final JSONObject createQrcode(String accessToken, JSONObject data)
		{
			JSONObject json = http.httpPost("https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + accessToken , data.toJSONString());
			if( json.containsKey("ticket") )
				return json;
			
			return null;
		}
		
		public static JSONObject articleQuery(String accessToken, Integer offset, Integer count, Integer no_content) {
			String url = "https://api.weixin.qq.com/cgi-bin/freepublish/batchget?access_token=" + accessToken;
			JSONObject data = new JSONObject().fluentPut("offset", offset).fluentPut("count", count).fluentPut("no_content", no_content!=null?no_content:1);
			return http.httpPost(url, data.toJSONString());
		}
		
		public static JSONArray articleArrayQuery(String appid, String accessToken, Integer offset, Integer count, Integer no_content) {
			JSONObject json = articleQuery(accessToken, offset, count, no_content);
			log.info("article: {}", json);
			if( json==null ) {
				FileCache.del("mp.article." + appid + "." + offset);
				return null;
			}
			if( !json.containsKey("item") ) {
				FileCache.del("mp.article." + appid + "." + offset);
				return null;
			}
			
			JSONArray items = new JSONArray();
			JSONArray array = json.getJSONArray("item");
			array.forEach(ai->{
				JSONArray nis = ((JSONObject)ai).getJSONObject("content").getJSONArray("news_item");
				items.addAll(nis);
			});
			if( items!=null && items.size()>0 ) {
				FileCache.set("mp.article." + appid + "." + offset, items.toJSONString());
			}
			
			return items;
		}
	}

	public static final String AccessToken(String appid, String secret) {
		String token = FileCache.get("mp.token." + appid, 7100000);
		if( token!=null )
			return token;
		
		String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" +appid+ "&secret=" + secret;
		JSONObject json = http.httpGet(url);
		if( json!=null && json.containsKey("access_token") ) {
			token = json.getString("access_token");
			FileCache.set("mp.token." + appid, token);
		}else {
			throw new MpException(appid, json);
		}
		return token;
	}
	
	public JSONObject jscodeToSession(String appid, String secret, String code)
	{
		String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appid + "&secret=" + secret + "&js_code=" + code + "&grant_type=authorization_code";
		return http.httpGet(url);
	}
	
	public static final JSONObject post(String accessToken, String url, JSONObject data) {
		if( url.endsWith("access_token=") )
			url += accessToken;
		return http.httpPost(url, data.toJSONString());
	}
	
	public static final JSONObject get(String accessToken, String url) {
		if( url.endsWith("access_token=") )
			url += accessToken;
		return http.httpGet(url);
	}
}
