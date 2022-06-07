package com.dcits.im.api.service;

import com.dcits.im.api.model.IMNoticeSendRequest;
import com.dcits.im.api.model.IMNoticeSendResponse;

public interface IIMNoticeSend {
    String IM_NOTICE_SEND = "/im/notice/send";

    IMNoticeSendResponse runService(IMNoticeSendRequest request);
}
