package com.dcits.im.jpa.repository;

import com.dcits.comet.IContext;
import com.dcits.comet.commons.data.RowArgs;
import com.dcits.comet.commons.utils.BusiUtil;
import com.dcits.comet.commons.utils.PageUtil;
import com.dcits.comet.dao.model.QueryResult;
import com.dcits.comet.rpc.api.model.head.AppHead;
import com.dcits.ensemble.repository.BusinessRepository;
import com.dcits.im.jpa.entity.UmStaffInfo;
import com.dcits.im.jpa.model.ContactInfo;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

/**
 * 用户信息
 */
@Repository
public class UmStaffInfoRepository extends BusinessRepository<UmStaffInfo> {
    public static final String TOTAL_FLAG_E = "E";//E-查询总记录数

    public UmStaffInfo selectOne(UmStaffInfo staffInfo){
        return daoSupport.selectOne(staffInfo);
    }

    public List<UmStaffInfo> selectList(AppHead appHead, UmStaffInfo staffInfo){
        RowArgs rowArgs = PageUtil.convertAppHead(appHead);
        if (BusiUtil.isNotNull(rowArgs)) {
            QueryResult<UmStaffInfo> basePoQueryResult = daoSupport.selectQueryResult(UmStaffInfo.class.getName().concat(".selectList"),
                    staffInfo, rowArgs.getPageIndex(), rowArgs.getLimit());
            if (TOTAL_FLAG_E.equals(appHead.getTotalFlag())) {
                appHead.setTotalRows(String.valueOf(basePoQueryResult.getTotalrecord()));
            }
            return basePoQueryResult.getResultlist();
        } else {
            List<UmStaffInfo> basePoQueryList = daoSupport.selectList(UmStaffInfo.class.getName().concat(".selectList"), staffInfo);
            if (TOTAL_FLAG_E.equals(appHead.getTotalFlag())) {
                appHead.setTotalRows(String.valueOf(basePoQueryList.size()));
            }
            return basePoQueryList;
        }
    }

    public List<ContactInfo> selectListForRetailDealer(AppHead appHead, Map map){
        RowArgs rowArgs = PageUtil.convertAppHead(IContext.getInstance().getAppHead());
        if (BusiUtil.isNotNull(rowArgs)) {
            QueryResult<ContactInfo> basePoQueryResult = daoSupport.selectQueryResult(UmStaffInfo.class.getName().concat(".selectListForRetailDealer"),
                    map, rowArgs.getPageIndex(), rowArgs.getLimit());
            if (TOTAL_FLAG_E.equals(IContext.getInstance().getAppHead().getTotalFlag())) {
                IContext.getInstance().getAppHead().setTotalRows(String.valueOf(basePoQueryResult.getTotalrecord()));
            }
            return basePoQueryResult.getResultlist();
        } else {
            List<ContactInfo> basePoQueryList = daoSupport.selectList(UmStaffInfo.class.getName().concat(".selectListForRetailDealer"), map);
            if (TOTAL_FLAG_E.equals(IContext.getInstance().getAppHead().getTotalFlag())) {
                IContext.getInstance().getAppHead().setTotalRows(String.valueOf(basePoQueryList.size()));
            }
            return basePoQueryList;
        }
    }

    public List<ContactInfo> selectListForBatchDealer(AppHead appHead, Map map){
        RowArgs rowArgs = PageUtil.convertAppHead(IContext.getInstance().getAppHead());
        if (BusiUtil.isNotNull(rowArgs)) {
            QueryResult<ContactInfo> basePoQueryResult = daoSupport.selectQueryResult(UmStaffInfo.class.getName().concat(".selectListForBatchDealer"),
                    map, rowArgs.getPageIndex(), rowArgs.getLimit());
            if (TOTAL_FLAG_E.equals(IContext.getInstance().getAppHead().getTotalFlag())) {
                IContext.getInstance().getAppHead().setTotalRows(String.valueOf(basePoQueryResult.getTotalrecord()));
            }
            return basePoQueryResult.getResultlist();
        } else {
            List<ContactInfo> basePoQueryList = daoSupport.selectList(UmStaffInfo.class.getName().concat(".selectListForBatchDealer"), map);
            if (TOTAL_FLAG_E.equals(IContext.getInstance().getAppHead().getTotalFlag())) {
                IContext.getInstance().getAppHead().setTotalRows(String.valueOf(basePoQueryList.size()));
            }
            return basePoQueryList;
        }
    }

