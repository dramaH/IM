package com.dcits.im.jpa.repository;

import com.dcits.comet.commons.data.RowArgs;
import com.dcits.comet.commons.utils.BusiUtil;
import com.dcits.comet.commons.utils.PageUtil;
import com.dcits.comet.dao.model.QueryResult;
import com.dcits.comet.rpc.api.model.head.AppHead;
import com.dcits.im.jpa.entity.ImRecUserLeftMsg;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 用户聊天左消息表操作
 */
@Repository
public class ImRecUserLeftMsgRepository extends BusinessRepository<ImRecUserLeftMsg> {

    public List<ImRecUserLeftMsg> selectListForRead(AppHead appHead, Map map){
        RowArgs rowArgs = PageUtil.convertAppHead(appHead);
        if (BusiUtil.isNotNull(rowArgs)) {
            QueryResult<ImRecUserLeftMsg> basePoQueryResult = daoSupport.selectQueryResult(ImRecUserLeftMsg.class.getName().concat(".selectListForRead"),
                    map, rowArgs.getPageIndex(), rowArgs.getLimit());
            if (TOTAL_FLAG_E.equals(appHead.getTotalFlag())) {
                appHead.setTotalRows(String.valueOf(basePoQueryResult.getTotalrecord()));
            }
            return basePoQueryResult.getResultlist();
        } else {
            List<ImRecUserLeftMsg> basePoQueryList = daoSupport.selectList(ImRecUserLeftMsg.class.getName().concat(".selectListForRead"), map);
            if (TOTAL_FLAG_E.equals(appHead.getTotalFlag())) {
                appHead.setTotalRows(String.valueOf(basePoQueryList.size()));
            }
            return basePoQueryList;
        }
    }
}
