package user_manager;

import java.io.IOException;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.*;



import DBTool.DBManager;

public class function_classify extends HttpServlet{
	public void doGet (HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		try {
			DBManager dbm = new DBManager ();
			String type = request.getParameter("req_type");
			
			HttpSession session = request.getSession();
			String exp_name = session.getAttribute("exp_name").toString();
			//System.out.println(exp_name);
			
			if (type.equals("get_undef"))
			{
				JSONArray undef = new JSONArray();
				
			//	String sql = "SELECT DISTINCT(name) from "+exp_name+"_profile WHERE NAME NOT IN (SELECT fun_name FROM func_predef)";
				String sql = "SELECT * FROM `"+exp_name+"_def` WHERE user_classified_id='4'";
				ArrayList res = dbm.executeSql(sql);
				
				for (int i = 0; i < res.size(); i++)
				{
					JSONObject temp = new JSONObject ();
					HashMap hash=(HashMap)res.get(i);
					
					temp.put("func_name", hash.get("FUN_NAME").toString());
					temp.put("color", hash.get("COLOR").toString());
					
					undef.put(temp);
				}
				out.write(undef.toString());
				System.out.println(undef.toString());
			}
			else if (type.equals("get_def"))
			{
				JSONArray def = new JSONArray();
				
				/*JSONArray Comm = new JSONArray();
				JSONArray IO = new JSONArray();
				JSONArray Sync = new JSONArray();
				JSONArray User_Func = new JSONArray();
				JSONArray GPU_tran = new JSONArray();
				JSONArray GPU_sync = new JSONArray();
				JSONArray OpenMP = new JSONArray();
				JSONArray CUDA_Other = new JSONArray();
				JSONArray MPI_Other = new JSONArray();*/
				
				//String sql = "SELECT fun_name, color, user_classified_id FROM func_predef WHERE fun_name IN (SELECT DISTINCT(name) FROM "+exp_name+"_profile)";
				//System.out.println(sql);
				String sql = "SELECT * FROM `"+exp_name+"_def` WHERE user_classified_id!='4'";
				
				ArrayList res = dbm.executeSql(sql);
				
				for (int i=0; i < res.size(); i++)
				{
					JSONObject temp = new JSONObject ();
					HashMap hash=(HashMap)res.get(i);
					
					String id = hash.get("USER_CLASSIFIED_ID").toString();
					
					temp.put("id", id);
					temp.put("func_name", hash.get("FUN_NAME").toString());
					temp.put("color", hash.get("COLOR").toString());
					
					/*if (id.equals("1"))
						Comm.put(temp);
					else if (id.equals("2"))
						IO.put(temp);
					else if (id.equals("3"))
						Sync.put(temp);
					else if (id.equals("4"))
						User_Func.put(temp);
					else if (id.equals("5"))
						GPU_tran.put(temp);
					else if (id.equals("6"))
						GPU_sync.put(temp);
					else if (id.equals("7"))
						OpenMP.put(temp);
					else if (id.equals("9"))
						CUDA_Other.put(temp);
					else if (id.equals("10"))
						MPI_Other.put(temp);*/
					
					def.put(temp);
				}
				
				/*def.put(Comm);
				def.put(IO);
				def.put(Sync);
				def.put(User_Func);
				def.put(GPU_tran);
				def.put(GPU_sync);
				def.put(OpenMP);
				def.put(CUDA_Other);
				def.put(MPI_Other);*/
				
				out.write(def.toString());
				System.out.println(def.toString());
			}
			else if (type.equals("get_classify"))
			{
				JSONObject classify = new JSONObject();
				String sql = "SELECT class_id, class_name FROM `"+exp_name+"_classify` WHERE class_id!='4'";
				
				ArrayList res = dbm.executeSql(sql);
				for (int i = 0; i < res.size(); i++)
				{
					//JSONObject temp = new JSONObject ();
					HashMap hash=(HashMap)res.get(i);
					//temp.put(hash.get("CLASS_ID").toString(), hash.get("CLASS_NAME").toString());
					//classify.put(temp);
					//classify.put(hash.get("CLASS_NAME").toString());
					classify.put(hash.get("CLASS_ID").toString(), hash.get("CLASS_NAME").toString());
				}
				out.write(classify.toString());
				System.out.println(classify.toString());
			}
			else if (type.equals("add_cat"))
			{
				String cat_name = request.getParameter("cat_name");
				String cat_id = request.getParameter("cat_id");
				String cat_color = request.getParameter("cat_color");
				JSONObject ret = new JSONObject ();
				
				String sql = "SELECT * FROM `"+exp_name+"_classify` WHERE class_name = '"+cat_name+"'";
				ArrayList res1 = dbm.executeSql(sql);
				sql = "SELECT * FROM `"+exp_name+"_classify` WHERE class_color = '"+cat_color+"'";
				ArrayList res2 = dbm.executeSql(sql);
				sql = "SELECT * FROM `"+exp_name+"_classify` WHERE class_id = "+cat_id;
				ArrayList res3 = dbm.executeSql(sql);
				
				if (res1.size() != 0)
					ret.put("result", "cat_exist");
				else if (res2.size() != 0)
					ret.put("result", "color_exist");
				else if (res3.size() != 0)
					ret.put("result", "id_exist");
				else
				{
					sql = "INSERT INTO `"+exp_name+"_classify` (class_id, class_name, class_color) VALUES ("+cat_id+", '"+cat_name+"', '"+cat_color+"')";
					if (dbm.executeUpdate(sql) == true)
						ret.put("result", "success");
					else
						ret.put("result", "false");
				}
				System.out.println(ret.toString());
				out.write(ret.toString());
			}
			else if (type.equals("change_color"))
			{
				String func_name = request.getParameter("func_name");
				String color = request.getParameter("color");
				
				dbm.executeUpdate("UPDATE `"+exp_name+"_def` SET color='"+color+"' WHERE fun_name='"+func_name+"'");
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
			String func_name;
			String func_color;
			String func_id;
			DBManager dbm = new DBManager ();
			
			HttpSession session = request.getSession();
			String exp_name = session.getAttribute("exp_name").toString();
			
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
				func_color = temp.get("color").toString();
				func_id = temp.get("usr_classified_id").toString();
				
				dbm.executeUpdate("UPDATE `"+exp_name+"_def` SET user_classified_id = '"+func_id+"',color='"+func_color+"' WHERE fun_name ='"+func_name+"'");
				/*System.out.println(temp.get("func_name").toString());*/
			}			
		}
		catch (Exception e) {
            System.err.println(e.toString());
        }
	}
}
