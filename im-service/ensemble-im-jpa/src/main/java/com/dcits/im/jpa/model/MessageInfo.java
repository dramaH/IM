package com.dcits.im.jpa.model;

import com.dcits.im.jpa.entity.EnsBaseDbBean;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author hanqian
 * @Description 
 * @Date 2021-12-17 10:55:20
 * @Version 1.0
 */

@Data
@EqualsAndHashCode(callSuper=false)
public class MessageInfo extends EnsBaseDbBean{
	private Integer rows;
	private String sessionId;
	private String id;
	private String fromUserId;
	private String userId;
	private String userName;
	private Integer direction;
	private Integer action;
	private Integer isRead;
	private String title;
	private String content;
	private Integer readType;
	private Integer format;
	private String msgDate;
}
