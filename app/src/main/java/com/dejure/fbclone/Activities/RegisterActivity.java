package com.dejure.fbclone.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dejure.fbclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kaopiz.kprogresshud.KProgressHUD;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEt, passwordEt, confirmPasswordEt;
    private MaterialButton registerBtn;
    private TextView loginTv;

    private FirebaseAuth mAuth;
    private KProgressHUD hud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        confirmPasswordEt = findViewById(R.id.confirmPasswordEt);
        registerBtn = findViewById(R.id.registerBtn);
        loginTv = findViewById(R.id.loginTv);

        mAuth = FirebaseAuth.getInstance();

        //Progress Dialog
        hud = KProgressHUD.create(RegisterActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });

        loginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void validateData() {

        String mail = emailEt.getText().toString().trim();
        String password = passwordEt.getText().toString().trim();
        String confirmPassword = confirmPasswordEt.getText().toString().trim();

        if (!(Patterns.EMAIL_ADDRESS.matcher(mail).matches())) {
            emailEt.setError("Invalid email");
            return;
        }

        if (TextUtils.isEmpty(mail)) {
            emailEt.setError("Invalid email");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEt.setError("Enter at-least 6 digit password");
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordEt.setError("Enter at-least 6 digit password");
            return;
        }
        if (password.length() < 6) {
            passwordEt.setError("Enter at-least 6 digit password");
            return;
        }

        if (confirmPassword.length() < 6) {
            confirmPasswordEt.setError("Enter at-least 6 digit password");
            return;
        }

        if (!(password.equals(confirmPassword))) {
            passwordEt.setError("Password doesn't match with confirm password");
            return;
        }

        registerUser(mail, password);

    }

    private void registerUser(String mail, String password) {

        hud.show();
        mAuth.createUserWithEmailAndPassword(mail, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            hud.dismiss();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            hud.dismiss();
                        }
                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hud.dismiss();
                Toast.makeText(RegisterActivity.this, "" + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        finish();
    }

}
