package com.intech.yayananies.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.intech.yayananies.R;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private Button forHelper,AsHelper;
    FirebaseAuth mAuth;


    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null)
        {

            if (mAuth.getCurrentUser().getUid() != null){
                startActivity(new Intent(getApplicationContext(),PreferenceActivity.class));
            }else {
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }

        }else {

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    forHelper = findViewById(R.id.Helper);
    AsHelper = findViewById(R.id.workHelper);
        mAuth = FirebaseAuth.getInstance();

        forHelper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });

        AsHelper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Alert();
            }
        });



    }

    private AlertDialog dialog2;
    public void Alert() {

        Date currentTime = Calendar.getInstance().getTime();
        String date = DateFormat.format("dd MMM ,yyyy | hh:mm a",new Date(String.valueOf(currentTime))).toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog2 = builder.create();
        dialog2.show();
        builder.setMessage("You will need to be registered by an agency bureau." +
                "If you one you may request them to on-board you on the app if not please contact us on 07829**** " +
                "for connection. " +
                "" +
                "\n"+date );
        builder.setPositiveButton("Okay",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                      dialog2.dismiss();
                    }
                });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog2.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();
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
        }
        backPressedTime = System.currentTimeMillis();
    }

}