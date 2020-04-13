package com.chinaBank.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.chinaBank.bean.ExcelFileModel.ExcelBaseColData;
import com.chinaBank.bean.ExcelFileModel.ExcelBaseTabData;
import com.chinaBank.bean.ExcelFileModel.ExcelCheckRuleData;
import com.chinaBank.bean.ExcelFileModel.ExcelModelData;
import com.chinaBank.bean.ExcelFileModel.ExcelScriptInfoData;
import com.chinaBank.bean.SysSeting.ModelConfigData;
import com.chinaBank.service.ExcelFileModel.ExcelFileModelService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@RestController
@RequestMapping(value = "/excelModel")
public class ExcelFileModelController {
	private static final Logger LOG = LoggerFactory.getLogger(ExcelFileModelController.class);
	
	@Autowired
	private ExcelFileModelService excelService;
	
	@Autowired
	private ModelConfigData modelConfig;
    
	@RequestMapping("/addExcelModel")
	public Object addExcelModel(@RequestParam(name = "excel_model_name", required = false, defaultValue = "") String excel_model_name,
			@RequestParam(name = "model_status", required = false, defaultValue = "") String model_status,
			@RequestParam(name = "user", required = false, defaultValue = "") String user) {
		Map<String, Object> body = new HashMap<>();
		String create_user = user;
		ExcelModelData ModelData = excelService.selectExcelModelByModelName(excel_model_name);
		if(ModelData != null) {
			body.put("message", "此模板已经存在，不能修改为此模板!");
			body.put("code", "999");
		}else{
			String rsFlag = excelService.addExcelModel(excel_model_name, model_status, create_user);
			if("success".equals(rsFlag)) {
				body.put("message", "模板新增成功!");
				body.put("code", "200");

			}else {
				body.put("message", "模板新增失败!");
				body.put("code", "999");
			}
		}
		return body;
	}
	
	@RequestMapping("/updateExcelModel")
	public Object updateExcelModel(@RequestParam(name = "excel_model_name", required = false, defaultValue = "") String excel_model_name,
			@RequestParam(name = "model_status", required = false, defaultValue = "") String model_status,
			@RequestParam(name = "id", required = false, defaultValue = "") String id,
			@RequestParam(name = "user", required = false, defaultValue = "") String user) {
		Map<String, Object> body = new HashMap<>();
		String update_user = user;
		ExcelModelData excelModelData = excelService.selectExcelModelById(id);
		if(excelModelData !=null ) {
			if(excelModelData.getExcel_model_name().equals(excel_model_name)) {
				String rsFlag = excelService.updateExcelModel(excel_model_name, model_status, id, update_user);
				if("success".equals(rsFlag)) {
					body.put("message", "模板更新成功!");
					body.put("code", "200");
				}else {
					body.put("message", "模板更新失败!");
					body.put("code", "999");
				}
			}else {
				ExcelModelData ModelData = excelService.selectExcelModelByModelName(excel_model_name);
				if(ModelData != null) {
					body.put("message", "此模板已经存在，不能修改为此模板!");
					body.put("code", "999");
				}else{
					String rsFlag = excelService.updateExcelModel(excel_model_name, model_status, id, update_user);
					if("success".equals(rsFlag)) {
						body.put("message", "模板更新成功!");
						body.put("code", "200");
					}else {
						body.put("message", "模板更新失败!");
						body.put("code", "999");
					}
				}
			}
		}else {
			body.put("message", "没有此模板!");
			body.put("code", "999");
		}
		return body;
	}
	
	@RequestMapping("/deleteExcelModel")
	public Object deleteExcelModel(@RequestParam(name = "id", required = false, defaultValue = "") String id){
		Map<String, Object> body = new HashMap<>();
		ExcelBaseTabData excelBaseTabData = new ExcelBaseTabData();
		excelBaseTabData.setModel_id(id);
		List<ExcelBaseTabData> excelBaseTabDatas = excelService.selectBaseTabInfoByName(excelBaseTabData);// 通过模板ID拿到关联的表
		if (excelBaseTabDatas.size() >= 1) {
			return "模板下有关联的表，请先解除所有的关系！!";
		}
		List<ExcelCheckRuleData> reRules = excelService.findCheckRulesInfoByTabId(id); // 通过模板拿到绑定到模板下的所有规则
		if(reRules.size() >= 1) {
			return "模板下有关联的规则，请先解除所有的关系！!";
		}
		String rsFlag = excelService.deleteExcelModel(id);
		if("success".equals(rsFlag)) {
			body.put("message", "模板删除成功!");
			body.put("code", "200");

		}else {
			body.put("message", "模板删除失败!");
			body.put("code", "999");
		}
		return body;
	}
	
    @RequestMapping("/selectExcelTables")
    public Object selectExcelTables(ExcelBaseTabData excelBaseTabData){
        return excelService.selectBaseTabInfoByName(excelBaseTabData);//查询所有的表
    }
	
    @RequestMapping("/selectExcelModelById")
    public Object selectExcelModelById(
            @RequestParam(name = "id", required = false, defaultValue = "") String id){
        return excelService.selectExcelModelById(id);
    }
	
    @RequestMapping("/selectExcelModelByName")
    public Object selectExcelModelByName(
            @RequestParam(name = "pageNum", required = false, defaultValue = "") int pageNum,
            @RequestParam(name = "pageSize", required = false, defaultValue = "") int pageSize,
            @RequestParam(name = "excel_model_name", required = false, defaultValue = "") String excel_model_name){
        return excelService.findAllExcelModel(pageNum, pageSize, excel_model_name);
    }
    
	@RequestMapping("/addBaseTabInfo")
	public Object addBaseTabInfo(ExcelBaseTabData excelBaseTabData) {
		Map<String, Object> body = new HashMap<>();
		ExcelBaseTabData RexcelBaseTabData = excelService.selectBaseTabInfoByTableName(excelBaseTabData.getTable_name());
		if(RexcelBaseTabData != null) {
			body.put("message", "已经有表，新增失败!");
			body.put("code", "999");
		}else {
			String rsFlag = excelService.addBaseTabInfo(excelBaseTabData);
			if("success".equals(rsFlag)) {
				body.put("message", "表新增成功!");
				body.put("code", "200");
			}else {
				body.put("message", "表新增失败!");
				body.put("code", "999");
			}
		}
		return body;
	}
	
