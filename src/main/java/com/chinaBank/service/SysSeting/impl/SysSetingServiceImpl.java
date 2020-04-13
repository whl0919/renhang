package com.chinaBank.service.SysSeting.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chinaBank.bean.SysSeting.AreaData;
import com.chinaBank.bean.SysSeting.DeptData;
import com.chinaBank.bean.SysSeting.DictData;
import com.chinaBank.bean.SysSeting.HolidayData;
import com.chinaBank.bean.SysSeting.OrgData;
import com.chinaBank.bean.SysSeting.RoleData;
import com.chinaBank.bean.SysSeting.UserData;
import com.chinaBank.mapper.SysSetingMapper;
import com.chinaBank.service.SysSeting.SysSetingService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service(value = "sysSetingService")
public class SysSetingServiceImpl implements SysSetingService {

	@Autowired
	private SysSetingMapper sysSetingMapper;// 这里会报错，但是并不会影响

	//部门信息维护----start------
	@Override
	public String addDept(DeptData deptData) {
		sysSetingMapper.addDept(deptData);
		return "success";
	}

	@Override
	public String updateDept(DeptData deptData) {
		sysSetingMapper.updateDept(deptData);
		return "success";
	}

	@Override
	public String deleteDept(DeptData deptData) {
		sysSetingMapper.deleteDept(deptData);
		return "success";
	}
	
	@Override
	public DeptData selectSeptById(String id) {
		DeptData deptData = sysSetingMapper.selectSeptById(id);
		return deptData;
	}

	@Override
	public DeptData selectSeptByName(String dept_name) {
		DeptData deptData = sysSetingMapper.selectSeptByName(dept_name);
		return deptData;
	}
	
	@Override
	public PageInfo<DeptData> findAllDept(int pageNum, int pageSize, DeptData deptData) {
		PageHelper.startPage(pageNum, pageSize);
		List<DeptData> septDatas = sysSetingMapper.selectSept(deptData);
		PageInfo result = new PageInfo(septDatas);
		return result;
	}
	//部门信息维护----end------
	
	//用户信息维护----start------
	@Override
	public String addUser(UserData userData) {
		sysSetingMapper.addUser(userData);
		return "success";
	}

	@Override
	public String updateUser(UserData userData) {
		sysSetingMapper.updateUser(userData);
		return "success";
	}

//	@Override
//	public PageInfo<UserData> findAllUser(int pageNum, int pageSize, UserData userData) {
//
//	}
	
	@Override
	public List<UserData> selectUserById(String id) {
		List<UserData> userDatas = sysSetingMapper.selectUserById(id);
		return userDatas;
	}

	@Override
	public String updateUserPsd(String id, String user_psd) {
		sysSetingMapper.updateUserPsd(id, user_psd);
		return "success";
	}

//	@Override
//	public Map login(String user_id, String user_psd) {
//
//	}
	
	@Override
	public List<AreaData> selectOrgByBelongpbc(String belongpbc) {
		return	sysSetingMapper.selectOrgByBelongpbc(belongpbc);
	}
	//用户信息维护----end------
	
	//机构信息维护----start------
	@Override
	public PageInfo<OrgData> findAllOrg(int pageNum, int pageSize, OrgData orgData) {
		PageHelper.startPage(pageNum, pageSize);
		List<OrgData> orgDatas = sysSetingMapper.selectAllOrg(orgData);
		PageInfo result = new PageInfo(orgDatas);
		return result;
	}
	//机构信息维护----end------

	@Override
	public List<DeptData> findDeptTotal() {
		return sysSetingMapper.findDeptTotal();
	}

	@Override
	public UserData selectUserByUserId(String user_id, String user_psd) {
		return sysSetingMapper.selectUserByUserId(user_id, user_psd);
	}

	@Override
	public List<OrgData> selectOrgById(String org_id) {
		return sysSetingMapper.selectOrgById(org_id);
	}

	@Override
	public List<UserData> selectUser(UserData userData) {
		return sysSetingMapper.selectUser(userData);
	}

	@Override
	public List<OrgData> findOrg(OrgData orgData) {
		return sysSetingMapper.selectAllOrg(orgData);
	}

	@Override
	public void test(String id) {
		sysSetingMapper.test(id);
	}

	@Override
	public void test_deno(String id,String name) {
		sysSetingMapper.test_deno(id,name);
	}

	@Override
	public void update_test_deno(String id) {
		sysSetingMapper.update_test_deno(id);
	}

	@Override
	public List<String> selectBankType() {
		return 	sysSetingMapper.selectBankType();
	}

	@Override
	public String addDictType(String dict_type, String dict_type_value, String status,String create_user) {
		sysSetingMapper.addDictType(dict_type, dict_type_value, status,create_user);
		return "success";
	}

