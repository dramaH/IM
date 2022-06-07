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
@TableType(name="IM_INF_CSH",value=TableTypeEnum.UPRIGHT)
public class ImInfCsh extends  EnsBaseDbBean{

 /**
	* This field corresponds to the database column IM_INF_CSH.ID
	* @Description  主键ID
	*/
	 @TablePk(index = 1)
	private String id;
 /**
	* This field corresponds to the database column IM_INF_CSH.CSH_ID
	* @Description  客服ID
	*/
	private String cshId;
 /**
	* This field corresponds to the database column IM_INF_CSH.CSH_NAME
	* @Description  客服昵称
	*/
	private String cshName;
 /**
	* This field corresponds to the database column IM_INF_CSH.USER_ID
	* @Description  用户ID
	*/
	private String userId;
 /**
	* This field corresponds to the database column IM_INF_CSH.USER_NAME
	* @Description  用户名称
	*/
	private String userName;
}
