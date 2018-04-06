

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class validNewUser
 */
@WebServlet("/validNewUser")
public class validNewUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String fname = request.getParameter("fname");
		String lname = request.getParameter("lname");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		
		
		boolean validLogin = true; //valid login if all information is filled out
		String next = null; //what will this jsp redirect to when done
		
		//first name provided
		if (fname == null || fname.length() == 0) {
			request.setAttribute("fname_err", "Please enter a first name");
    		next = "/SignUp.jsp";
    		validLogin = false;
		}
		//last name provided
		if(lname == null || lname.length() == 0) {
			request.setAttribute("lname_err", "Please enter a first name");
    		next = "/SignUp.jsp";
    		validLogin = false;
		}
		//email provided
		if(email == null || email.length() == 0) {
			request.setAttribute("email_err", "Please enter an email");
    		next = "/SignUp.jsp";
    		validLogin = false;
		}
		//password provided
		if(password == null || password.length() == 0) {
			request.setAttribute("pass_err", "Please enter a password");
			next = "/SignUp.jsp";
			validLogin = false;
		}
		
		if(validLogin == true) {
			//if they filled in all lines, now check for if email is taken or not
			//if the email is taken, display error
			//request.setAttribute("email_err", "Email has already been registered");
			//if the email hasn't been taken, store information in database and login
			
			Connection conn;
			PreparedStatement ps;
			try {
				Class.forName("com.mysql.jdbc.Driver"); //mySQL database connection
				conn = DriverManager.getConnection("jdbc:mysql://localhost/DATABASENAME?user=root&password=rooat");
				ps = conn.prepareCall("Select user, pass from TABLENAME where username=? and password=?");
				
				ps.setString(1, fname);
				ps.setString(2, lname);
				ps.setString(3, email);
				ps.setString(4, password);
			} catch (SQLException | ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			next = "/userProfile.jsp";
			
		}
		
		//final step
		RequestDispatcher dispatch = getServletContext().getRequestDispatcher(next);
		
		try {
			dispatch.forward(request,response);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}