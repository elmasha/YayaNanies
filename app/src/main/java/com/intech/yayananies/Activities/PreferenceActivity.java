package com.intech.yayananies.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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
import com.intech.yayananies.Interface.RetrofitInterface;
import com.intech.yayananies.Models.Candidates;
import com.intech.yayananies.Models.Counties;
import com.intech.yayananies.Models.EmployerData;
import com.intech.yayananies.Models.ResponseStk;
import com.intech.yayananies.Models.StkQuery;
import com.intech.yayananies.R;
import com.intech.yayananies.TimeAgo;
import com.squareup.picasso.Picasso;


import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PreferenceActivity extends AppCompatActivity {

    private CountyAdapter adapter;
    private RecyclerView mRecyclerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference CountyRef = db.collection("Counties");
    CollectionReference YayaRef = db.collection("Yaya_Employer");
    CollectionReference CandidateRef = db.collection("Yaya_Candidates");
    CollectionReference AdminRef = db.collection("Admin");
    private TextView chooseCounty,OnCounty,OnAge,prefCount,OpenDrawer,ClearPreference,Selected;
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
    int AgeSect;
    private int profileState= 0;




    public  String CheckoutRequestID,ResponseCode,ResultCode,ResponseDescription,ResultDesc;
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "https://yayampesapi.herokuapp.com/";

    private static final long START_TIME_IN_MILLIS_COUNT = 27000;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS_COUNT;


    @Override
    protected void onStart() {
        super.onStart();
        ConfirmPreference.setEnabled(false);
        ConfirmPreference.setBackgroundResource(R.drawable.btn_round_grey);
        ConfirmPreference.setTextColor(Color.parseColor("#808080"));
        FetchProduct();
        countyText = "";
        ageText = "";
        Selected.setText("");
        OnCounty.setText("");
        OnAge.setText("");
    }

    private boolean preference_count;
    private String mpesa_receipt,checkOutReqID;
    private long payment_date;


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
        ClearPreference = findViewById(R.id.clearPref);
        Selected = findViewById(R.id.Selected);
        dl = (DrawerLayout) findViewById(R.id.PrefDrawer);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);


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


        ClearPreference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countyText = "";
                ageText = "";
                OnCounty.setText("");
                OnAge.setText("");
                sum =0;
                uniqueDates.clear();
                prefCount.setText("0");
                AgeSect =0;
                age.clearFocus();
                ConfirmPreference.setEnabled(false);
                ConfirmPreference.setBackgroundResource(R.drawable.btn_round_grey);
                ConfirmPreference.setTextColor(Color.parseColor("#808080"));
            }
        });

        AgeSect = age.getSelectedItemPosition();
        age.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (AgeSect != i){
                    ageText = age.getSelectedItem().toString();
                    OnAge.setText(ageText);
                    ConfirmPreference.setEnabled(true);
                    ConfirmPreference.setBackgroundResource(R.drawable.btn_round_gradient);
                    ConfirmPreference.setTextColor(Color.parseColor("#1C1B2B"));
                }else {
                    OnAge.setText("");
                    ConfirmPreference.setEnabled(false);
                    ConfirmPreference.setBackgroundResource(R.drawable.btn_round_grey);
                    ConfirmPreference.setTextColor(Color.parseColor("#808080"));
                }

                if (ageText != null){

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                AgeSect = 0;
                ConfirmPreference.setEnabled(false);
                OnAge.setText("");
                ConfirmPreference.setBackgroundResource(R.drawable.btn_round_grey);
                ConfirmPreference.setTextColor(Color.parseColor("#808080"));
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
                        getSupportFragmentManager().beginTransaction()
                                .remove(getSupportFragmentManager()
                                        .findFragmentById(R.id.Frame_preference)).commit();
                    }
                    profileState = 0;
                }
            }
        });


        ConfirmPreference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pref_Alert();
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
        ConfirmPreference.setEnabled(false);
        ConfirmPreference.setBackgroundResource(R.drawable.btn_round_grey);
        ConfirmPreference.setTextColor(Color.parseColor("#808080"));
        uniqueDates.clear();

    }


    private ProgressDialog progressStk;
    private void stk(){
        String Amount = "1";
        PesaNO = mpesaNo.getText().toString().trim().substring(1);
        HashMap<String,Object> stk_Push = new HashMap<>();
        stk_Push.put("User_name",userNameE);
        stk_Push.put("user_id",mAuth.getCurrentUser().getUid());
        stk_Push.put("phone","254"+PesaNO);
        stk_Push.put("amount",Amount);


        Call<ResponseStk> callStk = retrofitInterface.stk_push(stk_Push);

        callStk.enqueue(new Callback<ResponseStk>() {
            @Override
            public void onResponse(Call<ResponseStk> call, Response<ResponseStk> response) {

                if (response.code() == 200) {
                    newtime();
                    progressStk = new ProgressDialog(PreferenceActivity.this);
                    progressStk.setCancelable(false);
                    progressStk.setMessage("Processing payment...");
                    progressStk.show();
                    if (dialog_mpesa != null)dialog_mpesa.dismiss();
                    noMpesa.setVisibility(View.VISIBLE);
                    BtnConfirm.setVisibility(View.VISIBLE);
                    progressBarMpesa.setVisibility(View.INVISIBLE);
                    ResponseStk responseStk = response.body();
                    String responeDesc = responseStk.getCustomerMessage();
                    ResponseCode = responseStk.getResponseCode();
                    CheckoutRequestID = responseStk.getCheckoutRequestID();
                    String errorMessage = responseStk.getErrorMessage();
                    String errorCode = responseStk.getErrorCode();
                    Log.i("TAG", "CheckoutRequestID: " + response.body());

                    //Toast.makeText(getContext(), responeDesc , Toast.LENGTH_LONG).show();

                    if (responeDesc != null){
                        if (responeDesc.equals("Success. Request accepted for processing")){
                            ToastBack(responeDesc);
                        }else {

                        }
                    }else {

                        if (errorMessage.equals("No ICCID found on NMS")){
                            ToastBack("Please provide a valid mpesa number.");
                            progressStk.dismiss();
                        }
                        ToastBack(errorMessage);
                        progressStk.dismiss();
                    }


                } else if (response.code() == 404) {
                    ResponseStk errorResponse = response.body();
                    ToastBack(errorResponse.getErrorMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseStk> call, Throwable t) {

                // Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });



    }

    private void newtime(){
        new CountDownTimer(15000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                if (CheckoutRequestID != null){
                    StkQuery(CheckoutRequestID);

                }else {

                    if (progressStk != null)progressStk.dismiss();
                    //Toast.makeText(getContext(), "StkPush Request timeout...", Toast.LENGTH_LONG).show();
                    ToastBack("StkPush Request timeout...");
                    // progressStk.dismiss();
                }
            }
        }.start();
    }

    private void StkQuery(String checkoutRequestID){

        Map<String ,String > stk_Query = new HashMap<>();
        stk_Query.put("checkoutRequestId",checkoutRequestID);
        Call<StkQuery> callQuery = retrofitInterface.stk_Query(stk_Query);

        callQuery.enqueue(new Callback<StkQuery>() {
            @Override
            public void onResponse(Call<StkQuery> call, Response<StkQuery> response) {

                if (response != null){
                    if (response.code()== 200){

                        StkQuery stkQuery1 = response.body();
                        Toast.makeText(getApplicationContext(), ""+ stkQuery1.getResultDesc(), Toast.LENGTH_SHORT).show();
                        Log.i("TAG", "onResponse:"+response.body());
                        String body = stkQuery1.getResultDesc();
                        ResponseDescription = stkQuery1.getResponseDescription();
                        ResultCode = stkQuery1.getResultCode();
                        progressStk.dismiss();
                        pauseTimer();
                        resetTimer();

                        if (ResultCode.equals("0")){
                            new SweetAlertDialog(PreferenceActivity.this,SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Request sent successfully..")
                                    .show();


                        }else if (ResultCode.equals("1032")){
                            new SweetAlertDialog(PreferenceActivity.this,SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("This payment was cancelled")
                                    .setConfirmText("Close")
                                    .show();



                        }else if (ResultCode.equals("1031")){

                            new SweetAlertDialog(PreferenceActivity.this,SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("This payment was cancelled")
                                    .setConfirmText("Close")
                                    .show();



                        }else if (ResultCode.equals("2001")) {
                            new SweetAlertDialog(PreferenceActivity.this,SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Sorry you entered a wrong pin. Try again")
                                    .setConfirmText("Okay")
                                    .show();






                        }else if (ResultCode.equals("1")) {
                            new SweetAlertDialog(PreferenceActivity.this,SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("You current balance is insufficient.")
                                    .setConfirmText("Close")
                                    .show();

                        }


                    }else if (response.code()==404){
                        StkQuery errorResponse = response.body();
                        ToastBack(errorResponse.getErrorMessage());
                    }
                }


            }

            @Override
            public void onFailure(Call<StkQuery> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Now"+ t.getMessage(), Toast.LENGTH_SHORT).show();
                progressStk.dismiss();

            }
        });


    }


    //----Stk Timer------
    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }
            @Override
            public void onFinish() {
                mTimerRunning = false;

                if (CheckoutRequestID != null){
                    StkQuery(CheckoutRequestID);

                }else {

                    //if (progressStk != null)progressStk.dismiss();

                    //Toast.makeText(getContext(), "StkPush Request timeout...", Toast.LENGTH_LONG).show();
                    ToastBack("StkPush Request timeout...");
                    // progressStk.dismiss();
                }

            }
        }.start();
        mTimerRunning = true;

    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;

    }

    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS_COUNT;
        updateCountDownText();

    }
    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
    ///____end stk timer.





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

                    EmployerData employerData = documentSnapshot.toObject(EmployerData.class);

                    userNameE = documentSnapshot.getString("Name");
                    userNameE = employerData.getName();
                    contactE = employerData.getPhone_NO();
                    cityE = employerData.getStreet_name();
                    countyE = employerData.getCounty();
                    imageE = employerData.getUserImage();
//                    preference_count = employerData.isPreference_count();
//                    checkOutReqID = employerData.getCheckOutReqID();
//                    mpesa_receipt = employerData.getMpesa_receipt();
//                    payment_date = Long.parseLong(TimeAgo.getTimeAgo(employerData.getPayment_date().getTime()));

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

    private boolean infoState = false;
   void LoadSelectedCandidate(){

        CandidateRef.whereEqualTo("Employer_ID",mAuth.getCurrentUser().getUid())
                .whereEqualTo("Working_status","selected")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Candidates candidates = document.toObject(Candidates.class);
                        if (candidates.getCandidate_name() != null){

                            infoState = true;

                        }
                        if (infoState = true){

                        }
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
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(4,StaggeredGridLayoutManager.HORIZONTAL));
        mRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new CountyAdapter.OnItemCickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Counties  counties = documentSnapshot.toObject(Counties.class);
                String count = documentSnapshot.getId();
                countyText = counties.getCounty();
                OnCounty.setText(countyText);
                OnAge.setText(ageText);
                Selected.setText("");
                frameLayout.setVisibility(View.GONE);
                countystate=0;
                ConfirmPreference.setEnabled(true);
                ConfirmPreference.setBackgroundResource(R.drawable.btn_round_gradient);
                ConfirmPreference.setTextColor(Color.parseColor("#1C1B2B"));
                uniqueDates.clear();
                FetchAvailable();
            }
        });

    }

    ArrayList<Object> uniqueDates = new ArrayList<Object>();
    int sum ;
    private void FetchAvailable() {



        CandidateRef.whereEqualTo("County",countyText)
                .whereEqualTo("Status","Available")
                .whereGreaterThanOrEqualTo("Age",ageText)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                uniqueDates.add(document.getData());
                                for ( sum = 1; sum < uniqueDates.size(); sum++) {

                                }
                                prefCount.setText(sum+"");
                            }

                        } else {

                        }
                    }
                });

    }
    private void postJob(String pickedCat){


    }

    private AlertDialog dialogAlert;
    public void Dialog_Alert(String msg) {

        Date currentTime = Calendar.getInstance().getTime();
        String date = DateFormat.format("dd MMM ,yyyy | hh:mm a",new Date(String.valueOf(currentTime))).toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialogAlert = builder.create();
        dialogAlert.show();
        builder.setTitle("Attention");
        builder.setIcon(R.drawable.attention);
        builder.setMessage(msg+".\n" +date);

        builder.setPositiveButton("OKAY",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        countyText = "";
                        ageText = "";
                        OnCounty.setText("");
                        OnAge.setText("");
                        sum =0;
                        uniqueDates.clear();
                        prefCount.setText("0");
                        ConfirmPreference.setEnabled(false);
                        ConfirmPreference.setBackgroundResource(R.drawable.btn_round_grey);
                        ConfirmPreference.setTextColor(Color.parseColor("#808080"));
                        dialogAlert.dismiss();
                    }
                });

        builder.setCancelable(false);
        builder.show();
    }

    private AlertDialog dialogPref;
    public void Pref_Alert() {

        Date currentTime = Calendar.getInstance().getTime();
        String date = DateFormat.format("dd MMM ,yyyy | hh:mm a",new Date(String.valueOf(currentTime))).toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialogPref = builder.create();
        dialogPref.show();
        builder.setTitle("Search preference");
        builder.setIcon(R.drawable.search);
        builder.setMessage("You have selected age "+ageText+" and "+countyText +" from your preference.\n");

        builder.setNegativeButton("REFINE SEARCH", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                countyText = "";
                ageText = "";
                OnCounty.setText("");
                OnAge.setText("");
                sum =0;
                AgeSect =0;
                uniqueDates.clear();
                prefCount.setText("0");
                ConfirmPreference.setEnabled(false);
                ConfirmPreference.setBackgroundResource(R.drawable.btn_round_grey);
                ConfirmPreference.setTextColor(Color.parseColor("#808080"));
                dialogPref.dismiss();
            }
        });
        builder.setPositiveButton("PROCEED",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ageText = age.getSelectedItem().toString();
                        if (countyText.isEmpty()){
                            ToastBack("Choose a County.");
                            dialogPref.dismiss();
                        }else if (ageText.isEmpty()){

                            ToastBack("Choose Age.");
                            dialogPref.dismiss();

                        }else if (ageSet(ageText).equals("Select age")){

                            ToastBack("Choose Age.");
                            dialogPref.dismiss();
                        }else if (sum == 0){
                            if (dialogPref!= null)dialogPref.dismiss();
                            Dialog_Alert("There are no candidate from your selected preference");
                        }
                        else {
                            dialogPref.dismiss();
                            Intent toUpdate = new Intent(getApplicationContext(), SelectionActivity.class);
                            toUpdate.putExtra("County",countyText);
                            toUpdate.putExtra("Age",ageText);
                            toUpdate.putExtra("Sum",sum);
                            startActivity(toUpdate);
                            uniqueDates.clear();
                            prefCount.setText("0");

                            //  MpesaDialog();

                        }


                    }
                });

        builder.setCancelable(false);
        builder.show();
    }


    private AlertDialog dialog2;
    public void Logout_Alert() {

        Date currentTime = Calendar.getInstance().getTime();
        String date = DateFormat.format("dd MMM ,yyyy | hh:mm a",new Date(String.valueOf(currentTime))).toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog2 = builder.create();
        dialog2.show();
        builder.setTitle("Log Out");
        builder.setIcon(R.drawable.logout);
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




    private AlertDialog dialog_mpesa;
    private EditText mpesaNo;
    private String PesaNO;
    private Button BtnConfirm;
    private TextView noMpesa,mpesaText;
    private ProgressBar progressBarMpesa;
    private int phoneState = 0;
    private void MpesaDialog(){
        final  AlertDialog.Builder mbuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_mpesa, null);
        mbuilder.setView(mView);
        mpesaNo = mView.findViewById(R.id.MpesaPhone);
        BtnConfirm = mView.findViewById(R.id.verify_MpesaNo);
        progressBarMpesa = mView.findViewById(R.id.progress_MpesaNo);
        mpesaText = mView.findViewById(R.id.TextMpesa);
        noMpesa = mView.findViewById(R.id.no);
        DoubleBounce doubleBounce = new DoubleBounce();
        progressBarMpesa.setIndeterminateDrawable(doubleBounce);

        mpesaText.setText("Are you sure this "+contactE+" is your Mpesa number?");
        mpesaNo.setText(contactE);


        noMpesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phoneState == 0){
                    mpesaText.setText("Please enter is your Mpesa number");
                    mpesaNo.setVisibility(View.VISIBLE);
                    phoneState =1;
                    noMpesa.setText("Close");
                }else if (phoneState == 1){
                    phoneState = 0;
                    if (dialog_mpesa != null) dialog_mpesa.dismiss();
                    noMpesa.setText("No");
                }

            }
        });

        BtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stk();
                startTimer();
                noMpesa.setVisibility(View.INVISIBLE);
                BtnConfirm.setVisibility(View.INVISIBLE);
                progressBarMpesa.setVisibility(View.VISIBLE);

            }
        });
        dialog_mpesa = mbuilder.create();
        dialog_mpesa.show();

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
            Selected.setText("");
            sum =0;
            AgeSect =0;
            uniqueDates.clear();
            prefCount.setText("0");
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