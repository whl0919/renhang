package com.chinaBank.service.ExcelFileModel.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chinaBank.bean.ExcelFileModel.ExcelBaseColData;
import com.chinaBank.bean.ExcelFileModel.ExcelBaseTabData;
import com.chinaBank.bean.ExcelFileModel.ExcelCheckRuleData;
import com.chinaBank.bean.ExcelFileModel.ExcelModelData;
import com.chinaBank.bean.ExcelFileModel.ExcelScriptInfoData;
import com.chinaBank.mapper.ExcelFileModelMapper;
import com.chinaBank.service.ExcelFileModel.ExcelFileModelService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service(value = "excelService")
public class ExcelFileModelServiceImpl implements ExcelFileModelService {

	@Autowired
	private ExcelFileModelMapper excelFileModelMapper;

	@Override
	public String addExcelModel(String excel_model_name, String model_status, String create_user) {
		excelFileModelMapper.addExcelModel(excel_model_name, model_status, create_user);
		return "success";
	}

	@Override
	public String updateExcelModel(String excel_model_name, String model_status, String id, String update_user) {
		excelFileModelMapper.updateExcelModel(excel_model_name, model_status, id, update_user);
		return "success";
	}

	@Override
	public String deleteExcelModel(String id) {
		excelFileModelMapper.deleteExcelModel(id);
		return "success";
	}

	@Override
	public ExcelModelData selectExcelModelById(String id) {
		ExcelModelData excelModelDatas = excelFileModelMapper.selectExcelModelById(id);
		return excelModelDatas;
	}

	@Override
	public List<ExcelModelData> selectExcelModelByName(String excel_model_name) {
		List<ExcelModelData> excelModelDatas = excelFileModelMapper.selectExcelModelByName(excel_model_name);
		return excelModelDatas;
	}

	@Override
	public PageInfo<ExcelModelData> findAllExcelModel(int pageNum, int pageSize, String excel_model_name) {
		PageHelper.startPage(pageNum, pageSize);
		List<ExcelModelData> excelModelDatas = excelFileModelMapper.selectExcelModelByName(excel_model_name);
		PageInfo result = new PageInfo(excelModelDatas);
		return result;
	}
	
	@Override
	public String addBaseTabInfo(ExcelBaseTabData excelBaseTabData) {
		excelFileModelMapper.addBaseTabInfo(excelBaseTabData);
		return "success";
	}

	@Override
	public String updateBaseTabInfo(ExcelBaseTabData excelBaseTabData) {
		excelFileModelMapper.updateBaseTabInfo(excelBaseTabData);
		return "success";
	}

	@Override
	public String deleteBaseTabInfo(String id) {
		excelFileModelMapper.deleteBaseTabInfo(id);
		return "success";
	}

	@Override
	public ExcelBaseTabData selecBaseTabInfoById(String id) {
		ExcelBaseTabData excelBaseTabDatas = excelFileModelMapper.selectBaseTabInfoById(id);
		return excelBaseTabDatas;
	}

	@Override
	public ExcelBaseTabData selectBaseTabInfoByTableName(String table_name) {
		ExcelBaseTabData excelBaseTabDatas = excelFileModelMapper.selectBaseTabInfoByTableName(table_name);
		return excelBaseTabDatas;
	}

	@Override
	public PageInfo<ExcelBaseTabData> findAllBaseTabInfo(int pageNum, int pageSize, ExcelBaseTabData excelBaseTabData) {
		PageHelper.startPage(pageNum, pageSize);
		List<ExcelBaseTabData> excelBaseTabDatas = excelFileModelMapper.selectBaseTabInfo(excelBaseTabData);
		PageInfo result = new PageInfo(excelBaseTabDatas);
		return result;
	}

	@Override
	public String addBasecolumnInfo(ExcelBaseColData excelBaseColData) {
		excelFileModelMapper.addBasecolumnInfo(excelBaseColData);
		return "success";
	}

	@Override
	public String updateBasecolumnInfo(ExcelBaseColData excelBaseColData) {
		excelFileModelMapper.updateBasecolumnInfo(excelBaseColData);
		return "success";
	}

	@Override
	public String deleteBasecolumnInfo(String id) {
		excelFileModelMapper.deleteBasecolumnInfo(id);
		return "success";
	}

	@Override
	public ExcelBaseColData selectBasecolumnInfoById(String id) {
		ExcelBaseColData excelBaseColDatas = excelFileModelMapper.selectBasecolumnInfoById(id);
		return excelBaseColDatas;
	}

