package wbt;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.json.JSONObject;

public class AESGCM {
	
	public static final int AES_KEY_SIZE = 256; // in bits
    public static final int GCM_NONCE_LENGTH = 12; // in bytes
    public static final int GCM_TAG_LENGTH = 16; // in bytes
    
    public static String generateKey() {
    	try {
			SecureRandom random = new SecureRandom();
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(AES_KEY_SIZE, random);
			SecretKey key = keyGenerator.generateKey();
			return Base64.toBase64String(key.getEncoded());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;    	
    }
    
    public static int getTOTPFromEncryptedData(byte[] encryptedData, String user) {
    	String jsonString = new String(encryptedData);
		JSONObject obj = new JSONObject(jsonString);
		String cipherString = obj.getString("cipher");
		byte[] cipherData = Base64.decode(cipherString);
		String nonceString = obj.getString("nonce");
		String wbtKey = SqliteHelper.getWBTKey(user);
		byte[] decodedKey = Base64.decode(wbtKey);
		SecretKey key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
		byte[] nonce = Base64.decode(nonceString);
		
		try {
			GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, nonce);
			Security.addProvider(new BouncyCastleProvider());
			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC");
			cipher.init(Cipher.DECRYPT_MODE, key, spec);
			
			byte[] aad = "SecuriCast".getBytes();
            cipher.updateAAD(aad);
            
			byte[] plainText = cipher.doFinal(cipherData);
			
			obj = new JSONObject(new String(plainText));
			
			if (checkData(obj, user)) {
				return obj.getInt("otp");
			}
		} catch (NoSuchAlgorithmException | NoSuchProviderException | 
				NoSuchPaddingException | InvalidKeyException | 
				InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		
		return 0;
    }
    
    private static boolean checkData(JSONObject obj, String user) {
    	if (obj.getString("sid").equals("SecuriCast_Demo:"+user) &&
    			(System.currentTimeMillis() / 1000) - obj.getLong("t") <= 60 ) {
    		return true;
    	}
    	return false;
    }

}
