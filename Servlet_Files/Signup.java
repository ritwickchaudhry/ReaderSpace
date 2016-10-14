

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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class Signup
 */
@WebServlet("/Signup")
public class Signup extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Signup() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		response.getWriter().append("Served at: ").append(request.getContextPath());
		
		// Getting the connection details
		String connString = ServletInfo.connString;
		String username = ServletInfo.userName;
		String password = ServletInfo.passWord;
		
		// Local variables
		String user_id;
		String email;
		String passA;
		String passB;
		JSONObject returnObject = new JSONObject();
		
		// Getting user entered details
		user_id = request.getParameter("id");
		email = request.getParameter("email");
		passA = request.getParameter("password_a");
		passB = request.getParameter("password_b");
		
		// Set up output stream and type
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
		PrintWriter out = response.getWriter();
		
		if (user_id == null || passB == null || email == null || passA == null) {
			
			try {
				returnObject.put("status", false);
				returnObject.put("info","incomplete");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		else if (!passA.equals(passB)) {
			
			try {
				returnObject.put("status", false);
				returnObject.put("info","no match");
			} catch (JSONException e) {
				e.printStackTrace();
			}
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
				String query = "select * from reader "
						+ "where reader_id = ?;";
				PreparedStatement pstmt = conn.prepareStatement(query);
				pstmt.setString(1, user_id);
				
				// Checking Result Set
				// Counting the number of results obtained
				ResultSet result = pstmt.executeQuery();
				while(result.next()){
					num_results++;
				}
				
				// If no results found, then user id is unique. Create entry.
				if (num_results == 0) {
					returnObject.put("status", true);
					returnObject.put("info","");
					putEntry(user_id,email,passA);
				}
				// Some person exists with same user id. Can't create.
				else {
					returnObject.put("status", false);
					returnObject.put("info","user exists");
				}
				
			}
			catch (Exception sqle) {
				
				System.out.println("Exception :" +sqle);
				try {
					returnObject.put("status", false);
					returnObject.put("info","internal error");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}
		
		// Flush out with the json object
		out.print(returnObject);
		out.flush();
		out.close();
		
	}

	private void putEntry(String user_id, String email, String pass) {
		// TODO Auto-generated method stub
		
		// Getting the connection details
		String connString = ServletInfo.connString;
		String username = ServletInfo.userName;
		String password = ServletInfo.passWord;
		

		try ( 
				// Trying to connect
				Connection conn = DriverManager.getConnection(connString,username,password);
				Statement stmt = conn.createStatement();
			)
		{
			// Create query string
			String query = "insert into reader values (?,NULL,NULL,NULL,?,NULL,NULL,NULL,NULL,?);";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, user_id);
			pstmt.setString(2, email);
			pstmt.setString(3, pass);
			
			// Update the database
			pstmt.executeUpdate();
			
		}
		catch (Exception sqle) {
			System.out.println("Exception :" +sqle);
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
