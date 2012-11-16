package user_manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.*;

import DBTool.DBManager;

public class user_manage extends HttpServlet{
	public void doGet (HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		try {
			DBManager dbm = new DBManager ();
			String type = request.getParameter("req_type");
			
			if (type.equals("get_user"))
			{
				String sql = "select * from users";
				ArrayList res = dbm.executeSql(sql);
				
				JSONArray user_info = new JSONArray();	
				for (int i = 0; i < res.size(); i++)
				{
					JSONObject temp = new JSONObject ();
					HashMap hash = (HashMap)res.get(i);
					
					temp.put("name", hash.get("NAME").toString());
					temp.put("password", hash.get("PASSWORD").toString());
					temp.put("rights", hash.get("RIGHTS").toString());
					
					user_info.put(i, temp);
				}
				out.write(user_info.toString());
			//	System.out.println(user_info.toString());
			}
			else if (type.equals("del_user"))
			{
				String user_name = request.getParameter("user_name");
				String sql = "delete from users where name = '"+user_name+"'";
				
				JSONObject res = new JSONObject ();
				if (dbm.executeUpdate(sql) == true)
					res.put("result", "success");
				else 
					res.put("result", "fail");			
				out.write(res.toString());
			//	System.out.println(res.toString());	
			}
			else if (type.equals("add_user"))
			{
				String user_name = request.getParameter("name");
				String user_passwd = request.getParameter("password");
				String user_rights = request.getParameter("rights");
				String sql = "Select * from users where name='"+user_name+"'";
				JSONObject res = new JSONObject ();
				if (dbm.executeSql(sql).size() == 0)
				{
					sql = "INSERT INTO users (name, PASSWORD, rights) VALUES ('"+user_name+"','"+user_passwd+"','"+user_rights+"')";
					if (dbm.executeUpdate(sql) == true)
						res.put("result", "success");
					else 
						res.put("result", "failed");
				}
				else 
					res.put("result", "failed");
				out.write(res.toString());
				System.out.println(res.toString());
			}
		}
		
		catch (Exception e) {
            System.err.println(e.toString());
        }
        finally {
            out.close();
        }
	}
	
	public void doPost (HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		try {
			DBManager dbm = new DBManager ();
			String type = request.getParameter("req_type");
			if (type.equals("modify"))
			{
				String user = request.getParameter("name");
				String passwd = request.getParameter("password");
				
				String sql = "update users set password = '"+passwd+"' where name = '"+user+"'";
				dbm.executeUpdate(sql);
			}
			
		}
		catch (Exception e) {
	        System.err.println(e.toString());
	    }
	    finally {
	        out.close();
	    }
	}
	
}
