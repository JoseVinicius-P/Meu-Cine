package com.jv.meusfilmes.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.jv.meusfilmes.R;
import com.jv.meusfilmes.adapters.AdapterCompanhiasProdutoras;
import com.jv.meusfilmes.adapters.AdapterFilmesSimilares;
import com.jv.meusfilmes.dao.TmdbFilme;
import com.jv.meusfilmes.models.CompanhiaProdutora;
import com.jv.meusfilmes.models.Filme;
import com.jv.meusfilmes.utilities.Formatter;
import com.jv.meusfilmes.utilities.RecyclerItemClickListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;

public class DetalheFilmeActivity extends AppCompatActivity {

    private Filme filme;
    private ImageView iv_poster_horiz_filme, iv_poster_vert_filme;
    private TextView tv_titulo_filme, tv_data_lancamento, tv_duracao, tv_aprovacao, tv_sinopse, tv_generos;
    private Toolbar toolbar;
    private RecyclerView rv_companhias_produtoras, rv_filmes_similares;
    private AdapterCompanhiasProdutoras adapter_companhias_produtoras;
    private AdapterFilmesSimilares adapter_filmes_similares;
    private RecyclerView.LayoutManager lm_campanhias_produtoras, lm_filmes_similares;
    private View view_touched;
    private MaterialButton bt_exluir_filme, bt_add_filme;
    private SharedPreferences shared_preferences_filmes;
    private SharedPreferences.Editor editor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_filme);
        configurarToobar();

        inicializarComponentes();
        inicializarListenners();

        if(getExtra())
            upDateInterface();
        else
            finish();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void configurarToobar(){
        toolbar = findViewById(R.id.toobar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void inicializarComponentes(){
        shared_preferences_filmes = getSharedPreferences("MinhaListaFilmes", MODE_PRIVATE);
        editor = shared_preferences_filmes.edit();
        iv_poster_horiz_filme = findViewById(R.id.iv_poster_horiz_filme);
        iv_poster_vert_filme = findViewById(R.id.iv_poster_vert_filme);
        tv_titulo_filme = findViewById(R.id.tv_titulo_filme);
        tv_data_lancamento = findViewById(R.id.tv_data_lancamento);
        tv_duracao = findViewById(R.id.tv_duracao);
        tv_aprovacao = findViewById(R.id.tv_aprovacao);
        tv_sinopse = findViewById(R.id.tv_sinopse);
        tv_generos = findViewById(R.id.tv_generos);
        bt_add_filme = findViewById(R.id.bt_add_filme);
        bt_exluir_filme = findViewById(R.id.bt_exluir_filme);
        rv_companhias_produtoras = findViewById(R.id.rv_companhias_produtoras);

        //Criando mananger para o recycler view
        lm_campanhias_produtoras = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_companhias_produtoras.setLayoutManager(lm_campanhias_produtoras);
        rv_companhias_produtoras.setHasFixedSize(true);

        rv_filmes_similares = findViewById(R.id.rv_filmes_similares);

        //Criando mananger para o recycler view
        lm_filmes_similares = new GridLayoutManager(this, 3);
        rv_filmes_similares.setLayoutManager(lm_filmes_similares);
        rv_filmes_similares.setHasFixedSize(true);
    }

    private void inicializarListenners(){
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rv_filmes_similares.addOnItemTouchListener(new RecyclerItemClickListener(
                getApplicationContext(),
                rv_filmes_similares,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        view_touched = view;
                        setCarregamento(true);
                        getFilme(adapter_filmes_similares.getIdFilme(position));
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));

        bt_add_filme.setOnClickListener(v -> addFilmeALista(filme.getId()));

        bt_exluir_filme.setOnClickListener(v -> apagarFilmeLista());
    }

    //Usado para definir qual botão aparecerá: o de add ou de excluir
    private void definirBotao(){
        if (listaFilmeContains(filme.getId())){
            bt_exluir_filme.setVisibility(View.VISIBLE);
            bt_add_filme.setVisibility(View.GONE);
        }else{
            bt_add_filme.setVisibility(View.VISIBLE);
            bt_exluir_filme.setVisibility(View.GONE);
        }
    }

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
        tmdbFilme.getFilme(id_filme, new retrofit2.Callback<Filme>() {
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
        finish();
    }

    private void upDateInterface() {
        setImagePosterHoriz("https://image.tmdb.org/t/p/original" + filme.getBackdrop_path());
        setImagePosterVert("https://image.tmdb.org/t/p/w300" + filme.getPoster_path());
        tv_titulo_filme.setText(filme.getTitle());
        tv_data_lancamento.setText(Formatter.formatterDate(filme.getRelease_date()));
        tv_duracao.setText(Formatter.formatterRuntime(filme.getRuntime()));
        tv_aprovacao.setText(Formatter.formatterVoteAverage(String.valueOf(filme.getVote_average())));
        tv_sinopse.setText(filme.getOverview());
        tv_generos.setText(Formatter.formatterGenres(filme.getGenres()));
        exibirCompanhiasProdutoras(filme.getProduction_companies());
        definirBotao();
        getFilmesSimilares(filme.getId());
    }

    private void getFilmesSimilares(int id_filme){
        TmdbFilme tmdbFilme = new TmdbFilme(this);
        tmdbFilme.getFilmesSimilares(id_filme, this);
    }

    private void exibirCompanhiasProdutoras(List<CompanhiaProdutora> companhia_produtoras){
        adapter_companhias_produtoras = new AdapterCompanhiasProdutoras(companhia_produtoras);
        rv_companhias_produtoras.setAdapter(adapter_companhias_produtoras);
    }

    private void setImagePosterHoriz(String url){
        Picasso.get()
                .load(url)
                .fit()
                .placeholder(R.drawable.poster_fundo_default)
                .into(iv_poster_horiz_filme);
    }
    private void setImagePosterVert(String url){
        //obtendo altura que será usada na imagem conforme tamanho do poster horizontal
        //Está sendo definida como 15dp menor do que o poster horiontal
        int heigth = (int) ((getWidthDisplay()-getPixels(15))*9)/16;
        Picasso.get()
                .load(url)
                .resize(0, heigth)
                .centerCrop()
                .into(iv_poster_vert_filme, new Callback() {
                    @Override
                    public void onSuccess() {
                        setCorNotificationBar(getColorImageView());
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

    }
    private int getColorImageView(){
        Bitmap bitmap=((BitmapDrawable)iv_poster_vert_filme.getDrawable()).getBitmap();
        //Coordenada x do centro
        int x = bitmap.getWidth()/2;
        //Coordenada y do centro
        int y = bitmap.getHeight()/2;
        return bitmap.getPixel(x,y);
    }

    private void setCorNotificationBar(int cor){
        Window window = getWindow();
        window.setStatusBarColor(cor);
    }

    //usado para converter dp em pixel
    private int getPixels(int dp){
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    //usado para obter largura do display para fazer calculos de tamanho de imagens
    private int getWidthDisplay(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        return displayMetrics.widthPixels;
    }


    //Pegando id_filme, passado da activity
    private boolean getExtra(){
        boolean is_id_valido = false;
        filme = (Filme) getIntent().getSerializableExtra("filme");

        if(filme != null)
            is_id_valido = true;

        return is_id_valido;
    }

    public void exibirFilmesSimilares(List<Filme> filmes) {
        if(filmes != null && filmes.size() > 0){
            adapter_filmes_similares = new AdapterFilmesSimilares(filmes);
            rv_filmes_similares.setAdapter(adapter_filmes_similares);
        }
    }

    private void addFilmeALista(int id_filme){
        //Verifica se o filme já está na lista
        if(!listaFilmeContains(id_filme)){
            int qt_filmes = shared_preferences_filmes.getAll().size();
            String chave_filme = "filme_" + (qt_filmes+1);
            editor.putInt(chave_filme, id_filme);
            //Verifica se inserção na lista deu certo
            if(editor.commit()){
                definirBotao();
            }else{
                Toast toast = Toast.makeText(getApplicationContext(), "Filme não adicionado, tente novamente!", Toast.LENGTH_LONG);
                toast.show();
            }
        }else{
            definirBotao();
        }

    }

    private void apagarFilmeLista(){
        List<Integer> ids_filmes_local = getIdsFilmes();
        int id_filme = filme.getId();
        if(ids_filmes_local != null || ids_filmes_local.size() > 0){
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
            if(editor.commit()){
                definirBotao();
            }
        }
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
        }

        //Retonará nulo para fazer tratamento no outro metodo
        if (ids_filmes.size() > 0)
            return ids_filmes;
        else
            return null;
    }

}