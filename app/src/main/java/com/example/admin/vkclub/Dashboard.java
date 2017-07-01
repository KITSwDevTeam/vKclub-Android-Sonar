package com.example.admin.vkclub;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.app.ActionBar;
import android.content.Intent;
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
import android.support.v4.app.DialogFragment;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

import static android.app.PendingIntent.getActivity;

public class Dashboard extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Button logoutBtn, opendrawer, appmode;
    private Button mapButton;
    private GoogleApiClient mGoogleApiClient;
    private Location mBestReading;
    private LocationRequest mLocationRequest;

    private LocationManager locationManager;
    private LocationListener locationListener;

//    private Button mDoneButton;

//    private static final long ONE_MIN = 1000 * 60;
//    private static final long TWO_MIN = ONE_MIN * 2;
//    private static final long FIVE_MIN = ONE_MIN * 5;
//    private static final long POLLING_FREQ = 1000 * 30;
//    private static final long FASTEST_UPDATE_FREQ = 1000 * 5;
//    private static final float MIN_ACCURACY = 25.0f;
//    private static final float MIN_LAST_READ_ACCURACY = 500.0f;
//    private final static int REQUEST_RESOLVE_ERROR = 1001;

    TextView weltv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        weltv = (TextView)findViewById(R.id.welcomeMsg);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }
        };

//        if (!servicesAvailable()) {
//            Toast.makeText(Dashboard.this,
//                    "Google Play Service is not available",
//                    Toast.LENGTH_LONG).show();
//        } else {
//            Toast.makeText(Dashboard.this,
//                    "Google Play Service is available",
//                    Toast.LENGTH_LONG).show();
//        }
//
//        mLocationRequest = LocationRequest.create();
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//        mLocationRequest.setInterval(POLLING_FREQ);
//        mLocationRequest.setFastestInterval(FASTEST_UPDATE_FREQ);
//
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(LocationServices.API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();

        // Instantiate ImageButton
        mapButton = (Button) findViewById(R.id.mapBtn);

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
                mAuth.signOut();
                Intent intent = new Intent(Dashboard.this, LoginActivity.class);
                startActivity(intent);
            }
        });


        //Alert button sos
        Button sos = (Button) findViewById(R.id.sos);
        sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Creating alert Dialog with two Buttons

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Dashboard.this);

                // Setting Dialog Title
                alertDialog.setTitle("Please help! ");

                // Setting Dialog Message
                alertDialog.setMessage("I'm currently facing an emergency problem.");

                // Setting Icon to Dialog
//                alertDialog.setIcon(R.drawable.delete);

                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {

                                // Write your code here to execute after dialog
                                Toast.makeText(getApplicationContext(), "You clicked on YES", Toast.LENGTH_SHORT).show();
                            }
                        });
                // Setting Negative "NO" Button
                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,	int which) {
                                // Write your code here to execute after dialog
                                Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        });

                // Showing Alert Message
                alertDialog.show();
            }
        });



        // alert dialog pushing notification

//        mDoneButton = (Button) findViewById(R.id.openNotification);
//        mDoneButton.setOnClickListener((View.OnClickListener) this);

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
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_RESOLVE_ERROR) {
//
//            if (resultCode == RESULT_OK) {
//                // Make sure the app is not already connected or attempting to connect
//                if (!mGoogleApiClient.isConnecting() &&
//                        !mGoogleApiClient.isConnected()) {
//                    mGoogleApiClient.connect();
//                }
//            }
//        }
//    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    private void navigateScreen(Button btn, final Class next) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Dashboard.this, next);
                startActivity(intent);
            }
        });
    }

//    @Override
//    public void onClick(DialogInterface dialogInterface, int i) {
//
//        final CharSequence[] items = {
//                "Rajesh", "Mahesh", "Vijayakumar"
//        };
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Make your selection");
//        builder.setItems(items, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int item) {
//                // Do something with the selection
//                mDoneButton.setText(items[item]);
//            }
//        });
//        AlertDialog alert = builder.create();
//        alert.show();
//    }


