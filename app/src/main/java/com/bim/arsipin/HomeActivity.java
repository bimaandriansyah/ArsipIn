package com.bim.arsipin;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{

    CircleImageView image;
    TextView txtNama,txtMasuk,txtKeluar,txtSemua,txtPribadi,txtDinas;
    ImageView panahMasuk,panahKeluar,imgSemua,imgPribadi,imgDinas;
    EditText edtSearch;
    private ShimmerFrameLayout mShimmerViewContainer;
    ImageButton btnSearch;
    LinearLayout btnTambah,btnMasuk,btnKeluar,btnSemua,btnPribadi,btnDinas,infodata;
    FirebaseAuth firebaseAuth;
    GoogleSignInClient googleSignInClient;
    RecyclerView recyclerView;
    List<Model> modelList = new ArrayList<>();
    RecyclerView.LayoutManager layoutManager;
    FirebaseFirestore firestore;
    CustomAdapter adapter;
    String userID ,isiSearch;
    public boolean doubleTapParam = false;

    @Override
    public void onBackPressed() {
        if (doubleTapParam) {
            super.onBackPressed();
            return;
        }

        this.doubleTapParam = true;
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleTapParam = false;
            }
        }, 2000);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);

        mShimmerViewContainer.startShimmerAnimation();

        image = findViewById(R.id.userPhoto);
        txtNama = findViewById(R.id.txtNama);
        btnTambah = findViewById(R.id.btnTambah);
        recyclerView = findViewById(R.id.isiData);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

