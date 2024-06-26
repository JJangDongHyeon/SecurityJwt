package com.green.greengram.userfollow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.greengram.CharEncodingConfiguration;
import com.green.greengram.common.model.ResultDto;
import com.green.greengram.userfollow.model.UserFollowReq;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(CharEncodingConfiguration.class)//한글 쓸 때 안깨지게
//임포트 하나 할때는 중괄호 뺴도 됨 여러개할떄는 중괄호 넣어야함.
@WebMvcTest(UserFollowControllerImpl.class)
//@WebMvcTest는 Spring MVC 컨트롤러를 테스트할 때 사용하는 어노테이션입니다
//@WebMvcTest와 같은 스프링 테스트 설정에서는 테스트 대상이 되는 실제 컨트롤러 클래스(구현 클래스)를 명시하는 것이 더 명확하고,
// 테스트 환경 설정에 있어서도 오류의 여지를 줄일 수 있습니다. 인터페이스는 실제 구현부가 ㅇ벗음
class UserFollowControllerTest {
    @Autowired private ObjectMapper om;
    @Autowired private MockMvc mvc; //컨트롤러 테스트 할 때 포스트맨 처럼 레스트풀 통신날리는 용도.
    @MockBean  private UserFollowService service;
    private final String BASE_URL = "/api/user/follow";
    //컨트롤러에서 핵심:
    //제이슨 보내고 제이슨 받아서 검증
    //
    //보낼떄는 쿼리슽릥일 수 있어도
    //받을땐 99% 제이슨으로 받음 제이슨은 스트링으로 받음
    @Test
    void postUserFollow() throws Exception{
        UserFollowReq p = new UserFollowReq(1, 2);
        int resultData = 1;
        given(service.postUserFollow(p)).willReturn(resultData);
        String json = om.writeValueAsString(p);
//
        ResultDto<Integer> expectedResult = ResultDto.<Integer>builder()
                        .statusCode(HttpStatus.OK)
                        .resultMsg(HttpStatus.OK.toString())
                        .resultData(resultData)
                        .build();

        Map expectedResultMap = new HashMap();
        expectedResultMap.put("statusCode", HttpStatus.OK);
        expectedResultMap.put("resultMsg", HttpStatus.OK.toString());
        expectedResultMap.put("resultData", resultData);
//기대하는 값
//        String expectedResultJson = om.writeValueAsString(expectedResult);
        String expectedResultJson = om.writeValueAsString(expectedResult);

        mvc.perform(MockMvcRequestBuilders.post(BASE_URL)//스태틱 메소드라 .앞에MockMvcRequestBuilders생략가능
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(json)

        )       .andExpect(status().isOk())
                .andExpect(content().string(expectedResultJson))//제이슨 검증 하기
                .andDo(print());//이거는 옵션, 출력하라

            verify(service).postUserFollow(p);
    }


    @Test
    void deleteUserFollow() throws Exception {
        UserFollowReq p = new UserFollowReq(1,2);
        int resultData = 1;
        given(service.deleteUserFollow(p)).willReturn(1);

        MultiValueMap<String, String> params = new LinkedMultiValueMap();
        params.add("from_user_id", String.valueOf(p.getFromUserId()));
        params.add("to_user_id", String.valueOf(p.getToUserId()));

        ResultDto<Integer> expectedResult = ResultDto.<Integer>builder()
                .statusCode(HttpStatus.OK)
                .resultMsg(HttpStatus.OK.toString())
                .resultData(resultData)
                .build();

        String expectedResultJson = om.writeValueAsString(expectedResult);

        mvc.perform(
//                delete(BASE_URL + "?from_user_id=1&to_user_id=2")//이 작업을 파람이 대신 해줌
                delete(BASE_URL)
                        .params(params)//얘가 알아서 쿼리스트링으로 바뀜
        )       .andExpect(status().isOk())
                .andExpect(content().json(expectedResultJson))
                .andDo(print());

        verify(service).deleteUserFollow(p);
    }
}