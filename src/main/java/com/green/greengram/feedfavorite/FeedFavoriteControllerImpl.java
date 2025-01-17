package com.green.greengram.feedfavorite;

import com.green.greengram.common.model.ResultDto;
import com.green.greengram.feedfavorite.model.FeedFavoriteReq;
import com.green.greengram.security.AuthenticationFacade;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feed/favorite")
public class FeedFavoriteControllerImpl implements FeedFavoriteController{
    private final FeedFavoriteService service;
    private final AuthenticationFacade authenticationFacade;
    @GetMapping
    public ResultDto<Integer> toggleReq(@ParameterObject @ModelAttribute FeedFavoriteReq p) {
        p.setUserId(authenticationFacade.getLoginUserId());
        int result = service.toggleReq(p);

        return ResultDto.<Integer>builder()
                .statusCode(HttpStatus.OK)
                .resultMsg(result == 0? "좋아요 취소" : "좋아요")
                .resultData(result)
                .build();
    }




}
