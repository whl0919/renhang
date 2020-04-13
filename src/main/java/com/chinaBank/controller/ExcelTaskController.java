package com.chinaBank.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.chinaBank.bean.ExcelFileModel.ExcelBaseTabData;
import com.chinaBank.bean.ExcelFileModel.ExcelModelData;
import com.chinaBank.bean.ExcelFileModel.ExcelScriptInfoData;
import com.chinaBank.bean.ExcelTask.ExcelNoticeData;
import com.chinaBank.bean.ExcelTask.ExcelTaskData;
import com.chinaBank.bean.ExcelTask.ExcelTaskOrgData;
import com.chinaBank.bean.ExcelTask.ExcelTaskReportOrgData;
import com.chinaBank.bean.SysSeting.DeptData;
import com.chinaBank.bean.SysSeting.HolidayData;
import com.chinaBank.bean.SysSeting.OrgData;
import com.chinaBank.service.ExcelFileModel.ExcelFileModelService;
import com.chinaBank.service.ExcelTask.ExcelTaskService;
import com.chinaBank.service.SysSeting.SysSetingService;
import com.chinaBank.utils.EndDay;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * @program: 任务下发
 * @Date: 2019/11/28 15:03
 * @Author: Mr.Wang
 * @Description:
 */
@RestController
@RequestMapping(value = "/task")
public class ExcelTaskController {

	private static final Logger LOG = LoggerFactory.getLogger(ExcelTaskController.class);
	@Autowired
	private ExcelTaskService excelTaskService;
	
	@Autowired
	private SysSetingService sysSetingService;
	
	@Autowired
	private ExcelFileModelService excelService;
	
	@Autowired
	private EndDay endDay;
	
	@RequestMapping("/addExcelTask")
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public Object addExcelTask(HttpServletRequest request,ExcelTaskData excelTaskData) throws Exception {
		Map<String, Object> body = new HashMap<>();
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		MultipartFile file = multipartRequest.getFile("file");
        if (file.isEmpty()) {
			return "文件不能为空";
		}      
		InputStream inputStream = file.getInputStream();
        String data = sheetFileNameIsNull(inputStream, file, excelTaskData.getModel_id());
        if(!"success".equals(data)) {
       		body.put("message", data);
			body.put("code", "999");
        	return body;
        }
		String path = fileUploadServer(file,excelTaskData.getCreate_user());
		String t = String.valueOf(System.currentTimeMillis());// 生成一个时间搓
		String uuid = UUID.randomUUID().toString() + t.replaceAll("-", "").replaceAll(":", "").trim(); // 生成一个唯一的编码，批次号
		String report_cycle_type = excelTaskData.getReport_cycle_type();
		String report_cycle = excelTaskData.getReport_cycle();
		String status = excelTaskData.getStatus();
		excelTaskData.setAdress(path);
		excelTaskData.setBatch(uuid);//用于区分系列类型，做删除使用
	    String []report_date = excelTaskData.getReportDates(); //自定义上报开始时间
	    String []last_date = new String[report_date.length]; //自定义上报截止时间
	    if("1".equals(report_cycle_type) || "3".equals(report_cycle_type)) {//定时和自定义才去获取最后的上报日期
			getExcelTaskData("add",excelTaskData,report_date,last_date);
	    }
		if(status.equals("Y")) {//有效
			boolean flag = true;
			StringBuffer sf = new StringBuffer();
    		ExcelTaskData Data = new ExcelTaskData();
        	if("1".equals(report_cycle_type) || "2".equals(report_cycle_type)) {//定时/历史上报
        		Data = excelTaskService.selectExcelTaskByModelOrOrg(excelTaskData.getFile_name(), status,report_cycle,excelTaskData.getReport_date());
	        	if(Data!=null) {
	        		sf.append(report_cycle);
					flag = false;
	        	}
        	}else {
        		for(int y=0;y<report_date.length;y++) {
	        		Data = excelTaskService.selectExcelTaskByModelOrOrg(excelTaskData.getFile_name(), status,"",report_date[y]);
		        	if(Data!=null) {
		        		if(sf.length() == 0) {
		        			sf.append(report_date[y]);
		        		}else {
		        			sf.append(","+report_date[y]);
		        		}
						flag = false;
		        	}
        		}
        	}
	        if(flag) {
				addTask(excelTaskData, body, report_date, last_date);
	        }else {
        		body.put("message", sf.toString()+"日期已经下发了此文件，任务下发失败!");
				body.put("code", "999");
	        }
		}else {//无效
			addTask(excelTaskData, body, report_date, last_date);
		}
		return body;
	}
	
