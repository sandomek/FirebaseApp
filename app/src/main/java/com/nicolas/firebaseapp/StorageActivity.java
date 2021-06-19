package com.nicolas.firebaseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.storage.FirebaseStorage;

public class StorageActivity extends AppCompatActivity {
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private Button btnUpload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);
        btnUpload = findViewById(R.id.storage_btn_upload);

        btnUpload.setOnClickListener(v ->{
            Uri uri = Uri.parse("https://super.abril.com.br/wp-content/uploads/2021/06/Loki-X-coisas-para-saber-antes-de-assistir-a-serie-1.png?quality=70&strip=info&resize=680,453");
            storage.getReference().putFile(uri);
        });
    }
}
