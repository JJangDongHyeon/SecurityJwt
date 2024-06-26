package com.green.greengram.feedfavorite.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.beans.ConstructorProperties;

@Getter
@ToString
@EqualsAndHashCode
//@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FeedFavoriteReq {

    private long feedId; //feed_id

    @JsonIgnore
    private long userId; //user_id로 보내기

    @ConstructorProperties({ "feed_id" })
    public FeedFavoriteReq(long feedId){
        this.feedId = feedId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
