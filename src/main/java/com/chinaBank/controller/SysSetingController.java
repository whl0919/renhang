package com.chinaBank.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.chinaBank.bean.SysSeting.AreaData;
import com.chinaBank.bean.SysSeting.DeptData;
import com.chinaBank.bean.SysSeting.DictData;
import com.chinaBank.bean.SysSeting.HolidayData;
import com.chinaBank.bean.SysSeting.OrgData;
import com.chinaBank.bean.SysSeting.RoleData;
import com.chinaBank.bean.SysSeting.UserData;
import com.chinaBank.service.SysSeting.SysSetingService;
import com.chinaBank.utils.OrgExcelPoi;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import lombok.extern.slf4j.Slf4j;



@RestController
@RequestMapping(value = "/set")
@Slf4j
public class SysSetingController {
	private static final Logger LOG = LoggerFactory.getLogger(SysSetingController.class);
	@Autowired
	private SysSetingService sysSetingService;
	
	@RequestMapping("/test")
	@Transactional
	public void test() {
		String[]ids = new String[]{"11","22","44"};
		for(String id:ids) {
			sysSetingService.test(id);
		}
		sysSetingService.update_test_deno("2");
//		String[]names = new String[]{"name1","name2","55"};
//		insjdj(names);
		insjdj();
	}
	
	public void insjdj() {
//		for(String name:names) {
			sysSetingService.test_deno("7","Y");
//		}
	}
	
	@GetMapping("/upload")
	public String upload() {
		return "upload";
	}
	
	@RequestMapping("/testzx")
	public String getString() {
		return "测试01";
	}
	
	@RequestMapping("/addDept")
	public Object addDept(DeptData deptData) {
		Map<String, Object> body = new HashMap<>();
		DeptData deptDatas = sysSetingService.selectSeptByName(deptData.getDept_name());
		if(deptDatas != null) {
			body.put("message", "已经存在此部门，部门新增失败!");
			body.put("code", "999");
		}else {
			String rsFlag = sysSetingService.addDept(deptData);
			if("success".equals(rsFlag)) {
				body.put("message", "部门新增成功!");
				body.put("code", "200");
			}else {
				body.put("message", "部门新增失败!");
				body.put("code", "999");
			}
		}
		return body;
	}
	
	@RequestMapping("/updateDept")
	public Object updateDept(DeptData deptData) {
		Map<String, Object> body = new HashMap<>();
		DeptData Data= sysSetingService.selectSeptById(deptData.getId());//查询的是原始部门
		if(Data != null) {
			if(Data.getDept_name().equals(deptData.getDept_name())) {//没有修改名称
				String rsFlag = sysSetingService.updateDept(deptData);
				if("success".equals(rsFlag)) {
					body.put("message", "部门更新成功!");
					body.put("code", "200");
				}else {
					body.put("message", "部门更新失败!");
					body.put("code", "999");
				}
			}else {//讲名称也修改了
				DeptData deData = sysSetingService.selectSeptByName(deptData.getDept_name());
				if(deData != null) {
					body.put("message", "此部门已经存在，不能修改为此部门!");
					body.put("code", "999");
				}else {
					String rsFlag = sysSetingService.updateDept(deptData);
					if("success".equals(rsFlag)) {
						body.put("message", "部门更新成功!");
						body.put("code", "200");
					}else {
						body.put("message", "部门更新失败!");
						body.put("code", "999");
					}
				}
			}
		}else {//
			body.put("message", "无此部门!");
			body.put("code", "999");
		}
		return body;
	}
	
	@RequestMapping("/deleteDept")
	public Object deleteDept(DeptData deptData) {
		Map<String, Object> body = new HashMap<>();
		String rsFlag = sysSetingService.deleteDept(deptData);
		if("success".equals(rsFlag)) {
			body.put("message", "部门删除成功!");
			body.put("code", "200");
		}else {
			body.put("message", "部门删除失败!");
			body.put("code", "999");
		}
		return body;
	}
	
    @RequestMapping("/findDeptById")
    public Object findDeptById(
            @RequestParam(name = "id", required = false, defaultValue = "") String id){
        return sysSetingService.selectSeptById(id);
    }
    
    @RequestMapping("/findDeptTotal")
    public Object findDeptTotal(){
        return sysSetingService.findDeptTotal();
    }
	
