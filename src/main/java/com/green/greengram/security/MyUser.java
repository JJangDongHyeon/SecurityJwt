package com.green.greengram.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
//현재 필요없는 어노테이션, JSON에 더 많은 속성이 있는데 MyUser에 없는 멤버필드는 무시하고 객체 생성을 할 수 있는 것.
/*
예를 들어
{
    "userId": 10,
    "role": "ROLE_USER",
    "addr": "대구시"
}
위 JSON을 MyUser로 파싱할 때 에러가 발생된다. 왜? addr값을 담을 수 없기 때문에....
@JsonIgnoreProperties(ignoreUnknown = true) 이 어노테이션을 작성하면 addr은 무시하고 객체생성을 한다.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor//JSON > Object 할 때 필요
@Builder
public class MyUser {
    private long userId; // 로그인한 사용자의 pk값
    private String role; // 사용자 권한 , ROLE_권한이름
}