	@Override
	public PageInfo<ExcelBaseColData> findAllBasecolumnInfo(int pageNum, int pageSize,
			String table_id) {
		PageHelper.startPage(pageNum, pageSize);
		List<ExcelBaseColData> excelBaseColDatas = excelFileModelMapper.findAllBasecolumnInfo(table_id);
		PageInfo result = new PageInfo(excelBaseColDatas);
		return result;
	}

	@Override
	public List<ExcelBaseColData> selectcolumnsInfoByTabId(String table_id) {
		List<ExcelBaseColData> excelBaseColDatas = excelFileModelMapper.selectcolumnsInfoByTabId(table_id);
		return excelBaseColDatas;
	}

	@Override
	public String addScriptInfo(ExcelScriptInfoData excelScriptInfoData) {
		excelFileModelMapper.addScriptInfo(excelScriptInfoData);	
		return "success";
	}

	@Override
	public String addCheckRulesInfo(ExcelCheckRuleData excelCheckRuleData) {
		excelFileModelMapper.addCheckRulesInfo(excelCheckRuleData);
		return "success";
	}

	@Override
	public String updateCheckRulesInfo(ExcelCheckRuleData excelCheckRuleData) {
		excelFileModelMapper.updateCheckRulesInfo(excelCheckRuleData);
		return "success";
	}

	@Override
	public String deleteCheckRulesInfo(String id) {
		excelFileModelMapper.deleteCheckRulesInfo(id);
		return "success";
	}

	@Override
	public ExcelCheckRuleData selectCheckRulesInfoById(String id) {
		ExcelCheckRuleData excelCheckRuleDatas = excelFileModelMapper.selectCheckRulesInfoById(id);
		return excelCheckRuleDatas;
	}

	@Override
	public List<ExcelCheckRuleData> findAllCheckRulesInfo(ExcelCheckRuleData excelCheckRuleData) {
		return excelFileModelMapper.selectCheckRulesInfo(excelCheckRuleData);
	}

//	@Override
//	public String addConfigFileInfo(ExcelConfigFileData excelConfigFileData) {
//		excelFileModelMapper.addConfigFileInfo(excelConfigFileData);
//		return "success";
//	}
//	
//	@Override
//	public ExcelConfigFileData selectConfigFileInfoByFileName(String FileName) {
//		return excelFileModelMapper.selectConfigFileInfoByFileName(FileName);
//	}
//	
//	@Override
//	public String updateConfigFileInfo(ExcelConfigFileData excelConfigFileData) {
//		excelFileModelMapper.updateConfigFileInfo(excelConfigFileData);
//		return "success";
//	}
//
//	@Override
//	public String deleteConfigFileInfo(String id) {	
//		excelFileModelMapper.deleteConfigFileInfo(id);
//		return "success";
//	}
//
//	@Override
//	public List<ExcelConfigFileData> selectConfigFileInfoById(String id) {
//		return excelFileModelMapper.selectConfigFileInfoById(id);
//	}
//
//	@Override
//	public PageInfo<ExcelConfigFileData> findAllConfigFileInfo(int pageNum, int pageSize,
//			ExcelConfigFileData excelConfigFileData) {
//		PageHelper.startPage(pageNum, pageSize);
//		List<ExcelConfigFileData> excelConfigFileDatas = excelFileModelMapper.selectConfigFileInfo(excelConfigFileData);
//		PageInfo result = new PageInfo(excelConfigFileDatas);
//		return result;
//	}

//	@Override
//	public String addModelFileRelationship(String file_id, String status, String model_id, String model_name,String create_user) {
//		excelFileModelMapper.addModelFileRelationship(file_id, status, model_id, model_name,create_user);
//		return "success";
//	}
//
//	@Override
//	public String deleteModelFileRelationship(String file_id, String model_id) {
//		excelFileModelMapper.deleteModelFileRelationship(file_id, model_id);
//		return "success";
//	}
//
//	@Override
//	public PageInfo<ExcelModelData> findModelFileRelationship(int pageNum, int pageSize, String file_id) {
//		PageHelper.startPage(pageNum, pageSize);
//		List<ExcelModelData> excelModelDatas = excelFileModelMapper.findModelFileRelationship(file_id, "");
//		PageInfo result = new PageInfo(excelModelDatas);
//		return result;
//	}
//
//	@Override
//	public List<ExcelModelData> findModelFileByFileId(String file_id) {
//		List<ExcelModelData> excelModelDatas = excelFileModelMapper.findModelFileRelationship(file_id, "");
//		return excelModelDatas;
//	}

	@Override
	public List<ExcelBaseTabData> selectBaseTabInfoByName(ExcelBaseTabData excelBaseTabData) {
		List<ExcelBaseTabData> excelBaseTabDatas = excelFileModelMapper.selectBaseTabInfo(excelBaseTabData);
		return excelBaseTabDatas;
	}

