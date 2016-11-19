

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
//import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Servlet implementation class UpdateBookRating
 */
@WebServlet("/UpdateBookRating")
public class UpdateBookRating extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateBookRating() {
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
		JSONObject returnObject = new JSONObject();

		
		//Local Variables
		String id;
		String name;
		String date;
		String city;
		String introduction;
		String gender;
		String contact;
		
		// name = request.getParameter("name");
		// date = request.getParameter("date_of_birth");
		// System.out.println(date);
		// city = request.getParameter("city");
		// introduction = request.getParameter("introduction");
		// gender = request.getParameter("gender");
		id = request.getParameter("id");
		// contact = request.getParameter("contact");
		// System.out.println(id+"sadasd");
		
		// Set up output stream and type
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
		PrintWriter out = response.getWriter();
		System.out.println("yahoo1");
		
		try ( 
				// Trying to connect
				Connection conn = DriverManager.getConnection(connString,username,password);
				Statement stmt = conn.createStatement();
			)
		{
			String query = "select * from reader where reader_id = ?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, id);
			ResultSet result = pstmt.executeQuery();

			while (result.next()) {
				System.out.println("yahoo1");
				returnObject.put("status", true);
				returnObject.put("name",result.getString("Name"));
				returnObject.put("gender",result.getString("Gender"));
				returnObject.put("dob",result.getDate("Date_Of_Birth"));
				returnObject.put("city",result.getString("City"));
				returnObject.put("intro",result.getString("Introduction"));
			}



			// String conns = "";
			// for(int i = 0;i < contact.length(); i++){
				
			// 	if(contact.charAt(i) == ','){
			// 		String query1 = "insert into contact_number values (?,?)";
			// 		pstmt = conn.prepareStatement(query1);
			// 		pstmt.setString(1, id);
			// 		pstmt.setString(2, conns);
			// 		pstmt.executeUpdate();
			// 		conns = "";
			// 	}
			// 	else{
			// 		conns+=contact.charAt(i);
			// 	}
				
				
			// }
			// returnObject.put("status", true);
			// returnObject.put("info","Updated Successfully");
		}
		catch(Exception sqle){
			
			System.out.println("Exception :" +sqle);
			try {
				returnObject.put("status", false);
				returnObject.put("info","internal error");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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

		// Getting the connection details
		String connString = ServletInfo.connString;
		String username = ServletInfo.userName;
		String password = ServletInfo.passWord;
		JSONObject returnObject = new JSONObject();

		
		//Local Variables
		String id;
		String bookID;
		String rating;

		id = request.getParameter("id");
		bookID = request.getParameter("bookID");
		int rating = request.getParameter("rating");

		System.out.println(id+" <- id, bookID -> " + bookID + "rating -> " + rating);
		
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
			String query = "update review set rating=? where reader_id=? and book_id = ?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			
			pstmt.setInt(1, Integer.parseInt(rating));
			pstmt.setString(2, id);
			pstmt.setString(3, bookID);
			pstmt.executeQuery();
			
			returnObject.put("status", true);
			returnObject.put("info","Updated Successfully");
		}
		catch(Exception sqle){
			
			System.out.println("Exception :" +sqle);
			try {
				returnObject.put("status", false);
				returnObject.put("info","internal error");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// Flush out with the json object
		out.print(returnObject);
		out.flush();
		out.close();
		
	}

}
