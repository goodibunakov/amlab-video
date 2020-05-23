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
import ru.goodibunakov.amlabvideo.api.dto.video.VideoDTO
import ru.goodibunakov.amlabvideo.api.dto.playlists.PlaylistsDTO
import ru.goodibunakov.amlabvideo.api.dto.videos.AllVideosDTO
import java.util.concurrent.TimeUnit

/**
 * ---------------------------------------------
 *  YouTube Data API get-request examples
 * ---------------------------------------------
To get channels list :

Get Channels list by forUserName:
https://www.googleapis.com/youtube/v3/channels?part=snippet,contentDetails,statistics&forUsername=Apple&key=

Get channels list by channel id:
https://www.googleapis.com/youtube/v3/channels/?part=snippet,contentDetails,statistics&id=UCE_M8A5yxnLfW0KghEeajjw&key=

Get Channel sections:
https://www.googleapis.com/youtube/v3/channelSections?part=snippet,contentDetails&channelId=UCE_M8A5yxnLfW0KghEeajjw&key=

-------------------------------------------
To get Playlists :

Get Playlists by Channel ID:
https://www.googleapis.com/youtube/v3/playlists?part=snippet,contentDetails&channelId=UCq-Fj5jknLsUf-MWSy4_brA&maxResults=50&key=

Get Playlists by Channel ID with pageToken:
https://www.googleapis.com/youtube/v3/playlists?part=snippet,contentDetails&channelId=UCq-Fj5jknLsUf-MWSy4_brA&maxResults=50&key=&pageToken=CDIQAA

------------------------------------------
To get PlaylistItems :

Get PlaylistItems list by PlayListId:
https://www.googleapis.com/youtube/v3/playlistItems?part=snippet,contentDetails&maxResults=25&playlistId=PLHFlHpPjgk70Yv3kxQvkDEO5n5tMQia5I&key=

-----------------------------------------
To get videos :

Get videos list by video id:
https://www.googleapis.com/youtube/v3/videos?part=snippet,contentDetails,statistics&id=YxLCwfA1cLw&key=

Get videos list by multiple videos id:
https://www.googleapis.com/youtube/v3/videos?part=snippet,contentDetails,statistics&id=YxLCwfA1cLw,Qgy6LaO3SB0,7yPJXGO2Dcw&key=

-----------------------------------------
Get comments list

Get Comment list by video ID:
https://www.googleapis.com/youtube/v3/commentThreads?part=snippet,replies&videoId=el****kQak&key=A**********k

Get Comment list by channel ID:
https://www.googleapis.com/youtube/v3/commentThreads?part=snippet,replies&channelId=U*****Q&key=AI********k

Get Comment list by allThreadsRelatedToChannelId:
https://www.googleapis.com/youtube/v3/commentThreads?part=snippet,replies&allThreadsRelatedToChannelId=UC*****ntcQ&key=AI*****k
 */

interface ApiService {

    @GET("search?")
    fun getAllVideos(
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

    @GET("playlistItems?")
    fun getPlaylistVideos(
            @Query("part") part: String = "snippet,contentDetails,id",
            @Query("order") order: String = "date",
            @Query("channelId") playlistId: String,
            @Query("maxResults") maxResults: Int = 50,
            @Query("pageToken") pageToken: String = ""
    ): Observable<VideoDTO>

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