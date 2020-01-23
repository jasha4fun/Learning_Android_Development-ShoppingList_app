package com.example.shoppinglist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button buttonLogin;
    private TextView signUp;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), ProductListActivity.class));
        }
        dialog = new ProgressDialog(this);

        email = findViewById(R.id.email_login);
        password = findViewById(R.id.password_login);

        buttonLogin = findViewById(R.id.button_login);

        signUp = findViewById(R.id.signup_text);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Email = email.getText().toString().trim();
                String Password = password.getText().toString().trim();

                if (TextUtils.isEmpty(Email)) {
                    email.setError("Required Field...");
                    return;
                }

                if (TextUtils.isEmpty(Password)) {
                    password.setError("Required Field...");
                    return;
                }

                dialog.setMessage("Processing");
                dialog.show();

                firebaseAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            startActivity(new Intent(getApplicationContext(), ProductListActivity.class));

                            Toast.makeText(getApplicationContext(), "Successful :)", Toast.LENGTH_SHORT).show();

                            dialog.dismiss();

                        } else {

                            Toast.makeText(getApplicationContext(), "Failed :(", Toast.LENGTH_SHORT).show();

                            dialog.dismiss();
                        }

                    }
                });

            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));

            }
        });
    }
}
