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
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.jv.meusfilmes.R;
import com.jv.meusfilmes.adapters.AdapterFilmes;
import com.jv.meusfilmes.api.TmdbFilme;
import com.jv.meusfilmes.models.Filme;
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
    private ListaFilmesActivity listaFilmesActivity;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private TextView tv_sem_resultados;
    private SearchView searchView;
    private String pesquisa_string;
    private SharedPreferences shared_preferences_filmes;
    private SharedPreferences.Editor editor;

    private View view_touched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_filmes);
        configurarToobar();

        inicializarComponentes();
        inicializarListeners();

        //Carregando a página 1 dos filmes
        buscarFilmes(1);
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

        shared_preferences_filmes = getSharedPreferences("MinhaListaFilmes", MODE_PRIVATE);
        editor = shared_preferences_filmes.edit();
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

        rv_filmes.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        rv_filmes,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                view_touched = view;
                                setCarregamento(true);
                                getFilme(adapter_filmes.getIdFilme(position));
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                addFilmeALista(adapter_filmes.getIdFilme(position));
                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            }
                        }
                )
        );
    }

    private void setCarregamento(boolean is_carregando){
        if(is_carregando){
            view_touched.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            view_touched.setAlpha(0.3f);
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
                    Filme filme = response.body();
                    abrirTelaDetalheFilme(filme);
                    setCarregamento(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Filme> call, @NonNull Throwable t) {

            }
        });
    }

    private void abrirTelaDetalheFilme(Filme filme){
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
    }

    private void pesquisarFilmes(int page, String query){
        tv_sem_resultados.setVisibility(View.GONE);
        //Se for o primeiro carregamento da ista da pesquisa apaga-se o que está na lista se não mantem
        if(page == 1){
            limparListaFilmes();
        }

        progressBar.setVisibility(View.VISIBLE);
        TmdbFilme tmdbFilme = new TmdbFilme(this);
        tmdbFilme.pesquisarFilmes(listaFilmesActivity, page, query);
    }

    public void exibirFilmes(List<Filme> filmes){
        //Caso não seja recebido nenhum filme um texto será exibido
        if(filmes != null){
            //Só por garantia este textview será escondido aqui
            tv_sem_resultados.setVisibility(View.GONE);
            //caso seja a primeira exibição o adapter estará nulo e receberá uma lista de filmes
            //caso o adapter tenha sido instanciado somente serão adicionados novos itens
            if (adapter_filmes == null){
                adapter_filmes = new AdapterFilmes(filmes, this);
                rv_filmes.setAdapter(adapter_filmes);
            }else{
                adapter_filmes.addFilmes(filmes);
            }
        //Se
        }else if (adapter_filmes == null || adapter_filmes.getItemCount() <= 0){
            tv_sem_resultados.setVisibility(View.VISIBLE);
        }

        progressBar.setVisibility(View.GONE);

    }

    //Metodo para verificar se filme já foi adicionado a lista
    private boolean listaFilmeContains(int id_filme){
        boolean contain = false;

        for (int i = 1; i <= shared_preferences_filmes.getAll().size( ); i++) {
            String chave_filme = "filme_" + i;
            int id = shared_preferences_filmes.getInt(chave_filme, 0);

            if(id != 0){
                if (id == id_filme)
                    contain = true;
            }
        }
        return contain;
    }

    private void addFilmeALista(int id_filme){
        //Verifica se o filme já está na lista
        if(!listaFilmeContains(id_filme)){
            int qt_filmes = shared_preferences_filmes.getAll().size();
            String chave_filme = "filme_" + (qt_filmes+1);
            editor.putInt(chave_filme, id_filme);
            //Verifica se inserção na lista deu certo
            if(editor.commit()){
                Toast toast = Toast.makeText(getApplicationContext(), "Filme adicionado", Toast.LENGTH_SHORT);
                toast.show();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(), "Filme não adicionado, tente novamente!", Toast.LENGTH_LONG);
                toast.show();
            }
        }else{
            Toast toast = Toast.makeText(getApplicationContext(), "Este filme já está na lista", Toast.LENGTH_SHORT);
            toast.show();
        }

    }
}