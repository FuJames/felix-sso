package com.fqz.sso.client.service.impl;

import com.fqz.sso.client.model.SsoUser;
import com.fqz.sso.client.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fuqianzhong
 * @date 19/1/3
 */
@Service
public class UserServiceImpl implements UserService{

    private static List<SsoUser> ssoUsers = new ArrayList<>();

    static {
        for(int i = 0; i < 100 ; i++){
            SsoUser ssoUser = new SsoUser();
            ssoUser.setUserId(i);
            ssoUser.setUserName("SsoUser"+i);
            ssoUser.setPassword("SsoPwd"+i);
            ssoUsers.add(ssoUser);
        }
    }

    @Override
    public List<SsoUser> findAllUsers(){
        return ssoUsers;
    }

    @Override
    public SsoUser findUser(String name, String pwd){
        if(StringUtils.isEmpty(name) || StringUtils.isEmpty(pwd)){
            return null;
        }
        for(SsoUser ssoUser : ssoUsers){
            if(name.equals(ssoUser.getUserName()) && pwd.equals(ssoUser.getPassword())){
                return ssoUser;
            }
        }
        return null;
    }
}
