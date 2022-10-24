package top.corz.mini.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import top.corz.mini.utils.EncryUtil;

@Data
public class TopUser {

	private String username;
	
	@JsonIgnore
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
