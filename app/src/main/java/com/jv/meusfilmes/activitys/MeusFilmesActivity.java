package com.jv.meusfilmes.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jv.meusfilmes.R;
import com.jv.meusfilmes.adapters.AdapterFilmes;
import com.jv.meusfilmes.api.TmdbFilme;
import com.jv.meusfilmes.models.Filme;
import com.jv.meusfilmes.utilities.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class MeusFilmesActivity extends AppCompatActivity {

    //Componentes da tela
    private FloatingActionButton fab_add_filme;
    private SharedPreferences shared_preferences_filmes;
    private SharedPreferences.Editor editor;
    private RecyclerView rv_meus_filmes;
    private AdapterFilmes adapter_filmes;
    private RecyclerView.LayoutManager layoutManager;
    private TextView tv_sem_filmes;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_filmes);

        inicializarComponentes();
        addListeners();
        buscarMeusFilmes(getIdsFilmes());
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        buscarMeusFilmes(getIdsFilmes());
    }

    private void inicializarComponentes(){
        fab_add_filme = findViewById(R.id.fab_add_filme);
        shared_preferences_filmes = getSharedPreferences("MinhaListaFilmes", MODE_PRIVATE);
        editor = shared_preferences_filmes.edit();
        rv_meus_filmes = findViewById(R.id.rv_meus_filmes);
        //Criando mananger para o recycler view
        layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        rv_meus_filmes.setLayoutManager(layoutManager);
        rv_meus_filmes.setHasFixedSize(true);
        tv_sem_filmes = findViewById(R.id.tv_sem_filmes);
        progressBar = findViewById(R.id.progressBarLayout);
    }

    private void addListeners(){
        fab_add_filme.setOnClickListener(view -> abrirTelaListaFilmes());

        rv_meus_filmes.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        rv_meus_filmes,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                abrirTelaDetalheFilme(adapter_filmes.getListaFilmes().get(position));
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                apagarFilmeLista(position);
                                adapter_filmes.removerFilme(position);
                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            }
                        })
        );

    }

    private void abrirTelaDetalheFilme(Filme filme){
        Intent detalhe_filme = new Intent(this, DetalheFilmeActivity.class);
        detalhe_filme.putExtra("filme", filme);
        startActivity(detalhe_filme);
    }

    private void apagarFilmeLista(int position){
        List<Integer> ids_filmes_local = getIdsFilmes();
        int id_filme = adapter_filmes.getIdFilme(position);
        if(ids_filmes_local != null && ids_filmes_local.size() > 0){
            for (int i = 0; i < ids_filmes_local.size(); i++) {
                if(id_filme == ids_filmes_local.get(i)){
                    ids_filmes_local.remove(i);
                }
            }
            editor.clear();
            if (editor.commit()){
                salvarListaFilmes(ids_filmes_local);
            }
        }
    }

    private void salvarListaFilmes(List<Integer> ids_filmes){
        if(ids_filmes.size() > 0){
            for (int i = 1; i <= ids_filmes.size(); i++) {
                String chave_filme = "filme_" + i;
                editor.putInt(chave_filme, ids_filmes.get(i-1));
            }
            editor.apply();
        }else{
            rv_meus_filmes.setVisibility(View.GONE);
            tv_sem_filmes.setVisibility(View.VISIBLE);
        }

    }

    private void abrirTelaListaFilmes(){
        Intent lista_filmes = new Intent(this, ListaFilmesActivity.class);
        startActivity(lista_filmes);
    }

    private List<Integer> getIdsFilmes(){
        List<Integer> ids_filmes = new ArrayList<>();
        int qt_filmes = shared_preferences_filmes.getAll().size();
        //Se lista de filmes estiver vazia uma mensagem será exibida
        if (qt_filmes > 0){
            for(int i = 1; i <= qt_filmes; i++){
                String chave_filme = "filme_" + i;
                ids_filmes.add(shared_preferences_filmes.getInt(chave_filme, 0));
            }
        }else {
            tv_sem_filmes.setVisibility(View.VISIBLE);
        }

        //Retonará nulo para fazer tratamento no outro metodo
        if (ids_filmes.size() > 0)
            return ids_filmes;
        else
            return null;
    }

    private void buscarMeusFilmes(List<Integer> ids_filmes){
        tv_sem_filmes.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        limparListaFilmes();
        if(ids_filmes != null){
            TmdbFilme tmdbFilme = new TmdbFilme(this);
            tmdbFilme.getFilmesPorId(ids_filmes, this);
        }else{
            progressBar.setVisibility(View.GONE);
            tv_sem_filmes.setVisibility(View.VISIBLE);
        }

    }

    public void exibirFilmes(List<Filme> filmes){
        //Caso não seja recebido nenhum filme um texto será exibido
        if(filmes != null){
            //Só por garantia este textview será escondido aqui
            tv_sem_filmes.setVisibility(View.GONE);
            adapter_filmes = new AdapterFilmes(filmes, this);
            rv_meus_filmes.setAdapter(adapter_filmes);

        }else{
            tv_sem_filmes.setVisibility(View.VISIBLE);
        }

        progressBar.setVisibility(View.GONE);

    }

    private void limparListaFilmes(){
        if(adapter_filmes != null)
            adapter_filmes.clear();
        adapter_filmes = null;
    }


}