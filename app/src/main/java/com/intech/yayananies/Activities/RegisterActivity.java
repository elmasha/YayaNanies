package com.intech.yayananies.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.intech.yayananies.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText FirstName,MiddleName,LastName,IDNumber,
            StreetName,City,County,Email,
            Telephone,Password;

    private TextView toLogin,toMain;
    private LinearLayout google,facebook;
    FirebaseAuth mAuth;
    private Button BtnRegister;

    private String firstName,middleName,lastName,idNumber,streetName,city,county,email,
            telephone,id,userImage,password;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference YayaRef = db.collection("Yaya_Employer");

    private static final int RC_SIGN_IN = 1001;
    GoogleSignInClient googleSignInClient;


    private static final String TAG = "FacebookLogin";

    private CallbackManager mCallbackManager;
    SignInButton signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();

        toLogin = findViewById(R.id.ToLogin);
         signInButton = findViewById(R.id.sign_in_button);

        FirstName = findViewById(R.id.first_name);
        MiddleName = findViewById(R.id.middle_name);
        LastName = findViewById(R.id.last_name);
        IDNumber = findViewById(R.id.id_no);
        County = findViewById(R.id.county);
        StreetName = findViewById(R.id.street_name);
        City = findViewById(R.id.city);
        Email = findViewById(R.id.email_address);
        Telephone = findViewById(R.id.phone_no);
        BtnRegister = findViewById(R.id.Btn_register);
        Password = findViewById(R.id.password);
        toMain = findViewById(R.id.BackToMain2);


        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.login_button);

        loginButton.setPermissions(Arrays.asList("email", "public_profile"));

        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
                LoadProfile(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");

            }

            @Override
            public void onError(FacebookException error) {
                ToastBack(error.getMessage());
            }

        });

        BtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validation()){

                }else {
                    if (mAuth.getCurrentUser().getUid() != null){

                        RegisterUser(mAuth.getCurrentUser().getUid());
                    }else {
                        EmailPassRegistration();
                    }


                }

            }
        });


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInToGoogle();
            }
        });

        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });

        toMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

        configureGoogleClient();

    }

    private void EmailPassRegistration() {
        email = Email.getText().toString();
        password = Password.getText().toString();

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                        RegisterUser(mAuth.getCurrentUser().getUid());
                }else {
                    ToastBack(task.getException().getMessage());
                }
            }
        });


    }


    private void LoadProfile(AccessToken accessToken){

        GraphRequest graphRequest = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    firstName = object.getString("first_name");
                    lastName = object.getString("last_name");
                    email = object.getString("email");
                    id = object.getString("id");
                    userImage = "https://graph.facebook.com"+id+"/picture?type=normal";
                    FirstName.setText(firstName);
                    LastName.setText(lastName);
                    Email.setText(email);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void RegisterUser(String uid) {
        String token_Id = FirebaseInstanceId.getInstance().getToken();
        HashMap<String,Object> registerB = new HashMap<>();
        registerB.put("Name", firstName +" "+ middleName +" "+ lastName);
        registerB.put("ID_no",idNumber);
        registerB.put("Street_name",streetName);
        registerB.put("City",city);
        registerB.put("County",county);
        registerB.put("Email",email);
        registerB.put("Phone_NO",telephone);
        registerB.put("device_token",token_Id);
        registerB.put("User_ID",uid);
        registerB.put("SelectionCount",3);
        registerB.put("CandidatesCount",1);
        registerB.put("UserImage",userImage);
        registerB.put("timestamp", FieldValue.serverTimestamp());

        YayaRef.document(uid).set(registerB).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    ToastBack("Registration successful");
                    Intent toreg = new Intent(getApplicationContext(), PreferenceActivity.class);
                    startActivity(toreg);
                    finish();
                }else {
                    ToastBack(task.getException().getMessage());
                }
            }
        });


    }

    private boolean validation(){
        firstName = FirstName.getText().toString();
        middleName = MiddleName.getText().toString();
        lastName = LastName.getText().toString();
        idNumber = IDNumber.getText().toString();
        streetName = StreetName.getText().toString();
        city = City.getText().toString();
        county = County.getText().toString();
        email = Email.getText().toString();
        telephone = Telephone.getText().toString();
        password = Password.getText().toString();


        if (firstName.isEmpty()){
            ToastBack("Provide first name");
            return false;
        }else if (middleName.isEmpty()){
            ToastBack("Provide middle name");
            return false;
        }else if (lastName.isEmpty()){
            ToastBack("Provide last name");
            return false;
        }else if (idNumber.isEmpty()){
            ToastBack("Provide ID number");
            return false;
        }else if (streetName.isEmpty()){
            ToastBack("Provide street name");
            return false;
        }else if (city.isEmpty()){
            ToastBack("Provide City/Town");
            return false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // email_Log.setError("");
            ToastBack("Please enter a Valid email");
            return false;
        }else if (county.isEmpty()){
            ToastBack("Provide county");
            return false;
        }
        else if (telephone.isEmpty()){
            ToastBack("Provide your phone number.");
            return false;
        }else if (password.isEmpty()){
            ToastBack("Provide your password.");
            return false;
        }
        else{

            return true;
        }

    }


    private void configureGoogleClient() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // for the requestIdToken, this is in the values.xml file that
                // is generated from your google-services.json
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        // Set the dimensions of the sign-in button.

    }


    public void signInToGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                showToastMessage("Google Sign in Succeeded");
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                showToastMessage("Google Sign in Failed " + e);

                ToastBack("Google Sign in Failed " + e);
            }
        }
    }


    AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if (currentAccessToken ==null){
                FirstName.setText("");
                LastName.setText("");
                Email.setText("");
            }else {
                LoadProfile(currentAccessToken);
            }
        }
    };


    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, UI will update with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            FirstName.setText(user.getDisplayName());
                            Telephone.setText(user.getPhoneNumber());
                            Email.setText(user.getEmail());

                            if (!validation()){
                                ToastBack("Please fill the remaining fields");
                            }else {
                                RegisterUser(user.getUid());
                            }
                            Toast.makeText(RegisterActivity.this, "Authentication Succeeded.", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign-in fails, a message will display to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showToastMessage(String message) {
        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d(TAG, "signInWithCredential:success: currentUser: " + user.getEmail());
                            showToastMessage("Firebase Authentication Succeeded ");
                            FirstName.setText(user.getDisplayName());
                            Telephone.setText(user.getPhoneNumber());
                            Email.setText(user.getEmail());
                            userImage = String.valueOf(user.getPhotoUrl());
                            Password.setVisibility(View.GONE);

                            if (!validation()){
                                    ToastBack("Please fill the remaining fields");
                            }else {
                                RegisterUser(user.getUid());
                            }


                        } else {
                            Password.setVisibility(View.VISIBLE);
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            showToastMessage("Firebase Authentication failed:" + task.getException());
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