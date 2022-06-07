package com.dcits.im.cache;

import com.alibaba.fastjson.JSON;
import com.dcits.comet.commons.utils.BusiUtil;
import com.dcits.comet.rpc.api.model.head.AppHead;
import com.dcits.im.bc.redis.KeyValueRedisTemplate;
import com.dcits.im.jpa.entity.UmStaffInfo;
import com.dcits.im.jpa.repository.UmStaffInfoRepository;
import com.dcits.im.util.AppHeadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Component
public class UmStaffInfoCache {
    private static final String USER_KEY="USER_KEY_";

    public static final String USER_SYS="999999";
    public static final String USER_SYS_NAME="系统消息";

    @Resource
    private KeyValueRedisTemplate keyValueRedisTemplate;

    @Resource
    private UmStaffInfoRepository staffInfoRepository;

    public UmStaffInfo get(String userId) {
        if(BusiUtil.isNull(userId)){
            return null;
        }
        String REDIS_KEY= USER_KEY+userId;
        if(keyValueRedisTemplate.hasKey(REDIS_KEY)){
            //缓存中已有
            return JSON.parseObject(keyValueRedisTemplate.get(REDIS_KEY), UmStaffInfo.class);
        }else{
            UmStaffInfo staffInfo = new UmStaffInfo();
            staffInfo.setUserId(userId);

            if(USER_SYS.equals(userId)){
                //系统消息虚拟用户加入缓存
                staffInfo.setUserName(USER_SYS_NAME);
                keyValueRedisTemplate.set(REDIS_KEY, JSON.toJSONString(staffInfo));
                return staffInfo;
            }else{
                List<UmStaffInfo> umStaffInfos = staffInfoRepository.selectList(AppHeadUtil.getAppHeadPage("20"), staffInfo);
                if(BusiUtil.isNull(umStaffInfos)){
                    //空用户加入缓存
                    keyValueRedisTemplate.setTemp(REDIS_KEY, null);
                    return null;
                }else{
                    //查询到用户表中的用户加入缓存
                    staffInfo = umStaffInfos.get(0);
                    keyValueRedisTemplate.set(REDIS_KEY, JSON.toJSONString(staffInfo));
                    return staffInfo;
                }
            }
        }
    }
}
