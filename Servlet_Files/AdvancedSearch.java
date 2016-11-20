

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class GetDetails
 */
@WebServlet("/AdvancedSearch")
public class AdvancedSearch extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AdvancedSearch() {
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
				JSONObject jsonObj = new JSONObject();
				
				//Local Variables
				String id;
				String type;
				
				id = request.getParameter("id");
				type = request.getParameter("type");
				
				// Set up output stream and type
				response.setContentType("application/json");
				response.setHeader("Access-Control-Allow-Origin", "*");
		        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		        response.setHeader("Access-Control-Max-Age", "3600");
		        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
				PrintWriter out = response.getWriter();
				
				try ( 
						// Trying to connect
						Connection conn = DriverManager.getConnection(connString,username,password);
						Statement stmt = conn.createStatement();
					)
					{
					
						String query;
						PreparedStatement pstmt;
						ResultSet rs;
						if(type.equals("genre")){
							query = "select distinct genre from genre";
							pstmt = conn.prepareStatement(query);
							rs = pstmt.executeQuery();
							JSONArray arr = new JSONArray();
							while(rs.next()){
								arr.put(rs.getString(1));
							}
							jsonObj.put("genre", arr);
						}
						else if(type.equals("author")){
							query = "select author_id, name from author";
							pstmt = conn.prepareStatement(query);
							rs = pstmt.executeQuery();
							JSONArray arr = new JSONArray();
							while(rs.next()){
								arr.put(rs.getString(1)+","+rs.getString(2));
							}
							jsonObj.put("author", arr);
						}
					}
				catch(Exception sqle){
					System.out.println("Exception :" +sqle);
					try {
//						JSONObject jsonObj = new JSONObject();
						jsonObj.put("status", false);
						jsonObj.put("info","internal error");
//						returnObject.put(jsonObj);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				out.print(jsonObj);
				out.flush();
				out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}