

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
@WebServlet("/BookReadingDetails")
public class BookReadingDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BookReadingDetails() {
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
				String bookID;
				
				id = request.getParameter("id");
				bookID = request.getParameter("bookID");
				
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
						query = "select count(*) from book_reader where book_id=?";
						pstmt = conn.prepareStatement(query);
						pstmt.setString(1, bookID);
						rs = pstmt.executeQuery();
						while(rs.next()){
							jsonObj.put("totalreaders", rs.getString(1));
						}
						query = "select count(*) from book_reader where book_id=? and reader_id in (select followee from follow where follower=?)";
						pstmt = conn.prepareStatement(query);
						pstmt.setString(1, bookID);
						pstmt.setString(2, id);
						rs = pstmt.executeQuery();
						while(rs.next()){
							jsonObj.put("friendreaders", rs.getString(1));
						}
						query = "select * from reading where book_id=? and reader_id=?";
						pstmt = conn.prepareStatement(query);
						pstmt.setString(1, bookID);
						pstmt.setString(2, id);
						rs = pstmt.executeQuery();
						int count = 0;
						while(rs.next()){
							count++;
						}
						if(count == 0){
							query = "select * from already_read where book_id=? and reader_id=?";
							pstmt = conn.prepareStatement(query);
							pstmt.setString(1, bookID);
							pstmt.setString(2, id);
							rs = pstmt.executeQuery();
							int count1 = 0;
							while(rs.next()){
								count1++;
							}
							if(count1 == 0){
								jsonObj.put("type", "none");
							}
							else{
								jsonObj.put("type", "alreadyread");
							}
						}
						else{
							jsonObj.put("type", "reading");
						}
						jsonObj.put("status", true);
						
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
		// Getting the connection details
		String connString = ServletInfo.connString;
		String username = ServletInfo.userName;
		String password = ServletInfo.passWord;
		JSONObject jsonObj = new JSONObject();
		
		//Local Variables
		String id;
		String bookID;
		String type;
		
		type = request.getParameter("type");
		id = request.getParameter("id");
		bookID = request.getParameter("bookID");
		
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
				if(type.equals("none")){
					query = "delete from book_reader where book_id=? and reader_id=?";
					pstmt = conn.prepareStatement(query);
					pstmt.setString(1, bookID);
					pstmt.setString(2, id);
					pstmt.executeUpdate();
					query = "delete from reading where book_id=? and reader_id=?";
					pstmt = conn.prepareStatement(query);
					pstmt.setString(1, bookID);
					pstmt.setString(2, id);
					pstmt.executeUpdate();
					query = "delete from already_read where book_id=? and reader_id=?";
					pstmt = conn.prepareStatement(query);
					pstmt.setString(1, bookID);
					pstmt.setString(2, id);
					pstmt.executeUpdate();
					query = "delete from review where book_id=? and reader_id=?";
					pstmt = conn.prepareStatement(query);
					pstmt.setString(1, bookID);
					pstmt.setString(2, id);
					pstmt.executeUpdate();
				}
				else if(type.equals("reading")){
					query = "delete from already_read where book_id=? and reader_id=?";
					pstmt = conn.prepareStatement(query);
					pstmt.setString(1, bookID);
					pstmt.setString(2, id);
					pstmt.executeUpdate();
					query = "delete from reading where book_id=? and reader_id=?";
					pstmt = conn.prepareStatement(query);
					pstmt.setString(1, bookID);
					pstmt.setString(2, id);
					pstmt.executeUpdate();
					query = "insert into reading values (?,?,null)";
					pstmt = conn.prepareStatement(query);
					pstmt.setString(1, bookID);
					pstmt.setString(2, id);
					pstmt.executeUpdate();
					
					query = "delete from book_reader where book_id=? and reader_id=?";
					pstmt = conn.prepareStatement(query);
					pstmt.setString(1, bookID);
					pstmt.setString(2, id);
					pstmt.executeUpdate();
					query = "insert into book_reader values (?,?)";
					pstmt = conn.prepareStatement(query);
					pstmt.setString(1, bookID);
					pstmt.setString(2, id);
					pstmt.executeUpdate();
				}
				else if(type.equals("alreadyread")){
					query = "delete from reading where book_id=? and reader_id=?";
					pstmt = conn.prepareStatement(query);
					pstmt.setString(1, bookID);
					pstmt.setString(2, id);
					pstmt.executeUpdate();
					query = "delete from already_read where book_id=? and reader_id=?";
					pstmt = conn.prepareStatement(query);
					pstmt.setString(1, bookID);
					pstmt.setString(2, id);
					pstmt.executeUpdate();
					query = "insert into already_read values (?,?)";
					pstmt = conn.prepareStatement(query);
					pstmt.setString(1, bookID);
					pstmt.setString(2, id);
					pstmt.executeUpdate();
					
					query = "delete from book_reader where book_id=? and reader_id=?";
					pstmt = conn.prepareStatement(query);
					pstmt.setString(1, bookID);
					pstmt.setString(2, id);
					pstmt.executeUpdate();
					query = "insert into book_reader values (?,?)";
					pstmt = conn.prepareStatement(query);
					pstmt.setString(1, bookID);
					pstmt.setString(2, id);
					pstmt.executeUpdate();
				}
				jsonObj.put("status", true);
				
			}
		catch(Exception sqle){
			System.out.println("Exception :" +sqle);
			try {
//				JSONObject jsonObj = new JSONObject();
				jsonObj.put("status", false);
				jsonObj.put("info","internal error");
//				returnObject.put(jsonObj);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		out.print(jsonObj);
		out.flush();
		out.close();
	}

}