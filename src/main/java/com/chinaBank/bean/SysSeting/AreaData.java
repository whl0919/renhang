package com.chinaBank.bean.SysSeting;

import java.io.Serializable;

import lombok.Data;

@Data
public class AreaData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String value; //地区码
	private String label;  //地区值
	private Object children; //下级机构
}
