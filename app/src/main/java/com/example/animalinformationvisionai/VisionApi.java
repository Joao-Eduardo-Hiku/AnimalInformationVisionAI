package com.example.animalinformationvisionai;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface VisionApi {
    @POST("v1/images:annotate?key=YOUR_API_KEY")
    Call<JsonObject> analyzeImage(@Body JsonObject requestBody);
}