	public String sheetFileNameIsNull(InputStream in, MultipartFile Mufile,String model_id) throws Exception { //判断页签是否为空
		// 创建Excel工作薄
		try {
		String fileName = Mufile.getOriginalFilename();
		
//        try {
//            wb = new XSSFWorkbook(new FileInputStream(Mufile.get));
//        } catch (org.apache.poi.POIXMLException ex) {
//            ex.printStackTrace();
//            if (ex.getCause() instanceof org.apache.poi.openxml4j.exceptions.InvalidFormatException) {
//                wb = new HSSFWorkbook(new POIFSFileSystem(new FileInputStream(file)));
//            }else{
//                throw ex;
//            }
//        }
		
		Workbook work = this.getWorkbook(in, fileName);
		if (null == work) {
			return "创建Excel工作薄为空！";
		}
		ExcelModelData excelModelDatas = excelService.selectExcelModelById(model_id); //判断上传的文件模板是否存在
		if (excelModelDatas == null) {
			return "没有相关的文件模板配置,请到文件配置页面进行文件配置操作!";
		}
		ExcelBaseTabData excelBaseTabData = new ExcelBaseTabData();
		excelBaseTabData.setModel_id(model_id);
		excelBaseTabData.setStatus("Y"); //查询有效的表
		List<ExcelBaseTabData> excelBaseTabDatas = excelService.selectBaseTabInfoByName(excelBaseTabData);// 通过模板ID拿到关联的有效表信息
		if (excelBaseTabDatas.size() == 0) {
			return "没有相关的表信息配置,请到模板配置页面进行相关模板下的表信息配置操作!";
		}
		Sheet sheet = null;
		int sheetCount = work.getNumberOfSheets();
	    for(int x = 0;x<excelBaseTabDatas.size();x++) {//循环所有的表与当前页签进行匹配
			for (int e = 0; e < sheetCount; e++) { // 循环的是多个sheet页签
				sheet = work.getSheetAt(e);
				String sheetName = sheet.getSheetName();
				if(sheetName.equals(excelBaseTabDatas.get(x).getSheet_name())) { //这里用正则，页签匹配上做相应的规则校验
			    	ExcelScriptInfoData data = excelService.selectScriptInfoByTableName(excelBaseTabDatas.get(x).getTable_name(), "Y");
			    	if(data==null) {
			    		return "请到文件模板配置页面配置表信息，生成实体表后再下发任务!";
			    	}
					break;
				}
				if(!sheetName.equals(excelBaseTabDatas.get(x).getSheet_name()) && e == sheetCount-1) { //这里用正则，页签匹配上做相应的规则校验
					return excelBaseTabDatas.get(x).getSheet_name() +"页签没有配置，请配置!";
			    }
		    }
		}
		}catch(Exception e) {
			LOG.info(e.getMessage()+"判断文件信息错误");
			throw new Exception(e.getMessage()+"判断文件信息错误") ;
		}
		return "success";
	}
	
