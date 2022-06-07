package com.dcits.im.jpa.repository;

import com.dcits.comet.IContext;
import com.dcits.comet.commons.data.RowArgs;
import com.dcits.comet.commons.utils.BusiUtil;
import com.dcits.comet.commons.utils.PageUtil;
import com.dcits.comet.dao.DaoSupport;
import java.util.List;
import java.util.Map;

import com.dcits.comet.dao.model.QueryResult;
import com.dcits.comet.rpc.api.model.head.AppHead;
import com.dcits.im.jpa.entity.EnsBaseDbBean;
import com.dcits.im.jpa.entity.ImCnfParam;
import com.dcits.im.jpa.entity.ImRecUserLeftMsg;
import com.dcits.im.jpa.repository.exception.DataNotFoundException;
import com.dcits.im.jpa.repository.exception.MoreThanOneDataFoundException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 数据库操作抽象类
 * @Auther hanqian
 * @CreateTime 2021-12-06
 * @param <T>
 */
public abstract class BusinessRepository<T extends EnsBaseDbBean> {
    public static final String TOTAL_FLAG_E = "E";//E-查询总记录数

    @Autowired
    protected DaoSupport daoSupport;

    public BusinessRepository() {
    }

    /**
     * 单笔插入
     * @param entity 实体对象
     * @return
     */
    public int insert(@NonNull T entity) {
        if (entity == null) {
            throw new NullPointerException("entity");
        } else {
            long timestamp = System.currentTimeMillis();
            entity.setState(0);
            entity.setCreateTime(timestamp);
            entity.setTimestamp(timestamp);
            return this.daoSupport.insert(entity);
        }
    }

    /**
     * 单笔插入
     * @param entity 实体对象
     * @return
     */
    public int insert(@NonNull long timestamp, @NonNull T entity) {
        if (entity == null) {
            throw new NullPointerException("entity");
        } else {
            entity.setState(0);
            entity.setCreateTime(timestamp);
            entity.setTimestamp(timestamp);
            return this.daoSupport.insert(entity);
        }
    }

    /**
     * 多笔插入
     * @param list 实体对象列表
     * @return
     */
    public int insert(@NonNull List<T> list) {
        if (list == null) {
            throw new NullPointerException("list");
        } else {
            long timestamp = System.currentTimeMillis();
            list.forEach((entity) -> {
                entity.setState(0);
                entity.setCreateTime(timestamp);
                entity.setTimestamp(timestamp);
            });
            return this.daoSupport.insert(list);
        }
    }

    /**
     * 更新
     * @param entity 实体对象
     * @return
     */
    public int update(@NonNull T entity) {
        if (entity == null) {
            throw new NullPointerException("entity");
        } else {
            entity.setTimestamp(System.currentTimeMillis());
            return this.daoSupport.update(entity);
        }
    }

    /**
     * 删除
     * @param entity 实体对象
     * @return
     */
    public int delete(@NonNull T entity) {
        if (entity == null) {
            throw new NullPointerException("entity");
        } else {
            return this.daoSupport.delete(entity);
        }
    }

    /**
     * 批量插入
     * @param list 实体对象列表
     * @return
     */
    public int insertAddBatch(@NonNull List<T> list) {
        if (list == null) {
            throw new NullPointerException("list");
        } else {
            long timestamp = System.currentTimeMillis();
            list.forEach((entity) -> {
                entity.setState(0);
                entity.setCreateTime(timestamp);
                entity.setTimestamp(timestamp);
            });
            return this.daoSupport.insertAddBatch(list);
        }
    }

    /**
     * 批量更新
     * @param list 实体对象列表
     * @return
     */
    public int updateAddBatch(@NonNull List<T> list) {
        if (list == null) {
            throw new NullPointerException("list");
        } else {
            long timestamp = System.currentTimeMillis();
            list.forEach((entity) -> {
                entity.setTimestamp(timestamp);
            });
            return this.daoSupport.updateAddBatch(list);
        }
    }

    /**
     * 查询
     * @param t
     * @return
     */
    public List<T> selectList(AppHead appHead, T t){
        RowArgs rowArgs = PageUtil.convertAppHead(appHead);
        if (BusiUtil.isNotNull(rowArgs)) {
            QueryResult<T> basePoQueryResult = daoSupport.selectQueryResult(t.getClass().getName().concat(".selectList"),
                    t, rowArgs.getPageIndex(), rowArgs.getLimit());
            if (TOTAL_FLAG_E.equals(appHead.getTotalFlag())) {
                appHead.setTotalRows(String.valueOf(basePoQueryResult.getTotalrecord()));
            }
            return basePoQueryResult.getResultlist();
        } else {
            List<T> basePoQueryList = daoSupport.selectList(t.getClass().getName().concat(".selectList"), t);
            if (TOTAL_FLAG_E.equals(appHead.getTotalFlag())) {
                appHead.setTotalRows(String.valueOf(basePoQueryList.size()));
            }
            return basePoQueryList;
        }
    }

    /**
     * 查询
     * @param t
     * @return
     */
    public List<T> selectList(T t){
        RowArgs rowArgs = PageUtil.convertAppHead(IContext.getInstance().getAppHead());
        if (BusiUtil.isNotNull(rowArgs)) {
            QueryResult<T> basePoQueryResult = daoSupport.selectQueryResult(t.getClass().getName().concat(".selectList"),
                    t, rowArgs.getPageIndex(), rowArgs.getLimit());
            if (TOTAL_FLAG_E.equals(IContext.getInstance().getAppHead().getTotalFlag())) {
                IContext.getInstance().getAppHead().setTotalRows(String.valueOf(basePoQueryResult.getTotalrecord()));
            }
            return basePoQueryResult.getResultlist();
        } else {
            List<T> basePoQueryList = daoSupport.selectList(t.getClass().getName().concat(".selectList"), t);
            if (TOTAL_FLAG_E.equals(IContext.getInstance().getAppHead().getTotalFlag())) {
                IContext.getInstance().getAppHead().setTotalRows(String.valueOf(basePoQueryList.size()));
            }
            return basePoQueryList;
        }
    }

    /**
     * 查询
     * @param t
     * @return
     */
    public List<T> selectPage(T t){
        //TODO:待实现公共方法T
        return daoSupport.selectList(t);
    }

    /**
     * 查询
     * @param t
     * @param nullable
     * @return
     */
    public T selectOne(T t){
        return selectOne(t, false);
    }


    /**
     * 查询
     * @param t
     * @param nullable
     * @return
     */
    public T selectOne(T t, boolean nullable){
        List<T> list = daoSupport.selectList(t);
        if(BusiUtil.isNull(list) && nullable){
            throw new DataNotFoundException("data not found!");
        }
        if(BusiUtil.isNotNull(list)){
            if(list.size()>1){
                throw new MoreThanOneDataFoundException("more than one found!");
            }
            return list.get(0);
        }
        return null;
    }

}
