package wbt;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Provides functions to register a new user at the service provider.
 * 
 * @author Thomas Dressel
 *
 */
@WebServlet("/RegistrationServlet")
public class RegistrationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RegistrationServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

	}

	/**
	 * Gets the entered informations from the user and adds him to the database
	 * of the service provider, if he does not already exists.
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);

		String firstname = request.getParameter("firstname");
		String lastname = request.getParameter("lastname");
		String username = request.getParameter("username");
		String password = request.getParameter("pwd");
		String repassword = request.getParameter("repwd");
		boolean incorrect = false;

		if (!password.equals(repassword)) {
			incorrect = true;
			session.setAttribute("HintText",
					"<font color=red>Your entered passwords are not the same!</font>");
		}

		if (firstname == "" || lastname == "" || username == ""
				|| password == "" || repassword == "") {
			incorrect = true;
			session.setAttribute(SessionAttribute.HINT_TEXT,
					"<font color=red>Please fill in all fields!</font>");
		}

		try {
			ResultSet rs = SqliteHelper.getUser(username);
			boolean isEmpty;
			isEmpty = rs.next();
			if (isEmpty) {
				incorrect = true;
				session.setAttribute(SessionAttribute.HINT_TEXT,
						"<font color=red>User already exists!</font>");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if (incorrect) {
			session.setAttribute("firstname", firstname);
			session.setAttribute("lastname", lastname);
			session.setAttribute("username", username);
			response.sendRedirect("registration.jsp");
		} else {
			SqliteHelper.insertUser(username, password, firstname, lastname);
			response.sendRedirect("registrationSuccess.jsp");
		}
	}

}
