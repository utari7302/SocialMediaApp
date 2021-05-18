package com.dejure.fbclone.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dejure.fbclone.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kaopiz.kprogresshud.KProgressHUD;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEt, passwordEt;
    private MaterialButton LoginBtn;
    private TextView registerTv, forgetPassword;

    private static final int RC_SIGN_IN = 100;
    GoogleSignInClient mGoogleSignInClient;

    private FirebaseAuth mAuth;

    private KProgressHUD hud;

    private SignInButton googleSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEt = findViewById(R.id.emailEt);
        passwordEt = findViewById(R.id.passwordEt);
        LoginBtn = findViewById(R.id.LoginBtn);
        registerTv = findViewById(R.id.registerTv);
        forgetPassword = findViewById(R.id.forgetPassword);

        googleSignInButton = findViewById(R.id.googleSignInButton);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        hud = KProgressHUD.create(LoginActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialogForRecoverPassword();
            }
        });

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mail = emailEt.getText().toString();
                String password = passwordEt.getText().toString();

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

                if (password.length() < 6) {
                    passwordEt.setError("Enter at-least 6 digit password");
                    return;
                }

                userLogin(mail, password);
            }
        });

        registerTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

    }

    private void showBottomSheetDialogForRecoverPassword() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(LoginActivity.this,
                R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(LoginActivity.this).inflate(R.layout.bottom_sheet_dialog,
                (LinearLayout) findViewById(R.id.bottomSheetContainer));

        EditText emailEt;
        MaterialButton recoverBtn, cancelBtn;

        emailEt = view.findViewById(R.id.emailEt);
        recoverBtn = view.findViewById(R.id.recoverBtn);
        cancelBtn = view.findViewById(R.id.cancelBtn);


        recoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = emailEt.getText().toString().trim();

                if (TextUtils.isEmpty(mail)) {
                    emailEt.setError("Email Required");
                    return;
                }

                recoverPassword(mail);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

    }

    private void recoverPassword(String mail) {

        hud.setLabel("Sending Email");
        hud.show();
        mAuth.sendPasswordResetEmail(mail)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            hud.dismiss();
                            Toast.makeText(LoginActivity.this, "Email sent...", Toast.LENGTH_SHORT).show();
                        } else {
                            hud.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hud.dismiss();
                Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void userLogin(String mail, String password) {
        hud.show();
        mAuth.signInWithEmailAndPassword(mail, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            hud.dismiss();
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            finish();
                        } else {

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hud.dismiss();
                Toast.makeText(LoginActivity.this, "" + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.

                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        finish();
    }
}