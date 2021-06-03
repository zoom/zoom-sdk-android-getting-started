# Android Sample App
This repository contains a sample app to accompany the "Build an App" section from our documentation. In this app, we cover the most basic functionality of the Zoom SDK by demonstrating how to join a meeting anonymously and start an instant meeting as an authenticated user.

(_Note_: The sample app includes two Activities: one in Java and one in Kotlin. Only one activity is required for this app to run, so you may choose the language you are more comfortable with.)

## Prerequisites
- Download and install [Android Studio](https://developer.android.com/studio)
- Download the Zoom Client SDK for Android
- Create an SDK app through the [Zoom Marketplace](https://marketplace.zoom.us/develop/create) and retrieve your SDK key and secret values
- (Recommended) A physical Android device

## Download and Run
1. Clone the git repository
2. Open the android-client-sdk-sample-app in Android Studio
3. **If using Kotlin**: Navigate to `MainActivity` and locate the `initializeSdk` method.
4. **If using Java**: Navigate to `AndroidManifest.xml` and locate the two `activity` elements. Swap the `android:name` attributes of the two elements so that you have the following:
```xml
<activity android:name=".MainActivity" />
<activity android:name=".MainJavaActivity">
  <intent-filter>
    <action android:name="android.intent.action.MAIN" />

    <category android:name="android.intent.category.LAUNCHER" />
  </intent-filter>
</activity>
```
5. **If using Java**: Navigate to `MainJavaActivity` and locate the `initializeSdk` method.
6. Replace the `appKey` and `appSecret` values with your own credentials.
7. Connect your device, ensure it's selected, and click the `Run app` button in the top toolbar.

## Features
* Join a meeting: Using the meeting number and password (if one exists) for a scheduled or previously created meeting, this will start a new Activity with the default meeting UI created within the Zoomm SDK.
* Start an instant meeting: After successfully logging in using your Zoom account email and password, a meeting number and password will be generated and the meeting will start. This also uses the default meeting UI.

## Troubleshooting
For the sake of simplicity, this app does not provide feedback when encountering issues (e.g. unsuccessful login attempts, incorrect SDK credentials).

Should you run into any problems while using the SDK, follow these verification steps. If the app still is not working after these steps, please ask for help on the [dev forum](https://devforum.zoom.us/c/mobile-sdk/17).

1. Verify that your SDK key and secret are correct by re-copying them into your app
2. Ensure your device is not experiencing any network connectivity issues
3. If joining a meeting, verify that your meeting number and password are correct
4. If starting a meeting, verify that your email and password are correct
5. Uninstall and reinstall the app on your device

## Need help?

If you're looking for help, try [Developer Support](https://devsupport.zoom.us) or our [Developer Forum](https://devforum.zoom.us). Priority support is also available with [Premier Developer Support](https://zoom.us/docs/en-us/developer-support-plans.html) plans.
