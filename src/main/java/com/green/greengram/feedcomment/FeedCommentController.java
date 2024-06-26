package com.green.greengram.feedcomment;

import com.green.greengram.common.model.ResultDto;
import com.green.greengram.feedcomment.model.FeedCommentDeleteReq;
import com.green.greengram.feedcomment.model.FeedCommentGetRes;
import com.green.greengram.feedcomment.model.FeedCommentPostReq;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface FeedCommentController {
    ResultDto<Long> postFeedComment(FeedCommentPostReq p);
    ResultDto<Integer> delfeedFavorite(FeedCommentDeleteReq p);
    ResultDto<List<FeedCommentGetRes>>feedCommentListGet(long feedId);
}
