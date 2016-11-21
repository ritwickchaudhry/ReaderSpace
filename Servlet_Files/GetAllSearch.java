

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
import org.json.JSONObject;

//import com.sun.corba.se.pept.transport.Connection;

/**
 * Servlet implementation class GetAllSearch
 */
@WebServlet("/GetAllSearch")
public class GetAllSearch extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetAllSearch() {
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
		JSONArray jsonArray = new JSONArray();
		// Get input from the request
		user_id = request.getParameter("id");

		// Set up output stream and type
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
		System.out.println("Yolo");
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
				String query = "select reader_id as C1,name as C2 from reader;";
				PreparedStatement ps = conn.prepareStatement(query);
//				ps.setString(1, user_id);
				ResultSet rs = ps.executeQuery();
				while(rs.next()){
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("type","reader");
					jsonObj.put("id",rs.getString("C1"));
					jsonObj.put("name",rs.getString("C2"));
					jsonArray.put(jsonObj);
				}
				
				query = "select book_id as C1, name as C2 from book;";
				ps = conn.prepareStatement(query);
				rs = ps.executeQuery();
				while(rs.next()){
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("type","book");
					jsonObj.put("id",rs.getString("C1"));
					jsonObj.put("name",rs.getString("C2"));
					jsonArray.put(jsonObj);
				}
				
//				query = "select author_id as C1, name as C2 from author;";
//				ps = conn.prepareStatement(query);
//				rs = ps.executeQuery();
//				while(rs.next()){
//					JSONObject jsonObj = new JSONObject();
//					jsonObj.put("type","author");
//					jsonObj.put("id",rs.getString("C1"));
//					jsonObj.put("name",rs.getString("C2"));
//					jsonArray.put(jsonObj);
//				}
//				
//				query = "select publisher_id as C1, name as C2 from publisher;";
//				ps = conn.prepareStatement(query);
//				rs = ps.executeQuery();
//				while(rs.next()){
//					JSONObject jsonObj = new JSONObject();
//					jsonObj.put("type","publisher");
//					jsonObj.put("id",rs.getString("C1"));
//					jsonObj.put("name",rs.getString("C2"));
//					jsonArray.put(jsonObj);
//				}
				
				out.print(jsonArray);
				out.flush();
				out.close();
			}
		catch(Exception e){
			System.out.println(e);
		}



	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
