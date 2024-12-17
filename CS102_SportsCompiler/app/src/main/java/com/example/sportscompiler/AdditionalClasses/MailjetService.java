package com.example.sportscompiler.AdditionalClasses;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface MailjetService
{
    @Headers("Content-Type: application/json")
    @POST("v3.1/send")
    Call<Object> sendEmail(@Body MailjetEmailRequest requestBody);

}
