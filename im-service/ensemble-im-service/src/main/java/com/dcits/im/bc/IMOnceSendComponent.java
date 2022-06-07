package com.dcits.im.bc;

import com.dcits.comet.commons.Context;
import com.dcits.comet.commons.utils.BusiUtil;
import com.dcits.im.api.model.IMOnceSendRequest;
import com.dcits.im.bc.contact.ContactComp;
import com.dcits.im.bc.redis.SignalRedisTemplate;
import com.dcits.im.bc.session.SessionComp;
import com.dcits.im.cache.UmStaffInfoCache;
import com.dcits.im.constants.MsgEnum;
import com.dcits.im.jpa.entity.*;
import com.dcits.im.jpa.model.ContactInfo;
import com.dcits.im.jpa.model.MessageInfo;
import com.dcits.im.jpa.repository.ImRecUserLeftMsgRepository;
import com.dcits.im.jpa.repository.ImRecUserRightMsgRepository;
import com.dcits.im.jpa.repository.UmStaffInfoRepository;
import com.dcits.im.util.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class IMOnceSendComponent {
    @Resource
    private SignalRedisTemplate signalRedisTemplate;
    @Resource
    private ImRecUserLeftMsgRepository leftMsgRepository;
    @Resource
    private ImRecUserRightMsgRepository rightMsgRepository;
    @Resource
    private UmStaffInfoCache staffInfoCache;
    @Resource
    private UmStaffInfoRepository staffInfoRepository;
    @Resource
    private SessionComp sessionComp;
    @Resource
    private ContactComp contactComp;

    /**
     * 通知消息发送
     * @param request
     */
    public void send(IMOnceSendRequest request){
        String fromUserId = request.getBody().getFromUserId();
        String toUserId = request.getBody().getToUserId();
        String content = request.getBody().getContent();
        Integer format = request.getBody().getFormat();

        if(BusiUtil.isNull(staffInfoCache.get(fromUserId))){
            log.error("用{}户不存在！", fromUserId);
            throw BusiUtil.createBusinessException("IM0001", fromUserId);
        }

        if(BusiUtil.isNull(staffInfoCache.get(toUserId))){
            log.error("用{}户不存在！", toUserId);
            throw BusiUtil.createBusinessException("IM0001", toUserId);
        }

        List<ContactInfo> fromUserContactInfoList = contactComp.query(Context.getInstance().getAppHead(), fromUserId);
        Map<String, ContactInfo> fromUserContactInfoMap = fromUserContactInfoList.stream().collect(Collectors.toMap(ContactInfo::getUserId, ContactInfo::get));

        List<ContactInfo> toUserIdContactInfoList = contactComp.query(Context.getInstance().getAppHead(), toUserId);
        Map<String, ContactInfo> toUserIdContactInfoMap = toUserIdContactInfoList.stream().collect(Collectors.toMap(ContactInfo::getUserId, ContactInfo::get));


        //本方会话
        ImRecUserSession thisUserSession;
        if(BusiUtil.isNotNull(fromUserContactInfoMap.get(toUserId))){
            thisUserSession = sessionComp.getSessionAndAddCount(fromUserId, toUserId);
        }else{
            thisUserSession = sessionComp.getOnceSessionAndAddCount(fromUserId, toUserId);
        }
        //对方会话
        ImRecUserSession oppUserSession;
        if(BusiUtil.isNotNull(toUserIdContactInfoMap.get(fromUserId))){
            oppUserSession = sessionComp.getSessionAndAddCount(toUserId, fromUserId);
        }else{
            oppUserSession = sessionComp.getOnceSessionAndAddCount(toUserId, fromUserId);
        }

        String msgId = String.valueOf(SnowFlake.nextId());
        {//发送人R消息
            ImRecUserRightMsg userRightMsg = new ImRecUserRightMsg();
            userRightMsg.setId(msgId);
            userRightMsg.setSessionId(thisUserSession.getSessionId());
            userRightMsg.setUserId(fromUserId);
            userRightMsg.setFromUserId(fromUserId);
            userRightMsg.setToUserId(toUserId);
            userRightMsg.setContent(content);
            userRightMsg.setFormat(format);
            /*
             * 存储到数据库
             */
            long timestamp = System.currentTimeMillis();
            rightMsgRepository.insert(userRightMsg);



            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setSessionId(thisUserSession.getSessionId());
            messageInfo.setId(msgId);
            messageInfo.setFromUserId(fromUserId);
            messageInfo.setDirection(MsgEnum.DIRECT_RIGHT.getCode());//右
            messageInfo.setAction(MsgEnum.MSG_TYPE_USER.getCode());//用户消息
            messageInfo.setIsRead(MsgEnum.IS_READ_YES.getCode());//已读
            messageInfo.setContent(content);
            messageInfo.setReadType(MsgEnum.READ_TYPE_NOT_FORCE.getCode());//非强制阅读
            messageInfo.setFormat(MsgEnum.FORMAT_TEXT.getCode());//文字
            messageInfo.setCreateTime(timestamp);
            thisUserSession.getMessageInfoList().add(messageInfo);
        }

        {//接收人L消息
            ImRecUserLeftMsg userLeftMsg = new ImRecUserLeftMsg();
            userLeftMsg.setId(msgId);
            userLeftMsg.setSessionId(oppUserSession.getSessionId());
            userLeftMsg.setUserId(toUserId);
            userLeftMsg.setFromUserId(fromUserId);
            userLeftMsg.setToUserId(toUserId);
            userLeftMsg.setContent(content);
            userLeftMsg.setFormat(format);
            userLeftMsg.setIsRead(MsgEnum.IS_READ_NO.getCode());//未读
            /*
             * 存储到数据库
             */
            long timestamp = System.currentTimeMillis();
            leftMsgRepository.insert(userLeftMsg);

            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setSessionId(oppUserSession.getSessionId());
            messageInfo.setId(msgId);
            messageInfo.setFromUserId(fromUserId);
            messageInfo.setDirection(MsgEnum.DIRECT_LEFT.getCode());//左
            messageInfo.setAction(MsgEnum.MSG_TYPE_USER.getCode());//用户消息
            messageInfo.setIsRead(MsgEnum.IS_READ_NO.getCode());//未读取
            messageInfo.setContent(content);
            messageInfo.setReadType(MsgEnum.READ_TYPE_NOT_FORCE.getCode());//非强制阅读
            messageInfo.setFormat(MsgEnum.FORMAT_TEXT.getCode());//文字
            messageInfo.setCreateTime(timestamp);
            oppUserSession.getMessageInfoList().add(messageInfo);
        }

        /*
         * 发送上线事件到集群中的其他实例
         */
        signalRedisTemplate.push(thisUserSession);
        signalRedisTemplate.push(oppUserSession);
        return;
    }
}
