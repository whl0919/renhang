package com.chinaBank.bean.ExcelTask;

import lombok.Data;

@Data
public class ExcelTaskReportOrgData {

	private static final long serialVersionUID = 1L;
	private String id;
	private String startDate; //开始时间
	private String endDate; //结束时间
	private String reportType; //上传周期类型
	private String fileName;//文件
	private String deptName; //部门
	private String deptId; //部门
	private String adress; //下载模板地址
	private String path; //上传文件地址
	private String status;//机构上传状态
	private String org;//机构
	private String orgName;//机构
	private String date;//当前时间
	private String reportCycle; //上传类型
	private String paymentDay; //账期
	private String batch; //批次号
	private String model_id; //关联文件配置模板
	private String model_name;//关联文件配置模板
	private String create_date; //创建日期
	private String upUser; //上传人
	private String remark; //备注
	private String report_cycle_class; //自然日/工作日
	private String upType;//错误信息标识
	private String countY;//统计在审核的数量
	private String countN;//统计未审核的数量
	private String count;//上报的机构数量
	private String paymentDayTJ;//统计查询任务使用
}
