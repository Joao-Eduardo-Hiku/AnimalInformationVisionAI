package com.example.animalinformationvisionai;


import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.List;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;


public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    private List<AnimalInfos> animalList;
    public Adapter(List<AnimalInfos> animalList) {
        this.animalList = animalList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tela_recycle_view, parent, false);
        return new MyViewHolder(view);
    }

    private void TraduzirInformacoes(Context context, String textoOriginal, TextView destino, String dado) {
        try {
            String url = "https://api.mymemory.translated.net/get?q="
                    + Uri.encode(textoOriginal)
                    + "&langpair=en|pt";

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            // Registra a resposta completa no log para depuração (nível debug).
                            Log.d("TraducaoAPI", "Resposta: " + response.toString());

                            // Extrai a tradução
                            JSONObject data = response.getJSONObject("responseData");

                            // Obtém o texto traduzido
                            String traducao = data.getString("translatedText");

                            // Atualiza o TextView recebido
                            destino.setText(dado + traducao);
                        } catch (JSONException e) {
                            // Em caso de erro no parsing, usa o texto original
                            destino.setText(dado + textoOriginal);

                            e.printStackTrace();
                        }
                    },
                    error -> {
                        // Em caso de erro na tradução, usa o texto original
                        destino.setText(dado + textoOriginal);

                        // Registra o erro no log
                        Log.e("TraducaoAPI", "Erro: " + error.toString());
                    }
            );
            Volley.newRequestQueue(context).add(request);

        } catch (Exception e) {  // Captura exceções gerais que possam ocorrer fora da requisição
            // Em caso de exceção geral, usa o texto original
            destino.setText("Nome: " + textoOriginal);

            e.printStackTrace();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AnimalInfos animal = animalList.get(position);


        if (animal.getName() != null) {
            TraduzirInformacoes(holder.itemView.getContext(), animal.getName(), holder.textNome, "Nome:");
        } else {
            holder.textNome.setText("Nome não disponível");
        }

        if (animal.getKingdom() != null) {
            holder.textReino.setText("Reino: " + animal.getKingdom());
        } else {
            holder.textReino.setText("Reino não disponível");
        }

        if (animal.getPhylum() != null) {
            holder.textFilo.setText("Filo: " + animal.getPhylum());
        } else {
            holder.textFilo.setText("Filo não disponível");
        }

        if (animal.getOrder() != null) {
            holder.textOrdem.setText("Ordem: " + animal.getOrder());
        } else {
            holder.textOrdem.setText("Ordem não disponível");
        }

        if (animal.getFamily() != null) {
            holder.textFamilia.setText("Familia: " + animal.getFamily());
        } else {
            holder.textFamilia.setText("Familia não disponível");
        }

        if (animal.getScientific_name() != null) {
            holder.textNomeCientifico.setText("Nome Cientifico: " + animal.getScientific_name());
        } else {
            holder.textNomeCientifico.setText("Nome Cientifico não disponível");
        }

        if (animal.getLocations() != null && !animal.getLocations().isEmpty()) {
            // transforma a lista em uma string separada por vírgula
            String locais = String.join(", ", animal.getLocations());
            holder.textLocais.setText("Locais: " + locais);
        } else {
            holder.textLocais.setText("Locais não disponível");
        }

        if (animal.getPrey() != null) {
            TraduzirInformacoes(holder.itemView.getContext(), animal.getPrey(), holder.textPresa, "Presa:");
        } else {
            holder.textPresa.setText("Presa não disponível");
        }



    }

    @Override
    public int getItemCount() {
        if (animalList != null) {
            return animalList.size();
        } else {
            return 0;
        }
    }

    public void updateList(List<AnimalInfos> newList) {
        animalList = newList;
        notifyDataSetChanged(); // Avisa ao RecyclerView para se atualizar
    }
    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textNome, textReino, textFilo,
                textOrdem, textFamilia, textNomeCientifico, textLocais, textPresa;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textNome  = itemView.findViewById(R.id.Nome);
            textReino  = itemView.findViewById(R.id.Reino);
            textFilo  = itemView.findViewById(R.id.Filo);
            textOrdem  = itemView.findViewById(R.id.Ordem);
            textFamilia  = itemView.findViewById(R.id.Familia);
            textNomeCientifico  = itemView.findViewById(R.id.NomeCientifico);
            textLocais  = itemView.findViewById(R.id.Locais);
            textPresa  = itemView.findViewById(R.id.Presa);

        }
    }
}
