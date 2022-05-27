package com.p1.uber.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.p1.uber.R;
import com.p1.uber.activities.client.MapClientActivity;
import com.p1.uber.activities.driver.MapDriverActivity;

public class MainActivity extends AppCompatActivity {

    Button mButtonIAmClient;
    Button mButtonIAmDriver;
    SharedPreferences mPref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mPref = getApplicationContext().getSharedPreferences( "typeUser",MODE_PRIVATE);
        final SharedPreferences.Editor editor = mPref.edit();
        mButtonIAmClient=findViewById(R.id.btnclient);
        mButtonIAmDriver=findViewById(R.id.btnIAmDriver);
        
        mButtonIAmClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("user", "activities/client");
                editor.apply();
                goToSelectAuth();
                

            }


        });
        mButtonIAmDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("user", "activities/driver");
                editor.apply();
                goToSelectAuth();


            }


        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            String user = mPref.getString("user","");
            if (user.equals("activities/client")){
                Intent intent =new Intent(MainActivity.this, MapClientActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
            else {
                Intent intent =new Intent(MainActivity.this, MapDriverActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
    }

    private void goToSelectAuth() {
        Intent intent =new Intent(MainActivity.this, SelectOptionAuthActivity.class);
        startActivity(intent);
    }
}
