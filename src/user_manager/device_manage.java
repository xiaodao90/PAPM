package user_manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.*;

import DBTool.DBManager;
public class device_manage extends HttpServlet{
	public void doGet (HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {

		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/json;charset=UTF-8");
		response.setHeader("ContentType", "text/json"); 
		response.setCharacterEncoding("UTF-8"); 
		ServletOutputStream out = response.getOutputStream();
		try{
			DBManager dbm = new DBManager ();
			String req_type = request.getParameter("req_type");
			
			if (req_type.equals("Getinfo"))
			{
				int i;
				ArrayList res = new ArrayList();
				JSONObject info_obj = new JSONObject ();
				JSONArray info_list = new JSONArray ();
			
				String type = request.getParameter("type");
				String dep = new String (request.getParameter("dep").getBytes("ISO-8859-1"),"utf-8");
				String servroom = request.getParameter("servroom");
				String cabinet = request.getParameter("cab");
				String location = request.getParameter("location");
				String resp = new String (request.getParameter("resp").getBytes("ISO-8859-1"),"utf-8");
				String business = request.getParameter("bus");
				String ip = request.getParameter("ip");
				String brand = request.getParameter("brand");
				String setid = request.getParameter("setid");
				
				if (dep.equals("全部单位"))
					dep = "like '%'";
				else 
					dep = "='" + dep +"'";
				
				if (servroom.equals("all"))
					servroom = "like '%'";
				else 
					servroom = "='" + servroom +"'";
				
				if (cabinet.equals(""))
					cabinet = "like '%'";
				else 
					cabinet = "='" + cabinet +"'";
				
				if (location.equals(""))
					location = "like '%'";
				else 
					location = "='" + location +"'";
				
				if (resp.equals(""))
					resp = "like '%'";
				else 
					resp = "='" + resp +"'";
				
				if (business.equals(""))
					business = "like '%'";
				else 
					business = "='" + business +"'";
				
				if (ip.equals(""))
					ip = "like '%'";
				else 
					ip = "='" + ip+"'";
						
				if (brand.equals(""))
					brand = "like '%'";
				else 
					brand = "='" + brand +"'";
				
				if (setid.equals(""))
					setid = "like '%'";
				else 
					setid = "='" + setid +"'";
				
				if (type.equals("all"))
				{
					String [] typelist = {"ServerInfo", "NetworkInfo", "StorageInfo", "ControlInfo", "MonitorInfo"};
					int num = 0;
					
					for (i = 0; i < 5; i++)
					{
						if (typelist[i].equals("StorageInfo") || typelist[i].equals("MonitorInfo"))
							res = dbm.executeSql("SELECT * FROM "+typelist[i]+" WHERE Department "+dep+" AND ServRoom "+servroom+" AND CabinetID "+cabinet+" AND Position "+location+" AND respPerson "+resp+" AND business "+business+" AND Brand "+brand+" AND SetID "+setid+";");
						else 
							res = dbm.executeSql("SELECT * FROM "+typelist[i]+" WHERE Department "+dep+" AND ServRoom "+servroom+" AND CabinetID "+cabinet+" AND Position "+location+" AND respPerson "+resp+" AND business "+business+" AND IP "+ip+" AND Brand "+brand+" AND SetID "+setid+";");
						for (int j = 0; j < res.size(); j++, num++)
						{
							HashMap hash = (HashMap)res.get(j);
							
							JSONObject temp = getData(typelist[i], hash);
							temp.put("id", num);
							info_list.put(temp);
						}
					}
					info_obj.put("Rows", info_list);
					info_obj.put("Total", num);
					System.out.println(num);
					System.out.println(info_obj.toString());				
					out.write((info_obj.toString()).getBytes("UTF-8"));
				}
				else
				{
					if (type.equals("StorageInfo") || type.equals("MonitorInfo"))
						res = dbm.executeSql("SELECT * FROM "+type+" WHERE Department "+dep+" AND ServRoom "+servroom+" AND CabinetID "+cabinet+" AND Position "+location+" AND respPerson "+resp+" AND business "+business+" AND Brand "+brand+" AND SetID "+setid+";");
					else 
						res = dbm.executeSql("SELECT * FROM "+type+" WHERE Department "+dep+" AND ServRoom "+servroom+" AND CabinetID "+cabinet+" AND Position "+location+" AND respPerson "+resp+" AND business "+business+" AND IP "+ip+" AND Brand "+brand+" AND SetID "+setid+";");
					for (i = 0; i < res.size(); i++)
					{
						HashMap hash = (HashMap)res.get(i);
						
						JSONObject temp = getData(type, hash);
						temp.put("id", i);
						info_list.put(temp);
					}
					info_obj.put("Rows", info_list);
					info_obj.put("Total", i);
					System.out.println(info_obj.toString());				
					out.write((info_obj.toString()).getBytes("UTF-8"));
				}
			}
		}
		catch (Exception e) {
            System.err.println(e.toString());
        }
        finally {
            out.close();
        }
	}
	
	JSONObject getData (String type, HashMap hash) throws JSONException
	{
		JSONObject temp = new JSONObject ();
				
		temp.put("HostName",isNull(hash.get("HOSTNAME")));
		temp.put("ServRoom",isNull(hash.get("SERVROOM")));
		temp.put("CabinetID",isNull(hash.get("CABINETID")));
		temp.put("Position",isNull(hash.get("POSITION")));
		temp.put("DeviceType",isNull(hash.get("DEVICETYPE")));	
		temp.put("IfTrusteeship",isNull(hash.get("IFTRUSTEESHIP")));
		temp.put("Brand",isNull(hash.get("BRAND")));
		temp.put("Ifabroad",isNull(hash.get("IFABROAD")));
		temp.put("Model",isNull(hash.get("MODEL")));
		temp.put("DeviceSize",isNull(hash.get("DEVICESIZE")));
		temp.put("SpaceDevice",isNull(hash.get("SPACEDEVICE")));
		
		
		/*temp.put("IP",isNull(hash.get("IP")));*/
		
		
		temp.put("Department",isNull(hash.get("DEPARTMENT")));
		temp.put("respPerson",isNull(hash.get("RESPPERSON")));
		temp.put("business",isNull(hash.get("BUSINESS")));
		temp.put("Function",isNull(hash.get("FUNCTION")));
		
		/*temp.put("Mac",isNull(hash.get("MAC")));
		temp.put("IfHA",isNull(hash.get("IFHA")));*/
		
		temp.put("busiResPerson",isNull(hash.get("BUSIRESPERSON")));
		temp.put("HardTele",isNull(hash.get("HARDTELE")));
		temp.put("BusiTele",isNull(hash.get("BUSITELE")));
		temp.put("DeviceSeries",isNull(hash.get("DEVICESERIES")));
		temp.put("SetID",Float.parseFloat(isNull(hash.get("SETID"))));
		temp.put("LoginDate",isNull(hash.get("LOGINDATE")));
		temp.put("UserPerson",isNull(hash.get("USERPERSON")));
		temp.put("UseStatus",isNull(hash.get("USESTATUS")));
		temp.put("ValueType",isNull(hash.get("VALUETYPE")));
		temp.put("ConID",isNull(hash.get("CONID")));
		temp.put("SupplName",isNull(hash.get("SUPPLNAME")));
		temp.put("SuppCon",isNull(hash.get("SUPPCON")));
		temp.put("SuppTele",isNull(hash.get("SUPPTELE")));
		temp.put("warranty",isNull(hash.get("WARRANTY")));
		temp.put("DeviceWeight",isNull(hash.get("DEVICEWEIGHT")));
		temp.put("FixPattern",isNull(hash.get("FIXPATTERN")));
		temp.put("LoadWeight",isNull(hash.get("LOADWEIGHT")));
		temp.put("PowerNum",isNull(hash.get("POWERNUM")));
		temp.put("PowerPattern",isNull(hash.get("POWERPATTERN")));
		temp.put("PowerLinePatt",isNull(hash.get("POWERLINEPATT")));
		temp.put("Vol",isNull(hash.get("VOL")));
		temp.put("Ampere",isNull(hash.get("AMPERE")));
		temp.put("RatedPower", isNull(hash.get("RATEDPOWER")));
		temp.put("zeroVoltage",isNull(hash.get("ZEROVOLTAGE")));
		temp.put("WorkTemp",isNull(hash.get("WORKTEMP")));
		temp.put("Workhumidity",isNull(hash.get("WORKHUMIDITY")));
		temp.put("Heat",isNull(hash.get("HEAT")));
		temp.put("Wind",isNull(hash.get("WIND")));
		temp.put("repairDate",isNull(hash.get("REPAIRDATE")));
		temp.put("RepaireTime",isNull(hash.get("REPAIRETIME")));
		
		if (type.equals("ServerInfo"))
		{
			temp.put("table_name", "ServerInfo");
			temp.put("DeviceID",isNull(hash.get("SERVERID")));
			
			temp.put("IP",isNull(hash.get("IP")));
			temp.put("Mac",isNull(hash.get("MAC")));
			temp.put("IfHA",isNull(hash.get("IFHA")));
			
			temp.put("Memory",isNull(hash.get("MEMORY")));
			temp.put("CPUType",isNull(hash.get("CPUTYPE")));
			temp.put("CPUcount",isNull(hash.get("CPUCOUNT")));
			temp.put("DiskCount",isNull(hash.get("DISKCOUNT")));
			temp.put("DiskVolume",isNull(hash.get("DISKVOLUME")));
			temp.put("OS",isNull(hash.get("OS")));
			temp.put("Value", isNull(hash.get("VALUE")));
			temp.put("Deadline", isNull(hash.get("DEADLINE")));
			temp.put("Datafrom", isNull(hash.get("DATAFROM")));
			temp.put("Datato", isNull(hash.get("DATATO")));
		}
		else if (type.equals("NetworkInfo"))
		{
			temp.put("table_name", "NetworkInfo");
			temp.put("DeviceID",isNull(hash.get("NETWORKID")));
			
			temp.put("IP",isNull(hash.get("IP")));
			temp.put("Mac",isNull(hash.get("MAC")));
			temp.put("IfHA",isNull(hash.get("IFHA")));
			
			temp.put("Memory",isNull(hash.get("MEMORY")));
			temp.put("CPUType",isNull(hash.get("CPUTYPE")));
			temp.put("CPUcount",isNull(hash.get("CPUCOUNT")));
			temp.put("OS",isNull(hash.get("OS")));
			temp.put("NetworkCode", isNull(hash.get("NETWORKCODE")));
			temp.put("boardCount", isNull(hash.get("BOARDCOUNT")));
			temp.put("BoardSeri", isNull(hash.get("BOARDSERI")));
			temp.put("SpaceCount", isNull(hash.get("SPACECOUNT")));
			temp.put("PortCount", isNull(hash.get("PORTCOUNT")));
			temp.put("FreeCount", isNull(hash.get("FREECOUNT")));
			temp.put("InterfaceType", isNull(hash.get("INTERFACETYPE")));
			temp.put("Value", isNull(hash.get("VALUE")));
		}
		else if (type.equals("StorageInfo"))
		{
			temp.put("table_name", "StorageInfo");
			temp.put("DeviceID",isNull(hash.get("STORAGEID")));
			
			temp.put("IP","—");
			temp.put("Mac",isNull(hash.get("MAC")));
			temp.put("IfHA",isNull(hash.get("IFHA")));
			
			temp.put("Memory",isNull(hash.get("MEMORY")));
			temp.put("CPUType",isNull(hash.get("CPUTYPE")));
			temp.put("CPUcount",isNull(hash.get("CPUCOUNT")));
			temp.put("DiskCount",isNull(hash.get("DISKCOUNT")));
			temp.put("DiskVolume",isNull(hash.get("DISKVOLUME")));
			temp.put("OS",isNull(hash.get("OS")));
			temp.put("Value", isNull(hash.get("VALUE")));
		}
		else if (type.equals("ControlInfo"))
		{
			temp.put("table_name", "ControlInfo");
			temp.put("DeviceID",isNull(hash.get("CONTROLID")));
			
			temp.put("IP",isNull(hash.get("IP")));
			temp.put("ConnectDevice", isNull(hash.get("CONNECTIONDEVICE")));
			temp.put("FreePort", isNull(hash.get("FREEPORT")));
			temp.put("InterfaceType", isNull(hash.get("INTERFACETYPE")));
			temp.put("Value", isNull(hash.get("VALUE")));
		}
		else if (type.equals("MonitorInfo"))
		{
			temp.put("table_name", "MonitorInfo");
			temp.put("DeviceID",isNull(hash.get("MONITORID")));
			
			temp.put("IP","—");
			temp.put("InterfaceType",isNull(hash.get("INTERFACETYPE")));
			temp.put("Resolution", isNull(hash.get("RESOLUTION")));
		}
		
		return temp;
	}
	
	
	String isNull (Object obj)
	{
		if (obj == null)
			return "无";
		else if (obj.toString().equals(""))
			return "无";
		else 
			return obj.toString();
	}
}
