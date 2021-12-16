package com.intech.yayananies.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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
import com.intech.yayananies.Activities.InfoActivity;
import com.intech.yayananies.Activities.MainActivity;
import com.intech.yayananies.Activities.ProfileActivity;
import com.intech.yayananies.Adpater.MyCandidateAdapter;
import com.intech.yayananies.Models.Candidates;
import com.intech.yayananies.R;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {
View root;
    private CircleImageView profImage;
    private TextView EmployerName,EmployerID,EmployerEmail,logout;

    private RecyclerView mRecyclerView;
    private MyCandidateAdapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference CountyRef = db.collection("Counties");
    CollectionReference YayaRef = db.collection("Yaya_Employer");
    CollectionReference CandidateRef = db.collection("Yaya_Candidates");
    private FirebaseAuth mAuth;


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
        logout = root.findViewById(R.id.LogOut);
        mRecyclerView = root.findViewById(R.id.recycler_candidates);


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logout_Alert();
            }
        });


        LoadUserDetails();
        FetchProduct();
        return root;
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
                id2 = documentSnapshot.getId();
                if (id2 != null){
                    Intent toUpdate = new Intent(getContext(), InfoActivity.class);
                    toUpdate.putExtra("ID",id2);
                    startActivity(toUpdate);
                }else {
                    ToastBack("Please select a candidate.");
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


    private String userNameE,countyE,cityE,contactE,imageE,emailE,idE;
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


                    EmployerEmail.setText(emailE);
                    EmployerName.setText(userNameE);
                    EmployerID.setText(idE);

                    if (imageE != null){
                        Picasso.with(getContext()).load(imageE).placeholder(R.drawable.load)
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