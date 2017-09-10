package net.hashcoding.code.scucrawler.network.service;

import io.reactivex.Observable;
import net.hashcoding.code.scucrawler.entity.Page;
import net.hashcoding.code.scucrawler.entity.Result;
import retrofit2.http.*;

import java.util.List;

public interface ArticleService {

    @FormUrlEncoded
    @PUT("/article")
    Observable<Result<Long>> put(
            @Field("type") String type,
            @Field("url") String url,
            @Field("title") String title,
            @Field("thumb") String thumb,
            @Field("content") String content,
            @Field("createdAt") String createdAt);

    @GET("/article/url_exists")
    Observable<Result<Boolean>> isUrlExists(@Query("url") String url);

    @PUT("/article/{articleId}/attachments")
    Observable<Result<String>> updateAttachments(
            @Path("articleId") Long articleId,
            @Body List<Page.Attachment> attachments);
}
