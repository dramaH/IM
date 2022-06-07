package com.dcits.im.bc;

import com.dcits.comet.commons.utils.BusiUtil;
import com.dcits.im.api.model.IMMessageHisQueryRequest;
import com.dcits.im.api.model.IMMessageHisQueryResponse;
import com.dcits.im.cache.UmStaffInfoCache;
import com.dcits.im.constants.MsgEnum;
import com.dcits.im.jpa.entity.ImRecUserSession;
import com.dcits.im.jpa.model.MessageInfo;
import com.dcits.im.jpa.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class IMMessageHisQueryComponent {

    @Resource
    private ImRecUserSessionRepository sessionRepository;
    @Resource
    private ImRecUserRightMsgRepository rightMsgRepository;
    @Resource
    private ImRecSysMsgRepository sysMsgRepository;
    @Resource
    private UmStaffInfoCache staffInfoCache;

    public IMMessageHisQueryResponse doQuery(IMMessageHisQueryRequest request){
        IMMessageHisQueryResponse response = new IMMessageHisQueryResponse();

        if(BusiUtil.isNull(request.getBody().getSessionId())){
            log.error("会话ID不能为空！");
            throw BusiUtil.createBusinessException("IM0002");
        }
        ImRecUserSession userSession = new ImRecUserSession();
        userSession.setSessionId(request.getBody().getSessionId());
        userSession = sessionRepository.selectOne(userSession);

        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setSessionId(request.getBody().getSessionId());
        messageInfo.setId(request.getBody().getMsgId());
        messageInfo.setRows(request.getBody().getRows());

        List<IMMessageHisQueryResponse.Body.Message> msgList = new ArrayList<>();
        if(BusiUtil.isNull(userSession)){
            return response;
        }
        if(userSession.getMsgType()==MsgEnum.MSG_TYPE_SYS.getCode()){//系统消息
            List<MessageInfo> messageInfoList = sysMsgRepository.selectMessageList(messageInfo);
            for (MessageInfo msg : messageInfoList) {
                IMMessageHisQueryResponse.Body.Message message = new IMMessageHisQueryResponse.Body.Message();
                message.setMsgId(msg.getId());
                message.setUserName(MsgEnum.MSG_TYPE_SYS.getName());
                message.setDirection(msg.getDirection());
                message.setAction(userSession.getMsgType());
                message.setIsRead(msg.getIsRead());//已读标志
                message.setTitle(msg.getTitle());
                message.setContent(msg.getContent());
                message.setReadType(msg.getReadType());
                message.setFormat(msg.getFormat());
                message.setMsgDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date(msg.getCreateTime())));
                message.setTimestamp(msg.getCreateTime());
                msgList.add(message);
            }
        }else{//用户消息 + 临时会话消息
            List<MessageInfo> messageInfoList = rightMsgRepository.selectListLeftAndRight(messageInfo);
            for (MessageInfo msg : messageInfoList) {
                IMMessageHisQueryResponse.Body.Message message = new IMMessageHisQueryResponse.Body.Message();
                message.setMsgId(msg.getId());
                message.setUserId(msg.getUserId());
                message.setUserName(staffInfoCache.get(msg.getUserId()).getUserName());
                message.setDirection(msg.getDirection());
                message.setAction(userSession.getMsgType());//消息类型
                message.setIsRead(msg.getIsRead());//已读标志
                message.setTitle(msg.getTitle());
                message.setContent(msg.getContent());
                message.setReadType(msg.getReadType());
                message.setFormat(msg.getFormat());
                message.setMsgDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date(msg.getCreateTime())));
                message.setTimestamp(msg.getCreateTime());
                msgList.add(message);
            }
        }

        response.getBody().setMsgList(msgList);
        return response;
    }

}
