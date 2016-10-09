

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		// Getting the connection details
		String connString = ServletInfo.connString;
		String username = ServletInfo.userName;
		String password = ServletInfo.passWord;
		
		// Get input from the request
		String user_id = request.getParameter("id");
		String user_password = request.getParameter("password");
		PrintWriter out = response.getWriter();
		
		// Initial check for incomplete information
		if (user_id == null || user_password == null) {
			out.println("Username or Password is missing.");
		}
		else {
			
			try ( 
				// Trying to connect
				Connection conn = DriverManager.getConnection(connString,username,password);
				Statement stmt = conn.createStatement();
			)
			{
				// Local variables
				int num_results = 0;
				
				
				// Make query to check password
				String query = "select * from password where ID = ? and password = ?";
				PreparedStatement pstmt = conn.prepareStatement(query);
				pstmt.setString(1, user_id);
				pstmt.setString(2, user_password);
				
				// Checking Result Set
				ResultSet result = pstmt.executeQuery();
				while(result.next()){
					num_results++;
				}
				if(num_results == 0){
					out.println(false);
				}
				else {
					HttpSession session = request.getSession(true);
					session.setAttribute("id", user_id);
					out.println(true);
				}
				
			}
			catch (Exception sqle) {
				System.out.println("Exception :" +sqle);
			}
		}
		out.close();
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
