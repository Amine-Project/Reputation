package com.diai.reputation;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;

//import com.bumptech.glide.Glide;
//import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class Contact_list extends AppCompatActivity {
    ListView lv;
    TextView text;
    TextView text0;
    int shareNumber;
    int rateNumber;

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


        loadValue(shareNumber, rateNumber);


        lv.setAdapter(listAdpter);
        text = (TextView) findViewById(R.id.shareNb);
        text0 = (TextView) findViewById(R.id.rateNb);

        if ((rateNumber <= 0) && (shareNumber <= 0)) {
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
            onDestroy();
        }

        text.setText(Integer.toString(shareNumber));
        text0.setText(Integer.toString(rateNumber));


        Button finish = (Button) findViewById(R.id.finish);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Home.class);
                startActivity(intent);
                finish();
                onDestroy();
            }
        });
    }

    @Override
    protected void onStop() {
        save(shareNumber, rateNumber);
        super.onStop();
    }

    @Override
    protected void onPause() {
        save(shareNumber, rateNumber);
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if ((rateNumber <= 0) && (shareNumber <= 0)) {
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
            onDestroy();
        }
    }

    @Override
    protected void onDestroy() {
        save(shareNumber, rateNumber);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        save(shareNumber, rateNumber);
        finish();
        onDestroy();
    }


    public void save(int a, int b) {
        FileOutputStream file = null;

        try {
            file = openFileOutput("variable.txt", MODE_PRIVATE);
            String text = Integer.toString(shareNumber) + ":" + Integer.toString(rateNumber);
            file.write(text.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void loadValue(int a, int b) {
        FileInputStream file = null;

        try {
            file = openFileInput("variable.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(file);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String[] line = bufferedReader.readLine().toString().split(":");
            shareNumber = Integer.parseInt(line[0]);
            rateNumber = Integer.parseInt(line[1]);
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private class MyListAdapter extends BaseAdapter {

        Context context;
        ArrayList<Contact> contactList;
        LayoutInflater layoutInflater;
        int count = 0;

        public MyListAdapter(final Context context) {
            this.context = context;
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
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("phoneNumbers");


            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //String id = new String();
                    for (int i = 0; i < contactList.size(); i++)
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (contactList.get(i).phoneNumber.equals(ds.getValue(String.class))) {
                                contactList.get(i).found = true;
                                //id = ds.getValue(String.class);
                                count++;

                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageRef = storage.getReference();

                                final int finalI = i;
                                storageRef.child("images/" + ds.getKey().toString()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        contactList.get(finalI).image = uri;
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        contactList.get(finalI).image = null;
                                    }
                                });
                                break;
                            }
                        }

                    Boolean first = getSharedPreferences("PREF", MODE_PRIVATE).getBoolean("isFirstRun", true);

                    if (first) {
                        if (count > 2) {
                            rateNumber = 2;
                        } else
                            rateNumber = count;

                        text0.setText(Integer.toString(rateNumber));
                        getSharedPreferences("PREF", MODE_PRIVATE).edit().putBoolean("isFirstRun", false).commit();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
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


        private boolean appInstalledOrNot(String uri) {
            PackageManager pm = getPackageManager();
            boolean app_installed;
            try {
                pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
                app_installed = true;
            } catch (PackageManager.NameNotFoundException e) {
                app_installed = false;
            }
            return app_installed;
        }


        @Override
        public View getView(final int position, View convertView, @NonNull final ViewGroup parent) {


            final LayoutInflater inflater = getLayoutInflater();
            final View row = inflater.inflate(R.layout.contact, parent, false);
            //ImageView image=(ImageView)findViewById(R.id.profile_image);
            final TextView name = (TextView) row.findViewById(R.id.contactName);
            final TextView phoneNumber = (TextView) row.findViewById(R.id.contactPhone);
            Button share = (Button) row.findViewById(R.id.share);
            final Button rate = (Button) row.findViewById(R.id.gRate);
            final CircleImageView imageView = (CircleImageView) row.findViewById(R.id.contact_image);


            phoneNumber.setText(contactList.get(position).phoneNumber);
            name.setText(contactList.get(position).name);

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (appInstalledOrNot("com.whatsapp")) {
                        Intent sendIntent = new Intent("android.intent.action.MAIN");
                        sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
                        sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(phoneNumber.getText().toString()) + "@s.whatsapp.net");
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "Install Reputation App");

                        startActivity(sendIntent);
                        shareNumber = shareNumber - 1;
                        text.setText(Integer.toString(shareNumber));
                    } else {
                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("sms:" + phoneNumber));
                        intent.putExtra("sms_body", "We invite you to download the Reputation App");
                        startActivity(intent);
                        shareNumber = shareNumber - 1;
                        text.setText(Integer.toString(shareNumber));
                    }

                }
            });

            if (contactList.get(position).found) {
                rate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), Profile.class);
                        rateNumber = rateNumber - 1;
                        intent.putExtra("id", phoneNumber.getText().toString());
                        text0.setText(Integer.toString(rateNumber));
                        startActivity(intent);
                    }
                });

                if (contactList.get(position).image != null){
                    Glide.with(row.getContext())
                            .load(contactList.get(position).image)
                            .into(imageView);
                }

            } else
                rate.setVisibility(View.GONE);


            return row;
        }

    }

    private class Contact {
        Uri image;
        String name;
        String phoneNumber;
        boolean found;

        public Contact(String name, String phoneNumber) {
            this.name = name;
            this.phoneNumber = phoneNumber;
            found = false;
            image = null;
        }

    }

}


