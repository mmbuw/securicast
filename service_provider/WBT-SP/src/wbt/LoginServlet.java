package wbt;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

/**
 * The {@code LoginServlet} handles the login process.
 * 
 * @author Thomas Dressel
 *
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static HashMap<String, HttpSession> sessionMap = new HashMap<String, HttpSession>();

	public static HttpServletRequest request;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

	}

	/**
	 * Checks first, if the entered username and password are valid. 
	 * Then checksif two factor authentication is actived. If it is activated
	 * it redirects the user to the {@link AuthenticationServlet} to start the 
	 * authentication process, else links the user to the welcome page.
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("pwd");
		try {
			ResultSet rs = SqliteHelper.getValidUser(username, password);
			boolean validUser = rs.next();
			HttpSession session = request.getSession(true);
			if (!validUser) {
				rs.close();
				SqliteHelper.closeRsStmnt();
				session.setAttribute(SessionAttribute.WRONG_CREDENTIALS, true);
				response.sendRedirect("index.jsp");
			} else if (validUser) {
				session.setAttribute(SessionAttribute.FIRST_NAME, rs.getString("firstname"));
				session.setAttribute(SessionAttribute.LAST_NAME, rs.getString("lastname"));
				rs.close();
				SqliteHelper.closeRsStmnt();

				boolean tfa = SqliteHelper.is2FAactivated(username);
				String hash = Functions.hash(username);
				session.setAttribute(SessionAttribute.USER, username);
				session.setAttribute(SessionAttribute.HASH, hash);
				session.setAttribute(SessionAttribute.TFA, tfa);
				session.setAttribute(SessionAttribute.VALID_2ND_FACTOR, false);
				
				String passphrase = Passphrase.getPassphrase();
				session.setAttribute(SessionAttribute.PASSPHRASE, passphrase);
				
				JSONObject obj = new JSONObject();
				obj.put("otpUser", "SecuriCast_Demo:" + username);
				obj.put("passphrase", passphrase);
				session.setAttribute(SessionAttribute.WBT_DATA, obj);


				addCookie("user", hash, 30, response);

				if (!tfa) {
					response.sendRedirect("welcome.jsp");
				} else {
					response.sendRedirect("authentication.jsp");
				}
			}
		} catch (SQLException e) {
			System.out.println("SQLException occured: " + e.getMessage());
			e.printStackTrace();
		}
	}

	
	/**
	 * Adds a {@link Cookie} to the current {@link HttpServletResponse}.
	 * 
	 * @param cookieName the name of the cookie
	 * @param cookieValue the value of the cookie
	 * @param maxAge the period of validity in minutes
	 * @param response the {@link HttpServletResponse} to which the 
	 * {@link Cookie} has to be added
	 */
	private void addCookie(String cookieName, String cookieValue, int maxAge,
			HttpServletResponse response) {
		Cookie newCookie = new Cookie(cookieName, cookieValue);
		newCookie.setMaxAge(maxAge * 60);
		response.addCookie(newCookie);
	}

	
	/**
	 * Adds the hashed username to the {@link HttpSession} it belongs to.
	 * 
	 * @param username the username that will be hashed and added
	 */
	public static void addHashToSession(String username) {
		sessionMap.get(username).setAttribute("loggedin",
				Functions.hash(username));
	}

}
