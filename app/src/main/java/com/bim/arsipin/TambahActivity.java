package com.bim.arsipin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TambahActivity extends AppCompatActivity {
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;
    FirebaseAuth firebaseAuth;
    EditText edtJudul, edtFile;
    TextView headerMenu;
    LinearLayout btnSubmit;
    RadioButton rdoSurat;
    RadioGroup jenisSurat;
    String userID,menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        edtJudul = findViewById(R.id.edtJudul);
        edtFile = findViewById(R.id.edtFile);
        jenisSurat = (RadioGroup) findViewById(R.id.radioJenisSurat);
        headerMenu = findViewById(R.id.headerMenu);

        Bundle bundle = getIntent().getExtras();
        menu = bundle.getString("menu");
        headerMenu.setText(menu);

        btnSubmit = findViewById(R.id.btnSubmit);

        LinearLayout upPDF = (LinearLayout) findViewById(R.id.layoutPDF);
        upPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPDF();
            }
        });
    }

    private void selectPDF() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"SELECT PDF FILE"),1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){

            String uriString = data.toString();
            File myfile = new  File(uriString);

            edtFile.setText(myfile.getName().substring(0,myfile.getName().length()-10));

            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadPDF(data.getData());
                }
            });
        }
    }

    private void uploadPDF(Uri data) {
        String judul = edtJudul.getText().toString();
        int selectedID = jenisSurat.getCheckedRadioButtonId();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        rdoSurat = (RadioButton) findViewById(selectedID);
        String jenis = rdoSurat.getText().toString();
        userID = firebaseUser.getUid();
        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy");
        String date = df.format(Calendar.getInstance().getTime());
        String id = UUID.randomUUID().toString();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("File is Loading ...");
        progressDialog.show();

        StorageReference reference = storageReference .child("uploadPDF"+System.currentTimeMillis()+".pdf");

        reference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete());
                        Uri uri = uriTask.getResult();

                        Map<String,Object> user = new HashMap<>();
                        user.put("Id",id);
                        user.put("Judul", judul);
                        user.put("search",judul.toLowerCase());
                        user.put("Jenis", jenis);
                        user.put("Date", date);
                        user.put("Uri",uri.toString());
                        user.put("Arsip",menu);
                        firebaseFirestore.collection("users").document(userID).collection(menu).document(id).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(TambahActivity.this,HomeActivity.class));
                                finish();
                            }
                        });
                        progressDialog.dismiss();

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                double progress = (100.0* snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                progressDialog.setMessage("File Uploaded ... "+(int)progress+"%");

            }
        });
    }
}