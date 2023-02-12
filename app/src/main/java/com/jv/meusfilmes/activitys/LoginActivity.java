package com.jv.meusfilmes.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.jv.meusfilmes.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                abrirTelaMeusFilmes();
            }
        }, 3000);

    }

    public void abrirTelaMeusFilmes(){
        Intent meus_filmes = new Intent(this, MeusFilmesActivity.class);
        startActivity(meus_filmes);
        finish();
    }
}