package com.dcits.im.bc;

import com.dcits.comet.IContext;
import com.dcits.comet.commons.Context;
import com.dcits.comet.commons.utils.BusiUtil;
import com.dcits.im.api.model.IMContactUserRequest;
import com.dcits.im.api.model.IMContactUserResponse;
import com.dcits.im.bc.contact.ContactComp;
import com.dcits.im.constants.MsgEnum;
import com.dcits.im.constants.UserTypeEnum;
import com.dcits.im.convert.ContactConvert;
import com.dcits.im.jpa.model.ContactInfo;
import com.dcits.im.util.PinYinUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Component
public class IMContractUserComponent {

    @Resource
    private ContactComp contactComp;

    public IMContactUserResponse doQuery(IMContactUserRequest request){

        IMContactUserResponse response = new IMContactUserResponse();

        String userId = request.getBody().getUserId();
        if(BusiUtil.isNull(userId)){
            userId = request.getSysHead().getUserId();
        }

        /**
         * 查询联系人
         */
        List<ContactInfo> contactInfoList = contactComp.query(Context.getInstance().getAppHead(), userId);
        IContext.getInstance().getAppHead().setTotalRows(String.valueOf(contactInfoList.size()));

        //处理用户类型名称、用户名首字母
        List<IMContactUserResponse.Body.Contract> contractList = ContactConvert.INSTANCE.toContact(contactInfoList);
        for (IMContactUserResponse.Body.Contract contract : contractList) {
            if(BusiUtil.isEquals(userId, contract.getUserId())){
                //去掉当前用户
                continue;
            }
            contract.setContactType(MsgEnum.CONTACT_TYPE_NORMAL.getCode());
            if(BusiUtil.isNotNull(contract.getUserType())){
                if(UserTypeEnum.toEnum(contract.getUserType()) != null){
                    contract.setUserTypeName(UserTypeEnum.toEnum(contract.getUserType()).getName());
                }else{
                    contract.setUserTypeName(contract.getUserType());
                }
            }
            contract.setCapFirst(PinYinUtil.getInitCha(contract.getUserName()));
        }
        response.getBody().setContactList(contractList);
        return response;
    }

}
