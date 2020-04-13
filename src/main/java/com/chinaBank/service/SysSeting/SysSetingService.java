package com.chinaBank.service.SysSeting;

import java.util.List;

import com.chinaBank.bean.SysSeting.AreaData;
import com.chinaBank.bean.SysSeting.DeptData;
import com.chinaBank.bean.SysSeting.DictData;
import com.chinaBank.bean.SysSeting.HolidayData;
import com.chinaBank.bean.SysSeting.OrgData;
import com.chinaBank.bean.SysSeting.RoleData;
import com.chinaBank.bean.SysSeting.UserData;
import com.github.pagehelper.PageInfo;

public interface SysSetingService {

	/**
	 * 添加部门信息
	 */
	String addDept(DeptData deptData);

	/**
	 * 更新部门信息
	 */
	String updateDept(DeptData deptData);

	/**
	 * 删除部门信息
	 */
	String deleteDept(DeptData deptData);

	/**
	 * 通过ID查询部门
	 */
	DeptData selectSeptById(String id);
	
	/**
	 * 查询所有部门
	 */
	List<DeptData> findDeptTotal();

	/**
	 * 通过部门名称查询部门
	 */
	DeptData selectSeptByName(String dept_name);

	/**
	 * 查询部门信息
	 */
	PageInfo<DeptData> findAllDept(int pageNum, int pageSize, DeptData deptData);
	
	/**
	 * 根据所属机构查询下属机构信息
	 */
	List<AreaData> selectOrgByBelongpbc(String belongpbc);
	
	/**
	 * 添加用户信息
	 */
	String addUser(UserData userData);

	/**
	 * 更新用户信息
	 */
	String updateUser(UserData userData);

	/**
	 * 更新密码
	 */
	String updateUserPsd(String id, String user_psd);

	/**
	 * 通过ID查询用户
	 */
	List<UserData> selectUserById(String id);
	
//	/**
//	 * 用于登录查询
//	 */
//	Map login(String user_id, String user_psd);

//	/**
//	 * 查询用户信息
//	 */
//	PageInfo<UserData> findAllUser(int pageNum, int pageSize, UserData userData);

	/**
	 * 查询所有机构信息
	 */
	PageInfo<OrgData> findAllOrg(int pageNum, int pageSize, OrgData orgData);
	/**
	 * 查询机构信息
	 */
	List<OrgData> findOrg(OrgData orgData);
	/**
	 * 查询用户信息
	 */
	UserData selectUserByUserId(String user_id,String user_psd);
	/**
	 * 查询机构信息
	 */
	List<OrgData> selectOrgById(String org_id);
	/**
	 * 查询用户信息
	 */
	List<UserData> selectUser(UserData userData);
	
	void test(String id);
	void test_deno(String id,String name);
	void update_test_deno(String id);
	/**
	 * 查询机构类型
	 */
	List<String> selectBankType();
	
	/**
	 * 添加字典类型
	 */
	String addDictType(String dict_type, String dict_type_value,String status,String create_user);
	/**
	 * 更新字典类型
	 */
	String updateDictType(String dict_type, String dict_type_value,String status,String id,String update_user);
	/**
	 * 查询字典类型
	 */
	List<DictData> selectDictType(String dict_type, String dict_type_value,String status);
	/**
	 * 查询字典类型
	 */
	DictData selectDictTypeById(String id);
	/**
	 * 查询是否有当前字典类型
	 */
	int selectDictTypeByType(String dict_type,String dict_type_value);
	/**
	 * 增加字典值
	 */
	String addDictTypeValue(String dict_id, String dict_value,String dict_type_id,String create_user);
	/**
	 * 更新字典值
	 */
	String updateDictTypeValue(String dict_id, String dict_value,String id,String update_user);
	/**
	 * 查询字典值
	 */
	List<DictData> selectDictTypeValue(String dict_id, String dict_value,String dict_type_id);
	/**
	 * 查询是否有当前字典值
	 */
	int selectDictTypeValueByTypeId(String dict_id,String dict_type_id);
	/**
	 * 查询字典值
	 */
	DictData selectDictTypeValueById(String id);
	/**
	 * 删除字典值
	 */
	String deleteDictTypeValueById(String id);
	
	/**
	 * 返回字典规则,用于规则校验
	 */
	List<String> selectDictValue(String dict_type_id);
	/**
	 * 节假日维护
	 */
	String insertHolidayDate(String user,String holiday_date,String work_type);
	/**
	 * 查询节假日
	 */
	List<HolidayData> selecttHolidayDate();
	/**
	 * 删除节假日
	 */
	String deletetHolidayDate(String holiday_date);
	/**
	 * 查询日期
	 */
	HolidayData selecttHolidayDateByDate(String holiday_date,String work_type);
	/**
	 * 添加角色
	 */
	String insertRole(RoleData roleData);
	/**
	 * 更新角色
	 */
	String updateRole(RoleData roleData);

	/**
	 * 删除角色
	 */
	String deleteRole(String role_id);

	/**
	 * 通过名称角色
	 */
	RoleData selectRoleByName(String roleName);
	/**
	 * 通过id角色
	 */
	RoleData selectRoleById(String role_id);
	
	/**
	 * 查询所有角色
	 */
	List<RoleData> findRoleTotal();
	/**
	 * 条件查询查询角色
	 */
	List<RoleData> selectRole(RoleData roleData);
	/**
	 * 菜单维护
	 */
	String insertRoleAndMenuShip(String menu_id,String user,String role_id);
	/**
	 * 菜单查询
	 */
	List<RoleData> selectMenu(String role_id);
	/**
	 * 更新菜单状态
	 */
	String updateMenu(String role_id,String user);
	
	/**
	 * 查询字典排序
	 */
	List<DictData> selectDictTypeOrderBy(String dict_type, String dict_type_value);
	
	/**
	 * 批量删除字典值
	 */
	String deleteDictTypeValueByTypeId(String dict_type_id);
	
	/**
	 * 删除字典类型
	 */
	String deleteDictTypeFor(String dict_type_id);
	
	String insetIntoOrg(OrgData orgData);
}