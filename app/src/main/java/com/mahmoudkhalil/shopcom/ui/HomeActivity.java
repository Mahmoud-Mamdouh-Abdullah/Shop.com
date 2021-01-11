package com.mahmoudkhalil.shopcom.ui;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.mahmoudkhalil.shopcom.R;
import com.mahmoudkhalil.shopcom.databinding.ActivityHomeBinding;
import com.mahmoudkhalil.shopcom.models.User;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActivityHomeBinding binding;
    private TextView name_tv, email_tv, profile_tv;
    private Gson gson;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        name_tv = (TextView) binding.navView.getHeaderView(0).findViewById(R.id.full_name);
        email_tv = (TextView) binding.navView.getHeaderView(0).findViewById(R.id.email);
        profile_tv = (TextView) binding.navView.getHeaderView(0).findViewById(R.id.profile_text);
        gson = new Gson();
        String login_user = sharedPreferences.getString("login_user", "null");
        if(login_user != null) {
            user = gson.fromJson(login_user,User.class);
            name_tv.setText(user.getFullName());
            email_tv.setText(user.getEmail());
            profile_tv.setText(get2FromName(user.getFullName()));
        }
        setSupportActionBar(binding.toolbar);

        binding.navView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,binding.drawerLayout,binding.toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();



        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
            binding.navView.setCheckedItem(R.id.nav_home);
            getSupportActionBar().setTitle("All Categories");
        }


    }

    @Override
    public void onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START);
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
        switch (menuItem.getItemId()){
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
                editor.putBoolean("remember_user",false);
                editor.apply();
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private String get2FromName(String name) {
        String [] nameWords = name.split(" ");
        return nameWords[0].charAt(0) + String.valueOf(nameWords[nameWords.length - 1].charAt(0));
    }
}