	@RequestMapping("/updateBaseTabInfo")
	public Object updateBaseTabInfo(ExcelBaseTabData excelBaseTabData) {
		Map<String, Object> body = new HashMap<>();
		ExcelBaseTabData rsExcelBaseTabData = excelService.selecBaseTabInfoById(excelBaseTabData.getId()); //根据主键id查询的表
		if(rsExcelBaseTabData != null) {
			ExcelBaseTabData RexcelBaseTabData = excelService.selectBaseTabInfoByTableName(excelBaseTabData.getTable_name()); //查询是否已经有相同的名称的表
			ExcelScriptInfoData excelScriptInfoData = excelService.selectScriptInfoByTableName(rsExcelBaseTabData.getTable_name(),"Y"); //查询表是否已经生成
			if(rsExcelBaseTabData.getTable_name().equals(excelBaseTabData.getTable_name())) { //新表和原表做比较，表名没改
				updateTabInfo(body,excelBaseTabData);
			}else {//修改了表名
				if(RexcelBaseTabData != null) {
					body.put("message", "已经有此表，更新失败!");
					body.put("code", "999");
				}else {
					if(excelScriptInfoData != null) {//表已经生成
						String sql = "alter table " + rsExcelBaseTabData.getTable_name() +" rename to " + excelBaseTabData.getTable_name();
						String flag = excelService.runSqlScript(sql); //改表结构，改表明
						if("success".equals(flag)){
							String newName = excelBaseTabData.getTable_name();
							excelScriptInfoData.setTable_name(newName);
							String sFlag = excelService.updateScriptInfo(excelScriptInfoData); //更新脚本
							if("success".equals(sFlag)) {
								updateTabInfo(body,excelBaseTabData);
							}else {
								body.put("message", "更新脚本失败，更新表失败!");
								body.put("code", "999");
							}
						}else {
							body.put("message", "更新表名失败，更新表失败!");
							body.put("code", "999");
						}
					}else {
						updateTabInfo(body,excelBaseTabData);
					}
				}
			}
		}else {
			body.put("message", "无此表!");
			body.put("code", "200");
		}
		return body;
	}
	
	public void updateTabInfo(Map<String, Object> body,ExcelBaseTabData excelBaseTabData) {//更新表
		String rsFlag = excelService.updateBaseTabInfo(excelBaseTabData);
		if("success".equals(rsFlag)) {
			body.put("message", "更新表成功!");
			body.put("code", "200");
		}else {
			body.put("message", "更新表失败!");
			body.put("code", "999");
		}
	}
	
	@RequestMapping("/deleteBaseTabInfo")
	public Object deleteBaseTabInfo(@RequestParam(name = "id", required = false, defaultValue = "") String id) {
		Map<String, Object> body = new HashMap<>();
		ExcelBaseTabData rsExcelBaseTabData = excelService.selecBaseTabInfoById(id);
		if(rsExcelBaseTabData != null) {
			ExcelScriptInfoData excelScriptInfoData = excelService.selectScriptInfoByTableName(rsExcelBaseTabData.getTable_name(),"Y");
			if(excelScriptInfoData != null) {//已经生成了实体表
				excelScriptInfoData.setScript_type("N"); //将已经生成实体表的状态改为未生成，来达到删除的目的
				String sFlag = excelService.updateScriptInfo(excelScriptInfoData);
				if("success".equals(sFlag)) {
					String sql = "drop table " + rsExcelBaseTabData.getTable_name();
					String flag = excelService.runSqlScript(sql); //删除整个表，执行drop操作
					if("success".equals(flag)){
						List<ExcelBaseColData> excelBaseColDatas = excelService.selectBasecolumnInfoByTableId(rsExcelBaseTabData.getId());//查询列配置
						if(excelBaseColDatas.size() >= 1) {
							boolean falg = false;				
							for(ExcelBaseColData data:excelBaseColDatas) { //删除所有表下的列配置
								String rflag = excelService.deleteBasecolumnInfo(data.getId());
								if(!"success".equals(rflag)) {
									body.put("message", "删除列配置失败!");
									body.put("code", "999");
									falg = true;
									continue;
								}
							}
							if(!falg) {
								deleteTabInfo(body,id);
							}
						}else {
							deleteTabInfo(body,id);
						}
					}else {
						body.put("message", "drop表失败!");
						body.put("code", "999");
					}
				}else {
					body.put("message", "更新脚本状态为无效失败!");
					body.put("code", "999");
				}
			}else {//没有生成了实体表
				List<ExcelBaseColData> excelBaseColDatas = excelService.selectBasecolumnInfoByTableId(rsExcelBaseTabData.getId());//查询列配置
				if(excelBaseColDatas.size() >= 1) {
					boolean falg = false;
					for(ExcelBaseColData data:excelBaseColDatas) { //删除所有表下的列配置
						String rflag = excelService.deleteBasecolumnInfo(data.getId());
						if(!"success".equals(rflag)) {
							body.put("message", "删除列配置失败!");
							body.put("code", "999");
							falg = true;
							continue;
						}
					}
					if(!falg) {
						deleteTabInfo(body,id);
					}
				}else {
					deleteTabInfo(body,id);
				}
			}
		}else {
			body.put("message", "无此表，删除失败!");
			body.put("code", "999");
		}
		return body;
	}
	
	public void deleteTabInfo(Map<String, Object> body,String id) {//删除表
		String rsFlag = excelService.deleteBaseTabInfo(id);
		if("success".equals(rsFlag)) {
			body.put("message", "表删除成功!");
			body.put("code", "200");
		}else {
			body.put("message", "表删除失败!");
			body.put("code", "999");
		}
	}
	
    @RequestMapping("/findBaseTabInfoById")
    public Object findBaseTabInfoById(
            @RequestParam(name = "id", required = false, defaultValue = "") String id){
        return excelService.selecBaseTabInfoById(id);
    }
	
