package com.example.chatme;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StartActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText textEmail;
    private EditText textPassword;
    private EditText textPasswordConfirm;
    private EditText textName;
    private String email;
    private String password;
    private String name;
    private Button buttonLogIn;
    private TextView textViewTapChangeLogIn;
    private boolean loginIsActive = true;
    private DatabaseReference userRef;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        textEmail = findViewById(R.id.editTextEmail);
        textPassword = findViewById(R.id.editTextPassword);
        textPasswordConfirm = findViewById(R.id.editTextPasswordConfirm);
        textName = findViewById(R.id.editTextName);
        buttonLogIn = findViewById(R.id.buttonLogIn);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userRef = firebaseDatabase.getReference().child("users");
        textViewTapChangeLogIn = findViewById(R.id.textViewTapChangeLogIn);
        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(textEmail.getText().toString().trim(), textPassword.getText().toString().trim());
            }
        });
    }

    @Override
    protected void onStart() {
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(StartActivity.this, ListUserActivity.class));
        }
        super.onStart();
    }

    private void createAccount(String email, String password) {
        if (loginIsActive) {
            if(textEmail.getText().toString().trim().equals("")){
                Toast.makeText(this, "Email is don`t must be empty", Toast.LENGTH_SHORT).show();
            }else if(textPassword.getText().toString().trim().equals("")){
                Toast.makeText(this, "Password is don`t must be empty", Toast.LENGTH_SHORT).show();
            }else{
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(StartActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d("TAG", "signUp:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Intent intent = new Intent(StartActivity.this, ListUserActivity.class);
                                    intent.putExtra("name", textName.getText().toString().trim());
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(StartActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

        }else {
            if (textEmail.getText().toString().trim().equals("")) {
                Toast.makeText(this, "Email is don`t must be empty", Toast.LENGTH_SHORT).show();
            } else if (textPassword.getText().toString().trim().length() < 7) {
                Toast.makeText(this, "Password must be least 7 characters", Toast.LENGTH_SHORT).show();
            } else if (textPassword.getText().toString().trim().equals("")) {
                Toast.makeText(this, "Password is don`t must be empty", Toast.LENGTH_SHORT).show();
            } else if (!textPassword.getText().toString().trim().equals(textPasswordConfirm.getText().toString().trim())) {
                    Toast.makeText(this, "Password don`t match", Toast.LENGTH_SHORT).show();
            } else {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("TAG", "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    createUser(user);
                                    /*assert currentUser != null;
                                    getUserName(currentUser, textName.getText().toString().trim());*/
                                    Intent intent = new Intent(StartActivity.this, ListUserActivity.class);
                                    intent.putExtra("name", textName.getText().toString().trim());
                                    startActivity(intent);
//                            updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("TAG", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(StartActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                                }
                            }
                        });
                }

            }
        }

    private void createUser(FirebaseUser currentUser) {
        Users user = new Users();
        user.setId(currentUser.getUid());
        user.setEmail(currentUser.getEmail());
        user.setName(textName.getText().toString().trim());
        userRef.push().setValue(user);
    }

/*    private void getUserName(FirebaseUser user, String name){
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        user.updateProfile(profileChangeRequest);
    }*/

    public void toggleModeLogin(View view) {
        if (loginIsActive) {
            loginIsActive = false;
            buttonLogIn.setText("Sign Up");
            textViewTapChangeLogIn.setText("Or, Log in");
            textPasswordConfirm.setVisibility(View.VISIBLE);
        } else {
            loginIsActive = true;
            buttonLogIn.setText("Log in");
            textViewTapChangeLogIn.setText("Or, register");
            textPasswordConfirm.setVisibility(View.GONE);
        }
    }

    private void signOut() {
        mAuth.signOut();
    }
/*
    private void reload() {
        mAuth.getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(StartActivity.this, "Reload successful!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(StartActivity.this, "Reload successful!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }*/
}