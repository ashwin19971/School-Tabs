package com.example.ashwingiri.schooltab;

import android.annotation.TargetApi;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class MainActivity extends AppCompatActivity {

    ComponentName adminComponent;
    DevicePolicyManager mDPM;
    Calendar cal;
    int hour;
    AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        adminComponent = new ComponentName(getApplicationContext(), SchoolAdminReceiver.class);
        mDPM=(DevicePolicyManager) getApplicationContext()
                .getSystemService(Context.DEVICE_POLICY_SERVICE);
        cal = new GregorianCalendar();
        hour = cal.get(Calendar.HOUR_OF_DAY);
        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if (hour > 7 && hour < 14) {
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

            if(!isMyDevicePolicyReceiverActive()){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Allow to access Device administration permission");

                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent intent = new Intent(
                                        DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                                intent.putExtra(
                                        DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                                        adminComponent);
                                intent.putExtra(
                                        DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                                        "You need to activate Device Administrator to disable camera!");
                                startActivityForResult(intent,1234);
                            }
                        }
                );

                alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "Camera will be not disabled during school hours",
                                        Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                );
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }

        }
    }

//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (isMyDevicePolicyReceiverActive()) {
//            AdminEnabledCheckbox.setChecked(true);
//        } else {
//            AdminEnabledCheckbox.setChecked(false);
//        }
//
//        AdminEnabledCheckbox.setOnCheckedChangeListener(
//                new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                        if (isChecked) {
//                            Intent intent = new Intent(
//                                    DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
//                            intent.putExtra(
//                                    DevicePolicyManager.EXTRA_DEVICE_ADMIN,
//                                    adminComponent);
//                            intent.putExtra(
//                                    DevicePolicyManager.EXTRA_ADD_EXPLANATION,
//                                    "You need to activate Device Administrator to disable camera!");
//                            startActivityForResult(intent,1234);
//                        } else {
//                            mDPM.removeActiveAdmin(adminComponent);
//                        }
//                    }
//                }
//        );
//    }
//
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1234:
                    if (hour > 7 && hour < 14) {
                        mDPM.setCameraDisabled(adminComponent, true);
                    } else {
                        mDPM.setCameraDisabled(adminComponent, false);
                    }
                    break;
            }
        }
    }

    private boolean isMyDevicePolicyReceiverActive() {
        return mDPM.isAdminActive(adminComponent);
    }

}

