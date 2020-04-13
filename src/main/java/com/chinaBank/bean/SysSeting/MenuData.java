package com.chinaBank.bean.SysSeting;

import lombok.Data;

@Data
public class MenuData {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id; //主键
	private String menu_id;//菜单标识
	private String menu_name;//菜单名称
	private String prev_id;//上级菜单
	
}
