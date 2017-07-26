package com.example.admin.vkclub;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.app.ActionBar;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
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
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;


import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.MANAGE_DOCUMENTS;
import static android.R.attr.bitmap;
import static android.R.attr.data;
import static android.R.attr.viewportHeight;
import static android.R.style.Theme_Black_NoTitleBar_Fullscreen;
import static com.example.admin.vkclub.R.id.name;

public class Dashboard extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private static final int PICK_IMAGE_REQUEST = 234;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Button logoutBtn, opendrawer, appmode, mapButton, membershipBtn, mProvider, mContact, mUpdateprofile, mSetting,mNotification;
    private EditText mName, mEmail, mCurrentpass;
    private ImageView userPhoto;
    private TextView userName, userEmail;
    private GoogleApiClient mGoogleApiClient;
    private Location mBestReading;
    private LocationRequest mLocationRequest;
    private Resources mResources;
    StorageReference gsReference;
    FirebaseStorage storage;
    Uri uri;



    private BroadcastReceiver broadcastReceiver;
    String phoneNumber= "+13343758067";
    String message, facebookUserId = "";
    int statusCode;
    double currentLat, currentLon;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        storage = FirebaseStorage.getInstance();

        appmode = (Button) findViewById(R.id.appMode);
        mapButton = (Button) findViewById(R.id.mapBtn);
        membershipBtn = (Button) findViewById(R.id.membershipp);
        mName = (EditText) findViewById(R.id.name1);
        mEmail = (EditText) findViewById(R.id.email1);
        mCurrentpass = (EditText) findViewById(R.id.confirmpass1);
        userPhoto = (ImageView) findViewById(R.id.userphoto);
        userName = (TextView) findViewById(R.id.username);
        userEmail = (TextView) findViewById(R.id.useremail);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        opendrawer = (Button) findViewById(R.id.openDrawer);
        mProvider = (Button) findViewById(R.id.provider);
        logoutBtn = (Button) findViewById(R.id.logout);
        mSetting = (Button) findViewById(R.id.setting);
        mContact = (Button) findViewById(R.id.contact);
        mNotification = (Button) findViewById(R.id.openNotification);

        if (!runtime_permissions())
            start_gps_service();

        // upcoming module
        upComingModule(membershipBtn, "membership");

        // call navigate
        navigateScreen(mapButton, Map.class);

        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        opendrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorStatusBar));
        }

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        Log.d("++++++++++++++TAG+++++++++++++", user.getProviderId());
        if (user != null) {

            // find the Facebook profile and get the user's id
            for (UserInfo profile : user.getProviderData()) {
                Log.v(">>>>>>>>>>>>>>>>>>>>>>>>>", profile.getProviderId());
//                presentDialog("Provider ID", profile.getProviderId());

                // check if the provider id matches "facebook.com"
                if (profile.getProviderId().equals("facebook.com")) {
//                    presentDialog("Provider ID", profile.getProviderId());
                    facebookUserId = profile.getUid();
//                    setProfilePic(user.getPhotoUrl().toString());
//                    Log.d("*******************************",user.getPhotoUrl().toString());
                    setProfilePic("https://graph.facebook.com/" + facebookUserId + "/picture?height=500");
                } else if (profile.getProviderId().equals("password")) {
                    if (user.getPhotoUrl() == null) {

                    } else {
                        setProfilePic(user.getPhotoUrl().toString());
                    }
                }
                userName.setText(user.getDisplayName());
                userEmail.setText(user.getEmail());
                userPhoto.setImageURI(user.getPhotoUrl());
//                presentDialog("yeah",userPhoto.toString());
            }

//  sign in option whether the user login with fb or email
            for (UserInfo profile : user.getProviderData()) {
                // check if the provider id matches "facebook.com"
                if (profile.getProviderId().equals("facebook.com")) {
                    mProvider.setText("FB Linked");
                    mProvider.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getApplicationContext(), "You are current linked your profile with facebook account", Toast.LENGTH_SHORT).show();
                        }
                    });
                    userPhoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            changeprofile();
                        }
                    });
                } else {
                    if (profile.getProviderId().equals("password")) {
                        mProvider.setText("Edit");
                        mProvider.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AlertDialog.Builder mBuilder = new AlertDialog.Builder(Dashboard.this);
                                View mView = getLayoutInflater().inflate(R.layout.edit_info, null);
                                mBuilder.setView(mView);
                                AlertDialog dialog = mBuilder.create();
                                dialog.show();
                            }
                        });
                        userPhoto.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                changeprofile();
                            }
                        });
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

            //Show notification
            mNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog();
                }
            });

            // Logout
            logoutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAuth.getInstance().signOut();
                    LoginManager.getInstance().logOut();
                    Intent intent = new Intent(Dashboard.this, LoginActivity.class);
                    startActivity(intent);
                }
            });

            //setting
            mSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(Dashboard.this);
                    View mView = getLayoutInflater().inflate(R.layout.setting, null);
                    mBuilder.setView(mView);
                    AlertDialog dialog = mBuilder.create();
                    dialog.show();
                }
            });


            //contact
            mContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final List<String> listItems = new ArrayList<String>();
                    listItems.add("Reception(+855 78 777 284)");
                    listItems.add("Reception(+855 96 2222 735)");
                    listItems.add("Cancel");