	/**
	 * 判断文件格式
	 *
	 * @param inStr
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public Workbook getWorkbook(InputStream inStr, String fileName) throws Exception {
		Workbook workbook = null;
		String fileType = fileName.substring(fileName.lastIndexOf("."));
		if (".xls".equals(fileType)) {
			workbook = new HSSFWorkbook(inStr);
		} else if (".xlsx".equals(fileType)) {
			workbook = new XSSFWorkbook(inStr);
		} else {
			throw new Exception("请上传excel文件！");
		}
		return workbook;
	}

	public void addTask(ExcelTaskData excelTaskData,Map<String, Object> body,String []report_date,String []last_date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date newDate = new Date();
		excelTaskData.setCreate_date(df.format(newDate));
    	if("1".equals(excelTaskData.getReport_cycle_type()) || "2".equals(excelTaskData.getReport_cycle_type())) {//定时/临时
    		if("1".equals(excelTaskData.getReport_cycle_type())) {//定时
    			excelTaskData.setReport_date(df.format(newDate));
    		}
    		String rsFlag = excelTaskService.addExcelTask(excelTaskData);
			if("success".equals(rsFlag)) {
				body.put("message", "任务下发成功!");
				body.put("code", "200");
			}else {
				body.put("message", "任务下发失败!");
				body.put("code", "999");
			}
    	}else {
    		StringBuffer sn = new StringBuffer();
    		for(int y=0;y<report_date.length;y++) {
    			excelTaskData.setReport_date(report_date[y]);
    			excelTaskData.setReport_cycle_last_day(last_date[y]);
    			String rsFlag = excelTaskService.addExcelTask(excelTaskData);
    			if(!"success".equals(rsFlag)) {
                   if(sn.length()==0) {
                	   sn.append(report_date[y]);
                   }else {
                	   sn.append(","+report_date[y]);
                   }
    			}
    		}
    		if(sn.length()==0) {
    			body.put("message", "任务下发成功!");
				body.put("code", "200");
    		}else {
    			body.put("message", sn.toString()+"日期，任务下发失败!");
				body.put("code", "999");
    		}
    	}
	}
	
	public void getExcelTaskData(String Flag,ExcelTaskData excelTaskData,String []report_date,String []last_date) throws Exception {
	    String report_cycle_type = excelTaskData.getReport_cycle_type();//上报类型
	    int report_days = Integer.parseInt(excelTaskData.getReport_days());//上报天数
	    String report_cycle_class = excelTaskData.getReport_cycle_class();//自然日/工作日
	    Date date =new Date();
	    String report_cycle_last_day = "";//上报截止日期
	    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	    if("1".equals(report_cycle_type)) {//定时
	    	if("update".equals(Flag)) {
	    		date = df.parse(excelTaskData.getReport_date());
	    	}
	    	if("CD".equals(report_cycle_class)) {//自然日
	    		 report_cycle_last_day = endDay.getWorkDay(date, report_days, "CD");
	    	}else if("WD".equals(report_cycle_class)) {//工作日
	    		 report_cycle_last_day = endDay.getWorkDay(date, report_days, "WD");
	    	}
		    excelTaskData.setReport_cycle_last_day(report_cycle_last_day);//定时任务上传的截止时间
	    }else if("3".equals(report_cycle_type)){//自定义
	    	for(int i=0;i<report_date.length;i++) { 	
	    		Date xDate = df.parse(report_date[i]);
		    	if("CD".equals(report_cycle_class)) {//自然日
		    		 report_cycle_last_day = endDay.getWorkDay(xDate, report_days, "CD");
		    	}else if("WD".equals(report_cycle_class)) {//工作日
		    		 report_cycle_last_day = endDay.getWorkDay(xDate, report_days, "WD");
		    	}
		    	last_date[i] = report_cycle_last_day; //自定义任务上传的截止时间
	    	}
	    }
	}
	
	
	@RequestMapping("/updateExcelTask")
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public Object updateExcelTask(HttpServletRequest request,ExcelTaskData excelTaskData) throws Exception {
		Map<String, Object> body = new HashMap<>();
		String adress = excelTaskData.getAdress();
		if("".equals(adress)) {
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;	
			MultipartFile file = multipartRequest.getFile("file");
	        if (file.isEmpty()) {
				return "文件不能为空";
			}      
			InputStream inputStream = file.getInputStream();
	        String data = sheetFileNameIsNull(inputStream, file, excelTaskData.getModel_id());
	        if(!"success".equals(data)) {
	       		body.put("message", data);
				body.put("code", "999");
	        	return body;
	        }
			String path = fileUploadServer(file,excelTaskData.getCreate_user());
			excelTaskData.setAdress(path);
		}else {
			excelTaskData.setAdress(adress);
		}
		String report_cycle_type = excelTaskData.getReport_cycle_type();
		String [] reportDates = new String[0];
	    String []last_date = new String[0];
    	if("3".equals(report_cycle_type)) {//自定义
    		reportDates = excelTaskData.getReport_date().split(",");
    	    last_date = new String[reportDates.length]; //自定义上报截止时间
    	}
	    if("1".equals(report_cycle_type) || "3".equals(report_cycle_type)) {//定时和自定义才去获取最后的上报日期
			getExcelTaskData("update",excelTaskData,reportDates,last_date);
	    }
		String status = excelTaskData.getStatus();
		String report_cycle = excelTaskData.getReport_cycle();
        if(excelTaskData.getStatus().equals("Y")) {//有效
			boolean flag = true;
			StringBuffer sf = new StringBuffer();
    		ExcelTaskData Data = new ExcelTaskData();
        	String DFlag = excelTaskService.deleteExcelTaskByBatch(excelTaskData.getBatch(),"F");
        	if("success".equals(DFlag)) {
            	if("1".equals(report_cycle_type) || "2".equals(report_cycle_type)) {//定时
            		Data = excelTaskService.selectExcelTaskByModelOrOrg(excelTaskData.getFile_name(), status,report_cycle,excelTaskData.getReport_date());
    	        	if(Data!=null) {
    	        		sf.append(report_cycle);
    					flag = false;
    	        	}
            	}else {
            		for(int y=0;y<reportDates.length;y++) {
    	        		Data = excelTaskService.selectExcelTaskByModelOrOrg(excelTaskData.getFile_name(), status,"",reportDates[y]);
    		        	if(Data!=null) {
    		        		if(sf.length() == 0) {
    		        			sf.append(reportDates[y]);
    		        		}else {
    		        			sf.append(","+reportDates[y]);
    		        		}
    						flag = false;
    		        	}
            		}
            	}
    	        if(flag) {
    	        	updateTask(excelTaskData, body,reportDates, last_date,excelTaskData.getBatch());
    	        }else {
    	        	excelTaskService.deleteExcelTaskByBatch(excelTaskData.getBatch(),excelTaskData.getOldStatus());
            		body.put("message", sf.toString()+"日期已经下发了此文件，任务更新失败!");
    				body.put("code", "999");
    	        }
        	}else {
    			body.put("message", "任务下发失败!");
				body.put("code", "999");
        	}
		}else {//改为无效，随便改，不做任何判断
        	String DFlag = excelTaskService.deleteExcelTaskByBatch(excelTaskData.getBatch(),"F");
        	if("success".equals(DFlag)) {
        		updateTask(excelTaskData, body, reportDates, last_date,excelTaskData.getBatch());
        	}else {
    			body.put("message", "任务下发失败!");
				body.put("code", "999");
        	}
		}
		return body;
	}
	
	public void updateTask(ExcelTaskData excelTaskData,Map<String, Object> body,String [] reportDates,String []last_date,String uuid) {
//		String t = String.valueOf(System.currentTimeMillis());// 生成一个时间搓
//		String uuid = UUID.randomUUID().toString() + t.replaceAll("-", "").replaceAll(":", "").trim(); // 生成一个唯一的编码，批次号
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date newDate = new Date();
		excelTaskData.setUpdate_date(df.format(newDate));
		excelTaskData.setBatch(uuid);
    	if("1".equals(excelTaskData.getReport_cycle_type()) || "2".equals(excelTaskData.getReport_cycle_type())) {//定时/临时
    		String rsFlag = excelTaskService.addExcelTask(excelTaskData);
			if("success".equals(rsFlag)) {
				if("2".equals(excelTaskData.getReport_cycle_type())) {
					String rFlag = excelTaskService.updateExcelTaskReportByTask(excelTaskData.getBatch(), excelTaskData.getAdress(), excelTaskData.getReport_date());
					if("success".equals(rFlag)) {
						body.put("message", "任务更新成功!");
						body.put("code", "200");
					}else {
						body.put("message", "任务更新失败!");
						body.put("code", "999");
					}
				}else {
					body.put("message", "任务更新成功!");
					body.put("code", "200");
				}
			}else {
				body.put("message", "任务更新失败!");
				body.put("code", "999");
			}
    	}else {
    		StringBuffer sn = new StringBuffer();
    		for(int y=0;y<reportDates.length;y++) {
    			excelTaskData.setReport_date(reportDates[y]);
    			excelTaskData.setReport_cycle_last_day(last_date[y]);
    			String rsFlag = excelTaskService.addExcelTask(excelTaskData);
    			if(!"success".equals(rsFlag)) {
                   if(sn.length()==0) {
                	   sn.append(reportDates[y]);
                   }else {
                	   sn.append(","+reportDates[y]);
                   }
    			}
    		}
    		if(sn.length()==0) {
    			body.put("message", "任务更新成功!");
				body.put("code", "200");
    		}else {
    			body.put("message", sn.toString()+"日期，任务更新失败!");
				body.put("code", "999");
    		}
    	}
	}
	
	@RequestMapping("/deleteExcelTask")
	public Object deleteExcelTask( @RequestParam(name = "batch", required = false, defaultValue = "") String batch) {
		Map<String, Object> body = new HashMap<>();
		String rsFlag = excelTaskService.deleteExcelTaskByBatch(batch,"F");
		if("success".equals(rsFlag)) {
			body.put("message", "任务删除成功!");
			body.put("code", "200");
		}else {
			body.put("message", "任务删除失败!");
			body.put("code", "999");
		}
		return body;
	}
	
    @RequestMapping("/selectExcelTask")
    public Object selectExcelTask(
            @RequestParam(name = "pageNum", required = false, defaultValue = "") int pageNum,
            @RequestParam(name = "pageSize", required = false, defaultValue = "") int pageSize,
            ExcelTaskData excelTaskData){
        return excelTaskService.selectExcelTask(pageNum, pageSize, excelTaskData);
    }
    
	@RequestMapping("/addExcelTaskOrg")
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public Object addExcelTaskOrg(@RequestBody Map map) throws Exception {
		Map<String, Object> body = new HashMap<>();
		ExcelTaskOrgData excelTaskOrgData = new ExcelTaskOrgData();
		OrgData orgData = new OrgData();
     	String org = (String) map.get("org"); 
     	String type = (String) map.get("type"); 
     	String create_user = (String) map.get("create_user");
     	String batch = (String) map.get("batch");
     	String BANK = (String) map.get("BANK");
     	String BANKTYPE = (String) map.get("BANKTYPE");
     	String AREA = (String) map.get("AREA");
     	String SUPERIORBANKCODE = (String) map.get("SUPERIORBANKCODE");
     	String Report_cycle_type = (String) map.get("Report_cycle_type");
     	orgData.setBANK(BANK);
     	orgData.setBANKTYPE(BANKTYPE);
     	orgData.setAREA(AREA);
     	orgData.setSUPERIORBANKCODE(SUPERIORBANKCODE);
     	excelTaskOrgData.setCreate_user(create_user);
     	excelTaskOrgData.setBatch(batch);
		List<String> rnList= excelTaskService.selectExcelTaskOrgByBatch(batch);
		if(rnList.size()!=0) {//修改
			excelTaskService.updateExcelTaskOrg(batch);
		}
		String [] orgs = org.split(",");
		System.out.println("orgs"+orgs.length);
		List <String>dataList = new ArrayList<String>();
		StringBuffer sf = new StringBuffer();
		if("ALL".equals(type)) {
			List<OrgData> OrgData = sysSetingService.findOrg(orgData);
			System.out.println("OrgData"+OrgData.size());
			String [] arrays = new String[OrgData.size()];
			for(int i=0;i<OrgData.size();i++) {
				arrays[i] = OrgData.get(i).getBANKCODE();
			}
			String [] newArrays = new String[orgs.length+arrays.length];
			System.arraycopy(orgs, 0, newArrays, 0, orgs.length);
			System.arraycopy(arrays, 0, newArrays, orgs.length, arrays.length);
		    for(int i=0;i<newArrays.length;i++){
		      if(!dataList.contains(newArrays[i])){
		    	  dataList.add(newArrays[i]);
		      }
		    }
			for(int i=0;i<dataList.size();i++) {
				excelTaskOrgData.setOrg(dataList.get(i));
				String flag = excelTaskService.addExcelTaskOrg(excelTaskOrgData);
				if(!"success".equals(flag)) {
					if(sf.length()==0) {
						sf.append(dataList.get(i));
					}else {
						sf.append(","+dataList.get(i));
					}
				}
			}
			if("2".equals(Report_cycle_type)) {//历史任务，结构关联后，直接下发任务
				getTask(dataList);
			}
		}else {
			for(int i=0;i<orgs.length;i++) {
				excelTaskOrgData.setOrg(orgs[i]);
				String flag = excelTaskService.addExcelTaskOrg(excelTaskOrgData);
				if(!"success".equals(flag)) {
					if(sf.length()==0) {
						sf.append(orgs[i]);
					}else {
						sf.append(","+orgs[i]);
					}
				}
			}
			if("2".equals(Report_cycle_type)) {//历史任务，结构关联后，直接下发任务
				Collections.addAll(dataList, orgs);
				getTask(dataList);
			}
		}
		if(sf.length()==0) {
			body.put("message", "机构维护成功!");
			body.put("code", "200");
		}else {
			body.put("message", sf.toString()+"机构维护失败!");
			body.put("code", "999");
		}
		return body;
	}
	
    @RequestMapping("/selectExcelTaskOrgByBatch")
    public Object selectExcelTaskOrgByBatch(
            @RequestParam(name = "batch", required = false, defaultValue = "") String batch){
    	return excelTaskService.selectExcelTaskOrgByBatch(batch);
    }
    
    @RequestMapping("/selectExcelTaskCheckOrgByBatch")
    public Object selectExcelTaskCheckOrgByBatch(
    		@RequestParam(name = "pageNum", required = false, defaultValue = "") int pageNum,
            @RequestParam(name = "pageSize", required = false, defaultValue = "") int pageSize,
            OrgData orgData){
		PageHelper.startPage(pageNum, pageSize);
		List<OrgData> data =  excelTaskService.selectExcelTaskCheckOrgByBatch(orgData);
		PageInfo result = new PageInfo(data);
		return result;
    }
	
    @RequestMapping("/selectExcelTaskReportByOrg")
    public Object selectExcelTaskReportByOrg(
            @RequestParam(name = "pageNum", required = false, defaultValue = "") int pageNum,
            @RequestParam(name = "pageSize", required = false, defaultValue = "") int pageSize,
            ExcelTaskReportOrgData excelTaskReportOrgData){
		PageHelper.startPage(pageNum, pageSize);
		List<ExcelTaskReportOrgData> dataList = excelTaskService.selectExcelTaskReportByOrg(excelTaskReportOrgData.getFileName(),
				excelTaskReportOrgData.getOrg(),excelTaskReportOrgData.getPaymentDay(),
				excelTaskReportOrgData.getStatus(),excelTaskReportOrgData.getBatch(),
				excelTaskReportOrgData.getPaymentDayTJ());
		PageInfo result = new PageInfo(dataList);
		return result;
    }
    
    @RequestMapping("/updateExcelTaskReportRemark")
    public Object updateExcelTaskReportRemark(
            @RequestParam(name = "id", required = false, defaultValue = "") String id,
            @RequestParam(name = "status", required = false, defaultValue = "") String status,
            @RequestParam(name = "remark", required = false, defaultValue = "") String remark ){
		Map<String, Object> body = new HashMap<>();
		String flag = excelTaskService.updateExcelTaskReportRemark(id, remark, status);
		if("success".equals(flag)) {
			body.put("message", "审核成功!");
			body.put("code", "200");
		}else {
			body.put("message", "审核失败!");
			body.put("code", "999");
		}
		return body;
    }  
    
	@RequestMapping("/addExcelNotice")
	public Object addExcelNotice(HttpServletRequest request,ExcelNoticeData ExcelNoticeData) throws Exception {
		Map<String, Object> body = new HashMap<>();
		String path = "";
		if("notEmpty".equals(ExcelNoticeData.getPath())) {
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			MultipartFile file = multipartRequest.getFile("file");
	        if (file.isEmpty()) {
				return "文件不能为空";
			}    
			path = fileUploadNotice(file,ExcelNoticeData.getUser());
		}
		ExcelNoticeData.setPath(path);
		String rsFlag = excelTaskService.addExcelNotice(ExcelNoticeData);
		if("success".equals(rsFlag)) {
			body.put("message", "通知新增成功!");
			body.put("code", "200");
		}else {
			body.put("message", "通知新增失败!");
			body.put("code", "999");
		}
		return body;
	}
	
	@RequestMapping("/selectExcelNotice")
	public Object selectExcelNotice(@RequestParam(name = "title", required = false, defaultValue = "") String title,
			@RequestParam(name = "status", required = false, defaultValue = "") String status,
			@RequestParam(name = "pageNum", required = false, defaultValue = "") int pageNum,
            @RequestParam(name = "pageSize", required = false, defaultValue = "") int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<ExcelNoticeData> dataList = excelTaskService.selectExcelNotice(title,status);
		PageInfo result = new PageInfo(dataList);
		return result;
	}
	
	@RequestMapping("/selectExcelNoticeOrgByNoticeId")
	public Object selectExcelNoticeOrgByNoticeId(
			@RequestParam(name = "pageNum", required = false, defaultValue = "") int pageNum,
            @RequestParam(name = "pageSize", required = false, defaultValue = "") int pageSize,
            OrgData orgData){
		PageHelper.startPage(pageNum, pageSize);
		List<OrgData> data =  excelTaskService.selectExcelNoticeOrgByNoticeId(orgData);
		PageInfo result = new PageInfo(data);
		return result;
	}
	
	@RequestMapping("/selectExcelNoticeByNoticeId")
	public Object selectExcelNoticeByNoticeId(@RequestParam(name = "notice_id", required = false, defaultValue = "") String notice_id){
		List<String> data =  excelTaskService.selectExcelNoticeByNoticeId(notice_id);
		return data;
	}
	
	@RequestMapping("/updateExcelNotice")
	public Object updateExcelNotice(HttpServletRequest request,ExcelNoticeData ExcelNoticeData) throws Exception {
		Map<String, Object> body = new HashMap<>();
		String path = "";
		if("notEmpty".equals(ExcelNoticeData.getPath())) {
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			MultipartFile file = multipartRequest.getFile("file");
	        if (file.isEmpty()) {
				return "文件不能为空";
			}    
			path = fileUploadNotice(file,ExcelNoticeData.getUser());
		}
		ExcelNoticeData.setPath(path);
        String rsFlag = excelTaskService.updateExcelNotice(ExcelNoticeData);
        if("success".equals(rsFlag)) {
			body.put("message", "通知更新成功!");
			body.put("code", "200");
		}else {
			body.put("message", "通知更新失败!");
			body.put("code", "999");
		}
		return body;
	}
	
	@RequestMapping("/deleteExcelNotice")
	public Object deleteExcelNotice(@RequestParam(name = "id", required = false, defaultValue = "") String id) {
		Map<String, Object> body = new HashMap<>();
        String rsFlag = excelTaskService.deleteExcelNotice(id);
        if("success".equals(rsFlag)) {
			body.put("message", "通知删除成功!");
			body.put("code", "200");
		}else {
			body.put("message", "通知删除失败!");
			body.put("code", "999");
		}
		return body;
	}
	
	@RequestMapping("/selectExcelNoticeByOrg")
	public Object selectExcelNoticeByOrg(@RequestParam(name = "title", required = false, defaultValue = "") String title,
			@RequestParam(name = "org", required = false, defaultValue = "") String org,
			@RequestParam(name = "pageNum", required = false, defaultValue = "") int pageNum,
            @RequestParam(name = "pageSize", required = false, defaultValue = "") int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<ExcelNoticeData> dataList = excelTaskService.selectExcelNoticeByOrg(title, org);
		PageInfo result = new PageInfo(dataList);
		return result;
	}
	
	@RequestMapping("/addExcelOrgNotice")
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public Object addExcelOrgNotice(@RequestBody Map map) throws Exception {
		Map<String, Object> body = new HashMap<>();
		ExcelNoticeData excelNoticeData = new ExcelNoticeData();
		OrgData orgData = new OrgData();
     	String org = (String) map.get("org"); 
     	String type = (String) map.get("type"); 
     	String user = (String) map.get("user");
     	String notice_id = (String) map.get("notice_id");
     	String BANK = (String) map.get("BANK");
     	String BANKTYPE = (String) map.get("BANKTYPE");
     	String AREA = (String) map.get("AREA");
     	String SUPERIORBANKCODE = (String) map.get("SUPERIORBANKCODE");
     	orgData.setBANK(BANK);
     	orgData.setBANKTYPE(BANKTYPE);
     	orgData.setAREA(AREA);
     	orgData.setSUPERIORBANKCODE(SUPERIORBANKCODE);
     	excelNoticeData.setUser(user);
     	excelNoticeData.setNotice_id(notice_id);
		List<String> rnList= excelTaskService.selectExcelNoticeByNoticeId(notice_id);
		if(rnList.size()!=0) {//修改
			excelTaskService.updateExcelOrgNotice(notice_id);
		}
		String [] orgs = org.split(",");
		System.out.println("orgs"+orgs.length);
		List <String>dataList = new ArrayList<String>();
		StringBuffer sf = new StringBuffer();
		if("ALL".equals(type)) {
			List<OrgData> OrgData = sysSetingService.findOrg(orgData);
			System.out.println("OrgData"+OrgData.size());
			String [] arrays = new String[OrgData.size()];
			for(int i=0;i<OrgData.size();i++) {
				arrays[i] = OrgData.get(i).getBANKCODE();
			}
			String [] newArrays = new String[orgs.length+arrays.length];
			System.arraycopy(orgs, 0, newArrays, 0, orgs.length);
			System.arraycopy(arrays, 0, newArrays, orgs.length, arrays.length);
		    for(int i=0;i<newArrays.length;i++){
		      if(!dataList.contains(newArrays[i])){
		    	  dataList.add(newArrays[i]);
		      }
		    }
			for(int i=0;i<dataList.size();i++) {
				excelNoticeData.setOrg(dataList.get(i));
				String flag = excelTaskService.addExcelOrgNotice(excelNoticeData);
				if(!"success".equals(flag)) {
					if(sf.length()==0) {
						sf.append(dataList.get(i));
					}else {
						sf.append(","+dataList.get(i));
					}
				}
			}
		}else {
			for(int i=0;i<orgs.length;i++) {
				excelNoticeData.setOrg(orgs[i]);
				String flag = excelTaskService.addExcelOrgNotice(excelNoticeData);
				if(!"success".equals(flag)) {
					if(sf.length()==0) {
						sf.append(orgs[i]);
					}else {
						sf.append(","+orgs[i]);
					}
				}
			}
		}
		if(sf.length()==0) {
			body.put("message", "机构维护成功!");
			body.put("code", "200");
		}else {
			body.put("message", sf.toString()+"机构维护失败!");
			body.put("code", "999");
		}
		return body;
	}
	
    
	// 文件下载相关代码
	@RequestMapping("/download")
	public Object downloadFile(HttpServletRequest request, HttpServletResponse response,
			@RequestBody Map map)
			throws Exception {
		Map<String, Object> body = new HashMap<>();
//		String fileName = "中国人民银行2017年度个人住房贷款抽样调查表.doc";// 设置文件名，根据业务需要替换成要下载的文件名
			// 设置文件路径
//			String realPath = "D:/upload";
		    String adress = (String)map.get("adress");
		    String file_name = (String)map.get("file_name");
		    if(file_name == "" || file_name == null) {
		    	String []loadFileName = adress.split("//"); //取文件名称
		    	file_name = loadFileName[loadFileName.length-1].split("\\.")[0];
		    }
			File file = new File(adress);
			if (file.exists()) {
				response.setContentType("application/vnd.ms-excel;charset=UTF-8");// 设置强制下载不打开
				response.addHeader("Content-Disposition",
						"attachment;fileName=" + URLEncoder.encode(file_name, "utf-8"));// 设置文件名
				byte[] buffer = new byte[1024];
				FileInputStream fis = null;
				BufferedInputStream bis = null;
				try {
					fis = new FileInputStream(file);
					bis = new BufferedInputStream(fis);
					OutputStream os = response.getOutputStream();
					int i = bis.read(buffer);
					while (i != -1) {
						os.write(buffer, 0, i);
						i = bis.read(buffer);
					}
					System.out.println("success");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (bis != null) {
						try {
							bis.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (fis != null) {
						try {
							fis.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}else {
				body.put("message", "下载地址不存在，下载失败!");
				body.put("code", "999");
			}
		return body;
	}
	
	/**
	 * 文件上传
	 **/
	public String fileUploadServer(MultipartFile file,String user) throws Exception{
		String t = String.valueOf(System.currentTimeMillis());// 生成一个时间搓
		String uuid = UUID.randomUUID().toString() + t.replaceAll("-", "").replaceAll(":", "").trim(); // 生成一个唯一的编码，批次号
		  try {
				  // 获取文件名
			  String fileName = file.getOriginalFilename();
		//		  logger.info("上传的文件名为：" + fileName);
			  // 获取文件的后缀名
		//			  String suffixName = fileName.substring(fileName.lastIndexOf("."));
		//		  LOG.info("文件的后缀名为：" + suffixName);
			 
			  // 设置文件存储路径
			  String filePath = "D://taskFile//" + fileName + "//" + user + "//" + uuid + "//";
			  String path = filePath + fileName;
			 
			  File dest = new File(path);
			  // 检测是否存在目录
			  if (!dest.getParentFile().exists()) {
			    dest.getParentFile().mkdirs();// 新建文件夹
			  }
			  file.transferTo(dest);// 文件写入
		      return path;
		  } catch (Exception e) {
		    e.printStackTrace();
		    throw new Exception("*****文件上传失败****"+e);
		  }
	}
	
