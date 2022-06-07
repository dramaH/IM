package com.dcits.im.bs;

import com.dcits.comet.flow.ExecutorFlow;
import com.dcits.comet.rpc.api.annotation.CometMapping;
import com.dcits.comet.rpc.provider.api.annotation.CometProvider;
import com.dcits.im.api.model.IMNoticeSendRequest;
import com.dcits.im.api.model.IMNoticeSendResponse;
import com.dcits.im.api.model.IMOnceSendRequest;
import com.dcits.im.api.model.IMOnceSendResponse;
import com.dcits.im.api.service.IIMNoticeSend;
import com.dcits.im.api.service.IIMOnceSend;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 一次性发送消息
 */
@Slf4j
@CometProvider
public class IMOnceSend implements IIMOnceSend {

	@CometMapping(value = IM_ONCE_SESSION_SEND, serviceCode = "IM", messageType = "OnceSession", messageCode = "QUERY", name = "一次性发送消息")
	public IMOnceSendResponse runService(@RequestBody IMOnceSendRequest request) {
		return ExecutorFlow.startFlow("onceSendFlow", request);
	}
}
