package com.green.greengram.feedfavorite;

import com.green.greengram.BaseIntegrationTest;
import com.green.greengram.common.model.ResultDto;
import com.green.greengram.feedfavorite.model.FeedFavoriteReq;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FeedFavoriteIntegrationTest extends BaseIntegrationTest {
    private final String BASE_URL = "/api/feed/favorite";

    @Test
    @Rollback(false)
    @DisplayName("ins - 게시글 좋아요")
    public void insFavorite() throws Exception{
        MultiValueMap<String , String> params = new LinkedMultiValueMap();
        params.add("feed_id","1");
        params.add("user_id","1");

        MvcResult mr = mvc.perform(
                    get(BASE_URL)
                            .params(params)
        )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        String resContents = mr.getResponse().getContentAsString();
        ResultDto<Integer> result = om.readValue(resContents, ResultDto.class);//스트링을 객체로 바꾸는거
        assertEquals(1, result.getResultData());
    }

    @Test
    @Rollback(false)
    @DisplayName("ins - 게시글 좋아요 취소")
    public void delFavorite() throws Exception{
        MultiValueMap<String , String> params = new LinkedMultiValueMap();
        params.add("feed_id","5");
        params.add("user_id","1");

        MvcResult mr = mvc.perform(
                        get(BASE_URL)
                                .params(params)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        String resContents = mr.getResponse().getContentAsString();
        ResultDto<Integer> result = om.readValue(resContents, ResultDto.class);//스트링을 객체로 바꾸는거
        assertEquals(0, result.getResultData());
    }
}
