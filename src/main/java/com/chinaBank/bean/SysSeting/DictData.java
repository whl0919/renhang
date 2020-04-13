package com.chinaBank.bean.SysSeting;

import lombok.Data;

@Data
public class DictData {

	private String id; //主键
	private String dict_type; //字典类型
	private String dict_type_value;//字典类型值
	private String dict_id;//字典id
	private String dict_value;//字典值
	private String create_user;//创建人
	private String update_user;//更新人
	private String status;//是否有效
	private String dict_type_id;//字典类型id
	private String create_date;//创建时间
	private String update_date;//更新时间
	
}
