package com.diai.reputation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.diai.reputation.Model.Entreprise;
import com.diai.reputation.Model.Rating;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {

    private String id;


    //private static final int REQUEST_INVITE = 100;
    private String TAG = "Profile";
    private TextView fname;
    private TextView lname;
    private TextView service;
    private TextView note;
    private RatingBar ratingbar1;
    private RatingBar ratingbar2;
    private RatingBar ratingbar3;
    private RatingBar ratingbar4;
    private RatingBar ratingbar5;
    private RatingBar ratingbar6;
    private Button sbmtBtn;

    DatabaseReference mDatabase;
    private FirebaseUser currentFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        id=getIntent().getExtras().getString("id");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //server side
        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        //final String userId = currentFirebaseUser.getUid();
        //end

        fname = (TextView)findViewById(R.id.tvFname);
        lname = (TextView)findViewById(R.id.tvLname);
        service = (TextView)findViewById(R.id.tvService);
        note = (TextView)findViewById(R.id.note);
        ratingbar1 = (RatingBar)findViewById(R.id.ratingBar1);
        ratingbar2 = (RatingBar)findViewById(R.id.ratingBar2);
        ratingbar3 = (RatingBar)findViewById(R.id.ratingBar3);
        ratingbar4 = (RatingBar)findViewById(R.id.ratingBar4);
        ratingbar5 = (RatingBar)findViewById(R.id.ratingBar5);
        ratingbar6 = (RatingBar)findViewById(R.id.ratingBar6);//la note generale

        sbmtBtn = (Button)findViewById(R.id.submitBtn);

        //recpere these from listview item
        fname.setText(id);//
        lname.setText("amroch");
        service.setText("plombier");

        sbmtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //updating ui
                float val1= ratingbar1.getRating();
                float val2= ratingbar2.getRating();
                float val3= ratingbar3.getRating();
                float val4= ratingbar4.getRating();
                float val5= ratingbar5.getRating();
                if((val1!=0)&&(val2!=0)&&(val3!=0)&&(val4!=0)&&(val5!=0)){
                    double rating = (val1+val2+val3+val4+val5)/5.0;
                    ratingbar6.setRating(((float) rating));
                    note.setText(String.valueOf(rating));
                    //writing data to database
                    //String key = mDatabase.child("rated").push().getKey();
                    final Rating rate=new Rating(val1,val2,val3,val4,val5);
                    mDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                if(ds.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    mDatabase.child(ds.getKey()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("rated by").child(id).setValue(rate);
                                    Toast.makeText(v.getContext(),"Inserted",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                        }
                    });
                }
                else
                    Toast.makeText(v.getContext(),"Rate all fields",Toast.LENGTH_SHORT).show();




            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