    @RequestMapping("/findAllDept")
    public Object findAllDept(
            @RequestParam(name = "pageNum", required = false, defaultValue = "") int pageNum,
            @RequestParam(name = "pageSize", required = false, defaultValue = "") int pageSize,
            DeptData deptData){
        return sysSetingService.findAllDept(pageNum, pageSize, deptData);
    }

    
	@RequestMapping("/addUser")
	public Object addUser(UserData userData) {
		Map<String, Object> body = new HashMap<>();
		String create_user = userData.getUser_id();
		userData.setCreate_user(create_user);
		UserData user = sysSetingService.selectUserByUserId(userData.getUser_id(),"");
		if (user != null) {
			body.put("message", "已经有用户，新增失败!");
			body.put("code", "999");
		}else {
			userData.setUser_psd("111111");// 默认密码
			String rsFlag = sysSetingService.addUser(userData);
			if("success".equals(rsFlag)) {
				body.put("message", "用户新增成功!");
				body.put("code", "200");
			}else {
				body.put("message", "用户新增失败!");
				body.put("code", "999");
			}
		}
		return body;
	}
	
	@RequestMapping("/updateUser")
	public Object updateUser(UserData userData) {
		Map<String, Object> body = new HashMap<>();
		userData.setUpdate_user(userData.getUpdate_user());
		String rsFlag = sysSetingService.updateUser(userData);
		if("success".equals(rsFlag)) {
			body.put("message", "用户更新成功!");
			body.put("code", "200");

		}else {
			body.put("message", "用户更新失败!");
			body.put("code", "999");
		}
		return body;
	}
	
    @RequestMapping("/updateUserPsd")
    public Object updateUserPsd(
    		@RequestParam(name = "user_psd", required = false, defaultValue = "") String user_psd,
    		@RequestParam(name = "id", required = false, defaultValue = "") String id){
		Map<String, Object> body = new HashMap<>();
    	String rsFlag =  sysSetingService.updateUserPsd(id, user_psd);
		if("success".equals(rsFlag)) {
			body.put("message", "用户密码更新成功!");
			body.put("code", "200");

		}else {
			body.put("message", "用户密码更新失败!");
			body.put("code", "999");
		}
		return body;
    }
	
    @RequestMapping("/findUserById")
    public Object findUserById(
            @RequestParam(name = "id", required = false, defaultValue = "") String id){
        return sysSetingService.selectUserById(id);
    }
    
    @RequestMapping("/login")
    public Object login(
            @RequestParam(name = "user_id", required = false, defaultValue = "") String user_id,
            @RequestParam(name = "user_psd", required = false, defaultValue = "") String user_psd){
		Map<String, Object> body = new HashMap<>();
		UserData user = sysSetingService.selectUserByUserId(user_id, "");
		if(user != null && user.getUser_id() != "") {
			UserData suUser = sysSetingService.selectUserByUserId(user_id, user_psd);
			if(suUser != null && suUser.getUser_id() != "") {
				body.put("data", suUser);
				body.put("code", "200");
			}else {
				body.put("message", "用户密码错误!");
				body.put("code", "777");
			}
		}else if(user == null || user.getUser_id() == ""){
			body.put("message", "没有此用户!");
			body.put("code", "888");
		}else {
			body.put("message", "用户登录失败!");
			body.put("code", "999");
		}
		return body;
    }
	