//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        // TODO Auto-generated method stub
//        // Get first reading. Get additional location updates if necessary
//        if (servicesAvailable()) {
//
////            new Timer().scheduleAtFixedRate(new TimerTask() {
////                @Override
////                public void run() {
////                    runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////                            // Get best last location measurement meeting criteria
////                            mBestReading = bestLastKnownLocation(MIN_LAST_READ_ACCURACY, FIVE_MIN);
////                            weltv.setText(String.valueOf(mBestReading.getLatitude()));
////                        }
////                    });
////                }
////            }, 0, 7000);
//
//            mBestReading = bestLastKnownLocation(MIN_LAST_READ_ACCURACY, FIVE_MIN);
//            weltv.setText(String.valueOf(mBestReading.getLatitude()));
//
//            if (null == mBestReading
//                    || mBestReading.getAccuracy() > MIN_LAST_READ_ACCURACY
//                    || mBestReading.getTime() < System.currentTimeMillis() - TWO_MIN) {
//
//                weltv.setText("mBestReading is null");
//                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    return;
//                }
//                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (LocationListener) this);
//
//
//                // Schedule a runnable to unregister location listeners
//                Executors.newScheduledThreadPool(1).schedule(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, Dashboard.this);
//                    }
//
//                }, ONE_MIN, TimeUnit.MILLISECONDS);
//            }
//        }
//    }

//    @Override
//    public void onConnectionSuspended(int i) {
//        Toast.makeText(Dashboard.this,
//                "onConnectionSuspended: " + String.valueOf(i),
//                Toast.LENGTH_LONG).show();
//    }

//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        // TODO Auto-generated method stub
//        if (connectionResult.hasResolution()) {
//            try {
//                // Start an Activity that tries to resolve the error
//                connectionResult.startResolutionForResult(this,
//                        REQUEST_RESOLVE_ERROR);
//				/*
//				 * Thrown if Google Play services canceled the original
//				 * PendingIntent
//				 */
//            } catch (IntentSender.SendIntentException e) {
//                // Log the error
//                e.printStackTrace();
//            }
//        } else {
//			/*
//			 * If no resolution is available, display a dialog to the user with
//			 * the error.
//			 */
//            Log.d("Connection Failed:", "" + connectionResult.getErrorCode()
//                    + " " + connectionResult.toString());
//            showErrorDialog(connectionResult.getErrorCode());
//        }
//    }


//    private boolean servicesAvailable() {
//        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
//        if (ConnectionResult.SUCCESS == resultCode) {
//            return true;
//        } else {
//            GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0).show();
//            return false;
//        }
//    }
//
//    private void showErrorDialog(int errorCode) {
//        // Get the error dialog from Google Play services
//        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
//                errorCode,
//                this,
//                REQUEST_RESOLVE_ERROR);
//
//        // If Google Play services can provide an error dialog
//        if (errorDialog != null) {
//            errorDialog.show();
//        }
//    }

//    @Override
//    public void onLocationChanged(Location location) {
//        Toast.makeText(Dashboard.this, "onLocationChanged...", Toast.LENGTH_SHORT).show();
//        // TODO Auto-generated method stub
//        // Determine whether new location is better than current best
//        // estimate
//        if (null == mBestReading || location.getAccuracy() < mBestReading.getAccuracy()) {
//            mBestReading = location;
//
//            if (mBestReading.getAccuracy() < MIN_ACCURACY) {
//                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//            }
//        }
//    }

//    private Location bestLastKnownLocation(float minAccuracy, long minTime) {
//        Location bestResult = null;
//        float bestAccuracy = Float.MAX_VALUE;
//        long bestTime = Long.MIN_VALUE;
//
//        // Get the best most recent location currently available
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return bestResult;
//        }
//        Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//
//        if (mCurrentLocation != null) {
//            float accuracy = mCurrentLocation.getAccuracy();
//            long time = mCurrentLocation.getTime();
//
//            if (accuracy < bestAccuracy) {
//                bestResult = mCurrentLocation;
//                bestAccuracy = accuracy;
//                bestTime = time;
//            }
//        }
//
//        // Return best reading or null
//        if (bestAccuracy > minAccuracy || bestTime < minTime) {
//            return null;
//        }
//        else {
//            return bestResult;
//        }
//    }
}
