package com.intech.yayananies.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.intech.yayananies.Adpater.CandidateAdapter;
import com.intech.yayananies.Interface.RetrofitInterface;
import com.intech.yayananies.Models.Candidates;
import com.intech.yayananies.Models.EmployerData;
import com.intech.yayananies.Models.ResponseStk;
import com.intech.yayananies.Models.Result;
import com.intech.yayananies.Models.StkQuery;
import com.intech.yayananies.R;
import com.intech.yayananies.TimeAgo;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SelectionActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference BureauRef = db.collection("Yaya_Bureau");
    CollectionReference CandidateRef = db.collection("Yaya_Candidates");
    CollectionReference YayaRef = db.collection("Yaya_Employer");
    private FirebaseAuth mAuth;
    private TextView textUser,textEmail,textBureauName,toPref,
            selectName,selectSalary,selectCounty,selectWard,selectAge;
    private TextView CloseFrame;
    private String County,Age;
    private FloatingActionButton addCandidate;
    private CandidateAdapter adapter;
    private RecyclerView mRecyclerView;
    private FrameLayout frameLayoutPass;
    private LinearLayout linearLayoutDetails;
    private Button ChangePref,ConfirmPref;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CircleImageView selectedImage;



    public  String CheckoutRequestID,ResponseCode,ResultCode,ResponseDescription,ResultDesc;
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "https://yayampesapii.herokuapp.com/";

    private static final long START_TIME_IN_MILLIS_COUNT = 27000;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS_COUNT;
    private int SUM;

    private boolean preference_count;
    private String mpesa_receipt,checkOutReqID;
    private String payment_date;



    @Override
    protected void onStart() {
        super.onStart();
        LoadUserDetails();
        FetchProduct();
        LoadAPI();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        mAuth = FirebaseAuth.getInstance();
        mRecyclerView = findViewById(R.id.recycler_selection);
        frameLayoutPass = findViewById(R.id.Selection_pass);
        County = getIntent().getStringExtra("County");
        Age = getIntent().getStringExtra("Age");
        SUM = getIntent().getIntExtra("Sum",0);
        ConfirmPref = findViewById(R.id.confirm_candidate);
        ChangePref = findViewById(R.id.change_preference);
        toPref = findViewById(R.id.BackToPref);
        selectName = findViewById(R.id.Select_name);
        selectedImage = findViewById(R.id.Select_image);
        selectSalary = findViewById(R.id.Select_salary);
        selectCounty = findViewById(R.id.Select_county);
        selectWard = findViewById(R.id.Select_ward);
        CloseFrame = findViewById(R.id.Close_frame);
        linearLayoutDetails = findViewById(R.id.Deal_layout);
        selectAge  = findViewById(R.id.Select_age);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);


        CloseFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frameLayoutPass.setVisibility(View.GONE);
            }
        });

        swipeRefreshLayout = findViewById(R.id.swipeSelection);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FetchProduct();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);

            }
        });


        toPref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), PreferenceActivity.class));
            }
        });

        ChangePref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PreferenceActivity.class));
            }
        });

        ConfirmPref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date = new Date();
                if (id2 != null){

                    if (payDay != null){
                        ToastBack(getDifferenceDays(date,payDay)+"");
                        if (getDifferenceDays(date,payDay) > 1 ){
                            ToastBack(getDifferenceDays(date,payDay)+" Days");
                        }else {
                            ToastBack(getDifferenceDays(date,payDay)+" Day");
                        }


                        if (getDifferenceDays(date,payDay) >= 3){
                            FormatReceipt();
                        }else if (getDifferenceDays(date,payDay) <= 3){

                            if(mpesa_receipt != null){
                                Intent toUpdate = new Intent(getApplicationContext(), InfoActivity.class);
                                toUpdate.putExtra("ID",id2);
                                startActivity(toUpdate);
                            }else {
                                MpesaDialog();
                            }


                        }

                    }else {
                        MpesaDialog();
                    }

                }else {
                    ToastBack("Please select a candidate.");
                }


            }
        });



        FetchProduct();
        LoadUserDetails();
        LoadAPI();
    }


    private void LoadAPI() {

        Call<Result> callStk = retrofitInterface.getResult();
        callStk.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.code() == 200){
                    Result responseStk = response.body();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });

    }

    ProgressDialog progressDialog33;
    private void FormatReceipt() {
        progressDialog33 = new ProgressDialog(this);
        progressDialog33.setMessage("Please wait..");
        progressDialog33.setCancelable(false);
        progressDialog33.show();
        HashMap<String ,Object> format = new HashMap<>();
        format.put("preference_count",false);
        format.put("mpesa_receipt",null);
        format.put("checkOutReqID",null);
        format.put("payment_date", null);

        YayaRef.document(mAuth.getCurrentUser().getUid()).update(format)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    MpesaDialog();
                    NotifyUser();
                    progressDialog33.dismiss();
                }else {
                    ToastBack(task.getException().getMessage());
                    progressDialog33.dismiss();
                }
            }
        });
    }

    private void NotifyUser() {
        HashMap<String ,Object> notify = new HashMap<>();
        notify.put("title","Selection fee");
        notify.put("desc","You have reached maximum number of days");
        notify.put("type","Selection fee activation");
        notify.put("to",mAuth.getCurrentUser().getUid());
        notify.put("from",mAuth.getCurrentUser().getUid());
        notify.put("timestamp",FieldValue.serverTimestamp());

        YayaRef.document(mAuth.getCurrentUser().getUid()).collection("Notifications")
                .document(mAuth.getCurrentUser().getUid()).set(notify)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                        }else {
                            ToastBack(task.getException().getMessage());
                        }
                    }
                });


    }


    private AlertDialog dialog_mpesa;
    private EditText mpesaNo;
    private String PesaNO;
    private Button BtnConfirm;
    private TextView noMpesa,mpesaText,mpesaText2;
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
        mpesaText2 = mView.findViewById(R.id.TextMpesa2);
        noMpesa = mView.findViewById(R.id.no);
        DoubleBounce doubleBounce = new DoubleBounce();
        progressBarMpesa.setIndeterminateDrawable(doubleBounce);

        mpesaText.setText("Are you sure this "+contactE+" is your Mpesa number?");
        mpesaText2.setText("For you to select this candidate you will require to pay amount =/100 for 3 days free selection.");
        mpesaNo.setText(contactE);

        noMpesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phoneState == 0){
                    mpesaText.setText("Please enter your Mpesa number");
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
                    progressStk = new ProgressDialog(SelectionActivity.this);
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
                            new SweetAlertDialog(SelectionActivity.this,SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Payment was successful..")
                                    .show();
                            Intent toInfo = new Intent(getApplicationContext(),InfoActivity.class);
                            toInfo.putExtra("ID",id2);
                            startActivity(toInfo);


                        }else if (ResultCode.equals("1032")){
                            new SweetAlertDialog(SelectionActivity.this,SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("This payment was cancelled")
                                    .setConfirmText("Close")
                                    .show();



                        }else if (ResultCode.equals("1031")){

                            new SweetAlertDialog(SelectionActivity.this,SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("This payment was cancelled")
                                    .setConfirmText("Close")
                                    .show();



                        }else if (ResultCode.equals("2001")) {
                            new SweetAlertDialog(SelectionActivity.this,SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Sorry you entered a wrong pin. Try again")
                                    .setConfirmText("Okay")
                                    .show();






                        }else if (ResultCode.equals("1")) {
                            new SweetAlertDialog(SelectionActivity.this,SweetAlertDialog.WARNING_TYPE)
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



    public int getDifferenceDays(Date d1, Date d2) {
        int daysdiff = 1;
        long diff = d2.getTime() - d1.getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000) + 1;
        daysdiff = (int) diffDays;
        return Math.abs(daysdiff);
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
    ///____end stk timer.

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }




    private String userNameE,countyE,cityE,contactE,imageE,emailE,idE,county,street;
    private Date payDay;
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
                    emailE = documentSnapshot.getString("Email");
                    idE = documentSnapshot.getString("ID_no");
                    EmployerData employerData = documentSnapshot.toObject(EmployerData.class);
                    county = employerData.getCounty();
                    street = employerData.getStreet_name();
                    mpesa_receipt = employerData.getMpesa_receipt();
                    payDay = employerData.getPayment_date();



                }
            }
        });

    }



    private  String id2;
    private void FetchProduct() {

        Query query = CandidateRef.whereEqualTo("County",County)
                .whereEqualTo("Status","Available")
                .whereGreaterThanOrEqualTo("Age", Age)
                .limit(40);
        FirestoreRecyclerOptions<Candidates> transaction = new FirestoreRecyclerOptions.Builder<Candidates>()
                .setQuery(query, Candidates.class)
                .setLifecycleOwner(this)
                .build();
        adapter = new CandidateAdapter(transaction);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager LayoutManager
                = new LinearLayoutManager(SelectionActivity.this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        mRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new CandidateAdapter.OnItemCickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Candidates  candidates = documentSnapshot.toObject(Candidates.class);
                id2 = documentSnapshot.getId();
                frameLayoutPass.setVisibility(View.VISIBLE);
                selectName.setText(candidates.getCandidate_name());
                selectCounty.setText(candidates.getCounty());
                selectWard.setText(candidates.getWard());
                selectSalary.setText(candidates.getSalary());
                selectAge.setText(candidates.getAge());
                if (candidates.getProfile_image() != null){
                    Picasso.with(getApplicationContext()).load(candidates.getProfile_image()).placeholder(R.drawable.load)
                            .error(R.drawable.profile)
                            .into(selectedImage);
                }

                UpdateStatus(id2);

            }
        });

    }

    //-----Update Status ----
    private void UpdateStatus(String ID){

        HashMap<String, Object> update = new HashMap<>();
        update.put("Working_status", "selected");
        update.put("date_selected", FieldValue.serverTimestamp());
        CandidateRef.document(ID).update(update).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                } else {
                    ToastBack(task.getException().getMessage());
                }
            }
        });


    }



    private Toast backToast;
    private void ToastBack(String message){


        backToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
//        View view = backToast.getView();
//
//        //Gets the actual oval background of the Toast then sets the colour filter
//        view.getBackground().setColorFilter(Color.parseColor("#0BF4DE"), PorterDuff.Mode.SRC_IN);
//
//        //Gets the TextView from the Toast so it can be editted
//        TextView text = view.findViewById(android.R.id.message);
//        text.setTextColor(Color.parseColor("#1C1B2B"));
        backToast.show();
    }

}