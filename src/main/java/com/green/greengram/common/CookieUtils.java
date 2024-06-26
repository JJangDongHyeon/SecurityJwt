package com.green.greengram.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
//옵셔널 처리하긴 무리라 뺴버릴 ㅖ정
public class CookieUtils {
    private final ObjectMapper om;

    public <T extends Object> T getData(T type, HttpServletRequest req, String name){
        try{
            Cookie cookie = getCookie(req, name);
            String json = cookie.getValue();
            return (T) om.readValue(json, type.getClass());
        }   catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //요청 header에
    public Cookie getCookie(HttpServletRequest req, String name){
        Cookie[] cookies = req.getCookies(); //요청에서 모든 쿠키 정보를 받는다.
        if(cookies != null && cookies.length > 0){ // 쿠키 정보가 있고 쿠키가 하나 이상 있다면
            for(Cookie cookie : cookies) {//쿠키 배열과 쿠키
                if(name.equals(cookie.getName())){//찾고자하는 key가 있는지 확인 후 있다면  해당 쿠키 리턴
                    return cookie;
                }
            }
        }
        return null;
    }

    public void setCookie(HttpServletResponse res, String name, String value, int maxAge){
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/"); //Root URL (우리 백엔드 모든 요청에 해당되게 세팅)
        cookie.setHttpOnly(true);//보안쿠키
        cookie.setMaxAge(maxAge); // 만료 시간
        res.addCookie(cookie);
    }

    public void deleteCookie (HttpServletResponse res , String name){
        setCookie(res, name, null, 0);//maxAge(0)하면 바로 지우는 거니까 value에 뭘 넣어도 의미 없음

    }
}
