package com.dcits.im.jpa.repository;

import com.dcits.ensemble.repository.BusinessRepository;
import com.dcits.im.jpa.entity.UmSysUserRole;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class UmSysUserRoleRepository extends BusinessRepository<UmSysUserRole> {

    public List<UmSysUserRole> selectListNoPaging(UmSysUserRole umSysUserRole){
        return daoSupport.selectList(umSysUserRole);
    }
    public UmSysUserRole selectOne(UmSysUserRole umSysUserRole){
        return daoSupport.selectOne(umSysUserRole);
    }
}
