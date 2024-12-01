package com.example.a4;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    TextInputEditText tietUsername, tietPassword;
    Button btnLogin;
    TextView tvSignup, tvForgetPassword;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();

        if (user != null) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }

        // Navigate to Signup
        tvSignup.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            finish();
        });

        // Handle login
        btnLogin.setOnClickListener(view -> {
            String username = tietUsername.getText().toString().trim();
            String password = tietPassword.getText().toString();
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                Toast.makeText(LoginActivity.this, "Email or password is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Logging in...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            auth.signInWithEmailAndPassword(username, password)
                    .addOnSuccessListener(authResult -> {
                        progressDialog.dismiss();
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        tvForgetPassword.setOnClickListener(view -> {
            EditText etEmail = new EditText(view.getContext());
            AlertDialog.Builder forgetPasswordDialog = new AlertDialog.Builder(view.getContext())
                    .setTitle("Forgot Password?")
                    .setView(etEmail)
                    .setPositiveButton("Send", (dialogInterface, i) -> {
                        String email = etEmail.getText().toString().trim();
                        if (TextUtils.isEmpty(email)) {
                            etEmail.setError("Please provide a valid email address");
                        } else {
                            ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                            progressDialog.show();
                            auth.sendPasswordResetEmail(email)
                                    .addOnCompleteListener(task -> {
                                        progressDialog.dismiss();
                                        if (task.isSuccessful()) {
                                            Toast.makeText(LoginActivity.this, "Check your inbox for password reset.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(e -> {
                                        progressDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, "Failed to send email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .setNegativeButton("Cancel", null);
            forgetPasswordDialog.show();
        });

    }

    public void init() {
        tietPassword = findViewById(R.id.tietPassword);
        tietUsername = findViewById(R.id.tietUsername);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignup = findViewById(R.id.tvSignup);
        tvForgetPassword = findViewById(R.id.tvForgetPassword);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }
}