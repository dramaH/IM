package com.dcits.im.bc.redis;

import com.alibaba.fastjson.JSON;
import com.dcits.im.constants.MessageConstants;
import com.dcits.im.jpa.entity.ImRecCurrentUser;
import com.dcits.im.jpa.entity.ImRecUserSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;

@Slf4j
@Component
public class SignalRedisTemplate {
	@Resource
	private RedisTemplate<String, Serializable> redisTemplate;

	/**
	 * 消息发送到绑定消息队列
	 * @param currentUser 消息体
	 */
	public void bind(ImRecCurrentUser currentUser) {
		redisTemplate.convertAndSend(MessageConstants.BIND_NOTICE_INNER_QUEUE, JSON.toJSONString(currentUser));
	}

	/**
	 * 消息发送到推送消息队列
	 * @param userSession 消息体
	 */
	public void push(ImRecUserSession userSession) {
		redisTemplate.convertAndSend(MessageConstants.MESSAGE_PUSH_LIST_INNER_QUEUE, JSON.toJSONString(userSession));
	}
}
