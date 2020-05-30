package com.example.supermarket;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashScreen extends AppCompatActivity {

    private final int SPLASH_TIME_OUT = 2000;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mAuth = FirebaseAuth.getInstance();

        checkUser();
    }

    private void checkUser() {
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            firebaseDatabase = FirebaseFirestore.getInstance();
            firebaseDatabase.collection("users").document(user.getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                String phone = documentSnapshot.getString("phone");
                                if (phone != null) {
                                    Intent intent = new Intent(getApplicationContext(), Home.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_up, R.anim.no_change);
                                } else {
                                    Intent intent = new Intent(getApplicationContext(), VerifyPhone.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_up, R.anim.no_change);
                                }
                            } else {
                                Intent intent = new Intent(getApplicationContext(), VerifyPhone.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_up, R.anim.no_change);
                            }
                        }
                    });
        } else {
            Intent i = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_up, R.anim.no_change);
        }
    }

}
