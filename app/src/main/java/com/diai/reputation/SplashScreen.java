package com.diai.reputation;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class SplashScreen extends AppCompatActivity {

    private static final long SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Boolean first= getSharedPreferences("PREDERENCE",MODE_PRIVATE).getBoolean("isFirstRun",true);

        if(first){
            save();
            getSharedPreferences("PREDERENCE",MODE_PRIVATE).edit().putBoolean("isFirstRun",false).commit();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, PhoneLogin.class);
                SplashScreen.this.startActivity(i);
                SplashScreen.this.finish();
            }
        },SPLASH_TIME_OUT);
    }


    public void save() {
        FileOutputStream file = null;

        try {
            file = openFileOutput("variable.txt", MODE_PRIVATE);
            file.write("5:2".getBytes());

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
