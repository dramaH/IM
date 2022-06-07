package com.dcits.im.jpa.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.dcits.comet.dao.annotation.TablePk;
import com.dcits.comet.dao.annotation.TableType;
import com.dcits.comet.dao.annotation.TableTypeEnum;

/**
 * @Author hanqian
 * @Description 
 * @Date 2021-12-07 19:13:36
 * @Version 1.0
 */

@Data
@EqualsAndHashCode(callSuper=false)
@TableType(name="IM_REC_HIST_LOGIN",value=TableTypeEnum.UPRIGHT)
public class ImRecHistLogin extends  EnsBaseDbBean{

 /**
	* This field corresponds to the database column IM_REC_HIST_LOGIN.ID
	* @Description  主键ID
	*/
	 @TablePk(index = 1)
	private String id;
 /**
	* This field corresponds to the database column IM_REC_HIST_LOGIN.USER_ID
	* @Description  用户ID
	*/
	private String userId;
 /**
	* This field corresponds to the database column IM_REC_HIST_LOGIN.NODE_ID
	* @Description  节点ID
	*/
	private String nodeId;
 /**
	* This field corresponds to the database column IM_REC_HIST_LOGIN.DEVICE_ID
	* @Description  终端设备ID
	*/
	private String deviceId;
 /**
	* This field corresponds to the database column IM_REC_HIST_LOGIN.DEVICE_NAME
	* @Description  终端设备型号
	*/
	private String deviceName;
 /**
	* This field corresponds to the database column IM_REC_HIST_LOGIN.HOST
	* @Description  绑定的服务器IP
	*/
	private String host;
 /**
	* This field corresponds to the database column IM_REC_HIST_LOGIN.CHANNEL
	* @Description  渠道类型
	*/
	private String channel;
 /**
	* This field corresponds to the database column IM_REC_HIST_LOGIN.APP_VERSION
	* @Description  终端应用版本
	*/
	private String appVersion;
 /**
	* This field corresponds to the database column IM_REC_HIST_LOGIN.OS_VERSION
	* @Description  终端系统版本
	*/
	private String osVersion;
 /**
	* This field corresponds to the database column IM_REC_HIST_LOGIN.LANGUAGE
	* @Description  终端语言
	*/
	private String language;
 /**
	* This field corresponds to the database column IM_REC_HIST_LOGIN.LONGITUDE
	* @Description  经度
	*/
	private String longitude;
 /**
	* This field corresponds to the database column IM_REC_HIST_LOGIN.LATITUDE
	* @Description  维度
	*/
	private String latitude;
 /**
	* This field corresponds to the database column IM_REC_HIST_LOGIN.LOCATION
	* @Description  位置
	*/
	private String location;
 /**
	* This field corresponds to the database column IM_REC_HIST_LOGIN.BIND_TIME
	* @Description  登录时间
	*/
	private String bindTime;
}
