package com.dcits.im.jpa.model;

import com.dcits.comet.dao.model.BasePo;
import lombok.Data;

@Data
public class ContactInfo extends BasePo {
    private String userId;
    private String userName;
    private String userType;
    private String clientNo;
    private String clientName;

    public ContactInfo get(){
        return this;
    }
}
