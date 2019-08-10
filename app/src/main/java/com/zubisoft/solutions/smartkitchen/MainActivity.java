package com.zubisoft.solutions.smartkitchen;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zubisoft.solutions.smartkitchen.model.User;
import com.zubisoft.solutions.smartkitchen.util.BlurTransformation;
import com.zubisoft.solutions.smartkitchen.util.SmartKitchenPreference;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HomeFragment.OnFragmentInteractionListener {

    private SmartKitchenPreference preference;
    private ImageView profile_image;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private NavigationView navigationView;
    private TextView txtName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preference = SmartKitchenPreference.getInstance(getApplicationContext());
        if (preference.isFirstTimeUser()) {
            preference.setFirstTimeUser(false);
        }

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        LinearLayout navHeader = navigationView.findViewById(R.id.nav_header);
        profile_image = navigationView.getHeaderView(0).findViewById(R.id.photo);
        txtName = navigationView.getHeaderView(0).findViewById(R.id.display_name);

        switchFragment(new HomeFragment());

//        String uid=getIntent().getStringExtra("uid");
//        String id=getIntent().getStringExtra("id");

        if (preference.isLoggedIn()) {

            HashMap data = preference.getUserDetails();
            String uid = data.get("uid").toString();
            String id = data.get("id").toString();

            if (!uid.equals("")) {
                loadUserDetails(id);
            }

//            SmartKitchenUtil.showToast(
//                    MainActivity.this,
//                    "uid:"+uid+"\nid:"+id,
//                    1000,
//                    SmartKitchenUtil.ToastType.INFO,
//                    Gravity.TOP
//            );
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

    }

    private void loadUserDetails(String id) {
        firestore.collection("user")
                .document(id)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    User user = task.getResult().toObject(User.class);
                    String displayName = user.getUsername();
                    txtName.setText(displayName);

                    String imageUrl = user.getImageUrl();
                    if (!imageUrl.isEmpty()) {
                        Glide.with(MainActivity.this)
                                .load(imageUrl)
                                .crossFade()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(profile_image);

                        ImageView blurrBg = navigationView.getHeaderView(0).findViewById(R.id.bg);

                        Glide.with(MainActivity.this)
                                .load(imageUrl)
                                .crossFade()
                                .transform(new BlurTransformation(getApplicationContext()))
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(blurrBg);
                    }
                } else {

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_saved_recipes) {

        } else if (id == R.id.nav_favs) {

        } else if (id == R.id.nav_category) {
            startActivity(new Intent(this, CategoriesActivity.class));

        } else if (id == R.id.nav_share) {


        } else if (id == R.id.nav_logout) {
            logout();
        } else if (id == R.id.nav_admin) {
            startActivity(new Intent(this, AdminActivity.class));
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setMessage("Do you want to logout?")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        preference.setLoginStatus(false);
                        auth.signOut();
                        LoginManager.getInstance().logOut();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void switchFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
