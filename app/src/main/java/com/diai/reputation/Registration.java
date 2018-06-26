package com.diai.reputation;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.diai.reputation.Model.Utilisateur;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.UploadTask;

public class Registration extends AppCompatActivity {

    private static final int REQUEST_INVITE = 100;
    private int numberOfInvites = 5;

    private DatabaseReference mDatabase;
    private FirebaseUser currentFirebaseUser;
    private StorageReference mStorageRef;

    Uri imageUri;
    ImageView userImage;
    Intent crop;

    private EditText fullNameTv;
    private EditText jobTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        final String userId = currentFirebaseUser.getUid();

        fullNameTv = (EditText)findViewById(R.id.fullName);
        jobTv = (EditText)findViewById(R.id.job);

        userImage = (ImageView)findViewById(R.id.userImage);

        Bitmap bit = BitmapFactory.decodeResource(getResources(), R.drawable.images);
        RoundedBitmapDrawable round = RoundedBitmapDrawableFactory.create(getResources(), bit);
        round.setCircular(true);
        userImage.setImageDrawable(round);


        Button btn = (Button)findViewById(R.id.upload);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        Button next = (Button)findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Write data into Firebase database
                if ( (!fullNameTv.getText().toString().isEmpty()) && (!jobTv.getText().toString().isEmpty()) && (imageUri!=null)) {
                    Utilisateur utilisateur = new Utilisateur(fullNameTv.getText().toString(), jobTv.getText().toString(), currentFirebaseUser.getPhoneNumber() );
                    mDatabase.child("users").child(userId).setValue(utilisateur);
                    mDatabase.child("phoneNumbers").child(userId).setValue(currentFirebaseUser.getPhoneNumber());
                    //Store the image in Firebase Storage
                    String path = "images/" + userId + ".jpg";
                    StorageReference userImgRef = mStorageRef.child(path);
                    try {
                        userImgRef.putFile(imageUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // Get a URL to the uploaded content
                                        //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle unsuccessful uploads
                                        // ...
                                    }
                                });
                    } catch (Exception e) {

                    }
                    Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                            .setMessage(getString(R.string.invitation_message))
                            //.setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                            //.setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                            //.setCallToActionText(getString(R.string.invitation_cta))
                            .build();
                    startActivityForResult(intent, REQUEST_INVITE);
                    /*Intent intent = new Intent(Registration.this, MyAccount.class);
                    startActivity(intent);
                    finish();*/
                } else
                    Toast.makeText(Registration.this, "Fill all the filled", Toast.LENGTH_SHORT).show();
            }
        });

}

        private void CropImage() {
            try {
            crop = new Intent("com.android.camera.action.CROP");
            crop.setDataAndType(imageUri, "image/*");


            crop.putExtra("crop", "true");
            crop.putExtra("outputX", 200);
            crop.putExtra("outputY", 200);
            crop.putExtra("aspectX", 4);
            crop.putExtra("aspectY", 4);
            crop.putExtra("scaleUpIfNeeded", true);
            crop.putExtra("return-data", true);
            startActivityForResult(crop, 1);
            } catch (ActivityNotFoundException ex) {

            }
        }
        private void openGallery() {
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(gallery, "Select Image from Gallery"), 2);
        }

 /*       @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            //super.onActivityResult(requestCode, resultCode, data);
            if(resultCode == RESULT_OK){
                if (requestCode == 2) {
                    imageUri = data.getData();
                    CropImage();
                } else if (requestCode == 1) {
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = bundle.getParcelable("data");
                    RoundedBitmapDrawable round = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                    round.setCircular(true);
                    userImage.setImageDrawable(round);
                }else{
                    Bitmap bit = BitmapFactory.decodeResource(getResources(), R.drawable.images);
                    RoundedBitmapDrawable round = RoundedBitmapDrawableFactory.create(getResources(), bit);
                    round.setCircular(true);
                    userImage.setImageDrawable(round);
                }
            }
        }*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("WTF", "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.d("WTF", "onActivityResult: sent invitation " + id);
                }
                if (ids.length < numberOfInvites){//
                    Toast.makeText(Registration.this,"Sorry! you need to invite "+(numberOfInvites - ids.length)+" more friends to continue",Toast.LENGTH_SHORT).show();
                    numberOfInvites-=ids.length;
                    Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                            .setMessage(getString(R.string.invitation_message))
                            .build();
                    startActivityForResult(intent, REQUEST_INVITE);

                }else {

                    Toast.makeText(Registration.this,"Thanks! Now you can continue",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Registration.this,Contact_list.class));

                }

            } else {
                // Sending failed or it was canceled, show failure message to the user
                // ...
                Toast.makeText(Registration.this,"invitation failed and not sent",Toast.LENGTH_SHORT).show();
            }
        }else if (requestCode == 2 && resultCode == RESULT_OK) {
            imageUri = data.getData();
            CropImage();
        }else if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            Bitmap bitmap = bundle.getParcelable("data");
            RoundedBitmapDrawable round = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
            round.setCircular(true);
            userImage.setImageDrawable(round);
        }else{
            Bitmap bit = BitmapFactory.decodeResource(getResources(), R.drawable.images);
            RoundedBitmapDrawable round = RoundedBitmapDrawableFactory.create(getResources(), bit);
            round.setCircular(true);
            userImage.setImageDrawable(round);
        }


    }

}