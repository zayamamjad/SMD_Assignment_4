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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    TextInputEditText tietUsername, tietPassword;
    Button btnSignup;
    TextView tvLogin;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();

        btnSignup.setOnClickListener(view -> {
            String username = tietUsername.getText().toString().trim();
            String password = tietPassword.getText().toString();
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                Toast.makeText(SignupActivity.this, "Email or password is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            showLoadingDialog(true);

            // Create user with email and password
            auth.createUserWithEmailAndPassword(username, password)
                    .addOnSuccessListener(authResult -> {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            user.sendEmailVerification()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(SignupActivity.this, "Verification email sent to " + username, Toast.LENGTH_SHORT).show();

                                            FirebaseUser currentUser = auth.getCurrentUser();
                                            if(currentUser != null){
                                                String userID = auth.getCurrentUser().getUid();
                                                FirebaseFirestore db = FirebaseFirestore.getInstance();

                                                HashMap<String, Object> data = new HashMap<>();
                                                data.put("email", currentUser.getEmail());
                                                db.collection("users")
                                                        .document(userID)
                                                        .set(data)
                                                        .addOnCompleteListener(task1 -> {
                                                            showLoadingDialog(false);
                                                            if (task1.isSuccessful()) {
                                                                startActivity(new Intent(SignupActivity.this, HomeActivity.class));
                                                                finish();
                                                            } else {
                                                                Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        } else {
                                            Toast.makeText(SignupActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        showLoadingDialog(false);
                        Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        tvLogin.setOnClickListener(view -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });


    }

    private void showLoadingDialog(boolean show) {
        if (show) {
            Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();
        }
    }

    public void init() {
        tietPassword = findViewById(R.id.tietPassword);
        tietUsername = findViewById(R.id.tietUsername);
        btnSignup = findViewById(R.id.btnSignup);
        tvLogin = findViewById(R.id.tvLogin);
        auth = FirebaseAuth.getInstance();
    }
}
