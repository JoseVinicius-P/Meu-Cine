package com.jv.meusfilmes.utilities;

import com.jv.meusfilmes.models.Genero;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

//Classe usada para formatar conforme Design da aplicação
public abstract class Formatter {

    public static String formatterVoteAverage(String vote_average){
        int porcentagem = (int) (Double.parseDouble(vote_average)*10);
        return porcentagem + "% gostou";
    }

    public static String formatterDate(String data_string){
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(Objects.requireNonNull(formato.parse(data_string)));
        } catch (ParseException ignored) {

        }
        String mesAbreviado = new DateFormatSymbols().getShortMonths()[calendar.get(Calendar.MONTH)];
        return calendar.get(Calendar.DAY_OF_MONTH) + " de " + mesAbreviado + " de " + calendar.get(Calendar.YEAR);
    }

    public static String formatterRuntime(int runtime){
        int horas = (int) (runtime / 60);
        int minutos = runtime % 60;

        return horas + "h " + minutos + "m";
    }

    public static String formatterGenres(List<Genero> generos){
        String generos_local = "";

        for (int i = 0; i < generos.size(); i++) {

            generos_local = generos_local.concat(generos.get(i).getName());

            if (i != generos.size()-1){
                generos_local = generos_local.concat(", ");
            }
        }

        return generos_local;
    }

}
