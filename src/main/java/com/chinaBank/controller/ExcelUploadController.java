package com.chinaBank.controller;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.chinaBank.bean.ExcelFileModel.ExcelBaseColData;
import com.chinaBank.bean.ExcelFileModel.ExcelBaseTabData;
import com.chinaBank.bean.ExcelFileModel.ExcelCheckRuleData;
import com.chinaBank.bean.ExcelFileModel.ExcelModelData;
import com.chinaBank.bean.ExcelFileModel.ExcelScriptInfoData;
import com.chinaBank.bean.ExcelTask.ExcelTaskReportOrgData;
import com.chinaBank.bean.ExcelUpload.UploadErrorData;
import com.chinaBank.bean.SysSeting.ModelConfigData;
import com.chinaBank.bean.SysSeting.OrgData;
import com.chinaBank.service.ExcelFileModel.ExcelFileModelService;
import com.chinaBank.service.ExcelTask.ExcelTaskService;
import com.chinaBank.service.ExcelUpload.ExcelUploadService;
import com.chinaBank.service.SysSeting.SysSetingService;
import com.chinaBank.utils.ImportUtil;
import com.chinaBank.utils.OrgExcelPoi;
import com.chinaBank.utils.ExcelPoi.ExcelReaderUtil;
import com.chinaBank.utils.test.TestService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@RestController
@RequestMapping(value = "/excelLoad")
public class ExcelUploadController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUploadController.class);

	@Autowired
	private ModelConfigData modelConfig;

	@Autowired
	private ExcelUploadService fileUploadService;
	
	@Autowired
	private SysSetingService sysSetingService;
	
	@Autowired
	private ExcelFileModelService excelService;
	
	@Autowired
	private ExcelTaskService excelTaskService;
	
	@Autowired
	private TestService testService;
	
	/**
	 * 下载导入模板
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/downloadExcel")
	public void downloadExcel(HttpServletResponse res, HttpServletRequest req) throws Exception {
		String fileName = "测试模板下载.xlsx";
		ServletOutputStream out;
		res.setContentType("multipart/form-data");
		res.setCharacterEncoding("UTF-8");
		res.setContentType("text/html");
		String filePath = modelConfig.getPath();
		String userAgent = req.getHeader("User-Agent");
		if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
			fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
		} else {
			// 非IE
			fileName = new String((fileName).getBytes("UTF-8"), "ISO-8859-1");
		}

		filePath = URLDecoder.decode(filePath, "UTF-8");
		res.setHeader("Content-Disposition", "attachment;fileName=" + fileName);
		FileInputStream inputStream = new FileInputStream(filePath);
		out = res.getOutputStream();
		int b = 0;
		byte[] buffer = new byte[1024];
		while ((b = inputStream.read(buffer)) != -1) {
			// 4.写到输出流(out)中
			out.write(buffer, 0, b);
		}
		inputStream.close();
		if (out != null) {
			out.flush();
			out.close();
		}

	}

	/*
	 * 请求上传的页面地址
	 */
	@GetMapping("/multiUpload")
	public String multiUpload() {
		return "multiUpload";
	}

	/*
	 * 上传
	 */
	@PostMapping("/multiUpload")
	public String multiUpload(HttpServletRequest request) {
		List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");	
		String filePath = "D:/upload/";
		File Pathfile = new File(filePath);
		if (!Pathfile.exists()) {
			Pathfile.mkdir();
		}
		for (int i = 0; i < files.size(); i++) {
			MultipartFile file = files.get(i);
			if (file.isEmpty()) {
				return "上传第" + (i++) + "个文件失败";
			}
			String fileName = file.getOriginalFilename();
			File dest = new File(filePath + fileName);
			if (!dest.getParentFile().exists()) { // 判断文件父目录是否存在
				dest.getParentFile().mkdir();
			}
			try {
				file.transferTo(dest);
				LOGGER.info("第" + (i + 1) + "个文件上传成功");
			} catch (IOException e) {
				LOGGER.error(e.toString(), e);
				return "上传第" + (i++) + "个文件失败";
			}
		}

		return "上传成功";

	}

	// 文件下载相关代码
	@RequestMapping("/download")
	public String downloadFile(HttpServletRequest request, HttpServletResponse response,@RequestBody Map map)
			throws Exception {
//		String fileName = "中国人民银行2017年度个人住房贷款抽样调查表.doc";// 设置文件名，根据业务需要替换成要下载的文件名
//		if (fileName != null) {
			// 设置文件路径
//			String realPath = "D:/upload";
//			File file = new File(realPath, fileName);
		    String adress = (String)map.get("adress");
		    String file_name = (String)map.get("file_name");
		    if(file_name == "" || file_name == null) {
		    	String []loadFileName = adress.split("//"); //取文件名称
		    	file_name = loadFileName[loadFileName.length-1].split("\\.")[0];
		    }
			File file = new File(adress);
			if (file.exists()) {
				response.setContentType("application/force-download");// 设置强制下载不打开
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
			}
//		}
		return null;
	}

	@Autowired
	private ImportUtil importUtil;

	/*
	 * 请求上传的页面地址
	 */
	@GetMapping("/upload")
	public String upload() {
		return "upload";
	}
	
	@PostMapping(value = "/excelIn", consumes = "multipart/*", headers = "content-type=multipart/form-data")
	public String addBlacklist(
	        @RequestParam("file") MultipartFile multipartFile, HttpServletRequest request
	) throws Exception {    //判断上传内容是否符合要求
	    //判断上传内容是否符合要求
	    String fileName = multipartFile.getOriginalFilename();
	    if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
	        return "上传的文件格式不正确";
	    }

//	    String files = saveFile(multipartFile, request);
//	    String files = "e:/20200119员工信息.xlsx";
//	    String files = "C:/Users/GTComputer/Desktop/20200119员工信息.xlsx";
//	    String files = "C:/Users/GTComputer/Desktop/分行员工信息.xls";
	    String files = "C:/Users/GTComputer/Desktop/20200119员工信息3.xlsx";
//	    ExcelReaderUtil.readExcel(files);
//	    int result = 0;
//	    try {
//	        result =  testService.addBlackLists(files);
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	    }finally {
//	        File file = new File(files);
//	        System.gc();
//	        boolean delSuccess = file.delete();
//	        if(delSuccess){
//	            System.out.println("删除文件成功");
//	        }else{
//	            System.out.println("删除文件失败");
//	        }
//	    }
//	    return String.valueOf(result);
	    return "haha";
	}	
	
	private String saveFile(MultipartFile multipartFile, HttpServletRequest request) {
	    String path;
	    String fileName = multipartFile.getOriginalFilename();
	    // 判断文件类型
	    //String realPath = request.getSession().getServletContext().getRealPath("/");
	    String trueFileName = fileName;
	    // 设置存放Excel文件的路径
	    path = "e:/" + trueFileName;
	    File file = new File(path);
	    if (file.exists() && file.isFile()) {
	        file.delete();
	    }
	    try {
	        multipartFile.transferTo(new File(path));
	    } catch (IOException e) {
	        e.printStackTrace();
	        throw new RuntimeException(e);
	    }

	    return path;
}
	
	@RequestMapping(value = "/upload")
