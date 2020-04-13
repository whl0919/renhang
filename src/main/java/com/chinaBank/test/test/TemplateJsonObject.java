package com.chinaBank.test.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.apache.ibatis.javassist.ClassPool;
import org.apache.ibatis.javassist.CtClass;
import org.apache.ibatis.javassist.CtField;
import org.apache.ibatis.javassist.CtMethod;
import org.apache.ibatis.javassist.bytecode.AnnotationsAttribute;
import org.apache.ibatis.javassist.bytecode.ClassFile;
import org.apache.ibatis.javassist.bytecode.ConstPool;
import org.apache.ibatis.javassist.bytecode.FieldInfo;
import org.apache.ibatis.javassist.bytecode.annotation.Annotation;
import org.apache.ibatis.javassist.bytecode.annotation.StringMemberValue;

/**
 * Created by cdw on 2018/04/19.
 *
 * Apache POI操作Excel对象 HSSF：操作Excel 2007之前版本(.xls)格式,生成的EXCEL不经过压缩直接导出
 * XSSF：操作Excel 2007及之后版本(.xlsx)格式,内存占用高于HSSF SXSSF:从POI3.8
 * beta3开始支持,基于XSSF,低内存占用,专门处理大数据量(建议)。
 *
 * 注意: 值得注意的是SXSSFWorkbook只能写(导出)不能读(导入)
 *
 * 说明: .xls格式的excel(最大行数65536行,最大列数256列) .xlsx格式的excel(最大行数1048576行,最大列数16384列)
 * 这里引用的是阿里的json包，也可以自行转换成net.sf.json.JSONArray net.sf.json.JSONObject
 */
public class TemplateJsonObject {
	public static void main(String[] args) throws Exception{
		ClassPool pool=ClassPool.getDefault();
		CtClass ct=pool.getCtClass("com.chinaBank.test.test.Point");
		CtMethod m=ct.getDeclaredMethod("move");
		CtField cf = ct.getField("x");
		
        ClassFile ccFile = ct.getClassFile();
        ConstPool constpool = ccFile.getConstPool();
        FieldInfo fieldInfo = cf.getFieldInfo();
//        ClassFile ccFile = ct.getClassFile();
        // 属性附上注解
//        AnnotationsAttribute methodAttr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
        AnnotationsAttribute fieldAttr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
        Annotation autowired = new Annotation("cn.afterturn.easypoi.excel.annotation.Excel",constpool);
        autowired.addMemberValue("name",  new StringMemberValue("result", constpool));
        fieldAttr.addAnnotation(autowired);
        fieldInfo.addAttribute(fieldAttr);
        ct.addField(cf);
        
        
		System.out.println("属性名称===" + cf.getName());  
		m.insertBefore("{ System.out.print(\"dx:\"+$1); System.out.println(\"dy:\"+$2);}");
		m.insertAfter("{System.out.println(this.x); System.out.println(this.y);}");
		ct.writeFile();
		ct.getClass();
		//通过反射调用方法，查看结果
		Class pc=ct.toClass();
		Method move= pc.getMethod("move",new Class[]{int.class,int.class});
		Constructor<?> con=pc.getConstructor(new Class[]{int.class,int.class});
		move.invoke(con.newInstance(1,2),1,2);
	}
}