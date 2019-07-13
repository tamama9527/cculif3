package org.zankio.cculife;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

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

    }
}
