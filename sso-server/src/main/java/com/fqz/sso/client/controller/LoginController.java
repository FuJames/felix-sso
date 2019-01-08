package com.fqz.sso.client.controller;

import com.fqz.sso.client.model.Constant;
import com.fqz.sso.client.model.SsoUser;
import com.fqz.sso.client.service.UserService;
import com.fqz.sso.client.utils.CookieUtils;
import com.fqz.sso.client.utils.JedisUtils;
import com.fqz.sso.client.utils.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author fuqianzhong
 * @date 19/1/3
 */
@Controller
public class LoginController {

    @Resource
    private UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    /**
     * login page
     * @return
     */
    @RequestMapping("/login")
    public String login(HttpServletRequest request, HttpServletResponse response, Model model){
        //get redirect_url from request ?
        model.addAttribute(Constant.REDIRECT_URL, request.getParameter(Constant.REDIRECT_URL));
        return "login";
    }

    /**
     * login ajax api
     * @param userName
     * @param password
     * @param rememberPwd
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/doLogin")
    public String doLogin(@RequestParam String userName,
                          @RequestParam String password,
                          @RequestParam(required = false) String rememberPwd,
                          @RequestParam String redirectUrl,
                           HttpServletRequest request,
                           HttpServletResponse response
                           ){
        //invalid login
        SsoUser ssoUser = userService.findUser(userName, password);
        if(ssoUser == null){
            return "redirect:login";
        }
        //1. generate token
        String token = TokenUtils.createToken(ssoUser);
        //2. save token to redis
        String storeKey = TokenUtils.parseToStoreKey(ssoUser);
        JedisUtils.setObject(storeKey,ssoUser);
        //3. create cookie
        boolean isRemeber = (rememberPwd != null && rememberPwd.equals("on")) ? true : false;
        CookieUtils.setCookie(Constant.SSO_TOKEN,token,null,response,isRemeber);
        logger.info(">>>>>> login success <<<<<<<");
        if(redirectUrl != null && redirectUrl.trim().length() > 0){
            return "redirect:" + redirectUrl;
        }
        return "redirect:login";
    }

    @RequestMapping("/logout")
    public String doLogout(HttpServletRequest request,
                          HttpServletResponse response){
        //get current cookie
        Cookie cookie = CookieUtils.getCookie(Constant.SSO_TOKEN,request);
        if(cookie == null){
            return "redirect:login";
        }
        //get token from cookie
        String token = cookie.getValue();

        //clear redis
        String storeKey = TokenUtils.parseToStoreKey(token);
        JedisUtils.deleteKey(storeKey);

        //clear cookie
        CookieUtils.deleteCookie(Constant.SSO_TOKEN,request,response);

        return "redirect:login";
    }
}
