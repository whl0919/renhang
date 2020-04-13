package com.chinaBank.service.ExcelFileModel;

import java.util.List;

import com.chinaBank.bean.ExcelFileModel.ExcelBaseColData;
import com.chinaBank.bean.ExcelFileModel.ExcelBaseTabData;
import com.chinaBank.bean.ExcelFileModel.ExcelCheckRuleData;
import com.chinaBank.bean.ExcelFileModel.ExcelModelData;
import com.chinaBank.bean.ExcelFileModel.ExcelScriptInfoData;
import com.github.pagehelper.PageInfo;

public interface ExcelFileModelService {
	
	//模板配置start
	/**
	 * 添加模板信息
	 */
	String addExcelModel(String excel_model_name, String model_status, String create_user);

	/**
	 * 更新模板信息
	 */
	String updateExcelModel(String excel_model_name, String model_status, String id, String update_user);

	/**
	 * 删除模板信息
	 */
	String deleteExcelModel(String id);

	/**
	 * 通过ID查询模板
	 */
	ExcelModelData selectExcelModelById(String id);
	
	/**
	 * 查询所有的模板
	 */
	List<ExcelModelData> selectExcelModelAll();

	/**
	 * 通过模板名称查询模板
	 */
	List<ExcelModelData> selectExcelModelByName(String excel_model_name);
	
	/**
	 * 通过模板名称查询模板
	 */
	ExcelModelData selectExcelModelByModelName(String excel_model_name);

	/**
	 * 查询模板信息
	 */
	PageInfo<ExcelModelData> findAllExcelModel(int pageNum, int pageSize, String excel_model_name);
	//模板配置end
	
	//表相关配置start
	/**
	 * 配置表信息
	 */
	String addBaseTabInfo(ExcelBaseTabData excelBaseTabData);

	/**
	 * 更新表配置信息
	 */
	String updateBaseTabInfo(ExcelBaseTabData excelBaseTabData);

	/**
	 * 删除表配置信息
	 */
	String deleteBaseTabInfo(String id);

	/**
	 * 通过ID查询表信息
	 */
	ExcelBaseTabData selecBaseTabInfoById(String id);

	/**
	 * 通过表名称查询表信息
	 */
	ExcelBaseTabData selectBaseTabInfoByTableName(String table_name);
	
	/**
	 * 通过模板id查询该模板下绑定的表信息
	 */
	List<ExcelBaseTabData> selectBaseTabInfoByName(ExcelBaseTabData excelBaseTabData);

	/**
	 * 查询表配置信息
	 */
	PageInfo<ExcelBaseTabData> findAllBaseTabInfo(int pageNum, int pageSize, ExcelBaseTabData excelBaseTabData);
	//表相关配置end
	
	//列相关配置start
	/**
	 * 配置列信息
	 */
	String addBasecolumnInfo(ExcelBaseColData excelBaseColData);

	/**
	 * 更新列配置信息
	 */
	String updateBasecolumnInfo(ExcelBaseColData excelBaseColData);

	/**
	 * 删除列配置信息
	 */
	String deleteBasecolumnInfo(String id);

	/**
	 * 通过ID查询列信息
	 */
	ExcelBaseColData selectBasecolumnInfoById(String id);

	/**
	 * 查询列配置信息
	 */
	PageInfo<ExcelBaseColData> findAllBasecolumnInfo(int pageNum, int pageSize, String table_id);
	//列相关配置end
	
	/**
	 * 通过表名称查询表信息
	 */
	List<ExcelBaseColData> selectcolumnsInfoByTabId(String table_id);
	
	/**
	 * 脚本信息记录
	 */
	String addScriptInfo(ExcelScriptInfoData excelScriptInfoData);
	
	/**
	 * 更新脚本
	 */
	String updateScriptInfo(ExcelScriptInfoData excelScriptInfoData);
	
	/**
	 * 脚本信息查询
	 */
	ExcelScriptInfoData selectScriptInfoByTableName(String table_name,String script_type);
	
	/**
	 * 添加规则信息
	 */
	String addCheckRulesInfo(ExcelCheckRuleData excelCheckRuleData);

