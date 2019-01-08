package com.fqz.sso.client.utils;

import com.fqz.sso.client.model.Constant;
import com.fqz.sso.client.model.SsoUser;
import org.apache.commons.codec.binary.Base64;

import java.nio.charset.Charset;
import java.util.UUID;

/**
 * @author fuqianzhong
 * @date 19/1/3
 */
public class TokenUtils {

    public static String createToken(SsoUser ssoUser){
        String content = ssoUser.getUserId() + "_" + UUID.randomUUID().toString().replaceAll("-","");
        return Base64.encodeBase64String(content.getBytes(Charset.forName("utf-8")));
    }

    //store key : sso-user{userId}
    public static String parseToStoreKey(SsoUser ssoUser){
        return Constant.SSO_USER+ssoUser.getUserId();
    }


    public static String parseToStoreKey(String token){
        if(token == null || token.length() == 0){
            return null;
        }

        String content = new String(Base64.decodeBase64(token), Charset.forName("utf-8"));

        if(content != null){
            return Constant.SSO_USER+content.split("_")[0];
        }
        return null;
    }

}
