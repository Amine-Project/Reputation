package com.diai.reputation;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.diai.reputation.Model.Utilisateur;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MyAccount extends AppCompatActivity {

    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;

    private LinearLayout fields;
    private ImageView pro_image;
    private TextView fullNameTv;
    private TextView jobTv;
    private TextView phoneNumberTv;
    private Button edit;
    private RatingBar note;

    private LinearLayout editFields;
    private EditText etFullName;
    private EditText etJob;
    private EditText etphoneNumber;
    private Button btnSubmit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        final Context context = this;

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        final String userId = currentUser.getUid();


        fields = (LinearLayout)findViewById(R.id.linearLayout);
        pro_image = (ImageView)findViewById(R.id.pro_image);
        fullNameTv = (TextView)findViewById(R.id.fullNameTv);
        jobTv = (TextView)findViewById(R.id.jobTv);
        phoneNumberTv = (TextView)findViewById(R.id.phoneNumerTv);
        edit = (Button)findViewById(R.id.edit);
        note = (RatingBar)findViewById(R.id.note);
        note.setEnabled(false);

        editFields = (LinearLayout)findViewById(R.id.editFields);
        etFullName = (EditText)findViewById(R.id.fullNameEt);
        etJob = (EditText)findViewById(R.id.jobEt);
        etphoneNumber = (EditText)findViewById(R.id.phoneNumerEt);
        btnSubmit = (Button)findViewById(R.id.btnSubmit);

        mStorageRef.child("images/" + userId +".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (uri != null)
                    Glide.with(context)
                            .load(uri)
                            .into(pro_image);
            }
        });

        //get data from db
        final DatabaseReference usersRef = mDatabase.child("users").getRef();
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if( ds.getKey().equals(userId) ) {
                        //Log.d("WTF","ds.getValue:"+ds.getValue());

                        Utilisateur user = new Utilisateur(ds.getValue(Utilisateur.class));

                        fullNameTv.setText(user.getFirstName());
                        jobTv.setText(user.getJob());
                        phoneNumberTv.setText(user.getPhoneNumer());

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //note.setRating();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etFullName.setText(fullNameTv.getText());
                etJob.setText(jobTv.getText());
                etphoneNumber.setText(phoneNumberTv.getText());
                etphoneNumber.setEnabled(false);
                fields.setVisibility(View.GONE);
                editFields.setVisibility(View.VISIBLE);
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilisateur user = new Utilisateur(etFullName.getText().toString(), etJob.getText().toString(), etphoneNumber.getText().toString());

                usersRef.child(userId).setValue(user);

                fullNameTv.setText(etFullName.getText());
                jobTv.setText(etJob.getText());
                phoneNumberTv.setText(etphoneNumber.getText());

                editFields.setVisibility(View.GONE);
                fields.setVisibility(View.VISIBLE);

            }
        });
    }
}
