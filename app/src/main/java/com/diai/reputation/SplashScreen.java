package com.diai.reputation;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class SplashScreen extends AppCompatActivity {

    private static final long SPLASH_TIME_OUT = 1000;

    private DatabaseReference usersRef;
    private FirebaseUser currentFirebaseUser;

    private Boolean inDb = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        usersRef = FirebaseDatabase.getInstance().getReference("users");
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Boolean first= getSharedPreferences("PREDERENCE",MODE_PRIVATE).getBoolean("isFirstRun",true);

        if(first){
            save();
            getSharedPreferences("PREDERENCE",MODE_PRIVATE).edit().putBoolean("isFirstRun",false).commit();
        }
        if (currentFirebaseUser!=null) {
            //User is signed in
            final String userId = currentFirebaseUser.getUid();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot ds : dataSnapshot.getChildren()){

                                if( ds.getKey().equals(userId) ) {
                                    Log.d("WTF","inDb");
                                    inDb = true;
                                    Intent intent = new Intent(SplashScreen.this, Contact_list.class);
                                    startActivity(intent);
                                    finish();

                                }
                            }
                            if(!inDb) {
                                 Log.d("WTF","notInDb");
                                 Intent i = new Intent(SplashScreen.this, PhoneLogin.class);
                                 SplashScreen.this.startActivity(i);
                                 SplashScreen.this.finish();
                            }
                            Toast.makeText(SplashScreen.this,"Successfully signed in with"+currentFirebaseUser.getPhoneNumber(),Toast.LENGTH_SHORT).show();//for Testing

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            },SPLASH_TIME_OUT);
        }else {
            // No user is signed in
            Intent i = new Intent(SplashScreen.this, PhoneLogin.class);
            SplashScreen.this.startActivity(i);
            SplashScreen.this.finish();
            Toast.makeText(SplashScreen.this,"No user is signed in",Toast.LENGTH_SHORT).show();//for testing

        }

    }


    public void save() {
        FileOutputStream file = null;

        try {
            file = openFileOutput("variable.txt", MODE_PRIVATE);
            file.write("2:1".getBytes());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (file != null){
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
