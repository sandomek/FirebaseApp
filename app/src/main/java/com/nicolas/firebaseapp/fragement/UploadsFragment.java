package com.nicolas.firebaseapp.fragement;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nicolas.firebaseapp.R;
import com.nicolas.firebaseapp.UpdateActivity;
import com.nicolas.firebaseapp.adapter.ImageAdapter;
import com.nicolas.firebaseapp.model.Upload;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class UploadsFragment extends Fragment {
    private Button btnLogout, btnStorage;
    private DatabaseReference database = FirebaseDatabase.getInstance()
            .getReference("uploads");
    private ArrayList<Upload> listaUploads = new ArrayList<>();

    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    public UploadsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_uploads, container, false);

        btnLogout = layout.findViewById(R.id.main_btn_logout);
        btnStorage = findViewById(R.id.main_btn_storage);
        recyclerView = findViewById(R.id.main_recycler);

        imageAdapter = new ImageAdapter(getApplication(),listaUploads);
        imageAdapter.setListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                Upload upload = listaUploads.get(position);
                deleteUpload(upload);
            }

            @Override
            public void onUpdateClick(int position) {
                Upload upload = listaUploads.get(position);

                Intent intent = new Intent(getApplicationContext(), UpdateActivity.class);
                // o putExtra -> envia o Upload pra outra Activity
                intent.putExtra("upload",upload);
                startActivity(intent);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(imageAdapter);



    }
}
