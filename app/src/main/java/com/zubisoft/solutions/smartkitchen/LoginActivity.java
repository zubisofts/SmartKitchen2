package com.zubisoft.solutions.smartkitchen;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.zubisoft.solutions.smartkitchen.model.User;
import com.zubisoft.solutions.smartkitchen.util.SmartKitchenPreference;
import com.zubisoft.solutions.smartkitchen.util.SmartKitchenUtil;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 10044;
    private EditText txtEmail;
    private EditText txtPassword;
    private RelativeLayout btnLogin;
    private FloatingActionButton btnFb;
    private FloatingActionButton btnGoogle;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    private CallbackManager callbackManager;
    private SmartKitchenPreference preference;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        RelativeLayout link_register = findViewById(R.id.link_register);
        link_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

        /*Initialize google signin options*/
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        callbackManager = CallbackManager.Factory.create();

        preference = SmartKitchenPreference.getInstance(this);

        txtEmail = findViewById(R.id.email);
        txtPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btn_login);

        btnFb = findViewById(R.id.btn_fb);
        btnFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleFacebookLogin();
            }
        });
        btnGoogle = findViewById(R.id.btn_google);
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleGoogleSignin();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtEmail.getText().toString();
                String password = txtPassword.getText().toString();

                boolean valid = validateFields(email, password);
                if (valid) {
                    loginUserWithEmailAndPassword(email, password);
                } else {
                    SmartKitchenUtil.showToast(
                            LoginActivity.this,
                            "Please fill all the required fields.",
                            3000,
                            SmartKitchenUtil.ToastType.ERROR,
                            Gravity.TOP
                    );
                }
            }
        });

    }

    private void handleGoogleSignin() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    private void loginUserWithEmailAndPassword(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            fetchUser(task.getResult().getUser().getUid());
                        } else {
                            SmartKitchenUtil.showToast(
                                    LoginActivity.this,
                                    task.getException().getMessage(),
                                    3000,
                                    SmartKitchenUtil.ToastType.ERROR,
                                    Gravity.TOP
                            );
                        }
                    }
                });
    }

    private void fetchUser(final String uid) {
        firestore.collection("user")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    QuerySnapshot snapshots = task.getResult();
                    for (DocumentSnapshot snapshot : snapshots.getDocuments()) {
                        User user = snapshot.toObject(User.class);
                        if (user.getUser_id().equals(uid)) {

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                            intent.putExtra("uid",user.getUser_id());
//                            intent.putExtra("id",user.getId());
                            preference.logUserDetails(user.getUser_id(), user.getId());
                            preference.setLoginStatus(true);
                            startActivity(intent);

                            return;
                        }
                    }
                } else {
                    SmartKitchenUtil.showToast(
                            LoginActivity.this,
                            task.getException().getMessage(),
                            3000,
                            SmartKitchenUtil.ToastType.ERROR,
                            Gravity.TOP
                    );
                }
            }
        });
    }

    private boolean validateFields(String email, String password) {

        return !(TextUtils.isEmpty(email) && TextUtils.isEmpty(password));
    }

    private void handleFacebookLogin() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                loginFacebook(accessToken);
            }


            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    private void loginFacebook(final AccessToken accessToken) {

        final AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's informatio
                            String uid = task.getResult().getUser().getUid();
                            fetchUser(uid);

                        } else {
                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            SmartKitchenUtil.showToast(
                                    LoginActivity.this,
                                    task.getException().getMessage(),
                                    3000,
                                    SmartKitchenUtil.ToastType.ERROR,
                                    Gravity.TOP
                            );

                        }

                        // [START_EXCLUDE]
//                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                final GoogleSignInAccount account = task.getResult(ApiException.class);
                auth.fetchSignInMethodsForEmail(account.getEmail())
                        .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                            @Override
                            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                if (!task.getResult().getSignInMethods().isEmpty()) {
                                    firebaseAuthWithGoogle(account);
                                } else {
                                    SmartKitchenUtil.showToast(
                                            LoginActivity.this,
                                            "User not exist or registered...",
                                            2000,
                                            SmartKitchenUtil.ToastType.ERROR,
                                            Gravity.TOP
                                    );
                                }
                            }
                        });

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
//                Log.w(TAG, "Google sign in failed", e);
                // ...
            }

        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        // [START_EXCLUDE silent]
//            progressDialog.show();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            final FirebaseUser user = auth.getCurrentUser();
                            User user1 = new User(user.getUid(), user.getDisplayName(), user.getEmail(), user.getPhotoUrl().toString());
                            firestore.collection("user")
                                    .add(user1)
                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            if (task.isSuccessful()) {
                                                final String id = task.getResult().getId();
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                                                                    intent.putExtra("uid",user.getUid());
//                                                                    intent.putExtra("id",id);
                                                preference.logUserDetails(user.getUid(), id);
                                                preference.setLoginStatus(true);
                                                startActivity(intent);
                                                finish();
                                            } else {
//                                                    progressDialog.hide();
                                                SmartKitchenUtil.showToast(
                                                        LoginActivity.this,
                                                        "An error occured, please try again.",
                                                        1000,
                                                        SmartKitchenUtil.ToastType.ERROR,
                                                        Gravity.TOP
                                                );
                                            }
                                        }
                                    });
                        } else {
                            // If sign in fails, display a message to the user.
                            SmartKitchenUtil.showToast(
                                    LoginActivity.this,
                                    "Authentication failed...",
                                    1000,
                                    SmartKitchenUtil.ToastType.ERROR,
                                    Gravity.TOP
                            );

//                                progressDialog.hide();

                        }

                        /* make the API call */

                        // [START_EXCLUDE]
//                        progressDialog.hide();
                        // [END_EXCLUDE]
                    }
                });
    }


}
