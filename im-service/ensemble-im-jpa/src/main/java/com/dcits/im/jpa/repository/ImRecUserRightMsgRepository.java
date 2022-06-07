package com.dcits.im.jpa.repository;

import com.dcits.comet.IContext;
import com.dcits.comet.commons.data.RowArgs;
import com.dcits.comet.commons.utils.BusiUtil;
import com.dcits.comet.commons.utils.PageUtil;
import com.dcits.comet.dao.model.QueryResult;
import com.dcits.comet.rpc.api.model.head.AppHead;
import com.dcits.im.jpa.entity.ImRecUserLeftMsg;
import com.dcits.im.jpa.entity.ImRecUserRightMsg;
import com.dcits.im.jpa.model.MessageInfo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 用户聊天右消息表操作
 */
@Repository
public class ImRecUserRightMsgRepository extends BusinessRepository<ImRecUserRightMsg> {

    public List<MessageInfo> selectListLeftAndRight(MessageInfo messageInfo){
        List<MessageInfo> basePoQueryList = daoSupport.selectList(ImRecUserRightMsg.class.getName().concat(".selectListLeftAndRight"), messageInfo);
        if (TOTAL_FLAG_E.equals(IContext.getInstance().getAppHead().getTotalFlag())) {
            IContext.getInstance().getAppHead().setTotalRows(String.valueOf(basePoQueryList.size()));
        }
        return basePoQueryList;
    }
}
