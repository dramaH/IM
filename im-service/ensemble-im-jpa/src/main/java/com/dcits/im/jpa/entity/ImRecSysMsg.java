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
 * @Date 2021-12-18 14:16:57
 * @Version 1.0
 */

@Data
@EqualsAndHashCode(callSuper=false)
@TableType(name="IM_REC_SYS_MSG",value=TableTypeEnum.UPRIGHT)
public class ImRecSysMsg extends  EnsBaseDbBean{

 /**
	* This field corresponds to the database column IM_REC_SYS_MSG.ID
	* @Description  主键ID
	*/
	 @TablePk(index = 1)
	private String id;
 /**
	* This field corresponds to the database column IM_REC_SYS_MSG.SESSION_ID
	* @Description  会话ID
	*/
	private String sessionId;
 /**
	* This field corresponds to the database column IM_REC_SYS_MSG.SYSTEM_IDS
	* @Description  终端系统类型
	*/
	private String systemIds;
 /**
	* This field corresponds to the database column IM_REC_SYS_MSG.TO_USER_ID
	* @Description  接收用户ID
	*/
	private String toUserId;
 /**
	* This field corresponds to the database column IM_REC_SYS_MSG.READ_TYPE
	* @Description  阅读类型
	*/
	private Integer readType;
 /**
	* This field corresponds to the database column IM_REC_SYS_MSG.TITLE
	* @Description  消息标题
	*/
	private String title;
 /**
	* This field corresponds to the database column IM_REC_SYS_MSG.CONTENT
	* @Description  消息内容
	*/
	private String content;
 /**
	* This field corresponds to the database column IM_REC_SYS_MSG.FORMAT
	* @Description  消息格式
	*/
	private Integer format;
 /**
	* This field corresponds to the database column IM_REC_SYS_MSG.IS_READ
	* @Description  已读标志
	*/
	private Integer isRead;
}
