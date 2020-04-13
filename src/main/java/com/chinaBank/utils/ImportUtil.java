package com.chinaBank.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.chinaBank.bean.ExcelFileModel.ExcelBaseColData;
import com.chinaBank.bean.ExcelFileModel.ExcelBaseTabData;
import com.chinaBank.bean.ExcelFileModel.ExcelCheckRuleData;
import com.chinaBank.bean.ExcelFileModel.ExcelModelData;
import com.chinaBank.bean.ExcelUpload.UploadErrorData;
import com.chinaBank.bean.ExcelUpload.UploadFileInfoData;
import com.chinaBank.bean.SysSeting.ModelConfigData;
import com.chinaBank.service.ExcelFileModel.ExcelFileModelService;
import com.chinaBank.service.ExcelUpload.ExcelUploadService;

@Service
public class ImportUtil {
	
	private static final Logger LOG = LoggerFactory.getLogger(ImportUtil.class);

	@Autowired
	private ExcelFileModelService excelService;
	
	@Autowired
	private ModelConfigData modelConfig;

	@Autowired
	SqlSessionTemplate SqlSession;

	@Autowired
	ExcelUploadService fileUploadService;
	
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
	public String getBankListByExcel(InputStream in, MultipartFile Mufile,String user,String org) throws Exception {
		BufferedWriter ow = null;
		String fileName = Mufile.getOriginalFilename();
		String path = "F:\\convert.sql";// sql文件保存路径、名字
		File file = new File(path);
		// 创建Excel工作薄
		Workbook work = this.getWorkbook(in, fileName);
		if (null == work) {
			return "创建Excel工作薄为空！";
		}
		Sheet sheet = null;
		if (!file.exists()) {
			file.createNewFile();
		}
		ow = new BufferedWriter(new FileWriter(file));
		String loadFileName = fileName.split("\\.")[0]; //取文件名称
//		ExcelConfigFileData excelConfigFileDatas = excelService.selectConfigFileInfoByFileName(loadFileName);// 通过文件名称查询到关联的文件配置信息
//		ExcelConfigFileData excelConfigFileDatas=null;// 通过文件名称查询到关联的文件配置信息
//		if (excelConfigFileDatas == null || excelConfigFileDatas.getFile_name() == "") {
//			return "没有相关的文件配置,请到文件配置页面进行文件配置操作!";
//		}
//		List<ExcelModelData> excelModelDatas = excelService.findModelFileByFileId(excelConfigFileDatas.getId()); // 暂时取一个数据，通过文件拿到文件下绑定的模板
		List<ExcelModelData> excelModelDatas=null;
		if (excelModelDatas.size() == 0) {
			return "没有相关的模板配置,请到模板配置页面进行模板配置操作!";
		}
		ExcelBaseTabData excelBaseTabData = new ExcelBaseTabData();
		excelBaseTabData.setModel_id(excelModelDatas.get(0).getId());
		List<ExcelBaseTabData> excelBaseTabDatas = excelService.selectBaseTabInfoByName(excelBaseTabData);// 通过模板ID拿到关联的表信息，暂时也只支持一个表
		if (excelBaseTabDatas.size() == 0) {
			return "没有相关的表信息配置,请到模板配置页面进行相关模板下的表信息配置操作!";
		}
//		List<ExcelSheetData> excelSheetData = excelService.selectSheetInfoById(excelBaseTabDatas.get(0).getSheet_id()); //通过表关联的sheet拿到关联的sheet信息
//		if (excelSheetData.size() != 0) {
//			String sheet_name = excelSheetData.get(0).getSheet_name();
//			String rule_desc = excelSheetData.get(0).getRule_desc();
		String sheet_name = "";
		String rule_desc = "";
			boolean isMatch = Pattern.matches(rule_desc, sheet_name);//验证sheet名称是否合法
			if (!isMatch) {
				return "sheet页签验证不通过！";
			}
//		}
		List<ExcelBaseColData> excelBaseColDatas = excelService.selectcolumnsInfoByTabId(excelBaseTabDatas.get(0).getId()); // 通过表拿到所有列
		if (excelBaseColDatas.size() == 0) {
			return "没有相关的列信息配置,请到模板配置页面进行相关模板下的表信息列配置操作!";
		}
		List<ExcelCheckRuleData> reRules = excelService.findCheckRulesInfoByTabId(excelModelDatas.get(0).getId()); // 通过模板拿到绑定到模板下的所有规则
		String table_name = excelBaseTabDatas.get(0).getTable_name();
		for (int i = 0; i < excelBaseColDatas.size(); i++) { // 将所有列的英文编码转换成数字形式列
			int colIndex = excelColStrToNum(excelBaseColDatas.get(i).getRead_col(),excelBaseColDatas.get(i).getRead_col().length());
			excelBaseColDatas.get(i).setIndex(colIndex - 1);
		}
		String t = String.valueOf(System.currentTimeMillis());// 生成一个时间搓
		String uuid = UUID.randomUUID().toString() + t.replaceAll("-", "").replaceAll(":", "").trim(); // 生成一个唯一的编码，批次号
		int readRow = Integer.parseInt(excelBaseTabDatas.get(0).getRead_row());
		for (int i = 0; i < work.getNumberOfSheets(); i++) { // 循环的是多个sheet页签，目前只支持一个sheet
			if(i == 0) { //暂时只支持第一个sheet上传
				sheet = work.getSheetAt(i);
				if (sheet == null) {
					return "sheet页签不能为空";
				}
//				UploadFileInfoData rsUploadFileInfoData = fileUploadService.selectUploadFileInfo(loadFileName, user, "Y"); //查询成功上传的数据
//				if(rsUploadFileInfoData !=null) {
//					return "已经有上传的表数据，不能再继续上传";
//				}			
				String rsValue = writeFile(sheet,table_name,excelBaseColDatas,uuid,ow,readRow); 
	            if(!"true".equals(rsValue)) {
	            	return rsValue;
	            }
	            if(!runSqlFile(path)) {
	            	return "sql脚本执行错误";
	            };
	            String uploadFlag = uploadErrorInfoYesOrNo(loadFileName,reRules,excelBaseColDatas,table_name,uuid,user);
	            UploadFileInfoData uploadFileInfoData = new UploadFileInfoData();
				uploadFileInfoData.setCreate_user(user);
				uploadFileInfoData.setFile_name(loadFileName);
				uploadFileInfoData.setOrg(org);
				uploadFileInfoData.setStatus("Y");//文件上传成功
				uploadFileInfoData.setBatch(uuid);
				String sql = "update " + table_name + " t set t.STATUS = 'Y' where t.BATCH = '" + uuid + "'";
	            if(!"true".equals(uploadFlag)) {
	            	if("false".equals(uploadFlag)) {
	        			String uploadPath =fileUploadServer(Mufile,user);
	        			uploadFileInfoData.setAdress(uploadPath);//上传文件到指定位置
	        			if("success".equals(fileUploadService.updateTable(sql))){
//	        				fileUploadService.adduploadFileInfo(uploadFileInfoData);
	                    	return "上传文件有违反软性规则，请到规则详情页面进行说明填写！";
	        			}else{
	        				return "更新上传文件状态失败";
	        			}	
	            	}
	            	return uploadFlag;
	            }
				String uploadPath =fileUploadServer(Mufile,user);//上传文件到指定位置
				uploadFileInfoData.setAdress(uploadPath);
				if("success".equals(fileUploadService.updateTable(sql))){
//					fileUploadService.adduploadFileInfo(uploadFileInfoData);
				}else{
					return "更新上传文件状态失败";
				}	
				ow.close();
			}
		}
		return "success";
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
			  String filePath = "D://chinaBankFile//" + fileName + "//" + user + "//" + uuid + "//";
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
	 * 处理违规数据
	 **/
	public String uploadErrorInfoYesOrNo(String loadFileName,List<ExcelCheckRuleData> reRules,List<ExcelBaseColData> excelBaseColDatas,String table_name,String uuid,String user) {
		boolean checkFlag = false; //记录是否有硬性校验不通过
		boolean check = false; //记录是否有软性校验不通过
//		List<UploadErrorData> uploadErrorDatas = fileUploadService.selectUploadErrorInfoByFileName(loadFileName,user,"");//根据用户和上传的文件，查询此文件之前是否有违规数据
//		String batch = "";//记录历史硬性违规批次号
//		if (uploadErrorDatas.size() >= 1) {
//			batch = uploadErrorDatas.get(0).getBatch(); // 拿到上一次校验过后的错误批次号
//		}
		if(reRules.size() >= 0) {
			for (int g = 0; g < reRules.size(); g++) { // 首先循环所有规则，将所有的规则用@关联的列转换成真实的列，并将每一个规则单独去进行校验
				if (reRules.get(g).getStatus().equals("Y")) { // 校验有效才做校验，否则不做任何校验操作
					String sql;
					String rule = reRules.get(g).getCheck_rule();
					if (reRules.get(g).getCheck_class().equals("sql")) { // sql校验     必须携程   select ROWNUM from mytest.table1 where @A != @B   这种格式 ,这里取反相关
						for (int j = 0; j < excelBaseColDatas.size(); j++) { // 循环所有的列
							rule = rule.replaceAll("@" + excelBaseColDatas.get(j).getRead_col(),excelBaseColDatas.get(j).getColumn_name());
						}
						sql = rule + " and BATCH = '"+ uuid +"'";
					}else { // 非sql校验
						for (int j = 0; j < excelBaseColDatas.size(); j++) { // 循环所有的列
							rule = rule.replaceAll("@" + excelBaseColDatas.get(j).getRead_col(),"e." + excelBaseColDatas.get(j).getColumn_name());
						}
						sql = "select ROWNUM from " + table_name + " t where not EXISTS (" + "select e.ID from " + table_name + " e where " + rule + " and t.ID = e.ID and e.BATCH = '"+ uuid +"') and t.BATCH = '"+ uuid +"'";
					}
					List<UploadErrorData> data= fileUploadService.selectCountCols(sql); //能查询到数据，表示校验通过，否则此条规则不通过
					if (data.size() != 0) { // 查询到数据，表示有违规数据
						if(reRules.get(g).getCheck_type().equals("Y")) {//硬性校验不通过
							checkFlag = true;
						}
						if(reRules.get(g).getCheck_type().equals("N")) {//软性校验不通过
							check = true;
						}
						String ruleId = reRules.get(g).getId();
						for(UploadErrorData uploadErrorData:data) {
							uploadErrorData.setUser(user);
							uploadErrorData.setFile_name(loadFileName);
							uploadErrorData.setRule_id(ruleId);
							uploadErrorData.setErr_Info(reRules.get(g).getError_info());
							uploadErrorData.setBatch(uuid);
							uploadErrorData.setCheck_type(reRules.get(g).getCheck_type());
							fileUploadService.addUploadErrorInfo(uploadErrorData);//记录违反规则的信息到错误信息表
						}
					}
				}
			}
		}
//		if (batch != "") {
//			fileUploadService.deleteUploadErrorInfo(batch,""); // 根据批次号删除掉上次所有的违规规则校验
//		}
		if (checkFlag) {		
			return "上传文件有违反硬性规则，上传失败，可以到规则详情页面进行违规信息查询！";
		}else {
			if(check) {
				return "false";
			}
			return "true";
		}
	}
	
	/**
	 * 将excel数据写入sql文件
	 * @throws Exception 
	 **/
	public String writeFile(Sheet sheet,String table_name,List<ExcelBaseColData> excelBaseColDatas,String uuid,BufferedWriter ow,int readRow) throws Exception{
		String error = ""; //返回错误信息
		Row row = null;
		StringBuffer dataBuf = new StringBuffer();
		boolean colFlag = false; //终止所有循环
		DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");//日期格式
		for( int j = readRow -1; j <= sheet.getLastRowNum() ; j++ ){// 循环的是行
			if(colFlag) {
				break;
			}
			row = sheet.getRow(j);
			if (row == null) {    // || row.getFirstCellNum() == j 默认不读第一行
				continue;
			}

			StringBuffer sf = new StringBuffer(); // 存储字段
			StringBuffer sfValues = new StringBuffer(); // 存储插入字段相对应的值
			sf.append("INSERT INTO " + table_name + "(" + " ID,BATCH,ROWNUM,STATUS,");   
			sfValues.append("(uuid(),'" + uuid + "','"+ (j+1) + "','N',");
			for (int z = 0; z < excelBaseColDatas.size(); z++) { // 循环的动态生成的列
				if(colFlag) {
					break;
				}
				for( int y = row.getFirstCellNum() ; y < row.getLastCellNum() ; y++ ){ // 循环的excel列
					String cellValue = getCellValue(row.getCell(y));
					if (y == excelBaseColDatas.get(z).getIndex() && z != excelBaseColDatas.size()-1) {// 列号相同，就表示同一列,取得是除最后一行的其他行
						sf.append(excelBaseColDatas.get(z).getColumn_name() + ",");				
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
						sf.append(excelBaseColDatas.get(z).getColumn_name() + ")");				
						if(excelBaseColDatas.get(z).getIsNull().equals("NOT NULL") && cellValue == "") {
							error = excelBaseColDatas.get(z).getColumn_desc()+"必须有值，请检查！";
							colFlag = true;
							break;
						}else {
							if(cellValue.startsWith("DATE_FORMAT")) { //表示是日期
								sfValues.append(cellValue + ");");
							}else {
								if(cellValue == "") {
									sfValues.append("null);");
								}else {
									sfValues.append("'" + cellValue + "'" + ");");
								}
							}
						}			
					}
				}
			}
			sf.append("values ").append(sfValues.toString()); // 拼接插入脚本
			dataBuf.append(sf.toString() + "\n");// 换行
		}
		if(colFlag) {
			return error;
		}		
		try {
			ow.write(dataBuf.toString());
			ow.flush();// 将脚本以sql文件保存到指定路径下
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception("***将脚本以sql文件保存到指定路径下发生错误***"+e);
		} finally {
			ow.close();
		}


		return "true";
	}
	
	/**
	 * 执行sql文件脚本，讲数据导入数据库
	 * @throws Exception 
	 **/
	public boolean runSqlFile(String path) throws Exception{
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
			runner.runScript(new InputStreamReader(new FileInputStream(path), "utf-8"));
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

}