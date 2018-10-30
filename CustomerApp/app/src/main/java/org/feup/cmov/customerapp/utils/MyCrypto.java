package org.feup.cmov.customerapp.utils;

import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;

import org.json.JSONObject;

import java.io.IOException;
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
import java.util.Calendar;

import javax.security.auth.x500.X500Principal;


/**
 * Contains utilities for cryptographic stuff.
 */
public final class MyCrypto {


    /**
     * Generates a RSA key pair of given alias.
     * If a key with given alias already exists, returns that instead.
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

        // setup
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.add(Calendar.YEAR, 1);
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
        keyPairGenerator.initialize(
                new KeyPairGeneratorSpec.Builder(context)
                        .setKeySize(512)
                        .setAlias(alias)
                        .setSubject(new X500Principal("CN=feup"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build());

        // generate key pair and return the public key
        return keyPairGenerator.generateKeyPair().getPublic();
    }


    /**
     * Creates a signature using the given key for the given message.
     *
     * @param alias Alias of the private key to sign with.
     * @param msg Message to sign.
     *
     * @return Base64 string signature.
     */
    public static String signMessage(String alias, JSONObject msg) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableEntryException, InvalidKeyException, SignatureException {

        // get private key
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        KeyStore.Entry entry = keyStore.getEntry(alias, null);
        if (entry == null) {
            return "";
        }
        PrivateKey privateKey = ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();

        // sign message
        byte[] bMsg = msg.toString().getBytes("UTF-8");
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(bMsg);
        byte[] bSignature = signature.sign();

        // encode signature to base64 string and return it
        return Base64.encodeToString(bSignature , Base64.DEFAULT | Base64.NO_WRAP);
    }

}
