package com.chinaBank.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.chinaBank.bean.ExcelUpload.UploadData;
import com.chinaBank.bean.ExcelUpload.UploadErrorData;
import com.chinaBank.bean.ExcelUpload.UploadFileInfoData;
import com.chinaBank.bean.SysSeting.OrgData;
import com.github.pagehelper.PageInfo;

public interface ExcelUploadMapper {
	
	List<UploadErrorData> selectCountCols(String sql);
	
	void updateTable(String sql);
	
	/**
	 * 插入验证规则错误信息
	 */
	void addUploadErrorInfo(UploadErrorData uploadErrorData);
	
	/**
	 * 插入软性校验的说明
	 */
	void updateUploadErrorInfo(@Param(value = "id") String id,@Param(value = "reason") String reason);
	
	/**
	 * 查询该文件所有未通过验证规则错误信息
	 */
	List<UploadErrorData> findAllUploadErrorInfo(UploadErrorData data);
	
	/**
	 * 删掉当前批次违反规则插入表的信息
	 */
//	void deleteUploadErrorInfo(@Param(value = "batch") String batch,@Param(value = "cheack_type") String cheack_type);
	
//	/**
//	 * 上传文件信息
//	 */
//	void adduploadFileInfo(UploadFileInfoData uploadFileInfoData);
	
	/**
	 * 查询上传的文件
	 */
//	UploadFileInfoData selectUploadFileInfo(@Param(value = "file_name") String file_name,@Param(value = "user") String user,@Param(value = "status") String status);
	
	/**
	 * 查询所有上传的文件
	 */
//	List<UploadFileInfoData> findAllUploadFileInfo(@Param(value = "file_name") String file_name,@Param(value = "user") String user,@Param(value = "status") String status);
	
	/**
	 * 查询所有上传的文件,用于审核
	 */
//	List<UploadFileInfoData> selectUploadFileInfoForCheck(UploadFileInfoData uploadFileInfoData);//查询成功的数据
	
	/**
	 * 文件名查询错误信息
	 */
//	PageInfo<UploadErrorData> selectUploadErrorInfoByFileName(@Param(value = "file_name") String file_name,@Param(value = "user") String user);
	
	/**
	 *文件审核
	 */
//	void updateUploadFileInfo(String id, String check_type, String remark);
	/**
	 *更新错误信息状态
	 */
	void updateUploadErrorInfoByBatch(@Param(value = "batch") String batch);
	/**
	 * 批量插入excel数据
	 */
	void insertListData(Map<String,Object> map);
	
	List<UploadErrorData> selectUploadErrorInfoByBatch(@Param(value = "reportId") String reportId); 
	
	void addUploadErrorData(@Param(value = "reportId") String reportId,
			@Param(value = "ruleId") String ruleId,
			@Param(value = "errInfo") String errInfo,
			@Param(value = "reason") String reason,
			@Param(value = "upUser") String upUser);
	
	String selectUploadErrorDataByBatch(@Param(value = "reportId") String reportId, @Param(value = "ruleId") String ruleId);
	
	List<Map> selectDataForExcel(String sql);
}
