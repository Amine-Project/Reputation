package com.diai.reputation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;


public class PhoneLogin extends AppCompatActivity {

    private String mVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    TextView t1,t2;
    ImageView i1;
    EditText e1,e2;
    ImageButton b1,b2;
    FrameLayout frameLayout;


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener!=null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        frameLayout = (FrameLayout)findViewById(R.id.frameLay);

        final ImageView logo = (ImageView)findViewById(R.id.logo);
        e1 = (EditText) findViewById(R.id.Phonenoedittext);
        b1 = (ImageButton) findViewById(R.id.PhoneVerify);
        t1 = (TextView)findViewById(R.id.textView2Phone);
        i1 = (ImageView)findViewById(R.id.imageView2Phone);
        e2 = (EditText) findViewById(R.id.OTPeditText);
        b2 = (ImageButton)findViewById(R.id.OTPVERIFY);
        t2 = (TextView)findViewById(R.id.textViewVerified);
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
                if (currentFirebaseUser!=null){
                    startActivity(new Intent(PhoneLogin.this,Registration.class));
                    PhoneLogin.this.finish();
                    Toast.makeText(PhoneLogin.this,"Successfully signed in with"+currentFirebaseUser.getPhoneNumber(),Toast.LENGTH_SHORT).show();//for Testing

                }else {


                }

            }
        };

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
               // Log.d(TAG, "onVerificationCompleted:" + credential);
                Toast.makeText(PhoneLogin.this,"Verification Complete",Toast.LENGTH_SHORT).show();
               signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
               // Log.w(TAG, "onVerificationFailed", e);
                Toast.makeText(PhoneLogin.this,"Verification Failed",Toast.LENGTH_SHORT).show();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(PhoneLogin.this,"InValid Phone Number",Toast.LENGTH_SHORT).show();
                    // ...
                }
                else {
                    t1.setText(e.toString());
                }

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
               // Log.d(TAG, "onCodeSent:" + verificationId);
                Toast.makeText(PhoneLogin.this,"Verification code has been sent",Toast.LENGTH_SHORT).show();
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                e1.setVisibility(View.GONE);
                b1.setVisibility(View.GONE);
                t1.setVisibility(View.GONE);
                i1.setVisibility(View.GONE);
                logo.setVisibility(View.GONE);

                frameLayout.setVisibility(View.VISIBLE);
                // ...
            }
        };

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (e1.getText().toString().isEmpty()){
                    //show a hint
                    e1.setError( "Phone Number is required!" );
                }else {
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            e1.getText().toString(),
                            60,
                            java.util.concurrent.TimeUnit.SECONDS,
                            PhoneLogin.this,
                            mCallbacks);
                }
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, e2.getText().toString());
                // [END verify_with_code]
                signInWithPhoneAuthCredential(credential);
            }
        });


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                           // Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(PhoneLogin.this,"Verification Done",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PhoneLogin.this,Registration.class));
                            PhoneLogin.this.finish();
                            // ...
                        } else {
                           // Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(PhoneLogin.this,"Invalid Verification",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

}
