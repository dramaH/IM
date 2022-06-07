package com.dcits.im.api.service;

import com.dcits.im.api.model.IMNoticeSendRequest;
import com.dcits.im.api.model.IMNoticeSendResponse;
import com.dcits.im.api.model.IMOnceSendRequest;
import com.dcits.im.api.model.IMOnceSendResponse;

public interface IIMOnceSend {
    String IM_ONCE_SESSION_SEND = "/im/onceSession/send";

    IMOnceSendResponse runService(IMOnceSendRequest request);
}
