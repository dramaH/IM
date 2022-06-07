package com.dcits.im.bs;

import com.dcits.comet.flow.ExecutorFlow;
import com.dcits.comet.rpc.api.annotation.CometMapping;
import com.dcits.comet.rpc.provider.api.annotation.CometProvider;
import com.dcits.im.api.model.IMContactUserRequest;
import com.dcits.im.api.model.IMContactUserResponse;
import com.dcits.im.api.service.IIMContactUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 联系人查询
 */
@Slf4j
@CometProvider
public class IMContractUser implements IIMContactUser {

	@CometMapping(value=IM_CONTACT_QUERY,serviceCode = "IM", messageType = "Contact", messageCode = "User", name = "联系人查询")
	public IMContactUserResponse runService(@RequestBody IMContactUserRequest request) {
        return ExecutorFlow.startFlow("contractUserFlow", request);
	}

}
