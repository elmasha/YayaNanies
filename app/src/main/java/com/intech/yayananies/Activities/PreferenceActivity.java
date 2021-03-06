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

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.intech.yayananies.Adpater.CountyAdapter;
import com.intech.yayananies.Fragments.NotificationFragment;
import com.intech.yayananies.Fragments.ProfileFragment;
import com.intech.yayananies.Interface.RetrofitInterface;
import com.intech.yayananies.Models.Candidates;
import com.intech.yayananies.Models.Counties;
import com.intech.yayananies.Models.EmployerData;
import com.intech.yayananies.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE;

public class PreferenceActivity extends AppCompatActivity {

    private CountyAdapter adapter;
    private RecyclerView mRecyclerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference CountyRef = db.collection("Counties");
    CollectionReference YayaRef = db.collection("Yaya_Employer");
    CollectionReference CandidateRef = db.collection("Yaya_Candidates");
    CollectionReference AdminRef = db.collection("Admin");
    private TextView chooseCounty,OnCounty,OnAge,prefCount,OpenDrawer,ClearPreference,Selected,MyDays;
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

    private Uri ImageUri;



    public  String CheckoutRequestID,ResponseCode,ResultCode,ResponseDescription,ResultDesc;
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "https://yayampesapi.herokuapp.com/";

    private static final long START_TIME_IN_MILLIS_COUNT = 27000;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS_COUNT;



    private UploadTask uploadTask;
    FirebaseStorage storage;
    StorageReference storageReference;
    int PERMISSION_ALL = 20003;
    private Bitmap compressedImageBitmap;
    String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE};

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
    private String mpesa_receipt,days;
    private String checkOutReqID;
    private Date payDay;
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
        MyDays = findViewById(R.id.days);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
        ///---initiate Storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


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

                if (imageE != null){
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
                }else {

                    Image_Alert();
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

    public int getDifferenceDays(Date d1, Date d2) {
        int daysdiff = 0;
        long diff = d2.getTime() - d1.getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000) + 1;
        daysdiff = (int) diffDays;
        return Math.abs(daysdiff);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    //data.getData returns the content URI for the selected Image
                    ImageUri = result.getUri();
                    if (ImageUri != null){
                        uploadImage(ImageUri);
                    }

                    break;
            }
    }

    private ProgressDialog progressDialog3;
    private void uploadImage(Uri imageUri) {
        if (dialogImage != null) dialogImage.dismiss();
        progressDialog3 = new ProgressDialog(PreferenceActivity.this);
        progressDialog3.setMessage("Please wait uploading...");
        progressDialog3.show();
        progressDialog3.setCancelable(false);
        File newimage = new File(imageUri.getPath());
        try {
            Compressor compressor = new Compressor(this);
            compressor.setMaxHeight(200);
            compressor.setMaxWidth(200);
            compressor.setQuality(10);
            compressedImageBitmap = compressor.compressToBitmap(newimage);
        } catch (IOException e) {
            e.printStackTrace();
        }


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compressedImageBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        final byte[] data = baos.toByteArray();


        final StorageReference ref = storageReference.child("Users/thumbs" + UUID.randomUUID().toString());
        uploadTask = ref.putBytes(data);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    String profileImage = downloadUri.toString();

                    HashMap<String,Object> registerB = new HashMap<>();
                    registerB.put("UserImage",profileImage);

                    YayaRef.document(mAuth.getCurrentUser().getUid()).update(registerB).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                ToastBack("Image uploaded successful");

                                progressDialog3.dismiss();
                            }else {
                                ToastBack(task.getException().getMessage());
                                progressDialog3.dismiss();
                            }
                        }
                    });




                }else {

                    ToastBack(task.getException().getMessage());
                    progressDialog3.dismiss();
                }
            }
        });


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
                    payDay = employerData.getPayment_date();
                    Date now = new Date();
                    if (payDay != null){
                        MyDays.setText(getDifferenceDays(now,payDay)+"");
                    }


                    if (imageE != null){
//                        Picasso.with(getApplicationContext()).load(imageE).placeholder(R.drawable.load)
//                                .error(R.drawable.profile)
//                                .into(profileImage);
                    }else {

                        profileImage.setBackgroundResource(R.drawable.add_user);
                        profileImage.setBackgroundResource(R.drawable.profile);
                    }

                }
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
                            progressDialog33.dismiss();
                        }else {
                            ToastBack(task.getException().getMessage());
                            progressDialog33.dismiss();
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

    private AlertDialog dialogImage;
    public void Image_Alert() {

        Date currentTime = Calendar.getInstance().getTime();
        String date = DateFormat.format("dd MMM ,yyyy | hh:mm a",new Date(String.valueOf(currentTime))).toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialogImage = builder.create();
        dialogImage.show();
        builder.setTitle("Upload image");
        builder.setIcon(R.drawable.ic_add_a_photo_60);
        builder.setMessage("You have no profile image do you wish to upload..\n");
        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                                .setMinCropResultSize(512,512)
                                .setAspectRatio(1,1)
                                .start(PreferenceActivity.this);

                    }
                });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialogImage.dismiss();
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