package wbt;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class StartupServlet
 */
@WebServlet("/StartupServlet")
public class StartupServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static Connection sqlConnection = null;
	private static Statement statement;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StartupServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void init() {
    	initializeServiceProvider();
    	System.out.println("___Service Provider initialized successfully___");
	}

	private void initializeServiceProvider() {
		
		sqlConnection = SqliteHelper.getConnection();
		try {
			statement = sqlConnection.createStatement();
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT, firstname TEXT, lastname TEXT, tfa BOOLEAN, otpsecret TEXT, secretkey TEXT);");
			if (statement != null) {
				statement.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		Passphrase.fillPassphraseList();
		Passphrase.getPassphrase();
			
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
