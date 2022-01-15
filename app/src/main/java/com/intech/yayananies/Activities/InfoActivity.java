package com.intech.yayananies.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.text.format.DateFormat;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.intech.yayananies.Models.Bureau;
import com.intech.yayananies.Models.Candidates;
import com.intech.yayananies.Models.EmployerData;
import com.intech.yayananies.R;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class InfoActivity extends AppCompatActivity {
    private TextView Name,Age,Status,County,Ward,Phone,ID_no,Salary,Residence,OutBureauNo,OutBureauName,InfoText;
    private Button ConfirmDeal,CancelDeal;
    private FloatingActionButton CallBtn,SmsBtn;
    private String ID;
    private CircleImageView candidateProfile;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference CandidateRef = db.collection("Yaya_Candidates");
    CollectionReference YayaRef = db.collection("Yaya_Employer");
    CollectionReference AdminRef = db.collection("Admin");
    CollectionReference BureauRef = db.collection("Yaya_Bureau");
    private String firstName,image,status,mobile,county,ward,UpdateStatus,
            village,nextOfKin,kinMobile,experience,salary,residence,dob,idNo,gender,age,Bureau_No,Bureau_Name;
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
        SmsBtn = findViewById(R.id.C_sms);
        linearLayoutDeal = findViewById(R.id.Deal_layout);
        Residence = findViewById(R.id.C_residence);
        InfoText = findViewById(R.id.infoTextView);
        OutBureauNo = findViewById(R.id.BureauNo);
        OutBureauName = findViewById(R.id.BureauName);


        SmsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!hasPermissions(getApplicationContext(), PERMISSIONS)){
                    ActivityCompat.requestPermissions(InfoActivity.this, PERMISSIONS, PERMISSION_ALL);
                }else {

                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage("tel:" +Bureau_No, null, "sms message", null, null);}

            }
        });


        ConfirmDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogDeal_Alert1("If you don't agree on this candidate please note the payment remains valid for 3 days");
            }
        });

        CancelDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               DialogDeal_Alert("Selecting this option will take you back to pick you preference afresh. \n Your payments is valid for 3 days");
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
                    callIntent.setData(Uri.parse("tel:" +"+"+Bureau_No));
                    startActivity(callIntent);
                }

            }
        });

        LoadCandidateDetails();
        LoadUserDetails();
    }



    private AlertDialog dialogOnDealConfirm;
    public void DialogConfirmDeal_Alert() {

        Date currentTime = Calendar.getInstance().getTime();
        String date = DateFormat.format("dd MMM ,yyyy | hh:mm a",new Date(String.valueOf(currentTime))).toString();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialogOnDealConfirm = builder.create();
        dialogOnDealConfirm.show();
        builder.setTitle("Deal confirmed");
        builder.setIcon(R.drawable.agree);
        builder.setCancelable(false);
        builder.setMessage("This deal has been confirmed successfully .\n" +date);

        builder.setPositiveButton("OKAY",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialogOnDealConfirm.dismiss();
                        startActivity(new Intent(getApplicationContext(),PreferenceActivity.class));
                    }
                });

        builder.setCancelable(false);
        builder.show();
    }


    private AlertDialog dialogOnDeal1;
    public void DialogDeal_Alert1(String msg) {

        Date currentTime = Calendar.getInstance().getTime();
        String date = DateFormat.format("dd MMM ,yyyy | hh:mm a",new Date(String.valueOf(currentTime))).toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialogOnDeal1 = builder.create();
        dialogOnDeal1.show();
        builder.setTitle("Attention");
        builder.setIcon(R.drawable.attention);
        builder.setMessage(msg+".\n" +date);

        builder.setPositiveButton("PROCEED",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialogOnDeal1.dismiss();
                        if (noOfCandidate == 1){
                            if (dialogOnDeal1 != null)dialogOnDeal1.dismiss();
                            Dialog_Alert("You reached maximum Number of candidates");
                        }else {
                            UpdateStatus();
                        }
                    }
                });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogOnDeal1.dismiss();
            }
        });

        builder.setCancelable(false);
        builder.show();
    }


    private AlertDialog dialogOnDeal;
    public void DialogDeal_Alert(String msg) {

        Date currentTime = Calendar.getInstance().getTime();
        String date = DateFormat.format("dd MMM ,yyyy | hh:mm a",new Date(String.valueOf(currentTime))).toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialogOnDeal = builder.create();
        dialogOnDeal.show();
        builder.setTitle("Attention");
        builder.setIcon(R.drawable.attention);
        builder.setMessage(msg+".\n" +date);

        builder.setPositiveButton("OKAY",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialogOnDeal.dismiss();
                        UpdateWokeStatus(ID);
                    }
                });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogOnDeal.dismiss();
            }
        });

        builder.setCancelable(false);
        builder.show();
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
                        dialogAlert.dismiss();
                    }
                });

        builder.setCancelable(false);
        builder.show();
    }


    private AlertDialog dialogConfirm;
    public void Discharge_Alert(String candidate) {

        Date currentTime = Calendar.getInstance().getTime();
        String date = DateFormat.format("dd MMM ,yyyy | hh:mm a",new Date(String.valueOf(currentTime))).toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialogConfirm = builder.create();
        dialogConfirm.show();
        builder.setTitle("Discharge candidate");
        builder.setIcon(R.drawable.discharge);
        builder.setMessage("Would you like to discharge "+candidate+".\n" +date);
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialogConfirm.dismiss();
            }
        });
        builder.setPositiveButton("PROCEED",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        builder.setCancelable(false);
        builder.show();
    }


    private AlertDialog dialog2;
    public void Deal_Alert() {

        Date currentTime = Calendar.getInstance().getTime();
        String date = DateFormat.format("dd MMM ,yyyy | hh:mm a",new Date(String.valueOf(currentTime))).toString();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog2 = builder.create();
        dialog2.show();
        builder.setIcon(R.drawable.attention);
        builder.setCancelable(false);
        builder.setPositiveButton("OKAY",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog2 != null)dialog2.dismiss();
                    }
                });
        builder.setTitle("Attention");
        builder.setMessage("By clicking 'CONFIRM DEAL' you confirm that you have reached an agreement if not please 'CANCEL DEAL'..\n" +
                "\n"
                +date);
        builder.show();
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
    private ProgressDialog progressDialog;
    private void UpdateStatus(){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();

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
                        if ( dialogOnDeal1 != null)dialogOnDeal1.dismiss();
                        candidateCount();
                    } else {
                        if ( dialogOnDeal1 != null)dialogOnDeal1.dismiss();
                        ToastBack(task.getException().getMessage());
                        progressDialog.dismiss();
                    }
                }
            });


    }



    private void NotifyUser() {
        HashMap<String ,Object> notify = new HashMap<>();
        notify.put("title","Confirmed deal");
        notify.put("desc","You have deal confirmed between "+Bureau_Name+" and you");
        notify.put("type","Candidate enroll.");
        notify.put("to",mAuth.getCurrentUser().getUid());
        notify.put("from",mAuth.getCurrentUser().getUid());
        notify.put("timestamp", FieldValue.serverTimestamp());

        HashMap<String ,Object> notify2 = new HashMap<>();
        notify2.put("title","Confirmed deal");
        notify2.put("desc","You have deal confirmed between "+userNameE+" and you on candidate "+firstName);
        notify2.put("type","Candidate closed deal.");
        notify2.put("to",BureauID);
        notify2.put("from",mAuth.getCurrentUser().getUid());
        notify2.put("timestamp", FieldValue.serverTimestamp());

        BureauRef.document(BureauID).collection("Notifications").document().set(notify2);
        YayaRef.document(mAuth.getCurrentUser().getUid()).collection("Notifications")
                .document().set(notify)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            DialogConfirmDeal_Alert();
                        }else {
                            ToastBack(task.getException().getMessage());
                        }
                    }
                });


    }


    private void candidateCount() {
        HashMap<String, Object> deal = new HashMap<>();
        deal.put("CandidatesCount", 1);
        YayaRef.document(mAuth.getCurrentUser().getUid()).update(deal).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    NotifyUser();
                   // getAvailableCounts(UpdateStatus);
                }else {
                    ToastBack(task.getException().getMessage());
                }
            }
        });
    }
    private ProgressDialog progressDialog2;
    private void UpdateWokeStatus(String ID){
        if (dialogOnDeal != null)dialogOnDeal.dismiss();
        progressDialog2 = new ProgressDialog(this);
        progressDialog2.setMessage("Please wait...");
        progressDialog2.setCancelable(false);
        progressDialog2.show();
        HashMap<String, Object> update = new HashMap<>();
        update.put("Working_status", "");
        CandidateRef.document(ID).update(update).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    ToastBack("Deal canceled");
                    startActivity(new Intent(getApplicationContext(),PreferenceActivity.class));
                } else {
                    ToastBack(task.getException().getMessage());
                    progressDialog2.dismiss();
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
    private long noOfCandidate;
    private String workStatus,employerNo,BureauID;
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
                    BureauID = candidates.getUser_ID();
                    Bureau_Name = candidates.getBureauName();
                    Bureau_No = candidates.getBureauNo();

                    LoadUserDetailsBureau(BureauID);

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
                    if (Bureau_Name != null | Bureau_No != null){
                        OutBureauNo.setText("+"+Bureau_No);
                        OutBureauName.setText(Bureau_Name);
                    }

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

                    EmployerData employerData = documentSnapshot.toObject(EmployerData.class);
                    userNameE = documentSnapshot.getString("Name");
                    contactE = documentSnapshot.getString("Phone_NO");
                    cityE = documentSnapshot.getString("Street_name");
                    countyE = documentSnapshot.getString("County");
                    noOfCandidate = employerData.getCandidatesCount();


                }
            }
        });

    }



    private String BureauNo;
    private void LoadUserDetailsBureau(String UiD){
        YayaRef.document(UiD).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot,
                                @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (documentSnapshot.exists()){

                    Bureau employerData = documentSnapshot.toObject(Bureau.class);
                    BureauNo = employerData.getPhone_NO();
                    OutBureauNo.setText("+"+BureauNo);

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



    private void newTimer (){
        new CountDownTimer(8000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                InfoText.setVisibility(View.GONE);
                if (dialog2 != null)dialog2.dismiss();
            }
        }.start();

    }


    @Override
    public void onBackPressed() {
        if (workStatus.equals("selected")){
//            ToastBack("Please Confirm deal with candidate or Cancel the deal.");
//            InfoText.setVisibility(View.VISIBLE);
            InfoText.setText("Please Confirm deal with candidate or Cancel the deal.");
            InfoText.setTextColor(Color.parseColor("#FFC107"));
            Deal_Alert();
            newTimer();



        }else if (workStatus.equals("")){
            Intent logout = new Intent(getApplicationContext(), MainActivity.class);
            logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logout);
            super.onBackPressed();
        }

    }


}