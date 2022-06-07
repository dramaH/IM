package com.dcits.im.jpa.repository;

import com.dcits.comet.IContext;
import com.dcits.comet.commons.data.RowArgs;
import com.dcits.comet.commons.utils.BusiUtil;
import com.dcits.comet.commons.utils.PageUtil;
import com.dcits.comet.dao.model.QueryResult;
import com.dcits.comet.rpc.api.model.head.AppHead;
import com.dcits.im.jpa.entity.ImRecSysMsg;
import com.dcits.im.jpa.model.MessageInfo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 系统消息表操作
 */
@Repository
public class ImRecSysMsgRepository extends BusinessRepository<ImRecSysMsg> {

    public List<ImRecSysMsg> selectListForRead(AppHead appHead, Map map){
        RowArgs rowArgs = PageUtil.convertAppHead(appHead);
        if (BusiUtil.isNotNull(rowArgs)) {
            QueryResult<ImRecSysMsg> basePoQueryResult = daoSupport.selectQueryResult(ImRecSysMsg.class.getName().concat(".selectListForRead"),
                    map, rowArgs.getPageIndex(), rowArgs.getLimit());
            if (TOTAL_FLAG_E.equals(appHead.getTotalFlag())) {
                appHead.setTotalRows(String.valueOf(basePoQueryResult.getTotalrecord()));
            }
            return basePoQueryResult.getResultlist();
        } else {
            List<ImRecSysMsg> basePoQueryList = daoSupport.selectList(ImRecSysMsg.class.getName().concat(".selectListForRead"), map);
            if (TOTAL_FLAG_E.equals(appHead.getTotalFlag())) {
                appHead.setTotalRows(String.valueOf(basePoQueryList.size()));
            }
            return basePoQueryList;
        }
    }

    public List<MessageInfo> selectMessageList(MessageInfo messageInfo){
        List<MessageInfo> basePoQueryList = daoSupport.selectList(ImRecSysMsg.class.getName().concat(".selectMessageList"), messageInfo);
        if (TOTAL_FLAG_E.equals(IContext.getInstance().getAppHead().getTotalFlag())) {
            IContext.getInstance().getAppHead().setTotalRows(String.valueOf(basePoQueryList.size()));
        }
        return basePoQueryList;
    }
}
