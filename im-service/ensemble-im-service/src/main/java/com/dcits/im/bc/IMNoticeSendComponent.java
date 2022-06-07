package com.dcits.im.bc;

import com.dcits.comet.commons.utils.BusiUtil;
import com.dcits.comet.commons.utils.StringUtil;
import com.dcits.comet.rpc.api.model.head.AppHead;
import com.dcits.im.api.model.IMNoticeSendRequest;
import com.dcits.im.bc.redis.SignalRedisTemplate;
import com.dcits.im.bc.session.SessionComp;
import com.dcits.im.cache.UmStaffInfoCache;
import com.dcits.im.constants.MsgEnum;
import com.dcits.im.jpa.entity.ImRecSysMsg;
import com.dcits.im.jpa.entity.ImRecUserSession;
import com.dcits.im.jpa.entity.UmStaffInfo;
import com.dcits.im.jpa.model.MessageInfo;
import com.dcits.im.jpa.repository.ImRecSysMsgRepository;
import com.dcits.im.jpa.repository.UmStaffInfoRepository;
import com.dcits.im.model.NoticeEnum;
import com.dcits.im.util.SnowFlake;
import com.dcits.im.util.UUIDUtil;
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
public class IMNoticeSendComponent {
    @Resource
    private SignalRedisTemplate signalRedisTemplate;
    @Resource
    private ImRecSysMsgRepository sysMsgRepository;
    @Resource
    private UmStaffInfoRepository staffInfoRepository;
    @Resource
    private SessionComp sessionComp;

    /**
     * 通知消息发送
     * @param request
     */
    public void send(AppHead appHead, IMNoticeSendRequest request){
        Integer noticeType = request.getBody().getNoticeType();
        List<IMNoticeSendRequest.Body.ToArray> toArrayList = request.getBody().getToArray();
        List<String> acceptIdList = new ArrayList<>();
        if(BusiUtil.isNotNull(toArrayList)){
            acceptIdList = toArrayList.stream().map(IMNoticeSendRequest.Body.ToArray::getAcceptId).collect(Collectors.toList());
        }
        String title = request.getBody().getTitle();
        Integer readType = request.getBody().getReadType();
        String content = request.getBody().getContent();
        Integer format = request.getBody().getFormat();

        List<String> noticeUserIdList = getNoticeUserIdList(appHead, noticeType, acceptIdList);
        if(BusiUtil.isNull(noticeUserIdList)){
            log.error("未找到消息接收人！");
            return;
        }
        String systemIds = StringUtil.join(acceptIdList, ",");
        for(String toUserId : noticeUserIdList){
            ImRecUserSession noticeSession = sessionComp.getNoticeSession(toUserId, UmStaffInfoCache.USER_SYS);
            String msgId = String.valueOf(SnowFlake.nextId());
            {//接收系统消息
                ImRecSysMsg sysMsg = new ImRecSysMsg();
                sysMsg.setId(msgId);
                sysMsg.setSessionId(noticeSession.getSessionId());
                sysMsg.setSystemIds(systemIds);
                sysMsg.setToUserId(toUserId);
                sysMsg.setReadType(readType);
                sysMsg.setTitle(title);
                sysMsg.setContent(content);
                sysMsg.setFormat(format);
                sysMsg.setIsRead(MsgEnum.IS_READ_NO.getCode());//未读
                /*
                 * 存储到数据库
                 */
                long timestamp = System.currentTimeMillis();
                sysMsgRepository.insert(timestamp, sysMsg);

                MessageInfo messageInfo = new MessageInfo();
                messageInfo.setSessionId(noticeSession.getSessionId());
                messageInfo.setId(msgId);
                messageInfo.setUserName(MsgEnum.MSG_TYPE_SYS.getName());
                messageInfo.setDirection(MsgEnum.DIRECT_LEFT.getCode());//左
                messageInfo.setAction(MsgEnum.MSG_TYPE_SYS.getCode());//系统消息
                messageInfo.setIsRead(MsgEnum.IS_READ_NO.getCode());//未读取
                messageInfo.setTitle(title);
                messageInfo.setContent(content);
                messageInfo.setReadType(readType);
                messageInfo.setFormat(MsgEnum.FORMAT_TEXT.getCode());//文字
                messageInfo.setCreateTime(timestamp);
                noticeSession.getMessageInfoList().add(messageInfo);
            }

            /*
             * 发送上线事件到集群中的其他实例
             */
            signalRedisTemplate.push(noticeSession);
        }
        return;
    }

    /**
     * 通过发送类型获取被通知人USER_ID
     * @param appHead
     * @param noticeType
     * @param acceptIdList
     * @return
     */
    public List<String> getNoticeUserIdList(AppHead appHead, Integer noticeType, List<String> acceptIdList){
        Map param = new HashMap<>();
        if(NoticeEnum.NOTICE_TYPE_USER.getCode().equals(noticeType)){
            param.put("userIds", acceptIdList);
        }else if(NoticeEnum.NOTICE_TYPE_ROLE.getCode().equals(noticeType)){
            param.put("roleIds", acceptIdList);
        }
        List<UmStaffInfo> staffInfoList = staffInfoRepository.selectListByUserRole(appHead, param);
        if(staffInfoList!=null){
            return staffInfoList.stream().map(UmStaffInfo::getUserId).collect(Collectors.toList());
        }
        return null;
    }
}