//    @Async("taskExecutor")
	public String uploadExcel(HttpServletRequest request, 
			@RequestParam(name = "user", required = false, defaultValue = "") String user,
            @RequestParam(name = "org", required = false, defaultValue = "") String org,
            @RequestParam(name = "model_id", required = false, defaultValue = "") String model_id,
            @RequestParam(name = "reportId", required = false, defaultValue = "") String reportId,
            @RequestParam(name = "zq", required = false, defaultValue = "") String zq) throws Exception {
//		return "你已经传过来了";
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		MultipartFile file = multipartRequest.getFile("file");
		if (file.isEmpty()) {
			return "文件不能为空";
		}
//		InputStream inputStream = file.getInputStream();
		String info = getBankListByExcel(file,user,org,model_id,reportId,zq);
//		inputStream.close();
		if(!"success".equals(info)) {
			return info;
		}
		return "上传成功";
	}
	
    @RequestMapping("/findAllUploadErrorInfo")
    public Object findAllUploadErrorInfo(@RequestParam(name = "pageNum", required = false, defaultValue = "") int pageNum,
            @RequestParam(name = "pageSize", required = false, defaultValue = "") int pageSize,
            UploadErrorData data){
        return fileUploadService.findAllUploadErrorInfo(pageNum, pageSize, data);
    }
    
//    @ResponseBody
//    @RequestMapping("/selectUploadErrorInfoByFileName")
//    public Object selectUploadErrorInfoByFileName(@RequestBody Map map){
//    	String FileName = (String) map.get("FileName");
//    	String user = (String) map.get("user");
//        return fileUploadService.selectUploadErrorInfoByFileName(FileName, user);
//    }

//    @ResponseBody
//    @RequestMapping("/findAllUploadFileInfo")
//    public Object findAllUploadFileInfo(@RequestBody Map map) {
//    	int pageNum = (Integer) map.get("pageNum");
//    	int pageSize = (Integer) map.get("pageSize");
//    	String FileName = (String) map.get("FileName");
//    	String user = (String) map.get("user");
//        return fileUploadService.findAllUploadFileInfo(pageNum, pageSize, FileName, user);
//    }
    
//    @ResponseBody
//    @RequestMapping("/selectUploadFileInfoForCheck")
//    public Object selectUploadFileInfoForCheck(  @RequestParam(name = "pageNum", required = false, defaultValue = "") int pageNum,
//            @RequestParam(name = "pageSize", required = false, defaultValue = "") int pageSize,
//            UploadFileInfoData uploadFileInfoData) {
//		uploadFileInfoData.setStatus("Y");//查询上传成功
//		StringBuffer sf  = new StringBuffer();
//    	if(uploadFileInfoData.getOrg() != "") {
//    		sf.append("('" + uploadFileInfoData.getOrg().trim() + "')"); 		
//    	}else {
//    		UserData data = sysSetingService.selectUserByUserId(uploadFileInfoData.getUser(), "");
//			List<OrgData> orgsData = sysSetingService.selectOrgById(data.getOrg_id());
//			if (orgsData.size() >= 1) {//查询当前管理员下的所有机构信息
//				for(int i=0;i<orgsData.size();i++) {
//					selectAllOrg(orgsData.get(i),orgsData);
//				}
//			}	
//    		for(int i=0;i<orgsData.size();i++) {
//    			if(i == orgsData.size() -1) {
//    				sf.append("'" + orgsData.get(i).getBANKCODE().trim() +"')");
//    			}else {
//    				if(i == 0) {
//        				sf.append("('" + orgsData.get(i).getBANKCODE().trim() +"',");
//        				continue;
//    				}
//    				sf.append("'" + orgsData.get(i).getBANKCODE().trim() +"',");
//    			}
//    			
//    		}
//    	}
//    	uploadFileInfoData.setUser("");//是为了查询所属机构下的数据，这里必须清空用户信息
//        uploadFileInfoData.setOrgs(sf.toString());
//        return fileUploadService.selectUploadFileInfoForCheck(pageNum, pageSize, uploadFileInfoData);
//    }
			
	public void selectAllOrg(OrgData OrgDato,List<OrgData> orgs) {//循环取下级机构
		List<OrgData> rsDatas = sysSetingService.selectOrgById(OrgDato.getBANKCODE());
		if (rsDatas.size() >= 1) {
			for (int i = 0; i < rsDatas.size(); i++) {
				orgs.add(rsDatas.get(i));
				selectAllOrg(rsDatas.get(i),orgs);
			}
		}
    }
    
	@RequestMapping("/updateUploadErrorInfo")
	public Object updateUploadErrorInfo(@RequestBody Map map) {
		String reason = (String) map.get("reason");
		String id = (String) map.get("id");
		Map<String, Object> body = new HashMap<>();
		String rsFlag = fileUploadService.updateUploadErrorInfo(id,reason);
		if("success".equals(rsFlag)) {
			body.put("message", "说明更新成功!");
			body.put("code", "200");

		}else {
			body.put("message", "说明更新失败!");
			body.put("code", "999");
		}
		return body;
	}
	
