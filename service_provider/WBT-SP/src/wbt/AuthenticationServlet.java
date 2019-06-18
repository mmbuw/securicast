package wbt;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.warrenstrange.googleauth.GoogleAuthenticator;

/**
 * Servlet starts the authentication with the second factor and creates the
 * qr-code which has to be displayed.
 * 
 * @author Thomas Dressel
 *
 */
@WebServlet("/AuthenticationServlet")
public class AuthenticationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AuthenticationServlet() {
		super();
	}

	/**
	 * Starts the authentication protocol using the <code>hPT</code> from the
	 * {@link HttpSession} attribute and creates saves the generated qr-code in
	 * a cookie.
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		
		int totp;
		
		try {
			String wbtOtpData = request.getParameter("otpData");
			
			if (wbtOtpData != null) {
				String tmp[] = wbtOtpData.split(",");
				byte[] bytes = new byte[tmp.length];
				int count = 0;
				for (String str : tmp) {
					bytes[count++] = Byte.parseByte(str);
				}
				String user = session.getAttribute(SessionAttribute.USER).toString();
				totp = AESGCM.getTOTPFromEncryptedData(bytes, user);
			} else {
				totp = Integer.parseInt(request.getParameter("totp"));
			}
			
			GoogleAuthenticator gAuth = new GoogleAuthenticator();
			
			if (String.valueOf(totp) == "") {
				response.sendRedirect("authentication.jsp");
			}
			
			boolean isCodeValid = gAuth.authorize(SqliteHelper.getKey(session.getAttribute(SessionAttribute.USER).toString()), totp);
			if (isCodeValid) {
				session.setAttribute(SessionAttribute.VALID_2ND_FACTOR, true);
				response.sendRedirect("welcome.jsp");
			} else {
				response.sendRedirect("authentication.jsp");
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect("authentication.jsp");
		} 
		

	}

}
