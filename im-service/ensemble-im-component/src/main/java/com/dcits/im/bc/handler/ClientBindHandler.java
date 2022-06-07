package com.dcits.im.bc.handler;

import com.dcits.comet.IContext;
import com.dcits.comet.commons.Context;
import com.dcits.comet.commons.utils.BusiUtil;
import com.dcits.im.bc.contact.ContactComp;
import com.dcits.im.bc.redis.SignalRedisTemplate;
import com.dcits.im.cache.UmStaffInfoCache;
import com.dcits.im.constants.ChannelAttrKeys;
import com.dcits.im.jpa.entity.*;
import com.dcits.im.jpa.model.ContactInfo;
import com.dcits.im.jpa.model.MessageInfo;
import com.dcits.im.jpa.repository.*;
import com.dcits.im.util.AppHeadUtil;
import com.dcits.im.util.UUIDUtil;
import com.farsunset.cim.sdk.server.group.BindChannelGroup;
import com.farsunset.cim.sdk.server.handler.IMRequestHandler;
import com.farsunset.cim.sdk.server.model.BindSentBody;
import com.farsunset.cim.sdk.server.model.ReplyBody;
import com.farsunset.cim.sdk.server.model.Transporter;
import io.netty.channel.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 客户长连接 账户绑定实现
 */
@Slf4j
@Component("client_bind")
public class ClientBindHandler implements IMRequestHandler {

	private final SimpleDateFormat dateFmt = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

	@Resource
	private UmStaffInfoCache userInfoCache;

	@Resource
	private ImRecCurrentUserRepository currentUserRepository;

	@Resource
	private ImRecHistLoginRepository histLoginRepository;

	@Resource
	private BindChannelGroup bindChannelGroup;

	@Resource
	private SignalRedisTemplate signalRedisTemplate;

	@Resource
	private ImRecUserSessionRepository userSessionRepository;

	@Resource
	private ImRecSysMsgRepository sysMsgRepository;

	@Resource
	private ImRecUserLeftMsgRepository userLeftMsgRepository;

	@Resource
	private ImRecUserRightMsgRepository userRightMsgRepository;

	@Resource
	private ContactComp contactComp;

