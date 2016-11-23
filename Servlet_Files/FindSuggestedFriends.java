import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.*;
import java.util.*;
import java.io.*;
/**
 * Servlet implementation class FindSuggestedFriends
 */
@WebServlet("/FindSuggestedFriends")
public class FindSuggestedFriends extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FindSuggestedFriends() {
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
				String query = "";
				query = "create temporary table newreachable"
						+ "(dst varchar(50), level int) "
						+ "create temporary table newnewreachable"
						+ "(dst varchar(50), level int)"
						+ "create temporary table visited"
						+ "(dst varchar(50))"
						+ "create temporary table weights"
						+ "(dst varchar(50), wts real)"
						+ "insert into newreachable "
						+ "SELECT followee, 1 "
						+ "from follow "
						+ "where follower='ritwickchaudhry';";
				
				stmt.executeUpdate(query);
				ResultSet rset = null;
				while(true)
				{
					query = "insert into wts"
							+ "select followee, (1/level) "
							+ "from newreachable;"
							+ "insert into newnewreachable "
							+ "select * from newreachable "
							+ "where dst not in "
							+ "visited;"
							+ "delete from newreachable;"
							+ "insert into newreachable"
							+ "select follow.followee as dst, level + 1 "
							+ "from newnewreachable, follow "
							+ "where newnewreachable.dst = follow.follower;"
							+ "insert into visited "
							+ "select distinct dst from newreachable"
							+ "except visited;"
							+ "select distinct * from newerachable;";
					
					rset = stmt.executeQuery(query);
					int count=0;
					while(rset.next())
					{
						count++;
					}
					if(count == 0)
						break;
				}
				
				query = "select dst,sum(wts) as finalWt from wts"
						+ "from weights"
						+ "group by dst"
						+ "order by finalWt";
				
				rset = stmt.executeQuery(query);
				while(rset.next())
				{
					System.out.println(rset.getString(1) + "\t\t" + rset.getInt(2) + "\n");
				}
				

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
