package com.example.admin.vkclub;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.app.ActionBar;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.DecimalFormat;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.style.TtsSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Dash;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;


import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.MANAGE_DOCUMENTS;
import static android.R.attr.bitmap;
import static android.R.attr.viewportHeight;
import static android.R.style.Theme_Black_NoTitleBar_Fullscreen;

public class Dashboard extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int RESULT_LOAD_IMG = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Button logoutBtn, opendrawer, appmode, mapButton, membershipBtn, mProvider, mSetting;
    private ImageView userPhoto;
    private TextView userName, userEmail;
    private ListView mList;
    private GoogleApiClient mGoogleApiClient;
    private Location mBestReading;
    private LocationRequest mLocationRequest;
    private Bitmap mBitmap;
    private Resources mResources;

    private BroadcastReceiver broadcastReceiver;
    String phoneNumber= "+855962304669";
    String message, facebookUserId = "";
    int statusCode;
    double currentLat, currentLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        appmode = (Button) findViewById(R.id.appMode);
        mapButton = (Button) findViewById(R.id.mapBtn);
        membershipBtn = (Button) findViewById(R.id.membershipp);

        if(!runtime_permissions())
            start_gps_service();

        // upcoming module
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
        final FirebaseUser user = mAuth.getCurrentUser();
        Log.d("++++++++++++++TAG+++++++++++++", user.getProviderId());
        if(user != null){
            userPhoto = (ImageView)findViewById(R.id.userprofile);
            userName = (TextView)findViewById(R.id.username);
            userEmail = (TextView)findViewById(R.id.useremail);

            // find the Facebook profile and get the user's id
            for(UserInfo profile : user.getProviderData()) {
                Log.v(">>>>>>>>>>>>>>>>>>>>>>>>>", profile.getProviderId());
                presentDialog("Provider ID", profile.getProviderId());

                // check if the provider id matches "facebook.com"
                if(profile.getProviderId().equals("facebook.com")) {
                    presentDialog("Provider ID", profile.getProviderId());
                    facebookUserId = profile.getUid();

                    setProfilePic("https://graph.facebook.com/" + facebookUserId + "/picture?height=500");
                } else if(profile.getProviderId().equals("password")){
                    // if profile pic exist
                    // setProfilePic("PhotoUrl from firebase");

                }
            }


            userName.setText(user.getDisplayName());
            userEmail.setText(user.getEmail());
        }


        userPhoto = (ImageView)findViewById(R.id.userprofile);
        mProvider = (Button) findViewById(R.id.provider);
//        //show in button whether the user sign in with email or facebook
//        for(UserInfo profile : user.getProviderData()) {
//            // check if the provider id matches "facebook.com"
//            if(profile.getProviderId().equals("facebook.com")) {
//                mProvider.setText("FB Linked");
//            } else if(profile.getProviderId().equals("password")){
//                mProvider.setText("Edit");
//            }
//        }


        for(UserInfo profile : user.getProviderData()) {
            // check if the provider id matches "facebook.com"
            if(profile.getProviderId().equals("facebook.com")) {
                mProvider.setText("FB Linked");
                mProvider.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(), "You are current linked your profile with facebook account", Toast.LENGTH_SHORT).show();

                    }
                });

            } else {
                if (profile.getProviderId().equals("password")) {
                    mProvider.setText("Edit");
                    mProvider.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //dialog full screen
//                            Dialog dialog=new Dialog(Dashboard.this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
//                            dialog.setContentView(R.layout.activity_create_account);
//                            dialog.show();
                        }
                    });

                    selectprofile();

                }
            }
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


        //
        mSetting = (Button)findViewById(R.id.setting);


        //Alert button sos
        Button sos = (Button) findViewById(R.id.sos);
        sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Creating alert Dialog with two Buttons

                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Dashboard.this);

                // Setting Dialog Title
                alertDialog.setTitle("Please help! ");

                // Setting Dialog Message
                alertDialog.setMessage("I'm currently facing an emergency problem.");

                // Setting Icon to Dialog
//                alertDialog.setIcon(R.drawable.delete);

                // Setting Positive "Yes" Button
                AlertDialog.Builder confirm = alertDialog.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                // Write your code here to execute after dialog
