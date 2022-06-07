package com.dcits.im.bc.handler;

import com.dcits.im.constants.ChannelAttrKeys;
import com.dcits.im.constants.SessionConst;
import com.dcits.im.jpa.entity.ImRecCurrentUser;
import com.dcits.im.jpa.repository.ImRecCurrentUserRepository;
import com.farsunset.cim.sdk.server.constant.ChannelSysAttrKeys;
import com.farsunset.cim.sdk.server.group.BindChannelGroup;
import com.farsunset.cim.sdk.server.handler.IMRequestHandler;
import com.farsunset.cim.sdk.server.model.Transporter;
import io.netty.channel.Channel;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 连接断开时，更新用户相关状态
 */
@Component("client_closed")
public class ClientClosedHandler implements IMRequestHandler {

	@Resource
	private ImRecCurrentUserRepository currentUserRepository;

	@Resource
	private BindChannelGroup bindChannelGroup;

	/**
	 * 消息中心用户退出事件会回调此方法
	 * @param channel 渠道
     * @param data 传输数据
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
	public void process(Channel channel, Transporter data) {
		String userId = channel.attr(ChannelAttrKeys.USER_ID).get();
		if (userId == null){
			return;
		}
		String channelId = channel.attr(ChannelSysAttrKeys.ID).get();
		bindChannelGroup.remove(userId, channel);

		/*
		 * ios开启了apns也需要显示在线，因此不删记录
		 */
		if (Objects.equals(channel.attr(ChannelAttrKeys.CHANNEL).get(), SessionConst.ChannelEnum.CHANNEL_IOS.getCode())){
			ImRecCurrentUser currentUser =  new ImRecCurrentUser();
			currentUser.setNodeId(channelId);
			currentUser.setIsOnline(SessionConst.OnlineStateEnum.STATE_INACTIVE.getCode());
			currentUserRepository.update(currentUser);
		}

		ImRecCurrentUser currentUser =  new ImRecCurrentUser();
		currentUser.setNodeId(channelId);
		currentUserRepository.delete(currentUser);
		return;
	}

}
