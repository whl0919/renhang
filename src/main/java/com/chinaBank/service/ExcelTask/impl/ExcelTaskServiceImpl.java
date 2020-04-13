package com.chinaBank.service.ExcelTask.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chinaBank.bean.ExcelTask.ExcelNoticeData;
import com.chinaBank.bean.ExcelTask.ExcelTaskData;
import com.chinaBank.bean.ExcelTask.ExcelTaskOrgData;
import com.chinaBank.bean.ExcelTask.ExcelTaskReportOrgData;
import com.chinaBank.bean.SysSeting.OrgData;
import com.chinaBank.mapper.ExcelTaskMapper;
import com.chinaBank.service.ExcelTask.ExcelTaskService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service(value = "excelTaskService")
public class ExcelTaskServiceImpl implements ExcelTaskService{

	@Autowired
	private ExcelTaskMapper excelTaskMapper;// 这里会报错，但是并不会影响
	@Override
	public String addExcelTask(ExcelTaskData excelTaskData) {
		excelTaskMapper.addExcelTask(excelTaskData);
		return "success";
	}

	@Override
	public String updateExcelTask(ExcelTaskData excelTaskData) {
		excelTaskMapper.updateExcelTask(excelTaskData);
		return "success";
	}

	@Override
	public String deleteExcelTask(String id) {
		excelTaskMapper.deleteExcelTask(id);
		return "success";
	}

	@Override
	public ExcelTaskData selectExcelTaskById(String id) {
		return excelTaskMapper.selectExcelTaskById(id);
	}

	@Override
	public PageInfo<ExcelTaskData> selectExcelTask(int pageNum,int pageSize,ExcelTaskData excelTaskData){
		PageHelper.startPage(pageNum, pageSize);
		List<ExcelTaskData> excelTaskDatas = excelTaskMapper.selectExcelTask(excelTaskData);
		PageInfo result = new PageInfo(excelTaskDatas);
		return result;
	}

	@Override
	public ExcelTaskData selectExcelTaskByModelOrOrg(String file_name, String status,String report_cycle,String report_date) {
		return excelTaskMapper.selectExcelTaskByModelOrOrg(file_name, status,report_cycle,report_date);
	}

	@Override
	public String deleteExcelTaskByBatch(String batch,String status) {
		excelTaskMapper.deleteExcelTaskByBatch(batch,status);
		return "success";
	}

	@Override
	public String addExcelTaskOrg(ExcelTaskOrgData excelTaskOrgData) {
		excelTaskMapper.addExcelTaskOrg(excelTaskOrgData);
		return "success";
	}

	@Override
	public List<ExcelTaskReportOrgData> selectExcelTaskReportOrg(ExcelTaskReportOrgData excelTaskReportOrgData) {
		List<ExcelTaskReportOrgData> excelTaskDatas = excelTaskMapper.selectExcelTaskReportOrg(excelTaskReportOrgData);
		return excelTaskDatas;
	}

	@Override
	public List<String> selectExcelTaskOrgByBatch(String batch) {
		return excelTaskMapper.selectExcelTaskOrgByBatch(batch);
	}

	@Override
	public String updateExcelTaskOrg(String batch) {
		excelTaskMapper.updateExcelTaskOrg(batch);
		return "success";
	}

	@Override
	public String addExcelTaskReportOrg(ExcelTaskReportOrgData excelTaskReportOrgData) {
		excelTaskMapper.addExcelTaskReportOrg(excelTaskReportOrgData);
		return "success";
	}

	@Override
	public ExcelTaskReportOrgData selectExcelTaskReport(String file_name, String date, String report_cycle,
			String report_date,String org) {
		return excelTaskMapper.selectExcelTaskReport(file_name, date, report_cycle, report_date,org);
	}

	@Override
	public List<ExcelTaskReportOrgData> selectExcelTaskReportByOrg(String fileName,String org,String paymentDay,String status,String batch,String paymentDayTJ) {
		return excelTaskMapper.selectExcelTaskReportByOrg(fileName,org,paymentDay,status,batch,paymentDayTJ);
	}

	@Override
	public String updateExcelTaskReportByOrg(String id, String path, String status,String upUser) {
		excelTaskMapper.updateExcelTaskReportByOrg(id, path, status,upUser);
		return "success";
	}

	@Override
	public ExcelTaskReportOrgData selectExcelTaskReportById(String id,String status) {
	    return excelTaskMapper.selectExcelTaskReportById(id,status);
	}

	@Override
	public List<String> selectExcelTaskOrgs() {
		return excelTaskMapper.selectExcelTaskOrgs();
	}

	@Override
	public List<OrgData> selectExcelTaskCheckOrgByBatch(OrgData orgData) {
		return excelTaskMapper.selectExcelTaskCheckOrgByBatch(orgData);
	}

	@Override
	public String updateExcelTaskReportRemark(String id, String remark,String status) {
		excelTaskMapper.updateExcelTaskReportRemark(id, remark,status);
		return "success";
	}

	@Override
	public String updateExcelTaskReportByTask(String batch, String adress, String startDate) {
		excelTaskMapper.updateExcelTaskReportByTask(batch, adress, startDate);
		return "success";
	}

	@Override
	public String addExcelNotice(ExcelNoticeData ExcelNoticeData) {
		excelTaskMapper.addExcelNotice(ExcelNoticeData);
		return "success";
	}

	@Override
	public String addExcelOrgNotice(ExcelNoticeData ExcelNoticeData) {
		excelTaskMapper.addExcelOrgNotice(ExcelNoticeData);
		return "success";
	}

	@Override
	public List<ExcelNoticeData> selectExcelNotice(String title,String status) {
		return excelTaskMapper.selectExcelNotice(title,status);
	}

	@Override
	public List<ExcelNoticeData> selectExcelNoticeByOrg(String title, String org) {
		return excelTaskMapper.selectExcelNoticeByOrg(title, org);
	}

	@Override
	public String updateExcelNotice(ExcelNoticeData ExcelNoticeData) {
		excelTaskMapper.updateExcelNotice(ExcelNoticeData);
		return "success";
	}

	@Override
	public String deleteExcelNotice(String id) {
		excelTaskMapper.deleteExcelNotice(id);
		return "success";
	}

	@Override
	public String updateExcelOrgNotice(String notice_id) {
		excelTaskMapper.updateExcelOrgNotice(notice_id);
		return "success";
	}

	@Override
	public List<String> selectExcelNoticeByNoticeId(String notice_id) {
		return excelTaskMapper.selectExcelNoticeByNoticeId(notice_id);
	}

	@Override
	public List<OrgData> selectExcelNoticeOrgByNoticeId(OrgData orgData) {
		return excelTaskMapper.selectExcelNoticeOrgByNoticeId(orgData);
	}

	@Override
	public List<ExcelTaskReportOrgData> selectExcelTaskReportByCount(String batch, String paymentDay) {
		return excelTaskMapper.selectExcelTaskReportByCount(batch,paymentDay);
	}

	@Override
	public String batchAuditUpload(String id,String status) {
		excelTaskMapper.batchAuditUpload(id,status);
		return "success";
	}

}
