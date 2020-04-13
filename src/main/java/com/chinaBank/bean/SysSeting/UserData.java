package com.chinaBank.bean.SysSeting;

import java.io.Serializable;

import lombok.Data;

@Data
public class UserData implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String user_id; //用户账号
	private String user_name; //用户名
	private String org_id; //机构
	private String org_name; //机构
	private String role_id; //角色id
	private String role_name; //角色
	private String user_psd; //密码
	private String user_type; //是否是人行用户（人行Y/非人行N）
	private String user_status; //状态启用Y/不启用N
	private String dept_name;//部门名称
	private String dept_id;//部门id
	private String create_user;//创建人
	private String update_user;//更新人
	private String login_id; //登录账号
}
