

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
 * Servlet implementation class message_transfer
 */
@WebServlet("/message_transfer")
public class message_transfer extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public message_transfer() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String connString = ServletInfo.connString;
		String username = ServletInfo.userName;
		String password = ServletInfo.passWord;
		JSONObject jsonObj = new JSONObject();
		
		//Local Variables
		String id;
		String type;
		
		id = request.getParameter("id");
		System.out.println(id);
//		type = request.getParameter("type");
		
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
				query = "with booksbyinterest(book_id, num_interest) as ("
						+ "select genre.book_id, count(*) as num_interest"
						+ " from interests,genre"
						+ " where interests.reader_id=? and interests.genre=genre.genre"
						+ " group by genre.book_id), "
						+ " booksinterested(book_id, num_interest) as ("
						+ " (select book_id, 0 as num_interest from ((select book_id from book) except (select book_id from booksbyinterest)) as temp)"
						+ " union"
						+ " (select * from booksbyinterest)"
						+ " ),"
						+ " trending(book_id, num_readers) as ((select book_id, count(*) as num_readers from "
						+ " book_reader group by book_id)"
						+ " union"
						+ " (select book_id, 0 from ((select book_id from book) except (select book_id from book_reader)) as temp1)),"
						+ " totalreaders(num) as (select count(distinct(reader_id)) from book_reader)"
						+ " select booksinterested.book_id, num_interest*0.6+num_readers*1.0/totalreaders.num as threshold"
						+ " from booksinterested, trending, totalreaders where booksinterested.book_id=trending.book_id "
						+ " and not exists(select * from already_read where already_read.book_id=booksinterested.book_id)"
						+ " and not exists (select * from reading where reading.book_id=booksinterested.book_id)"
						+ " order by threshold desc limit 3";
				System.out.println(query);
				pstmt = conn.prepareStatement(query);
				pstmt.setString(1, id);
				System.out.println("yahoo");
				rs = pstmt.executeQuery();
				JSONArray jsonArray = new JSONArray();
				while(rs.next()){
					
					jsonArray.put(rs.getString(1));
					
//						returnObject.put(jsonObj);
				}
				jsonObj.put("suggested",jsonArray);
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
