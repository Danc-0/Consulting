package com.danc.consulting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    FirebaseDatabase mFireBaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseAuth mAuth;

    public static final String TAG = "RegisterActivity";
    public static final String DOMAIN_NAME = "gmail.com";
    EditText mEmail, mPhone, mPassword, mConfirmPassword;
    Button btnRegister;
    TextView loginScreen;
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFireBaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFireBaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();

        mEmail = findViewById(R.id.email);
        mPhone = findViewById(R.id.phone);
        mPassword = findViewById(R.id.password);
        mConfirmPassword = findViewById(R.id.confirmPassword);
        btnRegister = findViewById(R.id.btnSignUp);
        mProgressBar = findViewById(R.id.progressBar);
        loginScreen = findViewById(R.id.login);

        loginScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Registered User");
                if (!isEmpty(mEmail.getText().toString())
                        && !isEmpty(mPhone.getText().toString())
                        && !isEmpty(mPassword.getText().toString())
                        && !isEmpty(mConfirmPassword.getText().toString())){

                    if (isValidDomain(mEmail.getText().toString())){

                        if (stringMatch(mPassword.getText().toString(), mConfirmPassword.getText().toString())) {
                            registerNewEmail(mEmail.getText().toString(), mPassword.getText().toString());
                        } else {
                            Toast.makeText(RegisterActivity.this, "Check passwords do not match", Toast.LENGTH_SHORT).show();
                        }
                    } else{
                    Toast.makeText(RegisterActivity.this, "Please use a valid email domain", Toast.LENGTH_SHORT).show();
                }
                } else{
                    Toast.makeText(RegisterActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void registerNewEmail(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    showDialog();
                    Toast.makeText(RegisterActivity.this, "Successful registration", Toast.LENGTH_SHORT).show();
                    sendEmailVerification();
                    mAuth.signOut();
                    redirectToLoginScreen();
                } if (!task.isSuccessful()){
                    hideDialog();
                    Toast.makeText(RegisterActivity.this, "Unable to register", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void sendEmailVerification() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: Verification email sent to: ");
                    Toast.makeText(RegisterActivity.this, "Email sent for verification", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity.this, "Couldn't verify your email", Toast.LENGTH_SHORT).show();
                }
            }
        });

        hideSoftKeyBoard();
    }

    public void redirectToLoginScreen() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public boolean isEmpty(String string) {
        return string.equals("");
    }

    private boolean isValidDomain(String email) {
        Log.d(TAG, "isValidDomain: verifying if the email is valid: " + email);
        String  domain = email.substring(email.indexOf("@") + 1).toLowerCase();
        Log.d(TAG, "isValidDomain: users domain: " + domain);
        return domain.equals(DOMAIN_NAME);

    }

    private boolean stringMatch(String s1, String s2) {
        return s1.equals(s2);
    }

    private void showDialog() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideDialog() {
        mProgressBar.setVisibility(View.GONE);
    }

    private void hideSoftKeyBoard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}