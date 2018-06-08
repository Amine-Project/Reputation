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
    private String name, services, sName = null;
    private float rate;


    //private static final int REQUEST_INVITE = 100;
    //private String TAG = "Profile";
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
        //parent = getIntent().getExtras().getString("parent");
        name = getIntent().getExtras().getString("name");
        services = getIntent().getExtras().getString("service");
        if (getIntent().getExtras().getString("sName") != null)
            sName = getIntent().getExtras().getString("sName");
        rate = getIntent().getExtras().getFloat("rate");

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


        fname.setText(name);
        if (sName != null) {
            lname.setText(sName);
        } else {
            lname.setVisibility(View.GONE);
            findViewById(R.id.textView2).setVisibility(View.GONE);
        }

        service.setText(services);
        ratingbar6.setRating(rate);
        note.setText(Float.toString(Math.round(rate)));
        //Toast.makeText(context, Float.toString(rate), Toast.LENGTH_SHORT).show();

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


        DatabaseReference database = FirebaseDatabase.getInstance().getReference();


        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (!ds.getKey().equals("phoneNumbers"))
                        for (DataSnapshot data : ds.getChildren()) {
                            if (data.getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                if (data.hasChild("rated")) {

                                    if (data.child("rated").hasChild(id)) {
                                /*
                                    Rating rating = data.child("rated").child(id).getValue(Rating.class);
                                ratingbar1.setRating(rating.getSeriousness());
                                ratingbar2.setRating(rating.getLoyalty());
                                ratingbar3.setRating(rating.getPunctuality());
                                ratingbar4.setRating(rating.getSociability());
                                ratingbar5.setRating(rating.getRespect());*/

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
                                    //String key = mDatabase.child(ds.getKey().toString()).child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child("rated").push().getKey();
                                    //mDatabase.child(ds.getKey().toString()).child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child("rated").child(key).setValue(rate);

                                    mDatabase.child(ds.getKey().toString()).child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString()).child("rated").child(id).setValue(rate);

                                }
                                if ((ds.hasChild(id)) && (!ds.getKey().toString().equals("phoneNumbers"))) {
                                    Toast.makeText(context, ds.getKey(), Toast.LENGTH_SHORT).show();
                                    Float fl = ds.child(id).child("rating").child("avg").getValue(Float.class);
                                    int nb = 0;
                                    //Rating rate1;
                                    if (ds.child(id).child("rating").hasChild("nb"))
                                        nb = ds.child(id).child("rating").child("nb").getValue(Integer.class);


                                    //Rating rate1 = new Rating(ds.getValue(Rating.class));
                                    float seriousness = ds.child(id).child("rating").child("seriousness").getValue(Float.class);
                                    seriousness=(float)((seriousness * nb + val1) / (nb + 1.0));
                                    /*float loyalty=(rate1.getLoyalty()*nb+val2)/nb+1;
                                    float punctuality=(rate1.getPunctuality()*nb+val3)/nb+1;
                                    float sociability=(rate1.getSociability()*nb+val4)/nb+1;
                                    float respect=(rate1.getRespect()*nb+val5)/nb+1;*/

                                    float loyalty = (float) (((ds.child(id).child("rating").child("loyalty").getValue(Float.class)) * nb + val2) / (nb + 1));
                                    float punctuality =(float) (((ds.child(id).child("rating").child("punctuality").getValue(Float.class))* nb + val3) / (nb + 1));
                                    float sociability =(float) (((ds.child(id).child("rating").child("sociability").getValue(Float.class)) * nb + val4) / (nb + 1));
                                    float respect =(float) (((ds.child(id).child("rating").child("respect").getValue(Float.class)) * nb + val5) / (nb + 1));
                                    float avg =(float)Math.round(((seriousness + loyalty + punctuality + sociability + respect) / 5));
                                    //String str = seriousness + " " +nb;//+ loyalty + " " + punctuality + " " + sociability + " " + respect;
                                    //Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
                                    //rate1 = new Rating(seriousness, loyalty, punctuality, sociability, respect, avg);
                                    //mDatabase.child(ds.getKey().toString()).child(id).child("rating").setValue(rate1);
                                    mDatabase.child(ds.getKey().toString()).child(id).child("rating").child("seriousness").setValue(seriousness);
                                    mDatabase.child(ds.getKey().toString()).child(id).child("rating").child("loyalty").setValue(loyalty);
                                    mDatabase.child(ds.getKey().toString()).child(id).child("rating").child("punctuality").setValue(punctuality);
                                    mDatabase.child(ds.getKey().toString()).child(id).child("rating").child("sociability").setValue(sociability);
                                    mDatabase.child(ds.getKey().toString()).child(id).child("rating").child("respect").setValue(respect);
                                    mDatabase.child(ds.getKey().toString()).child(id).child("rating").child("avg").setValue(avg);
                                    mDatabase.child(ds.getKey().toString()).child(id).child("rating").child("nb").setValue(nb+1);
                                    onDestroy();
                                        /*rate1 = new Rate(val1, val2, val3, val4, val5, rating, nb + 1);
                                        Toast.makeText(context, "vals", Toast.LENGTH_SHORT).show();
                                        mDatabase.child(ds.getKey().toString()).child(id).child("rating").setValue(rate1);*/
                                    //mDatabase.child(ds.getKey().toString()).child(id).child("rating").child("nb").setValue(nb+1);
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
