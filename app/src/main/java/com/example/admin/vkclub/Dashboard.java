package com.example.admin.vkclub;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Matrix;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.sip.SipSession;
import android.os.SystemClock;
import android.support.v4.app.DialogFragment;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.app.ActionBar;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
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
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.os.Build;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.content.Loader;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.style.TtsSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

public class Dashboard extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private static final int PICK_IMAGE_REQUEST = 234;
    private static final int REQUEST_CROP_ICON = 168;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Button voipBtn, setting, openNotification, aboutUs,mService;
    private Button logoutBtn, opendrawer, appmode, mapButton, membershipBtn, mProvider, mContact, mUpdateprofile, mSetting;
    private EditText mName, mEmail, mCurrentpass;
    private View uploading, uploadDone;
    private ImageView userPhoto;
    private TextView userName, userEmail, spinningStatus;
    public static TextView msg;
    private GoogleApiClient mGoogleApiClient;
    private Location mBestReading;
    private LocationRequest mLocationRequest;
    private Resources mResources;
    StorageReference gsReference;
    FirebaseStorage storage;
    Uri uri;
    DbBitmapUtility dbBitmapUtility;


    public Voip voipClient;

    private BroadcastReceiver broadcastReceiver;
//    String phoneNumber= "+13343758067";
    String phoneNumber= "+855966619121";
    String message, facebookUserId = "";
    int statusCode;
    double currentLat, currentLon;
    FirebaseUser user;

    public SipManager mSipManager = null;
    public SipProfile mSipProfile = null;
    public SipAudioCall audioCall = null;
    public String callAddress = "";
    public String sipUsername;
    public String sipDomain;
    public String sipPassword;
    public int sipPort = 5060;
    public IncomingCallReceiver callReceiver;
    public static final int CALL_ADDRESS = 1;
    public static final int SET_AUTH_INFO = 2;
    public static final int UPDATE_SETTINGS_DIALOG = 3;
    public static final int HANG_UP = 4;
    public Context context;
    public DialogFragment newFragment;
    public static boolean uploadStatus;

    private static Context returnContext;
    public static int reg_status = 0;
    public static boolean sipPermission;
    DataBaseHelper mDataBaseHelper;

    SharedPreferences prefs;
    String imageBlob;

    public static Activity dashboardActivity;
//    Animation anim = AnimationUtils.loadAnimation(this, R.anim.slide_down);

    private static final int LOCATION_PERMISSION_GRANTED = 123;
    private static final int LOCATION_PERMISSION_NOT_GRANTED = 321;
    private static final int SIP_PERMISSION_GRANTED = 789;
    private static final int SIP_PERMISSION_NOT_GRANTED = 987;
    private static final int ALL_PERMISSION_GRANTED = 99999;
    SharedPreferences preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Dashboard.returnContext = this;
        Dashboard.dashboardActivity = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        dbBitmapUtility = new DbBitmapUtility();

        storage = FirebaseStorage.getInstance();

        appmode = (Button) findViewById(R.id.appMode);
        mapButton = (Button) findViewById(R.id.mapBtn);
        membershipBtn = (Button) findViewById(R.id.membership);
        mName = (EditText) findViewById(R.id.nameprofile);
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
        membershipBtn = (Button) findViewById(R.id.membership);
        voipBtn = (Button) findViewById(R.id.voip);
        openNotification = (Button) findViewById(R.id.openNotification);
        aboutUs = (Button) findViewById(R.id.about_us);
        mService = (Button) findViewById(R.id.serviceBtn);
        msg = (TextView) findViewById(R.id.welcomeMsg);
        spinningStatus = (TextView) findViewById(R.id.spinning_status);
        uploading = findViewById(R.id.uploading_spinner);
        uploadDone = findViewById(R.id.upload_done);
        uploading.setVisibility(View.GONE);
        uploadDone.setVisibility(View.GONE);

        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorStatusBar));
        }

//        runtime_permissions();
//        initializeManager();
//        start_gps_service();

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

        opendrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        openNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotificationPanel();
            }
        });

