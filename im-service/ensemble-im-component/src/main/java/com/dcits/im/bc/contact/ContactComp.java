package com.dcits.im.bc.contact;

import com.dcits.comet.commons.utils.BusiUtil;
import com.dcits.comet.rpc.api.model.head.AppHead;
import com.dcits.im.constants.UserCategoryEnum;
import com.dcits.im.constants.UserTypeEnum;
import com.dcits.im.jpa.entity.UmStaffInfo;
import com.dcits.im.jpa.entity.UmSysUserRole;
import com.dcits.im.jpa.model.ContactInfo;
import com.dcits.im.jpa.repository.UmStaffInfoRepository;
import com.dcits.im.jpa.repository.UmSysUserRoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ContactComp {

    private final static String ROLE_ID_AREA_MGR_RETAIL="000006";//零售区域经理角色ID

    private final static String ROLE_ID_AREA_MGR_BATCH="000802";//批售区域经理角色ID

    @Resource
    private UmStaffInfoRepository staffInfoRepository;
    @Resource
    private UmSysUserRoleRepository sysUserRoleRepository;

    public List<ContactInfo> query(AppHead appHead, String userId){
        UmStaffInfo staffInfo = new UmStaffInfo();
        staffInfo.setUserId(userId);
        staffInfo = staffInfoRepository.selectOne(staffInfo);
        if(BusiUtil.isNull(staffInfo)){
            log.error("用户{}不存在！", userId);
            throw BusiUtil.createBusinessException("IM0001",userId);
        };
        List<ContactInfo> contactInfoList = new ArrayList<>();

        //用户是零售区域经理
        UmSysUserRole sysUserRole = new UmSysUserRole();
        sysUserRole.setUserId(userId);
        sysUserRole.setRoleId(ROLE_ID_AREA_MGR_RETAIL);
        sysUserRole = sysUserRoleRepository.selectOne(sysUserRole);
        if(BusiUtil.isNotNull(sysUserRole)){
            Map para = new HashMap<>();
            para.put("userId", userId);
            contactInfoList.addAll(staffInfoRepository.selectListForRetailDealer(appHead, para));
        }

        //用户是批售区域经理
        sysUserRole = new UmSysUserRole();
        sysUserRole.setUserId(userId);
        sysUserRole.setRoleId(ROLE_ID_AREA_MGR_BATCH);
        sysUserRole = sysUserRoleRepository.selectOne(sysUserRole);
        if(BusiUtil.isNotNull(sysUserRole)){
            Map para = new HashMap<>();
            para.put("userId", userId);
            contactInfoList.addAll(staffInfoRepository.selectListForBatchDealer(appHead, para));
        }

        //同用户WORK_NO的用户
        Map para = new HashMap<>();
        para.put("userId", userId);
        contactInfoList.addAll(staffInfoRepository.selectListForSameWorkNo(appHead, para));

        //数据去重
        contactInfoList = contactInfoList.stream().distinct().collect(Collectors.toList());
        return contactInfoList;
    }
}
