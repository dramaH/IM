package com.dcits.im.constants;

import com.dcits.comet.commons.utils.BusiUtil;

public enum MsgEnum {
    /**
     * 置顶状态
     */
    IS_TOP_NO(0,"否"),
    IS_TOP_YES(1,"是"),
    /**
     * 在线状态
     */
    IS_ONLINE_YES(0,"在线"),
    IS_ONLINE_APNS(1,"APNS在线"),
    IS_ONLINE_NO(2,"不在线"),
    /**
     * 会话开启状态
     */
    IS_OPEN_NO(0,"关闭"),
    IS_OPEN_YES(1,"开启"),
    /**
     * 消息方向
     */
    DIRECT_LEFT(0,"左消息"),
    DIRECT_RIGHT(1,"右消息"),
    /**
     * 消息动作
     */
    ACTION_SYS_MSG(1,"系统消息"),
    ACTION_USER_MSG(2,"用户消息"),
    ACTION_OPEN_SESSION(3,"开启消息"),
    ACTION_CLOSE_SESSION(4,"关闭消息"),
    ACTION_ONCE_SESSION(5,"临时会话消息"),

    /**
     * 消息动作
     */
    MSG_TYPE_SYS(1,"系统消息"),
    MSG_TYPE_USER(2,"用户消息"),
    MSG_TYPE_ONCE(3,"临时会话消息"),
    /**
     * 会话类型
     */
    SESSION_TYPE_ONCE(0,"一次性会话"),
    SESSION_TYPE_NORMAL(1,"普通会话"),
    /**
     * 阅读类型
     */
    READ_TYPE_NOT_FORCE(0,"非强制阅读"),
    READ_TYPE_FORCE(1,"强制阅读"),
    /**
     * 阅读类型
     */
    IS_READ_YES(0,"已读"),
    IS_READ_NO(1,"未读"),
    /**
     * 消息格式
     */
    FORMAT_TEXT(0,"文字"),
    FORMAT_FILE(1,"文件"),
    FORMAT_IMAGE(2,"图片"),
    FORMAT_SOUND(3,"语音"),
    FORMAT_ATTACH(4,"附件"),
    /**
     * 通知类型
     */
    NOTICE_TYPE_TO_USER(0,"指定用户"),
    NOTICE_TYPE_TO_ROLE(1,"指定角色"),
    NOTICE_TYPE_TO_SYSTEM(2,"指定终端系统类型"),
    NOTICE_TYPE_TO_ALL(9,"指定全部"),
    /**
     * 联系人类型
     */
    CONTACT_TYPE_NORMAL(0,"普通用户"),
    CONTACT_TYPE_SYSTEM(1,"系统用户");

    private Integer code;
    private String name;

    MsgEnum(Integer code, String name){
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public Integer getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "["+code+":"+name+"]";
    }

    public static MsgEnum toEnum(Integer code){
        if(BusiUtil.isNull(code)){
            return null;
        }
        for (MsgEnum value : values()) {
            if(value.getCode().equals(code)){
                return value;
            }
        }
        return null;
    }
}
