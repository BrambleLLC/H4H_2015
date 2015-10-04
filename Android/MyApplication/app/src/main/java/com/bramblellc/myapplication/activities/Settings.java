package com.bramblellc.myapplication.activities;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bramblellc.myapplication.R;
import com.bramblellc.myapplication.layouts.CustomActionbar;
import com.bramblellc.myapplication.layouts.FullWidthButton;

import java.util.HashSet;
import java.util.Set;

public class Settings extends Activity {

    private CustomActionbar settingsCustomActionbar;
    private FullWidthButton myDogsFullWidthButton;
    private FullWidthButton phoneAidInputFullWidthButton;
    private FullWidthButton testFullWidthButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        settingsCustomActionbar = (CustomActionbar) findViewById(R.id.settings_custom_actionbar);
        myDogsFullWidthButton = (FullWidthButton) findViewById(R.id.my_dogs_full_width_button);
        phoneAidInputFullWidthButton = (FullWidthButton) findViewById(R.id.phone_aid_input_full_width_button);
        testFullWidthButton = (FullWidthButton) findViewById(R.id.test_full_width_button);

        settingsCustomActionbar.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        myDogsFullWidthButton.getFullWidthButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDogsPressed(v);
            }
        });

        phoneAidInputFullWidthButton.getFullWidthButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //phoneAidInputPressed(v);
            }
        });

        testFullWidthButton.getFullWidthButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testFullWidthPressed(v);
            }
        });
    }

    public void testFullWidthPressed(View view) {
        Intent intent = new Intent(Settings.this, TestEnvironment.class);
        startActivity(intent);
    }

    public void myDogsPressed(View view) {
        new MaterialDialog.Builder(this)
                .title("ADD OR REMOVE A GUARD DOG")
                .positiveText("ADD")
                .negativeText("REMOVE")
                .neutralText("CANCEL")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        addDog();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        removeDog();
                    }
                })
                .show();
    }

    public void addDog() {
        new MaterialDialog.Builder(this)
                .title("ADD A GUARD DOG")
                .content("Enter the phone number of the Guard Dog you would like to add to your kennel.")
                .positiveText("Add")
                .negativeText("Cancel")
                .input("", "", new MaterialDialog.InputCallback() {
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
                        editor.commit();
                    }
                }).show();
    }

    public void removeDog() {
        new MaterialDialog.Builder(this)
                .title("REMOVE A GUARD DOG")
                .content("Enter the phone number of the Guard Dog you would like to remove from your kennel.")
                .positiveText("remove")
                .negativeText("Cancel")
                .input("", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        SharedPreferences prefs = getSharedPreferences("GuardDog", MODE_PRIVATE);
                        SharedPreferences.Editor editor = getSharedPreferences("GuardDog", MODE_PRIVATE).edit();
                        Set<String> dogSet = prefs.getStringSet("dogs", null);
                        if (dogSet.contains(input)) {
                            dogSet.remove(input.toString());
                            editor.putStringSet("dogs", dogSet);
                            editor.commit();
                        } else {
                            Toast.makeText(Settings.this, "There was no Guard Dog with the inputted phone number in your kennel.", Toast.LENGTH_LONG).show();
                        }
                    }
                }).show();

    }
}
