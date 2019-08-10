package com.zubisoft.solutions.smartkitchen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.zubisoft.solutions.smartkitchen.model.User;
import com.zubisoft.solutions.smartkitchen.util.SmartKitchenPreference;
import com.zubisoft.solutions.smartkitchen.util.SmartKitchenUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1032;
    private EditText txtUsername, txtEmail, txtPassword;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private ProgressBar progress;
    private RelativeLayout link_login;
    private RelativeLayout btnRegister;
    private TextView textRegister;
    private CallbackManager mCallbackManager;
    private ProgressDialog progressDialog;
    private GoogleSignInClient mGoogleSignInClient;

    private SmartKitchenPreference preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        FacebookSdk.setApplicationId("367529084197935");
        FacebookSdk.sdkInitialize(this);
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        setContentView(R.layout.activity_register);

        /*Initialize google signin options*/
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        preference = SmartKitchenPreference.getInstance(this);

        FloatingActionButton btnFacebook = findViewById(R.id.btn_fb);
        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> readPermisions = new ArrayList<>();
                readPermisions.add("public_profile");
                readPermisions.add("email");
                LoginManager.getInstance().logInWithReadPermissions(RegisterActivity.this, readPermisions);
            }
        });

        FloatingActionButton btnGoogle = findViewById(R.id.btn_google);
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        txtUsername = findViewById(R.id.username);
        txtEmail = findViewById(R.id.email);
        txtPassword = findViewById(R.id.password);

        progress = findViewById(R.id.progress);
        textRegister = findViewById(R.id.textRegister);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        CardView btnSkip = findViewById(R.id.btnSkip);
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preference.logUserDetails("", "");
                preference.setLoginStatus(true);
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();
            }
        });

        link_login = findViewById(R.id.link_login);
        link_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        btnRegister = findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = txtUsername.getText().toString();
                String email = txtEmail.getText().toString();
                String password = txtPassword.getText().toString();

                if (validateFields(username, email, password)) {
                    if (SmartKitchenUtil.isNetworkAvailable(RegisterActivity.this)) {
                        progress.setVisibility(View.VISIBLE);
                        btnRegister.setClickable(false);
                        btnRegister.setEnabled(false);
                        textRegister.setText("Creating account...");
                        registerUser(username, email, password);
                    } else {
                        SmartKitchenUtil.showToast(
                                RegisterActivity.this,
                                "No internet connection available.",
                                3000,
                                SmartKitchenUtil.ToastType.ERROR,
                                Gravity.TOP);
                    }

                }
            }
        });

        final AccessTokenTracker tokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {


            }
        };

    }

    private void handleFacebookAccessToken(final AccessToken accessToken) {
        final AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());


        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                try {
                    String email = object.getString("email");
                    final String image = object.getJSONObject("picture").getJSONObject("data").getString("url");
                    auth.fetchSignInMethodsForEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                                    if (task.getResult().getSignInMethods().isEmpty()) {
                                        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    FirebaseUser user = task.getResult().getUser();
                                                    String uid = user.getUid();
                                                    String email = user.getEmail();
                                                    String username = user.getDisplayName();
                                                    addUserToDatabase(uid, username, email, image);
                                                } else {
                                                    SmartKitchenUtil.showToast(
                                                            RegisterActivity.this,
                                                            task.getException().getMessage(),
                                                            3000,
                                                            SmartKitchenUtil.ToastType.ERROR,
                                                            Gravity.TOP);
                                                }
                                            }
                                        });
                                    } else {
                                        SmartKitchenUtil.showToast(
                                                RegisterActivity.this,
                                                "User with this account already exists.",
                                                3000,
                                                SmartKitchenUtil.ToastType.ERROR,
                                                Gravity.TOP);
                                    }

                                }
                            });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture.width(200)");
        request.setParameters(parameters);
        // Initiate the GraphRequest
        request.executeAsync();
        //ArrayList<String>   params=

    }

    private void registerUser(final String username, final String email, final String password) {

        auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        boolean check = !task.getResult().getSignInMethods().isEmpty();

                        if (check) {
                            // User does not exist, go ahead and add it
                            auth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {

                                            if (task.isSuccessful()) {
                                                if (task.getResult() != null) {
                                                    String id = task.getResult().getUser().getUid();
                                                    addUserToDatabase(id, username, email, "");
                                                } else {
                                                    progress.setVisibility(View.GONE);
                                                    btnRegister.setEnabled(true);
                                                    btnRegister.setClickable(true);
                                                    textRegister.setText("Register");
                                                    SmartKitchenUtil.showToast(
                                                            RegisterActivity.this,
                                                            task.getException().getMessage(),
                                                            2000,
                                                            SmartKitchenUtil.ToastType.ERROR,
                                                            Gravity.TOP
                                                    );
                                                }
                                            }
                                        }
                                    });
                        } else {
                            // User exists,
                            progress.setVisibility(View.GONE);
                            btnRegister.setEnabled(true);
                            btnRegister.setClickable(true);
                            textRegister.setText("Register");
                            SmartKitchenUtil.showToast(
                                    RegisterActivity.this,
                                    "Sorry, this user already exists...",
                                    2000,
                                    SmartKitchenUtil.ToastType.ERROR,
                                    Gravity.TOP
                            );
                        }
                    }
                });

