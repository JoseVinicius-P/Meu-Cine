package com.jv.meusfilmes.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    private int layout;

    public AdapterFilmes(List<Filme> filmes, Context context, int layout) {
        this.filmes = filmes;
        this.context = context;
        this.layout = layout;
    }

    @NonNull
    @Override
    public ViewHolderFilme onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Convertendo o XML do item do filme em uma view
        View item_filme = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);

        return new ViewHolderFilme(item_filme);
    }

    //Neste metodo inserimos os dados na vizualização (Holder) recebida como retorno do onCreate acima
    @Override
    public void onBindViewHolder(@NonNull ViewHolderFilme holder, int position) {
        Filme filme = filmes.get(position);

        //Este Adapter pode ser utilizado usando dois layouts distintos
        //se for o item_filme os dados contidos no if serão carregados
        if(layout == R.layout.item_filme){
            holder.tv_title.setText(filme.getTitle());
            holder.vote_average.setText(Formatter.formatterVoteAverage(filme.getVote_average()));
            setCorAprovacao(holder.vote_average, (int) (Double.parseDouble(filme.getVote_average())*10));
            //Criei uma classe para formação da data recebida da api
            holder.tv_date.setText(Formatter.formatterDate(filme.getRelease_date()));
        }

        //Carregado Imagem
        Picasso.get()
                .load("https://image.tmdb.org/t/p/w500"
                        +filme.getPoster_path())
                .fit()
                .placeholder(R.drawable.poster_default)
                .into(holder.iv_poster);
        setMargin(position, holder.rl_item);
    }

    //Usado para colocar margim a direito em elementos a esquerda e margin a esquerda em elementos a direita
    //É necessãrio pq se aplicar somente em um, o elemento fica mais que o outro
    private void setMargin(int position, RelativeLayout relative_layout){
        // Cria um objeto LayoutParams para o elemento
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, // largura do elemento
                RelativeLayout.LayoutParams.WRAP_CONTENT  // altura do elemento
        );
        if(position % 2 == 0){
            // Define as margens esquerda, topo, direita e baixo em pixels
            params.setMargins(0, getPixels(10), getPixels(10), getPixels(10));
        }else{
            // Define as margens esquerda, topo, direita e baixo em pixels
            params.setMargins(getPixels(10), getPixels(10),0, getPixels(10));
        }

        // Define os novos LayoutParams para o elemento
        relative_layout.setLayoutParams(params);

    }

    private int getPixels(int dp){
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
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

    public void addFilmeOnPosition(Filme filme, int position){
        filmes.remove(filme);
        filmes.add(position, filme);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, filmes.size() - position);
    }

    @SuppressLint("NotifyDataSetChanged")
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

    public boolean containsFilme(int id_filme){
        boolean contains = false;
        for (Filme filme: filmes){
            if (filme.getId() == id_filme) {
                contains = true;
                break;
            }
        }

        return contains;
    }

    public void removerFilme(int position){
        filmes.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, filmes.size() - position);
    }

    //Esta classe é usada como objetos que representam as views na recycler view
    public class ViewHolderFilme extends RecyclerView.ViewHolder {

        private TextView tv_title, vote_average, tv_date;
        private ImageView iv_poster;
        private RelativeLayout progressBarLayout, rl_item;

        public ViewHolderFilme(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            vote_average = itemView.findViewById(R.id.vote_average);
            tv_date = itemView.findViewById(R.id.tv_date);
            iv_poster = itemView.findViewById(R.id.iv_poster);
            progressBarLayout = itemView.findViewById(R.id.progressBarLayout);
            rl_item = itemView.findViewById(R.id.rl_item);
        }
    }
}
