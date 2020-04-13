package com.chinaBank.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.chinaBank.bean.ExcelFileModel.ExcelBaseColData;
import com.chinaBank.bean.ExcelFileModel.ExcelBaseTabData;
import com.chinaBank.bean.ExcelFileModel.ExcelCheckRuleData;
import com.chinaBank.bean.ExcelFileModel.ExcelModelData;
import com.chinaBank.bean.ExcelFileModel.ExcelScriptInfoData;

public interface ExcelFileModelMapper {

	/**
	 * 添加模板信息
	 */
	void addExcelModel(@Param(value = "excel_model_name") String excel_model_name,
			@Param(value = "model_status") String model_status, 
			@Param(value = "create_user") String create_user);

	/**
	 * 更新模板信息
	 */
	void updateExcelModel(@Param(value = "excel_model_name") String excel_model_name,
			@Param(value = "model_status") String model_status, 
			@Param(value = "id") String id,
			@Param(value = "update_user") String update_user);

	/**
	 * 删除模板信息
	 */
	void deleteExcelModel(@Param(value = "id") String id);

	/**
	 * 通过ID查询模板
	 */
	ExcelModelData selectExcelModelById(@Param(value = "id") String id);

	/**
	 * 通过模板名称查询模板
	 */
	List<ExcelModelData> selectExcelModelByName(@Param(value = "excel_model_name") String excel_model_name);
	
	/**
	 * 通过模板名称查询模板
	 */
	ExcelModelData selectExcelModelByModelName(@Param(value = "excel_model_name") String excel_model_name);
	
	/**
	 * 增表配置信息
	 */
	void addBaseTabInfo(ExcelBaseTabData excelBaseTabData);

	/**
	 * 改表配置信息
	 */
	void updateBaseTabInfo(ExcelBaseTabData excelBaseTabData);

	/**
	 * 删表配置信息
	 */
	void deleteBaseTabInfo(String id);

	/**
	 * 通过ID查询表配置信息
	 */
	ExcelBaseTabData selectBaseTabInfoById(@Param(value = "id") String id);

	/**
	 * 通过表名查询表配置信息
	 */
	ExcelBaseTabData selectBaseTabInfoByTableName(String table_name);

	/**
	 * 查表配置信息
	 */
	List<ExcelBaseTabData> selectBaseTabInfo(ExcelBaseTabData excelBaseTabData);
	
	/**
	 * 列配置信息
	 */
	void addBasecolumnInfo(ExcelBaseColData excelBaseColData);

	/**
	 * 列配置信息
	 */
	void updateBasecolumnInfo(ExcelBaseColData excelBaseColData);

	/**
	 * 列配置信息
	 */
	void deleteBasecolumnInfo(String id);

	/**
	 * 通过ID查询列配置信息
	 */
	ExcelBaseColData selectBasecolumnInfoById(@Param(value = "id") String id);

	/**
	 * 通过列名查询列配置信息
	 */
	List<ExcelBaseColData> selectBasecolumnInfoByName(@Param(value = "table_id") String table_id,@Param(value = "column_name") String column_name);

	/**
	 * 查列配置信息
	 */
	List<ExcelBaseColData> findAllBasecolumnInfo(String table_id);
	
//	/**
//	 * 将创建表脚本保存到脚本信息表
//	 */
//	void addScriptInfo(@Param(value = "table_name") String table_name,
//			@Param(value = "table_desc") String table_desc,
//			@Param(value = "script_information") String script_information,
//			@Param(value = "script_type") String script_type);

	/**
	 * 根据表查询表信息
	 */
	List<ExcelBaseColData> selectcolumnsInfoByTabId(String table_id);
	
	/**
	 * 记录脚本信息
	 */
	void addScriptInfo(ExcelScriptInfoData excelScriptInfoData);
	
	/**
	 * 增规则
	 */
	void addCheckRulesInfo(ExcelCheckRuleData excelCheckRuleData);

	/**
	 * 改规则
	 */
	void updateCheckRulesInfo(ExcelCheckRuleData excelCheckRuleData);

	/**
	 * 删规则
	 */
	void deleteCheckRulesInfo(String id);

	/**
	 * 通过ID查询规则信息
	 */
	ExcelCheckRuleData selectCheckRulesInfoById(String id);

