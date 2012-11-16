package user_manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.*;

import DBTool.DBManager;
public class function_display extends HttpServlet{
	public void doGet (HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		
		try {
			DBManager dbm = new DBManager ();
			String type = request.getParameter("req_type");
			System.out.println(type);
			HttpSession session = request.getSession();
			String exp_name = session.getAttribute("exp_name").toString();
			if (type.equals("get_dis"))
			{
				JSONObject dis_data = new JSONObject ();
				JSONArray dis_list = new JSONArray ();
				JSONArray undis_list = new JSONArray ();
				
				ArrayList res = dbm.executeSql("SELECT fun_name FROM `"+exp_name+"_def` WHERE flag = 1;");
				for (int i = 0; i < res.size(); i++)
				{
					HashMap hash = (HashMap)res.get(i);
					dis_list.put(hash.get("FUN_NAME").toString());
				}
				
				res = dbm.executeSql("SELECT fun_name FROM `"+exp_name+"_def` WHERE flag = 0;");
				for (int i = 0; i <res.size(); i++)
				{
					HashMap hash = (HashMap)res.get(i);
					undis_list.put(hash.get("FUN_NAME").toString());
				}
				dis_data.put("dis", dis_list);
				dis_data.put("undis", undis_list);
				System.out.println(dis_data.toString());
				out.write(dis_data.toString());
			}
		}
		catch (Exception e) {
            System.err.println(e.toString());
        }
        finally {
            out.close();
        }
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		try {
			DBManager dbm = new DBManager ();
			
			HttpSession session = request.getSession();
			String exp_name = session.getAttribute("exp_name").toString();
			String func_name;
			String flag;
			Map<String, String[]> map = request.getParameterMap(); 
			Set<Entry<String, String[]>> set = map.entrySet(); 
			String json="";
			Iterator<Entry<String, String[]>> it = set.iterator();  
			
	        while (it.hasNext()) {  
	            Entry<String, String[]> entry = it.next();  
	  
	            //System.out.println("KEY:"+entry.getKey());  
	            for (String i : entry.getValue()) {  
	                json = json + i;
	            }  
	        }  
			//System.out.println(map.toString());
			JSONArray list = new JSONArray (json);
			for (int i = 0; i < list.length(); i++)
			{
				JSONObject temp = list.getJSONObject(i);
				func_name = temp.get("func_name").toString();
				flag = temp.getString("flag").toString();
				dbm.executeUpdate("UPDATE `"+exp_name+"_def` SET flag = "+flag+" WHERE fun_name ='"+func_name+"'");
				//System.out.println(temp.get("func_name").toString());
			}		
		}
		catch (Exception e) {
            System.err.println(e.toString());
        }
	}
}
