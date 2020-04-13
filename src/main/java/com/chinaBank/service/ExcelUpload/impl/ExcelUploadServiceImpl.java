package com.chinaBank.service.ExcelUpload.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chinaBank.bean.ExcelUpload.UploadData;
import com.chinaBank.bean.ExcelUpload.UploadErrorData;
import com.chinaBank.mapper.ExcelUploadMapper;
import com.chinaBank.service.ExcelUpload.ExcelUploadService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service(value = "fileUploadService")
public class ExcelUploadServiceImpl implements ExcelUploadService{

	@Autowired
	private ExcelUploadMapper excelUploadMapper;
	
	@Override
	public List<UploadErrorData> selectCountCols(String sql) {
		List<UploadErrorData> data = excelUploadMapper.selectCountCols(sql);
		return data;
	}
	
	@Override
	public String addUploadErrorInfo(UploadErrorData uploadErrorData) {
		excelUploadMapper.addUploadErrorInfo(uploadErrorData);
		return "success";
	}

	@Override
	public PageInfo<UploadErrorData> findAllUploadErrorInfo(int pageNum, int pageSize,
			UploadErrorData data) {
		PageHelper.startPage(pageNum, pageSize);
		List<UploadErrorData> uploadErrorData = excelUploadMapper.findAllUploadErrorInfo(data);
		PageInfo result = new PageInfo(uploadErrorData);
		return result;
	}

//	@Override
//	public String deleteUploadErrorInfo(String batch,String cheack_type) {
//		excelUploadMapper.deleteUploadErrorInfo(batch,cheack_type);
//		return "success";
//	}

//	@Override
//	public String adduploadFileInfo(UploadFileInfoData uploadFileInfoData) {
//		excelUploadMapper.adduploadFileInfo(uploadFileInfoData);
//		return "success";
//	}

//	@Override
//	public UploadFileInfoData selectUploadFileInfo(String file_name, String user, String status) {
//		UploadFileInfoData uploadFileInfoData = excelUploadMapper.selectUploadFileInfo(file_name, user, status);
//		return uploadFileInfoData;
//	}

	@Override
	public String updateUploadErrorInfo(String id, String reason) {
		excelUploadMapper.updateUploadErrorInfo(id, reason);
		return "success";
	}

//	@Override
//	public PageInfo<UploadFileInfoData> findAllUploadFileInfo(int pageNum, int pageSize, String file_name, String user) {
//		PageHelper.startPage(pageNum, pageSize);
//		List<UploadFileInfoData> uploadFileInfoDatas = excelUploadMapper.findAllUploadFileInfo(file_name, user, "Y");//查询成功的数据
//		PageInfo result = new PageInfo(uploadFileInfoDatas);
//		return result;
//	}

	@Override
	public String updateTable(String sql) {
		excelUploadMapper.updateTable(sql);
		return "success";
	}

//	@Override
//	public PageInfo<UploadFileInfoData> selectUploadFileInfoForCheck(int pageNum, int pageSize, UploadFileInfoData uploadFileInfoData) {
//		PageHelper.startPage(pageNum, pageSize);
//		List<UploadFileInfoData> uploadFileInfoDatas = excelUploadMapper.selectUploadFileInfoForCheck(uploadFileInfoData);//查询成功的数据
//		PageInfo result = new PageInfo(uploadFileInfoDatas);
//		return result;
//	}

//	@Override
//	public PageInfo<UploadErrorData> selectUploadErrorInfoByFileName(String file_name, String user) {
//		return excelUploadMapper.selectUploadErrorInfoByFileName(file_name, user);
//	}

//	@Override
//	public String updateUploadFileInfo(String id, String check_type, String remark) {
//		excelUploadMapper.updateUploadFileInfo(id, check_type, remark);
//		return "success";
//	}

	@Override
	public String updateUploadErrorInfoByBatch(String batch) {
		excelUploadMapper.updateUploadErrorInfoByBatch(batch);
		return "success";
	}

	@Override
	public String insertListData(Map<String,Object> map) {
		excelUploadMapper.insertListData(map);
		return "success";
	}

	@Override
	public List<UploadErrorData> selectUploadErrorInfoByBatch(String reportId) {
		return excelUploadMapper.selectUploadErrorInfoByBatch(reportId);
	}

	@Override
	public String addUploadErrorData(String reportId, String ruleId, String errInfo, String reason, String upUser) {
		excelUploadMapper.addUploadErrorData(reportId,ruleId,ruleId,reason,upUser);
		return "success";
	}

	@Override
	public String selectUploadErrorDataByBatch(String reportId, String ruleId) {
		return excelUploadMapper.selectUploadErrorDataByBatch( reportId, ruleId);
	}

	@Override
	public List<Map> selectDataForExcel(String sql) {
		return excelUploadMapper.selectDataForExcel(sql);
	}
}
