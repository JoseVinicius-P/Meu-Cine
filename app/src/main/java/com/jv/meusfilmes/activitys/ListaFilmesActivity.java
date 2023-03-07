package com.jv.meusfilmes.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.jv.meusfilmes.R;
import com.jv.meusfilmes.adapters.AdapterFilmes;
import com.jv.meusfilmes.api.TmdbFilme;
import com.jv.meusfilmes.dao.FilmeDAO;
import com.jv.meusfilmes.models.Filme;
import com.jv.meusfilmes.utilities.CheckConnection;
import com.jv.meusfilmes.utilities.EndlessRecyclerViewScrollListener;
import com.jv.meusfilmes.utilities.RecyclerItemClickListener;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListaFilmesActivity extends AppCompatActivity {

    private RecyclerView rv_filmes;
    private AdapterFilmes adapter_filmes;
    private RecyclerView.LayoutManager layoutManager;
    //Esse objeto está sedo usando para armazena o listener do on touch para possibilitar remover e adicionar a Recycler view
    private RecyclerItemClickListener recycler_item_click_listener;
    private ListaFilmesActivity listaFilmesActivity;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private TextView tv_sem_resultados;
    private SearchView searchView;
    private String pesquisa_string;
    //Classe usada para acessar as shareds preferences da lista de filmes
    private FilmeDAO filme_dao;
    private View view_touched;

    //Para mostrar aviso de conexão
    private Snackbar snackbar_connection;
    private LinearLayout full_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_filmes);
        configurarToobar();

        inicializarComponentes();
        inicializarListeners();

        verificarConexao();
        //Carregando a página 1 dos filmes
        buscarFilmes(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Adcionando Listener de toque no item a recycler view
        //Está sendo adicionado aqui por que ele está sendo retirado
        //quando o usuário toca nele para evitar tela duplicada
        rv_filmes.addOnItemTouchListener(recycler_item_click_listener);
    }

    //No momento que o usuário sair da tela a call atual será cancelada
    @Override
    protected void onPause() {
        super.onPause();
        //No momento que o usuário sair da tela a call atual será cancelada
        TmdbFilme.cancelCurrentCall();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_seach, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Pesquisar filme...");

        inicializarListenersSearch(searchView);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



    private void inicializarListenersSearch(SearchView searchView){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                pesquisa_string = s;
                pesquisarFilmes(1, pesquisa_string);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        searchView.setOnCloseListener(() -> {
            pesquisa_string = null;
            limparListaFilmes();
            buscarFilmes(1);
            return false;
        });
    }

    private void configurarToobar(){
        toolbar = findViewById(R.id.toobar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void inicializarComponentes() {
        //instanciando contexto para passar para outras classes
        listaFilmesActivity = this;

        rv_filmes = findViewById(R.id.rv_filmes);

        //Criando mananger para o recycler view
        layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        rv_filmes.setLayoutManager(layoutManager);
        rv_filmes.setHasFixedSize(true);

        progressBar = findViewById(R.id.progressBarLayout);
        tv_sem_resultados = findViewById(R.id.tv_sem_resultados);

        filme_dao = new FilmeDAO(this);

        full_container = findViewById(R.id.full_container);
        snackbar_connection = Snackbar.make(full_container,
                "Verifique sua conexão",
                Snackbar.LENGTH_INDEFINITE);
    }

    private void inicializarListeners(){
        //Quando o usuario rolar até o final da lista um novo carregamento será iniciado
        rv_filmes.addOnScrollListener(new EndlessRecyclerViewScrollListener((GridLayoutManager) layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if(pesquisa_string == null){
                    buscarFilmes(page+1);
                }else{
                    pesquisarFilmes(page+1, pesquisa_string);
                }

            }
        });

        toolbar.setNavigationOnClickListener(v -> {
            if(!searchView.isIconified())
                searchView.setIconified(true);
            else
                finish();
        });

        //inicializando listener que será adicionado no OnResume
        recycler_item_click_listener = new RecyclerItemClickListener(
                getApplicationContext(),
                rv_filmes,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        rv_filmes.removeOnItemTouchListener(recycler_item_click_listener);
                        view_touched = view;
                        setCarregamento(true);
                        getFilme(adapter_filmes.getIdFilme(position));
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        initAddFilme(adapter_filmes.getIdFilme(position));
                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        );
    }

    //Metodo usado para tratar verificações da inserção do filme
    private void initAddFilme(int id_filme){
        String msg;
        if(!filme_dao.listaFilmeContains(id_filme)){
            if(filme_dao.addFilmeALista(id_filme)){
                msg = "Filme adicionado";
            }else{
                msg = "Filme não adicionado, tente novamente!";
            }
        }else{
            msg = "Este filme já está na lista";
        }

        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

    }

    private void setCarregamento(boolean is_carregando){
        //Se o carremento for true a progress bar ficará visivel e o item ficará um pouco tranparente
        if(is_carregando){
            view_touched.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            view_touched.setAlpha(0.6f);
        }else{
            view_touched.findViewById(R.id.progressBar).setVisibility(View.GONE);
            view_touched.setAlpha(1f);
        }

    }

    private void getFilme(int id_filme){

        TmdbFilme tmdbFilme = new TmdbFilme(this);
        tmdbFilme.getFilme(id_filme, new Callback<Filme>() {
            @Override
            public void onResponse(@NonNull Call<Filme> call, @NonNull Response<Filme> response) {
                if(response.isSuccessful()){
                    CheckConnection.setIs_internet(true);
                    Filme filme = response.body();
                    abrirTelaDetalheFilme(filme);
                    setCarregamento(false);
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

        //Inicia timer que verificará se existe conexão daqui 5 segundos, se não houver uma mensagem será exibida
        CheckConnection.verificarInternet(() -> {
            if(!CheckConnection.isInternet()) {
                if(!snackbar_connection.isShown()){
                    snackbar_connection.show();
                }
            }
        });
    }

    private void abrirTelaDetalheFilme(Filme filme){

        if (snackbar_connection != null)
            snackbar_connection.dismiss();

        Intent detalhe_filme = new Intent(this, DetalheFilmeActivity.class);
        detalhe_filme.putExtra("filme", filme);
        startActivity(detalhe_filme);
    }

    private void limparListaFilmes(){
        if(adapter_filmes != null)
            adapter_filmes.clear();
        adapter_filmes = null;
    }

    //Esse metodo é usado na incialização, no carregamento de mais paginas conforme rolagem do usuario e após finalização de pesquisa
    private void buscarFilmes(int page){

        tv_sem_resultados.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        TmdbFilme tmdbFilme = new TmdbFilme(this);
        tmdbFilme.getFilmesPopulares(listaFilmesActivity, page);

        //Inicia timer que verificará se existe conexão daqui 5 segundos, se não houver uma mensagem será exibida
        CheckConnection.verificarInternet(() -> {
            if(!CheckConnection.isInternet()) {
                if(!snackbar_connection.isShown()){
                    snackbar_connection.show();
                }
            }
        });
    }

    private void pesquisarFilmes(int page, String query){
        tv_sem_resultados.setVisibility(View.GONE);
        //Se for o primeiro carregamento da ista da pesquisa apaga-se o que está na lista se não mantem
        if(page == 1){
            limparListaFilmes();
        }

        //Cancelando call que pode estar ativa
        //para não haver carregamento sobreposto
        TmdbFilme.cancelCurrentCall();
        progressBar.setVisibility(View.VISIBLE);
        TmdbFilme tmdbFilme = new TmdbFilme(this);
        tmdbFilme.pesquisarFilmes(listaFilmesActivity, page, query);

        //Inicia timer que verificará se existe conexão daqui 5 segundos, se não houver uma mensagem será exibida
        CheckConnection.verificarInternet(() -> {
            if(!CheckConnection.isInternet()) {
                if(!snackbar_connection.isShown())
                    snackbar_connection.show();
            }
        });
    }

    public void exibirFilmes(List<Filme> filmes){
        //Caso não seja recebido nenhum filme um texto será exibido
        if(filmes != null){
            //Só por garantia este textview será escondido aqui
            tv_sem_resultados.setVisibility(View.GONE);
            //caso seja a primeira exibição o adapter estará nulo e receberá uma lista de filmes
            //caso o adapter tenha sido instanciado somente serão adicionados novos itens
            if (adapter_filmes == null){
                adapter_filmes = new AdapterFilmes(filmes, this, R.layout.item_filme);
                rv_filmes.setAdapter(adapter_filmes);
            }else{
                adapter_filmes.addFilmes(filmes);
            }
        //Se
        }else if (adapter_filmes == null || adapter_filmes.getItemCount() <= 0){
            tv_sem_resultados.setVisibility(View.VISIBLE);
        }

        if (snackbar_connection != null)
            snackbar_connection.dismiss();

        progressBar.setVisibility(View.GONE);
    }

    private void verificarConexao(){
        if(!CheckConnection.verificarConexao(this)){
            snackbar_connection.show();
        };
    }

}