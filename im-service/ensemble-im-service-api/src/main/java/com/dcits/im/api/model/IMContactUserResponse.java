package com.dcits.im.api.model;

import com.dcits.comet.flow.annotation.V;
import com.dcits.comet.rpc.api.model.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class IMContactUserResponse extends BaseResponse {
    private static final long serialVersionUID = 1L;

    @V(desc = "body")
    private Body body = new Body();

    @Data
    public static class Body implements Serializable {

        private static final long serialVersionUID = 1L;

        @V(desc = "联系人列表", notNull = false, length = "0")
        private List<Contract> contactList = new ArrayList<>();

        @Data
        public static class Contract implements Serializable {

            private static final long serialVersionUID = 1L;

            @V(desc = "联系人类型", notNull = false, length = "0")
            private Integer contactType;
            @V(desc = "用户ID", notNull = false, length = "32")
            private String userId;
            @V(desc = "用户名称", notNull = false, length = "50")
            private String userName;
            @V(desc = "用户类型编号", notNull = false, length = "2")
            private String userType;
            @V(desc = "用户类型名称", notNull = false, length = "50")
            private String userTypeName;
            @V(desc = "用户名首字母", notNull = false, length = "1")
            private String capFirst;
        }
    }
}
