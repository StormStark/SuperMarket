package com.example.supermarket;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class VerifyPhone extends AppCompatActivity {

    private String phoneN;
    private FirebaseUser currentUser;

    private CountryCodePicker cpp;
    private Button sendCode, verifyButton;
    private TextInputLayout phoneNumber, verificationCode;
    private TextView userName;

    private String verificationId;
    private FirebaseAuth mAuth;
    private Timer codeTimer;

    private FirebaseFirestore firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        mAuth = FirebaseAuth.getInstance();
        currentUser= mAuth.getCurrentUser();

        userName = findViewById(R.id.userName);
        userName.setText("Hi, "+currentUser.getDisplayName());
        verifyButton = findViewById(R.id.verifyButton);
        phoneNumber = findViewById(R.id.phoneNumber);
        cpp = findViewById(R.id.cpp);
        sendCode = findViewById(R.id.sendCode);
        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final int[] x = {60};

                phoneN = cpp.getSelectedCountryCodeWithPlus() + phoneNumber.getEditText().getText();
                Log.d("MIKE", phoneN);
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneN,        // Phone number to verify
                        x[0],                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        VerifyPhone.this,               // Activity (for callback binding)
                        mCallbacks);        // OnVerificationStateChangedCallbacks
                sendCode.setClickable(false);
                codeTimer = new Timer();
                codeTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                x[0]--;
                                sendCode.setText("Resend ("+x[0]+"s)");
                                if(x[0] == 0) {
                                    cancel();
                                    sendCode.setText("Resend");
                                    sendCode.setClickable(true);
                                }
                            }
                        });
                    }
                }, 0, 1000);
            }
        });

        firebaseDatabase = FirebaseFirestore.getInstance();

        verificationCode = findViewById(R.id.verificationCode);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, verificationCode.getEditText().getText().toString());
                verifyPhone(credential);
            }
        });


    }

    final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            Log.d("CODE SE", s);
            verificationId = s;
        }
    };

    private void verifyPhone(PhoneAuthCredential credential) {

        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Users user = new Users(currentUser.getDisplayName(),
                                    currentUser.getEmail(),
                                    currentUser.getPhotoUrl(),
                                    phoneN);
                            codeTimer.cancel();
                            firebaseDatabase.collection("users").document(currentUser.getUid())
                                    .set(user);

                            Toast.makeText(VerifyPhone.this, "Whoo Hoo", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(VerifyPhone.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