    @RequestMapping("/findAllBaseTabInfo")
    public Object findAllBaseTabInfo(
            @RequestParam(name = "pageNum", required = false, defaultValue = "") int pageNum,
            @RequestParam(name = "pageSize", required = false, defaultValue = "") int pageSize,
            ExcelBaseTabData excelBaseTabData){
        return excelService.findAllBaseTabInfo(pageNum, pageSize, excelBaseTabData);
    }

	@RequestMapping("/addBasecolumnInfo")
	public Object addBasecolumnInfo(ExcelBaseColData excelBaseColData) {
		Map<String, Object> body = new HashMap<>();
		ExcelBaseColData RxcelBaseColData = excelService.selectBasecolumnInfoByColumnName(excelBaseColData.getTable_id(),excelBaseColData.getColumn_name()); //查询是否有相同的列
		if(RxcelBaseColData == null) {
			ExcelBaseTabData rsExcelBaseTabData = excelService.selecBaseTabInfoById(excelBaseColData.getTable_id());
			if(rsExcelBaseTabData != null) {
				ExcelScriptInfoData excelScriptInfoData = excelService.selectScriptInfoByTableName(rsExcelBaseTabData.getTable_name(),"Y");
				if(excelScriptInfoData != null) {//已经生成了表
					StringBuffer sql = new StringBuffer(); //增加列
					StringBuffer indexSql = new StringBuffer(); //增加索引
					sql.append("alter table "+rsExcelBaseTabData.getTable_name() + " add column " + excelBaseColData.getColumn_name());
					getSql(sql,excelBaseColData);
					String flag = excelService.runSqlScript(sql.toString());
					if("success".equals(flag)){
		            	if("Y".equals(excelBaseColData.getIndex_type())) {//索引字段  
		            		indexSql.append("ALTER TABLE " + rsExcelBaseTabData.getTable_name() + " ADD INDEX index_" + excelBaseColData.getColumn_name() + " (" + excelBaseColData.getColumn_name() + ")");
							String InFlag = excelService.runSqlScript(indexSql.toString());
							if("success".equals(InFlag)){
								newBaseColumnInfo(body,excelBaseColData);
							}else {
								body.put("message", "列新增失败!");
								body.put("code", "999");
							}
		            	}else {
							newBaseColumnInfo(body,excelBaseColData);
		            	}
					}else {
						body.put("message", "列新增失败!");
						body.put("code", "999");
					}
				}else {//未生成表
					newBaseColumnInfo(body,excelBaseColData);
				}
			}else {
				body.put("message", "没有相关表!");
				body.put("code", "999");
			}
		}else {
			body.put("message", "已经有列，新增失败!");
			body.put("code", "999");
		}
		return body;
	}
	
	public void newBaseColumnInfo(Map<String, Object> body,ExcelBaseColData excelBaseColData) {//增加列
		String rsFlag = excelService.addBasecolumnInfo(excelBaseColData);
		if("success".equals(rsFlag)) {
			body.put("message", "列新增成功!");
			body.put("code", "200");
		}else {
			body.put("message", "列新增失败!");
			body.put("code", "999");
		}
	}
	
	public void getSql(StringBuffer sql,ExcelBaseColData excelBaseColData) { //列的拼接sql
		if("CH".equals(excelBaseColData.getColumn_type())) {//字符
    		sql.append(" varchar("+excelBaseColData.getColumn_length() + ") " + excelBaseColData.getIsNull() + " COMMENT '"+excelBaseColData.getColumn_desc() + "'");
    	}else if("BG".equals(excelBaseColData.getColumn_type())) {//数值（浮点数）
    		if("".equals(excelBaseColData.getColumn_Decimal())) {//精度为空，直接按字符格式创建字段
    			sql.append(" varchar("+excelBaseColData.getColumn_length() + ") " + excelBaseColData.getIsNull() + " COMMENT '"+excelBaseColData.getColumn_desc() + "'");
    		}else {
    			sql.append(" Decimal("+excelBaseColData.getColumn_length()+","+excelBaseColData.getColumn_Decimal()+") " +excelBaseColData.getIsNull() + " COMMENT '"+excelBaseColData.getColumn_desc() + "'");
    		}
    	}else if("IN".equals(excelBaseColData.getColumn_type())) {//整数
    		sql.append(" int("+excelBaseColData.getColumn_length()+") " +excelBaseColData.getIsNull() + " COMMENT '"+excelBaseColData.getColumn_desc() + "'");
    	}else {//DA日期
    		sql.append(" DATE " + excelBaseColData.getIsNull() + " COMMENT '"+excelBaseColData.getColumn_desc() + "'");
    	}	
	}
	
	public void getCoumnTypeSql(StringBuffer sql,ExcelBaseColData excelBaseColData) { //列的拼接sql
		if("CH".equals(excelBaseColData.getColumn_type())) {//字符
    		sql.append(" varchar("+excelBaseColData.getColumn_length() + ") " + excelBaseColData.getIsNull());
    	}else if("BG".equals(excelBaseColData.getColumn_type())) {//数值（浮点数）
    		if("".equals(excelBaseColData.getColumn_Decimal())) {//精度为空，直接按字符格式创建字段
    			sql.append(" varchar("+excelBaseColData.getColumn_length() + ") " + excelBaseColData.getIsNull());
    		}else {
    			sql.append(" Decimal("+excelBaseColData.getColumn_length()+","+excelBaseColData.getColumn_Decimal()+") " +excelBaseColData.getIsNull());
    		}
    	}else if("IN".equals(excelBaseColData.getColumn_type())) {//整数
    		sql.append(" int("+excelBaseColData.getColumn_length()+") " +excelBaseColData.getIsNull() + " COMMENT '"+excelBaseColData.getColumn_desc() + "'");
    	}else {//DA日期
    		sql.append(" DATE " + excelBaseColData.getIsNull());
    	}	
	}
	
