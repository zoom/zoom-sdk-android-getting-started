package com.example.basicdemoapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_main.*
import us.zoom.sdk.*

class MainActivity : AppCompatActivity() {
    private val authListener = object : ZoomSDKAuthenticationListener {
        /**
         * This callback is invoked when a result from the SDK's request to the auth server is
         * received.
         */
        override fun onZoomSDKLoginResult(result: Long) {
            if (result.toInt() == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
                // Once we verify that the request was successful, we may start the meeting
                startMeeting(this@MainActivity)
            }
        }
        override fun onZoomIdentityExpired() = Unit
        override fun onZoomSDKLogoutResult(p0: Long) = Unit
        override fun onZoomAuthIdentityExpired() = Unit
    }

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

        login_button.setOnClickListener {
            createLoginDialog()
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
     * Log into a Zoom account through the SDK using your email and password. For more information,
     * see [ZoomSDKAuthenticationListener.onZoomSDKLoginResult] in the [authListener].
     */
    private fun login(username: String, password: String) {
        val result = ZoomSDK.getInstance().loginWithZoom(username, password)
        if (result == ZoomApiError.ZOOM_API_ERROR_SUCCESS) {
            // Request executed, listen for result to start meeting
            ZoomSDK.getInstance().addAuthenticationListener(authListener)
        }
    }

    /**
     * Start an instant meeting as a logged-in user. An instant meeting has a meeting number and
     * password generated when it is created.
     */
    private fun startMeeting(context: Context) {
        val zoomSdk = ZoomSDK.getInstance()
        if (zoomSdk.isLoggedIn) {
            val meetingService = zoomSdk.meetingService
            val options = StartMeetingOptions()
            meetingService.startInstantMeeting(context, options)
        }
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
            }
            .show()
    }

    /**
     * Prompts the user to input their account email and password and uses the Zoom SDK to login.
     * See [ZoomSDKAuthenticationListener.onZoomSDKLoginResult] in the [authListener] for more information.
     */
    private fun createLoginDialog() {
        AlertDialog.Builder(this)
            .setView(R.layout.dialog_login)
            .setPositiveButton("Log in") { dialog, _ ->
                dialog as AlertDialog
                val emailInput = dialog.findViewById<TextInputEditText>(R.id.email_input)
                val passwordInput = dialog.findViewById<TextInputEditText>(R.id.pw_input)
                val email = emailInput?.text?.toString()
                val password = passwordInput?.text?.toString()
                email?.takeIf { it.isNotEmpty() }?.let { emailAddress ->
                    password?.takeIf { it.isNotEmpty() }?.let { pw ->
                        login(emailAddress, pw)
                    }
                }
            }.show()
    }
}
