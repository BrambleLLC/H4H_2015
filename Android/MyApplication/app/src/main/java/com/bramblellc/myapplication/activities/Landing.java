package com.bramblellc.myapplication.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bramblellc.myapplication.R;
import com.bramblellc.myapplication.sensor.GuardDogSensorListener;
import com.bramblellc.myapplication.services.ActionConstants;
import com.bramblellc.myapplication.services.AnalyzeService;
import com.bramblellc.myapplication.services.DataService;

import org.json.JSONObject;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Landing extends Activity {

    private ImageView transportType;

    private GuardDogSensorListener guardDogSensorListener;

    private BatchBroadcastReceiver batchBroadcastReceiver;

    private AnalyzeBroadcastReceiver analyzeBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("GuardDog", MODE_PRIVATE);

        guardDogSensorListener = new GuardDogSensorListener(this, prefs.getString("username", "hodor"));

        batchBroadcastReceiver = new BatchBroadcastReceiver();
        analyzeBroadcastReceiver = new AnalyzeBroadcastReceiver();

        boolean finish = getIntent().getBooleanExtra("finish", false);
        if (finish) {
            String un = prefs.getString("username", null);
            SharedPreferences.Editor editor = getSharedPreferences("GuardDog", MODE_PRIVATE).edit();
            editor.putString("old_username",un);
            editor.putString("username", null);
            editor.putString("password", null);
            editor.apply();
            Intent intent = new Intent(this, AccountPortal.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
            startActivity(intent);
            finish();
            return;
        }
        transportType = (ImageView) findViewById(R.id.transport_type);
        setContentView(R.layout.landing_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Random rnd = new Random();
        int index = rnd.nextInt(3);
        RelativeLayout layout =(RelativeLayout)findViewById(R.id.background);
        if (index == 1) {
            layout.setBackgroundResource(R.drawable.landing_background_1);
        }
        else if (index == 2) {
            layout.setBackgroundResource(R.drawable.landing_background_2);
        }
        else {
            layout.setBackgroundResource(R.drawable.landing_background_3);
        }
        init();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("Guard-Dog", "Stopped. Lmaoooooo.");
        stopListening();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        Log.d("Guard-Dog", "Resumed. Lmaoooooo.");
        startListening();
    }

    public void init() {
        boolean setup = getIntent().getBooleanExtra("setup", false);
        if (setup) {
            new MaterialDialog.Builder(this)
                    .title("WELCOME")
                    .content("Let's start out by adding some Guard Dogs to your account! A Guard Dog" +
                            " is someone that will receive alerts if you are ever determined to be in danger")
                    .positiveText("Next")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            addDogsChain();
                        }
                    })
                    .show();
        }
        else {
            startListening();
        }
    }

    public void addDogsChain() {
        new MaterialDialog.Builder(this)
                .title("ADD A GUARD DOG")
                .content("Enter the phone number of the Guard Dog you would like to add.")
                .positiveText("Add")
                .inputType(InputType.TYPE_CLASS_PHONE)
                .input("phone number", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        SharedPreferences prefs = getSharedPreferences("GuardDog", MODE_PRIVATE);
                        SharedPreferences.Editor editor = getSharedPreferences("GuardDog", MODE_PRIVATE).edit();
                        Set<String> dogSet = prefs.getStringSet("dogs", null);
                        if (dogSet == null) {
                            dogSet = new HashSet<>();
                        }
                        dogSet.add(input.toString());
                        editor.putStringSet("dogs", dogSet);
                        editor.apply();
                        addAnotherDog();
                    }
                }).show();
    }

    public void addAnotherDog() {
        new MaterialDialog.Builder(this)
                .content("Would you like to add more Guard Dogs?")
                .positiveText("Add another")
                .negativeText("No")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        addDogsChain();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        phoneRepairContact();
                    }
                })
                .show();
    }

    public void phoneRepairContact() {
        new MaterialDialog.Builder(this)
                .title("PHONE REPAIR")
                .content("Do you have a warranty or insurance for your phone and want to add their " +
                        "phone number to be contacted if your phone is damaged?")
                .positiveText("Yes")
                .negativeText("No")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        addPhoneService();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        congrats();
                    }
                })
                .show();
    }

    public void congrats() {
        new MaterialDialog.Builder(this)
                .title("WELCOME TO THE PACK!")
                .content("Thanks for downloading Guard Dog. We hope we can make you feel as safe as man's best friend.")
                .positiveText("Get started")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                    }
                })
                .show();
        startListening();
    }

    public void addPhoneService() {
        new MaterialDialog.Builder(this)
                .title("ADD A PHONE SERVICE CONTACT")
                .content("Enter the phone number of the phone service provider you would like to add.")
                .positiveText("Add")
                .inputType(InputType.TYPE_CLASS_PHONE)
                .input("phone number", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        SharedPreferences.Editor editor = getSharedPreferences("GuardDog", MODE_PRIVATE).edit();
                        String contact = input.toString();
                        editor.putString("phone", contact);
                        editor.apply();
                        congrats();
                    }
                }).show();
    }

    // LOGIN PATH
    // when the login button is pressed
    public void settingsPressed(View v){
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    public void promptForResponse(String content) {

    }

    public void startListening() {
        IntentFilter filter = new IntentFilter(ActionConstants.SENSOR_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(batchBroadcastReceiver, filter);
        Log.d("Guard-Dog", "Listening for frames now. Ayy lmao.");
    }

    public void stopListening() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(batchBroadcastReceiver);
        Log.d("Guard-Dog", "No longer listening for frames. Ayy lmao.");
    }

    private class BatchBroadcastReceiver extends BroadcastReceiver {

        private BatchBroadcastReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String content = intent.getStringExtra("content");
            Intent localIntent = new Intent(Landing.this, AnalyzeService.class);
            localIntent.putExtra("content", content);
            IntentFilter filter = new IntentFilter(ActionConstants.ANALYZE_ACTION);
            LocalBroadcastManager.getInstance(Landing.this).registerReceiver(analyzeBroadcastReceiver, filter);
            startService(localIntent);
        }

    }

    private class AnalyzeBroadcastReceiver extends BroadcastReceiver {

        private AnalyzeBroadcastReceiver() {

        }


        @Override
        public void onReceive(Context context, Intent intent) {
            LocalBroadcastManager.getInstance(Landing.this).unregisterReceiver(analyzeBroadcastReceiver);
            String content = intent.getStringExtra("content");
            boolean guess = intent.getBooleanExtra("guess", false);
            if (guess) {
                Landing.this.promptForResponse(content);
            }
        }

    }

}
