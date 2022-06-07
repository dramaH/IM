package com.dcits.im.api.model;

import com.dcits.comet.flow.annotation.V;
import com.dcits.comet.rpc.api.model.BaseResponse;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class IMMessageHisQueryResponse extends BaseResponse {
    private static final long serialVersionUID = 1L;

    @V(desc = "body")
    private Body body = new Body();

    @Data
    public static class Body implements Serializable {

        private static final long serialVersionUID = 1L;

        @V(desc = "联系人列表", notNull = false, length = "0")
        private List<Message> msgList = new ArrayList<>();

        @Data
        public static class Message implements Serializable {

            private static final long serialVersionUID = 1L;

            @V(desc = "消息ID", notNull = false, length = "32")
            private String msgId;
            @V(desc = "用户ID", notNull = false, length = "32")
            private String userId;
            @V(desc = "用户名称", notNull = false, length = "50")
            private String userName;
            @V(desc = "消息方向", notNull = false, length = "0")
            private Integer direction;
            @V(desc = "消息类型", notNull = false, length = "0")
            private Integer action;
            @V(desc = "是否读取", notNull = false, length = "0")
            private Integer isRead;
            @V(desc = "消息标题", notNull = false, length = "50")
            private String title;
            @V(desc = "消息内容", notNull = false, length = "512")
            private String content;
            @V(desc = "读取类型", notNull = false, length = "0")
            private Integer readType;
            @V(desc = "消息格式", notNull = false, length = "0")
            private Integer format;
            @V(desc = "消息日期", notNull = false, length = "8")
            private String msgDate;
            @V(desc = "消息时间戳", notNull = false, length = "0")
            private Long timestamp;
        }
    }
}
