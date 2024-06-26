package com.green.greengram.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor//final이 붙은 애들은 꼭 받는 생성자를 만들겠다는 어노테이션
@Component//빈등록 / 클래스를 객체화 하라
//extends를 했을때 빨간 줄이 뜨면 추상 메소드가 있는 클래스임
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProviderV2 jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(request);//header의  authorization키에 저장 돼있는 값을 리턴(있으면 문자열(JWT), 없으면 null)
        //JWT 값이 있으면 로그인 상태 , null이면 비로그인 상태(로그아웃 상태)
        log.info("JwtAuthenticationFilter-Token: {}",token);

        //토큰이 정상적으로 저장되어 있고, 만료가 되지 않았다면
        if (token != null && jwtTokenProvider.isValidateToken(token)){
            //SecurityContextHolder
            Authentication auth = jwtTokenProvider.getAuthentication(token);
            if(auth != null){
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(request, response);
    }
}
