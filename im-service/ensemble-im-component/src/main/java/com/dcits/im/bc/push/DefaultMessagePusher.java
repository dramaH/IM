package com.dcits.im.bc.push;

import com.alibaba.fastjson.JSON;
import com.dcits.im.bc.apns.ApnsComponent;
import com.dcits.im.bc.redis.KeyValueRedisTemplate;
import com.dcits.im.bc.redis.SignalRedisTemplate;
import com.farsunset.cim.sdk.server.model.MessagePushListBody;
import com.farsunset.cim.sdk.server.model.Transporter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/*
 * 消息发送实现类
 * 
 */
@Component
public class DefaultMessagePusher implements IMMessagePusher {

	@Resource
	private ApnsComponent apnsComponent;

	@Resource
	private SignalRedisTemplate signalRedisTemplate;

	@Resource
	private KeyValueRedisTemplate keyValueRedisTemplate;

	/**
	 * 向用户发送消息
	 * @param toUserId 接收用户ID
	 * @param transporter 发送消息组信息
	 */
	public final void push(String toUserId, Transporter transporter) {
		/*
		 * 说明iOS客户端开启了apns
		 */
		String deviceToken = keyValueRedisTemplate.getDeviceToken(toUserId);
		if(deviceToken != null) {
			//apnsComponent.push(transporter, deviceToken);
			return;
		}

		/*
		 * 通过发送redis广播，到集群中的每台实例，获得当前UID绑定了连接并推送
		 */
		//signalRedisTemplate.push(transporter);
	}
	public static void main(String[] args) {
		//测试使用
		//MessagePushListBody body = new MessagePushListBody();
		//body.setSessionId("111");
		//body.setIsOnline(1);
		//body.setIsOpen(1);
		//body.setUserId("user123");
		//String json = JSON.toJSONString(body);
		//System.out.println(json);
	}

}
