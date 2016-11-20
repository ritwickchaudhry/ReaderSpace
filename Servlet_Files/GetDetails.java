

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
@WebServlet("/GetDetails")
public class GetDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetDetails() {
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
						System.out.println(type);
						if(type.equals("reader")){
							System.out.println("yahoo");
							query = "select * from reader where reader_id = ?;";
							System.out.println("yahoo");
							pstmt = conn.prepareStatement(query);
							System.out.println("yahoo");
							pstmt.setString(1, id);
							System.out.println("yahoo");
							rs = pstmt.executeQuery();
							System.out.println("yahoo");
							while(rs.next()){
								
								jsonObj.put("status",true);
								jsonObj.put("name",rs.getString("Name"));
								jsonObj.put("gender",rs.getString("Gender"));
								jsonObj.put("dob",rs.getDate("Date_Of_Birth"));
								jsonObj.put("city",rs.getString("City"));
								jsonObj.put("intro",rs.getString("Introduction"));
//								returnObject.put(jsonObj);
							}
							
							// Query to find the followers of this guy
							query = "select follower from follow where followee=? limit 3";
							pstmt = conn.prepareStatement(query);
							pstmt.setString(1, id);
							rs = pstmt.executeQuery();
							JSONArray arr = new JSONArray();
							while(rs.next()){
								arr.put(rs.getString(1));
							}
							jsonObj.put("follower", arr);
							
							// Query to find the followees of this guy
							
							query = "select followee from follow where follower=? limit 3";
							pstmt = conn.prepareStatement(query);
							pstmt.setString(1, id);
							rs = pstmt.executeQuery();
							arr = new JSONArray();
							while(rs.next()){
								arr.put(rs.getString(1));
							}
							jsonObj.put("followee", arr);
							
							// Query to send the reading books
							
							query = "select book_id from reading where reader_id=? limit 3";
							pstmt = conn.prepareStatement(query);
							pstmt.setString(1, id);
							rs = pstmt.executeQuery();
							arr = new JSONArray();
							while(rs.next()){
								arr.put(rs.getString(1));
							}
							jsonObj.put("books", arr);
							
							// Query to send the reading books
							
							query = "select book_id from already_read where reader_id=? limit 3";
							pstmt = conn.prepareStatement(query);
							pstmt.setString(1, id);
							rs = pstmt.executeQuery();
							arr = new JSONArray();
							while(rs.next()){
								arr.put(rs.getString(1));
							}
							jsonObj.put("done-reading", arr);
						}
						else if(type.equals("book")){
							// ResultSet rs;
							// String query;
							// PreparedStatement p;
							query = "SELECT Name, Description, Author, Number_Of_Pages from Book where Book_ID = ?";
							pstmt = conn.prepareStatement(query);
//							id = "3";
							pstmt.setString(1, id);
							rs = pstmt.executeQuery();
							int count=0;
							while(rs.next())
							{
								jsonObj.put("status",true);
								jsonObj.put("Name",rs.getString(1));
								jsonObj.put("Description",rs.getString(2));
								jsonObj.put("Author",rs.getString(3));
								jsonObj.put("NumberOfPages",rs.getString(4));
								count++;
							}


							// Get overall rating
							query = "SELECT avg(rating) as tot_rating, count(*) from review group by book_id having book_id = ?";
							pstmt = conn.prepareStatement(query);
							pstmt.setString(1, id);
							rs = pstmt.executeQuery();
							
							int counter = 0;
							float totalRating = 0;
							
							while(rs.next())
							{
								totalRating = rs.getFloat(1);
								counter = rs.getInt(2);
							}

							System.out.println(totalRating);
							System.out.println(counter);

							jsonObj.put("counter",counter);
							jsonObj.put("totalRating", totalRating);

							if(count > 1)
							{
								System.out.println("Panic : Book_ID redundancy");
							}
						}
						else if(type.equals("author")){
							
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