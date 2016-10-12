

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

import org.json.JSONException;
import org.json.JSONObject;

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
		String user_id;
		String user_password;
		JSONObject returnObject = new JSONObject();
		
		// Get input from the request
		user_id = request.getParameter("id");
		user_password = request.getParameter("password");
		
		// Set up output stream and type
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
		PrintWriter out = response.getWriter();
		
		// Initial check for incomplete information
		if (user_id == null || user_password == null) {
			
			try {
				returnObject.put("status", false);
				returnObject.put("info","incomplete");
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
				String saved_password = "";
				
				
				// Make query to check password
				String query = "select password from reader "
						+ "where reader_id = ?";
				PreparedStatement pstmt = conn.prepareStatement(query);
				pstmt.setString(1, user_id);
				
				// Checking Result Set
				// Extract the password. ASSUMING UNIQUE USER_ID
				ResultSet result = pstmt.executeQuery();
				while(result.next()){
					saved_password = result.getString("password");
					num_results++;
				}
				
				// If no results returned, then no such user exists
				if(num_results == 0){
					returnObject.put("status", false);
					returnObject.put("info","no user");
				}
				// Now check for password
				else {
					
					// Wrong password					
					if (!user_password.equals(saved_password)) {
						returnObject.put("status", false);
						returnObject.put("info","wrong password");
					}
					else {
						returnObject.put("status", true);
						returnObject.put("info","");
						HttpSession session = request.getSession(true);
						session.setAttribute("id", user_id);
					}
					
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// doGet(request, response);
	}

}