	/**
	 * 消息中心用户登录绑定事件会回调此方法
	 * @param channel 渠道
	 * @param data 传输数据
	 */
	@SneakyThrows
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = Exception.class)
	public void process(Channel channel, Transporter data) {
		BindSentBody body = (BindSentBody) data;

		/**
		 * 初始化响应对象
		 */
		ReplyBody reply = new ReplyBody();
		reply.setType(data.getType());

		if(BusiUtil.isNull(body.getUserId())
				||userInfoCache.get(body.getUserId())==null
				||BusiUtil.isNull(body.getChannel())
				||BusiUtil.isNull(body.getUserToken())){
			reply.setTimestamp(System.currentTimeMillis());
			reply.setCode(HttpStatus.NON_AUTHORITATIVE_INFORMATION.value());
			reply.setMessage("用户登录失败！");
			channel.writeAndFlush(reply);
			return;
		}else{
			reply.setCode(HttpStatus.OK.value());
			reply.setMessage("登录成功！");
		}

		/*
		 * 添加到内存管理
		 */
		ImRecCurrentUser currentUser = new ImRecCurrentUser();
		currentUser.setUserId(body.getUserId());
		currentUser.setChannel(body.getChannel());
		List<ImRecCurrentUser> currentUserList = currentUserRepository.selectList(AppHeadUtil.getAppHeadNoPage(), currentUser);
		if(BusiUtil.isNotNull(currentUserList)){
			reply.setTimestamp(System.currentTimeMillis());
			reply.setCode(HttpStatus.NON_AUTHORITATIVE_INFORMATION.value());
			reply.setMessage("用户["+body.getUserId()+"]已登录，不能重复登录！");
			channel.writeAndFlush(reply);
			return;
		}
		bindChannelGroup.add(body.getUserId(), channel);

		/*
		 * 向客户端发送bind响应
		 */
		channel.writeAndFlush(reply);
		log.debug("[{}]登录成功，发送响应给客户端！", body.getUserId());

		Date bindTime = new Date();

		/*
		 * 插入当前用户信息表
		 */
		currentUser = new ImRecCurrentUser();
		currentUser.setId(UUIDUtil.getUUID());
		currentUser.setUserId(body.getUserId());
		currentUser.setNodeId(channel.id().asShortText());
		currentUser.setDeviceId(body.getDeviceId());
		currentUser.setDeviceName(body.getDeviceName());
		currentUser.setHost(InetAddress.getLocalHost().getHostAddress());
		currentUser.setChannel(body.getChannel());
		currentUser.setAppVersion(body.getAppVersion());
		currentUser.setOsVersion(body.getOsVersion());
		currentUser.setLanguage(body.getLanguage());
		currentUser.setLongitude(body.getLongitude());
		currentUser.setLatitude(body.getLatitude());
		currentUser.setLocation(body.getLocation());
		currentUser.setBindTime(dateFmt.format(bindTime));
		currentUser.setIsOnline(0);
		currentUserRepository.insert(currentUser);

		/*
		 * 管道绑定用户信息
		 */
		channel.attr(ChannelAttrKeys.USER_ID).set(currentUser.getUserId());
		channel.attr(ChannelAttrKeys.CHANNEL).set(currentUser.getChannel());
		channel.attr(ChannelAttrKeys.DEVICE_ID).set(currentUser.getDeviceId());
		channel.attr(ChannelAttrKeys.LANGUAGE).set(currentUser.getLanguage());

		/*
		 * 插入用户历史登录信息表
		 */
		ImRecHistLogin histLogin = new ImRecHistLogin();
		histLogin.setId(UUIDUtil.getUUID());
		histLogin.setUserId(body.getUserId());
		histLogin.setNodeId(channel.id().asShortText());
		histLogin.setDeviceId(body.getDeviceId());
		histLogin.setDeviceName(body.getDeviceName());
		histLogin.setHost(InetAddress.getLocalHost().getHostAddress());
		histLogin.setChannel(body.getChannel());
		histLogin.setAppVersion(body.getAppVersion());
		histLogin.setOsVersion(body.getOsVersion());
		histLogin.setLanguage(body.getLanguage());
		histLogin.setLongitude(body.getLongitude());
		histLogin.setLatitude(body.getLatitude());
		histLogin.setLocation(body.getLocation());
		histLogin.setBindTime(dateFmt.format(bindTime));
		histLoginRepository.insert(histLogin);

		/*
		 * 发送上线事件到集群中的其他实例，控制其他设备下线
		 */
		signalRedisTemplate.bind(currentUser);

		/*
		 * 推送会话列表
		 */
		ImRecUserSession userSession = new ImRecUserSession();
		userSession.setUserId(currentUser.getUserId());
		List<ImRecUserSession> userSessionList = userSessionRepository.selectList(AppHeadUtil.getAppHeadNoPage(), userSession);

		List<ContactInfo> contactInfoList = contactComp.query(Context.getInstance().getAppHead(), currentUser.getUserId());
		Map<String, ContactInfo> contactInfoMap = contactInfoList.stream().collect(Collectors.toMap(ContactInfo::getUserId, ContactInfo::get));

		if(BusiUtil.isNotNull(userSessionList)){
			for (ImRecUserSession session : userSessionList) {
				ImRecUserLeftMsg leftMsg = new ImRecUserLeftMsg();
				leftMsg.setSessionId(session.getSessionId());
				MessageInfo messageInfo = new MessageInfo();
				messageInfo.setSessionId(session.getSessionId());
				messageInfo.setRows(10);

				if(session.getMsgType()==1){//系统消息
					List<MessageInfo> messageInfoList = sysMsgRepository.selectMessageList(messageInfo);
					session.getMessageInfoList().addAll(messageInfoList);
				}else{//用户消息 + 临时会话消息
					List<MessageInfo> messageInfoList = userRightMsgRepository.selectListLeftAndRight(messageInfo);
					session.getMessageInfoList().addAll(messageInfoList);
				}

				//如果接收人用户ID是用户的联系人，则用户到接收人的会话设置成开启状态
				String[] userIds = session.getSessionId().split("#_#");
				if(userIds!=null && userIds.length==2){
					ContactInfo contactInfo = contactInfoMap.get(userIds[1]);
					if(BusiUtil.isNotNull(contactInfo)){
						session.setIsOpen(1);
						userSessionRepository.update(session);
					}
				}
				signalRedisTemplate.push(session);
			}
		}else{
			log.debug("[{}]没有待推送的会话信息！", currentUser.getUserId());
		}
	}
}
