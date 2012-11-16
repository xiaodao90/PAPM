package user_manager;

import graphVizAPI.GraphViz;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.*;
import DBTool.DBManager;
import java.io.File;

public class exp_info extends HttpServlet{
	public void doGet (HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		try {
			DBManager dbm = new DBManager ();
			String type = request.getParameter("req_type");
			HttpSession session = request.getSession();
			String exp_name = session.getAttribute("exp_name").toString();
			System.out.println(type);
			if (type.equals("get_node_info"))								//鑾峰彇鎵�湁鑺傜偣鍚�
			{
				JSONArray node_info = new JSONArray ();	
				ArrayList res = new ArrayList();
				res = dbm.executeSql("SELECT DISTINCT hostname FROM `"+exp_name+"_nodelist`");
				
				for (int i = 0; i < res.size(); i++)
				{
					JSONObject temp = new JSONObject ();
					/*JSONArray temp2 = new JSONArray ();*/
					
					HashMap hash = (HashMap)res.get(i);
					/*ArrayList temp3= dbm.executeSql("SELECT DISTINCT instance FROM "+exp_name+"_proc WHERE hostname='"+hash.get("HOSTNAME").toString()+"'");
					for (int j = 0; j < temp3.size(); j++)
					{
						HashMap temp4 = (HashMap)temp3.get(j);
						temp2.put(temp4.get("INSTANCE").toString());
					}*/
					temp.put("hostname",hash.get("HOSTNAME").toString());
					/*temp.put("instance", temp2);*/
					node_info.put(temp);
				}
		
				out.write(node_info.toString());
				System.out.println(node_info.toString());
			}
			else if (type.equals("get_rank_info"))							//鑾峰彇鎵�湁鐨刾id/rank鍙�
			{
				JSONArray rank_info = new JSONArray ();
				ArrayList res = new ArrayList ();
				res = dbm.executeSql("SELECT DISTINCT pid FROM `"+exp_name+"_profile`;");
				
				for (int i = 0; i < res.size(); i++)
				{
					JSONObject temp = new JSONObject ();
					HashMap hash = (HashMap)res.get(i);
					temp.put("rank", hash.get("PID").toString());
					rank_info.put(temp);
				}
				out.write(rank_info.toString());
			}
			else if (type.equals("get_se_time"))							//鑾峰彇瀹為獙鐨勫紑濮嬪拰缁撴潫鏃堕棿
			{
				JSONArray  se_time = new JSONArray ();
				JSONObject s_time = new JSONObject ();
				JSONObject e_time = new JSONObject ();
				ArrayList res = new ArrayList ();
				res = dbm.executeSql("SELECT UNIX_TIMESTAMP(start) as start, UNIX_TIMESTAMP(stop) as stop FROM `maintable` WHERE name='"+exp_name+"'");
				HashMap hash = (HashMap)res.get(0);
				s_time.put("stime", hash.get("START").toString());
				e_time.put("etime", hash.get("STOP").toString());
				
				se_time.put(s_time);
				se_time.put(e_time);
				System.out.println(se_time.toString());
				out.write(se_time.toString());
			}
			else if (type.equals("get_graph"))								//鑾峰彇proc涓笉鍚岀殑浜嬩欢+搴﹂噺鍗曚綅
			{
				JSONArray graph_list = new JSONArray ();
				String hostname = request.getParameter("host_name");
				String sql = "SELECT DISTINCT metric,instance FROM `"+exp_name+"_proc` WHERE hostname='"+hostname+"'";
				ArrayList res = new ArrayList ();
				res = dbm.executeSql(sql);
				
				for (int i = 0; i < res.size(); i++)
				{
					JSONObject temp = new JSONObject ();
					HashMap hash = (HashMap)res.get(i);
					
					temp.put("metric", hash.get("METRIC").toString());
					temp.put("instance", hash.get("INSTANCE").toString());
					
					graph_list.put(temp);
				}
				out.write(graph_list.toString());
				System.out.println(graph_list.toString());
			}
			else if (type.equals("get_instance"))							//鑾峰彇proc涓殑浜嬩欢
			{
				JSONArray instance_list = new JSONArray ();
				ArrayList res = new ArrayList ();
				res = dbm.executeSql("SELECT DISTINCT instance FROM `"+exp_name+"_proc`");
				
				for (int i = 0; i < res.size(); i++)
				{
					JSONObject temp = new JSONObject ();
					HashMap hash = (HashMap)res.get(i);
					temp.put("instance", hash.get("INSTANCE").toString());
					
					instance_list.put(temp);
				}
				out.write(instance_list.toString());
				System.out.println(instance_list.toString());
			}
			else if (type.equals("get_tree_rank"))
			{
				JSONArray rank_list = new JSONArray ();
				
				ArrayList res = new ArrayList ();
				res = dbm.executeSql("SELECT DISTINCT rank FROM `"+exp_name+"_tree` ORDER by rank;");
				for (int i = 0; i < res.size(); i++)
				{
					HashMap hash = (HashMap)res.get(i);
					rank_list.put(hash.get("RANK").toString());
				}
				out.write(rank_list.toString());
			}
			else if (type.equals("get_tree"))								//鐢熸垚鏍戠姸鍥�
			{
				JSONObject data = new JSONObject ();
				
				String graph_type = request.getParameter("graph_type");
				String rank = request.getParameter("rank");
				String dir = request.getParameter("dir");
				
				if (rank.equals("all"))
				{
					String sql = "SELECT MAX(level) as level FROM `"+exp_name+"_tree`";
					ArrayList res = new ArrayList ();
					res = dbm.executeSql(sql);
		
					int maxLevel = Integer.parseInt((((HashMap)res.get(0)).get("LEVEL")).toString());
					
					GraphViz gv = new GraphViz();
				    gv.addln(gv.start_graph());
				
				    gv.addln("graph [center  bgcolor=\"#CBDAF2\"]");
				    gv.addln("size=\"20,8\"; ratio = fill;");
				    gv.addln("rankdir="+dir);
				    HashMap nameMap = new HashMap ();

					for (int i = 1; i <= maxLevel; i++)
					{
						//sql = "SELECT * FROM `"+exp_name+"_tree` WHERE rank="+rank+" AND level="+i+" ORDER BY id";
						res = dbm.executeSql("SELECT name, SUM(time) AS time ,SUM(count) AS count, father, tree_relation , eid FROM `"+exp_name+"_tree` WHERE level = "+i+" GROUP BY tree_relation ORDER BY time desc;");
						String max_time = ((HashMap)res.get(0)).get("TIME").toString();
						
						String max_fun = ((HashMap)res.get(0)).get("NAME").toString();
	
						for (int j = 0; j < res.size(); j++)
						{
							HashMap hash = (HashMap)res.get(j);
							
							ArrayList temp = new ArrayList();
							int rank_num;
							
							String rank_list = "[";
							temp = dbm.executeSql("SELECT DISTINCT rank FROM `"+exp_name+"_tree` WHERE  tree_relation='"+hash.get("TREE_RELATION").toString()+"' ORDER BY rank;");
							rank_num = temp.size();
							int rank_array []= new int [temp.size()];
							for (int m = 0; m < temp.size(); m++)
								rank_array [m] = Integer.parseInt(((HashMap)temp.get(m)).get("RANK").toString());
							
							for (int m = 0; m < rank_num; m++)
							{
								rank_list += rank_array[m];
								if (m+1 < rank_num)
								{
									if (rank_array[m+1] - rank_array[m] == 1)
									{
										rank_list += "-";
										m++;
										while (m+1 < rank_num && rank_array[m+1] - rank_array[m] == 1)
											m++;
										if (m != rank_num - 1)
											rank_list += rank_array[m] + ",";
										else
											rank_list += rank_array[m];
									}
									else if (m != rank_num - 1)
									{
											rank_list += ",";
									}
								}
									
							}

							rank_list += "]";
							String name = hash.get("NAME").toString();
							String eid =  hash.get("EID").toString();
							double time = Double.parseDouble(hash.get("TIME").toString());
							int weight;
						
							if (time > 1000)
								weight = 20;
							else 
								weight = 1;
							
							nameMap.put(eid, name);
							
							String relation = hash.get("TREE_RELATION").toString();
							relation = relation.substring(1);
							String [] relationSplit = relation.split("\\.");
							
							System.out.println(relationSplit.length + ":" + relation);
							
							
							if (relationSplit.length == 1)
								gv.addln("\""+name+"_"+i+"\"[shape=box,color=\"#A1C4F6\",style=filled,label=\""+name+"\\n"+hash.get("COUNT").toString()+"::"+time+"\",fontsize=25]");
							else
							{
								gv.addln("\""+nameMap.get(relationSplit[relationSplit.length - 2]).toString()+"_"+(i-1)+"\" -> \""+nameMap.get(relationSplit[relationSplit.length - 1]).toString()+"_"+i+"\"[style=bold,label=\""+rank_num+rank_list+"\",fontsize=25];");
								if (name.equals(max_fun))
									gv.addln("\""+nameMap.get(relationSplit[relationSplit.length - 1]).toString()+"_"+i+"\""+"[shape=box,color=red,style=filled,label=\""+nameMap.get(relationSplit[relationSplit.length - 1]).toString()+"\\n"+hash.get("COUNT").toString()+"::"+time+"\",fontsize=25];");
								else
									gv.addln("\""+nameMap.get(relationSplit[relationSplit.length - 1]).toString()+"_"+i+"\""+"[shape=box,color=\"#A1C4F6\",style=filled,label=\""+nameMap.get(relationSplit[relationSplit.length - 1]).toString()+"\\n"+hash.get("COUNT").toString()+"::"+time+"\",fontsize=25];");
							}
							//System.out.println(relation+"--->"+relationSplit[0]+"[shape=box,label="+nameMap.get(relationSplit[1]).toString()+"\n"+time+"]");
							//String 
							
						}
					}
				    gv.addln(gv.end_graph());
				    System.out.println(gv.getDotSource());

				    String webRoot = request.getSession().getServletContext().getRealPath("/");

				   
				    File graph_out = new File(webRoot + "\\" + exp_name + "_" +rank+"_"+ dir +"_tree." + graph_type);		//windows
				   //File graph_out = new File(webRoot + "/" + exp_name + "_" +rank+"_"+ dir +"_tree." + graph_type);		//linux

				   
				    gv.writeGraphToFile( gv.getGraph( gv.getDotSource(), graph_type ), graph_out );
				}
				else 
				{
					String sql = "SELECT MAX(level) as level FROM `"+exp_name+"_tree` WHERE rank ="+rank;
					ArrayList res = new ArrayList ();
					res = dbm.executeSql(sql);
					
					int maxLevel = Integer.parseInt((((HashMap)res.get(0)).get("LEVEL")).toString());
					
					GraphViz gv = new GraphViz();
				    gv.addln(gv.start_graph());
				
				    gv.addln("graph [center  bgcolor=\"#CBDAF2\"]");
				    gv.addln("size=\"20,8\"; ratio = fill;");
				    gv.addln("rankdir="+dir);
				    
				    HashMap nameMap = new HashMap ();
				    System.out.println("maxLevel:"+maxLevel);
					for (int i = 1; i <= maxLevel; i++)
					{

						res = dbm.executeSql("SELECT DISTINCT name FROM `"+exp_name+"_tree` WHERE rank="+rank+" AND level="+i+" AND time = (SELECT MAX(time) FROM `"+exp_name+"_tree` WHERE rank="+rank+" AND level="+i+")");
						
						String max_fun = ((HashMap)res.get(0)).get("NAME").toString();
						
						
						sql = "SELECT DISTINCT name, time, rank, count, father, level, tree_relation, eid FROM `"+exp_name+"_tree` WHERE rank="+rank+" AND level="+i+" ORDER BY id";
						res =  dbm.executeSql(sql);
					
						for (int j = 0; j < res.size(); j++)
						{
							HashMap hash = (HashMap)res.get(j);
							
							ArrayList temp = new ArrayList();
							int rank_num;
							
							String rank_list = "[";
							temp = dbm.executeSql("SELECT DISTINCT rank FROM `"+exp_name+"_tree` WHERE  tree_relation='"+hash.get("TREE_RELATION").toString()+"' ORDER BY rank;");
							
							int rank_array []= new int [temp.size()];
							for (int m = 0; m < temp.size(); m++)
								rank_array [m] = Integer.parseInt(((HashMap)temp.get(m)).get("RANK").toString());
							
							String name = hash.get("NAME").toString();
							String eid =  hash.get("EID").toString();
							double time = Double.parseDouble(hash.get("TIME").toString());
							int weight;
						
							if (time > 1000)
								weight = 20;
							else 
								weight = 1;
							
							nameMap.put(eid, name);
							
							String relation = hash.get("TREE_RELATION").toString();
							relation = relation.substring(1);
							String [] relationSplit = relation.split("\\.");
							
							if (relationSplit.length == 1)
								gv.addln("\""+name+"_"+i+"\"[shape=box,color=\"#A1C4F6\",style=filled,label=\""+name+"\\n"+hash.get("COUNT").toString()+"::"+time+"\",fontsize=25]");
							else
							{
								gv.addln("\""+nameMap.get(relationSplit[relationSplit.length - 2]).toString()+"_"+(i-1)+"\" -> \""+nameMap.get(relationSplit[relationSplit.length - 1]).toString()+"_"+i+"\";");
								if (name.equals(max_fun))
									gv.addln("\""+nameMap.get(relationSplit[relationSplit.length - 1]).toString()+"_"+i+"\""+"[shape=box,color=red,style=filled,label=\""+nameMap.get(relationSplit[relationSplit.length - 1]).toString()+"\\n"+hash.get("COUNT").toString()+"::"+time+"\",fontsize=25];");
								else
									gv.addln("\""+nameMap.get(relationSplit[relationSplit.length - 1]).toString()+"_"+i+"\""+"[shape=box,color=\"#A1C4F6\",style=filled,label=\""+nameMap.get(relationSplit[relationSplit.length - 1]).toString()+"\\n"+hash.get("COUNT").toString()+"::"+time+"\",fontsize=25];");
							}
							//System.out.println(relation+"--->"+relationSplit[0]+"[shape=box,label="+nameMap.get(relationSplit[1]).toString()+"\n"+time+"]");
							//String 
							
						}
					}
					System.out.println(nameMap.size());


				    gv.addln(gv.end_graph());
				    System.out.println(gv.getDotSource());

				    String webRoot = request.getSession().getServletContext().getRealPath("/");

				    File graph_out = new File(webRoot + "\\" + exp_name + "_" +rank+"_"+ dir +"_tree." + graph_type);		//windows
				    //File graph_out = new File(webRoot + "/" + exp_name + "_" +rank+"_"+ dir +"_tree." + graph_type);		//linux

				   
				    gv.writeGraphToFile( gv.getGraph( gv.getDotSource(), graph_type ), graph_out );
				}
				
			    data.put("url", exp_name + "_" + rank+"_"+ dir + "_tree." + graph_type);
			    out.write(data.toString());
			}
			else if (type.equals("get_exp_data"))							//鑾峰緱瀹為獙璇︾粏鏁版嵁
			{
				int i;
				int page_num = Integer.parseInt(request.getParameter("page"));
				int page_size = Integer.parseInt(request.getParameter("pagesize"));
				String exp_type = session.getAttribute("exp_type").toString();
				System.out.println(exp_type);
				
				JSONObject data = new JSONObject ();
				JSONArray data_list = new JSONArray ();
				
				ArrayList res = new ArrayList();
				res = dbm.executeSql("SELECT * FROM `"+exp_name+"_trace`;");
			
				for (i = (page_num - 1) * page_size; i < res.size() && i < page_num * page_size; i++)
				{
					System.out.println(i);
					System.out.println(res.get(i).toString());
					JSONObject temp = new JSONObject();
					HashMap hash = (HashMap)res.get(i);
					
					if (exp_type.equals("mpi"))
					{
						temp.put("rank", hash.get("RANK").toString());
					}
					else if (exp_type.equals("ompi"))
					{
						temp.put("mpi_rank", hash.get("MPI_RANK").toString());
						temp.put("omp_rank", hash.get("OMP_RANK").toString());
					}
					else if (exp_type.equals("cmpi"))
					{
						temp.put("mpi_rank", hash.get("MPI_RANK").toString());
					}
					temp.put("pid", hash.get("PID").toString());
					temp.put("eid", hash.get("EID").toString());
					temp.put("name", hash.get("NAME").toString());
					temp.put("type", hash.get("TYPE").toString());
					temp.put("comm", hash.get("COMM").toString());
					temp.put("time", hash.get("TIME").toString());
					temp.put("finish", hash.get("FINISH").toString());
					data_list.put(temp);
				}
				data.put("Rows", data_list);
				data.put("Total", res.size());
				System.out.println(data.toString());
				out.write(data.toString());
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
			doGet(request, response);
		}
		catch (Exception e) {
			System.err.println(e.toString());
		}	
	}
/*   public static void sendTempFile(File file, HttpServletResponse response)
           throws IOException {

       String mimeType = null;
       String filename = file.getName();
       if (filename.length() > 5) {
           if (filename.substring(filename.length() - 5,
                   filename.length()).equals(".jpeg")) {
               mimeType = "image/jpeg";
           }
           else if (filename.substring(filename.length() - 4,
                   filename.length()).equals(".png")) {
               mimeType = "image/png";
           }
       }
       sendTempFile(file, response, mimeType);
   }

   *//**
    * Binary streams the specified file to the HTTP response in 1KB chunks.
    *
    * @param file  the file to be streamed.
    * @param response  the HTTP response object.
    * @param mimeType  the mime type of the file, null allowed.
    *
    * @throws IOException if there is an I/O problem.
    *//*
   public static void sendTempFile(File file, HttpServletResponse response,
                                   String mimeType) throws IOException {

       if (file.exists()) {
           BufferedInputStream bis = new BufferedInputStream(
                   new FileInputStream(file));

           //  Set HTTP headers
           if (mimeType != null) {
               response.setHeader("Content-Type", mimeType);
           }
           response.setHeader("Content-Length", String.valueOf(file.length()));
           SimpleDateFormat sdf = new SimpleDateFormat(
                   "EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
           sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
           response.setHeader("Last-Modified",
                   sdf.format(new Date(file.lastModified())));

           BufferedOutputStream bos = new BufferedOutputStream(
                   response.getOutputStream());
           byte[] input = new byte[1024];
           boolean eof = false;
           while (!eof) {
               int length = bis.read(input);
               if (length == -1) {
                   eof = true;
               }
               else {
                   bos.write(input, 0, length);
               }
           }
           bos.flush();
           bis.close();
           bos.close();
       }
       else {
           throw new FileNotFoundException(file.getAbsolutePath());
       }
       return;
   }

   *//**
    * Perform a search/replace operation on a String
    * There are String methods to do this since (JDK 1.4)
    *
    * @param inputString  the String to have the search/replace operation.
    * @param searchString  the search String.
    * @param replaceString  the replace String.
    *
    * @return The String with the replacements made.
    *//*
   public static String searchReplace(String inputString,
                                      String searchString,
                                      String replaceString) {

       int i = inputString.indexOf(searchString);
       if (i == -1) {
           return inputString;
       }

       String r = "";
       r += inputString.substring(0, i) + replaceString;
       if (i + searchString.length() < inputString.length()) {
           r += searchReplace(inputString.substring(i + searchString.length()),
                   searchString, replaceString);
       }

       return r;
   }
   
   public void service(HttpServletRequest request,HttpServletResponse response)
		   throws ServletException, IOException {

	   HttpSession session = request.getSession();
	   String filename = request.getParameter("filename");

	   if (filename == null) {
		   throw new ServletException("Parameter 'filename' must be supplied");
	   }

	   //  Replace ".." with ""
	   //  This is to prevent access to the rest of the file system
	   filename = searchReplace(filename, "..", "");

	   //  Check the file exists
	   File file = new File(System.getProperty("java.io.tmpdir"), filename);
	   if (!file.exists()) {
		   throw new ServletException("File '" + file.getAbsolutePath()
				   + "' does not exist");
	   }
	   sendTempFile(file, response);

  }*/

}