//                String[] listItems = { "Take Photo", "Select from Photo Library", "Cancel"};


                    //Create sequence of items
                    final CharSequence[] Animals = listItems.toArray(new String[listItems.size()]);
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Dashboard.this);
                    dialogBuilder.setTitle("Contact");
                    dialogBuilder.setItems(Animals, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            String selectedText = Animals[item].toString();  //Selected item in listview
                            if (selectedText.equals("Reception(+855 78 777 284)")) {
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:078777284"));

                                if (ActivityCompat.checkSelfPermission(Dashboard.this,
                                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    return;
                                }
                                startActivity(callIntent);
                            } else if (selectedText.equals("Reception(+855 96 2222 735)")) {
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:0962222735"));

                                if (ActivityCompat.checkSelfPermission(Dashboard.this,
                                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    return;
                                }
                                startActivity(callIntent);
                            }
                        }
                    });
                    //Create alert dialog object via builder
                    AlertDialog alertDialogObject = dialogBuilder.create();
                    //Show the dialog
                    alertDialogObject.show();
                }
            });

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
                                    registerReceiver(new BroadcastReceiver() {
                                        @Override
                                        public void onReceive(Context arg0, Intent arg1) {
                                            switch (getResultCode()) {
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
                                            }
                                        }
                                    }, new IntentFilter(SENT));

                                    //---when the SMS has been delivered---
                                    registerReceiver(new BroadcastReceiver() {
                                        @Override
                                        public void onReceive(Context arg0, Intent arg1) {
                                            switch (getResultCode()) {
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
                                    if (statusCode == 0)
                                        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
                                    else if (statusCode == 1) {
                                        String title = "Off Kirirom Mode";
                                        presentDialog(title, "This function is not accessible outside kirirom area.");
                                    } else if (statusCode == 2) {
                                        String title = "Unidentified";
                                        presentDialog(title, "Location failed. Turn on Location Service to Determine your current location for App Mode: \\n Setting > Location");
                                    } else {
                                        String title = "Error";
                                        presentDialog(title, "Invalid");

                                    }
                                }
                            });
                    // Setting Negative "NO" Button
                    alertDialog.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
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
    }

    private void changeprofile(){
        final List<String> listItems = new ArrayList<String>();
        listItems.add("Take Photo");
        listItems.add("Select from Photo Library");
        listItems.add("Cancel");

        final CharSequence[] Animals = listItems.toArray(new String[listItems.size()]);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Dashboard.this);
        dialogBuilder.setTitle("Upload Profile Picture");
        dialogBuilder.setItems(Animals, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                String selectedText = Animals[item].toString();  //Selected item in listview
                if (selectedText.equals("Take Photo")) {
                    dispatchTakePictureIntent();
                } else if (selectedText.equals("Select from Photo Library")) {
                    getImageFromAlbum();
                }
            }
        });
        //Create alert dialog object via builder
        AlertDialog alertDialogObject = dialogBuilder.create();
        //Show the dialog
        alertDialogObject.show();

    }

    private void setProfilePic(String photoUrl){
        new BitmapFromUrl(userPhoto).execute(photoUrl);
    }

