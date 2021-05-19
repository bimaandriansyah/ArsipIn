package com.bim.arsipin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<ViewHolder> {

    HomeActivity homeActivity;
    List<Model> modelList;
    AlertDialog.Builder dialog;
    View dialogView;
    String userID;

    public CustomAdapter(HomeActivity homeActivity, List<Model> modelList) {
        this.homeActivity = homeActivity;
        this.modelList = modelList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.data_view,parent,false);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        userID = firebaseUser.getUid();

        ViewHolder viewHolder = new ViewHolder(itemView);
        viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                dialog = new AlertDialog.Builder(homeActivity);
                dialogView = LayoutInflater.from(homeActivity).inflate(R.layout.click_file,null);
                dialog.setCancelable(true);
                TextView judul = (TextView) dialogView.findViewById(R.id.textJudul);
                TextView date = (TextView) dialogView.findViewById(R.id.textDate);
                ImageView gbrSurat = (ImageView) dialogView.findViewById(R.id.gambarSurat);
                judul.setText(modelList.get(position).getJudul());
                date.setText(modelList.get(position).getDate());
                if (modelList.get(position).jenis.equals("Surat Pribadi")){
                    gbrSurat.setImageResource(R.drawable.surat1);
                } else if (modelList.get(position).jenis.equals("Surat Dinas")){
                    gbrSurat.setImageResource(R.drawable.surat2);
                }
                dialog.setView(dialogView);

                final AlertDialog dialoga = dialog.create();

                LinearLayout btnDownload = (LinearLayout) dialogView.findViewById(R.id.btnDownload);
                btnDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setType("application/pdf");
                        intent.setData(Uri.parse(modelList.get(position).getUri()));
                        homeActivity.startActivity(intent);
                        dialoga.dismiss();
                    }
                });

                LinearLayout btnDelete = (LinearLayout) dialogView.findViewById(R.id.btnDelete);
                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                        firebaseFirestore.collection("users").document(userID).collection(modelList.get(position).getArsip()).document(modelList.get(position).getId())
                                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(modelList.get(position).getUri());
                                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(homeActivity, "Berhasil Menghapus Data", Toast.LENGTH_SHORT).show();
                                        homeActivity.showData(modelList.get(position).getArsip());
                                        dialoga.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(homeActivity, "Gagal euy"+modelList.get(position).getArsip()+" dan "+modelList.get(position).getId(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(homeActivity, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
//
                    }
                });

                dialoga.show();
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.judul.setText(modelList.get(position).judul);
        holder.jenisSurat.setText(modelList.get(position).jenis);
        holder.date.setText(modelList.get(position).date);
        if (modelList.get(position).jenis.equals("Surat Pribadi")){
            holder.gbrSurat.setImageResource(R.drawable.surat1);
        } else if (modelList.get(position).jenis.equals("Surat Dinas")){
            holder.gbrSurat.setImageResource(R.drawable.surat2);
        }

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }
}
