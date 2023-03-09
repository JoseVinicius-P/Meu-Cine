package com.jv.meusfilmes.activitys;

import androidx.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class MeusFilmesActivity extends AppCompatActivity {

    //Componentes da tela
    private FloatingActionButton fab_add_filme;

    private FilmeDAO filme_dao;
    private RecyclerView rv_meus_filmes;
    private AdapterFilmes adapter_filmes;
    private RecyclerView.LayoutManager layoutManager;
    //Esse objeto está sedo usando para armazena o listener do on touch para possibilitar remover e adicionar a Recycler view
    private RecyclerItemClickListener recycler_item_click_listener;
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
        addFilmesAdicionados();
        removerFilmesApagados();
        //buscarMeusFilmes(filme_dao.getIdsFilmes());
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Adcionando Listener de toque no item a recycler view
        //Está sendo adicionado aqui por que ele está sendo retirado
        //quando o usuário toca nele para evitar tela duplicada
        rv_meus_filmes.addOnItemTouchListener(recycler_item_click_listener);
    }

    //Remove itens do adapter caso tenham sido apagados da lista do usuário
    private void removerFilmesApagados(){
        if(adapter_filmes != null && adapter_filmes.getListaFilmes() != null && adapter_filmes.getListaFilmes().size() > 0){
            for (int i = 0; i < adapter_filmes.getListaFilmes().size(); i++) {
                if(!filme_dao.listaFilmeContains(adapter_filmes.getListaFilmes().get(i).getId())){
                    adapter_filmes.removerFilme(i);
                }
            }
            if (filme_dao.getIdsFilmes().size() == 0 && adapter_filmes != null){
                limparListaFilmes();
                tv_sem_filmes.setVisibility(View.VISIBLE);
            }
        }
    }

    private void addFilmesAdicionados(){
        if(adapter_filmes != null && adapter_filmes.getListaFilmes() != null){
            for (int i = 0; i < filme_dao.getIdsFilmes().size(); i++) {
                if (!adapter_filmes.containsFilme(filme_dao.getIdsFilmes().get(i))){
                    getFilme(filme_dao.getIdsFilmes().get(i));
                }
            }
        }
    }

    private void getFilme(int id_filme){

        TmdbFilme tmdbFilme = new TmdbFilme(this);
        tmdbFilme.getFilme(id_filme, new retrofit2.Callback<Filme>() {
            @Override
            public void onResponse(@NonNull Call<Filme> call, @NonNull Response<Filme> response) {
                if(response.isSuccessful()){
                    CheckConnection.setIs_internet(true);
                    List<Filme> filmes = adapter_filmes.getListaFilmes();
                    Filme filme = response.body();
                    if(filmes == null){
                        filmes = new ArrayList<>();
                    }
                    filmes.add(filme);
                    Collections.sort(filmes);
                    adapter_filmes.addFilmeOnPosition(filme, filmes.indexOf(filme));
                }else{
                    //Verificando se não existe nenhuma call ativa para não sobrepor calls
                    //E verificando se esta call não foi cancelada
                    if (!TmdbFilme.currentCallIsAtiva() && !call.isCanceled()) {
                        getFilme(id_filme);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<Filme> call, @NonNull Throwable t) {
                //Verificando se não existe nenhuma call ativa para não sobrepor calls
                //E verificando se esta call não foi cancelada
                if (!TmdbFilme.currentCallIsAtiva() && !call.isCanceled()) {
                    getFilme(id_filme);
                }
            }
        });

        CheckConnection.verificarInternet(() -> {
            if(!CheckConnection.isInternet()) {
                if(!snackbar_connection.isShown())
                    snackbar_connection.show();
            }
        });
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

        //inicializando listener que será adicionado no OnResume
        recycler_item_click_listener = new RecyclerItemClickListener(
                getApplicationContext(),
                rv_meus_filmes,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        rv_meus_filmes.removeOnItemTouchListener(recycler_item_click_listener);
                        abrirTelaDetalheFilme(adapter_filmes.getListaFilmes().get(position));
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                });
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
            TmdbFilme tmdbFilme = new TmdbFilme(this);
            tmdbFilme.getFilmesPorId(ids_filmes, this);
            verificarInternet();
        }else{
            progressBar.setVisibility(View.GONE);
            tv_sem_filmes.setVisibility(View.VISIBLE);
        }

    }

    private void verificarInternet(){
        //Inicia timer que verificará se existe conexão daqui 5 segundos, se não houver uma mensagem será exibida
        CheckConnection.verificarInternet(() -> {
            if(!CheckConnection.isInternet()) {
                if(!snackbar_connection.isShown())
                    snackbar_connection.show();
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
            adapter_filmes = new AdapterFilmes(filmes, this, R.layout.item_filme_poster);
            rv_meus_filmes.setAdapter(adapter_filmes);

        }else{
            tv_sem_filmes.setVisibility(View.VISIBLE);
        }

        if (snackbar_connection != null)
            snackbar_connection.dismiss();

        progressBar.setVisibility(View.GONE);

    }

    private void limparListaFilmes(){
        if(adapter_filmes != null)
            adapter_filmes.clear();
        adapter_filmes = null;
    }


}