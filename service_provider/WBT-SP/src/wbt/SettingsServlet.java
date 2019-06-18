package wbt;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import com.warrenstrange.googleauth.KeyRepresentation;


/**
 * Provides the function to start the bootstrapping protocol.
 * 
 * @author Thomas Dressel
 *
 */
@WebServlet("/SettingsServlet")
public class SettingsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SettingsServlet() {
		super();
	}

	/**
	 * Checks, if a user has two factor authentication activated and sets 
	 * a cookie.
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Cookie[] cookies = request.getCookies();

		String user = null;
		for(Cookie cookie : cookies){
		    if("user".equals(cookie.getName())){
		        user = cookie.getValue();
		    }
		}
		if(SqliteHelper.is2FAactivated(user)) {
			for(Cookie cookie : cookies){
			    if("2FA".equals(cookie.getName())){
			        cookie.setValue("1");
			        response.addCookie(cookie);
			    }
			}
		}
		response.sendRedirect("settings.jsp");
	}

	/**
	 * Starts the boostrapping and displays the qr-code with the corresponding secrets.
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Starting Protocol  ---Bootstrapping---");

		HttpSession session = request.getSession();
		String user = session.getAttribute(SessionAttribute.USER).toString();
			
		GoogleAuthenticatorConfigBuilder configBuilder = new GoogleAuthenticatorConfigBuilder();
		configBuilder.setCodeDigits(6);
		configBuilder.setTimeStepSizeInMillis(TimeUnit.SECONDS.toMillis(30));
		configBuilder.setKeyRepresentation(KeyRepresentation.BASE32);
		GoogleAuthenticator gAuth = new GoogleAuthenticator(configBuilder.build());
		GoogleAuthenticatorKey key = gAuth.createCredentials();
		SqliteHelper.saveUser2FAData(user, key);
		SqliteHelper.activate2FA(user);
		
		String wbtSecret = AESGCM.generateKey();
		SqliteHelper.saveSecretKey(user, wbtSecret);
		
		String otpUri = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL("SecuriCast_Demo", user, key);
		
		
		JSONObject obj = new JSONObject();
		obj.put("otpauth", otpUri);
		obj.put("wbt", wbtSecret);
		String qrCode = Functions.getQRCodeBase64String(obj.toString());
		session.setAttribute(SessionAttribute.QRCODE, qrCode);
		session.setAttribute(SessionAttribute.TFA, true);
		session.setAttribute(SessionAttribute.VALID_2ND_FACTOR, true);
		
		response.sendRedirect("settings.jsp");
	}

}
