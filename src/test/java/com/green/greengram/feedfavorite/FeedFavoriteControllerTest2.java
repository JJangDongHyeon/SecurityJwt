package com.green.greengram.feedfavorite;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.greengram.CharEncodingConfiguration;
import com.green.greengram.common.model.ResultDto;
import com.green.greengram.feedfavorite.model.FeedFavoriteReq;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(CharEncodingConfiguration.class)
@WebMvcTest(FeedFavoriteControllerImpl.class)
class FeedFavoriteControllerTest2 {
    @Autowired private ObjectMapper om;
    //JSON을 자바 객체로 바꿔주는 역할
    //제이슨에 데이터가 다 들어있는데 받을 때 왜 다시 객체로 바꾸는가?
    //쓰기 편하라고....
    //예를 들어 { name : "홍길동" , age: 22 } 여기서 홍길동만 뽑아 쓰고 싶을때 substring을 써서
    //쓰는건 귀찮고 힘들다 >> 근데 객체화 시키면 .getName()하면됨
    //Object > Json > Object 반복
    @Autowired private MockMvc mvc;
    //RestFul 통신용
    @MockBean private FeedFavoriteService service;
    //목빈으로 서비스 만드는 이유 1. 컨트롤러를 객체화 시키기 위해 2. 슬라이스 테스트라 컨트롤러만 테스트하기 위함 >>
    // 진짜 서비스 쓰는 것 보다 속도 빠름
    private final String BASE_URL = "/api/feed/favorite";

    void proc(FeedFavoriteReq p , Map<String, Object> result) throws Exception{
        int resultData = (int) result.get("resultData");
        given(service.toggleReq(p)).willReturn(resultData);

        MultiValueMap<String, String> params = new LinkedMultiValueMap();
        params.add("feed_id",String.valueOf(p.getFeedId()));
        params.add("user_id",String.valueOf(p.getUserId()));

        String expectedResultJson = om.writeValueAsString(result);

        mvc.perform(
//                delete(BASE_URL + "?from_user_id=1&to_user_id=2")//이 작업을 파람이 대신 해줌
                        get(BASE_URL)
                                .params(params)//얘가 알아서 쿼리스트링으로 바뀜
                )       .andExpect(status().isOk())
                .andExpect(content().json(expectedResultJson))
                .andDo(print());

        verify(service).toggleReq(p);
    }

    @Test
    void toggleReqTeacher() throws Exception {
        FeedFavoriteReq p = new FeedFavoriteReq(1 , 2);
//        p.setFeedId(1);
//        p.setUserId(2); @ConstructorProperties안썼을 때
        int resultData = 1;

        Map<String , Object > result = new HashMap();
        result.put("statusCode", HttpStatus.OK);
        result.put("resultMsg", "좋아요");
        result.put("resultData", resultData);
        proc(p, result);
    }
    @Test
    void toggleReq3() throws Exception{
        //resultMsg값이 "좋아요 취소"가 리턴이 되는지 확인
        FeedFavoriteReq p = new FeedFavoriteReq(1 , 2);
//        p.setFeedId(1);
//        p.setUserId(2); @ConstructorProperties안썼을 때
        int resultData = 0;

        Map<String , Object > result = new HashMap();
        result.put("statusCode", HttpStatus.OK);
        result.put("resultMsg", "좋아요 취소");
        result.put("resultData", resultData);
        proc(p, result);
    }
}