/**
 * <p>Title: MbsdCore14002370Out.java</p>
 * <p>Description: 根据用户角色查询用户信息</p>
 * <p>Copyright: Copyright (c) 2014-2018</p>
 * <p>Company: dcits</p>
 *
 * @author admin
 * @version v1.0
 */
package com.dcits.im.restful.model;

import com.dcits.comet.rpc.api.model.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/***
 * 根据用户角色查询用户信息</br>
 * @version v1.0
 * @since v1.0
 * @author admin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MbsdCore14002370Out extends BaseResponse {

    private static final long serialVersionUID = 1L;

    //用户信息
    private List<UserInfo> userInfoList;

}
