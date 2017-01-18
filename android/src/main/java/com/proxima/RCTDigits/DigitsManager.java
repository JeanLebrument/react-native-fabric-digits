package com.proxima.RCTDigits;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.AuthConfig;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsOAuthSigning;
import com.digits.sdk.android.DigitsSession;
import com.digits.sdk.android.models.Contacts;
import com.digits.sdk.android.models.DigitsUser;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;

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

        String phoneNumber = options.hasKey("phoneNumber") ? options.getString("phoneNumber") : "";

        // Check for Twitter config
        TwitterAuthConfig authConfig = getTwitterAuthConfig();
        Fabric.with(getReactApplicationContext(), new TwitterCore(authConfig), new Digits());

        AuthConfig.Builder builder = new AuthConfig.Builder()
                .withAuthCallBack(this)
                .withPhoneNumber(phoneNumber);

        if (options.hasKey("email")) {
            builder.withEmailCollection();
        }

        Digits.authenticate(builder.build());
    }

    @ReactMethod
    public void logout() {
        Digits.clearActiveSession();
    }

    @ReactMethod
    public void uploadContacts() {
        Digits.uploadContacts();
    }

    @ReactMethod
    public void getIdentifier() {
        Digits.getInstance().getIdentifier();
    }

    @ReactMethod
    public void findFriends(final Callback callback) {
        Digits.findFriends(new com.twitter.sdk.android.core.Callback<Contacts>() {

            @Override
            public void success(com.twitter.sdk.android.core.Result<Contacts> result) {
                WritableMap sessionData = new WritableNativeMap();
                WritableArray users = new WritableNativeArray();
                if (result.data.users != null) {
                    // Process data
                    for (DigitsUser user : result.data.users) {
                        users.pushString(user.idStr);
                    }
                }
                sessionData.putArray("friends", users);
                callback.invoke(null, sessionData);
            }

            @Override
            public void failure(TwitterException exception) {
                // Show error
                callback.invoke(true);
            }
        });
    }

    @ReactMethod
    public void sessionDetails(Callback callback) {
        DigitsSession session = Digits.getActiveSession();
        if (session != null) {
            WritableMap sessionData = new WritableNativeMap();
            sessionData.putString("userId", new Long(session.getId()).toString());
            sessionData.putBoolean("isValidUser", session.isValidUser());
            sessionData.putString("phoneNumber", session.getPhoneNumber().replaceAll("[^0-9]", ""));
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
        } else if (digitsException != null) {
            promise.reject(digitsException.toString());
        } else {
            promise.reject("Authentification failed without exception.");
        }

        promise = null;
        digitsSession = null;
        digitsException = null;
        getReactApplicationContext().removeLifecycleEventListener(this);
    }
}
