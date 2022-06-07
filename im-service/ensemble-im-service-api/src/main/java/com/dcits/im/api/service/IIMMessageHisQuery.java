package com.dcits.im.api.service;

import com.dcits.im.api.model.IMMessageHisQueryRequest;
import com.dcits.im.api.model.IMMessageHisQueryResponse;

public interface IIMMessageHisQuery {
    String IM_MSG_HIS_QUERY = "/im/msgHis/query";

    IMMessageHisQueryResponse runService(IMMessageHisQueryRequest request);
}