	@RequestMapping("/upBasecolumnInfo")
	public Object upBasecolumnInfo(ExcelBaseColData excelBaseColData) {
		Map<String, Object> body = new HashMap<>();
		ExcelBaseTabData rsExcelBaseTabData = excelService.selecBaseTabInfoById(excelBaseColData.getTable_id()); //查询表
		if(rsExcelBaseTabData != null) {
			ExcelScriptInfoData excelScriptInfoData = excelService.selectScriptInfoByTableName(rsExcelBaseTabData.getTable_name(),"Y"); //查询是否已经生成了表
			ExcelBaseColData data= excelService.selectBasecolumnInfoById(excelBaseColData.getId()); //查询原来的列		
	        if(data != null) {
    			ExcelBaseColData RxcelBaseColData = excelService.selectBasecolumnInfoByColumnName(excelBaseColData.getTable_id(),excelBaseColData.getColumn_name());
    			if(!data.getColumn_name().equals(excelBaseColData.getColumn_name()) && RxcelBaseColData != null) {
    				body.put("message", "已经有列，更新失败!");
    				body.put("code", "999");
    			}else {
	        		checkScript(excelScriptInfoData,data,excelBaseColData,rsExcelBaseTabData,body);
    			}
	        }else {
				body.put("message", "无此列，更新失败!");
				body.put("code", "999");
	        }
		}else {
			body.put("message", "没有相关表!");
			body.put("code", "999");
		}		
		return body;
	}
	
	public void checkScript(ExcelScriptInfoData excelScriptInfoData,ExcelBaseColData data,ExcelBaseColData excelBaseColData,ExcelBaseTabData rsExcelBaseTabData,Map<String, Object> body) {//根据是否生成实体表做列更新
		StringBuffer sql = new StringBuffer();
		StringBuffer indexSql = new StringBuffer();
		StringBuffer commentSql = new StringBuffer();
		if(excelScriptInfoData != null) { //生成过表
			String sqlCount ="select count(1) from " + rsExcelBaseTabData.getTable_name();
			int count = excelService.selectCount(sqlCount); //生成了表之后，表是否有数据
			if(count >=1) {//表如果有数据，表结构不能修改
				if(!excelBaseColData.getColumn_type().equals(data.getColumn_type()) || !excelBaseColData.getRead_col().equals(data.getRead_col())) {
					body.put("message", "表已经有数据，不能修改表列类型结构或则读取列!");
					body.put("code", "999");
				}else {
					if(!excelBaseColData.getColumn_length().equals(data.getColumn_length()) || !excelBaseColData.getColumn_Decimal().equals(data.getColumn_Decimal())) {
						body.put("message", "表已经有数据，不能修改表列长度!");
						body.put("code", "999");
					}else{//表已经有数据，只能改表名称、表的描述、索引
						sql.append("alter table "+rsExcelBaseTabData.getTable_name() + " change column " + data.getColumn_name() +" " + excelBaseColData.getColumn_name());
						getCoumnTypeSql(sql,excelBaseColData);
						commentSql.append("alter table " + rsExcelBaseTabData.getTable_name() + " modify column " + excelBaseColData.getColumn_name());
						String flag = excelService.runSqlScript(sql.toString());
						if("success".equals(flag)){
							getCoumnTypeSql(commentSql,excelBaseColData);
							commentSql.append(" COMMENT '"+ excelBaseColData.getColumn_desc() + "'");
							String commenflag = excelService.runSqlScript(commentSql.toString());
							if("success".equals(commenflag)) {
								if(Pdindex(data,rsExcelBaseTabData,excelBaseColData,indexSql,body)) {
									modifyBaseColumnInfo(body,excelBaseColData);
								}else {
									body.put("message", "索引更新失败!");
				    				body.put("code", "999");
								}
							}else {
								body.put("message", "更新列注释失败!");
			    				body.put("code", "999");
							}

			            }else {
			            	body.put("message", "更新列名失败!");
		    				body.put("code", "999");
			            }
					}
				}
			}else {//表没有数据，表结构正常修改
				commentSql.append("alter table "+rsExcelBaseTabData.getTable_name() + " change column " + data.getColumn_name() +" " + excelBaseColData.getColumn_name());
				sql.append("alter table "+rsExcelBaseTabData.getTable_name() + " modify column " + excelBaseColData.getColumn_name());
				getCoumnTypeSql(commentSql,excelBaseColData);
				String commenflag = excelService.runSqlScript(commentSql.toString());
				if("success".equals(commenflag)) {
					getSql(sql,excelBaseColData);
					String flag = excelService.runSqlScript(sql.toString());
					if("success".equals(flag)){
						if(Pdindex(data,rsExcelBaseTabData,excelBaseColData,indexSql,body)) {
							modifyBaseColumnInfo(body,excelBaseColData);
						}else {
							body.put("message", "索引更新失败!");
		    				body.put("code", "999");
						}
		            }else {
		            	body.put("message", "列更新失败!");
						body.put("code", "999");
		            }
				}else {
					body.put("message", "更新列名失败!");
					body.put("code", "999");
				}

			}
		}else {//还没有生成表，不用改表结构
			modifyBaseColumnInfo(body,excelBaseColData);
		}
	}
	
	public void modifyBaseColumnInfo(Map<String, Object> body,ExcelBaseColData excelBaseColData) {//更新列
		String fiFlag = excelService.updateBasecolumnInfo(excelBaseColData);
		if("success".equals(fiFlag)) {
			body.put("message", "列更新成功!");
			body.put("code", "200");
		}else {
			body.put("message", "列更新失败!");
			body.put("code", "999");
		}
	}
	
