

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
//import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
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
public class UpdateBookRatingComment extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateBookRatingComment() {
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
		String bookID;
		int rating = 0;
		String comment = "";

		id = request.getParameter("id");
		bookID = request.getParameter("bookID");

		System.out.println(id+" <- id, bookID -> " + bookID + " rating -> " + rating);
		
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
			String query = "select * from review where reader_id=? and book_id = ?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, id);
			pstmt.setString(2, bookID);
			ResultSet rs = pstmt.executeQuery();
			
			int count = 0;

			while(rs.next())
			{
				rating = rs.getInt("rating");
				comment = rs.getString(3);
				System.out.println(rating);
				count++;
			}

			if(count == 0)
			{
				returnObject.put("info", "NoRatingFound");				
			}
			else if(count == 1)
			{
				returnObject.put("info", "GotRating "+rating);
				returnObject.put("commentvalue", comment);
			}
			else
			{
				System.out.println("Some Problem with duplicates in review");
			}

			returnObject.put("status", true);
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
		String comment;
		id = request.getParameter("id");
		bookID = request.getParameter("bookID");
		rating = request.getParameter("rating");
		comment = request.getParameter("comment");

		System.out.println(id+" <- id, bookID -> " + bookID + " rating -> " + rating);
		
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
			String query = "delete from review where reader_id=? and book_id = ?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, id);
			pstmt.setString(2, bookID);
			pstmt.executeUpdate();
			
			query = "insert into review values(?, ?, ?, ?)";
			pstmt = conn.prepareStatement(query);
			
			pstmt.setString(3, comment);
			pstmt.setInt(4, Integer.parseInt(rating));
			pstmt.setString(2, id);
			pstmt.setString(1, bookID);
			pstmt.executeUpdate();
			
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
	}

}