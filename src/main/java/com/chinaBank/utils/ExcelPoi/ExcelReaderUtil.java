package com.chinaBank.utils.ExcelPoi;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.chinaBank.bean.ExcelFileModel.ExcelBaseColData;
import com.chinaBank.bean.ExcelFileModel.ExcelBaseTabData;
import com.chinaBank.service.ExcelFileModel.ExcelFileModelService;
import com.chinaBank.service.ExcelUpload.ExcelUploadService;
 
 /**
  * @author y
  * @create 2018-01-19 0:13
  * @desc
  **/
 public class ExcelReaderUtil {
     //excel2003扩展名
     public static final String EXCEL03_EXTENSION = ".xls";
     //excel2007扩展名
     public static final String EXCEL07_EXTENSION = ".xlsx";

 	 private  ExcelFileModelService excelService;
 	 
 	 private  ExcelUploadService fileUploadService;
 	 
 	 private int countRow = 0;//行数
 	 
 	 private String sheetNameNow = "";//当前页签
 
     /**
      * 每获取一条记录，即打印
      * 在flume里每获取一条记录即发送，而不必缓存起来，可以大大减少内存的消耗，这里主要是针对flume读取大数据量excel来说的
      * @param sheetName
      * @param sheetIndex
      * @param curRow
      * @param cellList
      */
     public static void sendRows(String filePath, String sheetName, int sheetIndex, int curRow, List<String> cellList) {
             StringBuffer oneLineSb = new StringBuffer();
             oneLineSb.append(filePath);
             oneLineSb.append("--");
             oneLineSb.append("sheet" + sheetIndex);
             oneLineSb.append("::" + sheetName);//加上sheet名
             oneLineSb.append("--");
             oneLineSb.append("row" + curRow);
             oneLineSb.append("::");
             for (String cell : cellList) {
                 oneLineSb.append(cell.trim());
                 oneLineSb.append("|");
             }
             String oneLine = oneLineSb.toString();
             if (oneLine.endsWith("|")) {
                 oneLine = oneLine.substring(0, oneLine.lastIndexOf("|"));
             }// 去除最后一个分隔符
     }
 
     public void readExcel(String fileName,List<ExcelBaseTabData> excelBaseTabDatas,String uuid,String ZQ,String org,String user,ExcelReaderUtil excelReaderUtil,ExcelFileModelService oldExcelService,ExcelUploadService oldFileUploadService) throws Exception {
//    	 writeDataBase(new HashMap(),excelBaseTabDatas, uuid, ZQ, org, user);
    	 excelService = oldExcelService;
    	 fileUploadService = oldFileUploadService;
    	 int totalRows =0;
         if (fileName.endsWith(EXCEL03_EXTENSION)) { //处理excel2003文件
        	 ExcelXlsReader excelXlsReader=new ExcelXlsReader();
             excelXlsReader.excelBaseTabDatas = excelBaseTabDatas;
             excelXlsReader.uuid = uuid;
             excelXlsReader.ZQ = ZQ;
             excelXlsReader.org = org;
             excelXlsReader.user = user;
             totalRows =excelXlsReader.process(fileName,excelReaderUtil);
         } else if (fileName.endsWith(EXCEL07_EXTENSION)) {//处理excel2007文件
        	 ExcelXlsxReader excelXlsxReader = new ExcelXlsxReader();
             excelXlsxReader.excelBaseTabDatas = excelBaseTabDatas;
             excelXlsxReader.uuid = uuid;
             excelXlsxReader.ZQ = ZQ;
             excelXlsxReader.org = org;
             excelXlsxReader.user = user;
             totalRows = excelXlsxReader.process(fileName,excelReaderUtil);
         } else {
             throw new Exception("文件格式错误，fileName的扩展名只能是xls或xlsx。");
         }
     }
     
     public void writeDataBase(Map<String,Object> dataMap,List<ExcelBaseTabData> excelBaseTabDatas,String uuid,String ZQ,String org,String user) throws Exception{
//    	 List<ExcelBaseColData> excelBaseColDatas222 = excelService.selectcolumnsInfoByTabId(excelBaseTabDatas.get(0).getId()); // 通过表拿到所有列
    	 StringBuffer sf = new StringBuffer(); // 存储插入的字段
    	 String sheetName = (String)dataMap.get("sheetName");
			try {
		 for(int x=0;x<excelBaseTabDatas.size();x++) {//循环所有模板下的的表
			 if(sheetName.equals(excelBaseTabDatas.get(x).getSheet_name())) {//找到需要解析的表
				    int readRow = 0;
				    if("" == sheetNameNow) {//第一个页签
				    	sheetNameNow = sheetName;
				    	readRow = Integer.parseInt(excelBaseTabDatas.get(x).getRead_row());//开始读取的行数
				    }else {
						if(sheetNameNow != sheetName) {//和当前的页签名称相同，表示同一个页签
							countRow = 0;
						    sheetNameNow = sheetName;
							readRow = Integer.parseInt(excelBaseTabDatas.get(x).getRead_row());//开始读取的行数
						}
				    }
					String table_name = excelBaseTabDatas.get(x).getTable_name();	
			    	Map<String, Object> map = new HashMap<>();
					sf.append("INSERT INTO " + table_name + "(" + " ID,BATCH,ROWNUM,STATUS,ZQ,ORG,USER,CREATED_DATE,");
			    	List<ExcelBaseColData> excelBaseColDatas = excelService.selectcolumnsInfoByTabId(excelBaseTabDatas.get(x).getId()); // 通过表拿到所有列
					for (int i = 0; i < excelBaseColDatas.size(); i++) { // 将所有列的英文编码转换成数字形式列
						int colIndex = excelColStrToNum(excelBaseColDatas.get(i).getRead_col(),excelBaseColDatas.get(i).getRead_col().length());
						excelBaseColDatas.get(i).setIndex(colIndex - 1);
					}
			    	String error = ""; //返回错误信息
//					Row row = null;
//					StringBuffer dataBuf = new StringBuffer();
					boolean colFlag = false; //终止所有循环
					List<String> list = new ArrayList<String>();
				    List<Map<String,Object>> rowList = (List<Map<String, Object>>) dataMap.get("rowList");//得到所有的行
					for( int j = readRow; j < rowList.size() ; j++ ){// 循环的是行
						if(countRow == 0) {
							countRow = j;
						}else {
							countRow++;
						}
						Map rowmap = rowList.get(j);
						List<String> colList = (List<String>)rowmap.get("cellList"); //得到一行数据
						if(colFlag) {
							break;
						}
						
						StringBuffer sfValues = new StringBuffer(); // 存储插入字段相对应的值
						sfValues.append("(uuid(),'" + uuid + "','"+ countRow + "','N',"+ZQ+",'"+org+"','"+user+"',now(),");
						for (int z = 0; z < excelBaseColDatas.size(); z++) { // 循环配置列
							if(colFlag) {
								break;
							}
							for( int y = 0 ; y < colList.size() ; y++ ){ // 循环的excel文件列
								String cellValue = colList.get(y);
								if (y == excelBaseColDatas.get(z).getIndex() && z != excelBaseColDatas.size()-1) {// 列号相同，就表示同一列,取得是除最后一行的其他行
									if(j == readRow) {//循环第一行数据的时候才做拼接操作，之后循环不用做
										sf.append(excelBaseColDatas.get(z).getColumn_name() + ",");	
									}
									if(excelBaseColDatas.get(z).getIsNull().equals("NOT NULL") && cellValue == "") {
										error = excelBaseColDatas.get(z).getColumn_desc()+"必须有值，请检查！";
										colFlag = true;
										break;
									}else {
//										if(cellValue.startsWith("DATE_FORMAT")) { //表示是日期
//											sfValues.append(cellValue + ",");
//										}else {
											if(cellValue == "") {
												sfValues.append("null,");
											}else {
												sfValues.append("'" + cellValue + "'" + ",");
											}
//										}
									}
								}
								if (y == excelBaseColDatas.get(z).getIndex() && z == excelBaseColDatas.size()-1) {// 列号相同，就表示同一列,最后一行
									if(j == readRow) {//循环第一行数据的时候才做拼接操作，之后循环不用做
									   sf.append(excelBaseColDatas.get(z).getColumn_name() + ")");	
									   map.put("col", sf.toString()+ "values");
									}
									if(excelBaseColDatas.get(z).getIsNull().equals("NOT NULL") && cellValue == "") {
										error = excelBaseColDatas.get(z).getColumn_desc()+"必须有值，请检查！";
										colFlag = true;
										break;
									}else {
//										if(cellValue.startsWith("DATE_FORMAT")) { //表示是日期
//											sfValues.append(cellValue + ");");
//										}else {
											if(cellValue == "") {
												sfValues.append("null)");
											}else {
												sfValues.append("'" + cellValue + "'" + ")");
											}
//										}
									}			
								}
							}
						}
						list.add(sfValues.toString());				
					}
					if(colFlag) {
						throw new Exception("插入数据库错误---"+error);
//						return error;
					}else {
						map.put("listData", list);
						String flag = fileUploadService.insertListData(map);
						if("success" != flag) {
							throw new Exception("插入数据失败---"+error);
						}
					}	
			 }
		 }
			}catch (Exception e) {
				throw new Exception(e.getMessage());
			}
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
 
     public static void main(String[] args) throws Exception {
         String path="C:\\Users\\y****\\Desktop\\TestSample\\H_20171226_***_*****_0430.xlsx";
//         ExcelReaderUtil.readExcel(path);
     }
}