package com.jv.meusfilmes.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jv.meusfilmes.R;
import com.jv.meusfilmes.activitys.DetalheFilmeActivity;
import com.jv.meusfilmes.models.Filme;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.List;

public class AdapterFilmesSimilares extends RecyclerView.Adapter<AdapterFilmesSimilares.ViewHolderFilmeSimilar> {

    private List<Filme> filmes_similares;


    public AdapterFilmesSimilares(List<Filme> filmes_similares) {
        this.filmes_similares = filmes_similares;
    }

    @NonNull
    @Override
    public ViewHolderFilmeSimilar onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Convertendo o XML do item dos filmes similares em uma view
        View item_filme_similar = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_filme_similar, parent, false);

        return new AdapterFilmesSimilares.ViewHolderFilmeSimilar(item_filme_similar);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderFilmeSimilar holder, int position) {

        if(filmes_similares.get(position).getAdult()){
            filmes_similares.remove(position);
            notifyItemRemoved(position);
        }else{
            //Carregado Imagem
            Picasso.get()
                    .load("https://image.tmdb.org/t/p/w300"
                            +filmes_similares.get(position).getPoster_path())
                    .fit()
                    .placeholder(R.drawable.poster_default)
                    .into(holder.iv_poster);
        }
    }

    @Override
    public int getItemCount() {
        int count_item;
        if (filmes_similares.size() < 18){
            count_item = filmes_similares.size();
        }else{
            count_item = 18;
        }
        return count_item;
    }

    public Integer getIdFilme(int posicao){
        return filmes_similares.get(posicao).getId();
    }

    public class ViewHolderFilmeSimilar extends RecyclerView.ViewHolder {

        private ImageView iv_poster;
        public ViewHolderFilmeSimilar(@NonNull View itemView) {
            super(itemView);
            iv_poster = itemView.findViewById(R.id.iv_poster);
        }
    }
}