//                                Toast.makeText(getApplicationContext(), "You clicked on Confirm", Toast.LENGTH_SHORT).show();





                                // Emergency sos sending sms


                                String SENT = "SMS_SENT";
                                String DELIVERED = "SMS_DELIVERED";

                                PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0,
                                        new Intent(SENT), 0);

                                PendingIntent deliveredPI = PendingIntent.getBroadcast(getApplicationContext(), 0,
                                        new Intent(DELIVERED), 0);

                                //---when the SMS has been sent---
                                registerReceiver(new BroadcastReceiver(){
                                    @Override
                                    public void onReceive(Context arg0, Intent arg1) {
                                        switch (getResultCode())
                                        {
                                            case Activity.RESULT_OK:
                                                Toast.makeText(getBaseContext(), "SMS sent",
                                                        Toast.LENGTH_SHORT).show();
                                                break;
                                            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                                Toast.makeText(getBaseContext(), "Generic failure",
                                                        Toast.LENGTH_SHORT).show();
                                                break;
                                            case SmsManager.RESULT_ERROR_NO_SERVICE:
                                                Toast.makeText(getBaseContext(), "No service",
                                                        Toast.LENGTH_SHORT).show();
                                                break;
                                            case SmsManager.RESULT_ERROR_NULL_PDU:
                                                Toast.makeText(getBaseContext(), "Null PDU",
                                                        Toast.LENGTH_SHORT).show();
                                                break;
                                            case SmsManager.RESULT_ERROR_RADIO_OFF:
                                                Toast.makeText(getBaseContext(), "Radio off",
                                                        Toast.LENGTH_SHORT).show();
                                                break;
                                        }
                                    }
                                }, new IntentFilter(SENT));

                                //---when the SMS has been delivered---
                                registerReceiver(new BroadcastReceiver(){
                                    @Override
                                    public void onReceive(Context arg0, Intent arg1) {
                                        switch (getResultCode())
                                        {
                                            case Activity.RESULT_OK:
                                                Toast.makeText(getBaseContext(), "SMS delivered",
                                                        Toast.LENGTH_SHORT).show();
                                                break;
                                            case Activity.RESULT_CANCELED:
                                                Toast.makeText(getBaseContext(), "SMS not delivered",
                                                        Toast.LENGTH_SHORT).show();
                                                break;
                                        }
                                    }
                                }, new IntentFilter(DELIVERED));

                                SmsManager sms = SmsManager.getDefault();
                                if(statusCode == 0)
                                    sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
                                else if(statusCode == 1)
                                {
                                    String title = "Off Kirirom Mode";
                                    presentDialog(title, "This function is not accessible outside kirirom area.");
                                }
                                else if(statusCode == 2)
                                {
                                    String title = "Unidentified";
                                    presentDialog(title, "Location failed. Turn on Location Service to Determine your current location for App Mode: \\n Setting > Location");
                                }
                                else{
                                    String title = "Error";
                                    presentDialog(title, "Invalid");

                                }
                                //alert.....

                            }
                        });
                // Setting Negative "NO" Button
                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,	int which) {
                                // Write your code here to execute after dialog
                                Toast.makeText(getApplicationContext(), "You clicked on Cancel", Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        });

                // Showing Alert Message
                alertDialog.show();
            }
        });
    }

    private void selectprofile() {

        // getting image of the user from gallery

        userPhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Intent myIntent = new Intent(view.getContext(), agones.class);
                //startActivityForResult(myIntent, 0);

                final List<String> listItems = new ArrayList<String>();
                listItems.add("Take Photo");
                listItems.add("Select from Photo Library");
                listItems.add("Cancel");
//                String[] listItems = { "Take Photo", "Select from Photo Library", "Cancel"};


                //Create sequence of items
                final CharSequence[] Animals = listItems.toArray(new String[listItems.size()]);
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Dashboard.this);
                dialogBuilder.setTitle("Upload Profile Picture");
                dialogBuilder.setItems(Animals, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        String selectedText = Animals[item].toString();  //Selected item in listview
                        if(selectedText.equals("Take Photo"))
                        {
                            dispatchTakePictureIntent();
//                            presentDialog("Choose Camera", "Success");
                        }
                        else if(selectedText.equals("Select from Photo Library")){
                            getImageFromAlbum();
                        }
                    }
                });
                //Create alert dialog object via builder
                AlertDialog alertDialogObject = dialogBuilder.create();
                //Show the dialog
                alertDialogObject.show();



            }
        });
    }

    private void setProfilePic(String photoUrl){
        new BitmapFromUrl(userPhoto).execute(photoUrl);
    }

    //get image from gallery
    private void getImageFromAlbum(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);

    }

    //get camera
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void upComingModule(Button btn, final String identifier){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = "Coming Soon!";
                switch (identifier){
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
            appmode.setText("IN-Kirirom Mode " + distance);
            this.statusCode = 0;
        } else if(distance >= 17){
            appmode.setText("OFF-Kirirom Mode " + distance);
            this.statusCode = 1;
        } else {
            appmode.setText("Unidentified " + distance);
            this.statusCode = 2;
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

                    currentLat = Double.parseDouble(intent.getExtras().get("latitude").toString());
                    currentLon = Double.parseDouble(intent.getExtras().get("longitude").toString());
                    message = "Please help! I'm currently facing an emergency problem. Here is my Location: http://maps.google.com/?q=" + currentLat + "," + currentLon;
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

    private void navigateScreen(Button btn, final Class next) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presentDialog("onActivity Result", "Hello"+String.valueOf(resultCode)+String.valueOf(RESULT_OK));
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                userPhoto.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(Dashboard.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
    }
}