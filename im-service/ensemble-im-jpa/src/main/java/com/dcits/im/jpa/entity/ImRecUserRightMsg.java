package com.dcits.im.jpa.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.dcits.comet.dao.annotation.TablePk;
import java.math.BigDecimal;
import com.dcits.comet.dao.annotation.TableType;
import com.dcits.comet.dao.annotation.TableTypeEnum;

/**
 * @Author hanqian
 * @Description 
 * @Date 2021-12-17 10:55:20
 * @Version 1.0
 */

@Data
@EqualsAndHashCode(callSuper=false)
@TableType(name="IM_REC_USER_RIGHT_MSG",value=TableTypeEnum.UPRIGHT)
public class ImRecUserRightMsg extends  EnsBaseDbBean{

 /**
	* This field corresponds to the database column IM_REC_USER_RIGHT_MSG.ID
	* @Description  主键ID
	*/
	 @TablePk(index = 1)
	private String id;
 /**
	* This field corresponds to the database column IM_REC_USER_RIGHT_MSG.SESSION_ID
	* @Description  会话ID
	*/
	private String sessionId;
 /**
	* This field corresponds to the database column IM_REC_USER_RIGHT_MSG.CHANNEL
	* @Description  渠道类型
	*/
	private String channel;
 /**
	* This field corresponds to the database column IM_REC_USER_RIGHT_MSG.USER_ID
	* @Description  用户ID
	*/
	private String userId;
 /**
	* This field corresponds to the database column IM_REC_USER_RIGHT_MSG.FROM_USER_ID
	* @Description  发送用户ID
	*/
	private String fromUserId;
 /**
	* This field corresponds to the database column IM_REC_USER_RIGHT_MSG.TO_USER_ID
	* @Description  接收用户ID
	*/
	private String toUserId;
 /**
	* This field corresponds to the database column IM_REC_USER_RIGHT_MSG.CONTENT
	* @Description  消息内容
	*/
	private String content;
 /**
	* This field corresponds to the database column IM_REC_USER_RIGHT_MSG.FORMAT
	* @Description  消息格式
	*/
	private Integer format;
}