	/**
	 * 文件上传
	 **/
	public String fileUploadNotice(MultipartFile file,String user) throws Exception{
		String t = String.valueOf(System.currentTimeMillis());// 生成一个时间搓
		String uuid = UUID.randomUUID().toString() + t.replaceAll("-", "").replaceAll(":", "").trim(); // 生成一个唯一的编码，批次号
		  try {
				  // 获取文件名
			  String fileName = file.getOriginalFilename();
		//		  logger.info("上传的文件名为：" + fileName);
			  // 获取文件的后缀名
		//			  String suffixName = fileName.substring(fileName.lastIndexOf("."));
		//		  LOG.info("文件的后缀名为：" + suffixName);
			 
			  // 设置文件存储路径
			  String filePath = "D://noticeFile//" + fileName + "//" + user + "//" + uuid + "//";
			  String path = filePath + fileName;
			 
			  File dest = new File(path);
			  // 检测是否存在目录
			  if (!dest.getParentFile().exists()) {
			    dest.getParentFile().mkdirs();// 新建文件夹
			  }
			  file.transferTo(dest);// 文件写入
		      return path;
		  } catch (Exception e) {
		    e.printStackTrace();
		    throw new Exception("*****文件上传失败****"+e);
		  }
	}
	
	public void getTask(List<String> orgs){
	  Date d = new Date();
      DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	  for(String org:orgs) {
				ExcelTaskReportOrgData excelTaskReportOrgData = new ExcelTaskReportOrgData();
			    excelTaskReportOrgData.setOrg(org);
				List<ExcelTaskReportOrgData> excelTaskDatas = excelTaskService.selectExcelTaskReportOrg(excelTaskReportOrgData);//查询机构下所有的任务			
				for(ExcelTaskReportOrgData e:excelTaskDatas) {//循环所有任务
					ExcelTaskReportOrgData data = new ExcelTaskReportOrgData();
					if("2".equals(e.getReportType())) { //临时任务
						data = excelTaskService.selectExcelTaskReport(e.getFileName(), "", e.getReportCycle(), e.getStartDate(),e.getOrg());
						if(data == null) {
							e.setPaymentDay(df.format(d));
							e.setCreate_date(df.format(d));
							excelTaskService.addExcelTaskReportOrg(e);
						}
					}
				 }
	   }
	}
	
