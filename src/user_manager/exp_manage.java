package user_manager;

import java.io.IOException;
import java.sql.SQLException;
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

public class exp_manage extends HttpServlet{
	public void doGet (HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		
		try{
			DBManager dbm = new DBManager ();
			String type = request.getParameter("req_type");
			System.out.println(type);
			if (type.equals("get_setime"))
			{
				String sql = "select min(unix_timestamp(start)),max(unix_timestamp(stop)) from maintable";
				ArrayList res = new ArrayList ();		
				res = dbm.executeSql(sql);
				//System.out.println(res.size());
				HashMap hash = (HashMap)res.get (0);
				String s_time = hash.get("MIN(UNIX_TIMESTAMP(START))").toString ();
				String e_time = hash.get("MAX(UNIX_TIMESTAMP(STOP))").toString ();
				
				//JSON 
		        JSONArray se_time = new JSONArray();
		        JSONObject s_member = new JSONObject ();
		        JSONObject e_member = new JSONObject ();
		        
		        s_member.put ("start", s_time);
		        e_member.put ("end", e_time);
		        
				se_time.put (0, s_member);
				se_time.put (1, e_member);
				
				out.write(se_time.toString());
			}
			else if (type.equals("get_experiment"))
			{
				String sql = "select name, type, owner, unix_timestamp(start),unix_timestamp(stop),finished from maintable where unix_timestamp(start)>="+request.getParameter("sstart")+" and (unix_timestamp(stop)<="+request.getParameter("send")+ " or stop is NULL) order by start desc";
				ArrayList res = new ArrayList ();
				res = dbm.executeSql(sql);
				//String [] exp_info = new String [res.size()];
				System.out.println(sql);
				JSONArray exp_info = new JSONArray();
				System.out.println(res.size());
				for (int i = 0; i < res.size(); i++)
				{
					JSONObject temp = new JSONObject ();
					HashMap hash=(HashMap)res.get(i);
					
					temp.put("name", hash.get("NAME").toString());
					temp.put("type", hash.get("TYPE").toString().toUpperCase());
					temp.put("owner", hash.get("OWNER").toString());
					temp.put("start", hash.get("UNIX_TIMESTAMP(START)").toString());	
					if (hash.get("UNIX_TIMESTAMP(STOP)") != null)
						temp.put("stop", hash.get("UNIX_TIMESTAMP(STOP)").toString());
					else
						temp.put("stop", "0");
					
					if (hash.get("FINISHED")!= null)
						temp.put("finished", "1");
					else
						temp.put("finished", "0");
					//System.out.println(hash.get("NAME").toString());
					exp_info.put(i, temp);
				}
				out.write(exp_info.toString());
			}
			else if (type.equals("select_exp"))
			{
				JSONObject data = new JSONObject ();
				HttpSession session = request.getSession();
				String exp_name = request.getParameter("exp_name");
				int t;
				
				ArrayList res = new ArrayList();
				res = dbm.executeSql("SELECT table_name FROM information_schema.tables WHERE table_name = '"+exp_name+"_def';");
				
				if (res.size() == 0)
					Predef (exp_name);
				else 
				{
					res = dbm.executeSql("SELECT table_name FROM information_schema.tables WHERE table_name = '"+exp_name+"_classify';");
					if (res.size() == 0)
						Predef (exp_name);
				}

				session.setAttribute("exp_name", exp_name);
				
				res = dbm.executeSql("SELECT type FROM maintable WHERE name = '"+exp_name+"'");
				HashMap hash = (HashMap)res.get(0);
				session.setAttribute("exp_type", hash.get("TYPE").toString());
				
				res = dbm.executeSql("SELECT table_name FROM information_schema.tables WHERE table_name = '"+exp_name+"_proc';");
				if (res.size() != 0)
					res = dbm.executeSql("SELECT * FROM `"+exp_name+"_proc`;");
				
				if (res.size() == 0)
					t = 0;
				else
					t = 1;
				data.put("proc", t);
				session.setAttribute("proc", t);
				
				res = dbm.executeSql("SELECT table_name FROM information_schema.tables WHERE table_name = '"+exp_name+"_papi';");
				if (res.size() != 0)
					res = dbm.executeSql("SELECT * FROM `"+exp_name+"_papi`;");
				if (res.size() == 0)
					t = 0;
				else
					t = 1;
				data.put("papi", t);
				session.setAttribute("papi", t);
				
				res = dbm.executeSql("SELECT table_name FROM information_schema.tables WHERE table_name = '"+exp_name+"_comm';");
				if (res.size() != 0)
					res = dbm.executeSql("SELECT * FROM `"+exp_name+"_comm` WHERE eid='83' || eid='86' || eid='76' || eid='61' ||eid='71' || eid='81' ||eid='74';");
				if (res.size() == 0)
					t = 0;
				else
					t = 1;
				data.put("comm", t);
				session.setAttribute("comm", t);
				
				res = dbm.executeSql("SELECT table_name FROM information_schema.tables WHERE table_name = '"+exp_name+"_profile';");
				if (res.size() == 0)
					t = 0;
				else
					t = 1;
				data.put("profile", t);
				session.setAttribute("profile", t);
				
				res = dbm.executeSql("SELECT table_name FROM information_schema.tables WHERE table_name = '"+exp_name+"_tree';");
				if (res.size() == 0)
					t = 0;
				else 
					t = 1;
				data.put("tree", t);
				session.setAttribute("tree", t);
				
				out.write(data.toString());
			}
			else if (type.equals("pretreatment"))
			{
				JSONObject res = new JSONObject ();
				HttpSession session = request.getSession();
				String exp_name = request.getParameter("exp_name");
				String exp_type = session.getAttribute("exp_type").toString();
				trace2profile (exp_name, exp_type);								//trace 转 profile
				trace2tree (exp_name);											//trace 转tree
				session.setAttribute("profile", 1);
				session.setAttribute("tree", 1);
				res.put("result", "success");
				out.write(res.toString());
			}
			else if (type.equals("trace2profile"))
			{
				JSONObject res = new JSONObject ();
				HttpSession session = request.getSession();
				String exp_name = request.getParameter("exp_name");
				String exp_type = session.getAttribute("exp_type").toString();
				trace2profile (exp_name, exp_type);								//trace 转 profile
				session.setAttribute("profile", 1);
				res.put("result", "success");
				out.write(res.toString());
			}
			else if (type.equals("trace2tree"))
			{
				JSONObject res = new JSONObject ();
				HttpSession session = request.getSession();
				String exp_name = request.getParameter("exp_name");
				trace2tree (exp_name);											//trace 转tree
				session.setAttribute("tree", 1);
				res.put("result", "success");
				out.write(res.toString());
			}
			else if (type.equals("delete_exp"))
			{
				String exp_name = request.getParameter("exp_name");
				Delete_exp (exp_name);
				
			}
			else if (type.equals("reset_exp"))
			{
				JSONObject result = new JSONObject ();
				String exp_name = request.getParameter("exp_name");
				Predef (exp_name);
				result.put("result", "success");
				out.write(result.toString());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
            System.err.println(e.toString());
        }
        finally {
            out.close();
        }
	}
	
	
	//Predef
	public void Predef (String new_exp_name)
	{
		DBManager dbm = new DBManager ();
		try{
			ArrayList res = new ArrayList();
			int flag = 0;

			//profile table is much smaller than trace table, so if profile table exists, we set flag=1 and use profile table
			res = dbm.executeSql("SELECT table_name FROM information_schema.tables WHERE table_name = '"+new_exp_name+"_profile';");
			if (res.size() != 0)
				flag = 1;
			

			/*if (!old_exp_name.equals("NULL"))						//if old_exp is not null, delete the old temp table
			{
				dbm.executeUpdate("drop table if exists `"+old_exp_name+"_def`");
				dbm.executeUpdate("drop table if exists `"+old_exp_name+"_classify`");
			}*/
	
			
			res = dbm.executeSql("SELECT table_name FROM information_schema.tables WHERE table_name = '"+new_exp_name+"_trace';");
			if (res.size() == 0)									//if new_exp_name table does not exist , return
			{
				System.out.println("Table "+new_exp_name+"_trace doesn't exist!");
				return ;
			}
			else 
			{
				res = dbm.executeSql("SELECT DISTINCT name FROM `"+new_exp_name+"_trace` WHERE type = 7 ORDER BY time;");
				
				for (int i = 0, eid = 500; i < res.size(); i++)
				{
					dbm.executeUpdate("UPDATE `"+new_exp_name+"_trace` SET eid = "+eid+" WHERE name = '"+((HashMap)res.get(i)).get("NAME").toString() + "'");
					eid ++;
				}
			}
			
			
			String temp = "drop table if exists `"+new_exp_name+"_def`";
			dbm.executeUpdate(temp);
			dbm.executeUpdate("drop table if exists `"+new_exp_name+"_classify`");
			//dbm.executeUpdate("drop table if exists "+new_exp_name+"_undef_temp");
			
			//create def table
			String sql = "CREATE TABLE `"+new_exp_name+"_def` (id int AUTO_INCREMENT, fun_name text, color text, user_classified_id int(10), flag int(2),PRIMARY KEY (id))";
			dbm.executeUpdate(sql);
			
			if (flag == 0)
				sql = "SELECT fun_name, color, user_classified_id FROM func_predef WHERE fun_name IN (SELECT DISTINCT(name) FROM `"+new_exp_name+"_trace`)";
			else 
				sql = "SELECT fun_name, color, user_classified_id FROM func_predef WHERE fun_name IN (SELECT DISTINCT(name) FROM `"+new_exp_name+"_profile`)";
			
			res = dbm.executeSql(sql);
			for (int i = 0; i < res.size(); i++)
			{
				HashMap hash=(HashMap)res.get(i);
				sql = "INSERT into `"+new_exp_name+"_def`(fun_name, color, user_classified_id, flag) VALUES ('"+hash.get("FUN_NAME").toString()+"','"+hash.get("COLOR")+"','"+hash.get("USER_CLASSIFIED_ID")+"','1')";
				dbm.executeUpdate(sql);
			}
			/*String sql = "CREATE TABLE "+new_exp_name+"_def SELECT fun_name, color, user_classified_id FROM func_predef WHERE fun_name IN (SELECT DISTINCT(name) FROM "+new_exp_name+"_profile)";
			dbm.executeUpdate(sql);*/
			
			if (flag ==0)
				sql = "SELECT DISTINCT(name) from `"+new_exp_name+"_trace` WHERE NAME NOT IN (SELECT fun_name FROM func_predef)";
			else 
				sql = "SELECT DISTINCT(name) from `"+new_exp_name+"_profile` WHERE NAME NOT IN (SELECT fun_name FROM func_predef)";
			res = dbm.executeSql(sql);
			
			for (int i = 0; i < res.size(); i++)
			{
				HashMap hash=(HashMap)res.get(i);
				sql = "INSERT into `"+new_exp_name+"_def` (fun_name, color, user_classified_id, flag) VALUES ('"+hash.get("NAME").toString()+"','"+random_color()+"','4','1')";
				dbm.executeUpdate(sql);
			}
		
			
			//create classify_temp table
			sql = "CREATE TABLE `"+new_exp_name+"_classify` (id int AUTO_INCREMENT, class_id int, class_name text, class_color text, PRIMARY KEY (id))";
			//sql = "CREATE TABLE "+new_exp_name+"_classify SELECT * FROM user_classify WHERE class_id IN (SELECT user_classified_id FROM "+new_exp_name+"_def)";
			dbm.executeUpdate(sql);
			
			sql = "SELECT * FROM user_classify WHERE class_id IN (SELECT user_classified_id FROM `"+new_exp_name+"_def`)";
			res = dbm.executeSql(sql);
			
			for (int i = 0; i < res.size(); i++)
			{
				HashMap hash=(HashMap)res.get(i);
				sql = "INSERT into `"+new_exp_name+"_classify` (class_id, class_name, class_color) VALUES ('"+hash.get("CLASS_ID").toString()+"','"+hash.get("CLASS_NAME")+"','"+hash.get("CLASS_COLOR")+"')";
				dbm.executeUpdate(sql);
			}
		}
		catch (Exception e) {
            System.err.println(e.toString());
        }
	}
	
	//create random color
	public String random_color ()
	{
		int red,green,blue,color;
		Random random = new Random();
		red=Math.abs(random.nextInt())%255;
		green=Math.abs(random.nextInt())%255;
		blue=Math.abs(random.nextInt())%255;
			
		System.out.println(Integer.toHexString(blue*65535+green*255+red));
		return "#"+Integer.toHexString(blue*65535+green*255+red).toUpperCase();
	}
	
	private void trace2profile(String exp_name, String exp_type) throws Exception
	{
		DBManager dbm = new DBManager();

		dbm.executeUpdate("DROP PROCEDURE IF EXISTS `t2f_"+exp_name+"`");
		
		String procedure = new String ();
		if (!exp_type.equals("omp"))
		{
			procedure = "CREATE PROCEDURE `t2f_"+exp_name+"`()\n"+
					"BEGIN\n"+
					"DROP TABLE IF EXISTS `"+exp_name+"_profile`;\n"+
					"CREATE TABLE `"+exp_name+"_profile` (id INT AUTO_INCREMENT,pid INT, hostname text, eid INT, name text, sumTime BIGINT DEFAULT 0, cnt INT DEFAULT 0, PRIMARY KEY (id));\n"+
					"INSERT INTO `"+exp_name+"_profile` (pid,hostname,eid,name,sumTime,cnt) SELECT pid,hostname,eid,name,SUM(CASE WHEN finish=1 THEN -time ELSE time END),COUNT(finish)>>1 FROM `"+exp_name+"_trace`  GROUP BY pid,eid ORDER BY eid ;\n"+
					"END";
		}
		else
		{
			procedure  = "CREATE PROCEDURE IF EXISTS 't2f_"+ exp_name +"'"+
					"BEGIN\n"+
					"DROP TABLE IF EXISTS `"+exp_name+"_profile`;\n"+
					"END";
		}

		dbm.executeUpdate(procedure);
		
		dbm.executeUpdate("CALL `t2f_"+exp_name+"`");
		dbm.executeUpdate("DROP PROCEDURE IF EXISTS `t2f_"+exp_name+"`");
		
		System.out.println("trace to profile success");
	}
	
	private void trace2tree (String expName)
	{
		String tableName= expName+"_trace";
		String tableNodelistName = expName + "_nodelist";
		String resutlttable=expName + "_tree";

		try{
			DBManager db=new DBManager();

            //compute rank counts
			db.executeUpdate("DROP TABLE IF EXISTS resutlttable");
            String sql1="select distinct `" + tableNodelistName + "`.mpirank,`"+tableNodelistName+"`.pid,`"+tableNodelistName+"`.hostname from `"+tableName+"`,`"+tableNodelistName+"` where `" + tableName + "`.pid=`"+tableNodelistName+"`.pid and `"+ tableName + "`.hostname=`"+tableNodelistName+"`.hostname;";
            //System.out.println(sql1);          
            ArrayList list0 = new ArrayList ();
            list0 = db.executeSql(sql1);

            sql1= "drop table if exists `"+ resutlttable+"`;";
            db.executeUpdate(sql1); 
            sql1="create table `" + resutlttable +"`(`id` int(11) NOT NULL auto_increment," +
            		" `name` varchar(255) default NULL,"+
            		" `time` float default NULL,"+
            		" `rank` int(10) default NULL,"+
            		" `count` int(11) default NULL," + 
            		" `father` int(11) default NULL," + 
            		" `level` int(11) default NULL,"+
            		" `tree_relation` varchar(255) default NULL,"+
            		"  `eid` int(11) default NULL,"+
            		" PRIMARY KEY  (`id`));"; 
            db.executeUpdate(sql1); 
            String[] ranknum = new String [list0.size()]; 			//an array to contain ranks 
            String[] pidnum = new String[list0.size()];
            String[] hostnamenum = new String[list0.size()];
            for(int i=0;i<list0.size();i++)
            {
            	HashMap hash=(HashMap)list0.get(i);
	            String rank=hash.get("MPIRANK").toString();
	            String pid= hash.get("PID").toString();
	            String hostname= hash.get("HOSTNAME").toString();
	        	ranknum[i]=rank;
	        	pidnum[i] = pid;
	        	hostnamenum[i]=hostname;
            }
      
	        ArrayList tree_stack=new ArrayList();
	        for (int i=0;i<list0.size();i++)
	        { 
	        	String rank=ranknum[i];
	        	String pid=pidnum[i];
	        	String hostname = hostnamenum[i];
	        	String sql2="select  eid, time, finish,name from `"+tableName+"` where pid='"+pid+"' and hostname = '"+hostname+"' order by time";
	        	ArrayList list1 = new ArrayList ();
	        	list1=db.executeSql(sql2); 							//选一个进程的信息
	        	for(int j=0;j<list1.size();j++)      				//遍历这个进程的数据 j
	        	{
	        		HashMap hash=(HashMap)list1.get(j);
            		int eid=Integer.parseInt(hash.get("EID").toString());
            		String name=hash.get("NAME").toString();
            		int finish=Integer.parseInt(hash.get("FINISH").toString());
            		int count=0;
              		int father=0;
            		if(finish==1)
            		{
            			tree_stack.add(hash); 
            			String tree_relation="";
            			for(int k=0; k<tree_stack.size();k++)
            			{
            				 HashMap hash2 = (HashMap)tree_stack.get(k);
            				 tree_relation = tree_relation + "." + hash2.get("EID");
            				 if(k == tree_stack.size()-1 && k > 0)
            				 {
            					 hash2 = (HashMap)tree_stack.get(k-1);
            					 father= Integer.parseInt(hash2.get("EID").toString());
            				 }
                    	 }
            			 //System.out.println(tree_relation);
            			 String sql3="select  * from `"+resutlttable+"` where rank='"+rank+"' and eid='"+eid+"' and father='"+father+"'";
            			 //System.out.println(sql3);
            			 ArrayList list3=new ArrayList ();
            			 list3=db.executeSql(sql3);
            			 //if(list3.size()==0)
            			 {
            				 String insertsql="insert into `"+resutlttable+"`(time,rank,level,father,tree_relation,eid,count,name) values ('0', '"+rank+"','"+tree_stack.size()+"','"+father+"','"+tree_relation+"','"+eid+"','"+count+"','"+name+"')";
            				 //System.out.println(insertsql);
            				 db.executeUpdate(insertsql);
            			 }
            		 }
            		else if(finish==2){
            			 double time_dev=0;
                 		 HashMap hash_stack=(HashMap)tree_stack.get(tree_stack.size()-1);
                 		 double time2=Double.parseDouble(hash.get("TIME").toString());
                 		 double time1=Double.parseDouble(hash_stack.get("TIME").toString());
                 		 time_dev= time2-time1;	    
                 		 String sql3="select  * from `"+resutlttable+"` where rank='"+rank+"'and eid='"+eid+"'";
                 		 ArrayList list3=new ArrayList ();
                 		 list3=db.executeSql(sql3);
                 		 hash_stack = (HashMap)list3.get(0);
                 		 count = Integer.parseInt(hash_stack.get("COUNT").toString())+1;
                 		 String insertsql="update `"+resutlttable+"` set time='"+time_dev+"',count='"+count+"' where rank='"+ rank +"' and eid='"+eid+"'";

                 	 	db.executeUpdate(insertsql);	
                 	 	tree_stack.remove(tree_stack.size()-1);
            		 }
            	 }
             }
             System.out.println("successful!");  
         }catch(Exception e){
             e.printStackTrace();
         }
	}
	
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			DBManager dbm = new DBManager ();
			String exp_name;
			HttpSession session = request.getSession();
			
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
			System.out.println(map.toString());
			JSONArray list = new JSONArray (json);
			
			for (int i = 0; i < list.length(); i++)
			{
				JSONObject temp = list.getJSONObject(i);
				exp_name = temp.get("exp_name").toString();
				
				Delete_exp (exp_name);
			}			
		}
		catch (Exception e) {
            System.err.println(e.toString());
        }
	}
	
	public void Delete_exp (String exp_name) throws SQLException
	{
		DBManager dbm = new DBManager ();
		String sql = "delete from maintable where name='"+exp_name+"'";
		dbm.executeUpdate(sql);
		
		dbm.executeUpdate("drop table if exists `"+exp_name+"_classify");
		dbm.executeUpdate("drop table if exists `"+exp_name+"_comm");
		dbm.executeUpdate("drop table if exists `"+exp_name+"_cupti");
		dbm.executeUpdate("drop table if exists `"+exp_name+"_def");
		dbm.executeUpdate("drop table if exists `"+exp_name+"_io");
		dbm.executeUpdate("drop table if exists `"+exp_name+"_nodelist");
		dbm.executeUpdate("drop table if exists `"+exp_name+"_papi");
		dbm.executeUpdate("drop table if exists `"+exp_name+"_proc");
		dbm.executeUpdate("drop table if exists `"+exp_name+"_profile");
		dbm.executeUpdate("drop table if exists `"+exp_name+"_trace");
		dbm.executeUpdate("drop table if exists `"+exp_name+"_tree");
	}
}
