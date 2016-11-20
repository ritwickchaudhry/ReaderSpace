

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
//import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Servlet implementation class EditDetails
 */
@WebServlet("/EditDetails")
public class EditDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EditDetails() {
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
		String name;
		String date;
		String city;
		String introduction;
		String gender;
		String contact;
		
		// name = request.getParameter("name");
		// date = request.getParameter("date_of_birth");
		// System.out.println(date);
		// city = request.getParameter("city");
		// introduction = request.getParameter("introduction");
		// gender = request.getParameter("gender");
		id = request.getParameter("id");
		// contact = request.getParameter("contact");
		// System.out.println(id+"sadasd");
		
		// Set up output stream and type
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
		PrintWriter out = response.getWriter();
		System.out.println("yahoo1");
		
		try ( 
				// Trying to connect
				Connection conn = DriverManager.getConnection(connString,username,password);
				Statement stmt = conn.createStatement();
			)
		{
			String query = "select * from reader where reader_id = ?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, id);
			ResultSet result = pstmt.executeQuery();

			while (result.next()) {
				System.out.println("yahoo1");
				returnObject.put("status", true);
				returnObject.put("name",result.getString("Name"));
				returnObject.put("gender",result.getString("Gender"));
				returnObject.put("dob",result.getDate("Date_Of_Birth"));
				returnObject.put("city",result.getString("City"));
				returnObject.put("intro",result.getString("Introduction"));
			}

			query = "select genre from interests where reader_id = ?";
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1,id);
			result = pstmt.executeQuery();
			JSONArray arr = new JSONArray();
			while(result.next()){
				arr.put(result.getString(1));
			}
			returnObject.put("genre", arr);
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
		String name;
		String date;
		String city;
		String introduction;
		String gender;
		String contact;
		String interestdata;
		
		name = request.getParameter("name");
		date = request.getParameter("date_of_birth");
		System.out.println(date);
		city = request.getParameter("city");
		introduction = request.getParameter("introduction");
		gender = request.getParameter("gender");
		id = request.getParameter("id");
		contact = request.getParameter("contact");
		interestdata = request.getParameter("interest");
		System.out.println(id+"sadasd");
		
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
			String query = "update reader set name=?,date_of_birth=?,city=?,introduction=?,gender=? where reader_id=?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, name);
			Date date2 = null;
			try 
		    {  
//		      String datestr="06/27/2007";
		      DateFormat formatter; 
		      
		      formatter = new SimpleDateFormat("yyyy-MM-dd");
		      date2 = (Date)formatter.parse(date); 
		      
		    } 
		    catch (Exception e)
		    {
		    	System.out.println(e.getMessage());
		    }
			pstmt.setDate(2, new java.sql.Date(date2.getTime()));
			System.out.println(new java.sql.Date(date2.getTime()));
			pstmt.setString(3, city);
			pstmt.setString(4, introduction);
			pstmt.setString(5, gender);
			pstmt.setString(6,id);
			pstmt.executeUpdate();
			String conns = "";
			for(int i = 0;i < contact.length(); i++){
				
				if(contact.charAt(i) == ','){
					String query1 = "insert into contact_number values (?,?)";
					pstmt = conn.prepareStatement(query1);
					pstmt.setString(1, id);
					pstmt.setString(2, conns);
					pstmt.executeUpdate();
					conns = "";
				}
				else{
					conns+=contact.charAt(i);
				}
				
				
			}

			String conns1 = "";
			String query2 = "delete from interests where reader_id=?";
			pstmt = conn.prepareStatement(query2);
			pstmt.setString(1, id);
			pstmt.executeUpdate();
			for(int i = 0;i < interestdata.length(); i++){
				
				if(interestdata.charAt(i) == ','){
					String query1 = "insert into interests values (?,?)";
					pstmt = conn.prepareStatement(query1);
					pstmt.setString(1, id);
					pstmt.setString(2, conns1);
					pstmt.executeUpdate();
					conns1 = "";
				}
				else{
					conns1+=interestdata.charAt(i);
				}
				
				
			}

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
		// Flush out with the json object
		out.print(returnObject);
		out.flush();
		out.close();
		
	}

}