    @RequestMapping("/selectExcelTaskReportByCount")
    public Object selectExcelTaskReportByCount(
            @RequestParam(name = "pageNum", required = false, defaultValue = "") int pageNum,
            @RequestParam(name = "pageSize", required = false, defaultValue = "") int pageSize,
            @RequestParam(name = "batch", required = false, defaultValue = "") String batch,
            @RequestParam(name = "paymentDay", required = false, defaultValue = "") String paymentDay){
		PageHelper.startPage(pageNum, pageSize);
		List<ExcelTaskReportOrgData> dataList = excelTaskService.selectExcelTaskReportByCount(batch,paymentDay);
		PageInfo result = new PageInfo(dataList);
		return result;
    }
    
    @RequestMapping("/batchAuditUpload")
    public Object batchAuditUpload(@RequestParam(name = "ids", required = false, defaultValue = "") String ids,
    		@RequestParam(name = "status", required = false, defaultValue = "") String status,
    		@RequestParam(name = "auType", required = false, defaultValue = "") String auType,
    		@RequestParam(name = "batch", required = false, defaultValue = "") String batch,
    		@RequestParam(name = "paymentDayTJ", required = false, defaultValue = "") String paymentDayTJ ){
    	Map<String, Object> body = new HashMap<>();
		boolean flag = false;
    	if("ALL".equals(auType)) {
    		List<ExcelTaskReportOrgData> dataList = excelTaskService.selectExcelTaskReportByOrg("",
    				"","","",batch,paymentDayTJ);
    		String []idArray = new String[dataList.size()];
    		for(int i=0;i<dataList.size();i++) {
    			idArray[i] = dataList.get(i).getId();
    		}
            flag = batchAuditBoolean(idArray,status);
    	}else {
        	String []idArray = ids.split(",");
        	flag = batchAuditBoolean(idArray,status);
    	}
    	if(!flag) {
    		body.put("message", "批量审批失败");
    		body.put("code", "999");
    	}else {
    		body.put("message", "批量审批成功");
    		body.put("code", "200");
    	}
		return body;
    }
    
    @Transactional
    public boolean batchAuditBoolean(String []idArray,String status) {
    	for(String id:idArray) {
    		String flag = excelTaskService.batchAuditUpload(id,status);
    		if(!"success".equals(flag)) {	
    		   return false;
    		}
    	}
        return true;
    }
    
}
