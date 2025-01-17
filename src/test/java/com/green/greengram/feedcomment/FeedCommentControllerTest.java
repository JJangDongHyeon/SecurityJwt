package com.green.greengram.feedcomment;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.greengram.CharEncodingConfiguration;
import com.green.greengram.feedcomment.model.FeedCommentGetRes;
import com.green.greengram.feedcomment.model.FeedCommentPostReq;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({ CharEncodingConfiguration.class })
@WebMvcTest({ FeedCommentControllerImpl.class })
class FeedCommentControllerTest {

    @Autowired private ObjectMapper om;
    @Autowired private MockMvc mvc;
    @MockBean private FeedCommentService service;
    private final String BASE_URL = "/api/feed/comment";
    @Test
    void postFeedComment() throws Exception{
        FeedCommentPostReq p = new FeedCommentPostReq();
        p.setFeedId(1);
        p.setUserId(2);
        p.setComment("안녕");

        long resultData = 1;

        String reqJson = om.writeValueAsString(p);

        given(service.postFeedComment(p)).willReturn(resultData);

        Map<String, Object> expectedRes = new HashMap();
        expectedRes.put("statusCode", HttpStatus.OK);
        expectedRes.put("resultMsg", "댓글 등록 완료");
        expectedRes.put("resultData", resultData);

        String expectedResJson = om.writeValueAsString(expectedRes);

        mvc.perform(
                        post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(reqJson)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResJson))
                .andDo(print());

        verify(service).postFeedComment(p);
    }

    @Test
    void delfeedFavorite() {
    }

    @Test
    void feedCommentListGet()  throws Exception{
        long feedId = 1;
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("feed_id", String.valueOf(feedId));

        List<FeedCommentGetRes> expectList = new ArrayList<>();
        FeedCommentGetRes item1 = new FeedCommentGetRes();
        item1.setFeedCommentId(1);

        FeedCommentGetRes item2 = new FeedCommentGetRes();
        item1.setFeedCommentId(2);

        FeedCommentGetRes item3 = new FeedCommentGetRes();
        item1.setFeedCommentId(3);

        expectList.add(item1);
        expectList.add(item2);
        expectList.add(item3);

        given(service.feedCommentListGet(feedId)).willReturn(expectList);

        Map<String, Object> expectedRes = new HashMap();
        expectedRes.put("statusCode", HttpStatus.OK);
        expectedRes.put("resultMsg", String.format("rows: %,d",expectList.size()));
        expectedRes.put("resultData", expectList);

        String expectedResJson = om.writeValueAsString(expectedRes);

        mvc.perform(
                get(BASE_URL).params(params)
        )
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResJson))
                .andDo(print());
        verify(service).feedCommentListGet(feedId);

    }
}