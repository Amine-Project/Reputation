package com.diai.reputation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Contact extends AppCompatActivity {

    private String id;
    private String name, services, sName = null;

    private ImageButton btnBack;
    private Button btnCall;
    private Button btnText;
    private ImageView imageView;
    private TextView fname;
    private TextView lname;
    private TextView service;
    private TextView phone;

    DatabaseReference phoneNumbersRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        imageView = (ImageView) findViewById(R.id.profile_image);
        fname = (TextView) findViewById(R.id.tvFname);
        lname = (TextView) findViewById(R.id.tvLname);
        service = (TextView) findViewById(R.id.tvService);
        phone = (TextView) findViewById(R.id.phone);
        btnCall = (Button)findViewById(R.id.btnCall);
        btnText = (Button)findViewById(R.id.btnText);
        btnBack = (ImageButton)findViewById(R.id.back);

        id = getIntent().getExtras().getString("id");
        //parent = getIntent().getExtras().getString("parent");
        name = getIntent().getExtras().getString("name");
        if (getIntent().getExtras().getString("sName") != null)
            sName = getIntent().getExtras().getString("sName");
        if (getIntent().getExtras().getString("service") != null)
            services = getIntent().getExtras().getString("service");

        phoneNumbersRef = FirebaseDatabase.getInstance().getReference("phoneNumbers");
        //get the phone nmber and pt it in "here"

        fname.setText(name);
        lname.setText(sName);
        service.setText(services);
        phone.setText("here");

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnCall.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            public void onClick(View arg0) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+phone));
                startActivity(callIntent);
            }
        });


    }
}