	public boolean Pdindex(ExcelBaseColData data,ExcelBaseTabData rsExcelBaseTabData,ExcelBaseColData excelBaseColData,StringBuffer indexSql,Map<String, Object> body) {//处理索引的修改
		boolean reFlag = false;
		if(data.getIndex_type().equals("Y")) {//原来的索引为Y
        	if("N".equals(excelBaseColData.getIndex_type())) {//新的索引为N
        		indexSql.append("drop index index_" + data.getColumn_name() + " on " + rsExcelBaseTabData.getTable_name());
        		String rsFlag = excelService.runSqlScript(indexSql.toString());
        		if("success".equals(rsFlag)) {
        			reFlag = true;
        		}else {
        			reFlag = false;
        		}
        	}else {//新的索引为Y
				if(!data.getColumn_name().equals(excelBaseColData.getColumn_name())) { //列名改动
					indexSql.append("drop index index_" + data.getColumn_name() + " on " + rsExcelBaseTabData.getTable_name());
	        		String rsFlag = excelService.runSqlScript(indexSql.toString());
	        		if("success".equals(rsFlag)) {
	        			StringBuffer sql = new StringBuffer();
	        			sql.append("alter table " + rsExcelBaseTabData.getTable_name() + " add index index_" + excelBaseColData.getColumn_name() + " (" + excelBaseColData.getColumn_name() + ")");
	            		String flag = excelService.runSqlScript(sql.toString());
	            		if("success".equals(flag)) {
	            			reFlag = true;
	            		}else {
	            			reFlag = false;
	            		}
	        		}else {
	        			reFlag = false;
	        		}
				}else {
					reFlag = true;
				}
        	}
		}else {//原来的索引为N
        	if("Y".equals(excelBaseColData.getIndex_type())) {////新的索引为Y
        		indexSql.append("alter table " + rsExcelBaseTabData.getTable_name() + " add index index_" + excelBaseColData.getColumn_name() + " (" + excelBaseColData.getColumn_name() + ")");
        		String rsFlag = excelService.runSqlScript(indexSql.toString());
        		if("success".equals(rsFlag)) {
        			reFlag = true;
        		}else {
        			reFlag = false;
        		}
        	}else{
        		reFlag = true;
        	}
		}
		return reFlag;
	}
	
	@RequestMapping("/deleteBasecolumnInfo")
	public Object deleteBasecolumnInfo(@RequestParam(name = "id", required = false, defaultValue = "") String id) {
		Map<String, Object> body = new HashMap<>();
		StringBuffer sql = new StringBuffer();
		StringBuffer indexSql = new StringBuffer();
		ExcelBaseColData data= excelService.selectBasecolumnInfoById(id); //主键查询，只会有一条数据返回
		if(data!=null) {
			ExcelBaseTabData rsExcelBaseTabData = excelService.selecBaseTabInfoById(data.getTable_id()); //查询表
			if(rsExcelBaseTabData != null) {
				ExcelScriptInfoData excelScriptInfoData = excelService.selectScriptInfoByTableName(rsExcelBaseTabData.getTable_name(),"Y"); //查询是否已经生成了表
				if(excelScriptInfoData != null) {//已经生成表
					String sqlCount ="select count(1) from " + rsExcelBaseTabData.getTable_name();
					int count = excelService.selectCount(sqlCount); //生成了表之后，表是否有数据
					if(count >=1) {//表如果有数据，表结构不能修改
						body.put("message", "表已经有数据，不能对表结构进行更改，不能删除列!");
						body.put("code", "200");
					}else {
						if(data.getIndex_type().equals("Y")) {
							indexSql.append("drop index index_" + data.getColumn_name() + " on " + rsExcelBaseTabData.getTable_name());
		            		String rsFlag = excelService.runSqlScript(indexSql.toString()); //删除索引
		            		if("success".equals(rsFlag)) {            			
		            			sql.append("alter table " + rsExcelBaseTabData.getTable_name() + " drop column " + data.getColumn_name());
		            			String Flag = excelService.runSqlScript(sql.toString()); //删除列
			            		if("success".equals(Flag)) { //删除列配置信息表数据     
			    					deleteNewBaseColumnInfo(body,id);
			            		}else {
			            			body.put("message", "删除列失败!");
			            			body.put("code", "999");
			            		}
		            		}else {
		            			body.put("message", "删除列的索引失败!");
								body.put("code", "999");
		            		}
						}else {
							sql.append("alter table " + rsExcelBaseTabData.getTable_name() + " drop column " + data.getColumn_name());
	            			String Flag = excelService.runSqlScript(sql.toString()); //删除列
		            		if("success".equals(Flag)) { //删除列配置信息表数据     
		    					deleteNewBaseColumnInfo(body,id);
		            		}else {
		            			body.put("message", "删除列失败!");
		            			body.put("code", "999");
		            		}
						}
					}
				}else {//没有生成表
					deleteNewBaseColumnInfo(body,id);
				}
			}else {
				body.put("message", "没有表!");
				body.put("code", "999");
			}
		}else {
			body.put("message", "没有此列!");
			body.put("code", "999");
		}
		return body;
	}
	
	public void deleteNewBaseColumnInfo(Map<String, Object> body,String id) {//更新列
		String rsFlag = excelService.deleteBasecolumnInfo(id);
		if("success".equals(rsFlag)) {
			body.put("message", "列删除成功!");
			body.put("code", "200");
		}else {
			body.put("message", "列删除失败!");
			body.put("code", "999");
		}
	}
	
    @RequestMapping("/findBasecolumnInfoById")
    public Object findBasecolumnInfoById(
            @RequestParam(name = "id", required = false, defaultValue = "") String id){
        return excelService.selectBasecolumnInfoById(id);
    }
	
    @RequestMapping("/findAllBasecolumnInfo")
    public Object findAllBasecolumnInfo(
            @RequestParam(name = "pageNum", required = false, defaultValue = "") int pageNum,
            @RequestParam(name = "pageSize", required = false, defaultValue = "") int pageSize,
            @RequestParam(name = "table_id", required = false, defaultValue = "") String table_id){
        return excelService.findAllBasecolumnInfo(pageNum, pageSize, table_id);
    }
    
