package org.zankio.cculife;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.zankio.ccudata.base.source.http.HTTPSource;
import org.zankio.cculife.override.Net;

import javax.net.ssl.X509TrustManager;

import io.fabric.sdk.android.Fabric;

public class CCULifeApplication extends Application {
    @Override
    public void onCreate() {

        super.onCreate();
        Fabric.with(this, new Crashlytics());


        X509TrustManager trustManager = Net.generateTrustManagers(this, "ecourse_ssl.crt");
        HTTPSource.trustManager.put("ecourse.ccu.edu.tw", trustManager);
        HTTPSource.sslSocketFactory.put("ecourse.ccu.edu.tw", Net.generateSSLSocketFactory(trustManager));
        X509TrustManager trustManager_taiwanbus = Net.generateTrustManagers(this, "taiwanbus.crt");
        HTTPSource.trustManager.put("www.taiwanbus.tw", trustManager_taiwanbus);
        HTTPSource.sslSocketFactory.put("www.taiwanbus.tw", Net.generateSSLSocketFactory(trustManager_taiwanbus));

    }
}
