package com.example.animalinformationvisionai;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TelaResultado extends AppCompatActivity {

    private Retrofit retrofit;
    private RecyclerView recyclerView;
    private Adapter adapter;
    private Animal animalAPI;
    private ImageButton chatBot;
    private TextView mensagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.tela_resultado);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String nomeBuscado = getIntent().getStringExtra("Busca");
        if (nomeBuscado != null) {
            buscarAnimal(nomeBuscado);
        } else {
            Toast.makeText(this, "Nenhum uma palavra recebida", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void buscarAnimal(String nomeAnimal) {
        if (retrofit == null) {
            String url = "https://api.api-ninjas.com/v1/";
            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            animalAPI = retrofit.create(Animal.class);
        }

        String apiKey = "aCn+wLFvmh7uUVIvtSThAg==YAo0YM0dVfmPm7bR";

        Call<List<AnimalInfos>> call = animalAPI.getAnimal(apiKey, nomeAnimal);

        call.enqueue(new Callback<List<AnimalInfos>>() {
            @Override
            public void onResponse(Call<List<AnimalInfos>> call, Response<List<AnimalInfos>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AnimalInfos> animais = response.body();
                    if (!animais.isEmpty()) {
                        adapter = new Adapter(animais);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(TelaResultado.this,
                                "Nenhum animal encontrado para '" + nomeAnimal + "'",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<List<AnimalInfos>> call, Throwable t) {
                Toast.makeText(TelaResultado.this,
                        "Erro de conexão: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }


}
