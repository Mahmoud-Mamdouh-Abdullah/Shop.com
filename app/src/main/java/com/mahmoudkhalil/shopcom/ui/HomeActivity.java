package com.mahmoudkhalil.shopcom.ui;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.mahmoudkhalil.shopcom.R;
import com.mahmoudkhalil.shopcom.models.User;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    private TextView name_tv, email_tv, profile_tv;
    private CircleImageView profileCircleImageView;
    private Gson gson;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    User user;

    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        String fromWhere = getIntent().getStringExtra("from");
        init();
        String login_user = sharedPreferences.getString("login_user", "null");
        if (login_user != null) {
            user = gson.fromJson(login_user, User.class);
            name_tv.setText(user.getFullName().split(" ")[0]);
            email_tv.setText(user.getEmail());
            if(user.getPhotoUri() != null) {
                profile_tv.setVisibility(View.GONE);
                Picasso.get().load(user.getPhotoUri()).centerCrop().fit().into(profileCircleImageView);
            } else {
                profile_tv.setText(get2FromName(user.getFullName()));
                profileCircleImageView.setVisibility(View.GONE);
            }
        }
        setSupportActionBar(toolbar);

        navView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        if(fromWhere.equals("order")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new OrdersFragment()).commit();
            navView.setCheckedItem(R.id.nav_orders);
            getSupportActionBar().setTitle("My Orders");
        }
        else if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
            navView.setCheckedItem(R.id.nav_home);
            getSupportActionBar().setTitle("All Categories");
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            final Dialog dialog = new Dialog(HomeActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.exit_dialog);
            Button ok = dialog.findViewById(R.id.ok);
            Button cancel = dialog.findViewById(R.id.cancel);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeActivity.this.finish();
                    mGoogleSignInClient.signOut();
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
                getSupportActionBar().setTitle("All Categories");
                break;
            case R.id.nav_cart:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new CartFragment()).commit();
                getSupportActionBar().setTitle("My Cart");
                break;
            case R.id.nav_favourite:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FavouriteFragment()).commit();
                getSupportActionBar().setTitle("My Favourite");
                break;
            case R.id.nav_orders:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new OrdersFragment()).commit();
                getSupportActionBar().setTitle("My Orders");
                break;
            case R.id.nav_logout:
                mGoogleSignInClient.signOut();
                editor.putBoolean("remember_user", false);
                editor.apply();
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private String get2FromName(String name) {
        String[] nameWords = name.split(" ");
        return nameWords[0].charAt(0) + String.valueOf(nameWords[nameWords.length - 1].charAt(0));
    }

    private void init(){
        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        name_tv = navView.getHeaderView(0).findViewById(R.id.full_name);
        email_tv = navView.getHeaderView(0).findViewById(R.id.email);
        profile_tv = navView.getHeaderView(0).findViewById(R.id.profile_text);
        profileCircleImageView = navView.getHeaderView(0).findViewById(R.id.profile_imageView);
        gson = new Gson();
    }
}