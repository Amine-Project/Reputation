package com.diai.reputation;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.diai.reputation.Model.Employer;
import com.diai.reputation.Model.Entreprise;
import com.diai.reputation.Model.Rating;
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

public class Profile extends AppCompatActivity {

    private String id;
    private String parent;


    //private static final int REQUEST_INVITE = 100;
    private String TAG = "Profile";
    private TextView fname;
    private TextView lname;
    private TextView service;
    private TextView note;
    private ImageView imageView;
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

        final Context context = this;
        id = getIntent().getExtras().getString("id");
        parent = getIntent().getExtras().getString("parent");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //server side
        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //final String userId = currentFirebaseUser.getUid();
        //end

        imageView = (ImageView) findViewById(R.id.profile_image);
        fname = (TextView) findViewById(R.id.tvFname);
        lname = (TextView) findViewById(R.id.tvLname);
        service = (TextView) findViewById(R.id.tvService);
        note = (TextView) findViewById(R.id.note);
        ratingbar1 = (RatingBar) findViewById(R.id.ratingBar1);
        ratingbar2 = (RatingBar) findViewById(R.id.ratingBar2);
        ratingbar3 = (RatingBar) findViewById(R.id.ratingBar3);
        ratingbar4 = (RatingBar) findViewById(R.id.ratingBar4);
        ratingbar5 = (RatingBar) findViewById(R.id.ratingBar5);
        ratingbar6 = (RatingBar) findViewById(R.id.ratingBar6);//la note generale

        sbmtBtn = (Button) findViewById(R.id.submitBtn);


        lname.setVisibility(View.GONE);
        findViewById(R.id.textView2).setVisibility(View.GONE);


        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = database.child(parent);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey().equals(id)) {
                        if (parent == "workers") {
                            if(ds.hasChild("rating")){
                                Rating rating=ds.child("rating").getValue(Rating.class);
                                ratingbar1.setRating(rating.getSeriousness());
                                ratingbar2.setRating(rating.getLoyalty());
                                ratingbar3.setRating(rating.getPunctuality());
                                ratingbar4.setRating(rating.getSociability());
                                ratingbar5.setRating(rating.getRespect());
                                ratingbar6.setRating(rating.getAvg());
                            }
                            else {
                                mDatabase.child(parent).child(id).child("rating").setValue(new Rating(0,0,0,0,0,0));
                                Rating rating=ds.child("rating").getValue(Rating.class);
                                ratingbar1.setRating(rating.getSeriousness());
                                ratingbar2.setRating(rating.getLoyalty());
                                ratingbar3.setRating(rating.getPunctuality());
                                ratingbar4.setRating(rating.getSociability());
                                ratingbar5.setRating(rating.getRespect());
                                ratingbar6.setRating(rating.getAvg());
                            }
                        } else if (parent == "companies") {
                            if(ds.hasChild("rating")){
                                Rating rating=ds.child("rating").getValue(Rating.class);
                                ratingbar1.setRating(rating.getSeriousness());
                                ratingbar2.setRating(rating.getLoyalty());
                                ratingbar3.setRating(rating.getPunctuality());
                                ratingbar4.setRating(rating.getSociability());
                                ratingbar5.setRating(rating.getRespect());
                                ratingbar6.setRating(rating.getAvg());
                            }
                            else {
                                mDatabase.child(parent).child(id).child("rating").setValue(new Rating(0,0,0,0,0,0));
                                Rating rating=ds.child("rating").getValue(Rating.class);
                                ratingbar1.setRating(rating.getSeriousness());
                                ratingbar2.setRating(rating.getLoyalty());
                                ratingbar3.setRating(rating.getPunctuality());
                                ratingbar4.setRating(rating.getSociability());
                                ratingbar5.setRating(rating.getRespect());
                                ratingbar6.setRating(rating.getAvg());
                            }
                        }
                    }

                }
                String value = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


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


        sbmtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //updating ui
                float val1 = ratingbar1.getRating();
                float val2 = ratingbar2.getRating();
                float val3 = ratingbar3.getRating();
                float val4 = ratingbar4.getRating();
                float val5 = ratingbar5.getRating();
                if ((val1 != 0) && (val2 != 0) && (val3 != 0) && (val4 != 0) && (val5 != 0)) {
                    float rating = (float) ((val1 + val2 + val3 + val4 + val5) / 5.0);
                    ratingbar6.setRating(((float) rating));
                    note.setText(String.valueOf(rating));
                    //writing data to database
                    //String key = mDatabase.child("rated").push().getKey();
                    final Rating rate = new Rating(val1, val2, val3, val4, val5,rating);
                    mDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                if ((ds.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) && (!ds.getKey().toString().equals("phoneNumbers"))) {
                                    //String key = mDatabase.child(ds.getKey().toString()).child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child("rated").push().getKey();
                                    //mDatabase.child(ds.getKey().toString()).child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child("rated").child(key).setValue(rate);

                                    mDatabase.child(ds.getKey().toString()).child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child("rated").child(id).setValue(rate);
                                    finish();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                        }
                    });
                } else
                    Toast.makeText(v.getContext(), "Rate all fields", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
