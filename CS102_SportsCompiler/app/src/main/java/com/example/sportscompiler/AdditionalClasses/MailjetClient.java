package com.example.sportscompiler.AdditionalClasses;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MailjetClient {
    private static final String BASE_URL = "https://api.mailjet.com/";
    private static final String API_KEY = "0c1c5c042d1b6299ca4d12a2567fb30b";
    private static final String API_SECRET = "dca354521b50f1ca37285671201d04c0";
    private static MailjetService mailjetService;

    public static MailjetService getInstance()
    {
        if (mailjetService == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .addInterceptor(chain -> chain.proceed(chain.request().newBuilder()
                            .header("Authorization", Credentials.basic(API_KEY, API_SECRET))
                            .build()))
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();

            mailjetService = retrofit.create(MailjetService.class);
        }
        return mailjetService;
    }
}



