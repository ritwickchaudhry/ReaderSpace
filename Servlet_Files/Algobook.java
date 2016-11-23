

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
 * Servlet implementation class Algobook
 */
@WebServlet("/Algobook")
public class Algobook extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Algobook() {
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
				query = "with mybooks as (select book_id from book_reader where reader_id=?), "
						+ "friends as (select followee from follow where follower=?),"
						+ "books_of_friends as (select distinct book_id from book_reader where reader_id in (select * from friends) and book_id not in (select * from mybooks)),"
						+ "similarityindex(book1, book2, num) as (select mybooks.book_id,books_of_friends.book_id,(select count(*) from genre A, genre B where A.book_id=mybooks.book_id and B.book_id=books_of_friends.book_id and A.genre=B.genre) from mybooks, books_of_friends),"
						+ "similarityfriendindex(id, num) as (select friends.followee, count(*) from interests A, interests B, friends where A.reader_id=? and B.reader_id=friends.followee and A.genre=B.genre group by friends.followee),"
						+ "final1table(book1, book2, num, friend) as (select book1, book2, A.num*B.num,id from similarityindex A, similarityfriendindex B where id in (select reader_id from book_reader where book_id=book2)),"
						+ "final2table(book1,book2,num,friend,rate) as (select book1, book2, num, friend, (select case when not exists(select * from review where reader_id=friend and book_id=book2) then 1 "
						+ "else (select 1+(rating*1.0/5-0.5) from review where reader_id=friend and book_id=book2) end )  from final1table),"
						+ "final3table(book2,rank) as (select book2, sum(num*rate) from final2table group by book2),"
						+ "remaining as ((select book_id from book) except (select * from mybooks)),"
						+ "newremaining(book2, rank) as (select remaining.book_id, count(*)*0.1 from remaining, genre A, interests where remaining.book_id=A.book_id and A.genre=interests.genre and interests.reader_id=? group by remaining.book_id)  "
						+ "((select * from final3table) union (select book2, rank from newremaining where newremaining.book2 not in (select book2 from final3table))) order by rank desc limit 3";
				System.out.println(query);
				pstmt = conn.prepareStatement(query);
				pstmt.setString(1, id);
				pstmt.setString(2, id);
				pstmt.setString(3, id);
				pstmt.setString(4, id);
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
