package com.chinaBank.bean.ExcelFileModel;

import java.io.Serializable;

import lombok.Data;

@Data
public class ExcelBaseColData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id; //主键
	private String column_name; //列名
	private String column_desc; //列描述
	private String read_col; //读取列
	private String column_type; //列类型
	private String column_length;  //列长度
	private String column_Decimal;//列精度
	private String index_type; //是否是索引字段
	private String table_id; //关联表ID
	private String table_desc; //关联表描述
	private String table_name; //关联表
	private String isNull; //是否为空
	private String create_user; //创建者
	private String update_user; //更新者
	private int index;//用于动态存数
	
}