    @RequestMapping("/findAllUser")
    public Object findAllUser(
            UserData userData){
		List<UserData> userDatas = new ArrayList<UserData>();
		UserData user = sysSetingService.selectUserByUserId(userData.getLogin_id(), "");
//			if(user.getRole_id().equals("Y")) {//当前用户是管理员	
				OrgData Org = new OrgData();
				Org.setBANKCODE(user.getOrg_id());
				List<OrgData> orgs = sysSetingService.selectOrgById(user.getOrg_id());
				if (orgs.size() > 0) {//查询当前管理员下的所有机构信息
					for(int i=0;i<orgs.size();i++) {
						selectAllOrg(orgs.get(i),orgs);
					}
//					if(!"developer".equals(user.getUser_id())) {
//						orgs.add(Org);
//					}
					//start----根据机构查询用户信息-----
					if(userData.getOrg_id() != "") {//是否有机构号作为查询条件
						List<UserData> rsUserDatas = sysSetingService.selectUser(userData);
		                if(rsUserDatas.size() >=1) {
		                	for(int x=0;x<rsUserDatas.size();x++) {
		                		if(!"developer".equals(rsUserDatas.get(x).getUser_id())) {
			                		userDatas.add(rsUserDatas.get(x));
		                		}
		                	}
		                }
					}else {
						for(int j=0;j<orgs.size();j++) {
							userData.setOrg_id(orgs.get(j).getBANKCODE());
							List<UserData> rsUserDatas = sysSetingService.selectUser(userData);
			                if(rsUserDatas.size() >=1) {
			                	for(int x=0;x<rsUserDatas.size();x++) {
			                		if(!"developer".equals(rsUserDatas.get(x).getUser_id())) {
				                		userDatas.add(rsUserDatas.get(x));
			                		}
			                	}
			                }
						}
					}
				}else {
					userDatas.add(user);
				}	

				//end----根据机构查询用户信息-----
//			}else {//普通用户   ordinaryUser
//				userDatas.add(user);
//			}
//		PageHelper.startPage(pageNum, pageSize);
//		PageInfo result = new PageInfo(userDatas);
		return userDatas;
    }

    public <T> Page<T> listConvertToPage(List<T> list, Pageable pageable) {
      int start = (int)pageable.getOffset();
      int end = (start + pageable.getPageSize()) > list.size() ? list.size() : ( start + pageable.getPageSize());
      return new PageImpl<T>(list.subList(start, end), pageable, list.size());
    }
    
	public void selectAllOrg(OrgData OrgDato,List<OrgData> orgs) {//循环取下级机构
		List<OrgData> rsDatas = sysSetingService.selectOrgById(OrgDato.getBANKCODE());
		if (rsDatas.size() >= 1) {
			for (int i = 0; i < rsDatas.size(); i++) {
				orgs.add(rsDatas.get(i));
				selectAllOrg(rsDatas.get(i),orgs);
			}
		}
    }
    
    @RequestMapping("/findTotalOrg")
    public Object findTotalOrg(
            @RequestParam(name = "pageNum", required = false, defaultValue = "") int pageNum,
            @RequestParam(name = "pageSize", required = false, defaultValue = "") int pageSize,
            OrgData orgData){
        return sysSetingService.findAllOrg(pageNum, pageSize, orgData);
    }
    
    @RequestMapping("/findOrg")
    public Object findOrg(OrgData orgData){
        return sysSetingService.findOrg(orgData);
    }
    
    @RequestMapping("/selectBankType")
    public Object selectBankType(){
        return sysSetingService.selectBankType();
    }
    
    @RequestMapping("/selectOrgByUserId")
    public Object selectOrgByUserId(
  		@RequestParam(name = "user_id", required = false, defaultValue = "") String user_id){
		List<AreaData> orgDatas = new ArrayList<AreaData>();
		AreaData ownAreaData = new AreaData();
		UserData user = sysSetingService.selectUserByUserId(user_id, "");
		ownAreaData.setLabel(user.getOrg_name());
		ownAreaData.setValue(user.getOrg_id());
		if(user != null && user.getUser_id() != "") {
			List<AreaData> rsOrgDatas = sysSetingService.selectOrgByBelongpbc(user.getOrg_id());
			for (int i = 0; i < rsOrgDatas.size(); i++) {
				selectOrgByBelongpbcMore(rsOrgDatas.get(i));
			}
			ownAreaData.setChildren(rsOrgDatas);
			orgDatas.add(ownAreaData);
			return orgDatas;
		}
		return null;
    }
    
	public void selectOrgByBelongpbcMore(AreaData areaData) {
		List<AreaData> roleDatas = sysSetingService.selectOrgByBelongpbc(areaData.getValue());
		if (roleDatas.size() >= 1) {
			areaData.setChildren(roleDatas);
			for (int i = 0; i < roleDatas.size(); i++) {
				selectOrgByBelongpbcMore(roleDatas.get(i));
			}
		}
	}
	
