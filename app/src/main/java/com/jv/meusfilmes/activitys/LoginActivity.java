package com.jv.meusfilmes.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.jv.meusfilmes.R;

public class LoginActivity extends AppCompatActivity {

    private ImageView iv_logo, iv_logo_tmdb;
    private boolean is_redirecionado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inicializarComponentes();
        inicializarListeners();

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        is_redirecionado = false;
        delay();
    }

    private void delay(){
            new Handler().postDelayed(() -> {
                if(!is_redirecionado){
                    abrirTelaMeusFilmes();
                }
            }, 1800);
    }

    private void inicializarComponentes(){
        iv_logo = findViewById(R.id.iv_logo);
        iv_logo_tmdb = findViewById(R.id.iv_logo_tmdb);
    }

    private void inicializarListeners(){
        iv_logo_tmdb.setOnClickListener(v -> redirecionarUsuario());
    }

    private void redirecionarUsuario(){
        String url = "https://www.themoviedb.org";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
        is_redirecionado = true;
    }

    public void abrirTelaMeusFilmes(){
        Intent meus_filmes = new Intent(this, MeusFilmesActivity.class);
        startActivity(meus_filmes);
        finish();
    }
}