package com.sellulose.app;

import android.app.Application;

import com.salesforce.androidsdk.analytics.security.Encryptor;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.app.SalesforceSDKManager.KeyInterface;
import com.salesforce.androidsdk.smartsync.app.SmartSyncSDKManager;

/**
 * Created by swapn on 17-Jan-18.
 */

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SalesforceSDKManager.initNative(getApplicationContext(), new NativeKeyImpl(), SalesforceLoginActivity.class);

    }

}

class NativeKeyImpl implements KeyInterface {

    @Override
    public String getKey(String name) {
        return Encryptor.hash(name + "12s9adpahk;n12-97sdainkasd=012", name + "12kl0dsakj4-cxh1qewkjasdol8");
    }
}