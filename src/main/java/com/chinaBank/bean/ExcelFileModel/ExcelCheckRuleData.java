package com.chinaBank.bean.ExcelFileModel;

import java.io.Serializable;

import lombok.Data;

@Data
public class ExcelCheckRuleData implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;  //主键
	private String check_type;  //校验类型（硬性Y/软性N）
	private String check_class;  //校验类别(计算/正则/对比/sql)
	private String check_program;  //程序校验（是Y/否N）
	private String status;  //是否有效（是Y/否N）
	private String check_rule; //校验规则
	private String error_info;  //错误提示消息
	private String model_id;  //关联模板
	private String table_id;  //关联表
	private String create_user;  //创建人
	private String update_user;  //更新人
	private String check_desc;//规则描述
	private String public_type;//是否是公共规则所关联过来的规则（是Y/否N）
	private String table_name;  //关联表
	private String[] public_ids; //公共规则id
	private String dict_type_id;//字典类型（只用于创建字典规则）
	private String col;//应用列（只用于创建字典规则）

}
