package com.diai.reputation;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.diai.reputation.Model.Entreprise;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Company.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Company#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Company extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private DatabaseReference mDatabase;
    private FirebaseUser currentFirebaseUser;
    private StorageReference mStorageRef;

    private EditText companyName;
    private EditText service;

    Uri imageUri;
    ImageView companyLogo;
    Intent crop;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Company() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Company.
     */
    // TODO: Rename and change types and number of parameters
    public static Company newInstance(String param1, String param2) {
        Company fragment = new Company();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("companies");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(currentFirebaseUser.getUid())){
                    Intent intent = new Intent(getContext(), Contact_list.class);
                    startActivity(intent);
                    onDestroy();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_company, container, false);
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(gallery, "Select Image from Gallery"), 2);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        companyName = (EditText)getView().findViewById(R.id.cname);
        service = (EditText)getView().findViewById(R.id.service);

        companyLogo=(ImageView)getView().findViewById(R.id.companyLogo);

        Bitmap bit= BitmapFactory.decodeResource(getResources(),R.drawable.images);
        RoundedBitmapDrawable round= RoundedBitmapDrawableFactory.create(getResources(),bit);
        round.setCircular(true);
        companyLogo.setImageDrawable(round);

        Button btn = (Button) getView().findViewById(R.id.upload1);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
        Button next = (Button) getView().findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if ((!companyName.getText().toString().isEmpty()) && (!service.getText().toString().isEmpty())) {
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                    Entreprise entreprise = new Entreprise(companyName.getText().toString(), service.getText().toString());
                    String userId = currentFirebaseUser.getUid();
                    mDatabase.child("companies").child(userId).setValue(entreprise);
                    //Store the image in Firebase Storage
                    String path = "images/companies/" + userId;
                    StorageReference userImgRef = mStorageRef.child(path);
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


                    Intent intent = new Intent(getContext(), Contact_list.class);
                    startActivity(intent);
                    onDestroy();

                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == 2) {
                imageUri = data.getData();
                CropImage();
            } else if (requestCode == 1) {
                Bundle bundle = data.getExtras();
                Bitmap bitmap = bundle.getParcelable("data");
                RoundedBitmapDrawable round = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                round.setCircular(true);
                companyLogo.setImageDrawable(round);
            }
        }
    }

    private void CropImage() {
        try{
            crop = new Intent("com.android.camera.action.CROP");
            crop.setDataAndType(imageUri,"image/*");


            crop.putExtra("crop","true");
            crop.putExtra("outputX",180);
            crop.putExtra("outputY",180);
            crop.putExtra("aspectX",4);
            crop.putExtra("aspectY",4);
            crop.putExtra("scaleUpIfNeeded",true);
            crop.putExtra("return-data",true);
            startActivityForResult(crop,1);
        }
        catch (ActivityNotFoundException ex)
        {

        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
