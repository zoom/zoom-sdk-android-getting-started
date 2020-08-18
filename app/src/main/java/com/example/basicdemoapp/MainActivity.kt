package com.example.basicdemoapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import us.zoom.sdk.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeSdk(this)

        join_button.setOnClickListener {
            joinMeeting(this, "", "") // TODO: Enter meeting number and password
        }

        start_button.setOnClickListener {
            login("", "") // TODO: Enter email and password
            startMeeting(this)
        }
    }

    /**
     * Initialize the SDK with your credentials. This is required before joining
     */
    fun initializeSdk(context: Context) {
        val sdk = ZoomSDK.getInstance()

        // TODO: Do not use hard-coded values for your key/secret in your app in production!
        val params = ZoomSDKInitParams().apply {
            appKey = "" // TODO: Retrieve your SDK key and enter it here
            appSecret = "" // TODO: Retrieve your SDK secret and enter it here
            domain = "zoom.us"
            enableLog = true // Optional: enable logging for debugging
        }
        // TODO: Add functionality to this listener (e.g. logs for debugging)
        val listener = object : ZoomSDKInitializeListener {
            override fun onZoomSDKInitializeResult(p0: Int, p1: Int) = Unit
            override fun onZoomAuthIdentityExpired() = Unit
        }

        sdk.initialize(context, listener, params)
    }

    /**
     * Join a meeting without any login/authentication with the meeting's number & password
     */
    fun joinMeeting(context: Context, meetingNumber: String, pw: String) {
        val meetingService = ZoomSDK.getInstance().meetingService
        val options = JoinMeetingOptions()
        val params = JoinMeetingParams().apply {
            displayName = "" // TODO: Enter your name
            meetingNo = meetingNumber
            password = pw
        }
        meetingService.joinMeetingWithParams(context, params, options)
    }

    fun login(username: String, password: String) {
        ZoomSDK.getInstance().loginWithZoom(username, password)
    }

    /**
     * Start an instant meeting as a logged-in user
     */
    fun startMeeting(context: Context) {
        val zoomSdk = ZoomSDK.getInstance()
        if (zoomSdk.isLoggedIn) {
            val meetingService = zoomSdk.meetingService
            val options = StartMeetingOptions()
            meetingService.startInstantMeeting(context, options)
        }
    }
}
