package com.dcits.im.jpa.entity;

import com.dcits.comet.dao.annotation.TablePk;
import com.dcits.comet.dao.annotation.TableType;
import com.dcits.comet.dao.annotation.TableTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @Author hanqian
 * @Description 
 * @Date 2021-12-08 14:54:35
 * @Version 1.0
 */

@Data
@EqualsAndHashCode(callSuper=false)
@TableType(name="IM_REC_CURRENT_USER",value=TableTypeEnum.UPRIGHT)
public class ImRecCurrentUser extends  EnsBaseDbBean {

 /**
	* This field corresponds to the database column IM_REC_CURRENT_USER.ID
	* @Description  主键ID
	*/
	 @TablePk(index = 1)
	private String id;
 /**
	* This field corresponds to the database column IM_REC_CURRENT_USER.USER_ID
	* @Description  用户ID
	*/
	private String userId;
 /**
	* This field corresponds to the database column IM_REC_CURRENT_USER.NODE_ID
	* @Description  节点ID
	*/
	private String nodeId;
 /**
	* This field corresponds to the database column IM_REC_CURRENT_USER.DEVICE_ID
	* @Description  终端设备ID
	*/
	private String deviceId;
 /**
	* This field corresponds to the database column IM_REC_CURRENT_USER.DEVICE_NAME
	* @Description  终端设备型号
	*/
	private String deviceName;
 /**
	* This field corresponds to the database column IM_REC_CURRENT_USER.HOST
	* @Description  绑定的服务器IP
	*/
	private String host;
 /**
	* This field corresponds to the database column IM_REC_CURRENT_USER.CHANNEL
	* @Description  渠道类型
	*/
	private String channel;
 /**
	* This field corresponds to the database column IM_REC_CURRENT_USER.APP_VERSION
	* @Description  终端应用版本
	*/
	private String appVersion;
 /**
	* This field corresponds to the database column IM_REC_CURRENT_USER.OS_VERSION
	* @Description  终端系统版本
	*/
	private String osVersion;
 /**
	* This field corresponds to the database column IM_REC_CURRENT_USER.LANGUAGE
	* @Description  终端语言
	*/
	private String language;
 /**
	* This field corresponds to the database column IM_REC_CURRENT_USER.LONGITUDE
	* @Description  经度
	*/
	private String longitude;
 /**
	* This field corresponds to the database column IM_REC_CURRENT_USER.LATITUDE
	* @Description  维度
	*/
	private String latitude;
 /**
	* This field corresponds to the database column IM_REC_CURRENT_USER.LOCATION
	* @Description  位置
	*/
	private String location;
 /**
	* This field corresponds to the database column IM_REC_CURRENT_USER.BIND_TIME
	* @Description  登录时间
	*/
	private String bindTime;
 /**
	* This field corresponds to the database column IM_REC_CURRENT_USER.IS_ONLINE
	* @Description  在线状态
	*/
	private Integer isOnline;
}
