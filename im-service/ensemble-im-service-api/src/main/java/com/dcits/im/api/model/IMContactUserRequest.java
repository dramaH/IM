package com.dcits.im.api.model;

import com.dcits.comet.flow.annotation.V;
import com.dcits.comet.rpc.api.model.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class IMContactUserRequest extends BaseRequest {
    private static final long serialVersionUID = 1L;

    @V(desc = "body")
    private Body body;

    @Data
    public static class Body implements Serializable {

        private static final long serialVersionUID = 1L;

        @V(desc = "用户ID", notNull = true, length = "32")
        private String userId;

    }
}
