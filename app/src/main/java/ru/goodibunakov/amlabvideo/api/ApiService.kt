package ru.goodibunakov.amlabvideo.api

import com.google.gson.GsonBuilder
import io.reactivex.Observable
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import ru.goodibunakov.amlabvideo.api.dto.playlists.PlaylistsDTO
import ru.goodibunakov.amlabvideo.api.dto.videos.AllVideosDTO
import java.util.concurrent.TimeUnit


interface ApiService {

    @GET("search?")
    fun getLastVideos(
            @Query("part") part: String = "snippet",
            @Query("order") order: String = "date",
            @Query("channelId") channelId: String = CHANNEL_ID,
            @Query("maxResults") maxResults: Int = 20,
            @Query("pageToken") pageToken: String = ""
    ): Observable<AllVideosDTO>

    @GET("playlists?")
    fun getPlaylists(
            @Query("part") part: String = "snippet",
            @Query("order") order: String = "date",
            @Query("channelId") channelId: String = CHANNEL_ID,
            @Query("maxResults") maxResults: Int = 50,
            @Query("pageToken") pageToken: String = ""
    ): Observable<PlaylistsDTO>

//    @GET("/ui-server/v2/service/address/knownSettlements")
//    fun getSettlementList(): Observable<KnownSettlementsInfoDTO>
//
//    @GET("ui-server/v2/service/address/objects/settlements")
//    fun getSettlementsByQuery(@Query("query") query: String, @Query("skip") skip: Int, @Query("limit") limit: Int): Observable<ListInfoDTO>

    companion object {
        private const val BASE_URL = "https://www.googleapis.com/youtube/v3/"
        private const val YOUTUBE_APIKEY = "AIzaSyB8XPLOU4IPt99fJHiDhvNjoywzqpA3JT8"
        private const val CHANNEL_ID = "UC_adZIRDiLC3eLIq24HBmRA"

        fun create(): ApiService {
            val gson = GsonBuilder().setLenient().create()

            val httpClient = OkHttpClient.Builder()
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            httpClient.addInterceptor(httpLoggingInterceptor)
            httpClient.addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    val original = chain.request()
                    val originalHttpUrl = original.url
                    val url = originalHttpUrl.newBuilder()
                            .addQueryParameter("key", YOUTUBE_APIKEY)
                            .build()
                    val requestBuilder = original.newBuilder().url(url)
                    val request = requestBuilder.build()
                    return chain.proceed(request)
                }
            })
            httpClient.connectTimeout(50, TimeUnit.SECONDS)
            httpClient.readTimeout(50, TimeUnit.SECONDS)
            val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient.build())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}