//        setting = (Button)findViewById(R.id.setting);
//        setting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getFragmentManager().beginTransaction().replace(android.R.id.content, new SipSettings()).commit();
//            }
//        });

        // upcoming module
        upComingModule(membershipBtn, "membership");

        // call navigate
        navigateScreen(mapButton, Map.class);
        navigateScreen(aboutUs, About.class);
        navigateScreen(mService, Services.class);

        voipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getAppContext(), Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getAppContext(), Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Dashboard.dashboardActivity, new String[]{
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.RECORD_AUDIO
                    }, 300);
                } else {
                    Intent in = new Intent(getAppContext(), Voip.class);
                    startActivity(in);
                }
            }
        });

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
//        Log.d("++++++++++++++TAG+++++++++++++", user.getProviderId());

        if (user != null) {
            // find the Facebook profile and get the user's id
            for (UserInfo profile : user.getProviderData()) {
                Log.v(">>>>>>>>>>>>>>>>>>>>>>>>>", profile.getProviderId());

                // check if the provider id matches "facebook.com"
                if (profile.getProviderId().equals("facebook.com")) {
                    String imageBlob = prefs.getString("get_blob", "");
                    mProvider.setText("FB Linked");
                    facebookUserId = profile.getUid();
                    if (imageBlob.length() == 0) {
                        // sometime image loading is freeze so set loading animation for better user experiences
                        userPhoto.setImageAlpha(0);
                        uploading.setVisibility(View.VISIBLE);
                        spinningStatus.setText("Loading...");

                        setProfilePic("https://graph.facebook.com/" + facebookUserId + "/picture?height=500");
                    } else {
                        DbBitmapUtility dbBitmapUtility = new DbBitmapUtility();
                        byte[] imageAsBytes = dbBitmapUtility.getBytesFromString(imageBlob);
                        Bitmap resultBitmap = dbBitmapUtility.getImage(imageAsBytes);
                        RoundedBitmapDrawable drawable = createRoundedBitmapDrawableWithBorder(resultBitmap);
                        userPhoto.setImageDrawable(drawable);
                    }
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
                } else if (profile.getProviderId().equals("password")) {
                    String imageBlob = prefs.getString("get_blob", "");
                    mProvider.setText("Edit");
                    if (user.getPhotoUrl() != null) {
                        if (imageBlob.length() == 0) {
                            // sometime image loading is freeze so set loading animation for better user experiences
                            userPhoto.setImageAlpha(0);
                            uploading.setVisibility(View.VISIBLE);
                            spinningStatus.setText("Loading...");

                            setProfilePic(user.getPhotoUrl().toString());
                        } else {
                            DbBitmapUtility dbBitmapUtility = new DbBitmapUtility();
                            byte[] imageAsBytes = dbBitmapUtility.getBytesFromString(imageBlob);
                            Bitmap resultBitmap = dbBitmapUtility.getImage(imageAsBytes);
                            RoundedBitmapDrawable drawable = createRoundedBitmapDrawableWithBorder(resultBitmap);
                            userPhoto.setImageDrawable(drawable);
                        }
                    }
                    mProvider.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showDialog();
                            mDrawerLayout.closeDrawer(Gravity.LEFT);
                        }
                    });
                    userPhoto.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            changeprofile();
                        }
                    });
                }

                //Get from firebase
                userName.setText(user.getDisplayName());
                userEmail.setText(user.getEmail());
//                userPhoto.setImageURI(user.getPhotoUrl());


                logoutBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getAppContext());
                        builder.setTitle("Are you sure ?");
                        builder.setMessage("Logout Vkclub from this device.");
                        builder.setCancelable(true);

                        builder.setPositiveButton(
                                "Logout",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        mAuth.getInstance().signOut();
                                        LoginManager.getInstance().logOut();
                                        Intent intent = new Intent(Dashboard.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                });

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                });

                //setting
                mSetting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(Dashboard.this);
