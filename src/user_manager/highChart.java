package user_manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.*;

import user_manager.highChart.omp_thread;

import DBTool.DBManager;

public class highChart extends HttpServlet{
	public class omp_thread
	{
		int omp_level;						
		int omp_rank;
		int omp_p_rank;
	}
	
	@SuppressWarnings("rawtypes")
	public void doGet (HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		try {
			DBManager dbm = new DBManager ();
			String type = request.getParameter("req_type");
			HttpSession session = request.getSession();
			String exp_name = session.getAttribute("exp_name").toString();
			System.out.println(type);
			System.out.println(exp_name);
			
			if (type.equals("get_profile_data"))									//get profile data
			{
				JSONArray profile_data = new JSONArray ();	
				ArrayList res = new ArrayList ();
				res = dbm.executeSql("SELECT  DISTINCT `"+exp_name+"_profile`.pid, mpirank FROM `"+exp_name+"_profile` , `"+exp_name+"_nodelist` WHERE `"+exp_name+"_nodelist`.pid = `"+exp_name+"_profile`.pid  ORDER BY mpirank");
				String pid[] = new String [res.size()];
				
				JSONArray pid_list = new JSONArray ();
				JSONObject pid_obj = new JSONObject ();
				for (int i=0; i < res.size(); i++)
				{
					HashMap hash = (HashMap)res.get(i);
					pid [i] = hash.get("PID").toString();
					pid_list.put(pid[i]);
				}
				pid_obj.put("pid", pid_list);
				profile_data.put(pid_obj);
				
				res = dbm.executeSql("SELECT DISTINCT name, color FROM `"+exp_name+"_profile`,`"+exp_name+"_def` where name=fun_name AND `"+exp_name+"_def`.flag = 1 ORDER BY eid desc;");
				int e_length = res.size(); 

				String[] func = new String [e_length];
				String[] color = new String [e_length];
				for (int i = 0; i < e_length; i++)
				{
					HashMap hash = (HashMap)res.get(i);
					func[i] = hash.get("NAME").toString();
					color[i] = hash.get("COLOR").toString();
				}
				
				JSONArray data_list = new JSONArray ();
				JSONObject data_obj = new JSONObject ();
				for (int i = 0; i < e_length; i++)
				{
					res = dbm.executeSql("SELECT  `"+exp_name+"_profile`.pid, mpirank, sumTime FROM `"+exp_name+"_profile` , `"+exp_name+"_nodelist` WHERE name='"+func[i]+"' AND `"+exp_name+"_nodelist`.pid = `"+exp_name+"_profile`.pid  ORDER BY mpirank");
					JSONArray time_list = new JSONArray ();
					JSONObject temp = new JSONObject ();
					
					if (res.size() == pid.length)
					{
						for (int j = 0; j < res.size(); j++)
						{
							HashMap hash = (HashMap)res.get(j);
							time_list.put(hash.get("SUMTIME").toString());
						}
					}
					else 
					{
						for (int j = 0, k = 0; j < res.size();)
						{
							if (((HashMap)res.get(j)).get("PID").toString().equals(pid[k]))
							{
								time_list.put(((HashMap)res.get(j)).get("SUMTIME").toString());
								++j;
								++k;
							}
							else
							{
								time_list.put("0");
								++k;
							}
						}
					}
					temp.put("func_name", func[i]);
					temp.put("color", color[i]);
					temp.put("data", time_list);
					data_list.put(temp);
				}
				data_obj.put("data", data_list);
				profile_data.put(data_obj);
				
				out.write(profile_data.toString());
			}
			else if (type.equals("get_trace_data"))									//get trace data
			{
				String exp_type = session.getAttribute("exp_type").toString();
	
				//if this is an omp experiment ,the experiment data need to be proceeded separately
				if (exp_type.equals("omp"))
				{
					JSONArray traceData = new JSONArray ();
					ArrayList res = new ArrayList ();
					int threadsNum;
					long sTime = 0;

					//get the max level of omp nest
					int maxLevel = 0;
					res = dbm.executeSql("SELECT MAX(omp_level) as max_level FROM `"+exp_name+"_trace`;");
					maxLevel = Integer.parseInt(((HashMap)res.get(0)).get("MAX_LEVEL").toString());
					
					res = dbm.executeSql("SELECT DISTINCT omp_level, omp_rank, omp_p_rank FROM `"+exp_name+"_trace` WHERE omp_rank!=-1 ORDER BY omp_level, omp_p_rank, omp_rank;");
					threadsNum = res.size();
					
					int flag [] = new int [threadsNum];
					omp_thread[] threads_temp = new omp_thread [threadsNum];
					omp_thread[] threads = new omp_thread [threadsNum];

					for (int i = 0; i < threadsNum; ++i)
					{
						HashMap hash = (HashMap) res.get(i);
						
						threads_temp [i] = new omp_thread ();
						threads_temp [i].omp_level = Integer.parseInt(hash.get("OMP_LEVEL").toString());
						threads_temp [i].omp_rank = Integer.parseInt(hash.get("OMP_RANK").toString());
						threads_temp [i].omp_p_rank = Integer.parseInt(hash.get("OMP_P_RANK").toString());
					}
					
					//get omp threads through a Depth-First Traversal
					JSONArray threadData = new JSONArray ();
					JSONObject threadObj = new JSONObject ();
					DFT (threads, flag, threads_temp, -1, -1, maxLevel, 0);
					for (int i = 0; i < threadsNum; ++i)
					{
						String temp = threads[i].omp_level +":" + threads[i].omp_rank;
						threadData.put(temp);
					//	System.out.println(temp);
					}
					threadObj.put("thread", threadData);
					threadObj.put("num", threadsNum);
					traceData.put(threadObj);
					
					//get the start time of this experiment
					res = dbm.executeSql("SELECT MIN(time) as MIN FROM `"+exp_name+"_trace` ;");
					sTime = Long.parseLong(((HashMap)res.get(0)).get("MIN").toString());
					
					//get task id 
					res = dbm.executeSql("SELECT DISTINCT task_id FROM `"+exp_name+"_trace` WHERE task_id!='ffffffffffffffff';");
					String taskId[] = new String [res.size()];
					int taskNum = res.size();
					for (int i = 0; i < taskNum; ++i)
						taskId [i] = ((HashMap)res.get(i)).get("TASK_ID").toString();
					
					for (int i = 0; i < taskNum; ++i)
					{
						JSONObject taskObj = new JSONObject ();
						JSONArray data = new JSONArray ();
						res = dbm.executeSql("SELECT * FROM `"+exp_name+"_trace` WHERE task_id='"+taskId[i]+"';");
						for (int j = 0; j < res.size(); ++j)
						{
							JSONObject temp = new JSONObject ();
							
							HashMap hash = (HashMap)res.get(j);
							long time = Long.parseLong(hash.get("TIME").toString());
							int omp_rank = Integer.parseInt(hash.get("OMP_RANK").toString());
							int omp_level = Integer.parseInt(hash.get("OMP_LEVEL").toString());
							int omp_p_rank = Integer.parseInt(hash.get("OMP_P_RANK").toString());
							int height = getHeight (threads, omp_level, omp_rank, omp_p_rank);
							
							temp.put("x", (time - sTime)/1000);
							temp.put("y", height);
							data.put(temp);
						}
						taskObj.put("data", data);
						taskObj.put("task_id", taskId[i]);
						traceData.put(taskObj);
					}
					System.out.println(traceData.toString());
					out.write(traceData.toString());
					/*for (int i = 0; i < threadsNum; ++i)
					{
						res = dbm.executeSql("SELECT * FROM `"+exp_name+"_trace` WHERE omp_level = "+threads[i].omp_level+" AND omp_rank = "+threads[i].omp_rank+" AND omp_p_rank = "+threads[i].omp_p_rank+";");
						
						for (int j = 0; j < res.size(); ++j)
						{
							JSONObject temp1 = new JSONObject ();
							JSONObject temp2 = new JSONObject ();
							JSONObject temp3 = new JSONObject ();
							JSONObject null_data = null;
							long t1 = 0, t2 = 0;
							HashMap hash = (HashMap)res.get(j);
							
							String task_id = hash.get("TASK_ID").toString();
							
							t1 = Long.parseLong(hash.get("TIME").toString());
							temp1.put("x", (t1 - sTime)/1000);
							temp1.put("y", threadsNum - i);
							
							t2 = Long.parseLong(hash.get("TIME").toString());
							temp2.put("x", (t2 - sTime)/1000);
							temp2.put("y", threadsNum - i);
							
							temp3.put("x", (t2 - sTime)/1000);
							temp3.put("y", null_data);
						}
					}*/
				}
				else
				{
					JSONArray trace_data = new JSONArray ();
					ArrayList res = new ArrayList ();
					
					res = dbm.executeSql("SELECT DISTINCT `"+exp_name+"_trace`.pid, `"+exp_name+"_nodelist`.mpirank FROM `"+exp_name+"_trace`, `"+exp_name+"_nodelist` WHERE `"+exp_name+"_trace`.pid = `"+exp_name+"_nodelist`.pid AND `"+exp_name+"_trace`.hostname = `"+exp_name+"_nodelist`.hostname ORDER BY `"+exp_name+"_nodelist`.mpirank");
					String pid [] = new String [res.size()];
					String tid [][] = new String [res.size()][];
					
					JSONArray pid_list = new JSONArray ();
					JSONArray tid_list = new JSONArray ();
					JSONArray rank_list = new JSONArray ();
					JSONArray omp_rank_list = new JSONArray ();
					
					HashMap<String,Integer> pid_map = new HashMap<String, Integer>();
					HashMap<String,Integer> tid_map = new HashMap<String, Integer>();
					
					JSONObject pid_obj = new JSONObject ();
					JSONObject tid_obj = new JSONObject ();
					int height = 0;
					int totalNum = 0;
					for (int i=0; i < res.size(); i++)
					{
						HashMap hash = (HashMap)res.get(i);
					
						pid [i] = hash.get("PID").toString();
						pid_list.put(pid[i]);
						rank_list.put(hash.get("MPIRANK").toString());
						pid_map.put(pid[i], height);
						height++;
						
						if (exp_type.equals("ompi"))
						{
							JSONArray omp_rank_temp = new JSONArray ();
							JSONArray tid_temp = new JSONArray();
							ArrayList res_temp = new ArrayList ();
							res_temp = dbm.executeSql("SELECT DISTINCT omp_rank, tid FROM `"+exp_name+"_trace` WHERE omp_rank != -1 AND pid = "+hash.get("PID").toString()+" ORDER BY omp_rank;");
							tid[i] = new String [res_temp.size()];
							for (int j = 0; j < res_temp.size(); j++)
							{
								HashMap hash_temp = (HashMap)res_temp.get(j);
								tid[i][j] = hash_temp.get("TID").toString();
								tid_map.put (tid[i][j], height);
								omp_rank_temp.put (hash_temp.get("OMP_RANK").toString());
								tid_temp.put (tid[i][j]);
								height++;
							}
							omp_rank_list.put (omp_rank_temp);
							tid_list.put (tid_temp);
						}
					}
					
					totalNum = res.size();
					res = dbm.executeSql("SELECT MIN(time) as min, MAX(time) as max FROM `"+exp_name+"_trace`, `"+exp_name+"_def` WHERE `"+exp_name+"_trace`.name = `"+exp_name+"_def`.fun_name AND `"+exp_name+"_def`.flag = 1;");
					
					long stime = Long.parseLong(((HashMap)res.get(0)).get("MIN").toString());
					long etime = Long.parseLong(((HashMap)res.get(0)).get("MAX").toString());
					long totalTime = etime - stime;
					
					if (exp_type.equals("mpi"))
					{
						pid_obj.put("pid", pid_list);
						pid_obj.put("rank", rank_list);
						pid_obj.put("num", totalNum);
						pid_obj.put("totalTime", totalTime);
						trace_data.put(pid_obj);
					}				
					if (exp_type.equals("ompi"))
					{
						tid_obj.put("num", height);
						tid_obj.put("omp_rank", omp_rank_list);
						tid_obj.put("tid", tid_list);
						tid_obj.put("totalTime", totalTime);
						trace_data.put(tid_obj);
					}
					else if (exp_type.equals("cmpi"))
					{
						pid_obj.put("pid", pid_list);
						pid_obj.put("rank", rank_list);
						pid_obj.put("num", totalNum);
						pid_obj.put("totalTime", totalTime);
						trace_data.put(pid_obj);
					}
					
					height--; 
					res = dbm.executeSql("SELECT DISTINCT name, color FROM `"+exp_name+"_trace`,`"+exp_name+"_def` where name=fun_name AND `"+exp_name+"_def`.flag = 1 ORDER BY eid ;");
					int e_length = res.size(); 

					String[] func = new String [e_length];
					String[] color = new String [e_length];
					for (int i = 0; i < e_length; i++)
					{
						HashMap hash = (HashMap)res.get(i);
						func[i] = hash.get("NAME").toString();
						color[i] = hash.get("COLOR").toString();
					}
					
					//Get Process trace data
					for (int i = 0; i < e_length; i++)
					{
						if(exp_type.equals("mpi"))
							res = dbm.executeSql("SELECT pid, time FROM `"+exp_name+"_trace` WHERE name='"+func[i]+"' ORDER BY pid;");
						else if (exp_type.equals("ompi"))
							res = dbm.executeSql("SELECT pid, tid, omp_rank, time FROM `"+exp_name+"_trace` WHERE name='"+func[i]+"' ORDER BY pid;");
						else if (exp_type.equals("cmpi"))
							res = dbm.executeSql("SELECT pid, time FROM `"+exp_name+"_trace` WHERE name='"+func[i]+"' ORDER BY pid;");
						JSONArray data_temp = new JSONArray ();
						JSONObject obj_temp = new JSONObject ();
						
						for (int j = 0; j < res.size(); j++)
						{
							JSONObject temp1 = new JSONObject ();
							JSONObject temp2 = new JSONObject ();
							JSONObject temp3 = new JSONObject ();
							JSONObject null_data = null;
							HashMap hash = (HashMap)res.get(j);
							String pid_temp = hash.get("PID").toString();
							String tid_temp = "0";
							String omp_rank = "-1";
							
							if (exp_type.equals("ompi"))
							{
								tid_temp = hash.get("TID").toString();
								omp_rank = hash.get("OMP_RANK").toString();
							}
							long stime_temp = Long.parseLong(hash.get("TIME").toString());
							
							j++;
							hash = (HashMap)res.get(j);
							long etime_temp = Long.parseLong(hash.get("TIME").toString());
							
							temp1.put("x", (stime_temp - stime)/1000.0);
							
							if (exp_type.equals("ompi") && !omp_rank.equals("-1"))
								temp1.put("y", height - tid_map.get(tid_temp));
							else
								temp1.put("y", height - pid_map.get(pid_temp));
							

							
							temp2.put("x", (etime_temp - stime)/1000.0);
							
							if (exp_type.equals("ompi") && !omp_rank.equals("-1"))
								temp2.put("y", height - tid_map.get(tid_temp));
							else
								temp2.put("y", height - pid_map.get(pid_temp));
			
							temp1.put("time", (etime_temp - stime_temp)/1000);
							temp1.put("tag", 1);
							temp2.put("time", (etime_temp - stime_temp)/1000);
							data_temp.put(temp1);
							temp2.put("tag", 2);
							data_temp.put(temp2);
							
							temp3.put("x", (etime_temp - stime)/1000.0);
							temp3.put("y", null_data);
							data_temp.put(temp3);
						}
						
						obj_temp.put("name", func[i]);
						obj_temp.put("color", color[i]);
						obj_temp.put("data", data_temp);

						trace_data.put(obj_temp);
					}
					
					//Get communication line data
					String rank_name;
					if (exp_type.equals("mpi"))
						rank_name = "RANK";
					else 
						rank_name = "MPI_RANK";
					
					res = dbm.executeSql("SELECT * FROM `"+exp_name+"_comm` WHERE ((eid='83' || eid='86' || eid='76' || eid='61' ||eid='71' || eid='81' ||eid='74') AND finish=1) || ((eid='80' || eid='73') AND finish=2) ORDER BY time;");
					
					int send_array []= new int [res.size()/2];
					int recv_array []= new int [res.size()/2];
					int send_flag = 0;
					int recv_flag = 0;
					JSONArray line_temp = new JSONArray ();
					for (int i = 0; i < res.size(); i++)
					{
						HashMap hash = (HashMap)res.get(i);

						if ((hash.get("FINISH").toString()).equals("1"))
						{
							send_array [send_flag] = i;
							send_flag ++;
						}
						else if ((hash.get("FINISH").toString()).equals("2"))
						{
							recv_array [recv_flag] = i;
							recv_flag ++;
						}
					}

					JSONObject line_data = new JSONObject ();
					line_data.put("name", "comm_line");
					line_data.put("color", "#000000");
					
					for (int i = 0; i < res.size()/2; i++)
					{
						JSONObject temp1 = new JSONObject ();
						JSONObject temp2 = new JSONObject ();
						JSONObject temp3 = new JSONObject ();
						JSONObject null_data = null;
						
						HashMap hash = (HashMap)res.get(send_array[i]);
						long stime_temp = Long.parseLong(hash.get("TIME").toString());
						
						temp1.put("x", (stime_temp - stime)/1000.0);
						temp1.put("y", height - Integer.parseInt(hash.get(rank_name).toString()));
						//temp1.put("rank", hash.get(rank_name).toString());
						
						line_temp.put(temp1);
						
						hash = (HashMap)res.get(recv_array[i]);
						long etime_temp = Long.parseLong(hash.get("TIME").toString());
						
						temp2.put("x", (etime_temp - stime)/1000.0);
						temp2.put("y", height - Integer.parseInt(hash.get(rank_name).toString()));
						//temp2.put("rank", hash.get(rank_name).toString());
						
						line_temp.put(temp2);

						temp3.put("x", (etime_temp - stime)/1000.0);
						temp3.put("y",null_data);
						line_temp.put(temp3);
					}
					line_data.put("data", line_temp);
					trace_data.put(line_data);
					out.write(trace_data.toString());
				}
			}
			/*else if (type.equals("get_trace_data_highstock"))
			{
				JSONArray trace_data = new JSONArray ();
				ArrayList res = new ArrayList ();
			//	res = dbm.executeSql("SELECT DISTINCT pid FROM `"+exp_name+"_trace` ORDER BY pid;");
				res = dbm.executeSql("SELECT DISTINCT `"+exp_name+"_trace`.pid FROM `"+exp_name+"_trace`, `"+exp_name+"_nodelist` WHERE `"+exp_name+"_trace`.pid = `"+exp_name+"_nodelist`.pid AND `"+exp_name+"_trace`.hostname = `"+exp_name+"_nodelist`.hostname ORDER BY `"+exp_name+"_nodelist`.mpirank");
				String pid [] = new String [res.size()];
				
				JSONArray pid_list = new JSONArray ();
				HashMap<String,Double> pid_map = new HashMap<String, Double>();
				JSONObject pid_obj = new JSONObject ();
				for (int i=0; i < res.size(); i++)
				{
					HashMap hash = (HashMap)res.get(i);
					
					pid [i] = hash.get("PID").toString();
					System.out.println(pid[i]);
					pid_list.put(pid[i]);
					pid_map.put(pid[i], i+0.0);
					//System.out.println(pid_map.get(pid[i])+"---"+pid[i]);
				}
				
				pid_obj.put("pid", pid_list);
				trace_data.put(pid_obj);
				
				res = dbm.executeSql("SELECT DISTINCT name, color FROM `"+exp_name+"_trace`,`"+exp_name+"_def` where name=fun_name ORDER BY eid ;");
				int e_length = res.size(); 

				String[] func = new String [e_length];
				String[] color = new String [e_length];
				for (int i = 0; i < e_length; i++)
				{
					HashMap hash = (HashMap)res.get(i);
					func[i] = hash.get("NAME").toString();
					color[i] = hash.get("COLOR").toString();
				}
				res = dbm.executeSql("SELECT MIN(time) as time FROM `"+exp_name+"_trace`;");
				//String stime_temp = ((HashMap)res.get(0)).get("TIME").toString();
				long stime = Long.parseLong(((HashMap)res.get(0)).get("TIME").toString());
				for (int i = 0; i < e_length; i++)
				{
					res = dbm.executeSql("SELECT pid, time FROM `"+exp_name+"_trace` WHERE name='"+func[i]+"' ORDER BY pid;");
					JSONArray data_temp = new JSONArray ();
					JSONObject obj_temp = new JSONObject ();
					for (int j = 0; j < res.size(); j++)
					{
				
						JSONArray temp1 = new JSONArray ();
						JSONArray temp2 = new JSONArray ();
						JSONArray temp3 = new JSONArray ();
						HashMap hash = (HashMap)res.get(j);
						String pid_temp = hash.get("PID").toString();
						long stime_temp = Long.parseLong(hash.get("TIME").toString());
						
						j++;
						hash = (HashMap)res.get(j);
						long etime_temp = Long.parseLong(hash.get("TIME").toString());
						
						temp1.put(stime_temp/1000);
						temp1.put(pid_map.get(pid_temp));
						//temp1.put("pid", pid_temp);
						//temp1.put("name", func[i]);
						//temp1.put("time", (etime_temp - stime_temp)/1000000.0);
						data_temp.put(temp1);
						temp2.put(etime_temp/1000);
						temp2.put(pid_map.get(pid_temp));
						//temp2.put("pid", pid_temp);
						//temp2.put("name", func[i]);
						//temp2.put("time", (etime_temp - stime_temp)/1000000.0);
						data_temp.put(temp2);
						temp3.put(etime_temp/1000);
						JSONObject l = null;
						temp3.put(l);
						data_temp.put(temp3);
					}
					
					obj_temp.put("name", func[i]);
					obj_temp.put("color", color[i]);
					obj_temp.put("data", data_temp);

					trace_data.put(obj_temp);
				}
				
				String exp_type = session.getAttribute("exp_type").toString();
				String rank_name;
				if (exp_type.equals("mpi"))
					rank_name = "RANK";
				else 
					rank_name = "MPI_RANK";
				
				res = dbm.executeSql("SELECT * FROM `"+exp_name+"_comm` WHERE ((eid='83' || eid='86' || eid='76' || eid='61' ||eid='71' || eid='81' ||eid='74') AND finish=1) || ((eid='80' || eid='73') AND finish=2) ORDER BY time;");
				
				int send_array []= new int [res.size()/2];
				int recv_array []= new int [res.size()/2];
				int send_flag = 0;
				int recv_flag = 0;
				JSONArray line_temp = new JSONArray ();
				for (int i = 0; i < res.size(); i++)
				{
					HashMap hash = (HashMap)res.get(i);

					if ((hash.get("FINISH").toString()).equals("1"))
					{
						send_array [send_flag] = i;
						send_flag ++;
					}
					else if ((hash.get("FINISH").toString()).equals("2"))
					{
						recv_array [recv_flag] = i;
						recv_flag ++;
					}
				}
				
				JSONObject line_data = new JSONObject ();
				line_data.put("name", "comm_line");
				line_data.put("color", "#000000");
				
				for (int i = 0; i < res.size()/2; i++)
				{
					JSONArray temp1 = new JSONArray ();
					JSONArray temp2 = new JSONArray ();
					JSONArray temp3 = new JSONArray ();
					
					HashMap hash = (HashMap)res.get(send_array[i]);
					long stime_temp = Long.parseLong(hash.get("TIME").toString());
					
					temp1.put(stime_temp / 1000);
					temp1.put(Integer.parseInt(hash.get(rank_name).toString()));
					//temp1.put("rank", hash.get(rank_name).toString());
					
					line_temp.put (temp1);
					
					hash = (HashMap)res.get(recv_array[i]);
					long etime_temp = Long.parseLong(hash.get("TIME").toString());
					
					temp2.put(etime_temp / 1000);
					temp2.put(Integer.parseInt(hash.get(rank_name).toString()));
					//temp2.put("rank", hash.get(rank_name).toString());
					JSONObject l = null;
					temp3.put(etime_temp/1000);
					temp3.put(l);
					
					line_temp.put (temp2);
					line_temp.put (temp3);
				}
				line_data.put("data", line_temp);
				trace_data.put(line_data);
				
				System.out.print(trace_data.toString());
				out.write(trace_data.toString());
			}*/
			else if (type.equals("get_func_name"))
			{
				JSONArray func_name = new JSONArray ();
				ArrayList res = new ArrayList ();
				res = dbm.executeSql("SELECT DISTINCT name FROM `"+exp_name+"_profile` ORDER BY eid ;");
		
				for (int i = 0; i < res.size(); i++)
				{
					HashMap hash = (HashMap)res.get(i);
					JSONObject temp = new JSONObject ();
					temp.put("func_name", hash.get("NAME").toString());
					func_name.put(temp);
				}
				out.write(func_name.toString());
			}
			else if (type.equals("get_PAPI_data"))									//get papi data
			{
				JSONArray PAPI_data = new JSONArray ();
				String rank = request.getParameter("rank");
				
				/*get event name from papi table, then put it into JSONArray */
				ArrayList res = new ArrayList ();
				res = dbm.executeSql("SELECT DISTINCT(papi_event) FROM `"+exp_name+"_papi` ;");
				//JSONArray event_list = new JSONArray ();
				//JSONObject event_obj = new JSONObject ();
				String [] event = new String [res.size()];
				for (int i = 0; i < res.size(); i++)
				{
					event[i] = ((HashMap)res.get(i)).get("PAPI_EVENT").toString();
				//	event_list.put(event[i]);
				}
				//event_obj.put("event", event_list);
				//PAPI_data.put(event_obj);
				
				res = dbm.executeSql("SELECT MIN(time) as time FROM `"+exp_name+"_papi`;");
				//String stime_temp = ((HashMap)res.get(0)).get("TIME").toString();
				long stime = Long.parseLong(((HashMap)res.get(0)).get("TIME").toString());
				
				for (int i = 0; i < event.length; i++)
				{
					res =  dbm.executeSql("SELECT name, data, time, finish FROM `"+exp_name+"_papi` , `"+exp_name+"_nodelist` WHERE `"+exp_name+"_papi`.hostname = `"+exp_name+"_nodelist`.hostname AND `"+exp_name+"_papi`.pid = `"+exp_name+"_nodelist`.pid AND `"+exp_name+"_nodelist`.mpirank = "+rank+" AND papi_event='"+event[i]+"' order by time;");
					JSONObject event_data_obj = new JSONObject ();
					JSONArray event_data_list = new JSONArray ();
					
					for (int j = 0; j < res.size(); j++)
					{
						/*JSONObject temp = new JSONObject ();*/
						JSONArray temp = new JSONArray ();
						HashMap hash = (HashMap)res.get(j);
						String name_temp = hash.get("NAME").toString();
						/*String data_temp = hash.get("DATA").toString();*/
						int data_temp = Integer.parseInt(hash.get("DATA").toString());
						long time_temp = Long.parseLong(hash.get("TIME").toString());
						String fininsh_temp = hash.get("FINISH").toString();
						
						/*temp.put("name", name_temp);
						temp.put("x", time_temp/1000);
						temp.put("y", data_temp);
						temp.put("finish", fininsh_temp);*/
						
						temp.put(time_temp/1000);
						temp.put(data_temp);
						
						event_data_list.put(temp);
					}
					event_data_obj.put("name", event[i]);
					event_data_obj.put("data", event_data_list);
					PAPI_data.put(event_data_obj);
				}
				out.write(PAPI_data.toString());
				System.out.println(PAPI_data.toString());
			}
			else if (type.equals("get_proc"))
			{
				System.out.println("dddd");
				JSONArray proc_data = new JSONArray ();
				
				String host_name = request.getParameter("host_name");
				String instance = request.getParameter("instance");
				String metric = request.getParameter("metric");
				
				ArrayList res = new ArrayList ();
				res = dbm.executeSql("SELECT DISTINCT instance FROM `"+exp_name+"_proc` WHERE hostname='"+host_name+"' AND metric = '"+metric+"' AND instance LIKE '"+instance+"%';");
				String [] instance_list = new String [res.size()]; 
				for (int i = 0; i < res.size(); i++)
				{
					HashMap hash=(HashMap)res.get(i);
		   			instance_list[i]=hash.get("INSTANCE").toString();   
				}
				
				for (int i = 0; i < instance_list.length; i++)
				{
					JSONObject proc_obj = new JSONObject ();
					JSONArray proc_list = new JSONArray ();
					res = dbm.executeSql("SELECT time_s*1000000+time_us AS time , data FROM `"+exp_name+"_proc` WHERE hostname='"+host_name+"' AND metric = '"+metric+"' AND instance='"+instance_list[i]+"';");
					for (int j = 0; j < res.size(); j++)
					{
						JSONArray temp = new JSONArray ();
						HashMap hash = (HashMap)res.get(j);
			     		
						long time_temp = Long.parseLong(hash.get("TIME").toString());
			     		double data_temp=Double.parseDouble(hash.get("DATA").toString());
			     		
			     		temp.put(time_temp/1000);
			     		temp.put(data_temp);
			     		proc_list.put(temp);
					}
//					System.out.println(proc_list.toString());
					proc_obj.put("name", instance_list[i]);
					proc_obj.put("data", proc_list);
					proc_data.put(proc_obj);
				}
				out.write(proc_data.toString());
				System.out.println(proc_data.toString());
			}
			else if (type.equals("get_rank"))
			{
				int flag = 0;
				JSONArray data_list = new JSONArray ();

				ArrayList res = new ArrayList ();
				res = dbm.executeSql("SELECT table_name FROM information_schema.tables WHERE table_name = '"+exp_name+"_profile';");
				if (res.size() != 0)
					flag = 1;
				
				if (flag == 1)
					res = dbm.executeSql("SELECT DISTINCT `"+exp_name+"_nodelist`.mpirank,  `"+exp_name+"_nodelist`.pid FROM `"+exp_name+"_nodelist` ,`"+exp_name+"_profile`  WHERE `"+exp_name+"_profile`.pid = `"+exp_name+"_nodelist`.pid AND `"+exp_name+"_profile`.hostname = `"+exp_name+"_nodelist`.hostname AND `"+exp_name+"_nodelist`.mpirank IS NOT NULL ORDER BY `"+exp_name+"_nodelist`.mpirank;");
				else
					res = dbm.executeSql("SELECT DISTINCT `"+exp_name+"_nodelist`.mpirank ,`"+exp_name+"_nodelist`.pid FROM `"+exp_name+"_nodelist` ,`"+exp_name+"_trace`  WHERE `"+exp_name+"_trace`.pid = `"+exp_name+"_nodelist`.pid AND `"+exp_name+"_trace`.hostname = `"+exp_name+"_nodelist`.hostname AND `"+exp_name+"_nodelist`.mpirank IS NOT NULL ORDER BY `"+exp_name+"_nodelist`.mpirank;");
				
				//res = dbm.executeSql("SELECT  DISTINCT `"+exp_name+"_profile`.pid, mpirank FROM `"+exp_name+"_profile` , `"+exp_name+"_nodelist` WHERE `"+exp_name+"_nodelist`.pid = `"+exp_name+"_profile`.pid  ORDER BY mpirank");
				
				int length = res.size();
				for (int i = 0; i < length; i++)
				{
					JSONObject temp = new JSONObject ();
					HashMap hash = (HashMap)res.get(i);
					temp.put("pid", hash.get("PID").toString());
					temp.put("rank", hash.get("MPIRANK").toString());
					
					data_list.put(temp);
				}
				out.write(data_list.toString());
				System.out.println(data_list.toString());
			}
			else if (type.equals("get_pie_data"))
			{
				JSONArray pie_data = new JSONArray ();
				String rank = request.getParameter("rank");
				String sql = "SELECT DISTINCT user_classified_id , class_name FROM `"+exp_name+"_def`, `"+exp_name+"_classify` WHERE user_classified_id = class_id;";
				
				ArrayList res = new ArrayList ();
				res = dbm.executeSql(sql);
				String [] cat_id = new String [res.size()];
				String [] cat_name = new String [res.size()];
				
				for (int i = 0; i < res.size(); i++)
				{
					HashMap hash = (HashMap)res.get(i);
					cat_id [i] = hash.get("USER_CLASSIFIED_ID").toString();
					cat_name [i] = hash.get("CLASS_NAME").toString();
				}
				System.out.println(rank);
				if (rank.equals("All"))
				{
					for (int i = 0; i < cat_id.length; i++)
					{
						/*JSONObject temp = new JSONObject ();*/
						JSONArray temp = new JSONArray ();
						sql = "SELECT SUM(sumTime) as time FROM `"+exp_name+"_profile` WHERE name in (SELECT fun_name FROM `"+exp_name+"_def` WHERE user_classified_id ="+cat_id[i]+" AND flag = 1);";
						res = dbm.executeSql(sql);
						HashMap hash = (HashMap)res.get(0);
						long time;
						if (hash.get("TIME") == null)
							time = 0;
						else
							time = Long.parseLong(hash.get("TIME").toString());
						/*temp.put("name", cat_name[i]);
						temp.put("y", time);*/
						temp.put(cat_name[i]);
						temp.put(time);
						pie_data.put(temp);
					}
				}
				else 
				{
					res = dbm.executeSql("SELECT pid FROM `"+exp_name+"_nodelist` WHERE mpirank = "+rank.substring(4)+";");
					String pid = ((HashMap)res.get(0)).get("PID").toString();
					
					for (int i = 0; i < cat_id.length; i++)
					{
						ArrayList result;
						JSONArray temp = new JSONArray ();
						sql = "SELECT SUM(sumTime) as time FROM `"+exp_name+"_profile` WHERE pid = "+pid+" AND name in (SELECT fun_name FROM `"+exp_name+"_def` WHERE user_classified_id ="+cat_id[i]+" AND flag = 1);";
						result = dbm.executeSql(sql);
						if (((HashMap)result.get(0)).get("TIME") == null)
							continue ;
						
						long time = Long.parseLong((((HashMap)result.get(0))).get("TIME").toString());
						temp.put(cat_name[i]);
						temp.put(time);
						pie_data.put(temp);
					}
				}
				out.write(pie_data.toString());
				System.out.println(pie_data.toString());
			}
			else if (type.equals("get_single_proc_data"))
			{
				String host_name = request.getParameter("host_name");
				String instance = request.getParameter("instance");
				String metric = request.getParameter("metric");
				
				JSONArray data_list = new JSONArray ();
				ArrayList res = new ArrayList ();
				res = dbm.executeSql("SELECT time_s*1000000+time_us AS time , data FROM `"+exp_name+"_proc` WHERE hostname='"+host_name+"' AND metric = '"+metric+"' AND instance='"+instance+"';");
				
				for (int i = 0; i < res.size(); i++)
				{
					JSONArray temp = new JSONArray ();
					HashMap hash = (HashMap)res.get(i);
		     		
					long time_temp = Long.parseLong(hash.get("TIME").toString());
		     		double data_temp=Double.parseDouble(hash.get("DATA").toString());
		     		
		     		temp.put(time_temp/1000);
		     		temp.put(data_temp);
		     		data_list.put(temp);
				}
				System.out.println(data_list.toString());
				out.write(data_list.toString());
			}
			else if (type.equals("get_spider_data"))
			{
				JSONArray spider_data = new JSONArray ();
				String rank = request.getParameter("rank");
				String sql = "SELECT DISTINCT user_classified_id , class_name FROM `"+exp_name+"_def`, `"+exp_name+"_classify` WHERE user_classified_id = class_id;";
				
				ArrayList res = new ArrayList ();
				res = dbm.executeSql(sql);
				String [] cat_id = new String [res.size()];
				String [] cat_name = new String [res.size()];				
				JSONArray cat_list = new JSONArray ();			
				for (int i = 0; i < res.size(); i++)
				{
					HashMap hash = (HashMap)res.get(i);
					cat_id [i] = hash.get("USER_CLASSIFIED_ID").toString();
					cat_name [i] = hash.get("CLASS_NAME").toString();
					cat_list.put(cat_name [i]);
				}
				spider_data.put(cat_list);
				
				
				if (rank.equals("All"))
				{
					res = dbm.executeSql("SELECT pid FROM `"+exp_name+"_nodelist` WHERE mpirank is not null ORDER BY mpirank ;");
					int pid_num = res.size();
					String [] pid_list = new String [pid_num];
					for (int i = 0; i < pid_num; i++)
						pid_list [i] = ((HashMap)res.get(i)).get("PID").toString();

					for (int i = 0; i < pid_num; i++)
					{
						JSONObject data_obj = new JSONObject ();
						JSONArray data_list = new JSONArray();
						for (int j = 0; j < cat_id.length; j++)
						{
							JSONArray temp = new JSONArray ();
							sql = "SELECT SUM(sumTime) as time FROM `"+exp_name+"_profile` WHERE name in (SELECT fun_name FROM `"+exp_name+"_def` WHERE user_classified_id ="+cat_id[j]+" AND flag = 1) AND pid='"+pid_list[i]+"';";
							res = dbm.executeSql(sql);
							long time = 0;
							if (((HashMap)res.get(0)).get("TIME") != null)
								time = Long.parseLong((((HashMap)res.get(0))).get("TIME").toString());
							data_list.put(time/1000);
						}
						data_obj.put("rank", i);
						data_obj.put("data", data_list);
						spider_data.put(data_obj);
					}
					
					/*for (int i = 0; i < cat_id.length; i++)
					{
						JSONArray temp = new JSONArray ();
						sql = "SELECT SUM(sumTime) as time FROM `"+exp_name+"_profile` WHERE name in (SELECT fun_name FROM `"+exp_name+"_def` WHERE user_classified_id ="+cat_id[i]+");";
						res = dbm.executeSql(sql);
						long time = Long.parseLong((((HashMap)res.get(0))).get("TIME").toString());

						data_list.put(time/1000);
					}*/
				}
				else 
				{
					JSONObject data_obj = new JSONObject ();
					JSONArray data_list = new JSONArray();
					res = dbm.executeSql("SELECT pid FROM `"+exp_name+"_nodelist` WHERE mpirank = "+rank.substring(4)+";");
					String pid = ((HashMap)res.get(0)).get("PID").toString();
					
					for (int i = 0; i < cat_id.length; i++)
					{
						ArrayList result;
						JSONArray temp = new JSONArray ();
						sql = "SELECT SUM(sumTime) as time FROM `"+exp_name+"_profile` WHERE pid = "+pid+" AND name in (SELECT fun_name FROM `"+exp_name+"_def` WHERE user_classified_id ="+cat_id[i]+" AND flag = 1);";
						result = dbm.executeSql(sql);
						if (((HashMap)result.get(0)).get("TIME") == null)
							data_list.put(0);
						else 
						{
							long time = Long.parseLong((((HashMap)result.get(0))).get("TIME").toString());
							data_list.put(time/1000);	
						}
					}
					data_obj.put("rank", rank.substring(4));
					data_obj.put("data", data_list);
					spider_data.put(data_obj);
				}
				
				out.write(spider_data.toString());
				System.out.println(spider_data.toString());
			}
			else if (type.equals("get_proc_spider_data"))
			{
				JSONArray spider_data = new JSONArray ();
				ArrayList res = new ArrayList ();
				res = dbm.executeSql("SELECT DISTINCT hostname FROM `"+exp_name+"_proc`;");
				String [] host = new String [res.size()];
				for (int i = 0; i < res.size(); i++)
				{
					host [i] = ((HashMap)res.get(i)).get("HOSTNAME").toString();
				}
				
				for (int i = 0; i < host.length; i++)
				{
					JSONArray list_temp = new JSONArray ();
					JSONObject obj_temp = new JSONObject ();
					long iosend, iorecv, netsend, netrecv;
					res = dbm.executeSql("SELECT SUM(data) as time FROM `"+exp_name+"_proc` WHERE hostname='"+host[i]+"' and instance = 'Iosend' and metric='Byte';");
					if (((HashMap)res.get(0)).get("TIME") == null)
						iosend = 0;
					else iosend = Long.parseLong((((HashMap)res.get(0))).get("TIME").toString());
					
					res = dbm.executeSql("SELECT SUM(data) as time FROM `"+exp_name+"_proc` WHERE hostname='"+host[i]+"' and instance = 'Iorecv' and metric='Byte';");
					if (((HashMap)res.get(0)).get("TIME") == null)
						iorecv = 0;
					else iorecv = Long.parseLong((((HashMap)res.get(0))).get("TIME").toString());
					
					res = dbm.executeSql("SELECT SUM(data) as time FROM `"+exp_name+"_proc` WHERE hostname='"+host[i]+"' and instance = 'Netsend' and metric='Byte';");
					if (((HashMap)res.get(0)).get("TIME") == null)
						netsend = 0;
					else netsend = Long.parseLong((((HashMap)res.get(0))).get("TIME").toString());
					
					res = dbm.executeSql("SELECT SUM(data) as time FROM `"+exp_name+"_proc` WHERE hostname='"+host[i]+"' and instance = 'Netrecv' and metric='Byte';");
					if (((HashMap)res.get(0)).get("TIME") == null)
						netrecv = 0;
					else netrecv = Long.parseLong((((HashMap)res.get(0))).get("TIME").toString());
					
					list_temp.put(iosend);
					list_temp.put(iorecv);
					list_temp.put(netsend);
					list_temp.put(netrecv);
					
					obj_temp.put("name", host[i]);
					obj_temp.put("data", list_temp);
					spider_data.put(obj_temp);
				}
				out.write(spider_data.toString());
				System.out.println(spider_data.toString());
			}
			else if (type.equals("get_proc_instace"))
			{
				ArrayList res = new ArrayList ();
				JSONArray instance = new JSONArray ();
				
				res = dbm.executeSql("SELECT DISTINCT instance FROM `"+exp_name+"_proc` WHERE pid != '-1'");
				for (int i = 0; i < res.size(); i++)
					instance.put (((HashMap)res.get(i)).get("INSTANCE").toString());

				out.write(instance.toString());
			}
			else if (type.equals("get_proc_stack_data"))
			{
				ArrayList res = new ArrayList ();
				ArrayList rank_list = new ArrayList ();
				
				JSONObject proc_stack_data = new JSONObject ();
				JSONArray data_array = new JSONArray ();
				JSONArray time_array = new JSONArray ();
				long time_list[];
				
				String instance = request.getParameter("instance");
				String metric = request.getParameter("metric");
				
				res = dbm.executeSql("SELECT DISTINCT time_s*1000000 + time_us as time FROM `"+exp_name+"_proc` WHERE pid = '-1'");
				time_list = new long [res.size()];
				for (int i = 0; i < res.size(); i++)
				{
					time_list [i] = Long.parseLong(((HashMap)res.get(i)).get("TIME").toString());
					//time_array.put(i);
					time_array.put(time_list[i]/1000);
				}
				proc_stack_data.put("time", time_array);

				//System.out.println(time_list[0]);
				
				rank_list = dbm.executeSql("SELECT DISTINCT mpirank, `"+exp_name+"_nodelist`.pid , `"+exp_name+"_nodelist`.hostname FROM `"+exp_name+"_proc`, `"+exp_name+"_nodelist` WHERE `"+exp_name+"_proc`.hostname = `"+exp_name+"_nodelist`.hostname AND `"+exp_name+"_proc`.pid = `"+exp_name+"_nodelist`.pid ORDER BY mpirank;");
				
				for (int i = 0; i < rank_list.size(); i++)
				{
					System.out.println(i);
					JSONObject data_obj = new JSONObject ();
					JSONArray data_temp = new JSONArray ();
					HashMap hash = (HashMap)rank_list.get(i);
					res = dbm.executeSql("SELECT data , metric FROM `"+exp_name+"_proc` WHERE pid = '"+hash.get("PID")+"' AND hostname = '"+hash.get("HOSTNAME")+"' AND instance = '"+instance+"' AND metric = '"+metric+"';");
					
					for (int j = 0; j < res.size(); j++)
					{
						hash = (HashMap)res.get(j);
						data_temp.put(Float.parseFloat(hash.get("DATA").toString()));
					}
					data_obj.put("name", "Rank" + i);
					data_obj.put("data", data_temp);
					
					data_array.put(data_obj);
					//proc_stack_data.put("series", data_obj);
					//System.out.println(proc_stack_data.toString());
				}
				proc_stack_data.put("series", data_array);
	//			System.out.println(proc_stack_data.toString());
				out.write(proc_stack_data.toString());
			}
			else if (type.equals("get_cat_trace_data"))
			{
				JSONArray cat_trace_data = new JSONArray ();
				
				ArrayList res = new ArrayList ();
		
				res = dbm.executeSql("SELECT DISTINCT `"+exp_name+"_trace`.pid, `"+exp_name+"_nodelist`.mpirank FROM `"+exp_name+"_trace`, `"+exp_name+"_nodelist` WHERE `"+exp_name+"_trace`.pid = `"+exp_name+"_nodelist`.pid AND `"+exp_name+"_trace`.hostname = `"+exp_name+"_nodelist`.hostname ORDER BY `"+exp_name+"_nodelist`.mpirank");
				String pid [] = new String [res.size()];
				
				JSONArray pid_list = new JSONArray ();
				JSONArray rank_list = new JSONArray ();
				HashMap<String,Integer> pid_map = new HashMap<String, Integer>();
				JSONObject pid_obj = new JSONObject ();
				for (int i=0; i < res.size(); i++)
				{
					HashMap hash = (HashMap)res.get(i);
					
					pid [i] = hash.get("PID").toString();
					System.out.println(pid[i]);
					pid_list.put(pid[i]);
					rank_list.put(hash.get("MPIRANK").toString());
					pid_map.put(pid[i], i);
				}
				
				pid_obj.put("pid", pid_list);
				pid_obj.put("rank", rank_list);
				pid_obj.put("num", res.size());
				cat_trace_data.put(pid_obj);
				
				res = dbm.executeSql("SELECT * FROM `"+exp_name+"_classify`;");
				String [] cat_name = new String [res.size()];
				String [] cat_id = new String [res.size()];
				String [] cat_color = new String [res.size()];
				int cat_num = res.size();
				for (int i = 0; i < res.size(); i++)
				{
					HashMap hash =  (HashMap)res.get(i);
					cat_name [i] = hash.get("CLASS_NAME").toString();
					cat_id [i] = hash.get("CLASS_ID").toString();
					cat_color [i] = hash.get("CLASS_COLOR").toString();
				}
				res = dbm.executeSql("SELECT MIN(time) as min, MAX(time) as max FROM `"+exp_name+"_trace`, `"+exp_name+"_def` WHERE `"+exp_name+"_trace`.name = `"+exp_name+"_def`.fun_name AND `"+exp_name+"_def`.flag = 1;");
				long stime = Long.parseLong(((HashMap)res.get(0)).get("MIN").toString());
				
				for (int i = 0; i < cat_num; i++)
				{
					res = dbm.executeSql("SELECT pid, time FROM `"+exp_name+"_trace`, `"+exp_name+"_def` WHERE name=fun_name AND flag=1 AND user_classified_id="+cat_id[i]+" ORDER BY pid, time;");
					
					JSONArray data_temp = new JSONArray ();
					JSONObject obj_temp = new JSONObject ();
					
					
					for (int j = 0; j < res.size(); j++)
					{
						JSONObject temp1 = new JSONObject ();
						JSONObject temp2 = new JSONObject ();
						JSONObject temp3 = new JSONObject ();
						JSONObject null_data = null;
						HashMap hash = (HashMap)res.get(j);
						String pid_temp = hash.get("PID").toString();
						long stime_temp = Long.parseLong(hash.get("TIME").toString());
						
						j++;
						hash = (HashMap)res.get(j);
						long etime_temp = Long.parseLong(hash.get("TIME").toString());

						temp1.put("x", (stime_temp - stime));
						temp1.put("y", pid_map.get(pid_temp));
				
						data_temp.put(temp1);
						temp2.put("x", (etime_temp - stime));
						temp2.put("y", pid_map.get(pid_temp));
						
						data_temp.put(temp2);
						
						temp3.put("x", (etime_temp - stime));
						temp3.put("y", null_data);
						data_temp.put(temp3);
						
						/*if (pid_temp.equals("28063") && i==0)
							System.out.println (stime_temp - stime + "  ----   " +stime );*/
					}
					
					obj_temp.put("name", cat_name[i]);
					obj_temp.put("color", cat_color[i]);
					obj_temp.put("data", data_temp);

					cat_trace_data.put(obj_temp);
				}
				out.write(cat_trace_data.toString());
				//System.out.println(cat_trace_data.toString());
			}
			else if (type.equals("get_cat_profile_data"))
			{
				JSONArray cat_profile_data = new JSONArray ();
	
				ArrayList res = new ArrayList ();
				res = dbm.executeSql("SELECT  DISTINCT `"+exp_name+"_profile`.pid, mpirank FROM `"+exp_name+"_profile` , `"+exp_name+"_nodelist` WHERE `"+exp_name+"_nodelist`.pid = `"+exp_name+"_profile`.pid  ORDER BY mpirank");
				String pid[] = new String [res.size()];
				
				JSONArray pid_list = new JSONArray ();
				JSONArray rank_list = new JSONArray ();
				JSONObject pid_obj = new JSONObject ();
				
				for (int i=0; i < res.size(); i++)
				{
					HashMap hash = (HashMap)res.get(i);
					pid [i] = hash.get("PID").toString();
					pid_list.put(pid[i]);
					rank_list.put(hash.get("MPIRANK").toString());
				}
				pid_obj.put("pid", pid_list);
				pid_obj.put("rank", rank_list);
				cat_profile_data.put(pid_obj);
				
				res = dbm.executeSql("SELECT * FROM `"+exp_name+"_classify`;");
				int e_length = res.size(); 

				String[] cat_name = new String [e_length];
				String[] cat_color = new String [e_length];
				String[] cat_id = new String [e_length];
				for (int i = 0; i < e_length; i++)
				{
					HashMap hash = (HashMap)res.get(i);
					cat_name[i] = hash.get("CLASS_NAME").toString();
					cat_color[i] = hash.get("CLASS_COLOR").toString();
					cat_id[i] = hash.get("CLASS_ID").toString();
				}
				
				JSONArray data_list = new JSONArray ();
				JSONObject data_obj = new JSONObject ();
				for (int i = 0; i < e_length; i++)
				{
					res = dbm.executeSql("SELECT  `"+exp_name+"_profile`.pid, mpirank, sum(sumTime) AS time FROM `"+exp_name+"_profile` , `"+exp_name+"_nodelist` WHERE  `"+exp_name+"_nodelist`.pid = `"+exp_name+"_profile`.pid AND name in (SELECT fun_name FROM `"+exp_name+"_def` WHERE `"+exp_name+"_def`.user_classified_id="+cat_id[i]+" AND flag = 1)  GROUP  BY mpirank");
					JSONArray time_list = new JSONArray ();
					JSONObject temp = new JSONObject ();
					
					if (res.size() == pid.length)
					{
						for (int j = 0; j < res.size(); j++)
						{
							HashMap hash = (HashMap)res.get(j);
							if (hash.get("TIME") == null)
								time_list.put("0");
							else
								time_list.put(hash.get("TIME").toString());
						}
					}
					else 
					{
						for (int j = 0, k = 0; j < res.size();)
						{
							HashMap hash = (HashMap)res.get(j);
							if (hash.get("PID").toString().equals(pid[k]))
							{
								if (hash.get("TIME") == null)
									time_list.put("0");
								else
									time_list.put(hash.get("TIME").toString());
								j++;
								k++;
							}
							else
							{
								time_list.put("0");
								k++;
							}
							System.out.println(time_list.toString());
						}
					}
					temp.put("func_name", cat_name[i]);
					temp.put("color", cat_color[i]);
					temp.put("data", time_list);
					data_list.put(temp);
				}
				data_obj.put("data", data_list);
				cat_profile_data.put(data_obj);
				
				System.out.print(cat_profile_data.toString());
				out.write(cat_profile_data.toString());
			}
		}
		catch (Exception e) {
            System.err.println(e.toString());
        }
        finally {
            out.close();
        }	
	}
	
	
	public int DFT (omp_thread threads[], int flag[], omp_thread threads_temp [], int omp_level, int omp_rank, int maxLevel, int num)
	{
		for (int i = 0; i < threads_temp.length; i++)
		{
			if (flag [i] == 1)
				continue;
			else if (threads_temp[i].omp_level == (omp_level + 1) && threads_temp[i].omp_p_rank == omp_rank)
			{
				flag [i] = 1;
				threads [num] = new omp_thread ();
				threads [num] = threads_temp [i];
				++ num;
				if (num == threads_temp.length)
					return num;
				else if (threads_temp [i].omp_level == maxLevel)
					continue;
				else if (threads_temp [i].omp_level < maxLevel)
				{
					num = DFT (threads, flag, threads_temp, threads_temp [i].omp_level, threads_temp [i].omp_rank, maxLevel, num);
				}
			}
		}
		return num;
	}
	
	public int getHeight (omp_thread threads[], int omp_level, int omp_rank, int omp_p_thread)
	{
		int height = 0;
		for (int i = 0; i < threads.length; ++i)
		{
			if (threads[i].omp_level == omp_level && threads[i].omp_rank == omp_rank && threads[i].omp_p_rank == omp_p_thread)
			{
				height = threads.length - i - 1;
			}
		}
		
		return height;
	}
}
