package com.example.animalinformationvisionai;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface Animal {
    @GET("animals")
    Call<List<AnimalInfos>> getAnimal(
            @Header("X-Api-Key") String apiKey,
            @Query("name") String nomeAnimal
    );
}

