package com.dcits.im.api.model;

import com.dcits.comet.flow.annotation.V;
import com.dcits.comet.rpc.api.model.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class IMMessageHisQueryRequest extends BaseRequest {
    private static final long serialVersionUID = 1L;

    @V(desc = "body")
    private Body body;

    @Data
    public static class Body implements Serializable {
        private static final long serialVersionUID = 1L;

        @V(desc = "会话ID", notNull = true, length = "32")
        private String sessionId;
        @V(desc = "消息ID", notNull = false, length = "32")
        private String msgId;
        @V(desc = "查询条数", notNull = true, length = "0")
        private Integer rows;
    }
}
