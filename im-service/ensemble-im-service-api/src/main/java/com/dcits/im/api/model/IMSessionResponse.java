package com.dcits.im.api.model;

import com.dcits.comet.flow.annotation.V;
import com.dcits.comet.rpc.api.model.BaseResponse;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class IMSessionResponse extends BaseResponse {
    private static final long serialVersionUID = 1L;

    @V(desc = "body")
    private Body body = new Body();

    @Data
    public static class Body implements Serializable {

        private static final long serialVersionUID = 1L;

        @V(desc = "会话列表", notNull = false, length = "0")
        private List<Session> sessionList = new ArrayList<>();

        @Data
        public static class Session implements Serializable {

            private static final long serialVersionUID = 1L;

            @V(desc = "主键ID", notNull = false, length = "32")
            private String id;
            @V(desc = "用户ID", notNull = false, length = "32")
            private String userId;
            @V(desc = "节点ID", notNull = false, length = "32")
            private String nodeId;
            @V(desc = "终端设备ID", notNull = false, length = "32")
            private String deviceId;
            @V(desc = "终端设备型号", notNull = false, length = "32")
            private String deviceName;
            @V(desc = "绑定的服务器IP", notNull = false, length = "15")
            private String host;
            @V(desc = "渠道类型", notNull = false, length = "10")
            private String channel;
            @V(desc = "终端应用版本", notNull = false, length = "10")
            private String appVersion;
            @V(desc = "终端系统版本", notNull = false, length = "10")
            private String osVersion;
            @V(desc = "终端语言", notNull = false, length = "10")
            private String language;
            @V(desc = "经度", notNull = false, length = "10")
            private String longitude;
            @V(desc = "维度", notNull = false, length = "10")
            private String latitude;
            @V(desc = "位置", notNull = false, length = "50")
            private String location;
            @V(desc = "登录时间", notNull = false, length = "19")
            private String bindTime;
            @V(desc = "在线状态", notNull = false, length = "19")
            private Integer isOnline;
            @V(desc = "消息时间戳", notNull = false, length = "0")
            private Long timestamp;
            @V(desc = "状态", notNull = false, length = "0")
            private Integer state;

        }
    }
}
