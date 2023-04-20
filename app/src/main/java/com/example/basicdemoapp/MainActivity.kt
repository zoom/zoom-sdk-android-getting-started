package com.example.basicdemoapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_main.*
import us.zoom.sdk.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeSdk(this)
        initViews()
    }

    /**
     * Initialize the SDK with your credentials. This is required before accessing any of the
     * SDK's meeting-related functionality.
     */
    private fun initializeSdk(context: Context) {
        val sdk = ZoomSDK.getInstance()

        // TODO: Do not use hard-coded values for your key/secret in your app in production!
        val params = ZoomSDKInitParams().apply {
            appKey = "" // TODO: Retrieve your SDK key and enter it here
            appSecret = "" // TODO: Retrieve your SDK secret and enter it here
            domain = "zoom.us"
            enableLog = true // Optional: enable logging for debugging
        }
        // TODO (optional): Add functionality to this listener (e.g. logs for debugging)
        val listener = object : ZoomSDKInitializeListener {
            /**
             * If the [errorCode] is [ZoomError.ZOOM_ERROR_SUCCESS], the SDK was initialized and can
             * now be used to join/start a meeting.
             */
            override fun onZoomSDKInitializeResult(errorCode: Int, internalErrorCode: Int) = Unit
            override fun onZoomAuthIdentityExpired() = Unit
        }

        sdk.initialize(context, listener, params)
    }

    private fun initViews() {
        join_button.setOnClickListener {
            createJoinMeetingDialog()
        }

        start_meeting_button.setOnClickListener {
            createStartMeetingDialog()
        }
    }

    /**
     * Join a meeting without any login/authentication with the meeting's number & password
     */
    private fun joinMeeting(context: Context, meetingNumber: String, pw: String) {
        val meetingService = ZoomSDK.getInstance().meetingService
        val options = JoinMeetingOptions()
        val params = JoinMeetingParams().apply {
            displayName = "" // TODO: Enter your name
            meetingNo = meetingNumber
            password = pw
        }
        meetingService.joinMeetingWithParams(context, params, options)
    }

    /**
     * Start a meeting as an authenticated user. For more information on Zoom user authentication,
     * see https://marketplace.zoom.us/docs/sdk/native-sdks/android/build-an-app/pkce/.
     */
    private fun startMeeting(context: Context, meetingNumber: String, zak: String) {
        val zoomSdk = ZoomSDK.getInstance()
        val startParams = StartMeetingParamsWithoutLogin().apply {
            meetingNo = meetingNumber
            zoomAccessToken = zak
        }
        val meetingService = zoomSdk.meetingService
        val options = StartMeetingOptions()
        meetingService.startMeetingWithParams(context, startParams, options)
    }

    /**
     * Prompt the user to input the meeting number and password and uses the Zoom SDK to join the
     * meeting.
     */
    private fun createJoinMeetingDialog() {
        AlertDialog.Builder(this)
            .setView(R.layout.dialog_join_meeting)
            .setPositiveButton("Join") { dialog, _ ->
                dialog as AlertDialog
                val numberInput = dialog.findViewById<TextInputEditText>(R.id.meeting_no_input)
                val passwordInput = dialog.findViewById<TextInputEditText>(R.id.password_input)
                val meetingNumber = numberInput?.text?.toString()
                val password = passwordInput?.text?.toString()
                meetingNumber?.takeIf { it.isNotEmpty() }?.let { meetingNo ->
                    password?.let { pw ->
                        joinMeeting(this@MainActivity, meetingNo, pw)
                    }
                }
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Prompts the user to input the meeting number and ZAK and uses the Zoom SDK to start a meeting.
     * For information on how to obtain a ZAK token, see https://marketplace.zoom.us/docs/sdk/native-sdks/android/build-an-app/pkce/.
     */
    private fun createStartMeetingDialog() {
        AlertDialog.Builder(this)
            .setView(R.layout.dialog_start_meeting_zak)
            .setPositiveButton("Start") { dialog, _ ->
                dialog as AlertDialog
                val meetingNumberInput =
                    dialog.findViewById<TextInputEditText>(R.id.meeting_number_input)
                val zakInput = dialog.findViewById<TextInputEditText>(R.id.zak_input)
                val meetingNumber = meetingNumberInput?.text?.toString()
                val zak = zakInput?.text?.toString()
                meetingNumber?.takeIf { it.isNotEmpty() }?.let { meetingNum ->
                    zak?.takeIf { it.isNotEmpty() }?.let { zakToken ->
                        startMeeting(this@MainActivity, meetingNum, zakToken)
                    }
                }
                dialog.dismiss()
            }.show()
    }
}
