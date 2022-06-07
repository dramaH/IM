package com.dcits.im.bf;

import com.dcits.comet.commons.Context;
import com.dcits.comet.commons.ThreadLocalManager;
import com.dcits.comet.flow.AbstractFlow;
import com.dcits.comet.rpc.api.model.head.AppHead;
import com.dcits.im.api.model.IMNoticeSendRequest;
import com.dcits.im.api.model.IMNoticeSendResponse;
import com.dcits.im.api.model.IMOnceSendRequest;
import com.dcits.im.api.model.IMOnceSendResponse;
import com.dcits.im.bc.IMNoticeSendComponent;
import com.dcits.im.bc.IMOnceSendComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class OnceSendFlow extends AbstractFlow<IMOnceSendRequest, IMOnceSendResponse> {

    @Resource
    private IMOnceSendComponent onceSendComponent;

    @Override
    public IMOnceSendResponse execute(IMOnceSendRequest request) {
        ThreadLocalManager.put("tranTimestamp",System.currentTimeMillis() + 600000L);
        IMOnceSendResponse response = new IMOnceSendResponse();
        onceSendComponent.send(request);
        return response;
    }
}
