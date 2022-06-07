package com.dcits.im.api.model;

import com.dcits.comet.flow.annotation.V;
import com.dcits.comet.rpc.api.model.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class IMNoticeSendRequest extends BaseRequest {
    private static final long serialVersionUID = 1L;

    @V(desc = "body")
    private Body body;

    @Data
    public static class Body implements Serializable {
        private static final long serialVersionUID = 1L;

        @V(desc = "通知类型", notNull = true, length = "0")
        private Integer noticeType;
        @V(desc = "接收列表", notNull = true, length = "0")
        private List<ToArray> toArray;
        @V(desc = "阅读类型", notNull = true, length = "0")
        private Integer readType;
        @V(desc = "标题", notNull = false, length = "50")
        private String title;
        @V(desc = "消息内容", notNull = false, length = "512")
        private String content;
        @V(desc = "消息格式", notNull = true, length = "0")
        private Integer format;

        @Data
        public static class ToArray implements Serializable {
            private static final long serialVersionUID = 1L;

            @V(desc = "接收者ID", notNull = true, length = "32")
            private String acceptId;

        }
    }
}
