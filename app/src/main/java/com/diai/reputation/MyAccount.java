package com.diai.reputation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.diai.reputation.Model.Employer;
import com.diai.reputation.Model.Utilisateur;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class MyAccount extends AppCompatActivity {

    private FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    private LinearLayout fields;
    private ImageView pro_image;
    private TextView fname;
    private TextView lname;
    private TextView service;
    private TextView phoneN;
    private Button edit;
    private RatingBar note;

    private LinearLayout editFields;
    private EditText etfname;
    private EditText etlname;
    private EditText etservice;
    private EditText etphoneN;
    private Button btnSubmit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final String userId = currentUser.getUid();


        fields = (LinearLayout)findViewById(R.id.linearLayout);
        pro_image = (ImageView)findViewById(R.id.pro_image);
        fname = (TextView)findViewById(R.id.textView);
        lname = (TextView)findViewById(R.id.textView1);
        service = (TextView)findViewById(R.id.textView2);
        phoneN = (TextView)findViewById(R.id.textView3);
        edit = (Button)findViewById(R.id.edit);
        note = (RatingBar)findViewById(R.id.note);
        note.setEnabled(false);

        editFields = (LinearLayout)findViewById(R.id.editFields);
        etfname = (EditText)findViewById(R.id.fname);
        etlname = (EditText)findViewById(R.id.lname);
        etservice = (EditText)findViewById(R.id.service);
        etphoneN = (EditText)findViewById(R.id.phone);
        btnSubmit = (Button)findViewById(R.id.btnSubmit);

        //get data from db
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(ds.child(userId).hasChild("firstName")){
                        Log.d("WTF","ds.vale"+ds.child(userId));
                        Utilisateur user = new Utilisateur(ds.child(userId).getValue(Utilisateur.class));
                        fname.setText(user.getFirstName());
                        lname.setText(user.getLastName());
                        service.setText("void");

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        //fill these fiels
       //pro_image
        phoneN.setText(currentUser.getPhoneNumber());//done
        //note.setRating();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etfname.setText(fname.getText());
                etlname.setText(lname.getText());
                etservice.setText(service.getText());
                etphoneN.setText(phoneN.getText());
                fields.setVisibility(View.GONE);
                editFields.setVisibility(View.VISIBLE);
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //kifach an3rf wach worker ola user ola ....(makaynch lo9t ste7)
                Employer worker = new Employer(etfname.getText().toString(), etlname.getText().toString(), etservice.getText().toString());

                mDatabase.child("workers").child(userId).setValue(worker);

                fname.setText(etfname.getText());
                lname.setText(etlname.getText());
                service.setText(etservice.getText());
                phoneN.setText(etphoneN.getText());

                editFields.setVisibility(View.GONE);
                fields.setVisibility(View.VISIBLE);

            }
        });
    }
}
