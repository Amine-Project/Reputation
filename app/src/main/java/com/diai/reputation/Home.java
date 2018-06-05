package com.diai.reputation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.diai.reputation.Model.Employer;
import com.diai.reputation.Model.Entreprise;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Home extends AppCompatActivity {

    private TextView mTextMessage;
    ArrayList<Home.Item> itemList= new ArrayList<>();
    ArrayList<String> ids= new ArrayList<>();
    ListView lv;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mTextMessage = (TextView) findViewById(R.id.message);
        lv=(ListView)findViewById(R.id.mainList);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("workers");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for(DataSnapshot ds :dataSnapshot.getChildren()){
                    Employer employer = new Employer(ds.getValue(Employer.class));
                    itemList.add(new Item(employer.getFirstName(),employer.getLastName(),employer.getService()));
                    ids.add(ds.getKey().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        myRef = database.getReference("companies");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for(DataSnapshot ds :dataSnapshot.getChildren()){
                    Entreprise entreprise = new Entreprise(ds.getValue(Entreprise.class));
                    itemList.add(new Item(entreprise.getCompanyName(),null,entreprise.getService()));
                    ids.add(ds.getKey().toString());
                }
                lv.setAdapter(new MyListAdapter());
                //lv.setOnClickListener(new On);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });




/*
        if(itemList.size()>0)
        {
            lv.setAdapter(new MyListAdapter(this));
        }else
        {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }
        */
    }


    public  ArrayList<Home.Item> load(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for(DataSnapshot ds :dataSnapshot.getChildren()){
                    Employer employer = new Employer(ds.getValue(Employer.class));
                    itemList.add(new Item(employer.getFirstName(),employer.getLastName(),employer.getService()));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
        return itemList;
    }
/*

    public ArrayList<Home.Item> data(){
        Firebase ref = new Firebase("https://reputation-8bc29.firebaseio.com/workers"); //Root URL


        ref.addValueEventListener(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                for (com.firebase.client.DataSnapshot child : dataSnapshot.getChildren()) {
                        itemList.add(new Item(child.getValue(Employer.class).getFirstName(),child.getValue(Employer.class).getLastName(),child.getValue(Employer.class).getService()));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        return itemList;
    }
*/



    private class MyListAdapter extends BaseAdapter {

        //Context context;
        LayoutInflater layoutInflater;

        public MyListAdapter() {
            //this.context = context;
        }

        @Override
        public int getCount() {
            return itemList.size();
        }

        @Override
        public Object getItem(int i) {
            return itemList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }


        @Override
        public View getView(final int position, View convertView, @NonNull ViewGroup parent) {


            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.liste_item, parent, false);
            //ImageView image=(ImageView)findViewById(R.id.profile_image);
            final TextView name = (TextView) row.findViewById(R.id.name);
            final TextView sName = (TextView) row.findViewById(R.id.sName);
            final TextView service = (TextView) row.findViewById(R.id.service);
            Button rate=(Button)row.findViewById(R.id.gRate);


            name.setText(itemList.get(position).name.toString());
            if(itemList.get(position).sName!=null) {
                sName.setText(itemList.get(position).sName);
            }else
                sName.setVisibility(View.INVISIBLE);
            service.setText(itemList.get(position).service);



            rate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(),Profile.class);
                    intent.putExtra("id",ids.get(position).toString());
                    startActivity(intent);
                }
            });

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(),Contact.class);
                    //intent.putExtra("id",ids.get(position).toString());
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

        public Item(String name, String sName, String service) {
            this.name = name;
            this.sName = sName;
            this.service = service;
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
