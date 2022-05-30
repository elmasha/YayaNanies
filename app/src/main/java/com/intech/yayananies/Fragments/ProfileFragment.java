package com.intech.yayananies.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chootdev.csnackbar.Align;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.getbase.floatingactionbutton.FloatingActionButton;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.intech.yayananies.Activities.InfoActivity;
import com.intech.yayananies.Activities.MainActivity;
import com.intech.yayananies.Activities.ProfileActivity;
import com.intech.yayananies.Activities.SelectionActivity;
import com.intech.yayananies.Adpater.MyCandidateAdapter;
import com.intech.yayananies.Models.Candidates;
import com.intech.yayananies.Models.EmployerData;
import com.intech.yayananies.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE;
import static com.theartofdev.edmodo.cropper.CropImage.toOvalBitmap;


public class ProfileFragment extends Fragment {
View root;
    private CircleImageView profImage,AddProfileImage;
    private TextView EmployerName,EmployerID,EmployerEmail,EmployerCounty,logout,closeEdit;

    private RecyclerView mRecyclerView;
    private MyCandidateAdapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference CountyRef = db.collection("Counties");
    CollectionReference YayaRef = db.collection("Yaya_Employer");
    CollectionReference CandidateRef = db.collection("Yaya_Candidates");
    private FirebaseAuth mAuth;
    private EditText EditUserName,EditEmail,EditPhone,EditLocation,EditStreet;
    private Button BtnSaveChanges;
    private FloatingActionButton editBtn,AddImage;
    private int editState = 0;
    private LinearLayout editLayout,primeLayout,ErrorLayout;
    private String userName,email,phone,location,userImage,street,ID;
    private Uri ImageUri;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        LoadUserDetails();
        FetchProduct();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       root = inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        EmployerEmail = root.findViewById(R.id.E_email);
        EmployerID = root.findViewById(R.id.E_id);
        profImage = root.findViewById(R.id.E_Profileimage);
        EmployerName = root.findViewById(R.id.E_name);
        EmployerCounty = root.findViewById(R.id.E_county);
        logout = root.findViewById(R.id.LogOut);
        mRecyclerView = root.findViewById(R.id.recycler_candidates);
        EditEmail = root.findViewById(R.id.edit_Tf_email);
        EditUserName = root.findViewById(R.id.edit_Tf_name);
        EditPhone = root.findViewById(R.id.edit_Tf_phone);
        EditLocation = root.findViewById(R.id.edit_Tf_location);
        EditStreet = root.findViewById(R.id.edit_Tf_street);
        editBtn = root.findViewById(R.id.edit_Tf_profile);
        editLayout = root.findViewById(R.id.EditView);
        BtnSaveChanges = root.findViewById(R.id.edit_Tf_saveChanges);
        closeEdit = root.findViewById(R.id.closeEdit);
        AddProfileImage = root.findViewById(R.id.Add_Profileimage);
        AddImage = root.findViewById(R.id.AddImage);
        ErrorLayout = root.findViewById(R.id.ErrorLayout);



