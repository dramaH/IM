package com.dcits.im.convert;

import com.dcits.im.api.model.IMContactUserResponse;
import com.dcits.im.jpa.entity.UmStaffInfo;
import com.dcits.im.jpa.model.ContactInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author hanqian@dcits.com
 * @date 2021年05月11日
 */
@Mapper
public interface ContactConvert {
    ContactConvert INSTANCE = Mappers.getMapper(ContactConvert.class);

    List<IMContactUserResponse.Body.Contract> toContact(List<ContactInfo> contactInfoList);
}
