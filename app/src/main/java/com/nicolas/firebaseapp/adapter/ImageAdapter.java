package com.nicolas.firebaseapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nicolas.firebaseapp.R;
import com.nicolas.firebaseapp.model.Upload;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter <ImageAdapter.ImageVH>  {
    private Context context;
    private ArrayList<Upload> listaUploads;
    private OnItemClickListener listener;

    public void setListener( OnItemClickListener listener ){
        this.listener = listener;
    }

    public ImageAdapter(Context c, ArrayList<Upload>l){
        this.context = c;
        this.listaUploads = l;
    }

    @NonNull
    @Override
    public ImageVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.image_item,
                                                parent,false);

        return new ImageVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageVH holder, int position) {
        Upload upload = listaUploads.get(position);
        holder.textnome.setText(upload.getNomeImagem());
        // setar a imagem -> atravez do Glide
        Glide.with(context)
        .load(upload.getUrl())
            .load(upload.getUrl())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return listaUploads.size();
    }

    public class ImageVH extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView textnome;
        ImageView imageView;


        public ImageVH(@NonNull View itemView) {
            super(itemView);

            textnome = itemView.findViewById(R.id.image_item_nome);
            imageView = itemView.findViewById(R.id.image_item_imageview);
            itemView.setOnCreateContextMenuListener(this);
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Selecionar Ação");

            MenuItem deletar = menu.add( 0, 1 , 1, "Deletar" );
            menu.add( 0, 2 , 2, "Atualizar" );
            //evento de click opção deletar
            deletar.setOnMenuItemClickListener(item -> {
                if (listener !=null ){
                    int position = getAdapterPosition();
                    listener.onDeleteClick(position);
                }
               return true;
            });

        }
    }

    public interface OnItemClickListener{

        void onDeleteClick(int position);

        void onUpdateClick(int position);
    }


}