	@RequestMapping("/addDictType")
	public Object addDictType( @RequestParam(name = "dict_type", required = false, defaultValue = "") String dict_type,
            @RequestParam(name = "dict_type_value", required = false, defaultValue = "") String dict_type_value,
            @RequestParam(name = "status", required = false, defaultValue = "") String status,
            @RequestParam(name = "create_user", required = false, defaultValue = "") String create_user) {
		Map<String, Object> body = new HashMap<>();
		int count = sysSetingService.selectDictTypeByType(dict_type,"");
		if (count >= 1) {
			body.put("message", "已经有此字典类型，新增失败!");
			body.put("code", "999");
		}else {
			int countValue = sysSetingService.selectDictTypeByType("",dict_type_value);
			if (countValue >= 1) {
				body.put("message", "已经有此字典类型值，新增失败!");
				body.put("code", "999");
			}else {
				String rsFlag = sysSetingService.addDictType(dict_type, dict_type_value, status, create_user);
				if("success".equals(rsFlag)) {
					body.put("message", "用户新增成功!");
					body.put("code", "200");
				}else {
					body.put("message", "用户新增失败!");
					body.put("code", "999");
				}
			}
		}
		return body;
	}
	
	@RequestMapping("/deleteDictTypeFor")
	public Object deleteDictTypeFor( @RequestParam(name = "dict_type_id", required = false, defaultValue = "") String dict_type_id) {
		Map<String, Object> body = new HashMap<>();
		 String flag = sysSetingService.deleteDictTypeValueByTypeId(dict_type_id);
        if(!"success".equals(flag)) {
        	body.put("message", "删除失败!");
			body.put("code", "999");
        }else {
        	String typeFlag = sysSetingService.deleteDictTypeFor(dict_type_id);
        	if(!"success".equals(typeFlag)) {
            	body.put("message", "删除失败!");
    			body.put("code", "999");
        	}else {
        		body.put("message", "删除成功!");
    			body.put("code", "200");
        	}
        }
		return body;
	}
	
	@RequestMapping("/addDictTypeValue")
	public Object addDictTypeValue( @RequestParam(name = "dict_id", required = false, defaultValue = "") String dict_id,
            @RequestParam(name = "dict_value", required = false, defaultValue = "") String dict_value,
            @RequestParam(name = "dict_type_id", required = false, defaultValue = "") String dict_type_id,
            @RequestParam(name = "create_user", required = false, defaultValue = "") String create_user) {
		Map<String, Object> body = new HashMap<>();
		int count = sysSetingService.selectDictTypeValueByTypeId(dict_id, dict_type_id);
		if (count >= 1) {
			body.put("message", "已经有此字典值，新增失败!");
			body.put("code", "999");
		}else {
			String rsFlag = sysSetingService.addDictTypeValue(dict_id, dict_value, dict_type_id, create_user);
			if("success".equals(rsFlag)) {
				body.put("message", "字典类型新增成功!");
				body.put("code", "200");
			}else {
				body.put("message", "字典类型新增失败!");
				body.put("code", "999");
			}
		}
		return body;
	}
	
	@RequestMapping("/updateDictType")
	public Object updateDictType( @RequestParam(name = "dict_type", required = false, defaultValue = "") String dict_type,
            @RequestParam(name = "dict_type_value", required = false, defaultValue = "") String dict_type_value,
            @RequestParam(name = "status", required = false, defaultValue = "") String status,
            @RequestParam(name = "update_user", required = false, defaultValue = "") String update_user,
            @RequestParam(name = "id", required = false, defaultValue = "") String id) {
		Map<String, Object> body = new HashMap<>();
		//区别唯一性的type不让改，所以可以直接改动值
		String rsFlag = sysSetingService.updateDictType(dict_type, dict_type_value, status, id, update_user);
		if("success".equals(rsFlag)) {
			body.put("message", "字典值更新成功!");
			body.put("code", "200");
		}else {
			body.put("message", "字典值更新失败!");
			body.put("code", "999");
		}
		return body;
	}
	
