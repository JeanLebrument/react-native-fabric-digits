package com.proxima.RCTDigits;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsAuthConfig;
import com.digits.sdk.android.DigitsClient;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsOAuthSigning;
import com.digits.sdk.android.DigitsSession;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableNativeMap;

import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;

import java.util.Map;

import io.fabric.sdk.android.Fabric;

public class DigitsManager extends ReactContextBaseJavaModule {
    private static final String META_DATA_KEY = "io.fabric.ApiKey";
    private static final String META_DATA_SECRET = "io.fabric.ApiSecret";
    private static final String TAG = "RCTDigits";
    volatile DigitsClient digitsClient;
    private ReactApplicationContext mContext;

    public DigitsManager(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
    }

    @Override
    public String getName() {
        return "DigitsManager";
    }

    @ReactMethod
    public void launchAuthentication(ReadableMap options, final Promise promise) {
        TwitterAuthConfig authConfig = getTwitterConfig();
        Fabric.with(mContext, new TwitterCore(authConfig), new Digits());

        String phoneNumber = options.hasKey("phoneNumber") ? options.getString("phoneNumber") : "";

        AuthCallback callback = new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                TwitterAuthConfig authConfig = TwitterCore.getInstance().getAuthConfig();
                TwitterAuthToken authToken = (TwitterAuthToken) session.getAuthToken();
                DigitsOAuthSigning oauthSigning = new DigitsOAuthSigning(authConfig, authToken);
                Map<String, String> authHeaders = oauthSigning.getOAuthEchoHeadersForVerifyCredentials();
                WritableNativeMap authHeadersNativeMap = new WritableNativeMap();

                for (Map.Entry<String, String> entry : authHeaders.entrySet()) {
                    authHeadersNativeMap.putString(entry.getKey(), entry.getValue());
                }

                promise.resolve(authHeadersNativeMap);
            }

            @Override
            public void failure(DigitsException exception) {
                promise.reject(exception.getMessage());
            }
        };
        DigitsAuthConfig.Builder digitsAuthConfigBuilder = new DigitsAuthConfig.Builder()
                .withAuthCallBack(callback)
                .withPhoneNumber(phoneNumber)
                .withThemeResId(R.style.CustomDigitsTheme);

        Digits.authenticate(digitsAuthConfigBuilder.build());
    }

    @ReactMethod
    public void logout() {
        Digits.getSessionManager().clearActiveSession();
    }

    private TwitterAuthConfig getTwitterConfig() {
        String key = getMetaData(META_DATA_KEY);
        String secret = getMetaData(META_DATA_SECRET);

        return new TwitterAuthConfig(key, secret);
    }

    // adapted from http://stackoverflow.com/questions/7500227/get-applicationinfo-metadata-in-oncreate-method
    private String getMetaData(String name) {
        try {
            ApplicationInfo ai = mContext.getPackageManager().getApplicationInfo(
                    mContext.getPackageName(),
                    PackageManager.GET_META_DATA
            );

            Bundle metaData = ai.metaData;
            if (metaData == null) {
                Log.w(TAG, "metaData is null. Unable to get meta data for " + name);
            } else {
                String value = metaData.getString(name);
                return value;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