//	@RequestMapping("/updateUploadFileInfo")
//	@ResponseBody
//	public Object updateUploadFileInfo(@RequestBody Map map) {
//		String check_type = (String) map.get("check_type");
//		String remark = (String) map.get("remark");
//		String id = (String) map.get("id");
//		Map<String, Object> body = new HashMap<>();
//		String rsFlag = fileUploadService.updateUploadFileInfo(id, check_type, remark);
//		if("success".equals(rsFlag)) {
//			body.put("message", "审核成功!");
//			body.put("code", "200");
//
//		}else {
//			body.put("message", "审核失败!");
//			body.put("code", "999");
//		}
//		return body;
//	}
	
	
    
	/*
	 * 请求vue界面
	 */
	@RequestMapping("/test")
	public String test() {
		return "/test";//html页面需要加“/”
	}
	
	
	/*
	 * 请求vue界面
	 */
	@RequestMapping("/index")
	public String index() {
		return "/index";//html页面需要加“/”
	}
	
	/*
	 * 请求vue界面
	 */
	@RequestMapping("/createTab")
	public String createTab() throws Exception {
		//支持四种数字类型：字符、整数、数值、日期
        //连接数据库
        Class.forName(modelConfig.getDriver());
        String url = modelConfig.getUrl();
        
        //测试url中是否包含useSSL字段，没有则添加设该字段且禁用
        if( url.indexOf("?") == -1 ){
            url = url + "?useSSL=false" ;
        }
        else if( url.indexOf("useSSL=false") == -1 || url.indexOf("useSSL=true") == -1 )
        {
            url = url + "&useSSL=false";
        }
        String userName = modelConfig.getUserName();
        String password = modelConfig.getPassword();
        Connection conn = DriverManager.getConnection(url, userName, password);
        Statement stat = conn.createStatement();
        //获取数据库表名
        ResultSet rs = conn.getMetaData().getTables(null, null, "sys_admin_divisions", null);
        
        // 判断表是否存在，如果存在则什么都不做，否则创建表
        if( rs.next() ){
            return "创建失败";
//        	System.out.print("建表失败");
        }
        else{
        	//show index from sys_admin_divisions;
            // 先判断是否纯在表名，有则先删除表在创建表
//            stat.executeUpdate("DROP TABLE IF EXISTS sys_admin_divisions;CREATE TABLE sys_admin_divisions("
            //创建行政区划表
            stat.executeUpdate("CREATE TABLE sys_admin_divisions("
                    + "ID varchar(32) NOT NULL COMMENT '行政区划ID(行政区划代码)这里不使用32位的UUID,使用全数字的行政区域代码作为ID(如:440000)',"
                    + "TYPE varchar(50) DEFAULT NULL COMMENT '类型（1省级 2市级 3区县）',"
                    + "CODE Decimal(20,3) DEFAULT NULL COMMENT '字母代码',"
                    + "NAME varchar(100) DEFAULT NULL COMMENT '名称',"
                    + "PINYIN varchar(100) DEFAULT NULL COMMENT '拼音',"
                    + "PARENT_ID varchar(32) DEFAULT NULL COMMENT '上级行政区划数字代码',"
                    + "IS_DISPLAY int(1) DEFAULT NULL COMMENT '是否显示( 0:否 1:是 )',"
                    + "SORT bigint(20) DEFAULT NULL COMMENT '排序标识',"
                    + "DEL_FLAG int(1) DEFAULT NULL COMMENT '删除标识(0:正常 1:已删除)',"
                    + "DEL_DATE date DEFAULT NULL COMMENT '日期',"
                    + "PRIMARY KEY (ID),"
                    + "index index_Code (CODE),"
                    + "index indexs_Name (NAME)"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='行政区划 (省市区)';"
                    );
        }
        // 释放资源
        stat.close();
        conn.close();
        return "创建表成功";
    }
	
	public Connection getConnection() throws Exception{
		Connection conn = null;
		if (null == conn) {
			try {
				Class.forName(modelConfig.getDriver());
		        String url = modelConfig.getUrl();        
		        //测试url中是否包含useSSL字段，没有则添加设该字段且禁用
		        if( url.indexOf("?") == -1 ){
		            url = url + "?useSSL=false" ;
		        }
		        else if( url.indexOf("useSSL=false") == -1 || url.indexOf("useSSL=true") == -1 )
		        {
		            url = url + "&useSSL=false";
		        }
		        String userName = modelConfig.getUserName();
		        String password = modelConfig.getPassword();
				conn =  DriverManager.getConnection(url, userName, password);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new Exception("***Connection初始化失败***，请检查配置信息*****"+e);
			}   
		}
		return conn;
	}

	/**
	 * 处理上传的文件
	 *
	 * @param in
	 * @param fileName
	 * @return
	 * @throws Exception 
	 * @throws Exception 
	 * @throws Exception
	 */
    @Async("taskExecutor")
	public String getBankListByExcel(MultipartFile Mufile,String user,String org,String model_id,String reportId,String zq) throws Exception {
    	ExcelTaskReportOrgData taskData = excelTaskService.selectExcelTaskReportById(reportId,"('Y','O','Z')");
		if(taskData!=null) {
			return "文件状态已经锁定,正在审核中,不能再次上传!";
		}
		ExcelTaskReportOrgData ITaskData = excelTaskService.selectExcelTaskReportById(reportId,"('I')");
		if(ITaskData!=null) {
			return "文件正在上传中,不能再次上传!";
		}
		excelTaskService.updateExcelTaskReportByOrg(reportId, "", "I",user);//表示正在上传
		//先将文件复制到本地，对本地文件进行操作，减少资源消耗
		File filePath = fileUploadServer(Mufile,user);
//		File f = new File(filePath);
		Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String temp = df.format(date);
		String sqlPath = "F:/upSql/"+temp+"/"+reportId;   // sql文件保存路径、名字
		File sqlDest = new File(sqlPath);
		String fileName = Mufile.getOriginalFilename();
		// 创建Excel工作薄
		InputStream in = new FileInputStream(filePath); 
//		XSSFWorkbook wb2 = 
//		XSSFWorkbook work = this.getWorkbook(in, fileName);
//		if (null == work) {
//		    if (filePath.exists() && filePath.isFile()) {
//		    	filePath.delete();
//		    }
//			return "创建Excel工作薄为空！";
//		}
		ExcelModelData excelModelDatas = excelService.selectExcelModelById(model_id); //判断上传的文件模板是否存在
		if (excelModelDatas == null) {
		    if (filePath.exists() && filePath.isFile()) {
		    	filePath.delete();
		    }
			return "没有相关的文件模板配置,请到文件配置页面进行文件配置操作!";
		}
		ExcelBaseTabData excelBaseTabData = new ExcelBaseTabData();
		excelBaseTabData.setModel_id(model_id);
		excelBaseTabData.setStatus("Y"); //查询有效的表
		List<ExcelBaseTabData> excelBaseTabDatas = excelService.selectBaseTabInfoByName(excelBaseTabData);// 通过模板ID拿到关联的有效表信息
		if (excelBaseTabDatas.size() == 0) {
		    if (filePath.exists() && filePath.isFile()) {
		    	filePath.delete();
		    }
			return "没有相关的表信息配置,请到模板配置页面进行相关模板下的表信息配置操作!";
		}
//		String t = String.valueOf(System.currentTimeMillis());// 生成一个时间搓
//		String uuid = UUID.randomUUID().toString() + t.replaceAll("-", "").replaceAll(":", "").trim(); // 生成一个唯一的编码，批次号
//		List<ExcelBaseTabData> excelBaseTabs = new ArrayList<ExcelBaseTabData>();//记录所有需要验证的表
		
	    for(int x = 0;x<excelBaseTabDatas.size();x++) {//循环所有的表,表是否生成了实体表,建立的所有非可用的表，再上传之前，都必须要求生成实体表
	    	ExcelScriptInfoData data = excelService.selectScriptInfoByTableName(excelBaseTabDatas.get(x).getTable_name(), "Y");
	    	if(data==null) {
	    		return "请到文件模板配置页面配置表信息，生成实体表后再上传!";
	    	}
	    }
		
//		String rnData = sheetFileNameIsNull(work, excelBaseTabDatas,excelBaseTabs);//判断所有的表是否已经生成了实体表
//		if(!"success".equals(rnData)) {
//		    if (filePath.exists() && filePath.isFile()) {
//		    	filePath.delete();
//		    }
//			return rnData;
//		}
	    
//	    ExcelReaderUtil.readExcel(sqlPath);
//        Date date = new Date();
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String ZQ = "DATE_FORMAT(" + "'" + df.format(date) + "','%Y-%m-%d')";
        ExcelReaderUtil ut = new ExcelReaderUtil();
        ut.readExcel(filePath.toString(), excelBaseTabDatas, reportId, ZQ, org, user,ut,excelService,fileUploadService);
//		String rsValue = writeFile(work, excelBaseTabDatas, reportId,sqlPath,org,user);//写入文件数据到表文件
//        if(!"true".equals(rsValue)) {
//		    if (filePath.exists() && filePath.isFile()) {
//		    	filePath.delete();
//		    }
//        	return rsValue;
//        }
//        if(!runSqlFile(sqlDest)) {//读取表文件，写入表数据到数据库(ok)
//		    if (filePath.exists() && filePath.isFile()) {
//		    	filePath.delete();
//		    }
//        	return "sql脚本执行错误";
//        };
		String loadFileName = fileName.split("\\.")[0]; //取文件名称
        String uploadData = uploadErrorInfoYesOrNo(loadFileName,excelBaseTabDatas,reportId,user,org,zq);
        String upFlag = uploadFinal(filePath.toString(),uploadData, excelBaseTabDatas, Mufile, user, reportId);
		if("true".equals(upFlag)) {
			return "success";
		}else {
		    if (filePath.exists() && filePath.isFile()) {
		    	filePath.delete();
		    }
		}
		in.close();
		return upFlag;
	}
	
	@Transactional
	public String uploadFinal(String uploadPath,String uploadFlag,List<ExcelBaseTabData> excelBaseTabs,MultipartFile Mufile,String user,String id) throws Exception {	
		//STATUS状态解释：U等待上传，N表示数据上传等待验证，X表示数据作废掉，I正在上传中
		//STATUS状态解释:   C表示有硬性校验验证失败，F表示验证出软性校验，但是没有硬性校验可以审核、可以重复上传，Z表示验证全部通过
		//STATUS状态解释:   O表示正在审核中，Y审核通过，T表示驳回数据
		boolean upFlag = true;
 		for(ExcelBaseTabData data:excelBaseTabs) { //将没有审核且有软性错误的数据，全部作废
 			String sql = "update " + data.getTable_name() + " t set t.STATUS = 'X' where t.BATCH = '" + id + "' and t.STATUS in ('F','C')";
 			fileUploadService.updateTable(sql);
 		}
		if(!"true".equals(uploadFlag)) {//处理软性或则硬性错误
         	if("false".equals(uploadFlag)) { //处理软性错误
         		for(ExcelBaseTabData data:excelBaseTabs) {//将最新上传有软性校验错误批次的数据全部改为有软性校验错误的状态
         			String sql = "update " + data.getTable_name() + " t set t.STATUS = 'F' where t.BATCH = '" + id + "' and t.STATUS = 'N'";
         			String flag = fileUploadService.updateTable(sql);
         			if(!"success".equals(flag)) {
         				 upFlag = false;
         			}
         		}
         		if(upFlag) {
//         			String uploadPath =fileUploadServer(Mufile,user);
         			excelTaskService.updateExcelTaskReportByOrg(id, uploadPath, "F",user);//F表示没有硬性校验，但是有软性校验，可以重复提交，可以等待验证
                 	return "上传文件有违反软性规则，请到规则详情页面进行说明填写！";
         		}
     				return "更新上传文件状态失败";
         	}
         	 //处理硬性错误
        	for(ExcelBaseTabData data:excelBaseTabs) {
             	String sql = "update " + data.getTable_name() + " t set t.STATUS = 'C' where t.BATCH = '" + id + "' and t.STATUS = 'N'";//c表示失败
     			String flag = fileUploadService.updateTable(sql);
     			if(!"success".equals(flag)) {
    				 upFlag = false;
    			}
        	}
        	if(upFlag) {
        		excelTaskService.updateExcelTaskReportByOrg(id, "", "C","");//上传失败,有硬性错误
             	return uploadFlag;
        	}
        		return "更新上传文件状态失败";
         }
		//通过了软性或则硬性错误验证，不存在任何错误
		 for(ExcelBaseTabData data:excelBaseTabs) {
			String sql = "update " + data.getTable_name() + " t set t.STATUS = 'Z' where t.BATCH = '" + id + "' and t.STATUS = 'N'";
			String flag = fileUploadService.updateTable(sql);
			if(!"success".equals(flag)) {
				 upFlag = false;
			}
		 }
		 if(upFlag) {
//			String uploadPath =fileUploadServer(Mufile,user);
  			excelTaskService.updateExcelTaskReportByOrg(id, uploadPath, "Z",user);
  			return uploadFlag;
		 }else {
			return "更新上传文件状态失败";
		 }
	}
	
	public String sheetFileNameIsNull(Workbook work,List<ExcelBaseTabData> excelBaseTabDatas,List<ExcelBaseTabData> excelBaseTabs) {
		Sheet sheet = null;
		int sheetCount = work.getNumberOfSheets();
		for (int e = 0; e < sheetCount; e++) { // 循环的是多个sheet页签
			sheet = work.getSheetAt(e);
			String sheetName = sheet.getSheetName();
		    for(int x = 0;x<excelBaseTabDatas.size();x++) {//循环所有的表与当前页签进行匹配
				if(sheetName.equals(excelBaseTabDatas.get(x).getSheet_name())) { //匹配页签名称，必须完全匹配上
			    	excelBaseTabs.add(excelBaseTabDatas.get(x));
			    	ExcelScriptInfoData data = excelService.selectScriptInfoByTableName(excelBaseTabDatas.get(x).getTable_name(), "Y");
			    	if(data==null) {
			    		return "请到文件模板配置页面配置表信息，生成实体表后再上传!";
			    	}
			    }
		    }
		}
		return "success";
	}
	
	/**
	 * 文件上传
	 **/
	public File fileUploadServer(MultipartFile file,String user) throws Exception{
		String t = String.valueOf(System.currentTimeMillis());// 生成一个时间搓
		String uuid = UUID.randomUUID().toString() + t.replaceAll("-", "").replaceAll(":", "").trim(); // 生成一个唯一的编码，批次号
		  try {
				  // 获取文件名
			  String fileName = file.getOriginalFilename();
	//		  logger.info("上传的文件名为：" + fileName);
			  // 获取文件的后缀名
//			  String suffixName = fileName.substring(fileName.lastIndexOf("."));
	//		  LOG.info("文件的后缀名为：" + suffixName);
			  String name = fileName.split("\\.")[0];
			  // 设置文件存储路径
			  String filePath = "D://chinaBankFile//" + name + "//" + user + "//" + uuid + "//";
			  String path = filePath + fileName;
			 
			  File dest = new File(path);
			  // 检测是否存在目录
			  if (!dest.getParentFile().exists()) {
			    dest.getParentFile().mkdirs();// 新建文件夹
			  }
			  file.transferTo(dest);// 文件写入
		      return dest;
		  } catch (Exception e) {
		    e.printStackTrace();
		    throw new Exception("*****文件上传失败****"+e);
		  }
		}
	
	/**
	 * 处理违规数据
	 **/
	@Transactional
	public String uploadErrorInfoYesOrNo(String loadFileName,List<ExcelBaseTabData> excelBaseTabDatas,String uuid,String user,String org,String zq) {
		fileUploadService.updateUploadErrorInfoByBatch(uuid);//将当前批次下旧的错误信息状态改为无效
		List<String> list = new ArrayList<String>();
    	Map<String, Object> map = new HashMap<>();
		StringBuffer dataTable = new StringBuffer(); // 存储插入字段
		dataTable.append("INSERT INTO dis_upload_error_info"+
	      "(id,"+
	      "file_name,"+
	      "check_type,"+
	      "rule_id,"+
	      "err_Info,"+
	      "user,"+
	      "batch,"+
	      "ROWNUM,"+
	      "sheet_name,"+
	      "status,"+
	      "org,"+
	      "zq,"+
	      "create_date"+
	      ")");
		 map.put("col", dataTable.toString()+ "values");
		int countError = 0;//提交条数
		boolean checkFlag = false; //记录是否有硬性校验不通过
		boolean check = false; //记录是否有软性校验不通过
		for(ExcelBaseTabData tab:excelBaseTabDatas) {
			String table_name = tab.getTable_name();
			String sheet_name = tab.getSheet_name();
			List<ExcelBaseColData> excelBaseColDatas = excelService.selectcolumnsInfoByTabId(tab.getId()); // 通过表拿到所有列
			if (excelBaseColDatas.size() == 0) {
				return "没有相关的列信息配置,请到模板配置页面进行相关模板下的表信息列配置操作!";
			}
			for (int i = 0; i < excelBaseColDatas.size(); i++) { // 将所有列的英文编码转换成数字形式列
				int colIndex = excelColStrToNum(excelBaseColDatas.get(i).getRead_col(),excelBaseColDatas.get(i).getRead_col().length());
				excelBaseColDatas.get(i).setIndex(colIndex - 1);
			}
			ExcelCheckRuleData data = new ExcelCheckRuleData();
			data.setTable_id(tab.getId());
			data.setModel_id(tab.getModel_id());
	    	List<ExcelCheckRuleData> reRules = excelService.findAllCheckRulesInfo(data); //表下面所有的规则
			if(reRules.size() >= 0) {
				for (int g = 0; g < reRules.size(); g++) { // 首先循环所有规则，将所有的规则用@关联的列转换成真实的列，并将每一个规则单独去进行校验
					String check_type = reRules.get(g).getCheck_type();
					String errorInfo = reRules.get(g).getError_info();
					if (reRules.get(g).getStatus().equals("Y")) { // 校验有效才做校验，否则不做任何校验操作
						String sql;
						String rule = reRules.get(g).getCheck_rule();
						if (reRules.get(g).getCheck_class().equals("sql")) { // sql校验     必须携程   select e.ID from mytest.table1 e where e.@A = e.@B   这种格式 ,这里取反相关
							for (int j = 0; j < excelBaseColDatas.size(); j++) { // 循环所有的列
								rule = rule.replaceAll("@" + excelBaseColDatas.get(j).getRead_col(),excelBaseColDatas.get(j).getColumn_name());
							}
							sql = "select ROWNUM from " + table_name + " t where not EXISTS (" + rule + " and t.ID = e.ID and e.BATCH = '"+ uuid +"') and t.BATCH = '"+ uuid +"' and STATUS = 'N'";
						}else { // 非sql校验
							for (int j = 0; j < excelBaseColDatas.size(); j++) { // 循环所有的列
								rule = rule.replaceAll("@" + excelBaseColDatas.get(j).getRead_col(),"e." + excelBaseColDatas.get(j).getColumn_name());
							}
							sql = "select ROWNUM from " + table_name + " t where not EXISTS (" + "select e.ID from " + table_name + " e where " + rule + " and t.ID = e.ID and e.BATCH = '"+ uuid +"') and t.BATCH = '"+ uuid +"' and STATUS = 'N'";
						}
						List<UploadErrorData> errData= fileUploadService.selectCountCols(sql); //能查询到数据，表示校验未通过，否则此条规则通过
						if (errData.size() != 0) { // 查询到数据，表示有违规数据
							if(reRules.get(g).getCheck_type().equals("Y")) {//当前是硬性校验不通过
								checkFlag = true;
							}
							if(reRules.get(g).getCheck_type().equals("N")) {//软性校验不通过
								check = true;
							}
							String ruleId = reRules.get(g).getId();
							for(int i=0;i<errData.size();i++) {
								countError++;
								StringBuffer dataValues = new StringBuffer(); // 存储插入字段相对应的值
								dataValues.append("(uuid(),");
								dataValues.append("'"+loadFileName+"',");
								dataValues.append("'"+check_type+"',");
								dataValues.append("'"+ruleId+"',");
								dataValues.append("'"+errorInfo+"',");
								dataValues.append("'"+user+"',");
								dataValues.append("'"+uuid+"',");
								dataValues.append("'"+errData.get(i).getROWNUM()+"',");
								dataValues.append("'"+sheet_name+"',");
								dataValues.append("'Y',");
								dataValues.append("'"+org+"',");
								dataValues.append("'"+zq+"',");
								dataValues.append("now())");
								list.add(dataValues.toString());	
								if(countError==5000) {
									map.put("listData", list);
									String flag = fileUploadService.insertListData(map);
									if("success" != flag) {
										return "插入错误信息失败";
									}
									list.clear();
									countError = 0;
								}
								if(i==errData.size()-1) {
									map.put("listData", list);
									String flag = fileUploadService.insertListData(map);
									if("success" != flag) {
										return "插入错误信息失败";
									}
									list.clear();
									countError = 0;
								}
//								 #{file_name},
//							      #{check_type},
//							      #{rule_id},
//							      #{err_Info},
//							      #{user},
//							      #{batch},
//							      #{ROWNUM},
//							      #{sheet_name},
//							      #{status},
//							      #{org},
//							      #{zq},
//							      now()
//								uploadErrorData.setUser(user);
//								uploadErrorData.setFile_name(loadFileName);
//								uploadErrorData.setRule_id(ruleId);
//								uploadErrorData.setErr_Info(reRules.get(g).getError_info());
//								uploadErrorData.setBatch(uuid);
//								uploadErrorData.setStatus("Y");//y表示最新数据
//								uploadErrorData.setSheet_name(sheet_name);
//								uploadErrorData.setOrg(org);
//								uploadErrorData.setZq(zq);
//								uploadErrorData.setCheck_type(reRules.get(g).getCheck_type());
//								fileUploadService.addUploadErrorInfo(uploadErrorData);//记录违反规则的信息到错误信息表
							}
						}
					}
				}
			}
//			if (batch != "") {
//				fileUploadService.deleteUploadErrorInfo(batch,""); // 根据批次号删除掉上次所有的违规规则校验
//			}
		}
		if (checkFlag) {//只要有硬性校验不通过，直接返回错误信息		
			return "上传文件有违反硬性规则，上传失败，可以到规则详情页面进行违规信息查询！";
		}else {//没有硬性错误，只有软性，或则没有任何错误
			if(check) {//有软性错误
				return "false";
			}
		}
		return "true";
	}
	
	/**
	 * 将excel数据写入sql文件
	 * @throws Exception 
	 **/
	public String writeFile(Workbook work,List<ExcelBaseTabData> excelBaseTabDatas,String uuid,String path,String org,String user) throws Exception{
		Sheet sheet = null;
		BufferedWriter ow = null;
		int sheetCount = work.getNumberOfSheets();
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String ZQ = "DATE_FORMAT(" + "'" + df.format(date) + "','%Y-%m-%d')";
		for (int e = 0; e < sheetCount; e++) { // 循环的是多个sheet页签
			sheet = work.getSheetAt(e);
			String sheetName = sheet.getSheetName();
			StringBuffer sf = new StringBuffer(); // 存储插入的字段
			List<Object> listData = new ArrayList<Object>();
		    for(int x = 0;x<excelBaseTabDatas.size();x++) {//循环所有的表与当前页签进行匹配
				String table_name = excelBaseTabDatas.get(x).getTable_name();
				int readRow = Integer.parseInt(excelBaseTabDatas.get(x).getRead_row());
			    if(sheetName.equals(excelBaseTabDatas.get(x).getSheet_name())) {//这里用正则，页签匹配上做相应的规则校验
			    	//匹配上表之后，立马将公共的字段放入要插入的脚本中
//			    	UploadData up = new UploadData();
			    	Map<String, Object> map = new HashMap<>();
					sf.append("INSERT INTO " + table_name + "(" + " ID,BATCH,ROWNUM,STATUS,ZQ,ORG,USER,CREATED_DATE,");
			    	List<ExcelBaseColData> excelBaseColDatas = excelService.selectcolumnsInfoByTabId(excelBaseTabDatas.get(x).getId()); // 通过表拿到所有列
					for (int i = 0; i < excelBaseColDatas.size(); i++) { // 将所有列的英文编码转换成数字形式列
						int colIndex = excelColStrToNum(excelBaseColDatas.get(i).getRead_col(),excelBaseColDatas.get(i).getRead_col().length());
						excelBaseColDatas.get(i).setIndex(colIndex - 1);
					}
			    	String error = ""; //返回错误信息
					Row row = null;
					StringBuffer dataBuf = new StringBuffer();
					boolean colFlag = false; //终止所有循环
					int rowFirst = readRow -1;//循环开始的行数
					int count = 0;
					List<String> list = new ArrayList<String>();
					for( int j = rowFirst; j <= sheet.getLastRowNum() ; j++ ){// 循环的是行
						count++;
						if(colFlag) {
							break;
						}
						row = sheet.getRow(j);
						if (row == null) {    // || row.getFirstCellNum() == j 默认不读第一行
							continue;
						}

						StringBuffer sfValues = new StringBuffer(); // 存储插入字段相对应的值
   
						sfValues.append("(uuid(),'" + uuid + "','"+ (j+1) + "','N',"+ZQ+",'"+org+"','"+user+"',now(),");
						for (int z = 0; z < excelBaseColDatas.size(); z++) { // 循环配置列
							if(colFlag) {
								break;
							}
							for( int y = row.getFirstCellNum() ; y < row.getLastCellNum() ; y++ ){ // 循环的excel文件列
								String cellValue = getCellValue(row.getCell(y));
								if (y == excelBaseColDatas.get(z).getIndex() && z != excelBaseColDatas.size()-1) {// 列号相同，就表示同一列,取得是除最后一行的其他行
									if(j == rowFirst) {//循环第一行数据的时候才做拼接操作，之后循环不用做
										sf.append(excelBaseColDatas.get(z).getColumn_name() + ",");	
									}
									if(excelBaseColDatas.get(z).getIsNull().equals("NOT NULL") && cellValue == "") {
										error = excelBaseColDatas.get(z).getColumn_desc()+"必须有值，请检查！";
										colFlag = true;
										break;
									}else {
										if(cellValue.startsWith("DATE_FORMAT")) { //表示是日期
											sfValues.append(cellValue + ",");
										}else {
											if(cellValue == "") {
												sfValues.append("null,");
											}else {
												sfValues.append("'" + cellValue + "'" + ",");
											}
										}
									}
								}
								if (y == excelBaseColDatas.get(z).getIndex() && z == excelBaseColDatas.size()-1) {// 列号相同，就表示同一列,最后一行
									if(j == rowFirst) {//循环第一行数据的时候才做拼接操作，之后循环不用做
									   sf.append(excelBaseColDatas.get(z).getColumn_name() + ")");	
//									   up.setColsData(sf.toString()+ "values");
									   map.put("col", sf.toString()+ "values");
									}
									if(excelBaseColDatas.get(z).getIsNull().equals("NOT NULL") && cellValue == "") {
										error = excelBaseColDatas.get(z).getColumn_desc()+"必须有值，请检查！";
										colFlag = true;
										break;
									}else {
										if(cellValue.startsWith("DATE_FORMAT")) { //表示是日期
											sfValues.append(cellValue + ");");
										}else {
											if(cellValue == "") {
												sfValues.append("null)");
											}else {
												sfValues.append("'" + cellValue + "'" + ")");
											}
										}
									}			
								}
							}
						}
//						sf.append("values ").append(sfValues.toString()); // 拼接插入脚本
//						dataBuf.append(sf.toString() + "\n");// 换行
						list.add(sfValues.toString());				
						if(5000 == count) {
							map.put("listData", list);
							String flag = fileUploadService.insertListData(map);
							if("success" == flag) {
								count = 0;
								list.clear();//清空list
							}else {
								return "插入数据失败";
							}

						}
						if(j == sheet.getLastRowNum()) { //最后一行
							map.put("listData", list);
							String flag = fileUploadService.insertListData(map);
							if("success" == flag) {
								count = 0;
								list.clear();//清空list
							}else {
								return "插入数据失败";
							}
						}
					}
					if(colFlag) {
						return error;
					}		
//					try {
//				        String newPath = path+"/"+sheetName+".sql";
//						File file = new File(newPath);
//					    if(!file.getParentFile().exists()) {
//						  file.getParentFile().mkdirs();// 新建文件夹
//						}
//						if(!file.exists()){     
//						   file.createNewFile();    
//						}
//						ow = new BufferedWriter(new FileWriter(file));
//						ow.write(dataBuf.toString());
//						ow.flush();// 将脚本以sql文件保存到指定路径下
//					} catch (Exception ex) {
//						// TODO Auto-generated catch block
//						ex.printStackTrace();
//						File dest = new File(path);
//						delFile(dest); //报错就删除所有生成的数据文件
//						throw new Exception("***将脚本以sql文件保存到指定路径下发生错误***"+ex);
//					} finally {
//						ow.close();
//					}
			    }
		    }
		}
		return "true";
	}
	
	 public boolean delFile(File file) {//删除文件
        if (!file.exists()) {
             return false;
        }
        if (file.isFile()) {
            return file.delete();
        } else {
             File[] files = file.listFiles();
             for (File f : files) {
                 delFile(f);
             }
             return file.delete();
         }
	  }
	
	/**
	 * 执行sql文件脚本，讲数据导入数据库
	 * @throws Exception 
	 **/
	public boolean runSqlFile(File path) throws Exception{
		Connection conn = null;
		ScriptRunner runner = null;
		boolean falg = false;
		try {
			conn = getConnection();
			runner = new ScriptRunner(conn);
			// 下面配置不要随意更改，否则会出现各种问题
			runner.setAutoCommit(true);// 自动提交
			runner.setFullLineDelimiter(false);
	//		runner.setDelimiter(";");//// 每条命令间的分隔符
			runner.setSendFullScript(false);
			runner.setStopOnError(false);
			// runner.setLogWriter(null);//设置是否输出日志
			// 如果又多个sql文件，可以写多个runner.runScript(xxx),
            File[] files = path.listFiles();
            for (File f : files) {
    			runner.runScript(new InputStreamReader(new FileInputStream(f), "utf-8"));
            }
			falg = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception("------执行sql脚本异常-----"+e);
		} finally {
			conn.close();
			runner.closeConnection();
		}
		return falg;
	}

	/**
	 * 根据数据类型返回列值
	 */
	public String getCellValue(Cell cell) {
	    String temp = "";
	    if (cell == null) {
	      return temp;
	    }
	    switch (cell.getCellType()) {
	      case Cell.CELL_TYPE_STRING:
	        return cell.getRichStringCellValue().getString();
	      case Cell.CELL_TYPE_NUMERIC:
	        if (DateUtil.isCellDateFormatted(cell)) {
	          Date date = cell.getDateCellValue();
	          DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//	          temp = df.format(date);
	          temp = "DATE_FORMAT(" + "'" + df.format(date) + "','%Y-%m-%d')";
	          return temp;
	        } else {
	          return String.valueOf(cell.getNumericCellValue());
	        }
	      case Cell.CELL_TYPE_FORMULA:
	        cell.setCellType(Cell.CELL_TYPE_STRING);
	        return cell.getStringCellValue();
	      default:
	        return temp;
	    }
	}

	/**
	 * 判断文件格式
	 *
	 * @param inStr
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public XSSFWorkbook getWorkbook(InputStream inStr, String fileName) throws Exception {
////		Workbook workbook = null;
//		SXSSFWorkbook wb1 = new SXSSFWorkbook();
////		String fileType = fileName.substring(fileName.lastIndexOf("."));
////		if (".xls".equals(fileType)) {
////			workbook = new HSSFWorkbook(inStr);
////		} else if (".xlsx".equals(fileType)) {
//////			workbook = new XSSFWorkbook(inStr);
//		wb1 = new SXSSFWorkbook(inStr); 
////		} else {
////			throw new Exception("请上传excel文件！");
////		}
		return  new XSSFWorkbook(inStr);
	}

	/**
	 * Excel column index begin 1
	 * 
	 * @param colStr
	 * @param length
	 * @return
	 */
	public static int excelColStrToNum(String colStr, int length) {
		int num = 0;
		int result = 0;
		for (int i = 0; i < length; i++) {
			char ch = colStr.charAt(length - i - 1);
			num = (int) (ch - 'A' + 1);
			num *= Math.pow(26, i);
			result += num;
		}
		return result;
	}

	/**
	 * Excel column index begin 1
	 * 
	 * @param columnIndex
	 * @return
	 */
	public static String excelColIndexToStr(int columnIndex) {
		if (columnIndex <= 0) {
			return null;
		}
		String columnStr = "";
		columnIndex--;
		do {
			if (columnStr.length() > 0) {
				columnIndex--;
			}
			columnStr = ((char) (columnIndex % 26 + (int) 'A')) + columnStr;
			columnIndex = (int) ((columnIndex - columnIndex % 26) / 26);
		} while (columnIndex > 0);
		return columnStr;
	}
	
	/**
	 * 将excel数据写入sql文件
	 * @throws Exception 
	 **/
	public String writeFileNew(Workbook work,List<ExcelBaseTabData> excelBaseTabDatas,String uuid,String path,String org,String user) throws Exception{
		Sheet sheet = null;
		BufferedWriter ow = null;
		int sheetCount = work.getNumberOfSheets();
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String ZQ = "DATE_FORMAT(" + "'" + df.format(date) + "','%Y-%m-%d')";
		for (int e = 0; e < sheetCount; e++) { // 循环的是多个sheet页签
			sheet = work.getSheetAt(e);
			String sheetName = sheet.getSheetName();
			StringBuffer sf = new StringBuffer(); // 存储插入的字段
			List<Object> listData = new ArrayList<Object>();
		    for(int x = 0;x<excelBaseTabDatas.size();x++) {//循环所有的表与当前页签进行匹配
				String table_name = excelBaseTabDatas.get(x).getTable_name();
				int readRow = Integer.parseInt(excelBaseTabDatas.get(x).getRead_row());
			    if(sheetName.equals(excelBaseTabDatas.get(x).getSheet_name())) {//这里用正则，页签匹配上做相应的规则校验
			    	//匹配上表之后，立马将公共的字段放入要插入的脚本中
//			    	UploadData up = new UploadData();
			    	Map<String, Object> map = new HashMap<>();
					sf.append("INSERT INTO " + table_name + "(" + " ID,BATCH,ROWNUM,STATUS,ZQ,ORG,USER,CREATED_DATE,");
			    	List<ExcelBaseColData> excelBaseColDatas = excelService.selectcolumnsInfoByTabId(excelBaseTabDatas.get(x).getId()); // 通过表拿到所有列
					for (int i = 0; i < excelBaseColDatas.size(); i++) { // 将所有列的英文编码转换成数字形式列
						int colIndex = excelColStrToNum(excelBaseColDatas.get(i).getRead_col(),excelBaseColDatas.get(i).getRead_col().length());
						excelBaseColDatas.get(i).setIndex(colIndex - 1);
					}
			    	String error = ""; //返回错误信息
					Row row = null;
					StringBuffer dataBuf = new StringBuffer();
					boolean colFlag = false; //终止所有循环
					int rowFirst = readRow -1;//循环开始的行数
					int count = 0;
					List<String> list = new ArrayList<String>();
					for( int j = rowFirst; j <= sheet.getLastRowNum() ; j++ ){// 循环的是行
						count++;
						if(colFlag) {
							break;
						}
						row = sheet.getRow(j);
						if (row == null) {    // || row.getFirstCellNum() == j 默认不读第一行
							continue;
						}

						StringBuffer sfValues = new StringBuffer(); // 存储插入字段相对应的值
   
						sfValues.append("(uuid(),'" + uuid + "','"+ (j+1) + "','N',"+ZQ+",'"+org+"','"+user+"',now(),");
						for (int z = 0; z < excelBaseColDatas.size(); z++) { // 循环配置列
							if(colFlag) {
								break;
							}
							for( int y = row.getFirstCellNum() ; y < row.getLastCellNum() ; y++ ){ // 循环的excel文件列
								String cellValue = getCellValue(row.getCell(y));
								if (y == excelBaseColDatas.get(z).getIndex() && z != excelBaseColDatas.size()-1) {// 列号相同，就表示同一列,取得是除最后一行的其他行
									if(j == rowFirst) {//循环第一行数据的时候才做拼接操作，之后循环不用做
										sf.append(excelBaseColDatas.get(z).getColumn_name() + ",");	
									}
									if(excelBaseColDatas.get(z).getIsNull().equals("NOT NULL") && cellValue == "") {
										error = excelBaseColDatas.get(z).getColumn_desc()+"必须有值，请检查！";
										colFlag = true;
										break;
									}else {
										if(cellValue.startsWith("DATE_FORMAT")) { //表示是日期
											sfValues.append(cellValue + ",");
										}else {
											if(cellValue == "") {
												sfValues.append("null,");
											}else {
												sfValues.append("'" + cellValue + "'" + ",");
											}
										}
									}
								}
								if (y == excelBaseColDatas.get(z).getIndex() && z == excelBaseColDatas.size()-1) {// 列号相同，就表示同一列,最后一行
									if(j == rowFirst) {//循环第一行数据的时候才做拼接操作，之后循环不用做
									   sf.append(excelBaseColDatas.get(z).getColumn_name() + ")");	
//									   up.setColsData(sf.toString()+ "values");
									   map.put("col", sf.toString()+ "values");
									}
									if(excelBaseColDatas.get(z).getIsNull().equals("NOT NULL") && cellValue == "") {
										error = excelBaseColDatas.get(z).getColumn_desc()+"必须有值，请检查！";
										colFlag = true;
										break;
									}else {
										if(cellValue.startsWith("DATE_FORMAT")) { //表示是日期
											sfValues.append(cellValue + ");");
										}else {
											if(cellValue == "") {
												sfValues.append("null)");
											}else {
												sfValues.append("'" + cellValue + "'" + ")");
											}
										}
									}			
								}
							}
						}
//						sf.append("values ").append(sfValues.toString()); // 拼接插入脚本
//						dataBuf.append(sf.toString() + "\n");// 换行
						list.add(sfValues.toString());				
						if(5000 == count) {
							map.put("listData", list);
							String flag = fileUploadService.insertListData(map);
							if("success" == flag) {
								count = 0;
								list.clear();//清空list
							}else {
								return "插入数据失败";
							}

						}
						if(j == sheet.getLastRowNum()) { //最后一行
							map.put("listData", list);
							String flag = fileUploadService.insertListData(map);
							if("success" == flag) {
								count = 0;
								list.clear();//清空list
							}else {
								return "插入数据失败";
							}
						}
					}
					if(colFlag) {
						return error;
					}		
//					try {
//				        String newPath = path+"/"+sheetName+".sql";
//						File file = new File(newPath);
//					    if(!file.getParentFile().exists()) {
//						  file.getParentFile().mkdirs();// 新建文件夹
//						}
//						if(!file.exists()){     
//						   file.createNewFile();    
//						}
//						ow = new BufferedWriter(new FileWriter(file));
//						ow.write(dataBuf.toString());
//						ow.flush();// 将脚本以sql文件保存到指定路径下
//					} catch (Exception ex) {
//						// TODO Auto-generated catch block
//						ex.printStackTrace();
//						File dest = new File(path);
//						delFile(dest); //报错就删除所有生成的数据文件
//						throw new Exception("***将脚本以sql文件保存到指定路径下发生错误***"+ex);
//					} finally {
//						ow.close();
//					}
			    }
		    }
		}
		return "true";
	}

    @RequestMapping("/selectUploadErrorInfoByBatch")
    public Object selectUploadErrorInfoByBatch(
    		@RequestParam(name = "reportId", required = false, defaultValue = "") String reportId,
    		@RequestParam(name = "pageNum", required = false, defaultValue = "") int pageNum,
            @RequestParam(name = "pageSize", required = false, defaultValue = "") int pageSize){
		PageHelper.startPage(pageNum, pageSize);
		List<UploadErrorData> uploadErrorData =fileUploadService.selectUploadErrorInfoByBatch(reportId);
		for(UploadErrorData d:uploadErrorData) {
			String remark = fileUploadService.selectUploadErrorDataByBatch(reportId,d.getRule_id());
			d.setReason(remark);
		}
		PageInfo result = new PageInfo(uploadErrorData);
		return result;
    }
    
    @RequestMapping("/addUploadErrorData")
    public Object addUploadErrorData(
    		@RequestParam(name = "reportId", required = false, defaultValue = "") String reportId,
    		@RequestParam(name = "ruleId", required = false, defaultValue = "") String ruleId,
    		@RequestParam(name = "errInfo", required = false, defaultValue = "") String errInfo,
    		@RequestParam(name = "reason", required = false, defaultValue = "") String reason,
    		@RequestParam(name = "upUser", required = false, defaultValue = "") String upUser)
    {
	        String flag = fileUploadService.addUploadErrorData(reportId,ruleId,errInfo,reason,upUser);
	        if("success".equals(flag)) {
	        	return "填写说明成功";
	        }else {
	        	return "填写说明失败";
	        }
    }

}