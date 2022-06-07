package com.dcits.im.bf;

import com.dcits.comet.flow.AbstractFlow;
import com.dcits.im.api.model.IMMessageHisQueryRequest;
import com.dcits.im.api.model.IMMessageHisQueryResponse;
import com.dcits.im.bc.IMMessageHisQueryComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class MessageHisQueryFlow extends AbstractFlow<IMMessageHisQueryRequest, IMMessageHisQueryResponse> {

    @Resource
    private IMMessageHisQueryComponent messageHisQueryComponent;

    @Override
    public IMMessageHisQueryResponse execute(IMMessageHisQueryRequest request) {
        return messageHisQueryComponent.doQuery(request);
    }
}
