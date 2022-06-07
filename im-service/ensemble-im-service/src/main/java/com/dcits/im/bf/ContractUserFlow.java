package com.dcits.im.bf;

import com.dcits.comet.IContext;
import com.dcits.comet.flow.AbstractFlow;
import com.dcits.im.api.model.IMContactUserRequest;
import com.dcits.im.api.model.IMContactUserResponse;
import com.dcits.im.bc.IMContractUserComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class ContractUserFlow extends AbstractFlow<IMContactUserRequest, IMContactUserResponse> {
    private final static String MAX_CONTACT_ROW="5000";//联系人单次最大查询条数

    @Resource
    IMContractUserComponent contractUserComponent;

    @Override
    public IMContactUserResponse execute(IMContactUserRequest request) {
        if("-1".equals(IContext.getInstance().getAppHead().getTotalNum())){
            IContext.getInstance().getAppHead().setTotalNum(MAX_CONTACT_ROW);
        }
        return contractUserComponent.doQuery(request);
    }
}