	@RequestMapping("/updateDictTypeValue")
	public Object updateDictTypeValue( @RequestParam(name = "dict_id", required = false, defaultValue = "") String dict_id,
            @RequestParam(name = "dict_value", required = false, defaultValue = "") String dict_value,
            @RequestParam(name = "dict_type_id", required = false, defaultValue = "") String dict_type_id,
            @RequestParam(name = "update_user", required = false, defaultValue = "") String update_user,
            @RequestParam(name = "id", required = false, defaultValue = "") String id) {
		Map<String, Object> body = new HashMap<>();
		DictData Data= sysSetingService.selectDictTypeValueById(id);
		if(Data != null) {
			if(Data.getDict_id().equals(dict_id)) {//没有修改名称
				String rsFlag = sysSetingService.updateDictTypeValue(dict_id, dict_value, id, update_user);
				if("success".equals(rsFlag)) {
					body.put("message", "字典值更新成功!");
					body.put("code", "200");
				}else {
					body.put("message", "字典值更新失败!");
					body.put("code", "999");
				}
			}else {//讲名称也修改了
				int count = sysSetingService.selectDictTypeValueByTypeId(dict_id, dict_type_id);
				if(count >=1) {
					body.put("message", "此字典值已经存在，不能修!");
					body.put("code", "999");
				}else {
					String rsFlag = sysSetingService.updateDictTypeValue(dict_id, dict_value, id, update_user);
					if("success".equals(rsFlag)) {
						body.put("message", "字典值更新成功!");
						body.put("code", "200");
					}else {
						body.put("message", "字典值更新失败!");
						body.put("code", "999");
					}
				}
			}
		}else {//
			body.put("message", "无此字典类型!");
			body.put("code", "999");
		}
		return body;
	}
	
    @RequestMapping("/selectDictType")
    public Object selectDictType(
            @RequestParam(name = "pageNum", required = false, defaultValue = "") int pageNum,
            @RequestParam(name = "pageSize", required = false, defaultValue = "") int pageSize,
            @RequestParam(name = "dict_type", required = false, defaultValue = "") String dict_type,
            @RequestParam(name = "dict_type_value", required = false, defaultValue = "") String dict_type_value,
            @RequestParam(name = "status", required = false, defaultValue = "") String status
           ){
		PageHelper.startPage(pageNum, pageSize);
		List<DictData> data = new ArrayList<DictData>();
		if("".equals(status) || null == status) {
			data = sysSetingService.selectDictTypeOrderBy(dict_type, dict_type_value);
		}else {
			data = sysSetingService.selectDictType(dict_type, dict_type_value, status);
		}
		PageInfo result = new PageInfo(data);
		return result;
    }
    
    @RequestMapping("/selectAllDictType")
    public Object selectAllDictType(){
		List<DictData> data = sysSetingService.selectDictType("", "", "Y");
		return data;
    }
    
    @RequestMapping("/selectDictTypeValue")
    public Object selectDictTypeValue(
            @RequestParam(name = "pageNum", required = false, defaultValue = "") int pageNum,
            @RequestParam(name = "pageSize", required = false, defaultValue = "") int pageSize,
            @RequestParam(name = "dict_id", required = false, defaultValue = "") String dict_id,
            @RequestParam(name = "dict_value", required = false, defaultValue = "") String dict_value,
            @RequestParam(name = "dict_type_id", required = false, defaultValue = "") String dict_type_id
           ){
		PageHelper.startPage(pageNum, pageSize);
		List<DictData> data = sysSetingService.selectDictTypeValue(dict_id, dict_value, dict_type_id);
		PageInfo result = new PageInfo(data);
		return result;
    }
    
	@RequestMapping("/deleteDictTypeValueById")
	public Object deleteDictTypeValueById( @RequestParam(name = "id", required = false, defaultValue = "") String id) {
		Map<String, Object> body = new HashMap<>();
		String rsFlag = sysSetingService.deleteDictTypeValueById(id);
		if("success".equals(rsFlag)) {
			body.put("message", "字典值删除成功!");
			body.put("code", "200");
		}else {
			body.put("message", "字典值删除失败!");
			body.put("code", "999");
		}
		return body;
	}
	
	@RequestMapping("/selectDictValue")
	public Object selectDictValue( @RequestParam(name = "col", required = false, defaultValue = "") String col,
			 @RequestParam(name = "dict_type_id", required = false, defaultValue = "") String dict_type_id) {
		 List<String> data = sysSetingService.selectDictValue(dict_type_id);
		 if(data.size() == 0) {
			 return "999";
		 }
		 StringBuffer sb = new StringBuffer();
		 for(int i=0;i<data.size();i++) {
			 if(sb.length() ==0 ) {
				 sb.append(col+" in ('" +data.get(i)+"'");
			 }else {
				 if(i==data.size()-1) {
					 sb.append(",'" +data.get(i)+"')");
				 }else {
					 sb.append(",'" +data.get(i)+"'");
				 }
			 }
		 }
		 return sb.toString();
	}
	
