package top.corz.mini.entity;

import lombok.Data;
import top.corz.mini.utils.VerifyUtils.VerifyMeta;

@Data
public class TopApp {

	@VerifyMeta(msg = "名称")
	private String name;
	@VerifyMeta(msg = "APPID")
	private String appid;
	@VerifyMeta(msg = "SECRET")
	private String secret;
	
	private String tag;					// 标签，用于分组
	
	private String type = "mini";		// mini小程序，mp公众号
	
	private String mpid;				// 关联的公众号 appid
	
	private String path;				// url跳转路径
	private String query;				// url跳转参数
	
	private Integer state = 1;			// 1注册，0关闭
	
	private String remark;
	
	private String note;				// 状态变更备注
	
	private long update;				// 更新时间
	
	private String url;				// 小程序，补充链接，小程序url无法生成的情况下使用
	
	public boolean tagCompare(String _tag) {
		if( _tag==null || this.tag==null )
			return true;
		return _tag!=null && _tag.equalsIgnoreCase(this.tag);
	}
}
