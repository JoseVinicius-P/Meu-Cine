package com.jv.meusfilmes.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

public class CheckConnection {

    private static boolean is_internet = false;

    //Inicia um time que verificará se existe conexão com a internet
    public static void verificarInternet(Runnable runnable) {
        is_internet = false;
        new Handler().postDelayed(runnable, 5000);
    }

    //Verificar se existe conexão ativa (WIFI, Dados móveis)
    public static boolean verificarConexao(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public static void setIs_internet(boolean internet){
        is_internet = internet;
    }

    public static boolean isInternet(){
        return is_internet;
    }
}
