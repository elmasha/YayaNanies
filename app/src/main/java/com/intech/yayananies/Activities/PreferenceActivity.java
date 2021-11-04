package com.intech.yayananies.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.intech.yayananies.Adpater.CountyAdapter;
import com.intech.yayananies.Models.Candidates;
import com.intech.yayananies.Models.Counties;
import com.intech.yayananies.R;
import com.squareup.picasso.Picasso;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class PreferenceActivity extends AppCompatActivity {

    private CountyAdapter adapter;
    private RecyclerView mRecyclerView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference CountyRef = db.collection("Counties");
    CollectionReference YayaRef = db.collection("Yaya_Employer");
    CollectionReference CandidateRef = db.collection("Yaya_Candidates");
    CollectionReference AdminRef = db.collection("Admin");
    private TextView chooseCounty,OnCounty,OnAge,prefCount;
    private Spinner age;
    private String countyText,ageText;
    private FrameLayout frameLayout;
    private Button ConfirmPreference;
    private CircleImageView profileImage;
    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        FetchProduct();
        countyText = "";
        ageText = "";
        OnCounty.setText("");
        OnAge.setText("");
    }

    int countystate = 0;

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

        ConfirmPreference = findViewById(R.id.confirm_preference);


        age.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ageText = age.getSelectedItem().toString();

                OnAge.setText(ageText);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
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

            }
        });

    }

    private Toast backToast;
    private void ToastBack(String message){


        backToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        View view = backToast.getView();

        //Gets the actual oval background of the Toast then sets the colour filter
        view.getBackground().setColorFilter(Color.parseColor("#0BF4DE"), PorterDuff.Mode.SRC_IN);

        //Gets the TextView from the Toast so it can be editted
        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(Color.parseColor("#1C1B2B"));
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
        }
        backPressedTime = System.currentTimeMillis();
    }


}