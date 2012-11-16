package user_manager;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.*;

import DBTool.DBManager;

public class protovisChart extends HttpServlet{
	public void doGet (HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		
		try{
			DBManager dbm = new DBManager ();
			String type = request.getParameter("req_type");
			HttpSession session = request.getSession();
			String exp_name = session.getAttribute("exp_name").toString();
			
			if (type.equals("get_chess_data"))
			{
				JSONObject chess_data = new JSONObject ();
				JSONArray data = new JSONArray();
				int maxsize = 0, minsize = 0;
				//ArrayList res = dbm.executeSql("SELECT DISTINCT rank from "+exp_name+"_comm WHERE eid =83 GROUP BY src_rank,dst_rank;");
				ArrayList res = dbm.executeSql("SELECT DISTINCT src_rank as rank FROM `"+exp_name+"_comm` WHERE eid='83' || eid='86' || eid='76' || eid='61' ||eid='71' || eid='81' ||eid='74'  UNION SELECT dst_rank as rank FROM `"+exp_name+"_comm` WHERE eid='83' || eid='86' || eid='76' || eid='61' ||eid='71' || eid='81' ||eid='74' ORDER BY rank;");
				
				int num = res.size();
			
				int [][] comm = new int [res.size()][res.size()];
				JSONArray rank_list =  new JSONArray();
				JSONObject rank_obj = new JSONObject();
				
				rank_list.put("Name");
				for (int i = 0; i < num; i++)
				{
					rank_list.put("Rank"+i);
					for (int j = 0; j < num; j++)
						comm[i][j] = 0;
				}
				data.put(rank_list);
			//	System.out.println("-----"+num);
				res = dbm.executeSql("SELECT  src_rank,dst_rank, SUM(send_size) AS sum from `"+exp_name+"_comm` WHERE eid='83' || eid='86' || eid='76' || eid='61' ||eid='71' || eid='81' ||eid='74' GROUP BY src_rank,dst_rank;");
				
				for (int i = 0; i < res.size(); i++)
				{
					HashMap hash = (HashMap)res.get(i);
					int src = Integer.parseInt(hash.get("SRC_RANK").toString());
					int dst = Integer.parseInt(hash.get("DST_RANK").toString());
					int data_size = Integer.parseInt(hash.get("SUM").toString());
					if (maxsize < data_size)
						maxsize = data_size;	
					if (minsize ==0 || minsize > data_size)
						minsize = data_size;
					comm[src][dst] = data_size;
				}
				
				for (int i = 0; i < num; i++)
				{
					JSONArray temp = new JSONArray();
					temp.put("Rank"+i);
					for (int j = 0; j < num; j++)
					{
						temp.put(comm[i][j]);
					}
					data.put(temp);
				}
				chess_data.put("data", data);
				chess_data.put("min", minsize);
				chess_data.put("max", maxsize);
				out.write(chess_data.toString());
				System.out.println(chess_data.toString());
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
