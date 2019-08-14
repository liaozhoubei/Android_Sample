package com.example.example;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface Service {
    @FormUrlEncoded
    @POST("modify")
    Call<Person> modifyPerson(@Field("name") String name);

    @GET("person/{id}")
    Call<Person> fetchPersonById(@Path("id") int id);
}
