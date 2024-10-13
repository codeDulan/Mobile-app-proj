package com.example.dishdash;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignupWindow extends AppCompatActivity {

    EditText txtfirstname, txtlastname, txtemail, txtpassword, txtconfirmpassword;
    Button signupbutton;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), HomeWindow2.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_window);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        txtfirstname = findViewById(R.id.firstname);
        txtlastname = findViewById(R.id.lastname);
        txtemail = findViewById(R.id.email);
        txtpassword = findViewById(R.id.password);
        txtconfirmpassword = findViewById(R.id.confirmpassword);
        signupbutton = findViewById(R.id.signupbutton);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.signin_link2);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupWindow.this, LoginWindow.class);
                startActivity(intent);
                finish();
            }
        });

        signupbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String firstName, lastName, email, password, confirmpassword;

                progressBar.setVisibility(View.VISIBLE);

                firstName = String.valueOf(txtfirstname.getText());
                lastName = String.valueOf(txtlastname.getText());
                email = String.valueOf(txtemail.getText());
                password = String.valueOf(txtpassword.getText());
                confirmpassword = String.valueOf(txtconfirmpassword.getText());

                if (TextUtils.isEmpty(firstName)){
                    Toast.makeText(SignupWindow.this, "Please enter your first name", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (TextUtils.isEmpty(lastName)){
                    Toast.makeText(SignupWindow.this, "Please enter your last name", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(SignupWindow.this, "Please enter an email", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    Toast.makeText(SignupWindow.this, "Please enter a password", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (TextUtils.isEmpty(confirmpassword)){
                    Toast.makeText(SignupWindow.this, "Please confirm your password", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                // Validate if password and confirm password match
                if (!password.equals(confirmpassword)) {
                    Toast.makeText(SignupWindow.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                // Create user using Firebase Auth
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            progressBar.setVisibility(View.GONE);

                            if (task.isSuccessful()) {
                                // Sign in success, save user data to Firebase Realtime Database
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

                                String userId = firebaseUser.getUid();
                                User user = new User(userId, firstName, email, "");  // Empty image URL for now

                                // Storing the user under the user ID in Realtime Database
                                databaseReference.child(userId).setValue(user)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(SignupWindow.this, "Signup successful!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(SignupWindow.this, HomeWindow2.class);
                                            startActivity(intent);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(SignupWindow.this, "Failed to store user data.", Toast.LENGTH_SHORT).show());

                            } else {
                                // Display the specific error message
                                Exception exception = task.getException();
                                Toast.makeText(SignupWindow.this, "Authentication failed: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }
}