	@RequestMapping("/insertHolidayDate")
	@Transactional
	public Object insertHolidayDate( @RequestParam(name = "user", required = false, defaultValue = "") String user,
			 @RequestParam(name = "holidays", required = false, defaultValue = "") String holidays,
			 @RequestParam(name = "work_type", required = false, defaultValue = "") String work_type) {
		Map<String, Object> body = new HashMap<>();
		String [] holdata = holidays.split(",");
		for(String holday:holdata) {
			HolidayData data = sysSetingService.selecttHolidayDateByDate(holday,"");
			if(data != null) {//判断是否有此节假日
				body.put("message", "已经存在--"+holday+"--日期维护信息，请重新选择日期!");
				body.put("code", "999");
				return body;
			}
		}
		for(String holday:holdata) {
			sysSetingService.insertHolidayDate(user, holday,work_type);
		}
		body.put("message", "节假日维护成功!");
		body.put("code", "200");
		return body;
	}
	
	@RequestMapping("/selectHolidayDate")
	public Object selecttHolidayDate() {
		List<HolidayData> data = sysSetingService.selecttHolidayDate();
		List <String>workList = new ArrayList<String>();
		List <String>hodList = new ArrayList<String>();
		Map dataMap = new HashMap();
		for(HolidayData d:data) {
			if(d.getWork_type().equals("Y")) {//节假日
				hodList.add(d.getHoliday_date());
			}
			if(d.getWork_type().equals("N")) {//节假日
				workList.add(d.getHoliday_date());
			}
		}
		dataMap.put("workdays", workList);
		dataMap.put("holidays", hodList);
		return dataMap;
	}
	
	@RequestMapping("/deletetHolidayDate")
	public Object deletetHolidayDate( @RequestParam(name = "holiday_date", required = false, defaultValue = "") String holiday_date) {
		Map<String, Object> body = new HashMap<>();
		sysSetingService.deletetHolidayDate(holiday_date);
		body.put("message", "日期维护成功!");
		body.put("code", "200");
		return body;
	}
	
	@RequestMapping("/insertRole")
	public Object insertRole(RoleData roleData) {
		Map<String, Object> body = new HashMap<>();
		RoleData data = sysSetingService.selectRoleByName(roleData.getRole_name());
		if(data != null) {
			body.put("message", "已经存在此角色，新增失败!");
			body.put("code", "999");
		}else {
			String rsFlag = sysSetingService.insertRole(roleData);
			if("success".equals(rsFlag)) {
				body.put("message", "角色新增成功!");
				body.put("code", "200");
			}else {
				body.put("message", "角色新增失败!");
				body.put("code", "999");
			}
		}
		return body;
	}
	
	@RequestMapping("/updateRole")
	public Object updateRole(RoleData roleData) {
		Map<String, Object> body = new HashMap<>();
		RoleData Data= sysSetingService.selectRoleById(roleData.getRole_id());//查询的是原始角色
		if(Data != null) {
			if(Data.getRole_name().equals(roleData.getRole_name())) {//没有修改名称
				String rsFlag = sysSetingService.updateRole(roleData);
				if("success".equals(rsFlag)) {
					body.put("message", "角色更新成功!");
					body.put("code", "200");
				}else {
					body.put("message", "角色更新失败!");
					body.put("code", "999");
				}
			}else {//讲名称也修改了
				RoleData deData = sysSetingService.selectRoleByName(roleData.getRole_name());
				if(deData != null) {
					body.put("message", "此角色已经存在，不能修改!");
					body.put("code", "999");
				}else {
					String rsFlag = sysSetingService.updateRole(roleData);
					if("success".equals(rsFlag)) {
						body.put("message", "角色更新成功!");
						body.put("code", "200");
					}else {
						body.put("message", "角色更新失败!");
						body.put("code", "999");
					}
				}
			}
		}else {//
			body.put("message", "无此角色!");
			body.put("code", "999");
		}
		return body;
	}
	
	@RequestMapping("/deleteRole")
	public Object deleteRole(@RequestParam(name = "role_id", required = false, defaultValue = "") String role_id) {
		Map<String, Object> body = new HashMap<>();
		String rsFlag = sysSetingService.deleteRole(role_id);
		if("success".equals(rsFlag)) {
			body.put("message", "角色删除成功!");
			body.put("code", "200");
		}else {
			body.put("message", "角色删除失败!");
			body.put("code", "999");
		}
		return body;
	}
	
