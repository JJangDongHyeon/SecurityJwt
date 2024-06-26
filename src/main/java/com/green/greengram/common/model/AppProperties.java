package com.green.greengram.common.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@ConfigurationProperties(prefix = "app") // prefix
public class AppProperties {
    private final Jwt jwt = new Jwt();
    //class명 Jwt는 application.yaml의 45Line의 'jwt'를 의미 야믈에선 소문자지만 상관없음
    //static을 안해도 되지만 넣는게 성능상 이점이 있다고함
    //멤버필드명은 야믈의 app/jwt/* 속성명과 매칭
    //application.yaml에서 '-'는 멤버필드에서 카멜케이스기법과 매칭함
    @Getter
    @Setter
    public static class Jwt{
        private String secret;
        private String headerSchemaName;
        private String tokenType;
        private long accessTokenExpiry;
        private long refreshTokenExpiry;
        private int refreshTokenCookieMaxAge;

        public void setRefreshTokenExpiry(long refreshTokenExpiry) {
            this.refreshTokenExpiry = refreshTokenExpiry;
            this.refreshTokenCookieMaxAge = (int) (refreshTokenExpiry * 0.001);//ms
        }
    }
}