    public List<ContactInfo> selectListForSameWorkNo(AppHead appHead, Map map){
        RowArgs rowArgs = PageUtil.convertAppHead(IContext.getInstance().getAppHead());
        if (BusiUtil.isNotNull(rowArgs)) {
            QueryResult<ContactInfo> basePoQueryResult = daoSupport.selectQueryResult(UmStaffInfo.class.getName().concat(".selectListForSameWorkNo"),
                    map, rowArgs.getPageIndex(), rowArgs.getLimit());
            if (TOTAL_FLAG_E.equals(IContext.getInstance().getAppHead().getTotalFlag())) {
                IContext.getInstance().getAppHead().setTotalRows(String.valueOf(basePoQueryResult.getTotalrecord()));
            }
            return basePoQueryResult.getResultlist();
        } else {
            List<ContactInfo> basePoQueryList = daoSupport.selectList(UmStaffInfo.class.getName().concat(".selectListForSameWorkNo"), map);
            if (TOTAL_FLAG_E.equals(IContext.getInstance().getAppHead().getTotalFlag())) {
                IContext.getInstance().getAppHead().setTotalRows(String.valueOf(basePoQueryList.size()));
            }
            return basePoQueryList;
        }
    }

    public List<ContactInfo> selectListForSameWorkNo(Map map){
        RowArgs rowArgs = PageUtil.convertAppHead(IContext.getInstance().getAppHead());
        if (BusiUtil.isNotNull(rowArgs)) {
            QueryResult<ContactInfo> basePoQueryResult = daoSupport.selectQueryResult(UmStaffInfo.class.getName().concat(".selectListForSameWorkNo"),
                    map, rowArgs.getPageIndex(), rowArgs.getLimit());
            if (TOTAL_FLAG_E.equals(IContext.getInstance().getAppHead().getTotalFlag())) {
                IContext.getInstance().getAppHead().setTotalRows(String.valueOf(basePoQueryResult.getTotalrecord()));
            }
            return basePoQueryResult.getResultlist();
        } else {
            List<ContactInfo> basePoQueryList = daoSupport.selectList(UmStaffInfo.class.getName().concat(".selectListForSameWorkNo"), map);
            if (TOTAL_FLAG_E.equals(IContext.getInstance().getAppHead().getTotalFlag())) {
                IContext.getInstance().getAppHead().setTotalRows(String.valueOf(basePoQueryList.size()));
            }
            return basePoQueryList;
        }
    }

    public List<UmStaffInfo> selectListByUserRole(AppHead appHead, Map map){
        RowArgs rowArgs = PageUtil.convertAppHead(appHead);
        if (BusiUtil.isNotNull(rowArgs)) {
            QueryResult<UmStaffInfo> basePoQueryResult = daoSupport.selectQueryResult(UmStaffInfo.class.getName().concat(".selectListByUserRole"),
                    map, rowArgs.getPageIndex(), rowArgs.getLimit());
            if (TOTAL_FLAG_E.equals(appHead.getTotalFlag())) {
                appHead.setTotalRows(String.valueOf(basePoQueryResult.getTotalrecord()));
            }
            return basePoQueryResult.getResultlist();
        } else {
            List<UmStaffInfo> basePoQueryList = daoSupport.selectList(UmStaffInfo.class.getName().concat(".selectListByUserRole"), map);
            if (TOTAL_FLAG_E.equals(appHead.getTotalFlag())) {
                appHead.setTotalRows(String.valueOf(basePoQueryList.size()));
            }
            return basePoQueryList;
        }
    }
}
