

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
 * Servlet implementation class ReturnReviewList
 */
@WebServlet("/ReturnReviewList")
public class ReturnReviewList extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReturnReviewList() {
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
					String query = "select * from review where book_id = ? order by rating desc limit 2";
					PreparedStatement pstmt = conn.prepareStatement(query);
//					pstmt.setString(1, id);
					pstmt.setString(1, bookID);
					ResultSet rs = pstmt.executeQuery();
					JSONArray arr = new JSONArray();
					while(rs.next()){
						JSONObject obj = new JSONObject();
						obj.put("ratingvalue", rs.getString(4));
						obj.put("commentvalue", rs.getString(3));
						obj.put("readervalue", rs.getString(2));
						arr.put(obj);
					}
					returnObject.put("status", true);
					returnObject.put("highestrating", arr);
					arr = new JSONArray();
					query = "select * from review "
							+ "where book_id=? and "
							+ "reader_id in "
							+ "(select followee from follow where follow.follower=?)"
							+ " order by rating desc limit 2";
					pstmt = conn.prepareStatement(query);
					pstmt.setString(1, bookID);
					pstmt.setString(2, id);
					rs = pstmt.executeQuery();
					while(rs.next()){
						JSONObject obj = new JSONObject();
						obj.put("ratingvalue", rs.getString(4));
						obj.put("commentvalue", rs.getString(3));
						obj.put("readervalue", rs.getString(2));
						arr.put(obj);
					}
					returnObject.put("friendrating", arr);
					
					arr = new JSONArray();
					query = "select * from review where book_id = ? order by rating limit 2";
					pstmt = conn.prepareStatement(query);
					pstmt.setString(1, bookID);
					rs = pstmt.executeQuery();
					while(rs.next()){
						JSONObject obj = new JSONObject();
						obj.put("ratingvalue", rs.getString(4));
						obj.put("commentvalue", rs.getString(3));
						obj.put("readervalue", rs.getString(2));
						arr.put(obj);
					}
					returnObject.put("lowestrating", arr);
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
	}

}
