/* Scribe
 * 
 * Vincent Rodriguez
 * Michael Takla
 * Felipe Mansilla
 * Noah Feldman * 
 * 
 */

package CS201GroupProject;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.http.Part;

import org.eclipse.jdt.internal.compiler.ast.Statement;



public class JDBCQuery {
	
	private static Connection conn = null;
    private static ResultSet rs = null;
    private static PreparedStatement ps = null;

    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/CourseMash";

	// SELECT statements

	// Users
	private final static String selectUserByEmail = "SELECT * FROM Users WHERE email=?";
	private final static String selectUserByUserID = "SELECT * FROM Users WHERE userID=?";
	private final static String selectPassword = "SELECT password FROM Users WHERE email=?";

	// Users
	private final static String addUser = "INSERT INTO Users(fname, lname, email, password) VALUES(?, ?, ?, ?)";

	

	// Database credentials
	static final String USER = "root";
	static final String PASS = "racecar9";

	  public static void connect() {
  		try {
  			Class.forName("com.mysql.jdbc.Driver");
  			conn = DriverManager.getConnection(DB_URL, USER, PASS);
  		} catch (ClassNotFoundException e) {
  			e.printStackTrace();
  		} catch (SQLException e) {
  			e.printStackTrace();
  		}
  	}

  public static void close() {
		try {
		    if (rs != null) {
		    		rs.close();
		    		rs = null;
		    }
		    if (conn != null) {
		    		conn.close();
		    		conn = null;
		    }
		    if (ps != null) {
		    		ps = null;
		    }
		} catch (SQLException sqle) {
		    System.out.println("connection close error");
		    sqle.printStackTrace();
		}
  }
	

	// USER METHODS

