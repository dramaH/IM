package com.dcits.im.bc.handler;

import com.dcits.comet.commons.utils.BusiUtil;
import com.dcits.im.bc.redis.SignalRedisTemplate;
import com.dcits.im.bc.session.SessionComp;
import com.dcits.im.cache.UmStaffInfoCache;
import com.dcits.im.constants.ChannelAttrKeys;
import com.dcits.im.constants.MsgEnum;
import com.dcits.im.jpa.entity.ImRecUserLeftMsg;
import com.dcits.im.jpa.entity.ImRecUserRightMsg;
import com.dcits.im.jpa.entity.ImRecUserSession;
import com.dcits.im.jpa.model.MessageInfo;
import com.dcits.im.jpa.repository.ImRecUserLeftMsgRepository;
import com.dcits.im.jpa.repository.ImRecUserRightMsgRepository;
import com.dcits.im.jpa.repository.ImRecUserSessionRepository;
import com.dcits.im.util.SnowFlake;
import com.dcits.im.util.UUIDUtil;
import com.farsunset.cim.sdk.server.handler.IMRequestHandler;
import com.farsunset.cim.sdk.server.model.MessageSentBody;
import com.farsunset.cim.sdk.server.model.ReplyBody;
import com.farsunset.cim.sdk.server.model.Transporter;
import io.netty.channel.Channel;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 客户长连接 消息发送
 */
@Component("msg_send")
public class MsgSendHandler implements IMRequestHandler {

	@Resource
	private UmStaffInfoCache staffInfoCache;

	@Resource
	private SessionComp sessionComp;

	@Resource
	private ImRecUserSessionRepository userSessionRepository;

	@Resource
	private ImRecUserLeftMsgRepository userLeftMsgRepository;

	@Resource
	private ImRecUserRightMsgRepository userRightMsgRepository;

	@Resource
	private SignalRedisTemplate signalRedisTemplate;