//        progressBar = (ProgressBar)findViewById(R.id.spin_kit);
//        Sprite doubleBounce = new DoubleBounce();
//        progressBar.setIndeterminateDrawable(doubleBounce);
//        progressBar.setVisibility(View.VISIBLE);

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        userID = firebaseUser.getUid();

        if (firebaseUser != null){
            Glide.with(HomeActivity.this)
                    .load(firebaseUser.getPhotoUrl())
                    .into(image);
            txtNama.setText(firebaseUser.getDisplayName());
        }

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        googleSignInClient = GoogleSignIn.getClient(HomeActivity.this
                , GoogleSignInOptions.DEFAULT_SIGN_IN);

        btnMasuk = (LinearLayout) findViewById(R.id.btnMasuk);
        btnKeluar = (LinearLayout) findViewById(R.id.btnKeluar);
        txtMasuk = findViewById(R.id.txtMasuk);
        panahMasuk = findViewById(R.id.panahMasuk);
        txtKeluar = findViewById(R.id.txtKeluar);
        panahKeluar = findViewById(R.id.panahKeluar);
        edtSearch = findViewById(R.id.edtSearch);
        btnSearch = findViewById(R.id.btnSearch);
        btnSemua = findViewById(R.id.btnSemuaSurat);
        btnPribadi = findViewById(R.id.btnPribadi);
        btnDinas = findViewById(R.id.btnDinas);
        imgSemua = findViewById(R.id.imgSemua);
        imgPribadi = findViewById(R.id.imgPribadi);
        imgDinas = findViewById(R.id.imgDinas);
        txtSemua = findViewById(R.id.txtSemua);
        txtPribadi = findViewById(R.id.txtPribadi);
        txtDinas = findViewById(R.id.txtDinas);
        infodata = findViewById(R.id.infoData);

        Action("Masuk");

        btnMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Action("Masuk");

                txtMasuk.setTextColor(HomeActivity.this.getResources().getColor(R.color.dasar));
                panahMasuk.setColorFilter(HomeActivity.this.getResources().getColor(R.color.dasar));
                txtKeluar.setTextColor(HomeActivity.this.getResources().getColor(R.color.abu));
                panahKeluar.setColorFilter(HomeActivity.this.getResources().getColor(R.color.abu));
            }
        });

        btnKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Action("Keluar");

                txtKeluar.setTextColor(HomeActivity.this.getResources().getColor(R.color.dasar));
                panahKeluar.setColorFilter(HomeActivity.this.getResources().getColor(R.color.dasar));
                txtMasuk.setTextColor(HomeActivity.this.getResources().getColor(R.color.abu));
                panahMasuk.setColorFilter(HomeActivity.this.getResources().getColor(R.color.abu));
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void Action(String menu) {
        showData(menu);
        btnDinas.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        imgDinas.setColorFilter(HomeActivity.this.getResources().getColor(R.color.abu));
        txtDinas.setTextColor(HomeActivity.this.getResources().getColor(R.color.abu));
        btnSemua.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dasar)));
        imgSemua.setColorFilter(HomeActivity.this.getResources().getColor(R.color.putih));
        txtSemua.setTextColor(HomeActivity.this.getResources().getColor(R.color.putih));
        btnPribadi.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        imgPribadi.setColorFilter(HomeActivity.this.getResources().getColor(R.color.abu));
        txtPribadi.setTextColor(HomeActivity.this.getResources().getColor(R.color.abu));
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isiSearch = edtSearch.getText().toString().toLowerCase();
                if (isiSearch.equals("")){
                    showData(menu);
                } else {
                    searchData(isiSearch,menu);
                }
            }
        });
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    isiSearch = edtSearch.getText().toString().toLowerCase();
                    if (isiSearch.equals("")){
                        showData(menu);
                    } else {
                        searchData(isiSearch,menu);
                    }
                    return true;
                }
                return false;
            }
        });

        btnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,TambahActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("menu",menu);
                startActivity(intent);
            }
        });

        btnDinas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDinas.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dasar)));
                imgDinas.setColorFilter(HomeActivity.this.getResources().getColor(R.color.putih));
                txtDinas.setTextColor(HomeActivity.this.getResources().getColor(R.color.putih));
                btnSemua.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                imgSemua.setColorFilter(HomeActivity.this.getResources().getColor(R.color.abu));
                txtSemua.setTextColor(HomeActivity.this.getResources().getColor(R.color.abu));
                btnPribadi.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                imgPribadi.setColorFilter(HomeActivity.this.getResources().getColor(R.color.abu));
                txtPribadi.setTextColor(HomeActivity.this.getResources().getColor(R.color.abu));
                jenisData("Surat Dinas",menu);
            }
        });

        btnPribadi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDinas.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                imgDinas.setColorFilter(HomeActivity.this.getResources().getColor(R.color.abu));
                txtDinas.setTextColor(HomeActivity.this.getResources().getColor(R.color.abu));
                btnSemua.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                imgSemua.setColorFilter(HomeActivity.this.getResources().getColor(R.color.abu));
                txtSemua.setTextColor(HomeActivity.this.getResources().getColor(R.color.abu));
                btnPribadi.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dasar)));
                imgPribadi.setColorFilter(HomeActivity.this.getResources().getColor(R.color.putih));
                txtPribadi.setTextColor(HomeActivity.this.getResources().getColor(R.color.putih));
                jenisData("Surat Pribadi",menu);
            }
        });

        btnSemua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDinas.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                imgDinas.setColorFilter(HomeActivity.this.getResources().getColor(R.color.abu));
                txtDinas.setTextColor(HomeActivity.this.getResources().getColor(R.color.abu));
                btnSemua.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dasar)));
                imgSemua.setColorFilter(HomeActivity.this.getResources().getColor(R.color.putih));
                txtSemua.setTextColor(HomeActivity.this.getResources().getColor(R.color.putih));
                btnPribadi.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                imgPribadi.setColorFilter(HomeActivity.this.getResources().getColor(R.color.abu));
                txtPribadi.setTextColor(HomeActivity.this.getResources().getColor(R.color.abu));
                showData(menu);
            }
        });

    }

    private void jenisData(String jenis, String menu){
        mShimmerViewContainer.startShimmerAnimation();
        firestore.collection("users").document(userID).collection(menu).whereEqualTo("Jenis", jenis)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        modelList.clear();
                        for (DocumentSnapshot doc:task.getResult()){
                            if(doc.exists()){
                                Model model = new Model(
                                        doc.getString("Id"),
                                        doc.getString("Judul"),
                                        doc.getString("Jenis"),
                                        doc.getString("Date"),
                                        doc.getString("Uri"),
                                        doc.getString("Arsip"));
                                modelList.add(model);
                            } else {
                                infodata.setVisibility(View.VISIBLE);
                            }
                        }
                        adapter = new CustomAdapter(HomeActivity.this,modelList);
                        recyclerView.setAdapter(adapter);
//                        progressBar.setVisibility(View.INVISIBLE);
                        mShimmerViewContainer.stopShimmerAnimation();
                        mShimmerViewContainer.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void searchData(String isiSearch,String menu) {
        mShimmerViewContainer.startShimmerAnimation();
        firestore.collection("users").document(userID).collection(menu).whereEqualTo("search", isiSearch)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        modelList.clear();
                        for (DocumentSnapshot doc:task.getResult()){
                            if(doc.exists()){
                                Model model = new Model(
                                        doc.getString("Id"),
                                        doc.getString("Judul"),
                                        doc.getString("Jenis"),
                                        doc.getString("Date"),
                                        doc.getString("Uri"),
                                        doc.getString("Arsip"));
                                modelList.add(model);
                            } else {
                                infodata.setVisibility(View.VISIBLE);
                            }
                        }
                        adapter = new CustomAdapter(HomeActivity.this,modelList);
                        recyclerView.setAdapter(adapter);
//                        progressBar.setVisibility(View.INVISIBLE);
                        mShimmerViewContainer.stopShimmerAnimation();
                        mShimmerViewContainer.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void showData(String menu) {
        mShimmerViewContainer.startShimmerAnimation();
        firestore.collection("users").document(userID).collection(menu)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        modelList.clear();
                        for (DocumentSnapshot doc:task.getResult()){
                            if(doc.exists()){
                                Model model = new Model(
                                        doc.getString("Id"),
                                        doc.getString("Judul"),
                                        doc.getString("Jenis"),
                                        doc.getString("Date"),
                                        doc.getString("Uri"),
                                        doc.getString("Arsip"));
                                modelList.add(model);
                            } else {
                                infodata.setVisibility(View.VISIBLE);
                            }
                        }
                        adapter = new CustomAdapter(HomeActivity.this,modelList);
                        recyclerView.setAdapter(adapter);
//                        progressBar.setVisibility(View.INVISIBLE);
                        mShimmerViewContainer.stopShimmerAnimation();
                        mShimmerViewContainer.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void showPopUp(View v){
        PopupMenu popupMenu = new PopupMenu(this,v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menulogout:
                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            firebaseAuth.signOut();
                            Intent intent = new Intent(HomeActivity.this,LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
//                            Toast.makeText(HomeActivity.this, "Logout Successfull", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
                return true;
            default:
                return false;
        }
    }
}