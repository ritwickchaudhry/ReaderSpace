

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
@WebServlet("/SearchReturnDetails")
public class SearchReturnDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchReturnDetails() {
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
				String arg;
				String type;
				
				arg = request.getParameter("arg");
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
						if(type.equals("genre")){
							query = "with view1 as (select book_id, avg(rating) from review group by book_id), "
									+ " view2 as (select genre.book_id,avg from genre, view1 where genre.genre=? and genre.book_id = view1.book_id), "
									+ "view3(book_id, number) as ((select * from view2) union (select book_id, 0 from genre where genre=? and book_id not in (select book_id from view2))) "
									+ "select * from view3 order by number desc limit 3";
							pstmt = conn.prepareStatement(query);
							pstmt.setString(1, arg);
							pstmt.setString(2, arg);
							rs = pstmt.executeQuery();
							JSONArray arr = new JSONArray();
							while(rs.next()){
								arr.put(rs.getString(1));
							}
							jsonObj.put("status", true);
							jsonObj.put("result", arr);
						}
						else if(type.equals("author")){
							query = "with view1 as (select book_id, avg(rating) from review group by book_id), "
									+ " view2 as (select book.book_id,avg from book, view1 where book.author=? and book.book_id = view1.book_id), "
									+ "view3(book_id, number) as ((select * from view2) union (select book_id, 0 from book where author=? and book_id not in (select book_id from view2))) "
									+ "select * from view3 order by number desc limit 3";
							pstmt = conn.prepareStatement(query);
							pstmt.setString(1, arg);
							pstmt.setString(2, arg);
							rs = pstmt.executeQuery();
							JSONArray arr = new JSONArray();
							while(rs.next()){
								arr.put(rs.getString(1));
							}
							jsonObj.put("status", true);
							jsonObj.put("result", arr);
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