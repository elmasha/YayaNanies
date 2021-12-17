package com.intech.yayananies.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.intech.yayananies.Adpater.CountyAdapter;
import com.intech.yayananies.Fragments.NotificationFragment;
import com.intech.yayananies.Fragments.ProfileFragment;
import com.intech.yayananies.Models.Candidates;
import com.intech.yayananies.Models.Counties;
import com.intech.yayananies.R;
import com.squareup.picasso.Picasso;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class PreferenceActivity extends AppCompatActivity {

    private CountyAdapter adapter;
    private RecyclerView mRecyclerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference CountyRef = db.collection("Counties");
    CollectionReference YayaRef = db.collection("Yaya_Employer");
    CollectionReference CandidateRef = db.collection("Yaya_Candidates");
    CollectionReference AdminRef = db.collection("Admin");
    private TextView chooseCounty,OnCounty,OnAge,prefCount,OpenDrawer;
    private Spinner age;
    private String countyText,ageText;
    private FrameLayout frameLayout;
    private Button ConfirmPreference;
    private CircleImageView profileImage;
    private FirebaseAuth mAuth;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    int countystate = 0;
    private int profileState= 0;


    @Override
    protected void onStart() {
        super.onStart();
        FetchProduct();
        countyText = "";
        ageText = "";
        OnCounty.setText("");
        OnAge.setText("");
        ConfirmPreference.setEnabled(false);
        ConfirmPreference.setBackgroundResource(R.drawable.btn_round_grey);
        ConfirmPreference.setTextColor(Color.parseColor("#808080"));
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        mAuth = FirebaseAuth.getInstance();
        mRecyclerView = findViewById(R.id.recycler_county);
        chooseCounty = findViewById(R.id.choose_county);
        age = findViewById(R.id.choose_age);
        frameLayout = findViewById(R.id.FrameCounty);
        OnAge = findViewById(R.id.on_age);
        OnCounty = findViewById(R.id.on_county);
        prefCount = findViewById(R.id.prefCount);
        profileImage = findViewById(R.id.E_image);
        OpenDrawer = findViewById(R.id.drawerOpen);
        dl = (DrawerLayout) findViewById(R.id.PrefDrawer);
        ConfirmPreference.setEnabled(false);
        ConfirmPreference.setBackgroundResource(R.drawable.btn_round_grey);
        ConfirmPreference.setTextColor(Color.parseColor("#808080"));

        nv = (NavigationView) findViewById(R.id.navigation_menu2);

        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.account:
                        if (dl.isDrawerOpen(GravityCompat.START)){
                            dl.closeDrawer(GravityCompat.START);
                        }
                        if (profileState ==0){
                            profileImage.setVisibility(View.GONE);
                            getSupportFragmentManager().beginTransaction().replace(R.id.Frame_preference,
                                    new ProfileFragment()).commit();
                            profileState = 1;
                        }else if (profileState ==1){
                            profileImage.setVisibility(View.GONE);
                            getSupportFragmentManager().beginTransaction().replace(R.id.Frame_preference,
                                    new ProfileFragment()).commit();
                            profileState = 0;
                        }

                        break;



                    case R.id.notification:

                        if (dl.isDrawerOpen(GravityCompat.START)){
                            dl.closeDrawer(GravityCompat.START);
                        }
                        getSupportFragmentManager().beginTransaction().replace(R.id.Frame_preference,
                                new NotificationFragment()).commit();
                        profileImage.setVisibility(View.VISIBLE);
                        profileState = 0;
                        break;

                    case R.id.preference:

                        if (dl.isDrawerOpen(GravityCompat.START)){
                            dl.closeDrawer(GravityCompat.START);
                        }
                        if(getSupportFragmentManager().findFragmentById(R.id.Frame_preference) != null) {
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .remove(getSupportFragmentManager().findFragmentById(R.id.Frame_preference)).commit();
                        }
                        profileImage.setVisibility(View.VISIBLE);
                        profileState = 0;
                        break;
                    case R.id.share:
                        if (dl.isDrawerOpen(GravityCompat.START)){
                            dl.closeDrawer(GravityCompat.START);
                        }
                        // shareApp(getApplicationContext());
                        break;
                    case R.id.refer:
                        if (dl.isDrawerOpen(GravityCompat.START)){
                            dl.closeDrawer(GravityCompat.START);
                        }
                        profileImage.setVisibility(View.VISIBLE);
                        profileState = 0;
                        break;
                    case R.id.logOut:
                        if (dl.isDrawerOpen(GravityCompat.START)){
                            dl.closeDrawer(GravityCompat.START);
                        }
                        Logout_Alert();
                        profileImage.setVisibility(View.VISIBLE);
                        profileState = 0;
                        break;

                    default:
                        return true;
                }


                return true;

            }
        });

        OpenDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!dl.isDrawerVisible(GravityCompat.START)){
                    dl.openDrawer(GravityCompat.START);
                }else if (dl.isDrawerVisible(GravityCompat.START)){
                    dl.closeDrawer(GravityCompat.START);
                }
            }
        });

        ConfirmPreference = findViewById(R.id.confirm_preference);


        age.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ageText = age.getSelectedItem().toString();

                OnAge.setText(ageText);
                if (ageText != null){
                    ConfirmPreference.setEnabled(true);
                    ConfirmPreference.setBackgroundResource(R.drawable.btn_round_gradient);
                    ConfirmPreference.setTextColor(Color.parseColor("#1C1B2B"));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (profileState ==0){
                    profileImage.setVisibility(View.GONE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.Frame_preference,
                            new ProfileFragment()).commit();
                    profileState = 1;
                }else if (profileState ==1){
                    profileImage.setVisibility(View.GONE);
                    if(getSupportFragmentManager().findFragmentById(R.id.Frame_preference) != null) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .remove(getSupportFragmentManager().findFragmentById(R.id.Frame_preference)).commit();
                    }
                    profileState = 0;
                }
            }
        });


        ConfirmPreference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                ageText = age.getSelectedItem().toString();
                if (countyText.isEmpty()){

                    ToastBack("Choose a County.");
                }else if (ageText.isEmpty()){

                    ToastBack("Choose Age.");

                }else if (ageSet(ageText).equals("Select age")){

                    ToastBack("Choose Age.");
                }
                else {
                    Intent toUpdate = new Intent(getApplicationContext(), SelectionActivity.class);
                    toUpdate.putExtra("County",countyText);
                    toUpdate.putExtra("Age",ageSet(ageText));
                    startActivity(toUpdate);


                }

            }
        });

        chooseCounty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (countystate == 0){
                    countystate = 1;
                    frameLayout.setVisibility(View.VISIBLE);
                }else if (countystate == 1){
                    countystate = 0;
                    frameLayout.setVisibility(View.GONE);
                }

            }
        });




        FetchProduct();
        LoaCount();
        LoadUserDetails();
        LoadSelectedCandidate();
        printHashKey();
    }


    private String userNameE,countyE,cityE,contactE,imageE;
    private void LoadUserDetails(){
        YayaRef.document(mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot,
                                @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (documentSnapshot.exists()){

                    userNameE = documentSnapshot.getString("Name");
                    contactE = documentSnapshot.getString("Phone_NO");
                    cityE = documentSnapshot.getString("Street_name");
                    countyE = documentSnapshot.getString("County");
                    imageE = documentSnapshot.getString("UserImage");

                    if (imageE != null){
                        Picasso.with(getApplicationContext()).load(imageE).placeholder(R.drawable.load)
                                .error(R.drawable.profile)
                                .into(profileImage);
                    }else {

                    }

                }
            }
        });

    }



    public void
    printHashKey()
    {

        // Add code to print out the key hash
        try {
            PackageInfo info
                    = getPackageManager().getPackageInfo(
                    "com.android.facebookloginsample",
                    PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {

                MessageDigest md
                        = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:",
                        Base64.encodeToString(
                                md.digest(),
                                Base64.DEFAULT));
            }
        }

        catch (PackageManager.NameNotFoundException e) {
        }

        catch (NoSuchAlgorithmException e) {
        }
    }

   void LoadSelectedCandidate(){

        CandidateRef.whereEqualTo("Employer_ID",mAuth.getCurrentUser().getUid())
                .whereEqualTo("Working_status","selected")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Candidates candidates = document.toObject(Candidates.class);
                        ToastBack(candidates.getCandidate_name());
                    }
                }else {
                    ToastBack(task.getException().getMessage());
                }
            }
        });
    }


    private long TotalCount;
    private void LoaCount(){

        AdminRef.document("No_of_candidates").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    return;
                }
                TotalCount = documentSnapshot.getLong("Total_number");
                prefCount.setText(TotalCount+"");
            }
        });
    }

    private String ageSet (String ageText){
        if (ageText.equals("18-25")){

            ageText = "18";
        }else if (ageText.equals("25-30")){

            ageText = "25";
        }
        else if (ageText.equals("30-35")){

            ageText = "30";
        }
        else if (ageText.equals("35-40")){

            ageText = "35";
        }else if (ageText.equals("40")){

            ageText = "40";
        }
        return ageText;
    }


    private void FetchProduct() {

        Query query = CountyRef
                .orderBy("no", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Counties> transaction = new FirestoreRecyclerOptions.Builder<Counties>()
                .setQuery(query, Counties.class)
                .setLifecycleOwner(this)
                .build();
        adapter = new CountyAdapter(transaction);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(PreferenceActivity.this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(horizontalLayoutManager);
        mRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new CountyAdapter.OnItemCickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Counties  counties = documentSnapshot.toObject(Counties.class);
                String count = documentSnapshot.getId();

                countyText = counties.getCounty();
                OnCounty.setText(countyText);
                OnAge.setText(ageSet(ageText));
                frameLayout.setVisibility(View.GONE);
                countystate=0;
                ConfirmPreference.setEnabled(true);
                ConfirmPreference.setBackgroundResource(R.drawable.btn_round_gradient);
                ConfirmPreference.setTextColor(Color.parseColor("#1C1B2B"));

            }
        });

    }


    private AlertDialog dialog2;
    public void Logout_Alert() {

        Date currentTime = Calendar.getInstance().getTime();
        String date = DateFormat.format("dd MMM ,yyyy | hh:mm a",new Date(String.valueOf(currentTime))).toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog2 = builder.create();
        dialog2.show();
        builder.setMessage("Are you sure to Log out..\n");
        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Log_out();

                    }
                });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog2.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }


    void Log_out(){

        String User_ID = mAuth.getCurrentUser().getUid();

        HashMap<String,Object> store = new HashMap<>();
        store.put("device_token", FieldValue.delete());

        YayaRef.document(User_ID).update(store).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


                if (task.isSuccessful()){

                    mAuth.signOut();
                    Intent logout = new Intent(getApplicationContext(), MainActivity.class);
                    logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(logout);
                    dialog2.dismiss();


                }else {

                    ToastBack( task.getException().getMessage());

                }

            }
        });

    }


    private Toast backToast;
    private void ToastBack(String message){

        backToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        backToast.show();
    }



    private long backPressedTime;
    @Override
    public void onBackPressed() {
        if (backPressedTime + 2500 > System.currentTimeMillis()) {
            backToast.cancel();
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
            super.onBackPressed();
            finish();
            return;
        } else {
            ToastBack("Double tap to exit");
            ConfirmPreference.setEnabled(false);
            ConfirmPreference.setBackgroundResource(R.drawable.btn_round_grey);
            ConfirmPreference.setTextColor(Color.parseColor("#808080"));
            if(getSupportFragmentManager().findFragmentById(R.id.Frame_preference) != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .remove(getSupportFragmentManager().findFragmentById(R.id.Frame_preference)).commit();
            }
            if (profileState == 0){

            }else if (profileState ==1){
                profileImage.setVisibility(View.VISIBLE);
            }

        }
        backPressedTime = System.currentTimeMillis();
    }


}