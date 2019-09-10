package org.zankio.ccudata.train.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.security.SignatureException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HMAC_SHA1 {
    public static String Signature(String xData, String AppKey) throws SignatureException {
        try {
            String result;
            // get an hmac_sha1 key from the raw key bytes
            SecretKeySpec signingKey = new SecretKeySpec(AppKey.getBytes("UTF-8"),"HmacSHA1");

            // get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);

            // compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(xData.getBytes("UTF-8"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                final Base64.Encoder encoder;
                encoder = Base64.getEncoder();
                result = encoder.encodeToString(rawHmac);
                return result;
            }
            else{
                result = new String(android.util.Base64.encode(rawHmac, android.util.Base64.NO_WRAP));
                return result;
            }
        } catch (Exception e) {
            throw new SignatureException("Failed to generate HMAC : "+ e.getMessage());
        }
    }
}
