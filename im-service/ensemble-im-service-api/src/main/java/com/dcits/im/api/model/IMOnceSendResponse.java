package com.dcits.im.api.model;

import com.dcits.comet.flow.annotation.V;
import com.dcits.comet.rpc.api.model.BaseResponse;
import lombok.Data;

import java.io.Serializable;

@Data
public class IMOnceSendResponse extends BaseResponse {
    private static final long serialVersionUID = 1L;

    @V(desc = "body")
    private Body body = new Body();

    @Data
    public static class Body implements Serializable {
        private static final long serialVersionUID = 1L;

    }
}
