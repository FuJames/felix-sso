package com.fqz.sso.client.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author fuqianzhong
 * @date 19/1/7
 * cookie属性参考:
 * 1. domain:默认为当前请求的域名,只有domain对应的域名或以domain结尾的域名可以访问此cookie
 * 2. httponly:无法通过js等客户端脚本来访问cookie,防止xss攻击
 * 3. maxAge:该Cookie失效时间，单位秒。如果为正数，则该Cookie在expiry秒之后失效。如果为负数，该Cookie为临时Cookie，关闭浏览器即失效，浏览器也不会以任何形式保存该Cookie。如果为0，表示删除该Cookie。默认为–1
 * 4. path:设置Cookie的使用路径。后面紧接着详解。如果设置为“/agx/”，则只有uri为“/agx”的程序可以访问该Cookie。如果设置为“/”，则本域名下的uri都可以访问该Cookie。注意最后一个字符必须为”/”
 * 5. secure:是否使用安全传输协议。为true时，只有当是https请求连接时cookie才会发送给服务器端，而http时不会，但是服务端还是可以发送给浏览端的。
 */
public class CookieUtils {

    public static void setCookie(String key, String value, String domain, HttpServletResponse response, boolean remeberPwd){
        Cookie cookie = new Cookie(key,value);
        if(domain != null) {
            cookie.setDomain(domain);
        }
        cookie.setPath("/");
        if(remeberPwd){
            cookie.setMaxAge(24*60*60);
        }else {
            cookie.setMaxAge(-1);
        }
        response.addCookie(cookie);
    }

    public static Cookie getCookie(String key, HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if(cookies != null && cookies.length > 0){
            for(int i = 0 ; i < cookies.length ; i++){
                Cookie cookie = cookies[i];
                if(cookie.getName().equals(key)){
                    return cookie;
                }
            }
        }
        return null;
    }

    public static void deleteCookie(String key, HttpServletRequest request, HttpServletResponse response){
        Cookie cookie = getCookie(key,request);
        if(cookie != null){
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }
}