	/**
	 * Add a new user to database
	 * 
	 * @param fname
	 * @param lname
	 * @param username
	 * @param password
	 * @param email
	 */
	public static void addUser(String fname, String lname, String email, String password) {
		connect();
		try {
			PreparedStatement ps = conn.prepareStatement(addUser);
			ps.setString(1, fname);
			ps.setString(2, lname);
			ps.setString(3, email);
			ps.setString(4, password);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	/**
	 * gets User object using username
	 * 
	 * @param username
	 * @return
	 */
	public static User getUserByEmail(String email) {
		connect();
		try {
			PreparedStatement ps = conn.prepareStatement(selectUserByEmail);
			ps.setString(1, email);
			ResultSet result = ps.executeQuery();
			while (result.next()) {
				return new User(result.getInt("userID"), result.getString("fname"), result.getString("lname"), result.getString("password"), result.getString("email"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return null;
	}

	/**
	 * Returns User object using userID
	 * 
	 * @param userID
	 * @return
	 */

	public static User getUserByUserID(int userID) {
		connect();
		try {
			PreparedStatement ps = conn.prepareStatement(selectUserByUserID);
			ps.setInt(1, userID);
			ResultSet result = ps.executeQuery();
			while (result.next()) {
				return new User(userID, result.getString("fname"), result.getString("lname"), result.getString("password"), result.getString("email"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return null;
	}

	/**
	 * does username currently exist
	 * 
	 * @param username
	 * @return true if username exists, else false
	 */
	public static boolean doesUserExist(String email) {
		connect();
		
		try {
			PreparedStatement ps = conn.prepareStatement(selectUserByEmail);
			ps.setString(1, email);
			ResultSet result = ps.executeQuery();
			while (result.next()) {
				return true;
			}
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return false;
	}

	/**
	 * validates password entry to username
	 * 
	 * @param username
	 * @param password
	 * @return true if passwords match, else false
	 */
	public static boolean validate(String username, String password) {
		connect();
		try {
			ps = conn.prepareStatement(selectPassword);
			ps.setString(1, username);
			rs = ps.executeQuery();
			System.out.println(rs);
			if (rs.next()) {
				if (password.equals(rs.getString("password"))) {
					return true;
				}
			}
		} catch (SQLException e) {
			System.out.println("SQLException in function \"validate\"");
			e.printStackTrace();
		} finally {
			close();
		}
		return false;
	}




  

  
    
    private final static String selectAllCourses = "SELECT * FROM Courses";

    public static Vector<Course> getallCourses() {
	connect();
	Vector<Course> courses = new Vector<Course>();
	try {
	    PreparedStatement ps = conn.prepareStatement(selectAllCourses);
	    ResultSet result = ps.executeQuery();
	    while(result.next()){
		courses.add(new Course(result.getInt("courseID"), result.getString("prefix"), result.getString("courseName"), result.getString("professor")));
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close();
	}
	return courses;
    }

    private final static String selectTitlesByCourse = "SELECT title FROM Posts p, Courses c WHERE p.courseID=c.courseID AND c.courseID=?";

    public static Vector<String> getTitlesByCourseID( int courseID ) {
		connect();
		Vector<String> titles = new Vector<String>();
		try {
		    PreparedStatement ps = conn.prepareStatement(selectTitlesByCourse);
		    ps.setInt(1, courseID);
		    ResultSet result = ps.executeQuery();
		    while(result.next()){
			titles.add(result.getString("title"));
		    }
		} catch( SQLException e ){
		    e.printStackTrace();
		} finally {
		    close();
		}
		return titles;
    }

    private final static String selectCoursesByUser = "SELEECT courseID, prefix, courseName, proffesor FROM Courses c, Users u, CourseUsers cu WHERE c.courseID=cu.courseID AND u.userID=cu.userID AND u.userID=?";

    public static Vector<Course> getCoursesByUserID( int userID ){
		connect();
		Vector<Course> courses = new Vector<Course>();
		try {
		    PreparedStatement ps = conn.prepareStatement(selectCoursesByUser);
		    ps.setInt(1, userID);
		    ResultSet result = ps.executeQuery();
		    while(result.next()){
			courses.add(new Course(result.getInt("courseID"), result.getString("prefix"), result.getString("courseName"), result.getString("professor")));
		    }
		} catch( SQLException e ){
		    e.printStackTrace();
		} finally {
		    close();
		}
		return courses;
    }

    private final static String selectPostsByCourse = "SELECT postID, userID, title, body FROM Posts p, Courses c WHERE p.courseID=c.courseID AND c.courseID=?";

    public static Vector<Post> getPostsByCourseID( int courseID ) {
		connect();
		Vector<Post> posts = new Vector<Post>();
		try {
		    PreparedStatement ps = conn.prepareStatement(selectPostsByCourse);
		    ps.setInt(1, courseID);
		    ResultSet result = ps.executeQuery();
		    while(result.next()){
			posts.add(new Post(result.getInt("postID"), result.getInt("userID"), result.getString("title"), result.getString("body")));
		    }
		} catch( SQLException e ){
		    e.printStackTrace();
		} finally {
		    close();
		}
		return posts;
    }

    private final static String selectRepliesByPost = "SELECT userID, reply FROM Users u, Posts p, Replies r WHERE u.userID=r.userID AND p.postID=r.postID AND p.postID=?";

    public static Vector<Reply> getRepliesByPostID( int postID ) {
		connect();
		Vector<Reply> replies = new Vector<Reply>();
		try {
		    PreparedStatement ps = conn.prepareStatement(selectRepliesByPost);
		    ps.setInt(1, postID);
		    ResultSet result = ps.executeQuery();
		    while(result.next()){
		    		replies.add(new Reply(result.getInt("userID"), result.getString("reply")));
		    }
		} catch( SQLException e ){
		    e.printStackTrace();
		} finally {
		    close();
		}
		return replies;
    }

    /*
    private final static String selectPassword = "SELECT password FROM Users u WHERE u.email=?";
    
    public static boolean validate(String email, String password) {
	connect();
	try {
	    PreparedStatement ps = conn.prepareStatement(selectPassword);
	    ps.setString(1, email);
	    ResultSet result = ps.executeQuery();
	    if (rs.next()) {
		if (password.equals(result.getString("password"))) {
		    return true;
		}
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close();
	}
	return false;
    }
    */
}
