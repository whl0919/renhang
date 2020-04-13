package com.chinaBank.bean.SysSeting;

import java.io.Serializable;

import lombok.Data;

@Data
public class RoleData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String role_id; //主键
	private String role_name; //角色名称
	private String role_desc; //角色描述
	private String role_status; //是否启用
	private String menu_id;//菜单
	private String menu_name;//菜单
	private String create_user;
	private String update_user;
	private String prev_id;//上级菜单

}
