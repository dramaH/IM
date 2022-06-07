package com.dcits.im.bs;

import com.dcits.comet.commons.ThreadLocalManager;
import com.dcits.comet.flow.ExecutorFlow;
import com.dcits.comet.rpc.api.annotation.CometMapping;
import com.dcits.comet.rpc.api.model.head.AppHead;
import com.dcits.comet.rpc.provider.api.annotation.CometProvider;
import com.dcits.im.api.model.IMNoticeSendRequest;
import com.dcits.im.api.model.IMNoticeSendResponse;
import com.dcits.im.api.service.IIMNoticeSend;
import com.dcits.im.bc.IMNoticeSendComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;

/**
 * 通知消息发送
 */
@Slf4j
@CometProvider
public class IMNoticeSend implements IIMNoticeSend {

	@CometMapping(value = IM_NOTICE_SEND, serviceCode = "IM", messageType = "Notice", messageCode = "Send", name = "通知消息发送")
	public IMNoticeSendResponse runService(@RequestBody IMNoticeSendRequest request) {
		return ExecutorFlow.startFlow("noticeSendFlow", request);
	}
}