	@Override
	public List<ExcelCheckRuleData> findCheckRulesInfoByTabId(String model_id) {
		ExcelCheckRuleData excelCheckRuleData = new ExcelCheckRuleData();
		excelCheckRuleData.setModel_id(model_id);
		List<ExcelCheckRuleData> excelCheckRuleDatas = excelFileModelMapper.selectCheckRulesInfo(excelCheckRuleData);
		return excelCheckRuleDatas;
	}

	@Override
	public ExcelScriptInfoData selectScriptInfoByTableName(String table_name,String script_type) {
		ExcelScriptInfoData excelScriptInfoData = excelFileModelMapper.selectScriptInfoByTableName(table_name,script_type);
		return excelScriptInfoData;
	}

	@Override
	public String runSqlScript(String sql) {
		excelFileModelMapper.runSqlScript(sql);
		return "success";
	}

	@Override
	public String updateScriptInfo(ExcelScriptInfoData excelScriptInfoData) {
		excelFileModelMapper.updateScriptInfo(excelScriptInfoData);
		return "success";
	}

	@Override
	public List<ExcelBaseColData> selectBasecolumnInfoByTableId(String tableId) {
		List<ExcelBaseColData> excelBaseColDatas = excelFileModelMapper.selectBasecolumnInfoByName(tableId,"");
		return excelBaseColDatas;
	}
	
	@Override
	public int selectCount(String sql) {
		int count = excelFileModelMapper.selectCount(sql);
		return count;
	}

//	@Override
//	public ExcelSheetData selectSheetInfoBySheetName(String sheet_name) {
//		return excelFileModelMapper.selectSheetInfoBySheetName(sheet_name);
//	}

	@Override
	public ExcelModelData selectExcelModelByModelName(String excel_model_name) {
		return excelFileModelMapper.selectExcelModelByModelName(excel_model_name);
	}

	@Override
	public ExcelBaseColData selectBasecolumnInfoByColumnName(String tableId, String colUmnName) {
		return excelFileModelMapper.selectBasecolumnInfoByColumnName(tableId, colUmnName);
	}

	@Override
	public String deleteScript(String table_name) {
		excelFileModelMapper.deleteScript(table_name);
		return "success";
	}

	@Override
	public List<ExcelModelData> selectExcelModelAll() {
		return excelFileModelMapper.selectExcelModelAll();
	}

	@Override
	public ExcelModelData findModelFileById(String file_id, String model_id) {
		return excelFileModelMapper.findModelFileById(file_id, model_id);
	}

	@Override
	public String addCheckRulesPublic(ExcelCheckRuleData excelCheckRuleData) {
		excelFileModelMapper.addCheckRulesPublic(excelCheckRuleData);
		return "success";
	}

	@Override
	public String updateCheckRulesPublic(ExcelCheckRuleData excelCheckRuleData) {
		excelFileModelMapper.updateCheckRulesPublic(excelCheckRuleData);
		return "success";
	}

	@Override
	public String deleteCheckRulesPublic(String id) {
		excelFileModelMapper.deleteCheckRulesPublic(id);
		return "success";
	}

	@Override
	public ExcelCheckRuleData selectCheckRulesPublicById(String id) {
		return excelFileModelMapper.selectCheckRulesPublicById(id);
	}

	@Override
	public List<ExcelCheckRuleData> findAllCheckRulesPublic(String check_desc,String check_class,String check_type) {
		return excelFileModelMapper.findAllCheckRulesPublic(check_desc,check_class,check_type);
	}

	@Override
	public String createTable(String sql) {
		excelFileModelMapper.createTable(sql);
	    return "success";
	}

	@Override
	public String addCheckRulesPublicShip(String table_id, String public_id, String model_id,String create_user,String col) {
		excelFileModelMapper.addCheckRulesPublicShip(table_id, public_id, model_id,create_user,col);
	    return "success";
	}

	@Override
	public List<String> selectCheckRulesPubShipForId(String model_id, String table_id,String col) {
		return excelFileModelMapper.selectCheckRulesPubShipForId(model_id, table_id,col);
	}

	@Override
	public String deleteCheckRulesPublicByTab(String model_id, String table_id,String col) {
		excelFileModelMapper.deleteCheckRulesPublicByTab(model_id, table_id,col);
		return "success";
	}

	@Override
	public String deleteCheckRulesPubShip(String id) {
		excelFileModelMapper.deleteCheckRulesPubShip(id);
		return "success";
	}
}
