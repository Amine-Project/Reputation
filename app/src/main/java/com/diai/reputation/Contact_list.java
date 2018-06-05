package com.diai.reputation;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.Manifest;

import com.diai.reputation.Model.Employer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class Contact_list extends AppCompatActivity {
    ListView lv;
    TextView text;
    TextView text0;
    int shareNumber = 5;
    int rateNumber = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_contact_list);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }

        lv = (ListView) findViewById(R.id.contactList);
        MyListAdapter listAdpter = new MyListAdapter(this);
        lv.setAdapter(listAdpter);
        text = (TextView) findViewById(R.id.shareNb);
        text0 = (TextView) findViewById(R.id.rateNb);


        Button finish = (Button) findViewById(R.id.finish);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Home.class);
                startActivity(intent);
                onDestroy();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if ((rateNumber <= 0) && (shareNumber <= 0)) {
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private class MyListAdapter extends BaseAdapter {

        Context context;
        ArrayList<Contact> contactList;
        LayoutInflater layoutInflater;

        public MyListAdapter(Context context) {
            //this.context = context;
            Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            contactList = new ArrayList<Contact>();
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phone = phone.replaceAll(" ", "");
                phone = phone.replaceAll("-", "");
                if (!phone.startsWith("+212")) {
                    if (phone.startsWith("0"))
                        phone = "+212" + phone.substring(1);
                }
                contactList.add(new Contact(name, phone));
            }
            cursor.close();
            this.context = context;
        }

        @Override
        public int getCount() {
            return contactList.size();
        }

        @Override
        public Object getItem(int i) {
            return contactList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }


        @Override
        public View getView(final int position, View convertView, @NonNull ViewGroup parent) {


            final LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.contact, parent, false);
            //ImageView image=(ImageView)findViewById(R.id.profile_image);
            final TextView name = (TextView) row.findViewById(R.id.contactName);
            final TextView phoneNumber = (TextView) row.findViewById(R.id.contactPhone);
            Button share = (Button) row.findViewById(R.id.share);
            Button rate = (Button) row.findViewById(R.id.gRate);
            final CircleImageView imageView = (CircleImageView) findViewById(R.id.profile_image);


            phoneNumber.setText(contactList.get(position).phoneNumber);
            name.setText(contactList.get(position).name);

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent sendIntent = new Intent("android.intent.action.MAIN");
                    sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
                    sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(phoneNumber.getText().toString()) + "@s.whatsapp.net");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Install Reputation App");

                    startActivity(sendIntent);
                    shareNumber = shareNumber - 1;
                    text.setText(Integer.toString(shareNumber));

                }
            });

            rate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), Profile.class);
                    rateNumber = rateNumber - 1;
                    intent.putExtra("id", phoneNumber.getText().toString());
                    text0.setText(Integer.toString(rateNumber));
                    startActivityForResult(intent, 10);
                }
            });

            final boolean[] found = {false};
            final String[] id = {new String()};

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("phoneNumbers");

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (phoneNumber.getText().toString().equals(ds.getValue(String.class))) {
                            found[0] = true;
                            id[0] = ds.getValue(String.class);
                            break;
                        }
                    }

                    if (found[0]) {
                        StorageReference mStorageRef;
                        StorageReference gsReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://reputation-8bc29.appspot.com/images/C4BYE5H0pzM0tm67giCGSFopVP62.jpg");


                        //Picasso.get().load(R.mipmap.ic_launcher).into(imageView);
                    } else {

                    }
                    //ds.getKey().toString();

                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });

            return row;
        }

    }

    private class Contact {
        int image;
        String name;
        String phoneNumber;

        public Contact(String name, String phoneNumber) {
            this.name = name;
            this.phoneNumber = phoneNumber;
        }
    }

}


