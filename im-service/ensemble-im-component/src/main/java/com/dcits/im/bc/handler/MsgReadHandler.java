package com.dcits.im.bc.handler;

import com.dcits.comet.commons.utils.BusiUtil;
import com.dcits.comet.rpc.api.model.head.AppHead;
import com.dcits.im.bc.redis.SignalRedisTemplate;
import com.dcits.im.constants.MsgEnum;
import com.dcits.im.jpa.entity.ImRecSysMsg;
import com.dcits.im.jpa.entity.ImRecUserLeftMsg;
import com.dcits.im.jpa.entity.ImRecUserSession;
import com.dcits.im.jpa.repository.ImRecSysMsgRepository;
import com.dcits.im.jpa.repository.ImRecUserLeftMsgRepository;
import com.dcits.im.jpa.repository.ImRecUserSessionRepository;
import com.farsunset.cim.sdk.server.handler.IMRequestHandler;
import com.farsunset.cim.sdk.server.model.MessageReadBody;
import com.farsunset.cim.sdk.server.model.Transporter;
import io.netty.channel.Channel;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户长连接 消息已读
 */
@Component("msg_read")
public class MsgReadHandler implements IMRequestHandler {
	@Resource
	private ImRecUserLeftMsgRepository leftMsgRepository;
	@Resource
	private ImRecSysMsgRepository sysMsgRepository;
	@Resource
	private ImRecUserSessionRepository sessionRepository;
	@Resource
	private SignalRedisTemplate signalRedisTemplate;

	/**
	 * 消息中心消息已读事件会回调此方法
	 * @param channel 渠道
	 * @param data 传输数据
	 */
	@SneakyThrows
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
	public void process(Channel channel, Transporter data) {
		MessageReadBody readBody = (MessageReadBody) data;

		//更新消息数
		int count=0;

		/**
		 * 读取消息数量
		 */
		if(BusiUtil.isNotNull(readBody.getSessionId()) && BusiUtil.isNotNull(readBody.getTimestamp())){

			/**
			 * 更新用户消息左表消息已读状态
			 */
			Map leftMsg = new HashMap();
			leftMsg.put("sessionId",readBody.getSessionId());
			leftMsg.put("createTime",readBody.getTimestamp());
			List<ImRecUserLeftMsg> leftMsgList = leftMsgRepository.selectListForRead(new AppHead(), leftMsg);
			for (ImRecUserLeftMsg userLeftMsg : leftMsgList) {
				if(userLeftMsg.getIsRead()==1){
					count++;
					userLeftMsg.setIsRead(MsgEnum.IS_READ_YES.getCode());//已读
					leftMsgRepository.update(userLeftMsg);
				}
				userLeftMsg.setIsRead(MsgEnum.IS_READ_YES.getCode());//已读
				leftMsgRepository.update(userLeftMsg);
			}

			/**
			 * 更新系统消息表消息已读状态
			 */
			Map sysMsg = new HashMap();
			sysMsg.put("sessionId",readBody.getSessionId());
			sysMsg.put("createTime",readBody.getTimestamp());
			List<ImRecSysMsg> sysMsgList = sysMsgRepository.selectListForRead(new AppHead(), sysMsg);
			for (ImRecSysMsg recSysMsg : sysMsgList) {
				if(recSysMsg.getIsRead()==MsgEnum.IS_READ_NO.getCode()){
					count++;
					recSysMsg.setIsRead(MsgEnum.IS_READ_YES.getCode());//已读
					sysMsgRepository.update(recSysMsg);
				}
			}
		}



		/**
		 * 更新会话未读消息数
		 */
		ImRecUserSession session = new ImRecUserSession();
		session.setSessionId(readBody.getSessionId());
		ImRecUserSession userSession = sessionRepository.selectOne(session);
		userSession.setNoReadNum(userSession.getNoReadNum()-count);
		sessionRepository.update(userSession);

		/*
		 * 发送上线事件到集群中的其他实例
		 */
		signalRedisTemplate.push(userSession);
	}
}
