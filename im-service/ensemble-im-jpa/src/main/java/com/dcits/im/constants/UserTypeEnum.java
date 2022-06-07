package com.dcits.im.constants;

import com.dcits.comet.commons.utils.BusiUtil;

public enum UserTypeEnum {

    /**用户类型*/  //TODO:需要与公共微服务枚举值同步
    USER_TYPE_RL_GM_ADMIN("01","柜面机构管理员"),
    USER_TYPE_RL_GM_OPER("02","柜面操作员"),
    USER_TYPE_RL_WY_ADMIN("03","经销商系统管理员"),
    USER_TYPE_RL_JRZY("04","一级金融专员"),
    USER_TYPE_RL_XSGW("05","销售顾问"),
    USER_TYPE_RL_SECOND_JRZY("06","二级金融专员"),
    USER_TYPE_RL_SP_SYS_ADMIN("07","SP系统管理员"),
    USER_TYPE_RL_SP_BUSI_ADMIN("08","SP业务管理员"),
    USER_TYPE_RL_SP_YWY("09","SP业务员"),
    USER_TYPE_RL_CLIENT_OPER("10","集团客户操作员"),
    USER_TYPE_RL_WY_BRAND_ADMIN("11","经销商品牌管理员"),
    USER_TYPE_RL_LEADER_QUERY("12","领导查询"),
    USER_TYPE_RL_DECISION_QUERY("13","决策APP查询"),
    USER_TYPE_RL_MANAGER_QUERY("14","区域经理APP查询"),
    USER_TYPE_BA_BRAND_MANAGER("21","批售品牌管理员"),
    USER_TYPE_BA_DEALER_SYS_MANAGER("22","批售经销商系统管理员"),
    USER_TYPE_BA_INVENTORY_USER("23","柜员盘点用户"),
    USER_TYPE_BA_FINANCIAL_DIRECTOR("24","经销商财务经理"),
    USER_TYPE_BA_SEC_INVENTORY_USER("25","经销商二级盘点用户"),
    USER_TYPE_BA_DEALER_INVESTOR("26","经销商投资人"),
    USER_TYPE_BA_DEALER_DIRECTOR("27","经销商总经理"),
    USER_TYPE_BA_NETWORK_MANAGER("28","网点管理"),
    USER_TYPE_BA_INSIDE_SALES("29","批售销售内勤"),
    USER_TYPE_BA_SALE_CASHIER("30","批售出纳"),
    USER_TYPE_BA_SALE_MANAGER("31","批售销售经理"),
    USER_TYPE_SYS_ROBOT("99","系统机器人");


    private String code;
    private String name;

    UserTypeEnum(String code, String name){
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

    public static UserTypeEnum toEnum(String code){
        if(BusiUtil.isNull(code)){
            return null;
        }
        for (UserTypeEnum value : values()) {
            if(value.getCode().equals(code)){
                return value;
            }
        }
        return null;
    }
}
