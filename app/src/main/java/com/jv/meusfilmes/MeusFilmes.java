package com.jv.meusfilmes;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

public class MeusFilmes extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }
}
