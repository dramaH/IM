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
@TableType(name="IM_CNF_PARAM",value=TableTypeEnum.UPRIGHT)
public class ImCnfParam extends  EnsBaseDbBean{

 /**
	* This field corresponds to the database column IM_CNF_PARAM.ID
	* @Description  主键ID
	*/
	 @TablePk(index = 1)
	private String id;
 /**
	* This field corresponds to the database column IM_CNF_PARAM.PARAM_KEY
	* @Description  参数键
	*/
	private String paramKey;
 /**
	* This field corresponds to the database column IM_CNF_PARAM.PARAM_VALUE
	* @Description  参数值
	*/
	private String paramValue;
}
