package com.dcits.im.jpa.entity;

import com.dcits.comet.dao.model.BasePo;
import lombok.Data;

@Data
public class EnsBaseDbBean extends BasePo implements Cloneable{
    /**
     * @Description  主键ID
     */
    private String id;
    /**
     * @Description  创建时间
     */
    private Long createTime;
    /**
     * @Description  时间戳
     */
    private Long timestamp;
    /**
     * @Description  状态
     */
    private Integer state;
}
