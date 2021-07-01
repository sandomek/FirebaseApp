package com.nicolas.firebaseapp.fragement;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nicolas.firebaseapp.R;
import com.nicolas.firebaseapp.UpdateActivity;
import com.nicolas.firebaseapp.adapter.ImageAdapter;
import com.nicolas.firebaseapp.model.Upload;
import com.nicolas.firebaseapp.util.LoadingDialog;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class UploadsFragment extends Fragment {
    private DatabaseReference database = FirebaseDatabase.getInstance()
                                            .getReference("uploads").
                                             child(FirebaseAuth.getInstance().getUid());

    private ArrayList<Upload> listaUploads = new ArrayList<>();
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;

    public UploadsFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_uploads, container, false);

        recyclerView = layout.findViewById(R.id.main_recycler);

        imageAdapter = new ImageAdapter(getContext(),listaUploads);
        imageAdapter.setListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                Upload upload = listaUploads.get(position);
                deleteUpload(upload);
            }

            @Override
            public void onUpdateClick(int position) {
                Upload upload = listaUploads.get(position);

                Intent intent = new Intent(getActivity(),UpdateActivity.class);
                // o putExtra -> envia o Upload pra outra Activity
                intent.putExtra("upload",upload);
                startActivity(intent);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(imageAdapter);

        return layout;


    }
    @Override
    public void onStart() {
        // onStart -> faz parte do cliclo de vida de uma Activity
        // -> e executado depois do OnCreate
        // -> é executado quando o app incia,
        // -> e quando volta do background
        super.onStart();
        getData();
    }

    public void deleteUpload(Upload upload){
        LoadingDialog dialog = new LoadingDialog(getActivity(),R.layout.custom_dialog);
        dialog.startLoadingDialog();

        //deletar img no Storage
        StorageReference imagemRef = FirebaseStorage.getInstance().getReferenceFromUrl(upload.getUrl());
        imagemRef.delete()
                .addOnSuccessListener(aVoid -> {
                    //deletar imagem no database
                    database.child(upload.getId()).removeValue()
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(getContext(),"Item Deletado",
                                        Toast.LENGTH_LONG).show();
                                dialog.dismissDialog();
                            });
                });
    }

    public void getData(){
        // listener p/ o nó uploads
        // -> caso ocorra uma alteração == retornar TODOS os dados !!
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for ( DataSnapshot no_filho : snapshot.getChildren()){
                    Upload upload = no_filho.getValue(Upload.class);
                    listaUploads.add(upload);
                    Log.i("DATABASE","ID:"+upload.getId()+ ",nome: "+upload.getNomeImagem());

                }
                imageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
