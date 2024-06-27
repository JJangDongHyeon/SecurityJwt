package com.green.greengram.security;

import com.green.greengram.security.jwt.JwtAuthenticationAccessDeniedHandler;
import com.green.greengram.security.jwt.JwtAuthenticationEntryPoint;
import com.green.greengram.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration//이 친구가 적힌 클래스 밑에는 @Bean이라는 어노테이션이 붙은 메소드가 무조건있음
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    /*
      메소드 빈 등록으로 주로 쓰는 케이스는 (현재 기준으로 설명하면) Security와 관련된
      빈 등록을 여러개 하고 싶을 때 메소드 형식으로 빈 등록하면 한 곳에 모을 수가 있으니 좋다.
      메소드 빈 등록으로 하지 않으면 각각 클래스로 만들어야 한다.
     */

    @Bean//이 빈이 붙으면 스프링컨테이너가 이 메소드를 무조건 호출, 내가 여기 옆에다가 파라미터 뭐를 받겠닥 적음
    //얘로 이 메소드를 만들거기 때문에 피룡한거 적음. 스프링컨테이너가 줄 수있는거를. 줄 수 있다면
    //스프링 컨테이너가 객체화 해서 줌.(DI해줌) . 스프링컨테이너가 못주는 파라미터라면 에러 터트림
    //시큐리티는 라이브러리에 빈 등록이 돼 있어서 객체화 됨
    //원래 시큐리티 쓰기로 해놓고 세팅 왜 안했니 하고 앱 실행이 안되는데 이제는 됨
    //@Bean 메소드타입의 빈 등록 (파라미터 , 리턴타입 중요) 파라미터는 빈등록할 때 필요한 객체
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        //파라미터없이 내가 직접 new 객체화해서 리턴으로 빈 등록 가능

        return httpSecurity.sessionManagement
                (session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))//꺽새 애로우 펑션
        //시큐리티에서 세션 사용을 하지 않음을 세팅
                .httpBasic(http -> http.disable())//http 이거 변수명은 맘대로

        //(SSR 서버사이드 렌더링 하지 않는다. 즉 html화면을 백엔드가 만들지 않는다.)
        // 백엔드에서 화면을 만들지 않더라도 위 세팅을 끄지 않아도  괜찮다. 사용하지 않는 걸 끔으로서 리소스 확보
        // 하기 위해서 사용하는 개념
        // 정리하면 , 시큐리티에서 제공해주는 로그인 홤녀 사용하지 않겠다.
                .formLogin(form -> form.disable()) //스프링에서 제공해주는 폼 로그인 화면을 안쓰겠다는 뜻
                .csrf(csrf -> csrf.disable()) //CSRF (CORS랑 많이 헷갈려 함) CSRF는 세션 로그인 상태에서만 됨
                //requestMatchers
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(
                                //회원가입 ,로그인 인증이 안 되어있더라도 사용 가능하게 세팅
                                        "/api/user/sign-up"
                                        ,"/api/user/sign-in"
                                        ,"/api/user/access-token"
                                //스웨거 사용가능하게 세팅
                                        , "/swagger"
                                        , "/swagger-ui/**"
                                        , "/v3/api-docs/**"
                                //프론트 화면 보일 수 있게 세팅
                                        ,"/" //이건 딱 주소값만 쳤을때 localhost 8080 쳤을때 쳣화면
                                        ,"/index.html"
                                        , "/css/**"
                                        , "/js/**"
                                        , "/static/**"
                                //프론트에서 사용하는 라우터 주소
                                        ,"/sign-in"
                                        ,"/sign-up"
                                        ,"/profile/*"
                                        ,"/feed"
                                //사진
                                        ,"/pic/**"
                                        ,"/fimg/**"//프론트 스태틱 이미 저장 공간
                                ).permitAll()
                                //v3는 스웨거에서 쓰는 주소값 체계
                                .anyRequest().authenticated()//로그인이 되어 있어야만 허용
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                                                         .accessDeniedHandler(new JwtAuthenticationAccessDeniedHandler())
                )
                //UsernamePasswordAuthenticationFilter.class 전에  jwtAuthenticationFilter 실행한다는 뜻
//                //람다에서 중괄호를 생략하고 싶으면 이 안에 쓰는 내용이 한 문장이어야 함 그리고 끝에 세미콜론이 없어야함
                //이건 로그인
                //되어 있지 않아도 사용할 수 있어야 하기 때문, 이거 2개는 로그인 안돼있어도 사용 할 수 있게 하겠다.

                .build();




        /*
                //만약, permitAll메소드가 void였다면 아래와 같이 작성을 해야함
                .authorizeHttpRequests(auth -> {
                    //
                    auth.requestMatchers("/api/user/sign-up","/api/user/sign-in").permitAll();
                    auth.requestMatchers("/api/ddd").authenticated();
                })
                //permitAll 메소드가 자기 자신의 주소값을 리턴한다면 체이닝 기법 가능
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/user/sign-up","/api/user/sign-in").permitAll()
                        .requestMatchers("/api/ddd").authenticated();
                })
                */
            /*
            //위 람다식을 풀어쓰면 아래와 같다. 람다식은 짧게 적을 수 있는 기법.
            return httpSecurity.sessionManagement(new Customizer<SessionManagementConfigurer<HttpSecurity>>() {
                @Override
                public void customize(SessionManagementConfigurer<HttpSecurity> session) {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                }
            }).build();
    */


    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
