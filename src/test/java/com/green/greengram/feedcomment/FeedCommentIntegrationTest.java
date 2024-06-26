package com.green.greengram.feedcomment;

import com.green.greengram.BaseIntegrationTest;
import com.green.greengram.common.model.ResultDto;
import com.green.greengram.feedcomment.model.FeedCommentPostReq;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FeedCommentIntegrationTest extends BaseIntegrationTest {
    private final String BASE_URL = "/api/feed/comment";
    @Test
    @DisplayName("post - 댓글 쓰기")
    @Rollback(value = false)
    public void postComment() throws Exception{
        int resultData= 13;
        FeedCommentPostReq p = new FeedCommentPostReq();
        p.setFeedId(1);
        p.setUserId(1);
        p.setComment("댓글");

        String reqJson = om.writeValueAsString(p);

        MvcResult mr = mvc.perform(
                post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reqJson)
        )
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn();

        String resContents = mr.getResponse().getContentAsString();
        ResultDto<Integer> result = om.readValue(resContents, ResultDto.class);
        assertEquals(resultData, result.getResultData());

    }
}
