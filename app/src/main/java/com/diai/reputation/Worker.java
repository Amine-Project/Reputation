package com.diai.reputation;

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
import android.widget.ImageView;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Worker.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Worker#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Worker extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Uri imageUri;
    ImageView workerImage;
    Intent crop;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Worker() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Worker.
     */
    // TODO: Rename and change types and number of parameters
    public static Worker newInstance(String param1, String param2) {
        Worker fragment = new Worker();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_worker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        workerImage=(ImageView)getView().findViewById(R.id.workerImage);

        Bitmap bit= BitmapFactory.decodeResource(getResources(),R.drawable.images);
        RoundedBitmapDrawable round= RoundedBitmapDrawableFactory.create(getResources(),bit);
        round.setCircular(true);
        workerImage.setImageDrawable(round);

        Button btn = (Button) getView().findViewById(R.id.upload0);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(gallery, "Select Image from Gallery"), 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            imageUri = data.getData();
            CropImage();
        } else if (requestCode == 1) {
            Bundle bundle = data.getExtras();
            Bitmap bitmap = bundle.getParcelable("data");
            RoundedBitmapDrawable round = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
            round.setCircular(true);
            workerImage.setImageDrawable(round);
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
