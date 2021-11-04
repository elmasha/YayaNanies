package com.intech.yayananies.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.intech.yayananies.Models.Candidates;
import com.intech.yayananies.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class InfoActivity extends AppCompatActivity {
    private TextView Name,Age,Status,County,Ward,Phone,ID_no,Salary,Back,Residence,InfoText;
    private Button ConfirmDeal,CancelDeal;
    private FloatingActionButton CallBtn,SmsBtn;
    private String ID;
    private CircleImageView candidateProfile;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference CandidateRef = db.collection("Yaya_Candidates");
    CollectionReference YayaRef = db.collection("Yaya_Employer");
    CollectionReference AdminRef = db.collection("Admin");
    private String firstName,image,status,mobile,county,ward,UpdateStatus,
            village,nextOfKin,kinMobile,experience,salary,residence,dob,idNo,gender,age;
    private FirebaseAuth mAuth;
    int PERMISSION_ALL = 20003;
    private Bitmap compressedImageBitmap;
    String[] PERMISSIONS = {android.Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS};
    private LinearLayout linearLayoutDeal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        mAuth = FirebaseAuth.getInstance();
        ID = getIntent().getStringExtra("ID");
        Name = findViewById(R.id.C_name);
        Age = findViewById(R.id.C_age);
        Status = findViewById(R.id.C_status);
        County = findViewById(R.id.C_county);
        Ward = findViewById(R.id.C_ward);
        Phone = findViewById(R.id.C_phone);
        ID_no = findViewById(R.id.C_id);
        Salary = findViewById(R.id.C_salary);
        ConfirmDeal = findViewById(R.id.confirm_deal);
        CancelDeal = findViewById(R.id.cancel_deal);
        CallBtn = findViewById(R.id.C_Call);
        candidateProfile = findViewById(R.id.C_image);
        Back = findViewById(R.id.BackToSect);
        SmsBtn = findViewById(R.id.C_sms);
        linearLayoutDeal = findViewById(R.id.Deal_layout);
        Residence = findViewById(R.id.C_residence);
        InfoText = findViewById(R.id.infoTextView);


        SmsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!hasPermissions(getApplicationContext(), PERMISSIONS)){
                    ActivityCompat.requestPermissions(InfoActivity.this, PERMISSIONS, PERMISSION_ALL);
                }else {

                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage("tel:" +mobile, null, "sms message", null, null);}

            }
        });


        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),SelectionActivity.class));
            }
        });


        ConfirmDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateStatus();
            }
        });

        CancelDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateWokeStatus(ID);
            }
        });

        CallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if(!hasPermissions(getApplicationContext(), PERMISSIONS)){
                    ActivityCompat.requestPermissions(InfoActivity.this, PERMISSIONS, PERMISSION_ALL);
                }else {
                    ToastBack("Calling Agent");
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" +mobile));
                    startActivity(callIntent);
                }

            }
        });

        LoadCandidateDetails();
        LoadUserDetails();
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {


                    return false;
                }

            }
        }
        return true;
    }


    //-----Update Status ----
    private void UpdateStatus(){

            HashMap<String, Object> update = new HashMap<>();
            update.put("Status", "UnAvailable");
            update.put("Working_status", "employed");
            update.put("Employer_name", userNameE);
            update.put("Employer_no", contactE);
            update.put("Employer_county", countyE);
            update.put("Employer_city", cityE);
            update.put("Employer_ID", mAuth.getCurrentUser().getUid());
            CandidateRef.document(ID).update(update).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        ToastBack("Status updated to " + UpdateStatus);
                        getAvailableCounts(UpdateStatus);
                    } else {

                        ToastBack(task.getException().getMessage());
                    }
                }
            });


    }

    private void UpdateWokeStatus(String ID){

        HashMap<String, Object> update = new HashMap<>();
        update.put("Working_status", "");
        CandidateRef.document(ID).update(update).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(getApplicationContext(),SelectionActivity.class));
                    ToastBack("Deal canceled");
                } else {
                    ToastBack(task.getException().getMessage());
                }
            }
        });


    }


    private void getAvailableCounts(String status){
        final DocumentReference sfDocRef = db.collection("Admin").document("No_of_candidates");

        if (status.equals("Available")){

            db.runTransaction(new Transaction.Function<Void>() {
                @Override
                public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                    DocumentSnapshot snapshot = transaction.get(sfDocRef);

                    // Note: this could be done without a transaction
                    //       by updating the population using FieldValue.increment()
                    double newPopulation = snapshot.getLong("Total_number") + 1;
                    transaction.update(sfDocRef, "Total_number", newPopulation);

                    // Success
                    return null;
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Transaction success!");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Transaction failure.", e);
                }
            });

        }else if (status.equals("UnAvailable")){

            db.runTransaction(new Transaction.Function<Void>() {
                @Override
                public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                    DocumentSnapshot snapshot = transaction.get(sfDocRef);

                    // Note: this could be done without a transaction
                    //       by updating the population using FieldValue.increment()
                    double newPopulation = snapshot.getLong("Total_number") - 1;
                    transaction.update(sfDocRef, "Total_number", newPopulation);

                    // Success
                    return null;
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Transaction success!");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Transaction failure.", e);
                }
            });


        }else {

        }

    }

    //----Load details---//
    private long noOfCandidates;
    private String workStatus,employerNo;
    private void LoadCandidateDetails() {

        CandidateRef.document(ID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot,
                                @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (documentSnapshot.exists()){
                    Candidates candidates = documentSnapshot.toObject(Candidates.class);
                    firstName = candidates.getCandidate_name();
                    age = candidates.getAge();
                    county = candidates.getCounty();
                    ward = candidates.getWard();
                    idNo = candidates.getID_no();
                    mobile = candidates.getMobile_no();
                    status = candidates.getStatus();
                    residence = candidates.getResidence();
                    salary = candidates.getSalary();
                    image =candidates.getProfile_image();
                    residence = candidates.getResidence();
                    workStatus = candidates.getWorking_status();
                    employerNo = candidates.getEmployer_no();

                    Name.setText(firstName);
                    Age.setText(age+" yrs");
                    County.setText(county);
                    Ward.setText(ward);
                    ID_no.setText(idNo);
                    Phone.setText(mobile);
                    Status.setText(status);
                    Ward.setText(ward);
                    Salary.setText(salary);
                    Residence.setText(residence);
                    Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.load).error(R.drawable.profile).into(candidateProfile);


                    if (status.equals("UnAvailable")){
                        Status.setTextColor(getResources().getColor(R.color.ColorRed));
                        linearLayoutDeal.setVisibility(View.GONE);
                    }else if (status.equals("Available")){

                        Status.setTextColor(getResources().getColor(R.color.ColorGreen));
                    }


                }
            }
        });

    }
    //...end load details

    private String userNameE,countyE,cityE,contactE;
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

                }
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


    @Override
    public void onBackPressed() {
        if (workStatus.equals("selected")){
//            ToastBack("Please Confirm deal with candidate or Cancel the deal.");
            InfoText.setVisibility(View.VISIBLE);
            InfoText.setText("Please Confirm deal with candidate or Cancel the deal.");
            InfoText.setTextColor(Color.parseColor("#F11E1E"));

            new CountDownTimer(5000, 1000) {
                public void onTick(long millisUntilFinished) {
                }
                public void onFinish() {
                    InfoText.setVisibility(View.GONE);
                }
            }.start();


        }else if (workStatus.equals("")){
            Intent logout = new Intent(getApplicationContext(), MainActivity.class);
            logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logout);
            super.onBackPressed();
        }

    }


}