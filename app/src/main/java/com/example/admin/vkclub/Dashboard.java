package com.example.admin.vkclub;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.app.ActionBar;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.DecimalFormat;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.TtsSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;


import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.MANAGE_DOCUMENTS;

public class Dashboard extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Button logoutBtn, opendrawer, appmode;
    private ImageButton mapButton, membershipBtn, mediaBtn;
    private GoogleApiClient mGoogleApiClient;
    private Location mBestReading;
    private LocationRequest mLocationRequest;

    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        appmode = (Button) findViewById(R.id.appMode);

        // Instantiate ImageButton
        mapButton = (ImageButton) findViewById(R.id.mapBtn);
        membershipBtn = (ImageButton)findViewById(R.id.membership);
        mediaBtn = (ImageButton)findViewById(R.id.media);

        if(!runtime_permissions())
            start_gps_service();

        // upcoming module
        upComingModule(mediaBtn, "media");
        upComingModule(membershipBtn, "membership");

        // call navigate
        navigateScreen(mapButton, Map.class);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        opendrawer = (Button) findViewById(R.id.openDrawer);

        opendrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        if(Build.VERSION.SDK_INT >= 21){
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorStatusBar));
        }

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            String name = user.getDisplayName();
            presentDialog("Hello World", name);
        }
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                } else {
                    // User is signed out
                }
            }
        };

        logoutBtn = (Button) findViewById(R.id.logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(Dashboard.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void upComingModule(ImageButton btn, final String identifier){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = "Coming Soon!";
                switch (identifier){
                    case "media":
                        presentDialog(title, "Introducing vKirirom Media will be available soon.");
                        break;
                    case "membership":
                        presentDialog(title, "Introducing vKirirom membership card with vKpoint will be available soon.");
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void start_gps_service(){
        Intent i = new Intent(getApplicationContext(), GPS_Service.class);
        startService(i);
    }

    private boolean runtime_permissions() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return true;
        }
        return false;
    }

    private void calcDistance(double currentLat, double currentLon){
        //Earth Ray
        double R = 6371;

        //Get latlong value diferences between two points
        double dLat = (currentLat - 11.317655) * Math.PI / 180;
        double dLon = (currentLon - 104.064933) * Math.PI / 180;

        //Calculate distance with Haversine Formula
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(11.317655 * Math.PI / 180)
                * Math.cos(currentLat * Math.PI / 180) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance  = R * c;

        if(distance < 17){
            appmode.setText("IN-Kirirom Mode");
        } else if(distance >= 17){
            appmode.setText("OFF-Kirirom Mode");
        } else {
            appmode.setText("Unidentified");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                // READY FOR LOCATION TRACKING
                start_gps_service();

            }else {
                // Request Runtime Permission again
                runtime_permissions();
            }
        }
    }

    @Override
    protected void onStart() {
        mAuth.addAuthStateListener(mAuthListener);
        // Connect the client.
//        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (mGoogleApiClient != null) {
//            mGoogleApiClient.connect();
//        }

        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //appmode.setText(""+intent.getExtras().get("latitude"));
//                    Location centreLocation = new Location("center_location");
//                    Location currentLocation = new Location("current_location");
//                    centreLocation.setLatitude(11.317655);
//                    centreLocation.setLongitude(104.064933);
//                    currentLocation.setLatitude((Double) intent.getExtras().get("latitude"));
//                    currentLocation.setLongitude((Double) intent.getExtras().get("longitude"));
//
//                    double distance = centreLocation.distanceTo(currentLocation);
//                    makeToast("++++++++++++++++============ " + distance);

                    double currentLat = Double.parseDouble(intent.getExtras().get("latitude").toString());
                    double currentLon = Double.parseDouble(intent.getExtras().get("longitude").toString());
                    calcDistance(currentLat, currentLon);
                }
            };
        }

        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    private void navigateScreen(ImageButton btn, final Class next) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this, next);
                startActivity(intent);
            }
        });
    }

    private void makeToast(String text){
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
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
}