	/**
	 * 更新规则信息
	 */
	String updateCheckRulesInfo(ExcelCheckRuleData excelCheckRuleData);

	/**
	 * 删除规则信息
	 */
	String deleteCheckRulesInfo(String id);

	/**
	 * 通过ID查询规则
	 */
	ExcelCheckRuleData selectCheckRulesInfoById(String id);

	/**
	 * 查询规则
	 */
	List<ExcelCheckRuleData> findAllCheckRulesInfo(ExcelCheckRuleData excelCheckRuleData);
	
	/**
	 * 通过模板查询绑定到模板下的规则
	 */
	List<ExcelCheckRuleData> findCheckRulesInfoByTabId(String model_id);
	
//	/**
//	 * 添加文件信息
//	 */
//	String addConfigFileInfo(ExcelConfigFileData excelConfigFileData);

//	/**
//	 * 更新文件信息
//	 */
//	String updateConfigFileInfo(ExcelConfigFileData excelConfigFileData);
//
//	/**
//	 * 删除文件信息
//	 */
//	String deleteConfigFileInfo(String id);
//
//	/**
//	 * 通过ID查询文件
//	 */
//	List<ExcelConfigFileData> selectConfigFileInfoById(String id);
//	
//	/**
//	 * 通过文件名查询文件
//	 */
//	ExcelConfigFileData selectConfigFileInfoByFileName(String FileName);
//
//	/**
//	 * 查询文件
//	 */
//	PageInfo<ExcelConfigFileData> findAllConfigFileInfo(int pageNum, int pageSize, ExcelConfigFileData excelConfigFileData);
	
//	/**
//	 * 添加文件关联模板信息
//	 */
//	String addModelFileRelationship(String file_id, String status, String model_id,String model_name,String create_user);
//	
//	/**
//	 * 删除文件关联模板信息
//	 */
//	String deleteModelFileRelationship(String file_id,String model_id);
//	
//	/**
//	 * 查询文件关联模板信息\分页查询
//	 */
//	PageInfo<ExcelModelData> findModelFileRelationship(int pageNum, int pageSize, String file_id);
//	
//	/**
//	 * 查询文件关联模板信息
//	 */
//	List<ExcelModelData> findModelFileByFileId(String file_id);
	
	/**
	 *动态执行sql
	 */
	String runSqlScript(String sql);
	/**
	 *通过表查询列
	 */
	List<ExcelBaseColData> selectBasecolumnInfoByTableId(String tableId);
	
	/**
	 *通过表查询列
	 */
	ExcelBaseColData selectBasecolumnInfoByColumnName(String tableId,String colUmnName);
	
	/**
	 * 动态查询数量
	 */
	int selectCount(String sql);
	
	/**
	 * 删除脚本信息
	 */
	String deleteScript(String table_name);
	
	ExcelModelData findModelFileById(String file_id,String model_id);
	
	/**
	 * 添加公共规则信息
	 */
	String addCheckRulesPublic(ExcelCheckRuleData excelCheckRuleData);
	/**
	 * 更新公共规则信息
	 */
	String updateCheckRulesPublic(ExcelCheckRuleData excelCheckRuleData);

	/**
	 * 删除公共规则信息
	 */
	String deleteCheckRulesPublic(String id);

	/**
	 * 通过ID查询公共规则
	 */
	ExcelCheckRuleData selectCheckRulesPublicById(String id);

	/**
	 * 查询公共规则
	 */
	List<ExcelCheckRuleData> findAllCheckRulesPublic(String check_desc,String check_class,String check_type);
	/**
	 *生成实体表
	 */
	String createTable(String sql);
	/**
	 * 添加由公共规则关联过来的规则
	 */
	String addCheckRulesPublicShip(String table_id,String public_id,String model_id,String create_user,String col);
	/**
	 * 查询当前表下所有关联过来的规则id
	 */
	List<String> selectCheckRulesPubShipForId(String model_id,String table_id,String col);
	/**
	 * 删除该表下所有的规则
	 */
	String deleteCheckRulesPublicByTab(String model_id,String table_id,String col);
	/**
	 * 删除关联的规则
	 */
	String deleteCheckRulesPubShip(String id);
	
}
