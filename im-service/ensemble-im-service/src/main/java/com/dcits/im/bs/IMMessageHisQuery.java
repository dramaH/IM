package com.dcits.im.bs;

import com.dcits.comet.flow.ExecutorFlow;
import com.dcits.comet.rpc.api.annotation.CometMapping;
import com.dcits.comet.rpc.provider.api.annotation.CometProvider;
import com.dcits.im.api.model.IMMessageHisQueryRequest;
import com.dcits.im.api.model.IMMessageHisQueryResponse;
import com.dcits.im.api.model.IMNoticeSendRequest;
import com.dcits.im.api.model.IMNoticeSendResponse;
import com.dcits.im.api.service.IIMMessageHisQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 历史消息查询
 */
@Slf4j
@CometProvider
public class IMMessageHisQuery implements IIMMessageHisQuery {

	@CometMapping(value = IM_MSG_HIS_QUERY, serviceCode = "IM", messageType = "MsgHis", messageCode = "QUERY", name = "历史消息查询")
	public IMMessageHisQueryResponse runService(@RequestBody IMMessageHisQueryRequest request) {
		return ExecutorFlow.startFlow("messageHisQueryFlow", request);
	}
}
