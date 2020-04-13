package com.chinaBank.service.ExcelTask;

import java.util.List;

import com.chinaBank.bean.ExcelTask.ExcelNoticeData;
import com.chinaBank.bean.ExcelTask.ExcelTaskData;
import com.chinaBank.bean.ExcelTask.ExcelTaskOrgData;
import com.chinaBank.bean.ExcelTask.ExcelTaskReportOrgData;
import com.chinaBank.bean.SysSeting.OrgData;
import com.github.pagehelper.PageInfo;

public interface ExcelTaskService {
	
	/**
	 * 添加上传任务
	 */
	String addExcelTask(ExcelTaskData excelTaskData);

	/**
	 * 更新上传任务
	 */
	String updateExcelTask(ExcelTaskData excelTaskData);

	/**
	 * 删除上传任务
	 */
	String deleteExcelTask(String id);
	
	/**
	 * 批次号删除上传任务
	 */
	String deleteExcelTaskByBatch(String batch,String status);

	/**
	 * ID查询上传任务
	 */
	ExcelTaskData selectExcelTaskById(String id);
	
	/**
	 * 查询所有上传任务
	 */
    PageInfo<ExcelTaskData> selectExcelTask(int pageNum, int pageSize,ExcelTaskData excelTaskData);
    
	/**
	 *查询上传任务
	 */
	ExcelTaskData selectExcelTaskByModelOrOrg(String file_name,String status,String report_cycle,String report_date);
	
	/**
	 * 关联机构
	 */
	String addExcelTaskOrg(ExcelTaskOrgData excelTaskOrgData);
	/**
	 * 通过批次号查询关联机构
	 */
	List<String> selectExcelTaskOrgByBatch(String batch);
	
	/**
	 * 更新任务机构表
	 */
	String updateExcelTaskOrg(String batch);
	
	/**
	 * 查询机构上传任务
	 */
    List<ExcelTaskReportOrgData> selectExcelTaskReportOrg(ExcelTaskReportOrgData excelTaskReportOrgData);
	/**
	 * 添加下发 任务详细信息
	 */
    String addExcelTaskReportOrg(ExcelTaskReportOrgData excelTaskReportOrgData);
	/**
	 * 查询是否有当前任务
	 */
    ExcelTaskReportOrgData selectExcelTaskReport(String file_name,String date,String report_cycle,String report_date,String org);
	/**
	 * 查询机构下任务
	 */
    List<ExcelTaskReportOrgData> selectExcelTaskReportByOrg(String fileName,String org,String paymentDay,String status,String batch,String paymentDayTJ);
    
	/**
	 * 统计查询机构上传情况
	 */
    List<ExcelTaskReportOrgData> selectExcelTaskReportByCount(String batch,String paymentDay);
	/**
	 * 查询机构下任务
	 */
    ExcelTaskReportOrgData selectExcelTaskReportById(String id,String status);
	/**
	 * 更新任务
	 */
    String updateExcelTaskReportByOrg(String id,String path,String status,String upUser);
	/**
	 * 查询所有下发任务的机构
	 */
    List<String> selectExcelTaskOrgs();
	/**
	 * 查询所有下发任务的机构
	 */
    List<OrgData> selectExcelTaskCheckOrgByBatch(OrgData orgData);
	/**
	 * 管理员审核
	 */
    String updateExcelTaskReportRemark(String id,String remark,String status);
	/**
	 * 更新任务，只用于历史任务更新使用
	 */
    String updateExcelTaskReportByTask(String batch,String adress,String startDate);
	/**
	 * 增加通知
	 */
	String addExcelNotice(ExcelNoticeData ExcelNoticeData);
	/**
	 * 通知关联机构
	 */
	String addExcelOrgNotice(ExcelNoticeData ExcelNoticeData);
	/**
	 * 查询通知(管理员使用)
	 */
    List<ExcelNoticeData> selectExcelNotice(String title,String status);
	/**
	 * 更新通知
	 */
    String updateExcelNotice(ExcelNoticeData ExcelNoticeData);
	/**
	 * 删除通知
	 */
    String deleteExcelNotice(String id);
	/**
	 * 查询通知（机构使用）
	 */
    List<ExcelNoticeData> selectExcelNoticeByOrg(String title,String org);
	/**
	 * 更新通知关联机构状态
	 */
	String updateExcelOrgNotice(String notice_id);
	/**
	 * 查询通知
	 */
    List<String> selectExcelNoticeByNoticeId(String notice_id);
	/**
	 * 查询通知关联机构
	 */
    List<OrgData> selectExcelNoticeOrgByNoticeId(OrgData orgData);
    
	/**
	 * 更新任务状态
	 */
    String batchAuditUpload(String id,String status);

}
