package wbt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

/**
 * Provides functions to interact with the database of the service provider.
 * 
 * @author Thomas Dressel
 *
 */
public class SqliteHelper {

	private static String spPath = "/mnt/3E9ED9A49ED954CD/GitHub/securicast/service_provider/WBT-SP/db/SP.db";
	private static Connection conn = null;

	public static Statement stmt;
	public static ResultSet rs;
	private static final String insertUser = "insert into users (username, password, firstname, lastname) values (?, ?, ?, ?);";
	private static final String activate2FA = "update users set tfa = ? where username = ?;";
	private static final String is2FAactivated = "select * from users where username = ?;";
	private static final String saveUser2FAData = "update users set otpsecret = ? where username = ?;";
	private static final String saveSecretKey = "update users set secretkey = ? where username = ?;";

	/**
	 * Initializes the connection to the database and returns the
	 * {@link Connection} to it.
	 * 
	 * @return the {@link Connection} to the database
	 */
	public static Connection getConnection() {
		if (conn == null) {
			try {
				Class.forName("org.sqlite.JDBC");
				conn = DriverManager.getConnection("jdbc:sqlite:" + spPath);

				return conn;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return conn;
		}
	}

	/**
	 * Returns the data of a user if he is already registered.
	 * 
	 * @param user
	 *            the name of a user
	 * @param password
	 *            the password of a user
	 * @return the {@link ResultSet} with the data of a user
	 */
	public static ResultSet getValidUser(String user, String password) {
		String searchUser = "select * from users where username='" + user + "' AND password='" + password + "'";
		try {
			stmt = SqliteHelper.getConnection().createStatement();
			rs = stmt.executeQuery(searchUser);
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns a {@link ResultSet} with the data of a user.
	 * 
	 * @param user
	 *            the name of the user
	 * @return the {@link ResultSet} with the data of a user
	 */
	public static ResultSet getUser(String user) {
		String searchUser = "select * from users where username='" + user + "'";
		try {
			stmt = SqliteHelper.getConnection().createStatement();
			rs = stmt.executeQuery(searchUser);
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Inserts the data of a user into the database.
	 * 
	 * @param username
	 *            the nickname of the user
	 * @param password
	 *            the password of the user
	 * @param firstname
	 *            the first name of a user
	 * @param lastname
	 *            the last name of a user
	 */
	public static void insertUser(String username, String password, String firstname, String lastname) {
		try {
			PreparedStatement statement = getConnection().prepareStatement(insertUser);
			statement.setString(1, username);
			statement.setString(2, password);
			statement.setString(3, firstname);
			statement.setString(4, lastname);
			statement.executeUpdate();
			if (statement != null) {
				statement.close();
			}
			System.out.println("Inserted '" + username + "' into database.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Closes the {@link Statement}.
	 * 
	 */
	public static void closeRsStmnt() {
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Activates the two factor authentication for a user.
	 * 
	 * @param user
	 *            the username where 2fa gets activated
	 */
	public static void activate2FA(String user) {
		try {
			PreparedStatement statement = getConnection().prepareStatement(activate2FA);
			statement.setBoolean(1, true);
			statement.setString(2, user);
			statement.executeUpdate();
			if (statement != null) {
				statement.close();
			}
			System.out.println("2FA for user " + user + " activated.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks, if a user has two factor authentication activated.
	 * 
	 * @param user
	 *            the name of a user
	 * @return <code>true</code> if the user has two factor authentication
	 *         activated, else <code>false</code>
	 */
	public static boolean is2FAactivated(String user) {
		try {
			boolean active;
			PreparedStatement statement = getConnection().prepareStatement(is2FAactivated);
			statement.setString(1, user);
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				active = result.getBoolean("tfa");
				result.close();
				statement.close();
				return active;
			} else {
				result.close();
				statement.close();
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Saves key for time-based one-time password in database.
	 * 
	 * @param username
	 * 			the name of a user
	 * @param key
	 * 			secret key for totp computation
	 */
	public static void saveUser2FAData(String username, GoogleAuthenticatorKey key) {
		try {
			PreparedStatement statement = getConnection().prepareStatement(saveUser2FAData);
			statement.setString(1, key.getKey());
			statement.setString(2, username);
			statement.executeUpdate();
			if (statement != null) {
				statement.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Saves secret key in database.
	 * 
	 * @param username
	 * 			the name of a user
	 * @param secretKey
	 * 			secret key
	 */
	public static void saveSecretKey(String username, String secretKey) {
		try {
			PreparedStatement statement = getConnection().prepareStatement(saveSecretKey);
			statement.setString(1, secretKey);
			statement.setString(2, username);
			statement.executeUpdate();
			if (statement != null) {
				statement.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get key for totp computation.
	 * 
	 * @param username
	 * 			the name of a user
	 * @return
	 * 			key for totop computation
	 */
	public static String getKey(String username) {
		String searchUser = "select * from users where username='" + username + "'";
		try {
			stmt = SqliteHelper.getConnection().createStatement();
			rs = stmt.executeQuery(searchUser);
			String key;
			if (rs.next()) {
				key = rs.getString("otpsecret");
				rs.close();
				stmt.close();
				return key;
			} else {
				rs.close();
				stmt.close();
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Get key for Bluetooth data encryption.
	 * 
	 * @param username
	 * 			the name of a user
	 * @return
	 * 			key for Bluetooth data encryption
	 */
	public static String getWBTKey(String username) {
		String searchUser = "select * from users where username='" + username + "'";
		try {
			stmt = SqliteHelper.getConnection().createStatement();
			rs = stmt.executeQuery(searchUser);
			String key;
			if (rs.next()) {
				key = rs.getString("secretkey");
				rs.close();
				stmt.close();
				return key;
			} else {
				rs.close();
				stmt.close();
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
