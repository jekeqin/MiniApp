package top.corz.mini.entity;

import lombok.Data;
import top.corz.mini.utils.EncryUtil;
import top.corz.mini.utils.VerifyUtils.VerifyMeta;

@Data
public class TopUser {

	@VerifyMeta(msg = "请输入登录账号")
	private String username;
	
	@VerifyMeta(msg = "请输入登录密码")
	private String password;
	
	private String nickname;
	
	private String avatar;
	
	private String email;
	
	private String mobile;
	
	private Integer level = 0;
	
	private Integer state = 1;
	
	private String token;
	
	public String newToken() {
		return username + "|" +  EncryUtil.MD5.encrypt(username + System.currentTimeMillis());
	}
}
