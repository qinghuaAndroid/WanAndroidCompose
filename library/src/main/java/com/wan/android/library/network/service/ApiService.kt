package com.wan.android.library.network.service

import com.wan.android.library.bean.ArticleEntity
import com.wan.android.library.bean.BannerEntity
import com.wan.android.library.bean.HotSearchEntity
import com.wan.android.library.bean.IntegralRecordEntity
import com.wan.android.library.bean.MyArticleEntity
import com.wan.android.library.bean.NavigationEntity
import com.wan.android.library.bean.RankEntity
import com.wan.android.library.bean.SystemListEntity
import com.wan.android.library.bean.TabEntity
import com.wan.android.library.bean.User
import com.wan.android.library.bean.UserInfoEntity
import com.wan.android.library.network.HttpResult
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * @author cy
 * Create at 2020/4/2.
 */
interface ApiService {
    /**
     * 登录
     */
    @FormUrlEncoded
    @POST("/user/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): HttpResult<User>

    /**
     * 注册
     */
    @FormUrlEncoded
    @POST("/user/register")
    suspend fun register(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("repassword") repassword: String
    ): HttpResult<Any>

    /**
     * banner
     */
    @GET("/banner/json")
    suspend fun getBanner(): HttpResult<MutableList<BannerEntity>>

    /**
     * 获取首页置顶文章数据
     */
    @GET("/article/top/json")
    suspend fun getTopList(): HttpResult<MutableList<ArticleEntity.DatasBean>>

    /**
     * 获取首页文章数据
     */
    @GET("/article/list/{page}/json")
    suspend fun getHomeList(@Path("page") pageNo: Int): HttpResult<ArticleEntity>

    /**
     * 收藏
     */
    @POST("/lg/collect/{id}/json")
    suspend fun collect(@Path("id") id: Int): HttpResult<Any>

    /**
     * 取消收藏
     */
    @POST("/lg/uncollect_originId/{id}/json")
    suspend fun unCollect(@Path("id") id: Int): HttpResult<Any>

    /**
     * 取消收藏
     */
    @POST("/lg/uncollect/{id}/json")
    @FormUrlEncoded
    suspend fun unMyCollect(@Path("id") id: Int, @Field("originId") originId: Int): HttpResult<Any>

    /**
     * 获取项目tab
     */
    @GET("/project/tree/json")
    suspend fun getProjectTabList(): HttpResult<MutableList<TabEntity>>

    /**
     * 获取项目tab
     */
    @GET("/wxarticle/chapters/json  ")
    suspend fun getAccountTabList(): HttpResult<MutableList<TabEntity>>

    /**
     * 获取项目列表
     */
    @GET("/project/list/{pageNum}/json")
    suspend fun getProjectList(@Path("pageNum") pageNum: Int, @Query("cid") cid: Int)
            : HttpResult<ArticleEntity>

    /**
     * 获取公众号列表
     */
    @GET("/wxarticle/list/{id}/{pageNum}/json")
    suspend fun getAccountList(@Path("id") cid: Int, @Path("pageNum") pageNum: Int)
            : HttpResult<ArticleEntity>

    /**
     * 体系
     */
    @GET("/tree/json")
    suspend fun getSystemList(): HttpResult<MutableList<SystemListEntity>>


    /**
     * 获取项目tab
     */
    @GET("/article/list/{pageNum}/json")
    suspend fun getSystemArticle(@Path("pageNum") pageNum: Int, @Query("cid") cid: Int)
            : HttpResult<ArticleEntity>

    /**
     * 导航
     */
    @GET("/navi/json")
    suspend fun getNavigation(): HttpResult<MutableList<NavigationEntity>>

    /**
     * 排名
     */
    @GET("/coin/rank/{pageNum}/json")
    fun getRank(@Path("pageNum") pageNum: Int): HttpResult<RankEntity>

    /**
     * 获取个人信息
     */
    @GET("/user/lg/userinfo/json")
    suspend fun getUserInfo(): HttpResult<UserInfoEntity>

    /**
     * 积分记录
     */
    @GET("/lg/coin/list/{pageNum}/json")
    suspend fun getIntegralRecord(@Path("pageNum") pageNum: Int): HttpResult<IntegralRecordEntity>

    /**
     * 广场列表
     */
    @GET("/user_article/list/{pageNum}/json")
    suspend fun getShareArticle(@Path("pageNum") pageNum: Int): HttpResult<ArticleEntity>

    /**
     * 我分享的文章
     */
    @GET("/user/lg/private_articles/{pageNum}/json")
    suspend fun getMyArticle(@Path("pageNum") pageNum: Int): HttpResult<MyArticleEntity>

    /**
     * 删除我分享的文章
     */
    @POST("/lg/user_article/delete/{id}/json")
    fun deleteMyArticle(@Path("id") id: Int): HttpResult<Any>

    /**
     * 分享文章
     */
    @POST("/lg/user_article/add/json")
    fun shareArticle(
        @Query("title") title: String,
        @Query("link") link: String
    ): HttpResult<Any>

    /**
     * 获取收藏文章数据
     */
    @GET("/lg/collect/list/{page}/json")
    suspend fun getCollectData(@Path("page") pageNo: Int):
            HttpResult<ArticleEntity>

    /**
     * 退出登录
     */
    @GET("/user/logout/json")
    suspend fun logout(): HttpResult<Any>

    /**
     * 搜索热词
     * http://www.wanandroid.com/hotkey/json
     */
    @GET("hotkey/json")
    suspend fun getHotSearchData(): HttpResult<MutableList<HotSearchEntity>>

    /**
     * 搜索
     * http://www.wanandroid.com/article/query/0/json
     * @param page
     * @param key
     */
    @POST("article/query/{page}/json")
    @FormUrlEncoded
    suspend fun queryBySearchKey(
        @Path("page") page: Int,
        @Field("k") key: String
    ): HttpResult<ArticleEntity>

    /**
     * 问答
     */
    @GET("/wenda/list/{pageNum}/json")
    suspend fun getQuestionList(@Path("pageNum") pageNum: Int): HttpResult<ArticleEntity>
}