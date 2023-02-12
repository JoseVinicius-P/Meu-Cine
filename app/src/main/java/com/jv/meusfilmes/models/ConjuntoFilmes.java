package com.jv.meusfilmes.models;

import java.util.ArrayList;
import java.util.List;

//Esta classe é usada pq o TMDB retorna um objeto que contem o array de filme dentro dele
public class ConjuntoFilmes {

    private List<Filme> results = new ArrayList<>();

    public List<Filme> getFilmes() {
        return results;
    }

    public void setFilmes(List<Filme> filmes) {
        this.results = filmes;
    }
}
