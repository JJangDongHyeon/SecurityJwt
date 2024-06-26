package com.green.greengram;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;

@Configuration//얘가 없고 @Bean을 하면 빈 등록 되기는 하지만 싱글톤이 유지가 안됨
//싱글톤 쓰는 이유 >> 메모리의 효율화를 위해 하나쓰고 파기 하나쓰고 파기 반복
public class CharEncodingConfiguration {
    @Bean//빈 주면 스프링이 얘를 호출 그럼 리턴하는걸 빈등록 해줌 빈 등록을 여러개 할때 @Configuration하면 편하다고함
    public CharacterEncodingFilter characterEncodingFilter(){
        return new CharacterEncodingFilter("UTF-8",true);
    }
}