	/**
	 * 消息中心消息发送事件会回调此方法
	 * @param channel 渠道
	 * @param data 传输数据
	 */
	@SneakyThrows
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
	public void process(Channel channel, Transporter data) {
		ReplyBody reply = new ReplyBody();
		reply.setType(data.getType());
		MessageSentBody body = (MessageSentBody) data;
		reply.setOriginMessageSentBody(body);
		String userId = channel.attr(ChannelAttrKeys.USER_ID).get();
		String toUserId = body.getToUserId();
		String referenceId = body.getReferenceId();
		String content = body.getContent();

		if(BusiUtil.isNull(userId) || BusiUtil.isNull(toUserId)){
			reply.setTimestamp(System.currentTimeMillis());
			reply.setCode(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS.value());
			reply.setMessage("本方用户ID和对方用户ID都不能为空！");
			channel.writeAndFlush(reply);
			return;
		}
		if(BusiUtil.isNull(staffInfoCache.get(toUserId))){
			reply.setTimestamp(System.currentTimeMillis());
			reply.setCode(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS.value());
			reply.setMessage("对方用户不存在！");
			channel.writeAndFlush(reply);
			return;
		}
		if(BusiUtil.isNull(content)){
			reply.setTimestamp(System.currentTimeMillis());
			reply.setCode(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS.value());
			reply.setMessage("消息内容不能为空！");
			channel.writeAndFlush(reply);
			return;
		}

		//本方会话
		ImRecUserSession thisUserSession = sessionComp.getSessionOnly(userId, toUserId);
		thisUserSession.setSendChannel(channel.attr(ChannelAttrKeys.CHANNEL).get());
		thisUserSession.setReferenceId(referenceId);
		//对方会话
		ImRecUserSession oppUserSession = sessionComp.getSessionAndAddCount(toUserId, userId);

		if(body.getAction()==3){
			//开启会话
			if(BusiUtil.isNotNull(thisUserSession)&& thisUserSession.getIsOpen().equals(MsgEnum.IS_OPEN_NO.getCode())){
				thisUserSession.setIsOpen(MsgEnum.IS_OPEN_YES.getCode());
				userSessionRepository.update(thisUserSession);
			}
			if(BusiUtil.isNotNull(oppUserSession)&& oppUserSession.getIsOpen().equals(MsgEnum.IS_OPEN_NO.getCode())){
				oppUserSession.setIsOpen(MsgEnum.IS_OPEN_YES.getCode());
				userSessionRepository.update(oppUserSession);
			}
		}else if(body.getAction().equals(MsgEnum.ACTION_CLOSE_SESSION.getCode())){
			//关闭会话
			if(BusiUtil.isNotNull(thisUserSession)&&thisUserSession.getIsOpen()==0){
				thisUserSession.setIsOpen(MsgEnum.IS_OPEN_NO.getCode());
				userSessionRepository.update(thisUserSession);
			}
			if(BusiUtil.isNotNull(oppUserSession)&&oppUserSession.getIsOpen()==0){
				oppUserSession.setIsOpen(MsgEnum.IS_OPEN_NO.getCode());
				userSessionRepository.update(oppUserSession);
			}
			reply.setTimestamp(System.currentTimeMillis());
			reply.setCode(HttpStatus.OK.value());
			reply.setMessage("关闭会话成功！");
			channel.writeAndFlush(reply);
			return;
		}


		String msgId = String.valueOf(SnowFlake.nextId());
		{//发送人R消息
			ImRecUserRightMsg userRightMsg = new ImRecUserRightMsg();
			userRightMsg.setId(msgId);
			userRightMsg.setSessionId(thisUserSession.getSessionId());
			userRightMsg.setUserId(userId);
			userRightMsg.setFromUserId(userId);
			userRightMsg.setToUserId(toUserId);
			userRightMsg.setContent(body.getContent());
			userRightMsg.setFormat(body.getFormat());
			/*
			 * 存储到数据库
			 */
			long timestamp = System.currentTimeMillis();
			userRightMsgRepository.insert(timestamp, userRightMsg);

			MessageInfo messageInfo = new MessageInfo();
			messageInfo.setSessionId(thisUserSession.getSessionId());
			messageInfo.setId(msgId);
			messageInfo.setFromUserId(userId);
			messageInfo.setDirection(MsgEnum.DIRECT_RIGHT.getCode());//右
			messageInfo.setAction(MsgEnum.ACTION_USER_MSG.getCode());//用户消息
			messageInfo.setIsRead(MsgEnum.IS_READ_YES.getCode());//已读
			messageInfo.setContent(body.getContent());
			messageInfo.setReadType(MsgEnum.READ_TYPE_NOT_FORCE.getCode());//非强制阅读
			messageInfo.setFormat(body.getFormat());
			messageInfo.setCreateTime(timestamp);
			thisUserSession.getMessageInfoList().add(messageInfo);
		}


		{//接收人L消息
			ImRecUserLeftMsg userLeftMsg = new ImRecUserLeftMsg();
			userLeftMsg.setId(msgId);
			userLeftMsg.setSessionId(oppUserSession.getSessionId());
			userLeftMsg.setUserId(toUserId);
			userLeftMsg.setFromUserId(userId);
			userLeftMsg.setToUserId(toUserId);
			userLeftMsg.setContent(body.getContent());
			userLeftMsg.setFormat(body.getFormat());
			userLeftMsg.setIsRead(MsgEnum.IS_READ_NO.getCode());
			/*
			 * 存储到数据库
			 */
			long timestamp = System.currentTimeMillis();
			userLeftMsgRepository.insert(timestamp, userLeftMsg);

			MessageInfo messageInfo = new MessageInfo();
			messageInfo.setSessionId(oppUserSession.getSessionId());
			messageInfo.setId(msgId);
			messageInfo.setFromUserId(userId);
			messageInfo.setDirection(MsgEnum.DIRECT_LEFT.getCode());//左
			messageInfo.setAction(MsgEnum.ACTION_USER_MSG.getCode());//用户消息
			messageInfo.setIsRead(MsgEnum.IS_READ_NO.getCode());//未读取
			messageInfo.setContent(body.getContent());
			messageInfo.setReadType(MsgEnum.READ_TYPE_NOT_FORCE.getCode());//非强制阅读
			messageInfo.setFormat(body.getFormat());
			messageInfo.setCreateTime(timestamp);
			oppUserSession.getMessageInfoList().add(messageInfo);
		}

		{//发送消息成功
			reply.setTimestamp(System.currentTimeMillis());
			reply.setCode(HttpStatus.OK.value());
			reply.setMessage("发送成功！");
			channel.writeAndFlush(reply);
		}

		/*
		 * 发送上线事件到集群中的其他实例
		 */
		signalRedisTemplate.push(thisUserSession);
		signalRedisTemplate.push(oppUserSession);
	}
}
