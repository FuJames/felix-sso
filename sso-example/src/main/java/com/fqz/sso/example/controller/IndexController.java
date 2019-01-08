package com.fqz.sso.example.controller;

import com.fqz.sso.client.model.Constant;
import com.fqz.sso.client.model.SsoUser;
import com.fqz.sso.example.config.SsoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author fuqianzhong
 * @date 19/1/3
 */
@Controller
public class IndexController {

    @Autowired
    private SsoConfiguration ssoConfiguration;

    @RequestMapping("/index")
    public String login(HttpServletRequest request, HttpServletResponse response, Model model){
        //get sso_user from attribute of request
        SsoUser ssoUser = (SsoUser) request.getAttribute(Constant.SSO_USER);
        model.addAttribute("ssoUser", ssoUser);
        model.addAttribute("ssoLogoutUrl", ssoConfiguration.getSsoServer()+"/logout");
        return "index";
    }

}
