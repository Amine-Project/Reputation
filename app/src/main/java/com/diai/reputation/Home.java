package com.diai.reputation;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.diai.reputation.Model.Employer;
import com.diai.reputation.Model.Entreprise;
import com.diai.reputation.Model.Rating;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import android.support.v7.widget.SearchView;

import java.util.ArrayList;

public class Home extends AppCompatActivity {

    private TextView mTextMessage;
    ArrayList<Home.Item> itemList = new ArrayList<>();
    ArrayList<String> ids = new ArrayList<>();
    ArrayList<String> parente = new ArrayList<>();
    ListView lv;
    Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mTextMessage = (TextView) findViewById(R.id.message);
        lv = (ListView) findViewById(R.id.mainList);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("workers");

        final DatabaseReference finalMyRef = myRef;
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Employer employer = new Employer(ds.getValue(Employer.class));
//                    Rating rating=new Rating(ds.child("rating").getValue(Rating.class));
                    Float fl = 0.f;
                    if (!ds.hasChild("rating")) {
                        finalMyRef.child(ds.getKey().toString()).child("rating").setValue(new Rating(0, 0, 0, 0, 0, 0));
                        //rating=new Rating(0,0,0,0,0,0);
                    } else {
                        fl = ds.child("rating").child("avg").getValue(Float.class);
                    }

                    //itemList.add(new Item(employer.getFirstName(), employer.getLastName(), employer.getService(),fl));
                    //ids.add(ds.getKey().toString());
                    Item item = new Item(employer.getFirstName(), employer.getLastName(), employer.getService(), fl);
                    if ((ids.contains(ds.getKey().toString()))) {
                        itemList.set(ids.indexOf(ds.getKey().toString()), item);
                    } else {
                        itemList.add(item);
                        ids.add(ds.getKey().toString());
                        parente.add("workers");
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


        myRef = database.getReference("companies");

        final DatabaseReference finalMyRef1 = myRef;
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Entreprise entreprise = new Entreprise(ds.getValue(Entreprise.class));
                    //=new Rating(0,0,0,0,0,0);
                    Float fl=0.f;
                    if (!ds.hasChild("rating")) {
                        finalMyRef1.child(ds.getKey().toString()).child("rating").setValue(new Rating(0, 0, 0, 0, 0, 0));
                        //rating=new Rating(0,0,0,0,0,0);
                    } else
                        fl = ds.child("rating").child("avg").getValue(Float.class);
                    //Toast.makeText(context,fl.toString(),Toast.LENGTH_SHORT);
                    Item item = new Item(entreprise.getCompanyName(), null, entreprise.getService(), fl);
                    if (ids.contains(ds.getKey().toString())) {
                        itemList.set(ids.indexOf(ds.getKey().toString()), item);
                    } else {
                        itemList.add(item);
                        ids.add(ds.getKey().toString());
                        parente.add("companies");
                    }

                }
                lv.setAdapter(new MyListAdapter(itemList));
                //lv.setOnClickListener(new On);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

    }
/*

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search,menu);

        MenuItem searchItem = menu.findItem(R.id.search_item);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ArrayList<Item> list=new ArrayList<>();

                for(Item temp: itemList){
                    if(temp.getService().toLowerCase().contains(query.toLowerCase())){
                        list.add(temp);
                    }
                }
                lv.setAdapter(new MyListAdapter(list));

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, Menu.NONE, "Profil");
        menu.add(0, 2, Menu.NONE, "Share");
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()){
            case 1:
                 intent=new Intent(this,User.class);
                startActivity(intent);
                return true;
            case 2:
                 intent=new Intent(this,Contact_list.class);
                 intent.putExtra("flag",true);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private class MyListAdapter extends BaseAdapter {

        //Context context;
        ArrayList<Item> itemArrayList=new ArrayList<>();

        public MyListAdapter(ArrayList<Item> arrayList) {
            //this.context = context;
            this.itemArrayList = arrayList;
        }

        @Override
        public int getCount() {
            return itemArrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return itemArrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }


        @Override
        public View getView(final int position, View convertView, @NonNull final ViewGroup parent) {


            LayoutInflater inflater = getLayoutInflater();
            final View row = inflater.inflate(R.layout.liste_item, parent, false);
            final TextView name = (TextView) row.findViewById(R.id.name);
            final TextView sName = (TextView) row.findViewById(R.id.sName);
            final TextView service = (TextView) row.findViewById(R.id.service);
            Button rate = (Button) row.findViewById(R.id.gRate);
            final ImageView imageView = (ImageView) row.findViewById(R.id.pro_image);
            RatingBar ratingBar = (RatingBar) row.findViewById(R.id.ratingBar);


            name.setText(itemArrayList.get(position).name);
            if (itemArrayList.get(position).sName != null) {
                sName.setText(itemArrayList.get(position).sName);
            } else
                sName.setVisibility(View.GONE);
            service.setText(itemArrayList.get(position).service);

            ratingBar.setRating(itemArrayList.get(position).rateScore);

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            storageRef.child("images/" + ids.get(position)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    if (uri != null)
                        Glide.with(row.getContext())
                                .load(uri)
                                .into(imageView);
                }
            });
/*
            Glide.with(row.getContext())
                    .load("https://firebasestorage.googleapis.com/v0/b/reputation-8bc29.appspot.com/o/images%2FC4BYE5H0pzM0tm67giCGSFopVP62.jpg?alt=media&token=3f3f61b4-d96c-48c3-9493-f8b9f80d3104")
                    .into(imageView);
*/
            rate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), Profile.class);
                    intent.putExtra("id", ids.get(position).toString());
                    intent.putExtra("name", itemArrayList.get(position).name.toString());
                    if (itemArrayList.get(position).sName != null)
                        intent.putExtra("sName", itemArrayList.get(position).sName);
                    intent.putExtra("parent", parente.get(position).toString());
                    intent.putExtra("service", itemArrayList.get(position).service);
                    intent.putExtra("rate", itemArrayList.get(position).rateScore);
                    startActivity(intent);
                }
            });

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), Contact.class);
                    intent.putExtra("id", ids.get(position).toString());
                    intent.putExtra("name", itemArrayList.get(position).name.toString());
                    if (itemArrayList.get(position).sName != null)
                        intent.putExtra("sName", itemArrayList.get(position).sName);
                    intent.putExtra("service", itemArrayList.get(position).service);
                    startActivity(intent);
                }
            });
            return row;
        }

    }


    public class Item {

        private String name;
        private String sName;
        private String service;
        private float rateScore;

        public Item(String name, String sName, String service, float rateScore) {
            this.name = name;
            this.sName = sName;
            this.service = service;
            this.rateScore = rateScore;
        }

        public Item(Item item) {
            this.name = item.getName();
            this.sName = item.getsName();
            this.service = item.getService();
        }

        public String getName() {
            return name;
        }

        public String getsName() {
            return sName;
        }

        public String getService() {
            return service;
        }
    }

}