//    get camera
    private void dispatchTakePictureIntent() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    //get image from gallery
    private void getImageFromAlbum(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
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
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                RoundedBitmapDrawable drawable = createRoundedBitmapDrawableWithBorder(bitmap);
                userPhoto.setImageDrawable(drawable);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                RoundedBitmapDrawable drawable = createRoundedBitmapDrawableWithBorder(photo);
                userPhoto.setImageDrawable(drawable);

//                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
//                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
//                File destination = new File(Environment.getExternalStorageDirectory(),
//                        System.currentTimeMillis() + ".jpg");
//                FileOutputStream fo;
//                try {
//                    destination.createNewFile();
//                    fo = new FileOutputStream(destination);
//                    fo.write(bytes.toByteArray());
//                    fo.close();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                userPhoto.setImageBitmap(thumbnail);
            }
        }
        upload();
    }

    //upload user profile photo to firebase
    private void upload() {

        if(userPhoto != null){
            userPhoto.setDrawingCacheEnabled(true);
            userPhoto.buildDrawingCache();
            Bitmap mbitmap = userPhoto.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mbitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] mdata = baos.toByteArray();
            StorageReference reference=storage.getReferenceFromUrl("gs://vkclub-c861b.appspot.com/");
            StorageReference imagesRef=reference.child("userprofile-photo/").child(user.getDisplayName()+"_"+user.getUid());
            UploadTask uploadTask = imagesRef.putBytes(mdata);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Dashboard.this, "Error : "+e.toString(), Toast.LENGTH_SHORT).show();

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(Dashboard.this, "Uploading Done!!!", Toast.LENGTH_SHORT).show();

                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(downloadUri)
                            .build();
                    user.updateProfile(profileUpdate)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        presentDialog("hiii","heeeee");
                                    }
                                }
                            });

//                    userPhoto.setDrawingCacheEnabled(true);
//                    userPhoto.buildDrawingCache();
//                    Bitmap mbitmap = userPhoto.getDrawingCache();
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    mbitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                    byte[] mdata = baos.toByteArray();
//                    StorageReference reference=storage.getReferenceFromUrl("gs://vkclub-c861b.appspot.com/");
//                    StorageReference imagesRef=reference.child("userprofile-photo/").child(user.getDisplayName()+"_"+user.getUid());
//                    UploadTask uploadTask = imagesRef.putBytes(mdata);
//                    uploadTask.addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(Dashboard.this, "Error : "+e.toString(), Toast.LENGTH_SHORT).show();
//                        }
//                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            Toast.makeText(Dashboard.this, "Uploading Done!!!", Toast.LENGTH_SHORT).show();
//
//                            Uri downloadUri = taskSnapshot.getDownloadUrl();
//                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
//                                    .setPhotoUri(downloadUri)
//                                    .build();
//                            user.updateProfile(profileUpdate)
//                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            if (task.isSuccessful()) {
//                                                presentDialog("hiii","heeeee");
//                                            }
//                                        }
//                                    });
//                        }
//                    });
//                    Uri downloadUri = taskSnapshot.getDownloadUrl();
//                    Picasso.with(Dashboard.this).load(downloadUri).into(userPhoto);
                }
            });
        }
    }

    //make user profile photo circular
    private RoundedBitmapDrawable createRoundedBitmapDrawableWithBorder(Bitmap mBitmap) {
        int bitmapWidth = mBitmap.getWidth();
        int bitmapHeight = mBitmap.getHeight();
        int borderWidthHalf = 10;

        int bitmapRadius = Math.min(bitmapWidth,bitmapHeight)/2;

        int bitmapSquareWidth = Math.min(bitmapWidth,bitmapHeight);

        int newBitmapSquareWidth = bitmapSquareWidth+borderWidthHalf;
        Bitmap roundedBitmap = Bitmap.createBitmap(newBitmapSquareWidth,newBitmapSquareWidth,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(roundedBitmap);
        canvas.drawColor(Color.BLACK);

        int x = (borderWidthHalf + bitmapSquareWidth - bitmapWidth);
        int y = (borderWidthHalf + bitmapSquareWidth - bitmapHeight);

        canvas.drawBitmap(mBitmap, x, y, null);

        // Initializing a new Paint instance to draw circular border
        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderWidthHalf);
        borderPaint.setColor(Color.GREEN);

        canvas.drawCircle(canvas.getWidth()/2, canvas.getWidth()/2, newBitmapSquareWidth/2, borderPaint);

        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(mResources,roundedBitmap);
        roundedBitmapDrawable.setCornerRadius(bitmapRadius);

        roundedBitmapDrawable.setAntiAlias(true);

        // Return the RoundedBitmapDrawable
        return roundedBitmapDrawable;
    }

    public void showDialog() {
        Notification newFragment = new Notification();
        FragmentManager fragmentManager = getSupportFragmentManager();
        if ((getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) ==
                Configuration.SCREENLAYOUT_SIZE_LARGE) {
            // The device is using a large layout, so show the fragment as a dialog
            newFragment.show(fragmentManager, "dialog");
        } else {
            // The device is smaller, so show the fragment fullscreen
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            // For a little polish, specify a transition animation
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            // To make it fullscreen, use the 'content' root view as the container
            // for the fragment, which is always the root view for the activity
            transaction.add(R.id.drawerLayout, newFragment)
                    .addToBackStack(null).commit();
        }
    }

    }
