package com.dcits.im.jpa.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.dcits.comet.dao.annotation.TablePk;
import com.dcits.comet.dao.annotation.TableType;
import com.dcits.comet.dao.annotation.TableTypeEnum;

/**
 * @Author hanqian
 * @Description 
 * @Date 2021-12-07 19:13:37
 * @Version 1.0
 */

@Data
@EqualsAndHashCode(callSuper=false)
@TableType(name="IM_REC_CSH_SESSION_TRANSFER",value=TableTypeEnum.UPRIGHT)
public class ImRecCshSessionTransfer extends  EnsBaseDbBean{

 /**
	* This field corresponds to the database column IM_REC_CSH_SESSION_TRANSFER.ID
	* @Description  主键ID
	*/
	 @TablePk(index = 1)
	private String id;
 /**
	* This field corresponds to the database column IM_REC_CSH_SESSION_TRANSFER.CUST_USER_ID
	* @Description  客户用户ID
	*/
	private String custUserId;
 /**
	* This field corresponds to the database column IM_REC_CSH_SESSION_TRANSFER.SESSION_ID_OLD
	* @Description  原会话ID
	*/
	private String sessionIdOld;
 /**
	* This field corresponds to the database column IM_REC_CSH_SESSION_TRANSFER.FROM_CSH_ID
	* @Description  转接发起客服ID
	*/
	private String fromCshId;
 /**
	* This field corresponds to the database column IM_REC_CSH_SESSION_TRANSFER.FROM_USER_ID
	* @Description  发送用户ID
	*/
	private String fromUserId;
 /**
	* This field corresponds to the database column IM_REC_CSH_SESSION_TRANSFER.SESSION_ID_NEW
	* @Description  新会话ID
	*/
	private String sessionIdNew;
 /**
	* This field corresponds to the database column IM_REC_CSH_SESSION_TRANSFER.TO_CSH_ID
	* @Description  接收方客服ID
	*/
	private String toCshId;
 /**
	* This field corresponds to the database column IM_REC_CSH_SESSION_TRANSFER.TO_USER_ID
	* @Description  接收用户ID
	*/
	private String toUserId;
}
