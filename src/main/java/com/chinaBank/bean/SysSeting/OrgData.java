package com.chinaBank.bean.SysSeting;

import java.io.Serializable;

import lombok.Data;

@Data
public class OrgData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String BANK_ID; //机构主键
	private String BANKCODE; //机构码
	private String BANK; //机构名称
	private String BANKTYPE; //机构类型
	private String SUPERIORBANKCODE; //上级机构码
	private String AREA; //地区
	private String BELONGPBC; //所属人行代码
	private String ADDRESS; //地址
	private String AREACODE; //地区码
	private String CONTACTPERSON; //联系人
	private String CONTACPHONE; //联系人电话
	private String BANKNAME; //银行名称
	private String BANKCATEGORY; //银行类别
	private String batch; //只用于任务下发关联机构
	private String notice_id; //只用于通知下发关联机构
}