    @RequestMapping("/createTabInfo")
	@Transactional(propagation=Propagation.REQUIRES_NEW)
    public Object createTabInfo(
            @RequestParam(name = "table_id", required = false, defaultValue = "") String table_id,
            @RequestParam(name = "create_user", required = false, defaultValue = "") String create_user) throws Exception{
    	StringBuffer sf = new StringBuffer(); 
    	List<ExcelBaseColData> excelBaseColDatas= excelService.selectcolumnsInfoByTabId(table_id);
    	String tab_desc = excelBaseColDatas.get(0).getTable_desc();
    	String table_name = excelBaseColDatas.get(0).getTable_name();
    	ExcelScriptInfoData data = excelService.selectScriptInfoByTableName(table_name, "Y");
    	if(data != null) {
    		 return "已经创建了此表 ，创建失败！";
    	}else {
        	if(excelBaseColDatas.size() >=1) {
            	sf.append("CREATE TABLE "+ table_name + " (");
            	sf.append("ID varchar(50) NOT NULL COMMENT '主键',");
            	sf.append("BATCH varchar(100) NOT NULL COMMENT '批次号',"); //用于删除数据使用
            	sf.append("ROWNUM varchar(10) NOT NULL COMMENT 'EXCEL行',"); //用于记录错误信息
            	sf.append("STATUS varchar(1) NOT NULL COMMENT '上传类型',"); //是否上传成功
            	sf.append("ZQ date NOT NULL COMMENT '账期',"); 
            	sf.append("ORG varchar(20) NOT NULL COMMENT '报送机构',"); 
            	sf.append("USER varchar(50) NOT NULL COMMENT '报送人',");
            	sf.append("CREATED_DATE datetime NOT NULL COMMENT '报送时间',");
                for(int i=0;i<excelBaseColDatas.size();i++) {
                	if("CH".equals(excelBaseColDatas.get(i).getColumn_type())) {//字符
                		sf.append(excelBaseColDatas.get(i).getColumn_name() + " varchar("+excelBaseColDatas.get(i).getColumn_length() + ") " + excelBaseColDatas.get(i).getIsNull() + " COMMENT '"+excelBaseColDatas.get(i).getColumn_desc() + "',");
                	}else if("BG".equals(excelBaseColDatas.get(i).getColumn_type())) {//数字
                		if("".equals(excelBaseColDatas.get(i).getColumn_Decimal())) {//精度为空，直接按字符格式创建字段
                    		sf.append(excelBaseColDatas.get(i).getColumn_name() + " varchar("+excelBaseColDatas.get(i).getColumn_length() + ") " + excelBaseColDatas.get(i).getIsNull() + " COMMENT '"+excelBaseColDatas.get(i).getColumn_desc() + "',");
                		}else {
                    		sf.append(excelBaseColDatas.get(i).getColumn_name() + " Decimal("+(Integer.parseInt(excelBaseColDatas.get(i).getColumn_length()) + Integer.parseInt(excelBaseColDatas.get(i).getColumn_Decimal())) +","+excelBaseColDatas.get(i).getColumn_Decimal()+") " + excelBaseColDatas.get(i).getIsNull() + " COMMENT '"+excelBaseColDatas.get(i).getColumn_desc() + "',");
                		}
                	}else if("IN".equals(excelBaseColDatas.get(i).getColumn_type())) {//整数
                		sf.append(excelBaseColDatas.get(i).getColumn_name() + " int("+excelBaseColDatas.get(i).getColumn_length() + ") " + excelBaseColDatas.get(i).getIsNull() + " COMMENT '"+excelBaseColDatas.get(i).getColumn_desc() + "',");
                	}else {//DA日期
                		sf.append(excelBaseColDatas.get(i).getColumn_name() + " DATE " + excelBaseColDatas.get(i).getIsNull() + " COMMENT '"+excelBaseColDatas.get(i).getColumn_desc() + "',");
                	}
                }
                sf.append("PRIMARY KEY (ID),");
                sf.append("index index_BATCH (BATCH)");
            	boolean flag = false;
                for(int i=0;i<excelBaseColDatas.size();i++) {
                	if("Y".equals(excelBaseColDatas.get(i).getIndex_type())) {//索引字段  
                		flag = true;
                    	if(i == excelBaseColDatas.size()-1) {
                    		sf.append(" ,index index_" + excelBaseColDatas.get(i).getColumn_name() +" (" + excelBaseColDatas.get(i).getColumn_name() + ")");
                    		continue;
                    	}
                		if(flag) { //防止没有建立任何索引字段
                			sf.append(",");
                		}
                    	sf.append("index index_" + excelBaseColDatas.get(i).getColumn_name() +" (" + excelBaseColDatas.get(i).getColumn_name() + ")");
                    	flag = false;
                	}
                }
                sf.append(") ENGINE = INNODB DEFAULT CHARSET = utf8 COMMENT = '"+ tab_desc + "';");
                LOG.info("生成的表："+sf.toString());
        		ExcelScriptInfoData excelScriptInfoData = new ExcelScriptInfoData();
        		excelScriptInfoData.setCreate_user(create_user);
        		excelScriptInfoData.setScript_information(sf.toString());
        		excelScriptInfoData.setTable_desc(tab_desc);
        		excelScriptInfoData.setTable_name(table_name);
    			excelScriptInfoData.setScript_type("Y");
    			String backFlag = excelService.addScriptInfo(excelScriptInfoData);
    			if("success".equals(backFlag)) {
    				String rnFlag = excelService.createTable(sf.toString());
    				if("success".equals(rnFlag)) {
    			        return "创建表成功";
    				}else {
    					return "创建表失败";
    				}
    			}else {
    				return "创建表失败";
    			}
        	}else {
        		return "请创建列，再生成表！";
        	}
    	}
    }
    
	@RequestMapping("/addCheckRulesInfo")
	@Transactional
	public Object addCheckRulesInfo(@RequestBody ExcelCheckRuleData excelCheckRuleData) {
		Map<String, Object> body = new HashMap<>();
		String public_type = excelCheckRuleData.getPublic_type();
		String create_user = excelCheckRuleData.getCreate_user();
		boolean flag = true;
		if("Y".equals(public_type)) {//是公共规则库关联而来的规则
			List<String> data = excelService.selectCheckRulesPubShipForId(excelCheckRuleData.getModel_id(), excelCheckRuleData.getTable_id(),excelCheckRuleData.getCol());
			if(data.size() != 0) {
				String rsFlag = excelService.deleteCheckRulesPublicByTab(excelCheckRuleData.getModel_id(), excelCheckRuleData.getTable_id(),excelCheckRuleData.getCol());
				if("success".equals(rsFlag)) {
					String[] publicIds = excelCheckRuleData.getPublic_ids();
					for(int i=0;i<publicIds.length;i++) {
						String RnFlag = excelService.addCheckRulesPublicShip(excelCheckRuleData.getTable_id(), publicIds[i], excelCheckRuleData.getModel_id(),create_user,excelCheckRuleData.getCol());
						if(!"success".equals(RnFlag)) {	
							flag = false;
							break;
						}
					}
					if(flag) {
						body.put("message", "规则新增成功!");
						body.put("code", "200");
					}else {
						body.put("message", "规则新增失败!");
						body.put("code", "999");
					}
				}else {
					body.put("message", "规则新增失败!");
					body.put("code", "999");
				}
			}else {
				String[] publicIds = excelCheckRuleData.getPublic_ids();
				for(int i=0;i<publicIds.length;i++) {
					String RnFlag = excelService.addCheckRulesPublicShip(excelCheckRuleData.getTable_id(), publicIds[i], excelCheckRuleData.getModel_id(),create_user,excelCheckRuleData.getCol());
					if(!"success".equals(RnFlag)) {	
						flag = false;
						break;
					}
				}
				if(flag) {
					body.put("message", "规则新增成功!");
					body.put("code", "200");
				}else {
					body.put("message", "规则新增失败!");
					body.put("code", "999");
				}
			}
		}else {
			String rsFlag = excelService.addCheckRulesInfo(excelCheckRuleData);
			if("success".equals(rsFlag)) {
				body.put("message", "规则新增成功!");
				body.put("code", "200");

			}else {
				body.put("message", "规则新增失败!");
				body.put("code", "999");
			}
		}
		return body;
	}
	
