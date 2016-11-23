		


import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 * Servlet implementation class UploadBookPhoto
 */
@WebServlet("/UploadBookPhoto")
@MultipartConfig(maxFileSize = 16177215)
public class UploadBookPhoto extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadBookPhoto() {
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
		System.out.println("bookphoto");

		try ( 
				// Trying to connect
				Connection conn = DriverManager.getConnection(connString,username,password);
				Statement stmt = conn.createStatement();
			)
			{
			response.setContentType("image/jpeg");
			String bookID = request.getParameter("id");
			PreparedStatement ps = conn.prepareStatement("SELECT Cover_pic_img FROM book WHERE Book_ID = ?");
			ps.setString(1, bookID);
			ResultSet rs = ps.executeQuery();
			byte[] imgBytes = null;
			if (rs != null) {
			    while (rs.next()) {
			        imgBytes = rs.getBytes(1);
			        // use the data in some way here
			    }
			    rs.close();
			}
//			System.out.println(imgBytes);
			
			ps.close();
			response.setContentLength(imgBytes.length);
			response.getOutputStream().write(imgBytes);
			
			}
		catch(Exception e){
			
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String connString = ServletInfo.connString;
		String username = ServletInfo.userName;
		String password = ServletInfo.passWord;
		System.out.println("bookphotopost");

		try ( 
				// Trying to connect
				Connection conn = DriverManager.getConnection(connString,username,password);
				Statement stmt = conn.createStatement();
			)
			{
//			String id = request.getParameter("id");
//			System.out.println(id);
			Part filePart = request.getPart("photo1");
			InputStream inputStream = null;
			if (filePart != null) {
	            // prints out some information for debugging
	            System.out.println(filePart.getName());
	            System.out.println(filePart.getSize());
	            System.out.println(filePart.getContentType());
	             
	            // obtains input stream of the upload file
	            inputStream = filePart.getInputStream();
	            System.out.println(inputStream.toString());
	            String query = "Update Book set Cover_Pic_name=?,Cover_Pic_img=? where Book_ID=?";
	            PreparedStatement pstmt = conn.prepareStatement(query);
	            pstmt.setString(1, filePart.getName());
	            pstmt.setString(3,"8");
	            pstmt.setBinaryStream(2,inputStream,filePart.getSize());
	            pstmt.executeUpdate();      
	        }
			else{
				System.out.println("error");
			}
			}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
	}

}