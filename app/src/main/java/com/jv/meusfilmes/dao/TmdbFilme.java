package com.jv.meusfilmes.dao;

import android.app.Activity;

import com.jv.meusfilmes.activitys.DetalheFilmeActivity;
import com.jv.meusfilmes.activitys.ListaFilmesActivity;
import com.jv.meusfilmes.activitys.MeusFilmesActivity;
import com.jv.meusfilmes.interfaces.TmdbService;
import com.jv.meusfilmes.models.ConjuntoFilmes;
import com.jv.meusfilmes.models.Filme;

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


    public TmdbFilme() {
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    //Retorna filmes populares
    public void getFilmesPopulares(/*Para acessar o metodo de mostrar*/ListaFilmesActivity listaFilmesActivity, int page){
        //Configuração do retrofit
        TmdbService tmdb_service = retrofit.create(TmdbService.class);
        Call<ConjuntoFilmes> call = tmdb_service.getFilmesPopulares(page);


        call.enqueue(new Callback<ConjuntoFilmes>() {
            @Override
            public void onResponse(Call<ConjuntoFilmes> call, Response<ConjuntoFilmes> response) {
                if (response.isSuccessful()){
                    conjuntoFilmes = response.body();
                    List<Filme> filmes = conjuntoFilmes.getFilmes();
                    listaFilmesActivity.exibirFilmes(filmes);
                }
            }

            @Override
            public void onFailure(Call<ConjuntoFilmes> call, Throwable t) {
                System.out.println("Erro: " + t);
            }
        });
    }

    //Usado para fazer pesquisa de um filme
    public void pesquisarFilmes(/*Para acessar o metodo de mostrar*/ListaFilmesActivity listaFilmesActivity, int page, String query){
        //Configuração do retrofit
        TmdbService tmdb_service = retrofit.create(TmdbService.class);
        Call<ConjuntoFilmes> call = tmdb_service.pesquisarFilmes(page, query);


        call.enqueue(new Callback<ConjuntoFilmes>() {
            @Override
            public void onResponse(Call<ConjuntoFilmes> call, Response<ConjuntoFilmes> response) {
                if (response.isSuccessful()){
                    conjuntoFilmes = response.body();
                    List<Filme> filmes = conjuntoFilmes.getFilmes();

                    //Caso não seja encontrado nenhum filme, envia um null ao metodo para este fazer o tratamento necessário
                    if(filmes.size() != 0)
                        listaFilmesActivity.exibirFilmes(filmes);
                    else
                        listaFilmesActivity.exibirFilmes(null);
                }
            }

            @Override
            public void onFailure(Call<ConjuntoFilmes> call, Throwable t) {
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
        Call<Filme> call = tmdb_service.getFilme(id_filme);

        call.enqueue(new Callback<Filme>() {
            @Override
            public void onResponse(Call<Filme> call, Response<Filme> response) {
                if (response.isSuccessful()){
                    filme = response.body();
                    filmes.add(filme);

                    //Verifica se todos os filmes foram recuperados
                    //e envia lista para activity exibir
                    if (filmes.size() == listaFilmesSize){
                        Collections.sort(filmes);
                        meusFilmesActivity.exibirFilmes(filmes);
                    }

                }
            }

            @Override
            public void onFailure(Call<Filme> call, Throwable t) {
                System.out.println("Erro: " + t);
            }
        });
    }

    public void getFilme(int id_filme, Callback<Filme> callback){
        //Configuração do retrofit
        TmdbService tmdb_service = retrofit.create(TmdbService.class);
        Call<Filme> call = tmdb_service.getFilme(id_filme);

        call.enqueue(callback);
    }

    public void getFilmesSimilares(int id_filme, DetalheFilmeActivity detalhe_filme_activity){
        //Configuração do retrofit
        TmdbService tmdb_service = retrofit.create(TmdbService.class);
        Call<ConjuntoFilmes> call = tmdb_service.getFilmesSimilares(id_filme);

        call.enqueue(new Callback<ConjuntoFilmes>() {
            @Override
            public void onResponse(Call<ConjuntoFilmes> call, Response<ConjuntoFilmes> response) {
                if (response.isSuccessful()){
                    conjuntoFilmes = response.body();
                    List<Filme> filmes = conjuntoFilmes.getFilmes();
                    detalhe_filme_activity.exibirFilmesSimilares(filmes);
                }
            }

            @Override
            public void onFailure(Call<ConjuntoFilmes> call, Throwable t) {

            }
        });
    }
}
