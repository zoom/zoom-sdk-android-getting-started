package com.example.basicdemoapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;

import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.JoinMeetingParams;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.StartMeetingOptions;
import us.zoom.sdk.StartMeetingParamsWithoutLogin;
import us.zoom.sdk.ZoomApiError;
import us.zoom.sdk.ZoomAuthenticationError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;
import us.zoom.sdk.ZoomSDKInitParams;
import us.zoom.sdk.ZoomSDKInitializeListener;

public class MainJavaActivity extends AppCompatActivity {

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
            public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) {
            }

            @Override
            public void onZoomAuthIdentityExpired() {
            }
        };
        sdk.initialize(context, listener, params);
    }

    private void initViews() {
        findViewById(R.id.join_button).setOnClickListener(view -> createJoinMeetingDialog());

        findViewById(R.id.start_meeting_button).setOnClickListener(view -> createStartMeetingDialog());
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
     * Start a meeting as an authenticated user. For more information on Zoom user authentication,
     * see https://marketplace.zoom.us/docs/sdk/native-sdks/android/build-an-app/pkce/.
     */
    public void startMeeting(Context context, String meetingNumber, String zak) {
        ZoomSDK sdk = ZoomSDK.getInstance();
        StartMeetingParamsWithoutLogin startParams = new StartMeetingParamsWithoutLogin();
        startParams.meetingNo = meetingNumber;
        startParams.zoomAccessToken = zak;
        MeetingService meetingService = sdk.getMeetingService();
        StartMeetingOptions options = new StartMeetingOptions();
        meetingService.startMeetingWithParams(context, startParams, options);
    }

    /**
     * Prompt the user to input the meeting number and password and uses the Zoom SDK to join the
     * meeting.
     */
    private void createJoinMeetingDialog() {
        new AlertDialog.Builder(this)
                .setView(R.layout.dialog_join_meeting)
                .setPositiveButton("Join", (dialogInterface, i) -> {
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
                })
                .show();
    }

    /**
     * Prompts the user to input the meeting number and ZAK and uses the Zoom SDK to start a meeting.
     * For information on how to obtain a ZAK token, see https://marketplace.zoom.us/docs/sdk/native-sdks/android/build-an-app/pkce/.
     */
    private void createStartMeetingDialog() {
        new AlertDialog.Builder(this)
                .setView(R.layout.dialog_start_meeting_zak)
                .setPositiveButton("Start", (dialogInterface, i) -> {
                    AlertDialog dialog = (AlertDialog) dialogInterface;
                    TextInputEditText meetingNumberInput = dialog.findViewById(R.id.meeting_number_input);
                    TextInputEditText zakInput = dialog.findViewById(R.id.zak_input);
                    if (meetingNumberInput != null && meetingNumberInput.getText() != null && zakInput != null && zakInput.getText() != null) {
                        String meetingNumber = meetingNumberInput.getText().toString();
                        String zak = zakInput.getText().toString();
                        if (meetingNumber.trim().length() > 0 && zak.trim().length() > 0) {
                            startMeeting(MainJavaActivity.this, meetingNumber, zak);
                        }
                    }
                    dialog.dismiss();
                })
                .show();
    }
}
