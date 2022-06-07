package com.dcits.im.jpa.entity;

import com.dcits.im.jpa.model.MessageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.dcits.comet.dao.annotation.TablePk;
import java.util.ArrayList;
import java.util.List;

import com.dcits.comet.dao.annotation.TableType;
import com.dcits.comet.dao.annotation.TableTypeEnum;

/**
 * @Author hanqian
 * @Description 
 * @Date 2021-12-28 10:11:14
 * @Version 1.0
 */

@Data
@EqualsAndHashCode(callSuper=false)
@TableType(name="IM_REC_USER_SESSION",value=TableTypeEnum.UPRIGHT)
public class ImRecUserSession extends  EnsBaseDbBean{

 /**
	* This field corresponds to the database column IM_REC_USER_SESSION.ID
	* @Description  主键ID
	*/
	 @TablePk(index = 1)
	private String id;
 /**
	* This field corresponds to the database column IM_REC_USER_SESSION.SESSION_ID
	* @Description  会话ID
	*/
	private String sessionId;
 /**
	* This field corresponds to the database column IM_REC_USER_SESSION.SESSION_NAME
	* @Description  会话名称
	*/
	private String sessionName;
 /**
	* This field corresponds to the database column IM_REC_USER_SESSION.USER_ID
	* @Description  用户ID
	*/
	private String userId;
 /**
	* This field corresponds to the database column IM_REC_USER_SESSION.OPP_USER_ID
	* @Description  对方用户ID
	*/
	private String oppUserId;
 /**
	* This field corresponds to the database column IM_REC_USER_SESSION.IS_TOP
	* @Description  置顶状态
	*/
	private Integer isTop;
 /**
	* This field corresponds to the database column IM_REC_USER_SESSION.IS_ONLINE
	* @Description  在线状态
	*/
	private Integer isOnline;
 /**
	* This field corresponds to the database column IM_REC_USER_SESSION.IS_OPEN
	* @Description  开启标志
	*/
	private Integer isOpen;
 /**
	* This field corresponds to the database column IM_REC_USER_SESSION.MSG_TYPE
	* @Description  消息类型
	*/
	private Integer msgType;
 /**
	* This field corresponds to the database column IM_REC_USER_SESSION.MSG_NUM
	* @Description  消息数
	*/
	private Integer msgNum;
 /**
	* This field corresponds to the database column IM_REC_USER_SESSION.NO_READ_NUM
	* @Description  未读消息数
	*/
	private Integer noReadNum;


	private List<MessageInfo> messageInfoList = new ArrayList<>();
	/**
	 * 关联消息id
	 */
	private String referenceId;
	/**
	 * 渠道
	 */
	private String sendChannel;
}
