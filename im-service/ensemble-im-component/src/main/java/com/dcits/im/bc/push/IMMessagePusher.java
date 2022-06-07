package com.dcits.im.bc.push;

import com.farsunset.cim.sdk.server.model.Transporter;

/**
 * 消息发送实接口
 */
public interface IMMessagePusher {

	/**
	 * 向用户发送消息
	 * @param toUserId 接收用户
	 * @param transporter 消息体
	 */
	void push(String toUserId, Transporter transporter);

}