//                    View mView = getLayoutInflater().inflate(R.layout.setting, null);
//                    mBuilder.setView(mView);
//                    AlertDialog dialog = mBuilder.create();
//                    dialog.show();
                        Intent in = new Intent(getAppContext(), Setting.class);
                        startActivity(in);
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

                        //Create sequence of items
                        final CharSequence[] Animals = listItems.toArray(new String[listItems.size()]);
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Dashboard.this);
                        dialogBuilder.setTitle("Contact");
                        dialogBuilder.setItems(Animals, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                String selectedText = Animals[item].toString();  //Selected item in listview
                                if (selectedText.equals("Reception(+855 78 777 284)")) {
                                    if (((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getPhoneType()
                                            == TelephonyManager.PHONE_TYPE_NONE) {
                                        presentDialog("NO PHONE", "ARE YOU OK?");
                                    } else {
                                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                                        callIntent.setData(Uri.parse("tel:0966619121"));
                                        if (ActivityCompat.checkSelfPermission(Dashboard.this,
                                                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                            return;
                                        }
                                        startActivity(callIntent);
                                    }
                                } else if (selectedText.equals("Reception(+855 96 2222 735)")) {
                                    if (((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getPhoneType()
                                            == TelephonyManager.PHONE_TYPE_NONE) {
                                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                                        callIntent.setData(Uri.parse("tel:0966619121"));

                                        if (ActivityCompat.checkSelfPermission(Dashboard.this,
                                                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                            return;
                                        }
                                        startActivity(callIntent);
                                    } else {
                                        presentDialog("NO PHONE", "ARE YOU OK?");
                                    }
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
                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Dashboard.this);
                        alertDialog.setTitle("Please help! ");
                        alertDialog.setMessage("I'm currently facing an emergency problem.");
                        AlertDialog.Builder confirm = alertDialog.setPositiveButton("Confirm",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
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
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.Vkclub.INCOMING_CALL");
            callReceiver = new IncomingCallReceiver();
            this.registerReceiver(callReceiver, filter);
        }
    }

    public void showDialog() {
        EditProfile newFragment = new EditProfile();
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

    public static Context getAppContext(){
        return Dashboard.returnContext;
    }

    public void initializeManager(){
        System.out.println("INITIALIZE MANAGER");
        if(mSipManager == null){
            mSipManager = SipManager.newInstance(this);
        }
        initializeLocalProfile();
    }
    /**
     * Logs you into your SIP provider, registering this device as the location to
     * send SIP calls to for your SIP address.
     */
    public void initializeLocalProfile() {

        if (mSipManager == null) {
            System.out.println("mSipManager == null");
            return;
        }

        if (mSipProfile != null) {
            System.out.println("mSipProfile != null");
            closeLocalProfile();
        }

//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//        sipUsername = prefs.getString("namePref", "");
//        sipDomain = prefs.getString("domainPref", "");
//        sipPassword = prefs.getString("passPref", "");
//
//        if (sipUsername.length() == 0 || sipDomain.length() == 0 || sipPassword.length() == 0) {
//            getFragmentManager().beginTransaction().replace(android.R.id.content, new SipSettings()).commit();
//            return;
//        }

        try {
            SipProfile.Builder builder = new SipProfile.Builder("10018", "192.168.7.251");
            builder.setPassword("A2apbx10018");
            builder.setPort(sipPort);
            builder.setProtocol("UDP");
            builder.setAutoRegistration(true);
            builder.setSendKeepAlive(true);
            mSipProfile = builder.build();


            if (Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
                Intent i = new Intent();
                i.setAction("android.Vkclub.INCOMING_CALL");
                PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, Intent.FILL_IN_DATA);
                mSipManager.open(mSipProfile, pi, null);

                // This listener must be added AFTER manager.open is called,
                // Otherwise the methods aren't guaranteed to fire.

                mSipManager.setRegistrationListener(mSipProfile.getUriString(), new SipRegistrationListener() {
                    @Override
                    public void onRegistering(String s) {
                        msg.setText("Registering with SIP server...");
                        System.out.println("1.SET Registering with SIP Server...");
                    }

                    @Override
                    public void onRegistrationDone(String s, long l) {
                        msg.setText("Register with SIP server success.");
                        System.out.println("1.SET Ready");
                        Dashboard.reg_status = 1;
                    }

                    @Override
                    public void onRegistrationFailed(String s, int i, String s1) {
                        msg.setText("Registration failed.");
                        System.out.println("1.SET Registration failed.");
                        Dashboard.reg_status = 2;
                    }
                });

                mSipManager.register(mSipProfile, 240, new SipRegistrationListener() {
                    @Override
                    public void onRegistering(String s) {
//                        msg.startAnimation(anim);
                        msg.setText("Registering with SIP server...");
                        Log.d("1.Registering with SIP Server...", "");
                    }

                    @Override
                    public void onRegistrationDone(String s, long l) {
//                        msg.setAnimation(anim);
                        msg.setText("Register with SIP server success.");
                        Log.d("1.Ready", "");
                        Dashboard.reg_status = 1;
                    }

                    @Override
                    public void onRegistrationFailed(String s, int i, String s1) {
//                        msg.setAnimation(anim);
                        msg.setText("Registration failed.");
                        Log.d("1.Register with SIP server failed.", "");
                        Dashboard.reg_status = 2;
                    }
                });
                Dashboard.sipPermission = true;
            }

        } catch (ParseException pe) {
            msg.setText("ParseException thrown");
            Dashboard.reg_status = 2;
            Log.d("ParseException", pe.toString());
        } catch (SipException se){
            msg.setText("SipException thrown");
            Dashboard.reg_status = 2;
            Log.d("SipException hi", se.toString());
        }

        System.out.println("INITIALIZE LOCAL PROFILE");

    }

    /**
     * Closes out your local profile, freeing associated objects into memory
     * and unregistering your device from the server.
     */
    public void closeLocalProfile() {
        if (mSipManager == null) {
            return;
        }
        try {
            if (mSipProfile != null) {
                if (Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
                    mSipManager.close(mSipProfile.getUriString());
                }
            }
        } catch (Exception ee) {
            Log.d("/onDestroy", "Failed to close local profile.", ee);
        }
        System.out.println("CLOSE LOCAL PROFILE");
    }

    /**
     * Make an outgoing call.
     */
    public void initiateCall(final String callee) {
        final Timer T = new Timer();
        final Calling calling = (Calling) Calling.getAppContext();
        try {
            SipAudioCall.Listener listener = new SipAudioCall.Listener() {
                // Much of the client's interaction with the SIP Stack will
                // happen via listeners.  Even making an outgoing call, don't
                // forget to set up a listener to set things up once the call is established.
                @Override
                public void onCallEstablished(SipAudioCall call) {
                    call.startAudio();
                    call.setSpeakerMode(false);
                    if(audioCall.isMuted()) {
                        audioCall.toggleMute();
                    }
                    System.out.println("call established "+ callee + "Dashboard");
                    System.out.println("IS IN CALL DASHBOARD "+call.isInCall());
                    checkStatus(call.getState());

                    if (call.isInCall()){
                        T.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                Calling.getInstance().updateCallDuation();
                                System.out.println("Timer Task......");
                            }
                        }, 0, 1000);
                    }
                }

                @Override
                public void onCallEnded(SipAudioCall call) {
                    System.out.println("Call ended ========================= Dashboard");
                    T.cancel();
                    System.out.println("Timer cancel....");
                    Activity callingActivity = Calling.getActivity();
                    callingActivity.finish();
                }

                @Override
                public void onRinging(SipAudioCall call, SipProfile caller) {
                    super.onRinging(call, caller);
                    System.out.println("ON RINGING");
                }

                @Override
                public void onRingingBack(SipAudioCall call) {
                    super.onRingingBack(call);
                    System.out.println("ON RINGING BACK");
                }

                @Override
                public void onReadyToCall(SipAudioCall call) {
                    super.onReadyToCall(call);
                    System.out.println("ON READY TO CALL");
                }

                @Override
                public void onError(SipAudioCall call, int errorCode, String errorMessage) {
                    super.onError(call, errorCode, errorMessage);
                    System.out.println("ON ERROR");
                }

                @Override
                public void onChanged(SipAudioCall call) {
                    super.onChanged(call);
                    System.out.println("ON CHANGED");
                }

                @Override
                public void onCallBusy(SipAudioCall call) {
                    super.onCallBusy(call);
                    System.out.println("ON CALL BUSY");
                    Activity callingActivity = Calling.getActivity();
                    callingActivity.finish();
                    calling.player.stop();
                }
            };

            callAddress = "sip:"+callee+"@"+"192.168.7.251";
            System.out.println("calling " + callee + "========================= Dashboard  " + sipDomain);
            audioCall = mSipManager.makeAudioCall(mSipProfile.getUriString(), callAddress, listener, 180);
            System.out.println("can make audio call");
        }
        catch (Exception e) {
            Log.i("/InitiateCall", "General Exception", e);
            if (mSipProfile != null) {
                try {
                    mSipManager.close(mSipProfile.getUriString());
                } catch (Exception ee) {
                    Log.i("/InitiateCall",
                            "Error when trying to close manager.", ee);
                    ee.printStackTrace();
                }
            }
            if (audioCall != null) {
                audioCall.close();
            }
        }
    }

    public void checkStatus(int state) {
        switch (state){
            case SipSession.State.IN_CALL:
                System.out.println("IN_CALL Dashboard");
                break;
            case SipSession.State.INCOMING_CALL:
                System.out.println("INCOMING_CALL Dashboard");
                break;
            case SipSession.State.INCOMING_CALL_ANSWERING:
                System.out.println("INCOMING_CALL_ANSWERING Dashboard");
                break;
            case SipSession.State.NOT_DEFINED:
                System.out.println("NOT_DEFINED Dashboard");
                break;
            case SipSession.State.OUTGOING_CALL:
                System.out.println("OUTGOING_CALL Dashboard");
                break;
            case SipSession.State.OUTGOING_CALL_CANCELING:
                System.out.println("OUTGOING_CALL_CANCELING Dashboard");
                break;
            case SipSession.State.OUTGOING_CALL_RING_BACK:
                System.out.println("OUTGOING_CALL_RING_BACK Dashboard");
                break;
            case SipSession.State.PINGING:
                System.out.println("PINGING Dashboard");
                break;
            case SipSession.State.READY_TO_CALL:
                System.out.println("READY_TO_CALL Dashboard");
                break;
            case SipSession.State.REGISTERING:
                System.out.println("REGISTERING Dashboard");
                break;
            case SipSession.State.DEREGISTERING:
                System.out.println("DEREGISTERING Dashboard");
                break;
            default:
                System.out.println("DEFAULT Dashboard");
                break;
        }
    }

    //    Class Sip Settings
    public static class SipSettings extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            // Note that none of the preferences are actually defined here.
            // They're all in the XML file res/xml/preferences.xml.
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }

    /**
     * Closes out your local profile, freeing associated objects into memory
     * and unregistering your device from the server.
     */


    private void changeprofile(){
        final List<String> listItems = new ArrayList<String>();
        listItems.add("Take Photo");
        listItems.add("Select from Photo Library");
        listItems.add("Cancel");

        final CharSequence[] methods = listItems.toArray(new String[listItems.size()]);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Dashboard.this);
        dialogBuilder.setTitle("Upload Profile Picture");
        dialogBuilder.setItems(methods, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                String selectedText = methods[item].toString();  //Selected item in listview
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
        new BitmapFromUrl(userPhoto, uploading).execute(photoUrl);
    }

//    get camera
    private void dispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA
            }, 500);
        }else {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }

    //get image from gallery
    private void getImageFromAlbum(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", true);
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

    private void calcDistance(double currentLat, double currentLon){
        if (currentLat == 0 && currentLon == 0){
            // Permission denied
            this.statusCode = 2;
            appmode.setText("Unidentified");
            appmode.setTextColor(Color.parseColor("#c0c0c0"));
            appmode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presentDialog("Unavailable", "Location Provider unavailable");
                }
            });
        }else if (currentLat == -1 && currentLon == -1){
            // locationManager null
            this.statusCode = 2;
            appmode.setText("Unidentified");
            appmode.setTextColor(Color.parseColor("#c0c0c0"));
            appmode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getAppContext());
                    builder.setTitle("Permission denied");
                    builder.setMessage("Vkclub cannot identify your current location due to location permission denied\n" +
                            "Would you like to enable it ?");
                    builder.setCancelable(true);
                    builder.setPositiveButton(
                            "Enable",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (Build.VERSION.SDK_INT >= 23){
                                        ActivityCompat.requestPermissions(Dashboard.dashboardActivity, new String[]{
                                                Manifest.permission.ACCESS_FINE_LOCATION,
                                                Manifest.permission.ACCESS_COARSE_LOCATION
                                        }, 400);
                                    }
                                }
                            });

                    builder.setNegativeButton(
                            "Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
        } else {
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
                appmode.setTextColor(Color.parseColor("#1A6940"));
                this.statusCode = 0;
                appmode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toastMessage("IN-Kirirom Mode detected\nThank you for using Vkclub.");
                    }
                });
            } else if(distance >= 17){
                appmode.setText("OFF-Kirirom Mode");
                this.statusCode = 1;
                appmode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toastMessage("OFF-Kirirom Mode detected\nThank you for using Vkclub.");
                    }
                });
            } else {
                presentDialog("Technical Error", "Location update failed!");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // in on create
        if (requestCode == 100){
            if (grantResults.length > 0){
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    start_gps_service();
                }else {
                    appmode.setText("Unidentified");
                    appmode.setTextColor(Color.parseColor("#c0c0c0"));
                    appmode.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getAppContext());
                            builder.setTitle("Permission denied");
                            builder.setMessage("Vkclub cannot identify your current location due to location permission denied\n" +
                                    "Would you like to enable it ?");
                            builder.setCancelable(true);
                            builder.setPositiveButton(
                                    "Enable",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            if (Build.VERSION.SDK_INT >= 23){
                                                ActivityCompat.requestPermissions(Dashboard.dashboardActivity, new String[]{
                                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                                }, 400);
                                            }
                                        }
                                    });

                            builder.setNegativeButton(
                                    "Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    });
                }
            }
        }

        if (requestCode == 200){
            if (grantResults.length > 0){
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED && grantResults[1] != PackageManager.PERMISSION_GRANTED){
                    presentDialog("Note", "You cannot make a call without using these permissions.");
                }
            }else {
                initializeManager();
            }
        }

        // if user clicked on appmode
        if (requestCode == 400){
            if (grantResults.length > 0){
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED && grantResults[1] != PackageManager.PERMISSION_GRANTED){
                    presentDialog("Denied", "You denied location permission\nVkclub cannot obtain your current location and some " +
                            "features might not work due to this denial.\nThank you for using Vkclub.");
                }
            }
        }

        if (requestCode == 500){
            if (grantResults.length > 0){
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    presentDialog("Permission Denied", "You denied camera permission.Vkclub cannot open camera without this permissions.\nThank you for using Vkclub.");
                }else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, 100);
        }else {
            start_gps_service();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.RECORD_AUDIO
            }, 200);
        }else {
            initializeManager();
        }

