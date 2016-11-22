

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
			id="ritwickchaudhry";
			
			query = "create temporary table newreachable"
					+ "(dst varchar(50), level double precision);"
					+ "create temporary table newnewreachable"
					+ "(dst varchar(50), level double precision);"
					+ "create temporary table visited"
					+ "(dst varchar(50));"
					+ "create temporary table weights "
					+ "(dst varchar(50), wts double precision);"
					+ "insert into newreachable "
					+ "SELECT followee, 1.0 "
					+ "from follow "
					+ "where follower=?;"
					+ "insert into visited "
					+ "values(?);";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1,id);
			pstmt.setString(2,id);
			pstmt.executeUpdate();
			ResultSet rset;
			while(true)
			{
				query = "insert into weights "
						+ "select dst, (1*1.0/level) "
						+ "from newreachable;"
						+ "delete from newnewreachable;"
						+ "insert into newnewreachable "
						+ "select distinct dst,level from newreachable "
						+ "where dst not in "
						+ "(select * from visited);"
						+ "insert into visited "
						+ "select distinct dst from newreachable "
						+ "except "
						+ "select * from visited;"
						+ "delete from newreachable;"
						+ "insert into newreachable "
						+ "select follow.followee as dst, level + 1.0 "
						+ "from newnewreachable, follow "
						+ "where newnewreachable.dst = follow.follower;";
						
						
				stmt.executeUpdate(query);
				
				query = "select * from newreachable;";
				
				
				rset = stmt.executeQuery(query);
				int count=0;
//				System.out.println("-------------");
				while(rset.next())
				{
//					System.out.println(rset.getString(1)+" "+rset.getFloat(2));
					count++;	
				}
//				System.out.println(count + "-----------------------\n");
				if(count == 0)
					break;
			}
			System.out.println("Exited While\n");
			query = "select dst,sum(wts) as finalWt "
					+ "from weights where dst not in (select followee from follow where follower=?) "
					+ "group by dst "
					+ "order by finalWt desc limit 3";
				pstmt = conn.prepareStatement(query);
				pstmt.setString(1, id);
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
