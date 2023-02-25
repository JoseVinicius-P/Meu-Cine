package com.jv.meusfilmes.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class FilmeDAO {
    private SharedPreferences shared_preferences_filmes;
    private SharedPreferences.Editor editor;

    public FilmeDAO(Context context) {
        this.shared_preferences_filmes = context.getSharedPreferences("MinhaListaFilmes", Context.MODE_PRIVATE);
        this.editor = shared_preferences_filmes.edit();
    }

    public void apagarFilmeLista(int id_filme){
        List<Integer> ids_filmes_local = getIdsFilmes();
        if(ids_filmes_local != null && ids_filmes_local.size() > 0){
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

    public void salvarListaFilmes(List<Integer> ids_filmes){
        //if(ids_filmes.size() > 0){
            for (int i = 1; i <= ids_filmes.size(); i++) {
                String chave_filme = "filme_" + i;
                editor.putInt(chave_filme, ids_filmes.get(i-1));
            }
            editor.apply();
        //}else{
            //rv_meus_filmes.setVisibility(View.GONE);
            //tv_sem_filmes.setVisibility(View.VISIBLE);
       //}

    }

    public List<Integer> getIdsFilmes(){
        List<Integer> ids_filmes = new ArrayList<>();
        int qt_filmes = shared_preferences_filmes.getAll().size();
        if (qt_filmes > 0){
            for(int i = 1; i <= qt_filmes; i++){
                String chave_filme = "filme_" + i;
                ids_filmes.add(shared_preferences_filmes.getInt(chave_filme, 0));
            }
        }else {
            return null;
        }

        //Retonará nulo para fazer tratamento no outro metodo
        if (ids_filmes.size() > 0)
            return ids_filmes;
        else
            return null;
    }

    //Metodo para verificar se filme já foi adicionado a lista
    public boolean listaFilmeContains(int id_filme){
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

    public boolean addFilmeALista(int id_filme){
        if(!listaFilmeContains(id_filme)){
            int qt_filmes = shared_preferences_filmes.getAll().size();
            String chave_filme = "filme_" + (qt_filmes+1);
            editor.putInt(chave_filme, id_filme);
            //Verifica e retorna se inserção na lista deu certo
            return editor.commit();
        }else{
            return false;
        }


    }
}
