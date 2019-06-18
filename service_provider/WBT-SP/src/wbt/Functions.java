package wbt;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;

import com.google.zxing.EncodeHintType;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;


/**
 * Provides functions to support the workflow of the service provider.
 * 
 * @author Thomas Dressel
 *
 */
public class Functions {

	/**
	 * Hashes a given {@link String} with the SHA-256 algorithm.
	 * 
	 * @param username the value that will be hashed
	 * @return the hashed username
	 */
	public static String hash(String username) {
		MessageDigest msgDigest = null;
		try {
			msgDigest = MessageDigest.getInstance("SHA-256");
			msgDigest.update(username.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		byte[] digest = msgDigest.digest();
		StringBuffer sb = new StringBuffer();
		String str;
		for (int i = 0; i < digest.length; i++) {
			str = Integer.toHexString(0xFF & digest[i]);
			if (str.length() < 2) {
				str = "0" + str;
			}
			sb.append(str);
		}
		return sb.toString();
	}

	/**
	 * Returns the {@link Cookie} with a given name of an 
	 * {@link HttpServletRequest}.
	 * 
	 * @param cookieName the name of the  {@link Cookie}
	 * @param request the {@link HttpServletRequest} which contains the 
	 * {@link Cookie}
	 * @return the {@link Cookie} with the searched name 
	 */
	public static Cookie getCookie(String cookieName, HttpServletRequest request) {
		Cookie cookie = null;
		Cookie[] cookies = request.getCookies();
		for (int i = 0; i < cookies.length; i++) {
			cookie = cookies[i];
			if (cookie.getName().equals(cookieName)) {
				return cookie;
			}
		}
		return cookie;
	}

	/**
	 * Checks, if the user named in the cookie is allowed to use
	 * the session.
	 * 
	 * @param request the {@link HttpServletRequest} that has to be checked
	 * @return a {@link Boolean} if the user is allowed or not
	 */
	public static boolean isLoggedIn(HttpServletRequest request) {
		HttpSession session = request.getSession();
		String hash = getCookie("user", request).getValue();
		boolean tfa = false;
		boolean valid2ndFactor = false;
		try {
			tfa = (boolean) session.getAttribute(SessionAttribute.TFA);
			valid2ndFactor = (boolean) session.getAttribute(SessionAttribute.VALID_2ND_FACTOR);
		} catch (NullPointerException e) {
			System.out.println("NullPointerException occured: " + e.getMessage());
			e.printStackTrace();
		}
		
		if (hash.equals(session.getAttribute(SessionAttribute.HASH))) {
			if (tfa && !valid2ndFactor) {
				return false;
			}
			return true;
		}
		return false;
	}
	
	private static byte[] getQRCode(String qrCodeData) {
		return QRCode.from(qrCodeData)
			.withCharset("UTF-8")
			.withHint(EncodeHintType.MARGIN, 0)
			.to(ImageType.PNG)
			.stream()
			.toByteArray();		
	}
	
	public static String getQRCodeBase64String(String qrCodeData) {
		Base64 base64 = new Base64();
		return base64.encodeToString(getQRCode(qrCodeData));
	}
}
