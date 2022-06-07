package com.dcits.im.restful.service;

import com.dcits.comet.rpc.api.annotation.CometConsumer;
import com.dcits.comet.rpc.api.annotation.CometMapping;
import com.dcits.im.restful.model.MbsdCore14002370In;
import com.dcits.im.restful.model.MbsdCore14002370Out;
import org.springframework.stereotype.Component;

/**
 * @description 调用核心公共PB
 * @date 2021/12/13
 */
@CometConsumer(value = "ensemble-pb-service")
@Component
public interface IPbService {

    /**
     * 用户信息查询
     * @param in
     * @return MbsdCore14002305Out
     */
    @CometMapping(value = "/pb/inq/userInfoByRole/query")
    public MbsdCore14002370Out getUserInfos(MbsdCore14002370In in);

}
