package com.fqz.sso.client.service;

import com.fqz.sso.client.model.SsoUser;

import java.util.List;

/**
 * @author fuqianzhong
 * @date 19/1/3
 */
public interface UserService {
    List<SsoUser> findAllUsers();

    SsoUser findUser(String name, String pwd);
}
