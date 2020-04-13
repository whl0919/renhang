package com.chinaBank.test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;

public class TestDemo {

	public static void main(String[] args) throws ParseException, ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
//		  String beginTime=new String("2017-06-09"); 
//		  String endTime=new String("2017-05-08"); 
//		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//		  Date sd1=df.parse(beginTime);
//		  Date sd2=df.parse(endTime);
//		    System.out.println(sd1.before(sd2));
//		   System.out.println(sd1.after(sd2));
//	       Calendar c = Calendar.getInstance();
//	        int month=c.get(c.WEEK_OF_MONTH);
//	        int week = c.get(c.WEEK_OF_YEAR);
//	        String quarter="";
//	        int m=c.get(c.MONTH);
//	        if (m >= 1 && m == 3) {
//	            quarter = "1";
//	        }
//	        else if (m >= 4 && m <= 6) {
//	            quarter = "2";
//	        }
//	        else if (m >= 7 && m <= 9) {
//	            quarter = "3";
//	        }
//	        else {
//	            quarter = "4";
//	        }
//	        System.out.println("今天是今年的第" + week + "周;");
//	        System.out.println("今天是今月的第" + month + "周;");
//	        System.out.println("当前季度为"+quarter+"季度");
		
//        List<UserEntity> list = EasyPoiUtils.importExcel(
//                new File("‪‪‪C://Users//GTComputer//Desktop//测试.xlsx"),
//                UserEntity.class, new ImportParams());
//        list.forEach((user)->{
//            System.out.println(user);
//        });
		
		
		
		
		
//	    String fileName = "C:\\Users\\GTComputer\\Desktop\\测试.xlsx";
//	    // 读取全部sheet
//	    // 这里需要注意 DemoDataListener的doAfterAllAnalysed 会在每个sheet读取完毕后调用一次。然后所有sheet都会往同一个DemoDataListener里面写
//	    EasyExcel.read(fileName, DemoData.class, new DemoDataListener()).doReadAll();
//
//	    // 读取部分sheet
//	    fileName = "C:\\Users\\GTComputer\\Desktop\\测试.xlsx";
//	    ExcelReader excelReader = EasyExcel.read(fileName).build();
//	    // 这里为了简单 所以注册了 同样的head 和Listener 自己使用功能必须不同的Listener
//	    ReadSheet readSheet1 =
//	        EasyExcel.readSheet(0).head(DemoData.class).registerReadListener(new DemoDataListener()).build();
//	    ReadSheet readSheet2 =
//	        EasyExcel.readSheet(1).head(DemoData.class).registerReadListener(new DemoDataListener()).build();
//	    // 这里注意 一定要把sheet1 sheet2 一起传进去，不然有个问题就是03版的excel 会读取多次，浪费性能
//	    excelReader.read(readSheet1, readSheet2);
//	    // 这里千万别忘记关闭，读的时候会创建临时文件，到时磁盘会崩的
//	    excelReader.finish();
		
		
//		String f1 = "@ExcelProperty(index = 0 , value = \"姓名\")\n" + 
//				"private String staff_code;" + 
//				"public String getStaff_code() {\n" + 
//				"return staff_code;" + 
//				"}" ;
//		
//		
//		// 设置类成员属性
//		HashMap propertyMap = new HashMap();
//		propertyMap.put("id", Class.forName("java.lang.Integer"));
//		propertyMap.put("name", Class.forName("java.lang.String"));
//		// 生成动态 Bean
//		DynamicBean bean = new DynamicBean(propertyMap);
//		
//		
//	       Method[] methods = bean.getClass().getDeclaredMethods();
//	        for(Method method : methods) {
//	            Tag tag = method.getAnnotation(Tag.class);
//	            if(tag != null) {
//	                InvocationHandler h = Proxy.getInvocationHandler(tag);
//	                Field hField = h.getClass().getDeclaredField("memberValues");
//	                hField.setAccessible(true);
//	                Map memberMethods = (Map) hField.get(h);
//	                memberMethods.put("value", "setAnnotation");
////	                String value = tag.value();
////	                Assert.assertEquals("setAnnotation", value);
//	            }
//	        }
//	        
//	        
//		// 给 Bean 设置值
//		bean.setValue("name", "454");
//		bean.setValue("id", new Integer(123));
//		System.out.println(" id :" + bean.getValue("id"));
//		System.out.println("name :" + bean.getValue("name"));
//		Object object = bean.getObject();
//		Class clazz = object.getClass();
//		Method[] methods = clazz.getDeclaredMethods();
//		for (int i = 0; i < methods.length; i++) {
//			System.out.println(methods[i].getName()); 
//		}
		
	}

}
