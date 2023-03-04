package com.jv.meusfilmes.api;

import android.content.Context;

import androidx.annotation.NonNull;

import com.jv.meusfilmes.R;
import com.jv.meusfilmes.activitys.DetalheFilmeActivity;
import com.jv.meusfilmes.activitys.ListaFilmesActivity;
import com.jv.meusfilmes.activitys.MeusFilmesActivity;
import com.jv.meusfilmes.interfaces.TmdbService;
import com.jv.meusfilmes.models.ConjuntoFilmes;
import com.jv.meusfilmes.models.Filme;
import com.jv.meusfilmes.utilities.CheckConnection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TmdbFilme {
    private Retrofit retrofit;
    private String baseUrl = "https://api.themoviedb.org/3/";
    private ConjuntoFilmes conjuntoFilmes =  new ConjuntoFilmes();
    private Filme filme = new Filme();
    private List<Filme> filmes = new ArrayList<>();
    private String apiKey;

    private static Call<?> current_call;

    public TmdbFilme(Context context) {
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiKey = context.getResources().getString(R.string.api_key);

    }

    //Retorna filmes populares
    public void getFilmesPopulares(/*Para acessar o metodo de mostrar*/ListaFilmesActivity listaFilmesActivity, int page){
        //Configuração do retrofit
        TmdbService tmdb_service = retrofit.create(TmdbService.class);
        Call<ConjuntoFilmes> call = tmdb_service.getFilmesPopulares(apiKey, page);


        call.enqueue(new Callback<ConjuntoFilmes>() {
            @Override
            public void onResponse(@NonNull Call<ConjuntoFilmes> call, @NonNull Response<ConjuntoFilmes> response) {
                if (response.isSuccessful()){
                    conjuntoFilmes = response.body();

                    List<Filme> filmes = null;
                    if (conjuntoFilmes != null) {
                        filmes = conjuntoFilmes.getFilmes();
                    }
                    CheckConnection.setIs_internet(true);
                    listaFilmesActivity.exibirFilmes(filmes);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ConjuntoFilmes> call, @NonNull Throwable t) {
                System.out.println("Erro: " + t);
            }
        });
    }

    //Usado para fazer pesquisa de um filme
    public void pesquisarFilmes(/*Para acessar o metodo de mostrar*/ListaFilmesActivity listaFilmesActivity, int page, String query){
        //Configuração do retrofit
        TmdbService tmdb_service = retrofit.create(TmdbService.class);
        Call<ConjuntoFilmes> call = tmdb_service.pesquisarFilmes(apiKey, page, query);


        call.enqueue(new Callback<ConjuntoFilmes>() {
            @Override
            public void onResponse(@NonNull Call<ConjuntoFilmes> call, @NonNull Response<ConjuntoFilmes> response) {
                if (response.isSuccessful()){
                    conjuntoFilmes = response.body();
                    List<Filme> filmes = null;
                    if (conjuntoFilmes != null) {
                        filmes = conjuntoFilmes.getFilmes();
                    }

                    //Caso não seja encontrado nenhum filme, envia um null ao metodo para este fazer o tratamento necessário
                    if(filmes != null && filmes.size() != 0)
                        listaFilmesActivity.exibirFilmes(filmes);
                    else
                        listaFilmesActivity.exibirFilmes(null);

                    CheckConnection.setIs_internet(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ConjuntoFilmes> call, @NonNull Throwable t) {
                System.out.println("Erro: " + t);
            }
        });
    }

    //Recebe todos os ids da lista do usuario e passa para o metodo getFilme
    public void getFilmesPorId(List<Integer> id_filmes, MeusFilmesActivity meusFilmesActivity){
        int listaFilmesSize = id_filmes.size();
        for (int i = 0; i < listaFilmesSize; i++) {
            int id_filme = id_filmes.get(i);
            getFilme(id_filme, listaFilmesSize, meusFilmesActivity);
        }
    }

    //Recebe o parametro size para verificar quando todos os filmes foram recuperados e passar para activity exibir
    //Metodo usado para pesquisar individualmente filmes savos pelo usuario
    private void getFilme(int id_filme, int listaFilmesSize, MeusFilmesActivity meusFilmesActivity){
        //Configuração do retrofit
        TmdbService tmdb_service = retrofit.create(TmdbService.class);
        Call<Filme> call = tmdb_service.getFilme(id_filme, apiKey);

        call.enqueue(new Callback<Filme>() {
            @Override
            public void onResponse(@NonNull Call<Filme> call, @NonNull Response<Filme> response) {
                if (response.isSuccessful()){
                    filme = response.body();
                    filmes.add(filme);
                    //Verifica se todos os filmes foram recuperados
                    //e envia lista para activity exibir
                    if (filmes.size() == listaFilmesSize){
                        CheckConnection.setIs_internet(true);
                        Collections.sort(filmes);
                        meusFilmesActivity.exibirFilmes(filmes);
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<Filme> call, @NonNull Throwable t) {

            }
        });
    }

    public void getFilme(int id_filme, Callback<Filme> callback){
        //Configuração do retrofit
        TmdbService tmdb_service = retrofit.create(TmdbService.class);
        Call<Filme> call = tmdb_service.getFilme(id_filme, apiKey);
        current_call = call;

        call.enqueue(callback);
    }

    public void getFilmesSimilares(int id_filme, DetalheFilmeActivity detalhe_filme_activity){
        //Configuração do retrofit
        TmdbService tmdb_service = retrofit.create(TmdbService.class);
        Call<ConjuntoFilmes> call = tmdb_service.getFilmesSimilares(id_filme, apiKey);

        call.enqueue(new Callback<ConjuntoFilmes>() {
            @Override
            public void onResponse(@NonNull Call<ConjuntoFilmes> call, @NonNull Response<ConjuntoFilmes> response) {
                if (response.isSuccessful()){
                    conjuntoFilmes = response.body();
                    List<Filme> filmes = null;
                    if (conjuntoFilmes != null) {
                        filmes = conjuntoFilmes.getFilmes();
                    }
                    detalhe_filme_activity.exibirFilmesSimilares(filmes);
                    CheckConnection.setIs_internet(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ConjuntoFilmes> call, @NonNull Throwable t) {

            }
        });
    }

    public static void cancelCurrentCall(){
        if (current_call != null){
            current_call.cancel();
        }
    }
}
