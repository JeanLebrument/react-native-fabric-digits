package com.proxima.RCTDigits;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.AuthConfig;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsOAuthSigning;
import com.digits.sdk.android.DigitsSession;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;

import java.util.Map;

import io.fabric.sdk.android.Fabric;

public class DigitsManager extends ReactContextBaseJavaModule implements LifecycleEventListener, AuthCallback {

    private static final String META_DATA_KEY = "io.Digits.ApiKey";
    private static final String META_DATA_SECRET = "io.Digits.ApiSecret";
    private static final String TAG = "RCTDigits";

    private boolean paused = false;

    private Promise promise;
    private DigitsSession digitsSession;
    private DigitsException digitsException;

    public DigitsManager(ReactApplicationContext reactContext) {
        super(reactContext);

        // Check for Twitter config
        TwitterAuthConfig authConfig = getTwitterAuthConfig();
        Fabric.with(getReactApplicationContext(), new TwitterCore(authConfig), new Digits.Builder().build());
    }

    @Override
    public String getName() {
        return "DigitsManager";
    }

    @ReactMethod
    public void launchAuthentication(ReadableMap options, final Promise promise) {
        if (this.promise != null) {
            promise.reject("Authentification process still in progress.");
            return;
        }

        getReactApplicationContext().addLifecycleEventListener(this);
        this.promise = promise;

        String phoneNumber = options != null && options.hasKey("phoneNumber") ?
            options.getString("phoneNumber") : "";

        AuthConfig.Builder digitsAuthConfigBuilder = new AuthConfig.Builder()
                .withAuthCallBack(this)
                .withPhoneNumber(phoneNumber);

        if (options != null && options.hasKey("email")) {
          digitsAuthConfigBuilder.withEmailCollection();
        }

        Digits.authenticate(digitsAuthConfigBuilder.build());
    }

    @ReactMethod
    public void logout() {
        Digits.clearActiveSession();
    }

    @ReactMethod
    public void sessionDetails(Callback callback) {
        DigitsSession session = Digits.getActiveSession();
        if (session != null) {
            WritableMap sessionData = new WritableNativeMap();
            sessionData.putString("authToken", session.getAuthToken().token);
            sessionData.putString("authTokenSecret", session.getAuthToken().secret);
            sessionData.putString("userId", new Long(session.getId()).toString());
            sessionData.putString("phoneNumber", session.getPhoneNumber().replaceAll("[^0-9]", ""));
            sessionData.putString("emailAddress", session.getEmail().address);
            sessionData.putBoolean("emailAddressIsVerified", session.getEmail().verified);
            callback.invoke(null, sessionData);
        } else {
            callback.invoke(null, null);
        }
    }

    private TwitterAuthConfig getTwitterAuthConfig() {
        try {
            ReactApplicationContext context = getReactApplicationContext();
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(
                    context.getPackageName(),
                    PackageManager.GET_META_DATA
            );
            Bundle metaData = applicationInfo.metaData;
            if (metaData == null) {
                Log.w(TAG, "Application metaData is null. Unable to get Digits configuration.");
                return null;
            }
            String key = metaData.getString(META_DATA_KEY);
            String secret = metaData.getString(META_DATA_SECRET);
            if (key == null || secret == null) {
                Log.w(TAG, "Application metaData does not contain Digits configuration.");
                return null;
            }
            return new TwitterAuthConfig(key, secret);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Error while configure Digits: " + e, e);
            return null;
        }
    }

    @Override
    public void success(DigitsSession session, String phoneNumber) {
        digitsSession = session;
        invokePromise();
    }

    @Override
    public void failure(DigitsException exception) {
        digitsException = exception;
        invokePromise();
    }

    @Override
    public void onHostResume() {
        paused = false;
        invokePromise();
    }

    @Override
    public void onHostPause() {
        paused = true;
    }

    @Override
    public void onHostDestroy() {
        paused = true;
    }

    private void invokePromise() {
        if (promise == null || paused) {
            return;
        }


        if (digitsSession != null) {
            TwitterAuthConfig authConfig = TwitterCore.getInstance().getAuthConfig();
            TwitterAuthToken authToken = (TwitterAuthToken) digitsSession.getAuthToken();
            DigitsOAuthSigning oauthSigning = new DigitsOAuthSigning(authConfig, authToken);
            Map<String, String> authHeaders = oauthSigning.getOAuthEchoHeadersForVerifyCredentials();
            WritableNativeMap authHeadersNativeMap = new WritableNativeMap();

            for (Map.Entry<String, String> entry : authHeaders.entrySet()) {
                authHeadersNativeMap.putString(entry.getKey(), entry.getValue());
            }

            promise.resolve(authHeadersNativeMap);
            promise = null;
        } else if (digitsException != null) {
            promise.reject(digitsException.toString());
            promise = null;
        }

        digitsSession = null;
        digitsException = null;
        getReactApplicationContext().removeLifecycleEventListener(this);
    }
}
