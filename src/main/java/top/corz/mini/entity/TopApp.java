package top.corz.mini.entity;

import lombok.Data;

@Data
public class TopApp {

	private String name;
	private String appid;
	private String secret;
	
	private String type = "mini";		// mini小程序，mp公众号
	
	private String mpid;				// 关联的公众号 appid
	
	private String path;				// url跳转路径
	private String query;				// url跳转参数
	
	private Integer state = 1;			// 1注册，0关闭
	
	private String remark;
	
	private String note;				// 状态变更备注
	
	private long update;				// 更新时间
	
}
