package com.dcits.im.service;

import com.dcits.comet.commons.exception.BusinessException;
import com.dcits.comet.commons.utils.BusiUtil;
import com.dcits.comet.commons.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TokenCheck {

    @Value("${ensemble.tokenCheckUrl}")
    private String tokenCheckUrl;

    /**
     * 发送Tokan验证
     * @param userToken
     * @throws IOException
     */
    public void post(String userToken) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            List<Header> headerList = new ArrayList<>();
            BasicHeader basicHeader= new BasicHeader("Cookie", userToken);
            headerList.add(basicHeader);
            HttpPost httpPost = new HttpPost(tokenCheckUrl);
            httpPost.setHeaders(headerList.toArray(new Header[0]));
            httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");
            httpPost.setEntity(new StringEntity("{}"));
            CloseableHttpResponse response = httpClient.execute(httpPost);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                String retCode;
                String retMsg;
                try{
                    HttpEntity entity = response.getEntity();
                    String result = EntityUtils.toString(entity, "UTF-8");
                    Map<String, Object> resultMap = JsonUtils.jsonStringToMap(result);
                    retCode = (String)((Map<String,Object>)resultMap.get("body")).get("retCode");
                    retMsg = (String)((Map<String,Object>)resultMap.get("body")).get("retMsg");
                }catch (Exception e){
                    log.error("上传服务权限认证失败", e);
                    throw BusiUtil.createBusinessException("FL0003", e.getMessage());
                }
                if(!"000000".equals(retCode)){
                    throw BusiUtil.createBusinessException("FL0003", "返回信息:"+retCode+","+retMsg);
                }
            }else{
                throw BusiUtil.createBusinessException("FL0003", "HTTP:"+code);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (IOException e) {
            log.error("上传服务权限认证失败", e);
            throw BusiUtil.createBusinessException("FL0003", e.getMessage());
        }
    }
}
