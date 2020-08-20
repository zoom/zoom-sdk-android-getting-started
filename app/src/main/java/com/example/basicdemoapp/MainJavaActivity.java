package com.example.basicdemoapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;

import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.JoinMeetingParams;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.StartMeetingOptions;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKInitParams;
import us.zoom.sdk.ZoomSDKInitializeListener;

public class MainJavaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_java);

        initializeSdk(this);

        findViewById(R.id.join_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createJoinMeetingDialog();
            }
        });

        findViewById(R.id.start_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Enter email and password
                login("", "");
                startMeeting(MainJavaActivity.this);
            }
        });
    }

    public void initializeSdk(Context context) {
        ZoomSDK sdk = ZoomSDK.getInstance();
        // TODO: Do not use hard-coded values for your key/secret in your app in production!
        ZoomSDKInitParams params = new ZoomSDKInitParams();
        params.appKey = ""; // TODO: Retrieve your SDK key and enter it here
        params.appSecret = ""; // TODO: Retrieve your SDK secret and enter it here
        params.domain = "";
        params.enableLog = true;
        // TODO: Add functionality to this listener (e.g. logs for debugging)
        ZoomSDKInitializeListener listener = new ZoomSDKInitializeListener() {
            @Override
            public void onZoomSDKInitializeResult(int i, int i1) {
            }

            @Override
            public void onZoomAuthIdentityExpired() {
            }
        };
        sdk.initialize(context, listener, params);
    }

    public void joinMeeting(Context context, String meetingNumber, String password) {
        MeetingService meetingService = ZoomSDK.getInstance().getMeetingService();
        JoinMeetingOptions options = new JoinMeetingOptions();
        JoinMeetingParams params = new JoinMeetingParams();
        params.displayName = ""; // TODO: Enter your name
        params.meetingNo = meetingNumber;
        params.password = password;
        meetingService.joinMeetingWithParams(context, params, options);
    }

    public void login(String username, String password) {
        ZoomSDK.getInstance().loginWithZoom(username, password);
    }

    public void startMeeting(Context context) {
        ZoomSDK sdk = ZoomSDK.getInstance();
        if (sdk.isLoggedIn()) {
            MeetingService meetingService = sdk.getMeetingService();
            StartMeetingOptions options = new StartMeetingOptions();
            meetingService.startInstantMeeting(context, options);
        }
    }

    private void createJoinMeetingDialog() {
        new AlertDialog.Builder(this)
                .setView(R.layout.dialog_join_meeting)
                .setPositiveButton("Join", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlertDialog dialog = (AlertDialog) dialogInterface;
                        TextInputEditText numberInput = dialog.findViewById(R.id.meeting_no_input);
                        TextInputEditText passwordInput = dialog.findViewById(R.id.password_input);
                        if (numberInput != null && numberInput.getText() != null && passwordInput != null && passwordInput.getText() != null) {
                            String meetingNumber = numberInput.getText().toString();
                            String password = passwordInput.getText().toString();
                            if (meetingNumber.trim().length() > 0 && password.trim().length() > 0) {
                                joinMeeting(MainJavaActivity.this, meetingNumber, password);
                            }
                        }
                    }
                })
                .show();
    }
}
