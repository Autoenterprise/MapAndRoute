package com.velychko.kyrylo.mapandroute.data.network;

import com.velychko.kyrylo.mapandroute.data.network.PlaceDataModel.JSONResult;
import com.velychko.kyrylo.mapandroute.data.network.RouteDataModel.RouteResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static android.R.attr.type;

public class RetrofitGoogleMap {

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/";
    private static ApiInterface apiInterface;

//    public static final String API_KEY = "AIzaSyCEV9kV5xiiPIQc4B6q11YmrmHHGnkx0Wg";
    public static final String API_KEY = "AIzaSyDviI42XVE6KJPtF4vz6TANYsGrz83OJ4c";


    interface ApiInterface {
        @GET("place/nearbysearch/json")
        Call<JSONResult> getPlacesByType(@Query("location") String location,
                             @Query("types") String types,
                             @Query("radius") String radius,
                             @Query("key") String key);

        @GET("place/textsearch/json")
        Call<JSONResult> getPlacesByQuery(@Query("location") String location,
                              @Query("query") String types,
                              @Query("radius") String radius,
                              @Query("language") String language,
                              @Query("key") String key);


        @GET("directions/json")
        Call<RouteResponse> getRoute(@Query(value = "origin") String origin,
                      @Query(value = "destination") String destination,
                      @Query("language") String language);
    }

    static {
        initialize();
    }

    private static void initialize() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiInterface = retrofit.create(ApiInterface.class);
    }

    public static Call<JSONResult> getPlacesByType(double lat, double lng, String type, String radius) {
        String strLocation = String.valueOf(lat) + ',' + String.valueOf(lng);
        type = "" + type;
        return apiInterface.getPlacesByType(strLocation,
                type,
                String.valueOf(radius),
                API_KEY);
    }

    public static Call<JSONResult> getPlacesByQuery(double lat, double lng, String query, String radius) {
        String strLocation = String.valueOf(lat) + ',' + String.valueOf(lng);
        return apiInterface.getPlacesByQuery(strLocation,
                query,
                String.valueOf(radius),
                "ru",
                API_KEY);
    }

    public static Call<RouteResponse> getRoute(String origin, String destination) {
        return apiInterface.getRoute(origin,
                destination,
                "ru");
    }
}
