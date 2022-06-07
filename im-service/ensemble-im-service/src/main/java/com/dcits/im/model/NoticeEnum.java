package com.dcits.im.model;

public enum NoticeEnum {

    NOTICE_TYPE_USER(0,"用户"),
    NOTICE_TYPE_ROLE(1,"角色"),
    NOTICE_TYPE_SYSTEM(2,"终端系统类型");


    private Integer code;
    private String message;

    NoticeEnum(Integer code, String message){
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }


    public Integer getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code+"-"+message;
    }
}
