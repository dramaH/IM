package com.dcits.im.bf;

import com.dcits.comet.commons.Context;
import com.dcits.comet.commons.ThreadLocalManager;
import com.dcits.comet.flow.AbstractFlow;
import com.dcits.comet.rpc.api.model.head.AppHead;
import com.dcits.im.api.model.IMNoticeSendRequest;
import com.dcits.im.api.model.IMNoticeSendResponse;
import com.dcits.im.bc.IMNoticeSendComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

@Slf4j
@Component
public class NoticeSendFlow extends AbstractFlow<IMNoticeSendRequest, IMNoticeSendResponse> {

    @Resource
    private IMNoticeSendComponent noticeSendComponent;

    @Override
    public IMNoticeSendResponse execute(IMNoticeSendRequest request) {
        ThreadLocalManager.put("tranTimestamp",System.currentTimeMillis() + 600000L);
        IMNoticeSendResponse response = new IMNoticeSendResponse();
        AppHead appHead = Context.getInstance().getAppHead();
        appHead.setPgupOrPgdn("1");
        appHead.setTotalNum("2000");
        appHead.setCurrentNum("0");
        appHead.setTotalFlag("E");
        doExecute(appHead, request);
        return response;
    }

    private void doExecute(AppHead appHead, IMNoticeSendRequest request){
        noticeSendComponent.send(appHead, request);
        //分页递归查询
        if(Integer.parseInt(appHead.getCurrentNum())+Integer.parseInt(appHead.getTotalNum())<Integer.parseInt(appHead.getTotalRows())){
            appHead.setCurrentNum(String.valueOf(Integer.parseInt(appHead.getCurrentNum())+Integer.parseInt(appHead.getTotalNum())));
            doExecute(appHead, request);
        }
    }
}
