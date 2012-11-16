package DBTool;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;


public class ConnectionTest {
	
	

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
	
		/*DBManager dbm = new DBManager ();
		String type = "get_profile_data";
		String exp_name = "mpitrace";
		
		if (type.equals("get_profile_data"))
		{
			JSONArray profile_data = new JSONArray ();
			ArrayList res = dbm.executeSql("SELECT DISTINCT name, eid FROM `"+exp_name+"_profile` ORDER BY eid;");
			int e_length = res.size(); 

			String[] func = new String [e_length];
			for (int i = 0; i < e_length; i++)
			{
				HashMap hash = (HashMap)res.get(i);

				func[i] = hash.get("NAME").toString();
			}
			
			res = dbm.executeSql("SELECT DISTINCT pid FROM `"+exp_name+"_profile`;");
		//	String[] pid = new String [res.size()];
			
			for (int i = 0; i < res.size(); i++)
			{
				HashMap hash = (HashMap)res.get(i);
				String pid = hash.get("PID").toString();
				JSONObject pid_temp = new JSONObject ();
				
				ArrayList temp = dbm.executeSql("SELECT name ,sumTime FROM `"+exp_name+"_profile` WHERE pid="+pid+" ORDER BY eid ;");
				if (e_length == temp.size())
				{
					JSONArray data_temp = new JSONArray ();
					for (int j = 0; j < temp.size(); j++)
					{
						
						data_temp.put(((HashMap)temp.get(j)).get("SUMTIME").toString());
					}
					pid_temp.put(pid, data_temp);
					profile_data.put(pid_temp);
					//System.out.println(data_temp.toString());
				}
				else 
				{
					JSONArray data_temp = new JSONArray ();
					for (int j = 0,k = 0; j < temp.size();)
					{
						if (((HashMap)temp.get(j)).get("NAME").toString().equals(func[k]))
						{
							data_temp.put(((HashMap)temp.get(j)).get("SUMTIME").toString());
							j++;
							k++;
						}
						else
						{
							data_temp.put("0");
							k++;
						}
					}
					//System.out.println(data_temp.toString());
					pid_temp.put(pid, data_temp);
					profile_data.put(pid_temp);
				}
			}
			System.out.println(profile_data.toString());
		}*/

	}

}
