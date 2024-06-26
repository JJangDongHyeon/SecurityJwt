package com.green.greengram.userfollow;

import com.green.greengram.BaseIntegrationTest;
import com.green.greengram.common.model.ResultDto;
import com.green.greengram.userfollow.model.UserFollowReq;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserFollowIntegrationTest extends BaseIntegrationTest {
    private final String BASE_URL = "/api/user/follow";
    @Test
    @Rollback(false)//데이터베이스에 들어가는지 일단 눈으로 확인 할 예정
    @DisplayName("post - 유저 팔로우")
    public void postUserFollow() throws Exception{
        UserFollowReq p = new UserFollowReq(4, 1);
        String reqJson = om.writeValueAsString(p);


        MvcResult mr =  mvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqJson)
        )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String resContents = mr.getResponse().getContentAsString();
        ResultDto<Integer> result = om.readValue(resContents, ResultDto.class);//스트링을 객체로 바꾸는거
        assertEquals(1, result.getResultData());
    }

    @Test
    @Rollback(value = false)
    @DisplayName("delete - 유저 팔로우")
    public void deleteUserFollow() throws Exception{
        MultiValueMap<String, String> params = new LinkedMultiValueMap();
        params.add("from_user_id", "4");
        params.add("to_user_id", "1");

        MvcResult mr =  mvc.perform(
                        delete(BASE_URL)
                                .params(params)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String resContents = mr.getResponse().getContentAsString();
        ResultDto<Integer> result = om.readValue(resContents, ResultDto.class);//스트링을 객체로 바꾸는거
        assertEquals(1, result.getResultData());
    }
}
