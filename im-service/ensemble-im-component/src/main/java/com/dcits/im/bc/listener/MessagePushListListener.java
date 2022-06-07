package com.dcits.im.bc.listener;

import com.alibaba.fastjson.JSON;
import com.dcits.comet.commons.ThreadLocalManager;
import com.dcits.comet.commons.utils.BusiUtil;
import com.dcits.im.cache.UmStaffInfoCache;
import com.dcits.im.constants.ChannelAttrKeys;
import com.dcits.im.jpa.entity.ImRecSysMsg;
import com.dcits.im.jpa.entity.ImRecUserLeftMsg;
import com.dcits.im.jpa.entity.ImRecUserRightMsg;
import com.dcits.im.jpa.entity.ImRecUserSession;
import com.dcits.im.jpa.model.MessageInfo;
import com.farsunset.cim.sdk.server.constant.IMConstant;
import com.farsunset.cim.sdk.server.group.BindChannelGroup;
import com.farsunset.cim.sdk.server.model.MessagePushListBody;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;

/**
 * 客户长连接 消息发送
 */
@Slf4j
@Component
public class MessagePushListListener implements MessageListener {
	@Autowired
	private RedisTemplate<String, Serializable> redisTemplate;

	@Resource
	private UmStaffInfoCache userInfoCache;

	@Resource
	private BindChannelGroup bindChannelGroup;

	@Override
	public void onMessage(Message redisMessage, byte[] bytes) {

		ThreadLocalManager.put("tranTimestamp",System.currentTimeMillis());
		byte[] bodyBytes = redisMessage.getBody();
		ImRecUserSession userSession = JSON.parseObject(
				(String)redisTemplate.getValueSerializer().deserialize(bodyBytes), ImRecUserSession.class);
		MessagePushListBody pushListBody = new MessagePushListBody();
		pushListBody.setType(IMConstant.IMTranTypeEnum.DATA_TYPE_MESSAGE_PUSH.getType());
		pushListBody.setSessionId(userSession.getSessionId());
		pushListBody.setSessionName(userSession.getSessionName());
		pushListBody.setUserId(userSession.getUserId());
		pushListBody.setNoReadNum(userSession.getNoReadNum());
		pushListBody.setIsOnline(userSession.getIsOnline());
		pushListBody.setIsOpen(userSession.getIsOpen());
		pushListBody.setIsTop(userSession.getIsTop());
		pushListBody.setMsgNum(userSession.getMsgNum());
		pushListBody.setNoReadNum(userSession.getNoReadNum());
		for (MessageInfo messageInfo : userSession.getMessageInfoList()) {
			MessagePushListBody.MessagePushBody msgBody = new MessagePushListBody.MessagePushBody();
			msgBody.setReferenceId(userSession.getReferenceId());
			msgBody.setMsgId(messageInfo.getId());
			msgBody.setUserId(messageInfo.getFromUserId());
			msgBody.setUserName(messageInfo.getFromUserId()==null?messageInfo.getUserName():userInfoCache.get(messageInfo.getFromUserId()).getUserName());
			msgBody.setDirection(messageInfo.getDirection());
			msgBody.setAction(messageInfo.getAction());
			msgBody.setIsRead(messageInfo.getIsRead());
			msgBody.setContent(messageInfo.getContent());
			msgBody.setFormat(messageInfo.getFormat());
			msgBody.setMsgDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date(messageInfo.getCreateTime())));
			msgBody.setTimestamp(messageInfo.getCreateTime());
			pushListBody.getMsgList().add(msgBody);
		}
		pushListBody.getMsgList().sort(Comparator.comparing(MessagePushListBody.MessagePushBody::getTimestamp));
		//推送消息
		Collection<Channel> userChannels = bindChannelGroup.find(userSession.getUserId());
		userChannels.forEach(channel ->{
			String bindChannel = channel.attr(ChannelAttrKeys.CHANNEL).get();
			String sendChannel = userSession.getSendChannel();
			if(!BusiUtil.isEquals(bindChannel, sendChannel)){
				channel.writeAndFlush(pushListBody);
			}
		});
	}
}
