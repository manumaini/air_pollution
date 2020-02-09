package com.example.air_pollution;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        imageView = header.findViewById(R.id.nav_image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new dashboard_fragment()).commit();
                drawer.closeDrawer(GravityCompat.START);

            }
        });


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.syncState();

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new dashboard_fragment()).commit();

        }
    }

    @Override
    public void onBackPressed() {

        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.sanalysis:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new sensor_analyticsFragment()).commit();
                navigationView.setCheckedItem(R.id.sanalysis);
                break;
            case R.id.hdanalysis:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new historic_dataFragment()).commit();
                navigationView.setCheckedItem(R.id.hdanalysis);
                break;
            case R.id.sconfig:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new sensor_configurationFragment()).commit();
                navigationView.setCheckedItem(R.id.sconfig);
                break;
            case R.id.salert:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new sensor_alertFragment()).commit();
                navigationView.setCheckedItem(R.id.salert);
                break;
            case R.id.asetting:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new alert_settingFragment()).commit();
                navigationView.setCheckedItem(R.id.asetting);
                break;

            case R.id.log_out:
                SharedPreferences sharedPreferences = getSharedPreferences("login_data",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("token");
                Intent intent = new Intent(this,loginActivity.class);
                startActivity(intent);


        }
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }
}
