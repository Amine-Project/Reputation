package com.diai.reputation;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
    private String fullName, job = null;
    private float rate;

    private ImageView imageView;

    private TextView fullNameTv;
    private TextView jobTv;
    private TextView noteTv;

    private RatingBar ratingbar1;
    private RatingBar ratingbar2;
    private RatingBar ratingbar3;
    private RatingBar ratingbar4;
    private RatingBar ratingbar5;
    private RatingBar ratingbar6;

    private Button sbmtBtn;


    private DatabaseReference mDatabase;
    private FirebaseUser currentFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final Context context = this;

        id = getIntent().getExtras().getString("id");
        fullName = getIntent().getExtras().getString("fullName");
        job = getIntent().getExtras().getString("job");
        rate = getIntent().getExtras().getFloat("rate");

        imageView = (ImageView) findViewById(R.id.profile_image);
        fullNameTv = (TextView) findViewById(R.id.fullName);
        jobTv = (TextView) findViewById(R.id.job);
        noteTv = (TextView) findViewById(R.id.note);
        ratingbar1 = (RatingBar) findViewById(R.id.ratingBar1);
        ratingbar2 = (RatingBar) findViewById(R.id.ratingBar2);
        ratingbar3 = (RatingBar) findViewById(R.id.ratingBar3);
        ratingbar4 = (RatingBar) findViewById(R.id.ratingBar4);
        ratingbar5 = (RatingBar) findViewById(R.id.ratingBar5);
        ratingbar6 = (RatingBar) findViewById(R.id.ratingBar6);//la note generale

        sbmtBtn = (Button) findViewById(R.id.submitBtn);


        fullNameTv.setText(fullName);
        jobTv.setText(job);
        ratingbar6.setRating(rate);
        noteTv.setText(Float.toString(Math.round(rate)));
        //Toast.makeText(context, Float.toString(rate), Toast.LENGTH_SHORT).show();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        storageRef.child("images/" + id).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if (uri != null)
                    Glide.with(context)
                            .load(uri)
                            .into(imageView);
            }
        });

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        for (DataSnapshot data : ds.getChildren()) {
                            if (data.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                if (data.hasChild("rated")) {

                                    if (data.child("rated").hasChild(id)) {
                                        ratingbar1.setRating(data.child("rated").child(id).child("seriousness").getValue(Float.class));
                                        ratingbar2.setRating(data.child("rated").child(id).child("loyalty").getValue(Float.class));
                                        ratingbar3.setRating(data.child("rated").child(id).child("punctuality").getValue(Float.class));
                                        ratingbar4.setRating(data.child("rated").child(id).child("sociability").getValue(Float.class));
                                        ratingbar5.setRating(data.child("rated").child(id).child("respect").getValue(Float.class));
                                        break;
                                    }
                                }
                            }
                        }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        final boolean[] flag = {true};
        sbmtBtn.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(final View v) {
                //updating ui
                final float val1 = ratingbar1.getRating();
                final float val2 = ratingbar2.getRating();
                final float val3 = ratingbar3.getRating();
                final float val4 = ratingbar4.getRating();
                final float val5 = ratingbar5.getRating();
                if ((val1 != 0) && (val2 != 0) && (val3 != 0) && (val4 != 0) && (val5 != 0)&&(flag[0])) {
                    final float rating = (float) ((val1 + val2 + val3 + val4 + val5) / 5.0);
                    final Rating rate = new Rating(val1, val2, val3, val4, val5, rating);
                    flag[0] =false;
                    mDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                if ((ds.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) && (!ds.getKey().toString().equals("phoneNumbers"))) {
                                    mDatabase.child(ds.getKey().toString()).child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child("rated").child(id).setValue(rate);
                                }
                                if ((ds.hasChild(id)) && (!ds.getKey().toString().equals("phoneNumbers"))) {
                                    Toast.makeText(context, ds.getKey(), Toast.LENGTH_SHORT).show();
                                    Float fl = ds.child(id).child("rating").child("avg").getValue(Float.class);
                                    int nb = 0;
                                    //Rating rate1;
                                    if (ds.child(id).child("rating").hasChild("nb"))
                                        nb = ds.child(id).child("rating").child("nb").getValue(Integer.class);
                                    float seriousness = ds.child(id).child("rating").child("seriousness").getValue(Float.class);
                                    seriousness=(float)((seriousness * nb + val1) / (nb + 1.0));
                                    float loyalty = (float) (((ds.child(id).child("rating").child("loyalty").getValue(Float.class)) * nb + val2) / (nb + 1));
                                    float punctuality =(float) (((ds.child(id).child("rating").child("punctuality").getValue(Float.class))* nb + val3) / (nb + 1));
                                    float sociability =(float) (((ds.child(id).child("rating").child("sociability").getValue(Float.class)) * nb + val4) / (nb + 1));
                                    float respect =(float) (((ds.child(id).child("rating").child("respect").getValue(Float.class)) * nb + val5) / (nb + 1));
                                    float avg =(float)Math.round(((seriousness + loyalty + punctuality + sociability + respect) / 5));
                                    mDatabase.child(ds.getKey().toString()).child(id).child("rating").child("seriousness").setValue(seriousness);
                                    mDatabase.child(ds.getKey().toString()).child(id).child("rating").child("loyalty").setValue(loyalty);
                                    mDatabase.child(ds.getKey().toString()).child(id).child("rating").child("punctuality").setValue(punctuality);
                                    mDatabase.child(ds.getKey().toString()).child(id).child("rating").child("sociability").setValue(sociability);
                                    mDatabase.child(ds.getKey().toString()).child(id).child("rating").child("respect").setValue(respect);
                                    mDatabase.child(ds.getKey().toString()).child(id).child("rating").child("avg").setValue(avg);
                                    mDatabase.child(ds.getKey().toString()).child(id).child("rating").child("nb").setValue(nb+1);
                                    onDestroy();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError error) {};
                    });
                } else
                    Toast.makeText(v.getContext(), "Rate all fields", Toast.LENGTH_SHORT).show();
            }

        }

        );
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