    @RequestMapping("/findRoleTotal")
    public Object findRoleTotal(){
        return sysSetingService.findRoleTotal();
    }
    
    @RequestMapping("/selectRoleById")
    public Object selectRoleById(
            @RequestParam(name = "role_id", required = false, defaultValue = "") String role_id){
        return sysSetingService.selectRoleById(role_id);
    }
    
    @RequestMapping("/selectRole")
    public Object selectRole(RoleData roleData,
    		 @RequestParam(name = "pageNum", required = false, defaultValue = "") int pageNum,
             @RequestParam(name = "pageSize", required = false, defaultValue = "") int pageSize){
		PageHelper.startPage(pageNum, pageSize);
		List<RoleData> data = sysSetingService.selectRole(roleData);
		PageInfo result = new PageInfo(data);
		return result;
    }
    
	@RequestMapping("/insertRoleAndMenuShip")
	@Transactional
	public Object insertRoleAndMenuShip(@RequestParam(name = "menus", required = false, defaultValue = "") String menus,
			 @RequestParam(name = "user", required = false, defaultValue = "") String user,
			 @RequestParam(name = "role_id", required = false, defaultValue = "") String role_id) {
		Map<String, Object> body = new HashMap<>();
		List<RoleData> data = sysSetingService.selectMenu(role_id);
		if(data.size()>0) {
			String mFlag = sysSetingService.updateMenu(role_id, user);
			if(!mFlag.equals("success")) {
				body.put("message", "菜单维护失败!");
				body.put("code", "999");
				return body;
			}
		}
		if("".equals(menus)) {
			body.put("message", "菜单维护成功!");
			body.put("code", "200");
		}else {
			boolean flag = true;
			String[] menuIds = menus.split(",");
			for(String menu_id:menuIds) {
				String rsFlag = sysSetingService.insertRoleAndMenuShip(menu_id, user, role_id);
				if(!"success".equals(rsFlag)) {
					flag = false;
				}
			}
			if(flag) {
				body.put("message", "菜单维护成功!");
				body.put("code", "200");
			}else {
				body.put("message", "菜单维护失败!");
				body.put("code", "999");
			}
		}
		return body;
	}
	
    @RequestMapping("/selectMenu")
    public Object selectMenu(
            @RequestParam(name = "role_id", required = false, defaultValue = "") String role_id){
        return sysSetingService.selectMenu(role_id);
    }
    
