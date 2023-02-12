package com.jv.meusfilmes.adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jv.meusfilmes.R;
import com.jv.meusfilmes.models.CompanhiaProdutora;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterCompanhiasProdutoras extends RecyclerView.Adapter<AdapterCompanhiasProdutoras.ViewHolderCompanhiaProdutora> {

    private List<CompanhiaProdutora> companhias_produtoras;

    public AdapterCompanhiasProdutoras(List<CompanhiaProdutora> companhias_produtoras) {
        this.companhias_produtoras = companhias_produtoras;
    }

    @NonNull
    @Override
    public ViewHolderCompanhiaProdutora onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Convertendo o XML do item da companhia produtora em uma view
        View item_companhia_produtora = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_companhia_produtora, parent, false);

        return new AdapterCompanhiasProdutoras.ViewHolderCompanhiaProdutora(item_companhia_produtora);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderCompanhiaProdutora holder, int position) {
        if(companhias_produtoras.get(position).getLogo_path() != null){
            Picasso.get()
                    .load("https://image.tmdb.org/t/p/w200/" + companhias_produtoras.get(position).getLogo_path())
                    .resize(0, holder.iv_logo_companhia_produtora.getMaxHeight())
                    .centerCrop()
                    .into(holder.iv_logo_companhia_produtora);
        }else{
            holder.iv_logo_companhia_produtora.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return companhias_produtoras.size();
    }

    public class ViewHolderCompanhiaProdutora extends RecyclerView.ViewHolder {

        public ImageView iv_logo_companhia_produtora;

        public ViewHolderCompanhiaProdutora(@NonNull View itemView) {
            super(itemView);
            iv_logo_companhia_produtora =  itemView.findViewById(R.id.iv_companhia_produtora);
        }
    }
}
