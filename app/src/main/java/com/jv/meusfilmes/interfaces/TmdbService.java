package com.jv.meusfilmes.interfaces;

import com.jv.meusfilmes.models.ConjuntoFilmes;
import com.jv.meusfilmes.models.Filme;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

//interface nessária para funcionamento do retrofit
public interface TmdbService {

    //Ultilzando o Query para passar page como um parametro na URL (page=N)
    @GET("movie/popular?language=pt-BR")
    Call<ConjuntoFilmes> getFilmesPopulares(@Query("api_key") String api_key, @Query("page") int page);

    @GET("movie/{id_filme}/recommendations?language=pt-BR&page=1")
    Call<ConjuntoFilmes> getFilmesSimilares(@Path("id_filme") int id_filme, @Query("api_key") String api_key);

    @GET("search/movie?language=pt-BR&include_adult=false")
    Call<ConjuntoFilmes> pesquisarFilmes(@Query("api_key") String api_key, @Query("page") int page, @Query("query") String query);

    //Metodo usado para obter um filem de cada vez
    @GET("movie/{id_filme}?language=pt-BR")
    Call<Filme> getFilme(@Path("id_filme") int id_filme, @Query("api_key") String api_key);
}
