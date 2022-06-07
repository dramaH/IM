package com.dcits.im.jpa.entity;

import com.dcits.comet.dao.annotation.TablePk;
import com.dcits.comet.dao.annotation.TableType;
import com.dcits.comet.dao.annotation.TableTypeEnum;
import com.dcits.ensemble.dbmanage.dbmodel.EnsBaseDbBean;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @Author 
 * @Description 
 * @Date 2021-12-13 13:53:05
 * @Version 1.0
 */

@Data
@EqualsAndHashCode(callSuper=false)
@TableType(name="UM_STAFF_INFO",value=TableTypeEnum.UPRIGHT)
public class UmStaffInfo extends  EnsBaseDbBean{

 /**
	* @Description  人员ID
	*/
	 @TablePk(index = 1)
	private String staffId;
 /**
	* @Description  用户编号
	*/
	private String userId;
 /**
	* @Description  用户姓名
	*/
	private String userName;
 /**
	* @Description  部门编号
	*/
	private String departmentNo;
 /**
	* @Description  人员类型
	*/
	private String staffType;
 /**
	* @Description  用户大类
	*/
	private String userBigType;
 /**
	* @Description  用户类别
	*/
	private String userCategory;
 /**
	* @Description  单位类型
	*/
	private String workType;
 /**
	* @Description  单位编号
	*/
	private String workNo;
 /**
	* @Description  用户类型
	*/
	private String userType;
 /**
	* @Description  SP管理员类型
	*/
	private String spAdminType;
 /**
	* @Description  机构编号
	*/
	private String branchNo;
 /**
	* @Description  网点编号
	*/
	private String outletNo;
 /**
	* @Description  性别
	*/
	private String sex;
 /**
	* @Description  身份证号码
	*/
	private String documentId;
 /**
	* @Description  手机号码
	*/
	private String mobile;
 /**
	* @Description  办公电话
	*/
	private String telephone;
 /**
	* @Description  备注
	*/
	private String remark;
 /**
	* @Description  电子邮箱
	*/
	private String email;
 /**
	* @Description  用户头像
	*/
	private String userProfile;
 /**
	* @Description  人员私章ID
	*/
	private String staffSealId;
 /**
	* @Description  用户状态
	*/
	private String userStatus;
 /**
	* @Description  最后修改人
	*/
	private String lastUpdateUser;
 /**
	* @Description  最后修改时间
	*/
	private Date lastUpdateTime;
 /**
	* @Description  创建人
	*/
	private String createUser;
 /**
	* This field corresponds to the database column UM_STAFF_INFO.CREATE_TIME
	* @Description  创建时间
	*/
	private Date createTime;
}
