package com.nicolas.firebaseapp.fragement;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nicolas.firebaseapp.R;
import com.nicolas.firebaseapp.model.Upload;
import com.nicolas.firebaseapp.util.LoadingDialog;

import java.io.ByteArrayOutputStream;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class StorageFragment extends Fragment {
    //referencia p/ Firebase Storage
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private Button btnUpload,btnGaleria;
    private ImageView imageView;
    private Uri imageUri = null;
    private EditText editNome;

    //referencia para o nó RealtimeDB
    private DatabaseReference database = FirebaseDatabase.getInstance()
            .getReference("uploads");

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    public StorageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        database = FirebaseDatabase.
                getInstance()
                .getReference("uploads")
                .child(auth.getUid());

        // Inflate the layout for this fragment

        View layout = inflater.inflate(R.layout.fragment_storage, container, false);

        btnUpload = layout.findViewById(R.id.storage_btn_upload);
        imageView = layout.findViewById(R.id.storage_image_cel);
        btnGaleria = layout.findViewById(R.id.storage_btn_galeria);
        editNome = layout.findViewById(R.id.storage_edit_nome);

        btnGaleria.setOnClickListener( v ->{
            Intent intent = new Intent();
            // Intent Implicita -> pegar uma arquivo do celular
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            // incia uma Activity, e espera um retorno(foto)
            startActivityForResult(intent,111);

        });

        btnUpload.setOnClickListener(v ->{
            if (editNome.getText().toString().isEmpty()){
                Toast.makeText(getActivity(),"Digite um Nome pra Imagem",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (imageUri != null){
                uploadImagemUri();
            }
            uploadImagemByte();
        });

        return layout;
    }

    private void uploadImagemUri() {
        LoadingDialog dialog = new LoadingDialog(getActivity(),R.layout.custom_dialog);
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
                    Toast.makeText(getActivity(),"UPLOAD FEITO COM SUCESSO MEU PIT",
                            Toast.LENGTH_SHORT).show();

                    //Inserir od dados da imagem no RealtimeDatabase

                    //pegar  a Url da imagem
                    taskSnapshot.getStorage().getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                // inserir no database

                                //criando refrencia(database) do upload
                                DatabaseReference refUpload = database.push();
                                String id = refUpload.getKey();

                                Upload upload = new Upload(id,nome,uri.toString());
                                //salvar upload no db
                                refUpload.setValue(upload).
                                        addOnSuccessListener( aVoid -> {
                                            dialog.dismissDialog();
                                            Toast.makeText(getActivity(),"Upload Feito Com Sucesso!",
                                                    Toast.LENGTH_SHORT);
                                            NavController navController = Navigation.
                                                    findNavController(getActivity(),
                                                             R.id.nav_host_fragment);
                                            //voltar para a fragement principal
                                            navController.navigateUp();

                                        });

                            });



                })
                .addOnFailureListener( e -> {
                    e.printStackTrace();
                });

    }
    private String getFileExtension(Uri imageUri) {
        ContentResolver cr = getActivity().getContentResolver();
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(cr.getType(imageUri));
    }
    //Resultado do StartActivityResult()
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("Result","requestCode"+requestCode +
                ", resultCode: "+resultCode);
        if (requestCode ==111 & resultCode == Activity.RESULT_OK) {
            // caso o usuario elecionou uma imagem da galeria

            //endereco da i,agem selecionada
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }
    public byte[] convertImage2Byte(ImageView imageView){
        //Converter imagem view pra um array de bytes[]
        Bitmap bitmap = ( (BitmapDrawable) imageView.getDrawable() ).getBitmap();
        // objeto baos ->  armazenar a imagem convertida pra bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] data = baos.toByteArray();
        return baos.toByteArray();
    }
    //Fazer o upload de uma imagem covertida p/ bytes
    public void uploadImagemByte(){
        byte[] data = convertImage2Byte(imageView);

        //Criar uma referencia pora imagem no Storage
        StorageReference imagemRef = storage.getReference().
                child("imagens/01.jpeg");

        //Realiza Upload da imagem
        imagemRef.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(getActivity(),"Upload Feito com Sucesso",
                            Toast.LENGTH_SHORT).show();
                    Log.i("UPLOAD","SUCESOOOOO PAI !!");

                })
                .addOnFailureListener( e->{
                    e.printStackTrace();

                });


        //storage.getReference().putBytes()

    }



}