	@RequestMapping("/updateCheckRulesInfo")
	public Object updateCheckRulesInfo(@RequestBody ExcelCheckRuleData excelCheckRuleData) {
		Map<String, Object> body = new HashMap<>();
		String rsFlag = excelService.updateCheckRulesInfo(excelCheckRuleData);
		if("success".equals(rsFlag)) {
			body.put("message", "规则更新成功!");
			body.put("code", "200");

		}else {
			body.put("message", "规则更新失败!");
			body.put("code", "999");
		}
		return body;
	}
	
	@RequestMapping("/deleteCheckRulesInfo")
	public Object deleteCheckRulesInfo(@RequestParam(name = "id", required = false, defaultValue = "") String id,
			@RequestParam(name = "public_type", required = false, defaultValue = "") String public_type){
		Map<String, Object> body = new HashMap<>();
		if(public_type.equals("Y")) {//是公共规则
			String rsFlag = excelService.deleteCheckRulesPubShip(id);
			if("success".equals(rsFlag)) {
				body.put("message", "规则删除成功!");
				body.put("code", "200");
			}else {
				body.put("message", "规则删除失败!");
				body.put("code", "999");
			}
		}else {
			String rsFlag = excelService.deleteCheckRulesInfo(id);
			if("success".equals(rsFlag)) {
				body.put("message", "规则删除成功!");
				body.put("code", "200");

			}else {
				body.put("message", "规则删除失败!");
				body.put("code", "999");
			}
		}
		return body;
	}
	
    @RequestMapping("/selectCheckRulesInfoById")
    public Object selectCheckRulesInfoById(
            @RequestParam(name = "id", required = false, defaultValue = "") String id){
        return excelService.selectCheckRulesInfoById(id);
    }
    
    @RequestMapping("/selectAllCheckRulesInfo")
    public Object selectAllCheckRulesInfo(
            @RequestParam(name = "pageNum", required = false, defaultValue = "") int pageNum,
            @RequestParam(name = "pageSize", required = false, defaultValue = "") int pageSize,
            ExcelCheckRuleData excelCheckRuleData){
		PageHelper.startPage(pageNum, pageSize);
		List<ExcelCheckRuleData> data = excelService.findAllCheckRulesInfo(excelCheckRuleData);
		PageInfo result = new PageInfo(data);
		return result;
    }
    
//    @RequestMapping("/addConfigFileInfo")
//	@ResponseBody
//	public Object addConfigFileInfo(ExcelConfigFileData excelConfigFileData) {
//		Map<String, Object> body = new HashMap<>();
//		ExcelConfigFileData rsExcelConfigFileData = excelService.selectConfigFileInfoByFileName(excelConfigFileData.getFile_name());
//		if (rsExcelConfigFileData != null) {
//			body.put("message", "已经有此文件，新增失败!");
//			body.put("code", "999");
//		}else {
//			String rsFlag = excelService.addConfigFileInfo(excelConfigFileData);
//			if("success".equals(rsFlag)) {
//				body.put("message", "文件新增成功!");
//				body.put("code", "200");
//			}else {
//				body.put("message", "文件新增失败!");
//				body.put("code", "999");
//			}
//		}
//		return body;
//	}
//	
//	@RequestMapping("/updateConfigFileInfo")
//	@ResponseBody
//	public Object updateConfigFileInfo(ExcelConfigFileData excelConfigFileData) {
//		Map<String, Object> body = new HashMap<>();
//		String rsFlag = excelService.updateConfigFileInfo(excelConfigFileData);
//		if("success".equals(rsFlag)) {
//			body.put("message", "文件更新成功!");
//			body.put("code", "200");
//
//		}else {
//			body.put("message", "文件更新失败!");
//			body.put("code", "999");
//		}
//		return body;
//	}
//	
//	@RequestMapping("/deleteConfigFileInfo")
//	@ResponseBody
//	public Object deleteConfigFileInfo(@RequestParam(name = "id", required = false, defaultValue = "") String id){
//		Map<String, Object> body = new HashMap<>();
//		List<ExcelModelData> excelModelDatas = excelService.findModelFileByFileId(id); // 暂时取一个数据，通过文件拿到文件下绑定的模板
//		if (excelModelDatas.size() >= 0) {
//			return "文件下有关联的模板，请先解除关系!";
//		}
//		String rsFlag = excelService.deleteConfigFileInfo(id);
//		if("success".equals(rsFlag)) {
//			body.put("message", "文件删除成功!");
//			body.put("code", "200");
//
//		}else {
//			body.put("message", "文件删除失败!");
//			body.put("code", "999");
//		}
//		return body;
//	}
//	
//    @ResponseBody
//    @RequestMapping("/selectConfigFileInfoById")
//    public Object selectConfigFileInfoById(
//            @RequestParam(name = "id", required = false, defaultValue = "") String id){
//        return excelService.selectConfigFileInfoById(id);
//    }
//    
//    @ResponseBody
//    @RequestMapping("/findAllConfigFileInfo")
//    public Object findAllConfigFileInfo(
//            @RequestParam(name = "pageNum", required = false, defaultValue = "") int pageNum,
//            @RequestParam(name = "pageSize", required = false, defaultValue = "") int pageSize,
//            ExcelConfigFileData excelConfigFileData){
//        return excelService.findAllConfigFileInfo(pageNum, pageSize, excelConfigFileData);
//    }
     
//	@RequestMapping("/addModelFileRelationship")
//	@ResponseBody
//	public Object addModelFileRelationship(
//			 @RequestParam(name = "file_id", required = false, defaultValue = "") String file_id,
//	            @RequestParam(name = "status", required = false, defaultValue = "") String status,
//	            @RequestParam(name = "model_id", required = false, defaultValue = "") String model_id,
//	            @RequestParam(name = "model_name", required = false, defaultValue = "") String model_name,
//	            @RequestParam(name = "create_user", required = false, defaultValue = "") String create_user
//			) {
//		Map<String, Object> body = new HashMap<>();
//		ExcelModelData excelModelData = excelService.findModelFileById(file_id, model_id);
//		if(excelModelData != null) {
//			body.put("message", "已经模板维护，新增失败!");
//			body.put("code", "999");
//		}else {
//			String rsFlag = excelService.addModelFileRelationship(file_id, status, model_id, model_name,create_user);
//			if("success".equals(rsFlag)) {
//				body.put("message", "模板维护新增成功!");
//				body.put("code", "200");
//			}else {
//				body.put("message", "模板维护新增失败!");
//				body.put("code", "999");
//			}
//		}
//		return body;
//	}
//	
//	@RequestMapping("/deleteModelFileRelationship")
//	@ResponseBody
//	public Object deleteModelFileRelationship(
//			 @RequestParam(name = "file_id", required = false, defaultValue = "") String file_id,
//	            @RequestParam(name = "model_id", required = false, defaultValue = "") String model_id
//			){
//		Map<String, Object> body = new HashMap<>();
//		String rsFlag = excelService.deleteModelFileRelationship(file_id, model_id);
//		if("success".equals(rsFlag)) {
//			body.put("message", "模板维护删除成功!");
//			body.put("code", "200");
//
//		}else {
//			body.put("message", "模板删除失败!");
//			body.put("code", "999");
//		}
//		return body;
//	}
//	
//    @ResponseBody
//    @RequestMapping("/findModelFileRelationship")
//    public Object findModelFileRelationship(
//            @RequestParam(name = "pageNum", required = false, defaultValue = "") int pageNum,
//            @RequestParam(name = "pageSize", required = false, defaultValue = "") int pageSize,
//            @RequestParam(name = "file_id", required = false, defaultValue = "") String file_id){
//        return excelService.findModelFileRelationship(pageNum, pageSize, file_id);
//    }
    