//        start_gps_service();
//        initializeManager();
        Dashboard.returnContext = this;

        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getExtras().get("latitude").toString().equals("unavailable") &&
                            intent.getExtras().get("longitude").toString().equals("unavailable")){
                        calcDistance(0, 0);
                    }else if (intent.getExtras().get("latitude").toString().equals("permission_denied") &&
                            intent.getExtras().get("longitude").toString().equals("permission_denied")){
                        calcDistance(-1, -1);
                    } else {
                        currentLat = Double.parseDouble(intent.getExtras().get("latitude").toString());
                        currentLon = Double.parseDouble(intent.getExtras().get("longitude").toString());
                        message = "Please help! I'm currently facing an emergency problem. Here is my Location: http://maps.google.com/?q=" + currentLat + "," + currentLon;
                        calcDistance(currentLat, currentLon);
                    }
                }
            };
        }

        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));

        System.out.println("ON RESUME");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(audioCall != null){
            audioCall.close();
        }
        if (broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }

        System.out.println("ON DESTROY");
        closeLocalProfile();

        if (callReceiver != null) {
            this.unregisterReceiver(callReceiver);
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

    private void toastMessage(String text){
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    public void presentDialog(String title, String msg) {
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
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap image = extras.getParcelable("data");
            upload(image);
        }

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null) {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(data.getData(), "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 200);
            cropIntent.putExtra("outputY", 200);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, REQUEST_CROP_ICON);


//            Bitmap photo = (Bitmap) data.getExtras().get("data");
//            RoundedBitmapDrawable drawable = createRoundedBitmapDrawableWithBorder(photo);
//            userPhoto.setImageDrawable(drawable);
        }

        if (requestCode == REQUEST_CROP_ICON && resultCode == RESULT_OK && data != null){
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            upload(photo);
        }
    }

    //upload user profile photo to firebase
    private void upload(final Bitmap mbitmap) {
        toastMessage("Uploading image...");
        uploading.setVisibility(View.VISIBLE);
        spinningStatus.setText("Uploading...");
        userPhoto.setImageAlpha(50);
        if(userPhoto != null){
            userPhoto.setDrawingCacheEnabled(true);
            userPhoto.buildDrawingCache();
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
                    Toast.makeText(Dashboard.this, "Upload Done!!!", Toast.LENGTH_SHORT).show();
                    uploading.setVisibility(View.GONE);
                    uploadDone.setVisibility(View.VISIBLE);
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    uploadDone.setVisibility(View.GONE);
                                    userPhoto.setImageAlpha(255);
                                }
                            }, 2500);
                    SharedPreferences.Editor editor = prefs.edit();
                    String encodedString = dbBitmapUtility.getString(mbitmap);
                    editor.putString("get_blob", encodedString);
                    editor.commit();
                    RoundedBitmapDrawable drawable = createRoundedBitmapDrawableWithBorder(mbitmap);
                    userPhoto.setImageDrawable(drawable);

                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(downloadUri)
                            .build();
                    user.updateProfile(profileUpdate);
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

    public void showNotificationPanel() {
        NotificationPanel newFragment = new NotificationPanel();
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
            transaction.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
            // To make it fullscreen, use the 'content' root view as the container
            // for the fragment, which is always the root view for the activity
            transaction.add(R.id.drawerLayout, newFragment)
                    .addToBackStack(null).commit();
        }
    }

}
