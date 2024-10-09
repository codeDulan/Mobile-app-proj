package com.example.dishdash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

public class LoginWindow extends AppCompatActivity {

    EditText txtemail, txtpassword;
    Button loginbutton;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView, textView2;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    ImageButton googleBtn;
    ImageButton facebookBtn;

    CallbackManager callbackManager;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), HomeWindow2.class);
            startActivity(intent);
            finish();
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_window);

        callbackManager = CallbackManager.Factory.create();

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        startActivity(new Intent(LoginWindow.this, HomeWindow2.class));
                        finish();
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException exception) {
                    }
                });

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        googleBtn = findViewById(R.id.google_signin_button);
        facebookBtn = findViewById(R.id.facebook_signin_button);

        mAuth = FirebaseAuth.getInstance();
        txtemail = findViewById(R.id.email);
        txtpassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        loginbutton = findViewById(R.id.signinbutton);
        textView = findViewById(R.id.signup_link2);
        textView2 = findViewById(R.id.forgotPassword);

        facebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginManager.getInstance().logInWithReadPermissions(LoginWindow.this, Arrays.asList("public_profile"));

            }
        });

        googleBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                signIn();
            }
        });

        textView.setOnClickListener(view -> {
            Intent intent = new Intent(LoginWindow.this, SignupWindow.class);
            startActivity(intent);
            finish();
        });

        textView2.setOnClickListener(view -> {
            String email = txtemail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(LoginWindow.this, "Please enter your registered email", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginWindow.this, "Password reset email sent. Check your inbox.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LoginWindow.this, "Error sending reset email. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        loginbutton.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            String email = txtemail.getText().toString();
            String password = txtpassword.getText().toString();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(LoginWindow.this, "Please enter the email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(LoginWindow.this, "Please enter the password", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), HomeWindow2.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginWindow.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    void signIn() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //facebook login=====================================
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        //===================================================

        if (requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Google sign-in failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                navigateToSecondActivity();
            } else {
                Toast.makeText(getApplicationContext(), "Authentication Failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void navigateToSecondActivity() {
        finish();
        Intent intent = new Intent(LoginWindow.this, HomeWindow.class);
        startActivity(intent);
    }
}
