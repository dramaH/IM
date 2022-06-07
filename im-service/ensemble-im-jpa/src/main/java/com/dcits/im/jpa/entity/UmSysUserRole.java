package com.dcits.im.jpa.entity;

import com.dcits.comet.dao.annotation.TableType;
import com.dcits.comet.dao.annotation.TableTypeEnum;
import com.dcits.ensemble.dbmanage.dbmodel.EnsBaseDbBean;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @Author 
 * @Description 
 * @Date 2021-04-15 09:54:47
 * @Version 1.0
 */

@Data
@EqualsAndHashCode(callSuper=false)
@TableType(name="UM_SYS_USER_ROLE",value=TableTypeEnum.UPRIGHT)
public class UmSysUserRole extends EnsBaseDbBean {

 /**
	* This field corresponds to the database column UM_SYS_USER_ROLE.ID
	* @Description  主键ID
	*/
	private String id;
 /**
	* This field corresponds to the database column UM_SYS_USER_ROLE.USER_ID
	* @Description  用户编号
	*/
	private String userId;
 /**
	* This field corresponds to the database column UM_SYS_USER_ROLE.ROLE_ID
	* @Description  角色ID
	*/
	private String roleId;
 /**
	* This field corresponds to the database column UM_SYS_USER_ROLE.ROLE_NAME
	* @Description  角色名称
	*/
	private String roleName;
 /**
	* This field corresponds to the database column UM_SYS_USER_ROLE.LAST_UPDATE_TIME
	* @Description  最后修改时间
	*/
	private Date lastUpdateTime;
 /**
	* This field corresponds to the database column UM_SYS_USER_ROLE.LAST_UPDATE_USER
	* @Description  最后修改人
	*/
	private String lastUpdateUser;
}
