package com.nicolas.firebaseapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nicolas.firebaseapp.model.Upload;
import com.nicolas.firebaseapp.util.LoadingDialog;

import java.util.Date;

public class UpdateActivity extends AppCompatActivity {
    //referencia p/ Firebase Storage
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private Button btnUpload, btnGaleria;
    private ImageView imageView;
    private Uri imageUri = null;
    private EditText editNome;

    //referencia para o nó RealtimeDB
    private DatabaseReference database = FirebaseDatabase.getInstance()
            .getReference("uploads");
    private Upload upload;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        btnUpload = findViewById(R.id.update_btn_upload);
        imageView = findViewById(R.id.update_image_cel);
        btnGaleria = findViewById(R.id.update_btn_galeria);
        editNome = findViewById(R.id.update_edit_nome);

        //recuperar o Upload selecionado
        upload = (Upload) getIntent().getSerializableExtra("upload");
        editNome.setText(upload.getNomeImagem());
        Glide.with(this).load(upload.getUrl()).into(imageView);

        btnGaleria.setOnClickListener( v ->{
            Intent intent = new Intent();
            // Intent Implicita -> pegar uma arquivo do celular
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            // incia uma Activity, e espera um retorno(foto)
            startActivityForResult(intent,111);

        });
        btnUpload.setOnClickListener( v ->{
            if (editNome.getText().toString().isEmpty()){
                Toast.makeText(this,"Sem Nome",Toast.LENGTH_SHORT).show();
                return;
            }
            // caso imagem não tenha sido atualizado
            if (imageUri ==null){
                //atualizar o nome da imagem
                String nome = editNome.getText().toString();
                upload.setNomeImagem(nome);
                database.child(upload.getId()).setValue(upload)
                .addOnSuccessListener(aVoid -> {
                    finish();
                });
                return;
            }
            atualizarImagem();
        });
    }
    public void atualizarImagem(){
        // deletar a imagem antiga no Storage
            storage.getReferenceFromUrl( upload.getUrl() ).delete();
        // fazer upload da imagem atualizada no Storage
            uploadImagemUri();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==111 & resultCode == Activity.RESULT_OK) {
            // caso o usuario elecionou uma imagem da galeria

            //endereco da i,agem selecionada
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private String getFileExtension(Uri imageUri) {
        ContentResolver cr = getContentResolver();
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(cr.getType(imageUri));
    }

    private void uploadImagemUri() {
        LoadingDialog dialog = new LoadingDialog(this,R.layout.custom_dialog);
        dialog.startLoadingDialog();

        String tipo = getFileExtension(imageUri);
        //refencia do arquivo Firebase
        Date d = new Date();
        String nome = editNome.getText().toString();

        // criando uma referencia p/ imagem no Storage
        StorageReference imagemRef = storage.getReference().
                child("imagens/"+nome+"-"+d.getTime()+"."+tipo);
        // realiza o upload da imagem
        imagemRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(this,"UPLOAD FEITO COM SUCESSO MEU PIT",
                            Toast.LENGTH_SHORT).show();

                    //Inserir od dados da imagem no RealtimeDatabase

                    //pegar  a Url da imagem
                    taskSnapshot.getStorage().getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                // atualizar no database

                                // atualizar o objeto upload
                                upload.setUrl(uri.toString());
                                upload.setNomeImagem( editNome.getText().toString() );

                                database.child(upload.getId()).setValue(upload)
                                .addOnSuccessListener(aVoid -> {
                                    dialog.dismissDialog();
                                    finish();
                                })
                                ;


                            });
                })
                .addOnFailureListener( e -> {
                    e.printStackTrace();
                });

    }



}