//                .addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                SmartKitchenUtil.showToast(
//                        RegisterActivity.this,
//                        "An error occured, please try again.",
//                        1000,
//                        SmartKitchenUtil.ToastType.ERROR,
//                        Gravity.TOP
//                );
//            }
//        });


    }

    private void addUserToDatabase(final String id, final String username, final String email, final String imageUrl) {

        User user = new User(id, username, email, imageUrl);

        firestore.collection("user")
                .add(user)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {

                            final String id2 = task.getResult().getId();
                            task.getResult().update("id", id2)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                progress.setVisibility(View.GONE);
                                                btnRegister.setEnabled(true);
                                                btnRegister.setClickable(true);
                                                textRegister.setText("Register");
                                                SmartKitchenUtil.showToast(
                                                        RegisterActivity.this,
                                                        "Account created successfully",
                                                        1000,
                                                        SmartKitchenUtil.ToastType.SUCCESS,
                                                        Gravity.TOP
                                                );

                                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
//                                                intent.putExtra("uid",id);
//                                                intent.putExtra("id",id2);
                                                preference.logUserDetails(id, id2);
                                                preference.setLoginStatus(true);
                                                startActivity(intent);
                                                finish();

                                            } else {
                                                progress.setVisibility(View.GONE);
                                                btnRegister.setEnabled(true);
                                                btnRegister.setClickable(true);
                                                textRegister.setText("Register");
                                                SmartKitchenUtil.showToast(
                                                        RegisterActivity.this,
                                                        "An error occured, please try again.",
                                                        1000,
                                                        SmartKitchenUtil.ToastType.ERROR,
                                                        Gravity.TOP
                                                );
                                            }

                                        }
                                    });


                        }
                    }
                });

    }

    public boolean validateFields(String username, String email, String password) {


        String err = "";

        if (TextUtils.isEmpty(username)) {
            err += "username field is empty=>";
        }
        if (TextUtils.isEmpty(email)) {
            err += "email field is empty=>";
        }
        if (TextUtils.isEmpty(password)) {
            err += "password field is empty";
        }

        String[] errors = err.split("=>");
        String finalErrors = "";

        for (int i = 0; i < errors.length; i++) {
            if (i == errors.length - 1) {
                finalErrors += errors[i];
            } else {
                finalErrors += errors[i] + "\n";
            }
        }

        if (err.isEmpty()) {
            return true;
        } else {
            SmartKitchenUtil.showToast(
                    RegisterActivity.this,
                    finalErrors,
                    3000,
                    SmartKitchenUtil.ToastType.ERROR,
                    Gravity.TOP);
            return false;
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

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
                                if (task.getResult().getSignInMethods().isEmpty()) {
                                    firebaseAuthWithGoogle(account);
                                } else {
                                    SmartKitchenUtil.showToast(
                                            RegisterActivity.this,
                                            "User with the email already exists...",
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
        progressDialog.show();
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
                                                task.getResult().update("id", id)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    SmartKitchenUtil.showToast(
                                                                            RegisterActivity.this,
                                                                            "Account created successfully...",
                                                                            1000,
                                                                            SmartKitchenUtil.ToastType.SUCCESS,
                                                                            Gravity.TOP
                                                                    );
                                                                    progressDialog.hide();
                                                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
//                                                                    intent.putExtra("uid",user.getUid());
//                                                                    intent.putExtra("id",id);
                                                                    preference.logUserDetails(user.getUid(), id);
                                                                    preference.setLoginStatus(true);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            }
                                                        });
                                            } else {
                                                progressDialog.hide();
                                                SmartKitchenUtil.showToast(
                                                        RegisterActivity.this,
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
                                    RegisterActivity.this,
                                    "Authentication failed...",
                                    1000,
                                    SmartKitchenUtil.ToastType.ERROR,
                                    Gravity.TOP
                            );

                            progressDialog.hide();

                        }

                        /* make the API call */

                        // [START_EXCLUDE]
//                        progressDialog.hide();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]


    @Override
    protected void onPause() {
        progressDialog.dismiss();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        progressDialog.dismiss();
        super.onDestroy();
    }
}