	@Override
	public List<DictData> selectDictType(String dict_type, String dict_type_value, String status) {
		return sysSetingMapper.selectDictType( dict_type,  dict_type_value,  status);
	}

	@Override
	public int selectDictTypeByType(String dict_type,String dict_type_value) {
		return sysSetingMapper.selectDictTypeByType(dict_type,dict_type_value);
	}

	@Override
	public String addDictTypeValue(String dict_id, String dict_value, String dict_type_id,String create_user) {
		sysSetingMapper.addDictTypeValue(dict_id, dict_value, dict_type_id,create_user);
		return "success";
	}

	@Override
	public List<DictData> selectDictTypeValue(String dict_id, String dict_value, String dict_type_id) {
		return sysSetingMapper.selectDictTypeValue(dict_id, dict_value, dict_type_id);
	}

	@Override
	public int selectDictTypeValueByTypeId(String dict_id, String dict_type_id) {
		return sysSetingMapper.selectDictTypeValueByTypeId(dict_id, dict_type_id);
	}

	@Override
	public DictData selectDictTypeById(String id) {
		return sysSetingMapper.selectDictTypeById(id);
	}

	@Override
	public DictData selectDictTypeValueById(String id) {
		return sysSetingMapper.selectDictTypeValueById(id);
	}

	@Override
	public String updateDictType(String dict_type, String dict_type_value, String status,String id,String update_user) {
		sysSetingMapper.updateDictType(dict_type, dict_type_value, status,id,update_user);
		return "success";
	}

	@Override
	public String updateDictTypeValue(String dict_id,String dict_value,String id,String update_user) {
		sysSetingMapper.updateDictTypeValue(dict_id, dict_value,id,update_user);
		return "success";
	}

	@Override
	public String deleteDictTypeValueById(String id) {
		sysSetingMapper.deleteDictTypeValueById(id);
		return "success";
	}

	@Override
	public List<String> selectDictValue(String dict_type_id) {
		 return sysSetingMapper.selectDictValue(dict_type_id);
	}

	@Override
	public String insertHolidayDate(String user,String holiday_date,String work_type) {
		sysSetingMapper.insertHolidayDate(user, holiday_date,work_type);
		return "success";
	}

	@Override
	public List<HolidayData> selecttHolidayDate() {
		return sysSetingMapper.selecttHolidayDate();
	}

	@Override
	public String deletetHolidayDate(String holiday_date) {
		sysSetingMapper.deletetHolidayDate(holiday_date);
		return "success";
	}

	@Override
	public HolidayData selecttHolidayDateByDate(String holiday_date,String work_type) {
		return sysSetingMapper.selecttHolidayDateByDate(holiday_date,work_type);
	}

	@Override
	public String insertRole(RoleData roleData) {
		sysSetingMapper.insertRole(roleData);
		return "success";
	}

	@Override
	public String updateRole(RoleData roleData) {
		sysSetingMapper.updateRole(roleData);
		return "success";
	}

	@Override
	public String deleteRole(String role_id) {
		sysSetingMapper.deleteRole(role_id);
		return "success";
	}

	@Override
	public RoleData selectRoleByName(String roleName) {
		return sysSetingMapper.selectRoleByName(roleName);
	}

	@Override
	public List<RoleData> findRoleTotal() {
		return sysSetingMapper.findRoleTotal();
	}

	@Override
	public List<RoleData> selectRole(RoleData roleData) {
		return sysSetingMapper.selectRole(roleData);
	}

	@Override
	public RoleData selectRoleById(String role_id) {
		return sysSetingMapper.selectRoleById(role_id);
	}

	@Override
	public String insertRoleAndMenuShip(String menu_id, String user, String role_id) {
		sysSetingMapper.insertRoleAndMenuShip(menu_id, user, role_id);
		return "success";
	}

	@Override
	public List<RoleData> selectMenu(String role_id) {
		return sysSetingMapper.selectMenu(role_id);
	}

	@Override
	public String updateMenu(String role_id,String user) {
		sysSetingMapper.updateMenu(role_id,user);
		return "success";
	}

	@Override
	public List<DictData> selectDictTypeOrderBy(String dict_type, String dict_type_value) {
		return sysSetingMapper.selectDictTypeOrderBy( dict_type,  dict_type_value);
	}

	@Override
	public String deleteDictTypeValueByTypeId(String dict_type_id) {
		sysSetingMapper.deleteDictTypeValueByTypeId( dict_type_id);
		return "success";
	}

	@Override
	public String deleteDictTypeFor(String dict_type_id) {
		// TODO Auto-generated method stub
		sysSetingMapper.deleteDictTypeFor( dict_type_id);
		return "success";
	}

	@Override
	public String insetIntoOrg(OrgData orgData) {
		sysSetingMapper.insetIntoOrg( orgData);
		return "success";
	}
}
