package com.dcits.im.api.model;

import com.dcits.comet.flow.annotation.V;
import com.dcits.comet.rpc.api.model.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class IMOnceSendRequest extends BaseRequest {
    private static final long serialVersionUID = 1L;

    @V(desc = "body")
    private Body body;

    @Data
    public static class Body implements Serializable {
        private static final long serialVersionUID = 1L;
        @V(desc = "发送者ID", notNull = true, length = "32")
        private String fromUserId;
        @V(desc = "接收者ID", notNull = true, length = "32")
        private String toUserId;
        @V(desc = "消息内容", notNull = false, length = "512")
        private String content;
        @V(desc = "消息格式", notNull = true, length = "0")
        private Integer format;
    }
}
