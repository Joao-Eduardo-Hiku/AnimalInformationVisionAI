package com.example.animalinformationvisionai;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class TelaCarregamento extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela_carregamento);

        // Faz a imagem girar
        ImageView imagem = findViewById(R.id.imagem_carregamento);
        RotateAnimation girar = new RotateAnimation(
                0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        girar.setDuration(2000);
        girar.setRepeatCount(Animation.INFINITE);
        imagem.startAnimation(girar);

        String animalBuscado = getIntent().getStringExtra("Busca");

        // Espera 10 segundos
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(TelaCarregamento.this, TelaResultado.class);
                intent.putExtra("Busca", animalBuscado);
                startActivity(intent);
                finish();
            }
        }, 10000);
    }
}