	/**
	 * 通过模板id查规则
	 */
	List<ExcelCheckRuleData> selectCheckRulesInfo(ExcelCheckRuleData excelCheckRuleData);
	
//	/**
//	 * 增文件
//	 */
//	void addConfigFileInfo(ExcelConfigFileData excelConfigFileData);
//
//	/**
//	 * 改文件
//	 */
//	void updateConfigFileInfo(ExcelConfigFileData excelConfigFileData);
//
//	/**
//	 * 删文件
//	 */
//	void deleteConfigFileInfo(String id);
//
//	/**
//	 * 通过ID查询文件
//	 */
//	List<ExcelConfigFileData> selectConfigFileInfoById(String id);
//	
//	/**
//	 * 通过文件名查询文件
//	 */
//	ExcelConfigFileData selectConfigFileInfoByFileName(String file_name);
//	
//	/**
//	 * 查文件
//	 */
//	List<ExcelConfigFileData> selectConfigFileInfo(ExcelConfigFileData excelConfigFileData);
	
//	/**
//	 * 增文件关联的模板
//	 */
//	void addModelFileRelationship(
//			@Param(value = "file_id") String file_id,
//			@Param(value = "status") String status,
//			@Param(value = "model_id") String model_id,
//			@Param(value = "model_name") String model_name,
//			@Param(value = "create_user") String create_user
//			);
//	
//	/**
//	 * 删文件关联的模板
//	 */
//	void deleteModelFileRelationship(
//			@Param(value = "file_id") String file_id,
//			@Param(value = "model_id") String model_id);
//
//	/**
//	 * 通过文件查询文件关联的模板
//	 */
//	List<ExcelModelData> findModelFileRelationship(String file_id,String model_id);
	
	/**
	 * 脚本信息查询
	 */
	ExcelScriptInfoData selectScriptInfoByTableName(@Param(value = "table_name") String table_name,@Param(value = "script_type") String script_type);
	
	/**
	 *动态执行sql
	 */
	void runSqlScript(String sql);
	/**
	 *更新脚本信息
	 */
	void updateScriptInfo(ExcelScriptInfoData excelScriptInfoData);

	int selectCount(String sql);
	
	ExcelBaseColData selectBasecolumnInfoByColumnName(@Param(value = "tableId") String tableId, @Param(value = "colUmnName") String colUmnName);
	
	/**
	 * 删除脚本信息
	 */
	void deleteScript(@Param(value = "table_name") String table_name);
	
	/**
	 * 查询所有的模板
	 */
	List<ExcelModelData> selectExcelModelAll();
	
	ExcelModelData findModelFileById(@Param(value = "file_id") String file_id,@Param(value = "model_id") String model_id);

	/**
	 * 添加公共规则信息
	 */
	void addCheckRulesPublic(ExcelCheckRuleData excelCheckRuleData);
	/**
	 * 更新公共规则信息
	 */
	void updateCheckRulesPublic(ExcelCheckRuleData excelCheckRuleData);

	/**
	 * 删除公共规则信息
	 */
	void deleteCheckRulesPublic(String id);

	/**
	 * 通过ID查询公共规则
	 */
	ExcelCheckRuleData selectCheckRulesPublicById(String id);

	/**
	 * 查询公共规则
	 */
	List<ExcelCheckRuleData> findAllCheckRulesPublic(@Param(value = "check_desc") String check_desc,
			@Param(value = "check_class") String check_class,@Param(value = "check_type") String check_desccheck_type);
	/**
	 *生成实体表
	 */
	void createTable(String sql);
	/**
	 * 添加由公共规则关联过来的规则
	 */
	void addCheckRulesPublicShip(@Param(value = "table_id") String table_id,
			@Param(value = "public_id") String public_id,
			@Param(value = "model_id") String model_id,
			@Param(value = "create_user") String create_user,
			@Param(value = "col") String col);
	
	/**
	 * 查询当前表下所有关联过来的规则id
	 */
	List<String> selectCheckRulesPubShipForId(@Param(value = "model_id") String model_id,
			@Param(value = "table_id") String table_id,
			@Param(value = "col") String col);
	/**
	 * 删除该表下所有的规则
	 */
	void deleteCheckRulesPublicByTab(@Param(value = "model_id") String model_id,
			@Param(value = "table_id") String table_id,
			@Param(value = "col") String col);
	/**
	 * 删除关联的规则
	 */
	void deleteCheckRulesPubShip(String id);
}