        ProfileFragment profileFragment = new ProfileFragment();
        AddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512,512)
                        .setAspectRatio(1,1)
                        .start(getActivity());

            }
        });

        closeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editState == 1){
                    editLayout.setVisibility(View.VISIBLE);
                    editState =0;
                }else if (editState == 0){
                    editLayout.setVisibility(View.GONE);
                    editState =1;
                }
            }
        });
        BtnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validation()){

                }else {
                    SaveChanges();
                }
            }
        });


        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editState == 0){
                    editLayout.setVisibility(View.VISIBLE);
                    editState =1;
                }else if (editState == 1){
                    editLayout.setVisibility(View.GONE);
                    editState =0;
                }
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logout_Alert();
            }
        });


        LoadUserDetails();
        FetchProduct();
        EmployerCount();
        return root;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    //data.getData returns the content URI for the selected Image
                    ImageUri = result.getUri();
                    AddProfileImage.setImageURI(ImageUri);
                    ToastBack(ImageUri+"");
                    break;
            }
    }

    private AlertDialog dialogDischarge;
    public void Discharge_Alert(String candidate) {

        Date currentTime = Calendar.getInstance().getTime();
        String date = DateFormat.format("dd MMM ,yyyy | hh:mm a",new Date(String.valueOf(currentTime))).toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        dialogDischarge = builder.create();
        dialogDischarge.show();
        builder.setTitle("Discharge candidate");
        builder.setIcon(R.drawable.discharge);
        builder.setMessage("Would you like to discharge "+candidate+".\n \n By discharging "+candidate+ " will be available for taking by others\n  \n" +date);
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                    dialogDischarge.dismiss();
            }
        });
        builder.setPositiveButton("PROCEED",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        UpdateStatus(ID);
                    }
                });

        builder.setCancelable(false);
        builder.show();
    }


    //-----Update Status ----
    private void UpdateStatus(String id){

        HashMap<String, Object> update = new HashMap<>();
        update.put("Status", "Available");
        update.put("Working_status", "");
        update.put("Employer_name", "");
        update.put("Employer_no", "");
        update.put("Employer_county", "");
        update.put("Employer_city", "");
        update.put("Employer_ID", "");
        CandidateRef.document(id).update(update).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    if (CandidateNo == 1){
                        candidateCount();
                    }else {
                        dialogDischarge.dismiss();
                        showSnackBarOnline(getContext(),candidate+" discharged successful");
                    }
                } else {
                    dialogDischarge.dismiss();
                    ToastBack(task.getException().getMessage());
                }
            }
        });


    }

    private void NotifyUser() {
        HashMap<String ,Object> notify = new HashMap<>();
        notify.put("title","Discharge candidate");
        notify.put("desc","You have successfully discharged "+candidate);
        notify.put("type","Discharge.");
        notify.put("to",mAuth.getCurrentUser().getUid());
        notify.put("from",mAuth.getCurrentUser().getUid());
        notify.put("timestamp", FieldValue.serverTimestamp());

        YayaRef.document(mAuth.getCurrentUser().getUid()).collection("Notifications")
                .document().set(notify)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            dialogDischarge.dismiss();
                            showSnackBarOnline(getContext(),candidate+" discharged successful");
                        }else {
                            ToastBack(task.getException().getMessage());
                        }
                    }
                });


    }

    private void candidateCount() {
        HashMap<String, Object> deal = new HashMap<>();
        deal.put("CandidatesCount", 0);
        YayaRef.document(mAuth.getCurrentUser().getUid()).update(deal).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    NotifyUser();
                }else {
                    ToastBack(task.getException().getMessage());
                    dialogDischarge.dismiss();
                }
            }
        });
    }

    private void SaveChanges() {

        HashMap<String,Object> store = new HashMap<>();
        store.put("Name", userName);
        store.put("Street_name",street);
        store.put("County",county);
        store.put("Email",email);
        store.put("Phone_NO",phone);



        YayaRef.document(mAuth.getCurrentUser().getUid()).update(store).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    showSnackBarOnline(getContext(),"Saved changes..");
                    if (editState == 0){
                        editLayout.setVisibility(View.VISIBLE);
                        editState =1;
                    }else if (editState == 1){
                        editLayout.setVisibility(View.GONE);
                        editState =0;
                    }

                }else {

                    showSnackBackOffline(getContext(),task.getException().getMessage());


                }
            }
        });
    }


    private boolean validation(){
        userName = EditUserName.getText().toString();
        email = EditEmail.getText().toString();
        phone = EditPhone.getText().toString();
        location = EditLocation.getText().toString();
        street = EditStreet.getText().toString();


        if (userName.isEmpty()){
            EditUserName.setError("Provide your full name");
            return false;
        }else if (email.isEmpty()){
            EditEmail.setError("Provide your email.");
            return false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            EditEmail.setError("Please enter a Valid email");
            return false;
        }
        else if (phone.isEmpty()){
            EditPhone.setError("Provide your phone number.");
            return false;
        }else if (location.isEmpty()){
            EditLocation.setError("Provide your location.");
            return false;
        }else if (street.isEmpty()){
            EditStreet.setError("Provide your street name.");
            return false;
        }
        else{

            return true;
        }

    }


    //----InterNet Connection----
    public void showSnackBackOffline(Context context, String msg) {
        Snackbar.with(getContext(),null).type(Type.ERROR).message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.CENTER).show();
    }

    public void showSnackBarOnline(Context context,String msg) {

        Snackbar.with(getContext(), null).type(Type.SUCCESS).message(msg)
                .duration(Duration.LONG)
                .fillParent(true)
                .textAlign(Align.CENTER).show();

    }




    private  String id2;
    private void FetchProduct() {

        Query query = CandidateRef.whereEqualTo("Employer_ID",mAuth.getCurrentUser().getUid())
                .limit(40);
        FirestoreRecyclerOptions<Candidates> transaction = new FirestoreRecyclerOptions.Builder<Candidates>()
                .setQuery(query, Candidates.class)
                .setLifecycleOwner(this)
                .build();
        adapter = new MyCandidateAdapter(transaction);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager LayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(LayoutManager);
        mRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new MyCandidateAdapter.OnItemCickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Candidates  candidates = documentSnapshot.toObject(Candidates.class);
                candidate = candidates.getCandidate_name();
                ID = candidates.getCandidateID();
                id2 = documentSnapshot.getId();
                if (id2 != null){

                    Discharge_Alert(candidate);
                }else {
                    ToastBack("Please select a candidate.");
                }
            }
        });

    }


    ArrayList<Object> uniqueCount = new ArrayList<Object>();
    int sumCanidate ;
    private void EmployerCount() {
        CandidateRef.whereEqualTo("Employer_ID",mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                uniqueCount.add(document.getData());
                                sumCanidate = 0;
                                for ( sumCanidate = 0; sumCanidate < uniqueCount.size(); sumCanidate++) {

                                }
                            }

                            if (sumCanidate <= 0){
                                ErrorLayout.setVisibility(View.VISIBLE);
                            }else {
                                ErrorLayout.setVisibility(View.GONE);
                            }
                        } else {

                        }
                    }
                });

    }




    private AlertDialog dialog2;
    public void Logout_Alert() {

        Date currentTime = Calendar.getInstance().getTime();
        String date = DateFormat.format("dd MMM ,yyyy | hh:mm a",new Date(String.valueOf(currentTime))).toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                    Intent logout = new Intent(getContext(), MainActivity.class);
                    logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(logout);
                    dialog2.dismiss();


                }else {

                    ToastBack( task.getException().getMessage());

                }

            }
        });

    }


    private String candidate;
    private String userNameE,countyE,cityE,contactE,imageE,emailE,idE,county,Image;
    private long CandidateNo;
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
                    CandidateNo = employerData.getCandidatesCount();
                    Image = employerData.getUserImage();
                    if (imageE != null){
                        AddImage.setVisibility(View.GONE);
                    }else {
                        AddImage.setVisibility(View.VISIBLE);
                    }


                    EmployerEmail.setText(emailE);
                    EmployerName.setText(userNameE);
                    EmployerID.setText(idE);
                    EmployerCounty.setText(county);

                    EditUserName.setText(userNameE);
                    EditEmail.setText(emailE);
                    EditLocation.setText(countyE);
                    EditPhone.setText(contactE);
                    EditStreet.setText(street);

                    if (imageE != null){
                        Picasso.with(profImage.getContext()).load(imageE).placeholder(R.drawable.load)
                                .error(R.drawable.profile)
                                .into(profImage);
                    }else {

                    }

                }
            }
        });

    }


    private Toast backToast;
    private void ToastBack(String message){


        backToast = Toast.makeText(getContext(), message, Toast.LENGTH_LONG);
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