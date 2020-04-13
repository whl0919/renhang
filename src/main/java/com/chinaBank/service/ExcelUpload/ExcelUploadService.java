package com.chinaBank.service.ExcelUpload;

import java.util.List;
import java.util.Map;

import com.chinaBank.bean.ExcelUpload.UploadData;
import com.chinaBank.bean.ExcelUpload.UploadErrorData;
import com.github.pagehelper.PageInfo;

public interface ExcelUploadService {
	
	/**
	 * 用于列规则校验
	 */
	List<UploadErrorData> selectCountCols(String sql);
	
	/**
	 * 用于列规则校验
	 */
	String updateTable(String sql);
	
	/**
	 * 插入验证规则错误信息
	 */
	String addUploadErrorInfo(UploadErrorData uploadErrorData);
	
	/**
	 * 插入软性校验的说明
	 */
	String updateUploadErrorInfo(String id,String reason);
	
	/**
	 * 查询该文件所有未通过验证规则错误信息
	 */
	PageInfo<UploadErrorData> findAllUploadErrorInfo(int pageNum, int pageSize, UploadErrorData uploadErrorData);
	
	/**
	 * 查询该文件所有未通过验证规则错误信息
	 */
//	PageInfo<UploadErrorData> selectUploadErrorInfoByFileName(String file_name,String user);
	
	/**
	 * 删除掉批次号下所有违规的信息
	 */
//	String deleteUploadErrorInfo(String batch,String cheack_type);
	
	/**
	 * 上传文件信息
	 */
//	String adduploadFileInfo(UploadFileInfoData uploadFileInfoData);
	
	/**
	 * 查询上传的文件
	 */
//	UploadFileInfoData selectUploadFileInfo(String file_name,String user,String status);
	
	/**
	 * 查询所有的文件信息
	 */
//	PageInfo<UploadFileInfoData> findAllUploadFileInfo(int pageNum, int pageSize, String file_name,String user);
	
	/**
	 * 查询所有的文件信息,用于审核
	 */
//	PageInfo<UploadFileInfoData> selectUploadFileInfoForCheck(int pageNum, int pageSize,UploadFileInfoData uploadFileInfoData);
	
	/**
	 * 文件审核
	 */
//	String updateUploadFileInfo(String id,String check_type,String remark);
	
	String updateUploadErrorInfoByBatch(String batch);
	
	/**
	 * 批量插入excel数据
	 */
	String insertListData(Map<String,Object> map);
	/**
	 * 查询任务分类错误信息
	 */
	List <UploadErrorData> selectUploadErrorInfoByBatch(String reportId);
	
	String addUploadErrorData(String reportId,String ruleId,String errInfo,String reason,String upUser);
	
	String selectUploadErrorDataByBatch(String reportId,String ruleId);
	
	/**
	 * 用于动态查询数据
	 */
	List<Map> selectDataForExcel(String sql);
    
}