  //导出excel表
    @RequestMapping("/exportExcel")
    public void exportExcel(HttpServletResponse response) throws Exception {
  
//        // 初始化HttpServletResponse对象
//        HttpServletResponse response = ServletActionContext.getResponse();
  
        // 定义表的标题
        String title = "机构信息表";
  
        //定义表的列名
        String[] rowsName = new String[] { "机构码", "机构名称", "机构类型", "上级机构码", "地区",
                "所属人行代码", "地址", "地区码", "联系人","联系人电话","银行名称","银行类别" };
  
        //定义表的内容
        List<Object[]> dataList = new ArrayList<Object[]>();
        Object[] objs = null;
        List<OrgData> OrgDataList = sysSetingService.findOrg(new OrgData());
        for (int i = 0; i < OrgDataList.size(); i++) {
        	OrgData org = OrgDataList.get(i);
            objs = new Object[rowsName.length];
            objs[0] = org.getBANKCODE();
            objs[1] = org.getBANK();
            objs[2] = org.getBANKTYPE();
            objs[3] = org.getSUPERIORBANKCODE();
            objs[4] = org.getAREA();
            objs[5] = org.getBELONGPBC();
            objs[6] = org.getADDRESS();
            objs[7] = org.getAREACODE();
            objs[8] = org.getCONTACTPERSON();
            objs[9] = org.getCONTACPHONE();
            objs[10] = org.getBANKNAME();
            objs[11] = org.getBANKCATEGORY();
            dataList.add(objs);
        }
  
        response.reset();//去掉缓存
        response.setHeader("Access-Control-Allow-Origin","*");
    	response.setContentType("application/vnd.ms-excel;charset=UTF-8");	
		response.addHeader("Content-Disposition",
				"attachment;fileName=" + URLEncoder.encode("机构信息表", "utf-8")+".xls");// 设置文件名
        
        // 创建ExportExcel对象
        OrgExcelPoi ex = new OrgExcelPoi(title, rowsName, dataList);
    // 输出Excel文件
        OutputStream output = response.getOutputStream();
        HSSFWorkbook workbook = new HSSFWorkbook(); // 创建工作簿对象
        ex.export(output,workbook);
        output.close();
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
    
    
    
  //导入excel表中的数据
    @RequestMapping("/importExcel")
    public Object importExcel(HttpServletRequest request,
    		@RequestParam(name = "user", required = false, defaultValue = "") String user) throws Exception {
    	Map<String, Object> body = new HashMap<>();
//        // 初始化HttpServletRequest对象
//        HttpServletRequest request = ServletActionContext.getRequest();
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		MultipartFile file = multipartRequest.getFile("file");
		List<OrgData> odList = new ArrayList();
		File path = fileUploadServer(file,user);
//		String path = "D://chinaBankOrgFile//上传机构信息表//1//c101fd77-13db-45cb-b9bc-116d484b957f1584427288401//上传机构信息表.xls";
//        // 获得文件名
//        String filename = getMyfileFileName();
//  
//        // 上传文件到服务器中
//        filename = FileUpload2.upload(filename, myfile);
  
//        Person per = new Person();// 新建一个per对象
//        Dept dept = new Dept();// 新建一个dept对象
//  
//        // 获取服务器中文件的路径
//        String path = request.getSession().getServletContext().getRealPath("")
//                + "/upload/" + filename;
            InputStream is = new FileInputStream(path.toString());//将路径转为输入流对象 输入流对象可以取出来读取
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);//将输入流对象存到工作簿对象里面
  
            // 循环工作表Sheet
            for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
                HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
                if (hssfSheet == null) {
                    continue;
                }
  
                // 循环行Row
                for (int rowNum = 3; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                    OrgData od = new OrgData();
                    HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                    if (hssfRow == null) {
                        continue;
                    }
                    od.setBANKCODE(getValue(hssfRow.getCell(0)));
                    od.setBANK(getValue(hssfRow.getCell(1)));
                    od.setBANKTYPE(getValue(hssfRow.getCell(2)));
                    od.setSUPERIORBANKCODE(getValue(hssfRow.getCell(3)));
                    od.setAREA(getValue(hssfRow.getCell(4)));
                    od.setBELONGPBC(getValue(hssfRow.getCell(5)));
                    od.setADDRESS(getValue(hssfRow.getCell(6)));
                    od.setAREACODE(getValue(hssfRow.getCell(7)));
                    od.setCONTACTPERSON(getValue(hssfRow.getCell(8)));
                    od.setCONTACPHONE(getValue(hssfRow.getCell(9)));
                    od.setBANKNAME(getValue(hssfRow.getCell(10)));
                    od.setBANKCATEGORY(getValue(hssfRow.getCell(11)));
                    odList.add(od);
                }
            }
            boolean flag = insertIntoOrg(odList);
            if(flag) {
            	body.put("message", "上传成功!");
				body.put("code", "200");
            }
        return body;//返回列表显示
    }
    
    @Transactional
    public boolean insertIntoOrg(List<OrgData> odList) throws Exception{
    	for(OrgData od:odList) {
    		 sysSetingService.insetIntoOrg(od);  
    	}
    	return true;
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
			  String filePath = "D://chinaBankOrgFile//" + name + "//" + user + "//" + uuid + "//";
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
     * 得到Excel表中的值
     * 
     * @param hssfCell
     *            Excel中的每一个格子
     * @return Excel中每一个格子中的值
     */
    @SuppressWarnings("static-access")
    private static String getValue(HSSFCell hssfCell) {
        if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
            // 返回布尔类型的值
            return String.valueOf(hssfCell.getBooleanCellValue());
        } else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
        	 // 返回数值类型的值
            Object inputValue = null;// 单元格值
            Long longVal = Math.round(hssfCell.getNumericCellValue());
            Double doubleVal = hssfCell.getNumericCellValue();
            if(Double.parseDouble(longVal + ".0") == doubleVal){   //判断是否含有小数位.0
                inputValue = longVal;
            }
            else{
                inputValue = doubleVal;
            }
            DecimalFormat df = new DecimalFormat("##");    //格式化为四位小数，按自己需求选择；
            return String.valueOf(df.format(inputValue));      //返回String类型
        } else {
            // 返回字符串类型的值
            return String.valueOf(hssfCell.getStringCellValue());
        }
  
    }

}