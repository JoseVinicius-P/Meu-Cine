package com.jv.meusfilmes.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//Classe usada para armazenar dados dos filmes recuperados
public class Filme implements Serializable, Comparable<Filme> {

    private String poster_path, vote_average, title, release_date, backdrop_path, overview;
    private int id, runtime;
    private List<Genero> genres = new ArrayList<>();
    private List<CompanhiaProdutora> production_companies = new ArrayList<>();
    private boolean adult;

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public List<Genero> getGenres() {
        return genres;
    }

    public void setGenres(List<Genero> genres) {
        this.genres = genres;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public List<CompanhiaProdutora> getProduction_companies() {
        return production_companies;
    }

    public void setProduction_companies(List<CompanhiaProdutora> production_companies) {
        this.production_companies = production_companies;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public boolean getAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    //Metodo usado para ordenar a lista pelo titulo do filme
    @Override
    public int compareTo(Filme outro_filme) {
        return this.getTitle().compareTo(outro_filme.getTitle());
    }
}
