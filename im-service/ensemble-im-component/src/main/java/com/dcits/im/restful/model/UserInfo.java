package com.dcits.im.restful.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author
 * @Description
 * @Date 2021-08-23 13:53:05
 * @Version 1.0
 */

@Data
public class UserInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	//用户编号
	private String userId;

	//用户姓名
	private String userName;

	//用户类型
	private String userType;

	//身份证号码
	private String documentId;

	//手机号码
	private String mobile;

	//性别
	private String sex;

	//电子邮箱
	private String email;

	//用户状态
	private String userStatus;

	//休假代理岗位名称
	private String roleNames;
}
