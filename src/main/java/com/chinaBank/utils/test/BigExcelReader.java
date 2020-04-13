package com.chinaBank.utils.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

	public abstract class BigExcelReader {

	    enum xssfDataType {
	        BOOL, ERROR, FORMULA, INLINESTR, SSTINDEX, NUMBER,
	    }

	    public static final int ERROR = 1;
	    public static final int BOOLEAN = 1;
	    public static final int NUMBER = 2;
	    public static final int STRING = 3;
	    public static final int DATE = 4;
	    public static final String DATE_FORMAT_STR = "yyyy-MM-dd HH:mm:ss";


	    // private DataFormatter formatter = new DataFormatter();
	    private InputStream sheet;
	    private XMLReader parser;
	    private InputSource sheetSource;
	    private int index = 0;

	    /**
	     * 读大数据量Excel
	     *
	     * @throws IOException
	     * @throws OpenXML4JException
	     * @throws SAXException
	     */
	    //@param maxColNum 读取的最大列数
	    public BigExcelReader(String filename) throws IOException, OpenXML4JException, SAXException {
	        OPCPackage pkg = OPCPackage.open(filename);
	        init(pkg);
	    }

	    /**
	     * 读大数据量Excel
	     *
	     * @param file Excel文件
	     * @throws IOException
	     * @throws OpenXML4JException
	     * @throws SAXException
	     */
	    //@param maxColNum 读取的最大列数
	    public BigExcelReader(File file) throws IOException, OpenXML4JException, SAXException {
	        OPCPackage pkg = OPCPackage.open(file);
	        init(pkg);
	    }

	    /**
	     * 读大数据量Excel
	     *
	     * @param in Excel文件输入流
	     * @throws IOException
	     * @throws OpenXML4JException
	     * @throws SAXException
	     */
	    public BigExcelReader(InputStream in) throws IOException, OpenXML4JException, SAXException {
	        OPCPackage pkg = OPCPackage.open(in);
	        init(pkg);
	    }

	    /**
	     * 初始化 将Excel转换为XML
	     *
	     * @param pkg
	     * @throws IOException
	     * @throws OpenXML4JException
	     * @throws SAXException
	     */
	    private void init(OPCPackage pkg) throws IOException, OpenXML4JException, SAXException {
	        XSSFReader xssfReader = new XSSFReader(pkg);
	        SharedStringsTable sharedStringsTable = xssfReader.getSharedStringsTable();
	        StylesTable stylesTable = xssfReader.getStylesTable();
	        //解析文件，得到rid

	        String rid = getRid(xssfReader);

	        if(rid == null){
	            throw new RuntimeException("解析xml出错了，rid获取失败");
	        }


	        //sheet = xssfReader.getSheet("rId"+ sheetId);只遍历一个电子表格，其中sheetId为要遍历的sheet索引，从1开始，1-3
	        //sheet = xssfReader.getSheet("rId3");
	        sheet = xssfReader.getSheet(rid);
	        parser = fetchSheetParser(sharedStringsTable, stylesTable);
	        sheetSource = new InputSource(sheet);
	    }

	    /**
	     * 执行解析操作
	     *
	     * @return 读取的Excel行数
	     */
	    public int parse() {
	        try {
	            parser.parse(sheetSource);
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (SAXException e) {
	            e.printStackTrace();
	        } finally {
	            if (sheet != null) {
	                try {
	                    sheet.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	        return index;
	    }

	    private XMLReader fetchSheetParser(SharedStringsTable sharedStringsTable, StylesTable stylesTable) throws SAXException {
	        XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
	        ContentHandler handler = new SheetHandler(sharedStringsTable, stylesTable);
	        parser.setContentHandler(handler);
	        return parser;
	    }

	    private String getRid(XSSFReader xssfReader){
	        try {
	            InputStream wbIn = xssfReader.getWorkbookData();
	            final Map<String, String> map = new HashMap<>();
	            XMLReader parserRid = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
	            parserRid.setContentHandler(new DefaultHandler() {
	                @Override
	                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
	                    System.out.println(qName);
	                    if (qName.equals("sheet")) {
	                        //获取rid的值
	                        String sheetIndex = attributes.getValue("sheetId");
//	                        if("1".equals(sheetIndex)){
	                            String rid = attributes.getValue("r:id");
	                            map.put("rid"+sheetIndex, rid);
	                            System.out.println(rid);
//	                        }

	                    }
	                }
	            });
	            InputSource inputSource = new InputSource(wbIn);
	            parserRid.parse(inputSource);

	            wbIn.close();
	            return map.get("rid");
	        }catch (Exception e){
	            throw new RuntimeException(e);
	        }
	    }

	    /**
	     * SAX解析的处理类
	     * 每解析一行数据后通过outputRow(String[] datas, int[] rowTypes, int rowIndex)方法进行输出
	     *
	     * @author zpin
	     */
	    private class SheetHandler extends DefaultHandler {
	        private SharedStringsTable sharedStringsTable; // 存放映射字符串
	        private StylesTable stylesTable;// 存放单元格样式
	        private String readValue;// 存放读取值
	        private xssfDataType dataType;// 单元格类型
	        private String[] rowDatas;// 存放一行中的所有数据
	        private int[] rowTypes;// 存放一行中所有数据类型
	        private int colIdx;// 当前所在列

	        private short formatIndex;
//	    private String formatString;// 对数值型的数据直接读为数值，不对其格式化，所以隐掉此处
	        //解析结果保存
	        private List<String[]> container =  new LinkedList<>();

	        private SheetHandler(SharedStringsTable sst, StylesTable stylesTable) {
	            this.sharedStringsTable = sst;
	            this.stylesTable = stylesTable;
	        }

	        private String formateDateToString(Date date) {
	            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//格式化日期
	            return sdf.format(date);

	        }

	        @Override
	        public void endDocument() throws SAXException {
	            doTask();
	        }

	        public void startElement(String uri, String localName, String name,
	                                 Attributes attributes) throws SAXException {
	            if (name.equals("c")) {// c > 单元格
	                colIdx = getColumn(attributes);
	                String cellType = attributes.getValue("t");
	                String cellStyle = attributes.getValue("s");

	                this.dataType = xssfDataType.NUMBER;
	                if ("b".equals(cellType)) {
	                    this.dataType = xssfDataType.BOOL;
	                } else if ("e".equals(cellType)) {
	                    this.dataType = xssfDataType.ERROR;
	                } else if ("inlineStr".equals(cellType)) {
	                    this.dataType = xssfDataType.INLINESTR;
	                } else if ("s".equals(cellType)) {
	                    this.dataType = xssfDataType.SSTINDEX;
	                } else if ("str".equals(cellType)) {
	                    this.dataType = xssfDataType.FORMULA;
	                } else if (cellStyle != null) {
	                    int styleIndex = Integer.parseInt(cellStyle);
	                    XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);
	                    this.formatIndex = style.getDataFormat();
//	                this.formatString = style.getDataFormatString();
	                }
	            }
	            // 解析到一行的开始处时，初始化数组
	            else if (name.equals("row")) {
	                int cols = getColsNum(attributes);// 获取该行的单元格数
	                rowDatas = new String[cols];
	                rowTypes = new int[cols];
	            }
	            readValue = "";
	        }

	        public void endElement(String uri, String localName, String name)
	                throws SAXException {
	            if (name.equals("v") || name.equals("t")) { // 单元格的值
	                switch (this.dataType) {
	                    case BOOL: {
	                        char first = readValue.charAt(0);
	                        rowDatas[colIdx] = first == '0' ? "FALSE" : "TRUE";
	                        rowTypes[colIdx] = BOOLEAN;
	                        break;
	                    }
	                    case ERROR: {
	                        rowDatas[colIdx] = "ERROR:" + readValue.toString();
	                        rowTypes[colIdx] = ERROR;
	                        break;
	                    }
	                    case INLINESTR: {
	                        rowDatas[colIdx] = new XSSFRichTextString(readValue).toString();
	                        rowTypes[colIdx] = STRING;
	                        break;
	                    }
	                    case SSTINDEX: {
	                        int idx = Integer.parseInt(readValue);
	                        rowDatas[colIdx] = new XSSFRichTextString(sharedStringsTable.getEntryAt(idx)).toString();
	                        rowTypes[colIdx] = STRING;
	                        break;
	                    }
	                    case FORMULA: {
	                        rowDatas[colIdx] = readValue;
	                        rowTypes[colIdx] = STRING;
	                        break;
	                    }
	                    case NUMBER: {
	                        // 判断是否是日期格式
	                        if (HSSFDateUtil.isADateFormat(formatIndex, readValue)) {
	                            Double d = Double.parseDouble(readValue);
	                            Date date = HSSFDateUtil.getJavaDate(d);
	                            rowDatas[colIdx] = formateDateToString(date);
	                            rowTypes[colIdx] = DATE;
	                        }
//	                   else if (formatString != null){
//	                      cellData.value = formatter.formatRawCellContents(Double.parseDouble(cellValue), formatIndex, formatString);
//	                      cellData.dataType = NUMBER;
//	                   }
	                        else {
	                            rowDatas[colIdx] = readValue;
	                            rowTypes[colIdx] = NUMBER;
	                        }
	                        break;
	                    }
	                }
	            }
	            // 当解析的一行的末尾时，输出数组中的数据
	            else if (name.equals("row")) {
	                container.add(rowDatas);
//	                if(container.size() == 1000){
	                    doTask();
//	                }
	                //outputRow(rowDatas, rowTypes, index++);
	            }

	        }

	        public void characters(char[] ch, int start, int length)
	                throws SAXException {
	            readValue += new String(ch, start, length);
	        }
	        public void doTask(){
	            outputAllRow(container);
	            //执行保存
	           /* for(String[] row : container){
	          Map<String, Object> map = new HashMap<String, Object>();
	          map.put("", row.);
	                for(String cell : row){
	                    System.out.print(cell+",");
	                }
	                System.out.println();
	            }*/

	            this.container.clear();
	        }

	    /*@Override
	    public void endDocument() throws SAXException {
	        doTask();
	    }*/
	    }

	    /**
	     * 输出每一行的数据
	     *
	     * @param datas    数据
	     * @param rowTypes 数据类型
	     * @param rowIndex 所在行
	     */
	    //public abstract void outputRow(String[] datas, int[] rowTypes, int rowIndex);

	    /**
	     * 输出数据
	     * @param lists
	     */
	    public abstract void outputAllRow(List<String[]> lists);

	    private int getColumn(Attributes attrubuts) {
	        String name = attrubuts.getValue("r");
	        int column = -1;
	        for (int i = 0; i < name.length(); ++i) {
	            if (Character.isDigit(name.charAt(i))) {
	                break;
	            }
	            int c = name.charAt(i);
	            column = (column + 1) * 26 + c - 'A';
	        }
	        return column;
	    }

	    private int getColsNum(Attributes attrubuts) {
	        /*String spans = attrubuts.getValue("spans");
	        String cols = spans.substring(spans.indexOf(":") + 1);
	        return Integer.parseInt(cols);*/
	        //String cols = attrubuts.getValue("r");
	        String cols = "6";
	        return Integer.parseInt(cols);
	    }


	}