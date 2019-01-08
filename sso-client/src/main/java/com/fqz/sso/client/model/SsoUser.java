package com.fqz.sso.client.model;

import java.io.Serializable;

/**
 * @author fuqianzhong
 * @date 19/1/3
 */
public class SsoUser implements Serializable {
    private static final long serialVersionUID = 5387877855860326859L;

    private Integer userId;
    private String userName;
    private String password;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "userId:"+userId+",userName:"+userName+",pwd:"+password;
    }
}
