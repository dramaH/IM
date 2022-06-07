package com.dcits.im.convert;

import com.dcits.im.jpa.entity.ImRecUserSession;
import com.farsunset.cim.sdk.server.model.MessagePushListBody;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author hanqian@dcits.com
 * @date 2021年05月11日
 */
@Mapper
public interface SessionConvert {
    SessionConvert INSTANCE = Mappers.getMapper(SessionConvert.class);

    MessagePushListBody toPushList(ImRecUserSession sessionList);
}
