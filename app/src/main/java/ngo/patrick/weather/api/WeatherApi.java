package ngo.patrick.weather.api;

import ngo.patrick.weather.model.CityConditionsResponse;
import ngo.patrick.weather.model.CityCurrentWeather;
import ngo.patrick.weather.model.CityList;
import ngo.patrick.weather.model.Result;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Weather API: contains all http calls needed, using Retrofit 
 */
public interface WeatherApi
{
    String API_KEY = "909c3f2a60c4336a/";

    @GET("aq")
    Call<CityList> getAllCities(
            @Query("query") String query);


    @GET("conditions/q/zmw:{zmw}.json")
    Call<CityConditionsResponse> getCityConditions(
            @Path("zmw") String zmw);


    public static final Retrofit conditions = new Retrofit.Builder()
        .baseUrl("http://api.wunderground.com/api/" + API_KEY)
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    public static final Retrofit autocomplete = new Retrofit.Builder()
            .baseUrl("http://autocomplete.wunderground.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}