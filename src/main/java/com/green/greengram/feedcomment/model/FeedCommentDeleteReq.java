package com.green.greengram.feedcomment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.beans.ConstructorProperties;

@Getter
public class FeedCommentDeleteReq {
    private long feedCommentId;

    @JsonIgnore
    private long signedUserId;

    public void setSignedUserId(long signedUserId) {
        this.signedUserId = signedUserId;
    }

    @ConstructorProperties({ "feed_comment_id" })
    public FeedCommentDeleteReq(long feedCommentId) {
        this.feedCommentId = feedCommentId;
    }
}
