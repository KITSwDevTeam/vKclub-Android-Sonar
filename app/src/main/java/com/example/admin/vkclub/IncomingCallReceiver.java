package com.example.admin.vkclub;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipProfile;
import android.net.sip.SipSession;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.CallLog;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.telephony.TelephonyManager;
import android.telecom.TelecomManager;

import com.facebook.appevents.internal.Constants;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.RunnableFuture;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by admin on 7/11/2017.
 */

public class IncomingCallReceiver extends BroadcastReceiver {
    /**
     * Processes the incoming call, answers it, and hands it over to the
     * WalkieTalkieActivity.
     * @param context The context under which the receiver is running.
     * @param intent The intent being received.
     */
    Dashboard dashboardActivity;
    protected String savedNumber;
    protected int lasteState;
    protected boolean isIncomingCall;
    protected Date callStartTime;
    protected DialogFragment callFragment;
    protected ContentResolver contentResolver;
    Intent in;
    Context context;
    Calling calling = (Calling) Calling.getAppContext();

    private Runnable callDuration;

    @Override
    public void onReceive(final Context context, Intent intent) {
        final Timer T = new Timer();
        SipAudioCall incomingCall = null;
        this.context = context;
        dashboardActivity = (Dashboard) this.context;
        System.out.println("Incoming call Receiver---------------------   " + dashboardActivity);

        try{
            SipAudioCall.Listener listener = new SipAudioCall.Listener(){
                @Override
                public void onCallEstablished(SipAudioCall call) {
                    System.out.println("call established Incoming Call Receiver");
                    System.out.println("IS IN CALL"+call.isInCall());

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
                    System.out.println("Call ended Incoming Call Receiver =========================");
                    T.cancel();
                    System.out.println("Timer cancel....");
                    registerCallLog(contentResolver, dashboardActivity.audioCall.getPeerProfile().getUserName(), "180", CallLog.Calls.INCOMING_TYPE, 1);
                    Calling.activity.finish();
                    calling.player.stop();


                }
            };

            System.out.println("Ringing...");
            in = new Intent(this.context, Calling.class);
            in.putExtra("STATE", "RECEIVING");
            this.context.startActivity(in);
            incomingCall = dashboardActivity.mSipManager.takeAudioCall(intent, listener);
            dashboardActivity.audioCall = incomingCall;
        } catch (Exception e) {
            System.out.println("Exception incoming call receiver " +  e);
            if (incomingCall != null) {
                incomingCall.close();
            }
        }
    }

    private void checkStatus(int state, String event){
        switch (state){
            case SipSession.State.IN_CALL:
                System.out.println("IN_CALL Incoming Call Receiver");
                break;
            case SipSession.State.INCOMING_CALL:
                System.out.println("INCOMING_CALL Incoming Call Receiver");
                break;
            case SipSession.State.INCOMING_CALL_ANSWERING:
                System.out.println("INCOMING_CALL_ANSWERING Incoming Call Receiver");
                break;
            case SipSession.State.NOT_DEFINED:
                System.out.println("NOT_DEFINED Incoming Call Receiver");
                break;
            case SipSession.State.OUTGOING_CALL:
                System.out.println("OUTGOING_CALL Incoming Call Receiver");
                break;
            case SipSession.State.OUTGOING_CALL_CANCELING:
                System.out.println("OUTGOING_CALL_CANCELING Incoming Call Receiver");
                break;
            case SipSession.State.OUTGOING_CALL_RING_BACK:
                System.out.println("OUTGOING_CALL_RING_BACK Incoming Call Receiver");
                break;
            case SipSession.State.PINGING:
                System.out.println("PINGING Incoming Call Receiver");
                break;
            case SipSession.State.READY_TO_CALL:
                if(event.equals("onCallEnded")){
                    dashboardActivity.dismissReceiveCallDialog();
                }
                System.out.println("READY_TO_CALL Incoming Call Receiver");
                break;
            case SipSession.State.REGISTERING:
                System.out.println("REGISTERING Incoming Call Receiver");
                break;
            case SipSession.State.DEREGISTERING:
                System.out.println("DEREGISTERING Incoming Call Receiver");
                break;
            default:
                System.out.println("DEFAULT Incoming Call Receiver");
                break;
        }
    }

    private void registerCallLog(ContentResolver contentResolver,
                                 String number,
                                 String duration,
                                 int type,
                                 int acknowledge){

        ContentValues logRecord = new ContentValues();
        logRecord.put(CallLog.Calls.NUMBER, number);
        logRecord.put(CallLog.Calls.DATE, System.currentTimeMillis());
        logRecord.put(CallLog.Calls.DURATION, duration);
        logRecord.put(CallLog.Calls.TYPE, type);
        logRecord.put(CallLog.Calls.NEW, acknowledge);

        System.out.println("REGISTERING CALL LOG");

        if(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALL_LOG)
                == PackageManager.PERMISSION_GRANTED){
            contentResolver.insert(CallLog.Calls.CONTENT_URI, logRecord);
            System.out.println("REGISTERD CALL LOG");
        }else {
            System.out.println("Cannot write to call log, unless you grant permission");
            Toast.makeText(context, "Cannot write to call log, unless you grant permission.", Toast.LENGTH_LONG).show();
        }

    }

}
