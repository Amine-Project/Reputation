package com.diai.reputation;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class Contact_list extends AppCompatActivity {
    ListView lv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        lv = (ListView) findViewById(R.id.contactList);
        MyListAdapter listAdpter = new MyListAdapter(this);
        lv.setAdapter(listAdpter);
/*
        Button finish=(Button)findViewById(R.id.finish);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext() , Home.class);
                startActivity(intent);
                onDestroy();
            }
        });*/
    }


    /*
    public ArrayList<Contact>   loadContact(){
        ArrayList<Contact> list=new ArrayList<Contact>();
        ContentResolver contentResolver=getContentResolver();
        Cursor cursor=getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        if(cursor.getCount()>0){
            String id=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));

            if(hasPhoneNumber>0){
                Cursor cursor1=contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"=?",new String[]{id},null);

                while(cursor1.moveToNext()){
                    String phoneNumber= cursor1.getString(cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    list.add(new Contact(name,phoneNumber));
                }
            }
        }
        return list;
    }



    public ArrayList<Contact> load()
    {
        Cursor cursor=getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        contactList=new ArrayList<Contact>();
        while (cursor.moveToNext()){
            String name=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phone=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contactList.add(new Contact(name,phone));
        }
        cursor.close();
        return contactList;
    }


    private HashMap<String, String> putData(String name, String ntel) {
        HashMap<String, String> item = new HashMap<String, String>();
        item.put("nom", name);
        item.put("tel", ntel);
        return item;
    }
*/
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


            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.contact, parent, false);
            //ImageView image=(ImageView)findViewById(R.id.profile_image);
            final TextView name = (TextView) row.findViewById(R.id.contactName);
            TextView phoneNumber = (TextView) row.findViewById(R.id.contactPhone);
            Button share = (Button) row.findViewById(R.id.share);
            Button rate=(Button)row.findViewById(R.id.gRate);


            name.setText(contactList.get(position).name.toString());
            phoneNumber.setText(contactList.get(position).phoneNumber);

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Share with " + name.getText(), Toast.LENGTH_SHORT).show();
                }
            });

            rate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

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


