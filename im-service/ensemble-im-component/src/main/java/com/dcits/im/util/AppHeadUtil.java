package com.dcits.im.util;

import com.dcits.comet.rpc.api.model.head.AppHead;

public class AppHeadUtil {

    public static AppHead getAppHeadPage(String page){
        AppHead appHead = new AppHead();
        appHead.setPgupOrPgdn("1");
        appHead.setTotalNum(page);
        appHead.setCurrentNum("0");
        appHead.setTotalFlag("E");
        return appHead;
    }

    public static AppHead getAppHeadNoPage(){
        AppHead appHead = new AppHead();
        appHead.setPgupOrPgdn("1");
        appHead.setTotalNum("-1");
        appHead.setCurrentNum("0");
        appHead.setTotalFlag("N");
        return appHead;
    }
}
