package com.odie.flickfinder.Networking;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * Created by Odie on 18/08/2018.
 */
public interface FanartTv {

    @Headers({
            "api-key: f28039dd64d896b8e5c5b63e4c3e1ee4"
    })
    @GET("tv/{id}")
    Call<ResponseBody> searchImages(@Path("id") String id);

}
