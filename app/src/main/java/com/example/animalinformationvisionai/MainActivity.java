package com.example.animalinformationvisionai;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;



public class MainActivity extends AppCompatActivity {
    private Button buscar;
    private ImageButton cameraMode;
    private EditText nomeAnimal;
    private LinearLayout historicoContainer;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "HistoricoPesquisa";
    private static final String KEY_HISTORICO = "Historico";
    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private static final int REQUEST_IMAGE_PICK = 101;
    private static final int REQUEST_PERMISSIONS = 200;
    private Uri photoURI;

    // Interface de callback para lidar com o resultado da tradução.
    public interface TraducaoCallback {
        // Chamado quando a tradução é bem-sucedida, passando o texto traduzido como parâmetro.
        void onTraduzido(String textoTraduzido);

        // Chamado em caso de erro durante a tradução, passando a exceção como parâmetro.
        void onErro(Exception e);
    }

// Recebe o texto original e um callback para notificar o resultado (sucesso ou erro).
    private void traduzirParaIngles(String textoOriginal, TraducaoCallback callback) {
        try {
            String url = "https://api.mymemory.translated.net/get?q="
                    + Uri.encode(textoOriginal)
                    + "&langpair=pt|en";

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            // Extrai a resposta.
                            JSONObject data = response.getJSONObject("responseData");

                            // Obtém o texto traduzido, converte para minúsculas e passa para o callback de sucesso.
                            String traducao = data.getString("translatedText").toLowerCase();
                            callback.onTraduzido(traducao);
                        } catch (JSONException e) {
                            callback.onErro(e);
                        }
                    },
                    error -> callback.onErro(error)
            );
            Volley.newRequestQueue(getApplicationContext()).add(request);

        } catch (Exception e) {
            callback.onErro(e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buscar = findViewById(R.id.btnBusca);
        cameraMode = findViewById(R.id.btnCamera);
        nomeAnimal = findViewById(R.id.animalBuscado);
        historicoContainer = findViewById(R.id.historicoContainer);
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        carregarHistorico();

        // Verifica se as permissões necessárias (câmera e leitura de armazenamento externo) foram concedidas.
        // Usa ContextCompat para compatibilidade com versões antigas do Android.
        // Se qualquer uma das permissões não estiver concedida (diferente de PERMISSION_GRANTED),
        // solicita as permissões ao usuário.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSIONS);
        }

        cameraMode.setOnClickListener(v -> showImagePickerDialog());

        buscar.setOnClickListener(v -> {
            String animalBuscado = nomeAnimal.getText().toString().trim();
            if (animalBuscado.isEmpty()) {
                nomeAnimal.setError("Digite o nome do animal");
                return;
            }
            traduzirParaIngles(animalBuscado, new TraducaoCallback() {
                @Override
                public void onTraduzido(String textoTraduzido) {
                    Intent intent = new Intent(MainActivity.this, TelaCarregamento.class);
                    intent.putExtra("Busca", textoTraduzido);
                    startActivity(intent);
                    salvarHistorico(animalBuscado);
                    nomeAnimal.setText("");
                }

                @Override
                public void onErro(Exception e) {
                    Intent intent = new Intent(MainActivity.this, TelaCarregamento.class);
                    intent.putExtra("Busca", animalBuscado.toLowerCase());
                    startActivity(intent);
                    salvarHistorico(animalBuscado);
                }
            });
        });


    }

    private void salvarHistorico(String animal) {
        // Obtém o Set existente de histórico da chave KEY_HISTORICO, ou cria um novo LinkedHashSet vazio se não existir.
        Set<String> historicoSet = sharedPreferences.getStringSet(KEY_HISTORICO, new LinkedHashSet<>());
        // Cria uma cópia do Set para modificações, preservando a ordem.
        Set<String> novoHistorico = new LinkedHashSet<>(historicoSet);
        // Verifica se o animal já existe no Set
        if (!novoHistorico.contains(animal)) {
            novoHistorico.add(animal);
            // Edita as SharedPreferences: coloca o novo Set na chave KEY_HISTORICO e aplica as mudanças de forma assíncrona
            sharedPreferences.edit().putStringSet(KEY_HISTORICO, novoHistorico).apply();
            adicionarBotaoHistorico(animal);
        }
    }
    private void carregarHistorico() {
        // Obtém o Set de histórico da chave KEY_HISTORICO, ou cria um novo LinkedHashSet vazio se não existir.
        Set<String> historicoSet = sharedPreferences.getStringSet(KEY_HISTORICO, new LinkedHashSet<>());

        // Cria uma cópia do Set para iteração, preservando a ordem.
        Set<String> historico = new LinkedHashSet<>(historicoSet);

        // Itera sobre cada animal no histórico
        for (String animal : historico) {
            adicionarBotaoHistorico(animal);
        }
    }

    private void adicionarBotaoHistorico(String animal) {
        Button botao = new Button(this);
        botao.setText(animal);
        // Desativa o comportamento padrão de transformar o texto em maiúsculas.
        botao.setAllCaps(false);
        botao.setTextColor(ContextCompat.getColor(this, R.color.marrom5));
        botao.setBackgroundColor(Color.parseColor("#96694b"));

        // Quando clicado, atualiza o texto de um EditText chamado nomeAnimal com o nome do animal.
        botao.setOnClickListener(v -> nomeAnimal.setText(animal));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(0, 8, 0, 8);
        // Aplica os parâmetros de layout ao botão.
        botao.setLayoutParams(params);
        // Adiciona o botão ao container LinearLayout (historicoContainer)
        historicoContainer.addView(botao);
    }

    private void abrirGaleria() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUEST_IMAGE_PICK);
    }


    // Processa imagens capturadas pela câmera ou selecionadas da galeria, convertendo-as em Bitmap e enviando para uma API
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return;

        // Declara uma variável para armazenar o Bitmap da imagem processada
        Bitmap bitmap = null;

        try {
            if (requestCode == REQUEST_IMAGE_CAPTURE && photoURI != null) {
                bitmap = getBitmapFromUri(photoURI); // captura e redimensiona a foto da câmera
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                // Obtém o URI da imagem selecionada dos dados da Intent.
                Uri imageUri = data.getData();

                // Obtém e redimensiona o Bitmap a partir do URI da imagem da galeria.
                bitmap = getBitmapFromUri(imageUri); // captura e redimensiona a imagem da galeria
            }

            if (bitmap != null) {
                // Envia o Bitmap para a API de visão para processamento
                enviarImagemParaAPI(bitmap); // envia para a API
            } else {
                Toast.makeText(this, "Não foi possível carregar a imagem", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao processar a imagem", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao enviar imagem para a API", Toast.LENGTH_SHORT).show();
        }
    }


    private void enviarImagemParaAPI(Bitmap bitmap) throws JSONException {
        // Cria um stream de bytes para armazenar a imagem comprimida.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);

        // Converte o stream para um array de bytes.
        byte[] imageBytes = baos.toByteArray();

        String base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

        // Cria um objeto JSON para representar a imagem, adicionando a propriedade "content" com a Base64.
        JsonObject image = new JsonObject();
        image.addProperty("content", base64Image);

        // Cria um objeto JSON para a feature de detecção: tipo "LABEL_DETECTION" e máximo de 5 resultados.
        JsonObject feature = new JsonObject();
        feature.addProperty("type", "LABEL_DETECTION");
        feature.addProperty("maxResults", 5);

        // Cria um array JSON de features e adiciona a feature criada.
        JsonArray features = new JsonArray();
        features.add(feature);

        // Cria um objeto JSON de request, adicionando a imagem e as features.
        JsonObject request = new JsonObject();
        request.add("image", image);
        request.add("features", features);

        // Cria um array JSON de requests e adiciona o request criado (a API permite múltiplos, mas aqui usa um).
        JsonArray requestsArray = new JsonArray();
        requestsArray.add(request);

        // Cria o corpo da requisição JSON, adicionando o array de requests.
        JsonObject requestBody = new JsonObject();
        requestBody.add("requests", requestsArray);

        // Define a URL da API Google Vision para anotação de imagens, incluindo a chave de API hardcoded.
        String url = "https://vision.googleapis.com/v1/images:annotate?key=AIzaSyCVjKr1Zbd-TTCTxpq0pPQ76iLXGXP_w6Q";

        // Cria uma requisição POST JSON usando Volley, passando a URL, o corpo como JSONObject (convertido de String),
        // listeners para sucesso e erro.
        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                new JSONObject(requestBody.toString()),
                response -> {  // Listener de sucesso: processa a resposta da API.
                    try {
                        // Extrai o primeiro objeto de labelAnnotations da resposta (assume que há pelo menos um).
                        // Navega pelo JSON: responses[0].labelAnnotations[0].
                        JSONObject label = response
                                .getJSONArray("responses")
                                .getJSONObject(0)
                                .getJSONArray("labelAnnotations")
                                .getJSONObject(0);

                        // Obtém a descrição da imagem
                        String descricao = label.getString("description");

                        // Salva a descrição no histórico (Daria para traduzir mas ficaria mais extenso)
                        salvarHistorico(descricao);

                        // Cria uma Intent para iniciar a TelaCarregamento, passando a descrição em minúsculas
                        Intent intent = new Intent(MainActivity.this, TelaCarregamento.class);
                        intent.putExtra("Busca", descricao.toLowerCase());
                        startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Nenhum objeto identificado", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Erro na requisição da API", Toast.LENGTH_SHORT).show();
                }
        );
        Volley.newRequestQueue(this).add(jsonRequest);
    }

    //Opção de escolha
    private void showImagePickerDialog() {
        String[] options = {"Câmera", "Galeria"};
        new AlertDialog.Builder(this)
                .setTitle("Escolha uma opção")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        abrirCamera();
                    } else {
                        abrirGaleria();
                    }
                }).show();
    }

    private void abrirCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                File storageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "CameraImages");
                if (!storageDir.exists()) storageDir.mkdirs();

                File photoFile = new File(storageDir, "captured_photo.jpg");
                photoURI = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Erro ao criar o arquivo da foto", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Câmera não disponível", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        Bitmap original = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        // Redimensiona para largura 1024 mantendo proporção
        int maxWidth = 1024;
        int width = original.getWidth();
        int height = original.getHeight();
        if (width > maxWidth) {
            float ratio = (float) height / (float) width;
            width = maxWidth;
            height = (int) (maxWidth * ratio);
            original = Bitmap.createScaledBitmap(original, width, height, true);
        }
        return original;
    }

}
