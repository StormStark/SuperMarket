package com.example.supermarket;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout username, password;
    private TextView signUp;
    private Button loginButton, googleButton;
    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    int RC_SIGN_IN = 0;

    private DatabaseReference firebaseDatabase;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        googleButton = findViewById(R.id.googleButton);
        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        signUp = findViewById(R.id.signUp);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(this);
        signUp.setOnClickListener(this);

        username.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                validationCheckUsername(username);
            }
        });

        password.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                validationCheckPassword(password);
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginButton:
                validationCheckUsername(username);
                validationCheckPassword(password);
                Log.d("TEST", username.getEditText().getText().toString());
                Log.d("TEST", password.getEditText().getText().toString());
                logIn();
                break;
            case R.id.signUp:
                Intent intent = new Intent(this, SignUp.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up, R.anim.no_change);
                break;
        }
    }

    private boolean validationCheckUsername(TextInputLayout textInputLayout) {
        if (username.getEditText().getText().toString().isEmpty()) {
            username.setError("Email required");
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(username.getEditText().getText().toString()).matches()) {
            username.setError("Not a valid email");
            return false;
        } else
            username.setError(null);

        return true;
    }

    private boolean validationCheckPassword(TextInputLayout textInputLayout) {
        if (password.getEditText().getText().toString().isEmpty()) {
            password.setError("Password required");
            return false;
        } else if (password.getEditText().getText().toString().length() < 8) {
            password.setError("Min 8 characters");
            return false;
        } else
            password.setError(null);
        return true;
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                //Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d("MIKE", user.getDisplayName());
                            Intent intent = new Intent(MainActivity.this, VerifyPhone.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_up, R.anim.no_change);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Login Failed",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void logIn() {
        if (validationCheckUsername(username) && validationCheckPassword(password)) {

            mAuth.signInWithEmailAndPassword(username.getEditText().getText().toString(), password.getEditText().getText().toString())
                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(MainActivity.this, "Authentication successful.",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, VerifyPhone.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_up, R.anim.no_change);
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }
    }

}
