package com.dcits.im.constants;

import com.dcits.comet.commons.utils.BusiUtil;

public enum UserCategoryEnum {

    /**用户类别*/
    USER_CATEGORY_INSIDE("01","内部"),
    USER_CATEGORY_OUTSIDE("02","外包"),
    USER_CATEGORY_INDIV("03","零售"),
    USER_CATEGORY_BATCH("04","批售"),
    USER_CATEGORY_FIRM("05","厂商");


    private String code;
    private String name;

    UserCategoryEnum(String code, String name){
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code;
    }

    public static UserCategoryEnum toEnum(String code){
        if(BusiUtil.isNull(code)){
            return null;
        }
        for (UserCategoryEnum value : values()) {
            if(value.getCode().equals(code)){
                return value;
            }
        }
        return null;
    }
}
