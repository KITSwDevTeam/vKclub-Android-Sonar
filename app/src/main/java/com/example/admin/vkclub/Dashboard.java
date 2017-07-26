package com.example.admin.vkclub;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.sip.SipSession;
import android.os.SystemClock;
import android.support.v4.app.DialogFragment;
import android.app.PendingIntent;
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
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.os.Build;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;


import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.MANAGE_DOCUMENTS;
import static android.R.attr.bitmap;

public class Dashboard extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Button logoutBtn, opendrawer, appmode, mapButton, membershipBtn, voipBtn, setting, openNotification, aboutUs;
    private ImageView userPhoto;
    private TextView userName, userEmail;
    public static TextView msg;
    private GoogleApiClient mGoogleApiClient;
    private Location mBestReading;
    private LocationRequest mLocationRequest;
    private Bitmap mBitmap;
    private Resources mResources;
    public Voip voipClient;

    private BroadcastReceiver broadcastReceiver;
    String phoneNumber= "+855962304669";
    String message, facebookUserId;
    int statusCode;
    double currentLat, currentLon;

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
    public static DialogFragment callFragment;
    public static FragmentManager fragmentManager;
    public int CALL_IN = 0;
    int hours = 0, mins = 0, secs = 0;

    private static Context returnContext;
    public static int reg_status = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Dashboard.returnContext = this;

        appmode = (Button) findViewById(R.id.appMode);
        mapButton = (Button) findViewById(R.id.mapBtn);
        membershipBtn = (Button) findViewById(R.id.membership);
        voipBtn = (Button)findViewById(R.id.voip);
        openNotification = (Button)findViewById(R.id.openNotification);
        aboutUs = (Button) findViewById(R.id.about_us);

        openNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        setting = (Button)findViewById(R.id.setting);

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(android.R.id.content, new SipSettings()).commit();
            }
        });

        msg = (TextView)findViewById(R.id.welcomeMsg);

        if (!requestSipPermissions())
            initializeManager();

        if(!runtime_permissions())
            start_gps_service();

        // upcoming module
        upComingModule(membershipBtn, "membership");

        // call navigate
        navigateScreen(mapButton, Map.class);
        navigateScreen(aboutUs, About.class);
        navigateScreen(voipBtn, Voip.class);

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

                builder.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
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

        System.out.println("ON CREATE");

        this.fragmentManager = getSupportFragmentManager();
//        voipClient = new Voip();

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.Vkclub.INCOMING_CALL");
        callReceiver = new IncomingCallReceiver();
        this.registerReceiver(callReceiver, filter);

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
            SipProfile.Builder builder = new SipProfile.Builder("10009", "192.168.7.251");
            builder.setPassword("A2apbx10009");
            builder.setPort(sipPort);
            builder.setProtocol("UDP");
//            builder.setAutoRegistration(true);
            builder.setSendKeepAlive(true);
            mSipProfile = builder.build();

            Intent i = new Intent();
            i.setAction("android.Vkclub.INCOMING_CALL");
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, Intent.FILL_IN_DATA);
            mSipManager.open(mSipProfile, pi, null);

            // This listener must be added AFTER manager.open is called,
            // Otherwise the methods aren't guaranteed to fire.

            mSipManager.setRegistrationListener(mSipProfile.getUriString(), new SipRegistrationListener() {
                @Override
                public void onRegistering(String s) {
                    System.out.println("1.SET Registering with SIP Server...");
                }

                @Override
                public void onRegistrationDone(String s, long l) {
                    System.out.println("1.SET Ready");
                    Dashboard.reg_status = 1;
                }

                @Override
                public void onRegistrationFailed(String s, int i, String s1) {
                    System.out.println("1.SET Registration failed.");
                    Dashboard.reg_status = 2;
                }
            });

            mSipManager.register(mSipProfile, 240, new SipRegistrationListener() {
                @Override
                public void onRegistering(String s) {
                    Log.d("1.Registering with SIP Server...", "");
                }

                @Override
                public void onRegistrationDone(String s, long l) {
                    Log.d("1.Ready", "");
                }

                @Override
                public void onRegistrationFailed(String s, int i, String s1) {
                    Log.d("1.Registration failed.", "");
                }
            });

        } catch (ParseException pe) {
            Dashboard.reg_status = 2;
            Log.d("ParseException", pe.toString());
        } catch (SipException se){
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
                mSipManager.close(mSipProfile.getUriString());
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

                    T.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Calling.getInstance().updateCallDuation();
                            System.out.println("Timer Task......");
                        }
                    }, 0, 1000);
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

        int x = borderWidthHalf + bitmapSquareWidth - bitmapWidth;
        int y = borderWidthHalf + bitmapSquareWidth - bitmapHeight;

        canvas.drawBitmap(mBitmap, x, y, null);

        // Initializing a new Paint instance to draw circular border
        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderWidthHalf*2);
        borderPaint.setColor(Color.GREEN);

        canvas.drawCircle(canvas.getWidth()/2, canvas.getWidth()/2, newBitmapSquareWidth/2, borderPaint);

        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(mResources,roundedBitmap);
        roundedBitmapDrawable.setCornerRadius(bitmapRadius);

        roundedBitmapDrawable.setAntiAlias(true);

        // Return the RoundedBitmapDrawable
        return roundedBitmapDrawable;
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
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)  {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            }, 100);
            return true;
        }
        return false;
    }

    private boolean requestSipPermissions(){
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.USE_SIP) != PackageManager.PERMISSION_DENIED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS) != PackageManager.PERMISSION_DENIED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_DENIED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_DENIED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{
                    Manifest.permission.USE_SIP,
                    Manifest.permission.WRITE_SETTINGS,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.RECORD_AUDIO
            }, 100);
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
            appmode.setTextColor(Color.parseColor("#1A6940"));
            this.statusCode = 0;
        } else if(distance >= 17){
            appmode.setText("OFF-Kirirom Mode " + distance);
            this.statusCode = 1;
        } else {
            appmode.setText("Unidentified " + distance);
            appmode.setTextColor(Color.parseColor("#c0c0c0"));
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
            } else if(grantResults[2] == PackageManager.PERMISSION_GRANTED &&
            grantResults[3] == PackageManager.PERMISSION_GRANTED &&
            grantResults[4] == PackageManager.PERMISSION_GRANTED &&
            grantResults[5] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[6] == PackageManager.PERMISSION_GRANTED){
                runtime_permissions();
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
        initializeManager();
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
        Dashboard.returnContext = this;

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

    private void makeToast(String text){
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

    public void showReceiveCallDialog() {

        if ((getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) ==
                Configuration.SCREENLAYOUT_SIZE_LARGE) {
            // The device is using a large layout, so show the fragment as a dialog
            callFragment.show(fragmentManager, "dialog");
        } else {
            // The device is smaller, so show the fragment fullscreen
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            // For a little polish, specify a transition animation
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            // To make it fullscreen, use the 'content' root view as the container
            // for the fragment, which is always the root view for the activity
            transaction.add(R.id.drawerLayout, callFragment)
                    .addToBackStack(null).commit();
        }
    }

    public void dismissReceiveCallDialog(){
        callFragment.getDialog().dismiss();
    }

    public void showDialog() {
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
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            // To make it fullscreen, use the 'content' root view as the container
            // for the fragment, which is always the root view for the activity
            transaction.add(R.id.drawerLayout, newFragment)
                    .addToBackStack(null).commit();
        }
    }
}
