package com.dcits.im.jpa.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.dcits.comet.dao.annotation.TablePk;
import java.math.BigDecimal;
import java.util.Date;
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
@TableType(name="IM_REC_USER_ARCHIVE_MSG",value=TableTypeEnum.UPRIGHT)
public class ImRecUserArchiveMsg extends  EnsBaseDbBean{

 /**
	* This field corresponds to the database column IM_REC_USER_ARCHIVE_MSG.ID
	* @Description  主键ID
	*/
	 @TablePk(index = 1)
	private String id;
 /**
	* This field corresponds to the database column IM_REC_USER_ARCHIVE_MSG.SESSION_ID
	* @Description  会话ID
	*/
	private String sessionId;
 /**
	* This field corresponds to the database column IM_REC_USER_ARCHIVE_MSG.USER_ID
	* @Description  用户ID
	*/
	private String userId;
 /**
	* This field corresponds to the database column IM_REC_USER_ARCHIVE_MSG.FROM_SESSION_ID
	* @Description  发起方会话ID
	*/
	private String fromSessionId;
 /**
	* This field corresponds to the database column IM_REC_USER_ARCHIVE_MSG.TO_USER_ID
	* @Description  接收用户ID
	*/
	private String toUserId;
 /**
	* This field corresponds to the database column IM_REC_USER_ARCHIVE_MSG.CONTENT
	* @Description  消息内容
	*/
	private String content;
 /**
	* This field corresponds to the database column IM_REC_USER_ARCHIVE_MSG.FORMAT
	* @Description  消息格式
	*/
	private Integer format;
 /**
	* This field corresponds to the database column IM_REC_USER_ARCHIVE_MSG.IS_READ
	* @Description  已读标志
	*/
	private Integer isRead;
 /**
	* This field corresponds to the database column IM_REC_USER_ARCHIVE_MSG.ARCHIVE_TIME
	* @Description  归档时间
	*/
	private Date archiveTime;
}
