package com.odie.flickfinder.Networking;

import org.json.JSONObject;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * Created by Odie on 17/08/2018.
 */
public interface TracktTv {

    @Headers({
            "trakt-api-version: 2",
            "trakt-api-key: a5d045a98f692bd7e54cd3b01021a0b13573fa61ebb5c7af22e699401077529a"
    })
    @GET("shows/{title}?extended=full")
    Call<ResponseBody> searchTitle(@Path("title") String title);

    @Headers({
            "trakt-api-version: 2",
            "trakt-api-key: a5d045a98f692bd7e54cd3b01021a0b13573fa61ebb5c7af22e699401077529a"
    })
    @GET("shows/{title}/seasons?extended=full")
    Call<ResponseBody> getSeasonData(@Path("title") String title);

    @Headers({
            "trakt-api-version: 2",
            "trakt-api-key: a5d045a98f692bd7e54cd3b01021a0b13573fa61ebb5c7af22e699401077529a"
    })
    @GET("shows/{title}/seasons/{seasonNumber}/episodes/{episodeNumber}?extended=full")
    Call<ResponseBody> getEpisodeData(@Path("title") String title, @Path("seasonNumber") int seasonNumber,
                                   @Path("episodeNumber") int episodeNumber);

}
