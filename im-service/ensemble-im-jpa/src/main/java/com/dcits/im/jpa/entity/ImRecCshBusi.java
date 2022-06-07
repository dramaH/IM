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
@TableType(name="IM_REC_CSH_BUSI",value=TableTypeEnum.UPRIGHT)
public class ImRecCshBusi extends  EnsBaseDbBean{

 /**
	* This field corresponds to the database column IM_REC_CSH_BUSI.ID
	* @Description  主键ID
	*/
	 @TablePk(index = 1)
	private String id;
 /**
	* This field corresponds to the database column IM_REC_CSH_BUSI.CSH_ID
	* @Description  客服ID
	*/
	private String cshId;
 /**
	* This field corresponds to the database column IM_REC_CSH_BUSI.CURR_SESSION_NUM
	* @Description  当前活跃会话数
	*/
	private Integer currSessionNum;
}