    @RequestMapping("/selectExcelModelAll")
    public Object selectExcelModelAll(){
        return excelService.selectExcelModelAll();
    }
    
	@RequestMapping("/addCheckRulesPublic")
	public Object addCheckRulesPublic(@RequestBody ExcelCheckRuleData excelCheckRuleData) {
		Map<String, Object> body = new HashMap<>();
		String rsFlag = excelService.addCheckRulesPublic(excelCheckRuleData);
		if("success".equals(rsFlag)) {
			body.put("message", "规则新增成功!");
			body.put("code", "200");

		}else {
			body.put("message", "规则新增失败!");
			body.put("code", "999");
		}
		return body;
	}
	
	@RequestMapping("/updateCheckRulesPublic")
	public Object updateCheckRulesPublic(@RequestBody ExcelCheckRuleData excelCheckRuleData) {
		Map<String, Object> body = new HashMap<>();
		String rsFlag = excelService.updateCheckRulesPublic(excelCheckRuleData);
		if("success".equals(rsFlag)) {
			body.put("message", "规则更新成功!");
			body.put("code", "200");

		}else {
			body.put("message", "规则更新失败!");
			body.put("code", "999");
		}
		return body;
	}
	
	@RequestMapping("/deleteCheckRulesPublic")
	public Object deleteCheckRulesPublic(@RequestParam(name = "id", required = false, defaultValue = "") String id){
		Map<String, Object> body = new HashMap<>();
		String rsFlag = excelService.deleteCheckRulesPublic(id);
		if("success".equals(rsFlag)) {
			body.put("message", "规则删除成功!");
			body.put("code", "200");

		}else {
			body.put("message", "规则删除失败!");
			body.put("code", "999");
		}
		return body;
	}
	
    @RequestMapping("/selectCheckRulesPublicById")
    public Object selectCheckRulesPublicById(
            @RequestParam(name = "id", required = false, defaultValue = "") String id){
        return excelService.selectCheckRulesPublicById(id);
    }
    
    @RequestMapping("/findAllCheckRulesPublic")
    public Object findAllCheckRulesPublic(
            @RequestParam(name = "pageNum", required = false, defaultValue = "") int pageNum,
            @RequestParam(name = "pageSize", required = false, defaultValue = "") int pageSize,
            @RequestParam(name = "check_desc", required = false, defaultValue = "") String check_desc,
            @RequestParam(name = "check_class", required = false, defaultValue = "") String check_class,
            @RequestParam(name = "check_type", required = false, defaultValue = "") String check_type){
		PageHelper.startPage(pageNum, pageSize);
		List<ExcelCheckRuleData> data = excelService.findAllCheckRulesPublic(check_desc,check_class,check_type);
		PageInfo result = new PageInfo(data);
		return result;
    }
    
    @RequestMapping("/selectCheckRulesPubShipForId")
    public Object selectCheckRulesPubShipForId(
            @RequestParam(name = "model_id", required = false, defaultValue = "") String model_id,
            @RequestParam(name = "table_id", required = false, defaultValue = "") String table_id,
            @RequestParam(name = "col", required = false, defaultValue = "") String col){
        return excelService.selectCheckRulesPubShipForId(model_id, table_id,col);
    }
    
    @RequestMapping("/selectcolumnsInfoByTabId")
    public Object selectCheckRulesPubShipForId(
            @RequestParam(name = "table_id", required = false, defaultValue = "") String table_id){
        List<ExcelBaseColData> excelBaseColDatas = excelService.selectcolumnsInfoByTabId(table_id); 
        return excelBaseColDatas;
    }

}
