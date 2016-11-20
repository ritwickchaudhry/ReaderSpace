

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
 * Servlet implementation class AlgoFriend
 */
@WebServlet("/AlgoFriend")
public class AlgoFriend extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AlgoFriend() {
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
				query = "with recursive friends as "
						+ "( select followee from follow where follower=?), "
						+ "friends_of_friends as ( select follow.followee as "
						+ "suggest, count(*) as "
						+ "num from follow,friends where follow.follower=friends.followee "
						+ "group by follow.followee), more_suggests(followee,num) as "
						+ "((select followee,1 as num from follow where follower = ?) "
						+ "union (select follow.followee as followee,1 as num "
						+ "from follow,more_suggests T where T.followee=follow.follower)),"
						+ "friends_suggest as ( select suggest, num from friends_of_friends "
						+ "where suggest not in (select followee "
						+ "from follow where follower=?)), "
						+ "final_suggest as ((select followee as suggest, "
						+ "num from more_suggests T "
						+ "where T.followee not in "
						+ "((select followee from follow where follower=?) "
						+ "union "
						+ "(select suggest as followee from friends_suggest))) "
						+ "union "
						+ "(select * from friends_suggest)) "
						+ "select suggest from final_suggest where "
						+ "not(suggest=?) order by num desc limit 3";
				pstmt = conn.prepareStatement(query);
				pstmt.setString(1, id);
				pstmt.setString(2, id);
				pstmt.setString(3, id);
				pstmt.setString(4, id);
				pstmt.setString(5, id);
				System.out.println("yahoo");
				rs = pstmt.executeQuery();
				JSONArray jsonArray = new JSONArray();
				while(rs.next()){
					
					jsonArray.put(rs.getString(1));
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
