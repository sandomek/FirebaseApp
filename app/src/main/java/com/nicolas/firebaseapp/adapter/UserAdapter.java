package com.nicolas.firebaseapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.nicolas.firebaseapp.R;
import com.nicolas.firebaseapp.model.User;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserVH> {
    private ArrayList<User> listaContatos;
    private Context context;
    private ClickAdapterUser listener;

    private static final int TIPO_ADICIONAR = 0;
    private static final int TIPO_SOLICITAR = 1;

    public void setListener(ClickAdapterUser listener){
        this.listener = listener;
    }

    public UserAdapter(ArrayList<User> listaUsuarios, Context context) {
        this.listaContatos = listaUsuarios;
        this.context = context;
    }

    @NonNull
    @Override
    public UserVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.user_recycler,
                                                parent,false);

        if (viewType == TIPO_SOLICITAR){
            Button b = v.findViewById(R.id.user_recycler_btn_add);
            b.setText("SOLICITADO");
            b.setEnabled(false);
        }

        return new UserVH(v);

    }

    @Override
    public void onBindViewHolder(@NonNull UserVH holder, int position) {
        User u = listaContatos.get(position);
        holder.textEmail.setText(u.getEmail());
        holder.textNome.setText(u.getNome());
        /* caso não foi adicionado -> cria evento de click */
        if (!u.getReceiveRequest()){
            holder.onClick();
        }


    }

    @Override
    public int getItemCount() {
        return listaContatos.size();
    }

    @Override
    public int getItemViewType(int position) {
        User contato = listaContatos.get(position);
        /* se o usuario ja foi solicitado */
        if (contato.getReceiveRequest()){
            return TIPO_SOLICITAR;
        }
        return TIPO_ADICIONAR;
    }

    public class UserVH extends RecyclerView.ViewHolder {
        TextView textNome, textEmail;
        RoundedImageView imgPhoto;
        Button btnAdicionar;

        public void onClick(){
            btnAdicionar.setOnClickListener(v -> {
                if (listener != null){
                    int position = getAdapterPosition();

                    listener.adicionarContato(position);
                }
            });
        }



        public UserVH(@NonNull View itemView) {
            super(itemView);
            textNome = itemView.findViewById(R.id.user_recycler_nome);
            textEmail = itemView.findViewById(R.id.user_recycler_email);
            imgPhoto = itemView.findViewById(R.id.user_recycler_photo);
            btnAdicionar = itemView.findViewById(R.id.user_recycler_btn_add);



        }
    }
    public interface ClickAdapterUser{
        void adicionarContato(int position);
    }




}
