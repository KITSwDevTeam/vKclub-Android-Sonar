package com.example.admin.vkclub;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.camera2.params.Face;
import android.os.Build;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthActionCodeException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;

import static com.example.admin.vkclub.Calling.context;

public class LoginActivity extends AppCompatActivity {
    private final String TAG = "LOG TAG";
    // Declare Firebase auth
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    // Buttons Declaration
    private Button signin, createacc, forgetpassword;
    // EditText Declaration
    private EditText email, pass;
    // TextView Declaration
    private TextView emailValidation, passValidation;
    private int submitAttempt = 0;
    private ProgressBar spinner;
    private CallbackManager callbackManager;
    SharedPreferences preference;
    SharedPreferences.Editor editor;
    String currentpass;
    private static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;

        if(Build.VERSION.SDK_INT >= 21){
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorStatusBar));
        }

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);

        // Instantiate buttons
        signin = (Button)findViewById(R.id.signin);
        createacc = (Button)findViewById(R.id.createacc);
        forgetpassword = (Button)findViewById(R.id.forgetpass);

        // Instantiate EditText
        email = (EditText)findViewById(R.id.email);
        pass = (EditText)findViewById(R.id.pass);

        // Instantiate TextView
        emailValidation = (TextView)findViewById(R.id.emailvalidation);
        passValidation = (TextView)findViewById(R.id.passvalidation);

        // screen navigation
        navigateScreen(createacc, CreateAccount.class);
        navigateScreen(forgetpassword, ForgetPassword.class);

        // call sign in function
        signIn(signin);

        // Initialize facebook login button
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton)findViewById(R.id.facebookLoginbtn);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        Log.d(TAG, "handleFacebookAccessToken:" + accessToken);

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success");
                    Intent intent = new Intent(LoginActivity.this, Dashboard.class);
                    startActivity(intent);
                    finish();
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signIn(Button btn) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final boolean emailStatus, passStatus;
                final String getEmail = email.getText().toString();
                final String getPass = pass.getText().toString();

                if ((getEmail.indexOf("@") <= 0) || getEmail.length() == 0) {
                    emailValidation.setText(getString(R.string.invalid_email));
                    emailStatus = false;
                }else if (getEmail.length() == 0){
                    emailValidation.setText("Please enter your email address.");
                    emailStatus = false;
                }else {
                    emailValidation.setText("");
                    emailStatus = true;
                }

                if (getPass.length() < 6) {
                    passValidation.setText(getString(R.string.invalid_password));
                    passStatus = false;
                } else {
                    passValidation.setText("");
                    passStatus = true;
                }

                if (emailStatus && passStatus) {
                    spinner.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(getEmail, getPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser mUser = mAuth.getCurrentUser();

                                preference = PreferenceManager.getDefaultSharedPreferences(context);
                                editor = preference.edit();
                                editor.putString("pass", getPass);
                                editor.commit();

                                boolean emailVerified = mUser.isEmailVerified();
                                System.out.println("Email Verified :::::::::::::::   " + emailVerified );
                                if (emailVerified){
                                    Intent intent =  new Intent(LoginActivity.this, Dashboard.class);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    spinner.setVisibility(View.GONE);
                                    presentDialog("Please verify your email address!", "Check email sent to " + getEmail + " for verification link.\nThank you for using Vkclub.");
                                }
                            } else {
                                spinner.setVisibility(View.GONE);
                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    // Invalid password
                                    presentDialog("Login Failed..", "Invalid Password");
                                    preventSpam(submitAttempt++);
                                } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                    // Invalid Email id
                                    presentDialog("Login Failed..", "Invalid Email");
                                    preventSpam(submitAttempt++);
                                } else if (task.getException() instanceof FirebaseNetworkException) {
                                    // No internet Connection
                                    presentDialog("Login Failed..", "No network coverage");
                                } else {
                                    try {
                                        throw task.getException();
                                    } catch (Exception e) {
                                        presentDialog("Login Failed..", e.getMessage());
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void preventSpam(int att) {
        if (att < 5) {
            Toast.makeText(LoginActivity.this, "Login Attempt " + submitAttempt, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(LoginActivity.this, "You cannot login anymore...", Toast.LENGTH_SHORT).show();
        }
    }

    private void presentDialog(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void navigateScreen(Button btn, final Class next) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, next);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
