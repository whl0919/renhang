package com.chinaBank.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.chinaBank.bean.ExcelTask.ExcelNoticeData;
import com.chinaBank.bean.ExcelTask.ExcelTaskData;
import com.chinaBank.bean.ExcelTask.ExcelTaskOrgData;
import com.chinaBank.bean.ExcelTask.ExcelTaskReportOrgData;
import com.chinaBank.bean.SysSeting.OrgData;
import com.github.pagehelper.PageInfo;

public interface ExcelTaskMapper {

	/**
	 * 添加上传任务
	 */
	void addExcelTask(ExcelTaskData excelTaskData);

	/**
	 * 更新上传任务
	 */
	void updateExcelTask(ExcelTaskData excelTaskData);

	/**
	 * 删除上传任务
	 */
	void deleteExcelTask(String id);
	
	/**
	 * 批次号删除上传任务
	 */
	void deleteExcelTaskByBatch(@Param(value = "batch") String batch,@Param(value = "status") String status);

	/**
	 * 通过ID查询上传任务
	 */
	ExcelTaskData selectExcelTaskById(String id);
	
	/**
	 * 查询所有上传任务
	 */
	List<ExcelTaskData> selectExcelTask(ExcelTaskData excelTaskData);
	
	/**
	 *查询上传任务
	 */
	ExcelTaskData selectExcelTaskByModelOrOrg(
			@Param(value = "file_name") String file_name,
			@Param(value = "status") String status,
			@Param(value = "report_cycle") String report_cycle,
			@Param(value = "report_date") String report_date);

	/**
	 * 关联机构
	 */
	void addExcelTaskOrg(ExcelTaskOrgData excelTaskOrgData);
	
	List<String> selectExcelTaskOrgByBatch(String batch);
	
	void updateExcelTaskOrg(String batch);
	
	/**
	 * 查询机构上传任务
	 */
    List<ExcelTaskReportOrgData> selectExcelTaskReportOrg(ExcelTaskReportOrgData excelTaskReportOrgData);
    
    void addExcelTaskReportOrg(ExcelTaskReportOrgData excelTaskReportOrgData);
    
    ExcelTaskReportOrgData selectExcelTaskReport(@Param(value = "file_name") String file_name,
    		@Param(value = "date") String date,
    		@Param(value = "report_cycle") String report_cycle,
    		@Param(value = "report_date") String report_date,
    		@Param(value = "org") String org);
    
    List<ExcelTaskReportOrgData> selectExcelTaskReportByOrg(
    		@Param(value = "fileName") String fileName,
    		@Param(value = "org") String org,
			@Param(value = "paymentDay") String paymentDay,
			@Param(value = "status") String status,
			@Param(value = "batch") String batch,
			@Param(value = "paymentDayTJ") String paymentDayTJ);
    
    void updateExcelTaskReportByOrg(@Param(value = "id") String id,
    		@Param(value = "path") String path,
    		@Param(value = "status") String status,
    		@Param(value = "upUser") String upUser);
	/**
	 * 查询机构下任务
	 */
    ExcelTaskReportOrgData selectExcelTaskReportById(@Param(value = "id") String id,@Param(value = "status") String status);
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
    void updateExcelTaskReportRemark(@Param(value = "id") String id,
    		@Param(value = "remark") String remark,
    		@Param(value = "status") String status);
	/**
	 * 更新任务
	 */
    void updateExcelTaskReportByTask(@Param(value = "batch") String batch,
    		@Param(value = "adress") String adress,
    		@Param(value = "startDate") String startDate);
	/**
	 * 增加通知
	 */
	void addExcelNotice(ExcelNoticeData ExcelNoticeData);
	/**
	 * 通知关联机构
	 */
	void addExcelOrgNotice(ExcelNoticeData ExcelNoticeData);
	/**
	 * 查询通知
	 */
    List<ExcelNoticeData> selectExcelNotice(@Param(value = "title") String title, @Param(value = "status") String status);
	/**
	 * 更新通知
	 */
    void updateExcelNotice(ExcelNoticeData ExcelNoticeData);
	/**
	 * 删除通知
	 */
    void deleteExcelNotice(String id);
	/**
	 * 查询通知（机构使用）
	 */
    List<ExcelNoticeData> selectExcelNoticeByOrg(@Param(value = "title") String title,
    		@Param(value = "org") String org);
	/**
	 * 更新通知关联机构状态
	 */
	void updateExcelOrgNotice(String notice_id);
	/**
	 * 查询通知
	 */
    List<String> selectExcelNoticeByNoticeId(String notice_id);
	/**
	 * 查询通知关联机构
	 */
    List<OrgData> selectExcelNoticeOrgByNoticeId(OrgData orgData);
    
    List<ExcelTaskReportOrgData> selectExcelTaskReportByCount(@Param(value = "batch") String batch, @Param(value = "paymentDay") String paymentDay);
    
    void batchAuditUpload(@Param(value = "id") String id,@Param(value = "status") String status);
}
