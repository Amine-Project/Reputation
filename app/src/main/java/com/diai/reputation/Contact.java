package com.diai.reputation;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Contact extends AppCompatActivity {

    ImageButton imageButton;
    ImageView imageView;
    private String id;
    private String name, services, sName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        imageButton = (ImageButton)findViewById(R.id.back);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button message=(Button)findViewById(R.id.contactMessage);
        Button call=(Button)findViewById(R.id.contactCall);

        imageView = (ImageView) findViewById(R.id.photo);

        TextView contactName=(TextView)findViewById(R.id.tvFname);
        TextView contactLast=(TextView)findViewById(R.id.tvLname);
        TextView contactService=(TextView)findViewById(R.id.tvService);
        TextView textView=(TextView)findViewById(R.id.contactLastName2);
        final TextView contactphone=(TextView)findViewById(R.id.phone);


        id = getIntent().getExtras().getString("id");
        //parent = getIntent().getExtras().getString("parent");
        name = getIntent().getExtras().getString("name");
        services = getIntent().getExtras().getString("service");
        if (getIntent().getExtras().getString("sName") != null)
            sName = getIntent().getExtras().getString("sName");

        contactName.setText(name);
        if(sName!=null)
            contactLast.setText(sName);
        else{
            contactLast.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
        }
        contactService.setText(services);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("phoneNumbers");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                contactphone.setText(dataSnapshot.child(id).getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        final Context context = this;
        String id = getIntent().getExtras().getString("id");

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        storageRef.child("images/" + id).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context)
                        .load(uri)
                        .into(imageView);
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myActivity = new Intent (Intent.ACTION_DIAL, Uri.parse( "tel:"+contactphone.getText()));
                startActivity(myActivity);
            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("sms:" + contactphone.getText()));
                startActivity(intent);
            }
        });
    }
}
