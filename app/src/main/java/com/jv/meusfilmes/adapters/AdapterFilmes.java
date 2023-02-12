package com.jv.meusfilmes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jv.meusfilmes.R;
import com.jv.meusfilmes.models.Filme;
import com.jv.meusfilmes.utilities.Formatter;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterFilmes extends RecyclerView.Adapter<AdapterFilmes.ViewHolderFilme> {

    private List<Filme> filmes;
    private Context context;

    public AdapterFilmes(List<Filme> filmes, Context context) {
        this.filmes = filmes;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolderFilme onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Convertendo o XML do item do filme em uma view
        View item_filme = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_filme, parent, false);

        return new ViewHolderFilme(item_filme);
    }

    //Neste metodo inserimos os dados na vizualização (Holder) recebida como retorno do onCreate acima
    @Override
    public void onBindViewHolder(@NonNull ViewHolderFilme holder, int position) {
        Filme filme = filmes.get(position);
        holder.tv_title.setText(filme.getTitle());
        holder.vote_average.setText(Formatter.formatterVoteAverage(filme.getVote_average()));
        setCorAprovacao(holder.vote_average, (int) (Double.parseDouble(filme.getVote_average())*10));
        //Criei uma classe para formação da data recebida da api
        holder.tv_date.setText(Formatter.formatterDate(filme.getRelease_date()));
        //Carregado Imagem
        Picasso.get()
                .load("https://image.tmdb.org/t/p/w500"
                        +filme.getPoster_path())
                .fit()
                .placeholder(R.drawable.poster_default)
                .into(holder.iv_poster);
    }

    //Cria String para exiição de aprovação

    //Metodo irá mudar a cor do text view conforme aprovação
    private void setCorAprovacao(TextView tv, int porcentagem){
        if(porcentagem >= 70){
            tv.setTextColor(context.getResources().getColor(R.color.green));
        }else if(porcentagem > 40){
            tv.setTextColor(context.getResources().getColor(R.color.yellow));
        }else{
            tv.setTextColor(context.getResources().getColor(R.color.red));
        }
    }

    @Override
    public int getItemCount() {
        return filmes.size();
    }

    //Usado para carregar mais filmes
    public void addFilmes(List<Filme> novosFilmes) {
        int initialSize = filmes.size();
        filmes.addAll(novosFilmes);
        notifyItemRangeInserted(initialSize, novosFilmes.size());
    }

    public void clear() {
        filmes.clear();
        notifyDataSetChanged();
    }

    public Integer getIdFilme(int posicao){
        return filmes.get(posicao).getId();
    }

    public List<Filme> getListaFilmes(){
        return filmes;
    }

    public void removerFilme(int position){
        filmes.remove(position);
        notifyItemRemoved(position);
    }

    //Esta classe é usada como objetos que representam as views na recycler view
    public class ViewHolderFilme extends RecyclerView.ViewHolder {

        private TextView tv_title, vote_average, tv_date;
        private ImageView iv_poster;
        private RelativeLayout progressBarLayout;

        public ViewHolderFilme(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            vote_average = itemView.findViewById(R.id.vote_average);
            tv_date = itemView.findViewById(R.id.tv_date);
            iv_poster = itemView.findViewById(R.id.iv_poster);
            progressBarLayout = itemView.findViewById(R.id.progressBarLayout);
        }
    }
}
