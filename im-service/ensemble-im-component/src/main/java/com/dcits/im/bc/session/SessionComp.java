package com.dcits.im.bc.session;

import com.dcits.comet.commons.utils.BusiUtil;
import com.dcits.im.cache.UmStaffInfoCache;
import com.dcits.im.constants.MsgEnum;
import com.dcits.im.jpa.entity.ImRecUserSession;
import com.dcits.im.jpa.repository.ImRecUserSessionRepository;
import com.dcits.im.util.UUIDUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

@Component
public class SessionComp {
    @Resource
    private ImRecUserSessionRepository userSessionRepository;
    @Resource
    private UmStaffInfoCache staffInfoCache;

    public ImRecUserSession getNoticeSession(String userId, String oppUserId){
        return getSessionAndAddCount(userId, oppUserId, MsgEnum.IS_TOP_YES, MsgEnum.IS_ONLINE_NO, MsgEnum.IS_OPEN_YES,
                MsgEnum.MSG_TYPE_SYS, true);
    }

    public ImRecUserSession getSessionAndAddCount(String userId, String oppUserId){
        return getSessionAndAddCount(userId, oppUserId, MsgEnum.IS_TOP_NO, MsgEnum.IS_ONLINE_NO, MsgEnum.IS_OPEN_YES,
                MsgEnum.MSG_TYPE_USER, true);
    }

    public ImRecUserSession getSessionOnly(String userId, String oppUserId){
        return getSessionAndAddCount(userId, oppUserId, MsgEnum.IS_TOP_NO, MsgEnum.IS_ONLINE_YES, MsgEnum.IS_OPEN_YES,
                MsgEnum.MSG_TYPE_USER, false);
    }

    public ImRecUserSession getOnceSessionAndAddCount(String userId, String oppUserId){
        return getSessionAndAddCount(userId, oppUserId, MsgEnum.IS_TOP_YES, MsgEnum.IS_ONLINE_YES, MsgEnum.IS_OPEN_YES,
                MsgEnum.MSG_TYPE_ONCE, true);
    }

    private synchronized ImRecUserSession getSessionAndAddCount(@NotNull String userId, @NotNull String oppUserId,
                                                                MsgEnum isTop, MsgEnum isOnline, MsgEnum isOpen, MsgEnum msgType, boolean addNoReadNum){
        ImRecUserSession userSession = new ImRecUserSession();
        userSession.setUserId(userId);
        userSession.setOppUserId(oppUserId);
        userSession = userSessionRepository.selectOne(userSession);
        if(BusiUtil.isNull(userSession)){
            userSession = new ImRecUserSession();
            userSession.setId(UUIDUtil.getUUID());
            userSession.setSessionId(userId+"#_#"+oppUserId);
            userSession.setSessionName(staffInfoCache.get(oppUserId).getUserName());
            userSession.setUserId(userId);
            userSession.setOppUserId(oppUserId);
            userSession.setIsTop(isTop.getCode());
            userSession.setIsOnline(isOnline.getCode());
            userSession.setIsOpen(isOpen.getCode());
            userSession.setMsgType(msgType.getCode());
            userSession.setMsgNum(1);
            if(addNoReadNum){
                userSession.setNoReadNum(1);
            }else{
                userSession.setNoReadNum(0);
            }
            userSessionRepository.insert(userSession);
        }else{
            userSession.setMsgNum(userSession.getMsgNum()+1);
            if(addNoReadNum){
                userSession.setNoReadNum(userSession.getNoReadNum()+1);
            }
            userSession.setMsgType(msgType.getCode());
            userSessionRepository.update(userSession);
        }
        return userSession;
    }
}
