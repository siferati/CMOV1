package org.feup.cmov.customerapp.utils;

import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.util.Calendar;

import javax.security.auth.x500.X500Principal;

import static android.support.constraint.Constraints.TAG;

/**
 * Contains utilities for cryptographic stuff.
 */
public final class MyCrypto {


    /**
     * Generates a RSA key pair
     *
     * @param context May be used to pop up some UI to ask the user to unlock or initialize the Android KeyStore facility.
     * @param alias Key alias.
     *
     * @return Public RSA key.
     */
    public static PublicKey generateRSAKeypair(Context context, String alias) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, IOException, KeyStoreException, CertificateException, UnrecoverableEntryException {

        // check if keypair already exists
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        KeyStore.Entry entry = keyStore.getEntry(alias, null);
        if (entry != null) {
            return ((KeyStore.PrivateKeyEntry) entry).getCertificate().getPublicKey();
        }

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.add(Calendar.YEAR, 1);

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");

        keyPairGenerator.initialize(
                new KeyPairGeneratorSpec.Builder(context)
                        .setKeySize(512)
                        .setAlias(alias)
                        .setSubject(new X500Principal("CN=feup"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build());

        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        Log.d("teste1", "" + ((RSAPrivateKey) keyPair.getPrivate()).getModulus());

        return keyPair.getPublic();
    }


    /**
     * Sign a request before sending it.
     *
     * @param alias Name of the private key stored in the keystore.
     * @param req Message to sign.
     */
    public static void signRequest(String alias, JSONObject req) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableEntryException, InvalidKeyException, SignatureException, JSONException {

        // get private key
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        KeyStore.Entry entry = keyStore.getEntry(alias, null);
        PrivateKey privateKey = ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();

        Log.d("teste2", "" + ((RSAPrivateKey) privateKey).getModulus());

        // sign message
        byte[] message = req.toString().getBytes("UTF-8");
        Signature s = Signature.getInstance("SHA256withRSA");
        s.initSign(privateKey);
        s.update(message);
        byte[] signature = s.sign();

        Log.d("teste3", Base64.encodeToString(signature , Base64.DEFAULT));

        req.put("signature", Base64.encodeToString(signature , Base64.DEFAULT));

        Log.d("teste4", req.toString());
    }

}
