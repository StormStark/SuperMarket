package com.example.supermarket;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUp extends AppCompatActivity {

    private TextInputLayout username, password, email;
    private Button signupButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        signupButton = findViewById(R.id.signupButton);

        username.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //validationCheckUsername(username);
            }
        });

        email.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                validationCheckEmail(email);
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

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validationCheckEmail(email) && validationCheckPassword(password) && validationCheckUsername(username)) {
                    mAuth.createUserWithEmailAndPassword(email.getEditText().getText().toString(), password.getEditText().getText().toString())
                            .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(username.getEditText().getText().toString().trim())
                                                .build();
                                        user.updateProfile(profileUpdate)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Intent intent = new Intent(SignUp.this, VerifyPhone.class);
                                                            startActivity(intent);
                                                            overridePendingTransition(R.anim.slide_up, R.anim.no_change);
                                                        } else {

                                                        }
                                                    }
                                                });

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(SignUp.this, "Authentication failed." + task.getException(),
                                                Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
            }
        });
    }

    private boolean validationCheckEmail(TextInputLayout textInputLayout) {
        if (textInputLayout.getEditText().getText().toString().isEmpty()) {
            textInputLayout.setError("Email required");
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(textInputLayout.getEditText().getText().toString()).matches()) {
            textInputLayout.setError("Not a valid email");
            return false;
        } else
            textInputLayout.setError(null);

        return true;
    }

    private boolean validationCheckUsername(TextInputLayout textInputLayout) {
        if (textInputLayout.getEditText().getText().toString().isEmpty()) {
            textInputLayout.setError("Username required");
            return false;
        } else
            textInputLayout.setError(null);

        return true;
    }

    private boolean validationCheckPassword(TextInputLayout textInputLayout) {
        if (textInputLayout.getEditText().getText().toString().isEmpty()) {
            textInputLayout.setError("Password required");
            return false;
        } else if (textInputLayout.getEditText().getText().toString().length() < 8) {
            textInputLayout.setError("Min 8 characters");
            return false;
        } else
            textInputLayout.setError(null);
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.no_change, R.anim.sldie_down);
    }
}
