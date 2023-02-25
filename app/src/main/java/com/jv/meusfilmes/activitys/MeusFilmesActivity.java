package com.jv.meusfilmes.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.jv.meusfilmes.R;
import com.jv.meusfilmes.adapters.AdapterFilmes;
import com.jv.meusfilmes.api.TmdbFilme;
import com.jv.meusfilmes.dao.FilmeDAO;
import com.jv.meusfilmes.models.Filme;
import com.jv.meusfilmes.utilities.CheckConnection;
import com.jv.meusfilmes.utilities.RecyclerItemClickListener;

import java.util.List;

public class MeusFilmesActivity extends AppCompatActivity {

    //Componentes da tela
    private FloatingActionButton fab_add_filme;

    private FilmeDAO filme_dao;
    private RecyclerView rv_meus_filmes;
    private AdapterFilmes adapter_filmes;
    private RecyclerView.LayoutManager layoutManager;
    private TextView tv_sem_filmes;
    private ProgressBar progressBar;
    private ConstraintLayout full_container;

    private Snackbar snackbar_connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_filmes);

        inicializarComponentes();
        addListeners();
        verificarConexao();
        buscarMeusFilmes(filme_dao.getIdsFilmes());
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        verificarConexao();
        buscarMeusFilmes(filme_dao.getIdsFilmes());
    }

    private void inicializarComponentes(){
        fab_add_filme = findViewById(R.id.fab_add_filme);
        filme_dao = new FilmeDAO(this);
        rv_meus_filmes = findViewById(R.id.rv_meus_filmes);
        //Criando mananger para o recycler view
        layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        rv_meus_filmes.setLayoutManager(layoutManager);
        rv_meus_filmes.setHasFixedSize(true);
        tv_sem_filmes = findViewById(R.id.tv_sem_filmes);
        progressBar = findViewById(R.id.progressBarLayout);
        full_container = findViewById(R.id.full_container);
        snackbar_connection = Snackbar.make(full_container,
                "Verifique sua conexão",
                Snackbar.LENGTH_INDEFINITE);
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
                                filme_dao.apagarFilmeLista(adapter_filmes.getIdFilme(position));
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

    private void abrirTelaListaFilmes(){
        Intent lista_filmes = new Intent(this, ListaFilmesActivity.class);
        startActivity(lista_filmes);
    }

    private void buscarMeusFilmes(List<Integer> ids_filmes){
        tv_sem_filmes.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        limparListaFilmes();
        if(ids_filmes != null){
            verificarInternet();
            TmdbFilme tmdbFilme = new TmdbFilme(this);
            tmdbFilme.getFilmesPorId(ids_filmes, this);
        }else{
            progressBar.setVisibility(View.GONE);
            tv_sem_filmes.setVisibility(View.VISIBLE);
        }

    }

    private void verificarInternet(){
        //Inicia timer que verificará se existe conexão daqui 5 segundos, se não houver uma mensagem será exibida
        CheckConnection.verificarInternet(() -> {
            if(!CheckConnection.isInternet()) {
                if(!snackbar_connection.isShown()){
                    snackbar_connection.show();
                }
                buscarMeusFilmes(filme_dao.getIdsFilmes());
            }else{
                if (snackbar_connection != null)
                    snackbar_connection.dismiss();
            }
        });
    }

    private void verificarConexao(){
        if(!CheckConnection.verificarConexao(this)){
            snackbar_connection.show();
        };
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