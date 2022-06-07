package com.dcits.im.api.service;

import com.dcits.im.api.model.IMContactUserRequest;
import com.dcits.im.api.model.IMContactUserResponse;
import org.springframework.web.bind.annotation.RequestBody;

public interface IIMContactUser {
    String IM_CONTACT_QUERY = "/im/contact/query";

    IMContactUserResponse runService(@RequestBody IMContactUserRequest request);
}
