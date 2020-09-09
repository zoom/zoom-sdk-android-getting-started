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
import us.zoom.sdk.ZoomApiError;
import us.zoom.sdk.ZoomAuthenticationError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;
import us.zoom.sdk.ZoomSDKInitParams;
import us.zoom.sdk.ZoomSDKInitializeListener;

public class MainJavaActivity extends AppCompatActivity {
    private ZoomSDKAuthenticationListener authListener = new ZoomSDKAuthenticationListener() {
        /**
         * This callback is invoked when a result from the SDK's request to the auth server is
         * received.
         */
        @Override
        public void onZoomSDKLoginResult(long result) {
            if (result == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
                // Once we verify that the request was successful, we may start the meeting
                startMeeting(MainJavaActivity.this);
            }
        }

        @Override
        public void onZoomSDKLogoutResult(long l) { }
        @Override
        public void onZoomIdentityExpired() { }
        @Override
        public void onZoomAuthIdentityExpired() { }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeSdk(this);
        initViews();
    }

    /**
     * Initialize the SDK with your credentials. This is required before accessing any of the
     * SDK's meeting-related functionality.
     */
    public void initializeSdk(Context context) {
        ZoomSDK sdk = ZoomSDK.getInstance();
        // TODO: Do not use hard-coded values for your key/secret in your app in production!
        ZoomSDKInitParams params = new ZoomSDKInitParams();
        params.appKey = ""; // TODO: Retrieve your SDK key and enter it here
        params.appSecret = ""; // TODO: Retrieve your SDK secret and enter it here
        params.domain = "zoom.us";
        params.enableLog = true;
        // TODO: Add functionality to this listener (e.g. logs for debugging)
        ZoomSDKInitializeListener listener = new ZoomSDKInitializeListener() {
            /**
             * @param errorCode {@link us.zoom.sdk.ZoomError#ZOOM_ERROR_SUCCESS} if the SDK has been initialized successfully.
             */
            @Override
            public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) { }

            @Override
            public void onZoomAuthIdentityExpired() { }
        };
        sdk.initialize(context, listener, params);
    }

    private void initViews() {
        findViewById(R.id.join_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createJoinMeetingDialog();
            }
        });

        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ZoomSDK.getInstance().isLoggedIn()) {
                    startMeeting(MainJavaActivity.this);
                } else {
                    createLoginDialog();
                }
            }
        });
    }

    /**
     * Join a meeting without any login/authentication with the meeting's number & password
     */
    public void joinMeeting(Context context, String meetingNumber, String password) {
        MeetingService meetingService = ZoomSDK.getInstance().getMeetingService();
        JoinMeetingOptions options = new JoinMeetingOptions();
        JoinMeetingParams params = new JoinMeetingParams();
        params.displayName = ""; // TODO: Enter your name
        params.meetingNo = meetingNumber;
        params.password = password;
        meetingService.joinMeetingWithParams(context, params, options);
    }

    /**
     * Log into a Zoom account through the SDK using your email and password. For more information,
     * see {@link ZoomSDKAuthenticationListener#onZoomSDKLoginResult} in the {@link #authListener}.
     */
    public void login(String username, String password) {
        int result = ZoomSDK.getInstance().loginWithZoom(username, password);
        if (result == ZoomApiError.ZOOM_API_ERROR_SUCCESS) {
            // Request executed, listen for result to start meeting
            ZoomSDK.getInstance().addAuthenticationListener(authListener);
        }
    }

    /**
     * Start an instant meeting as a logged-in user. An instant meeting has a meeting number and
     * password generated when it is created.
     */
    public void startMeeting(Context context) {
        ZoomSDK sdk = ZoomSDK.getInstance();
        if (sdk.isLoggedIn()) {
            MeetingService meetingService = sdk.getMeetingService();
            StartMeetingOptions options = new StartMeetingOptions();
            meetingService.startInstantMeeting(context, options);
        }
    }

    /**
     * Prompt the user to input the meeting number and password and uses the Zoom SDK to join the
     * meeting.
     */
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
                        dialog.dismiss();
                    }
                })
                .show();
    }

    /**
     * Prompts the user to input their account email and password and uses the Zoom SDK to login.
     * See {@link ZoomSDKAuthenticationListener#onZoomSDKLoginResult} in the {@link #authListener} for more information.
     */
    private void createLoginDialog() {
        new AlertDialog.Builder(this)
                .setView(R.layout.dialog_login)
                .setPositiveButton("Log in", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlertDialog dialog = (AlertDialog) dialogInterface;
                        TextInputEditText emailInput = dialog.findViewById(R.id.email_input);
                        TextInputEditText passwordInput = dialog.findViewById(R.id.pw_input);
                        if (emailInput != null && emailInput.getText() != null && passwordInput != null && passwordInput.getText() != null) {
                            String email = emailInput.getText().toString();
                            String password = passwordInput.getText().toString();
                            if (email.trim().length() > 0 && password.trim().length() > 0) {
                                login(email, password);
                            }
                        }
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
