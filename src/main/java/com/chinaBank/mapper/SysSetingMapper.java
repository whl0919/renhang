package com.chinaBank.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.chinaBank.bean.SysSeting.AreaData;
import com.chinaBank.bean.SysSeting.DeptData;
import com.chinaBank.bean.SysSeting.DictData;
import com.chinaBank.bean.SysSeting.HolidayData;
import com.chinaBank.bean.SysSeting.OrgData;
import com.chinaBank.bean.SysSeting.RoleData;
import com.chinaBank.bean.SysSeting.UserData;
import com.github.pagehelper.PageInfo;

public interface SysSetingMapper {

	/**
	 * 增部门
	 */
	void addDept(DeptData deptData);

	/**
	 * 改部门
	 */
	void updateDept(DeptData deptData);

	/**
	 * 删部门
	 */
	void deleteDept(DeptData deptData);

	/**
	 * 通过ID查询部门信息
	 */
	DeptData selectSeptById(@Param(value = "id") String id);

	/**
	 * 通过部门查询部门信息
	 */
	DeptData selectSeptByName(String dept_name);

	/**
	 * 查部门
	 */
	List<DeptData> selectSept(DeptData deptData);

	/**
	 * 根据所属机构查询下属机构信息
	 */
	List<AreaData> selectOrgByBelongpbc(@Param(value = "belongpbc") String belongpbc);
	
	/**
	 * 根据地区查询机构
	 */
	List<OrgData> selectOrgById(String org_id);

//	/**
//	 * 根据机构查询用户信息
//	 */
//	List<UserData> selectUserByOrgId(String org_id);

	/**
	 * 增用户
	 */
	void addUser(UserData userData);

	/**
	 * 改用户
	 */
	void updateUser(UserData userData);

	/**
	 * 改密码
	 */
	void updateUserPsd(@Param(value = "id") String id, @Param(value = "user_psd") String user_psd);

	/**
	 * 查用户
	 */
	List<UserData> selectUser(UserData userData);

	/**
	 * 删用户
	 */
	void deleteUser(UserData userData);

	/**
	 * 通过ID查询用户信息
	 */
	List<UserData> selectUserById(String id);
	
//	/**
//	 * 通过机构查询用户信息
//	 */
//	List<UserData> selectUserByOrg(String org_id);
	
	/**
	 * 通过UserId查询用户信息，用于登录
	 */
	UserData selectUserByUserId(@Param(value = "user_id") String user_id, @Param(value = "user_psd") String user_psd);
	
	/**
	 * 查所有机构
	 */
	List<OrgData> selectAllOrg(OrgData orgData);
	
	
	/**
	 * 查询所有部门
	 */
	List<DeptData> findDeptTotal();
	
//	List<OrgData> findOrg(@Param(value = "BANKCODE") String BANKCODE,@Param(value = "SUPERIORBANKCODE") String SUPERIORBANKCODE,@Param(value = "AREACODE") String AREACODE);
	
	void test(String id);
	void test_deno(@Param(value = "id") String id, @Param(value = "name") String name);
	void update_test_deno(String id);
	
	List<String> selectBankType();
	
	/**
	 * 添加字典类型
	 */
	void addDictType(String dict_type, String dict_type_value,String status,String create_user);
	void updateDictType(String dict_type, String dict_type_value,String status,String id,String update_user);
	List<DictData> selectDictType(String dict_type, String dict_type_value,String status);
	int selectDictTypeByType(String dict_type,String dict_type_value);
	DictData selectDictTypeById(String id);
	
	/**
	 * 添加字典值
	 */
	void addDictTypeValue(String dict_id, String dict_value,String dict_type_id,String create_user);
	void updateDictTypeValue(String dict_id ,String dict_value,String id,String update_user);
	List<DictData> selectDictTypeValue(String dict_id, String dict_value,String dict_type_id);
	int selectDictTypeValueByTypeId(String dict_id,String dict_type_id);
	DictData selectDictTypeValueById(String id);
	/**
	 * 删除字典值
	 */
	void deleteDictTypeValueById(String id);
	/**
	 * 返回字典规则,用于新建规则校验
	 */
	List<String> selectDictValue(@Param(value = "dict_type_id") String dict_type_id);
	/**
	 * 节假日维护
	 */
	void insertHolidayDate(@Param(value = "user") String user ,@Param(value = "holiday_date") String holiday_date,@Param(value = "work_type") String work_type);
	/**
	 * 查询节假日
	 */
	List<HolidayData> selecttHolidayDate();
	/**
	 * 删除节假日
	 */
	void deletetHolidayDate(String holiday_date);
	/**
	 * 查询日期
	 */
	HolidayData selecttHolidayDateByDate(@Param(value = "holiday_date") String holiday_date,@Param(value = "work_type") String work_type);
	/**
	 * 添加角色
	 */
	void insertRole(RoleData roleData);
	/**
	 * 更新角色
	 */
	void updateRole(RoleData roleData);

	/**
	 * 删除角色
	 */
	void deleteRole(String role_id);

	/**
	 * 通过ID角色
	 */
	RoleData selectRoleByName(@Param(value = "roleName") String roleName);
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
	void insertRoleAndMenuShip(@Param(value = "menu_id") String menu_id,
			@Param(value = "user") String user,
			@Param(value = "role_id") String role_id);
	/**
	 * 菜单查询
	 */
	List<RoleData> selectMenu(String role_id);
	/**
	 * 更新菜单状态
	 */
	void updateMenu(@Param(value = "role_id") String role_id,@Param(value = "user") String user);
	
	List<DictData> selectDictTypeOrderBy(@Param(value = "dict_type") String dict_type, @Param(value = "dict_type_value") String dict_type_value);
	
	void deleteDictTypeValueByTypeId(@Param(value = "dict_type_id") String dict_type_id);
	
	void deleteDictTypeFor(@Param(value = "dict_type_id") String dict_type_id);
	
	void insetIntoOrg(OrgData orgData);
}
