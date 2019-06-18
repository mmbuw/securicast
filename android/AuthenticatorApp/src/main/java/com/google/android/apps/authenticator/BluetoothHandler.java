package com.google.android.apps.authenticator;

import com.google.android.apps.authenticator.testability.DependencyInjector;

import org.bouncycastle.util.encoders.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class provides methods to handle the bluetooth data.
 *
 * @author Thomas Dressel
 */

public class BluetoothHandler {

    private static final int GCM_NONCE_LENGTH = 12; // in bytes
    private static final int GCM_TAG_LENGTH = 16; // in bytes


    public static String getData(String user) {
        String pin = null;
        try {
            OtpSource otpProvider = DependencyInjector.getOtpProvider();
            pin = otpProvider.getNextCode(user);
        } catch (OtpSourceException ignored) {}


        return pin;
    }

    public static String getEncryptedBluetoothData(String user) {
        AccountDb accountDb = DependencyInjector.getAccountDb();
        String wbtKey = accountDb.getWebBluetoothSecret(user);
        byte[] decodedKey = Base64.decode(wbtKey);
        SecretKey key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            final byte[] nonce = new byte[GCM_NONCE_LENGTH];
            random.nextBytes(nonce);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC");
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, nonce);
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);

            byte[] aad = "SecuriCast".getBytes();
            cipher.updateAAD(aad);

            JSONObject obj = new JSONObject();
            obj.put("sid", user);
            obj.put("otp", getData(user));
            obj.put("t", System.currentTimeMillis() / 1000);

            byte[] cipherBytes = cipher.doFinal(obj.toString().getBytes("UTF-8"));
            String cipherText = Base64.toBase64String(cipherBytes);
            obj = new JSONObject();
            obj.put("cipher", cipherText);
            obj.put("nonce", Base64.toBase64String(nonce));

            return obj.toString();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | NoSuchProviderException |
                InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException |
                IllegalBlockSizeException | JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
