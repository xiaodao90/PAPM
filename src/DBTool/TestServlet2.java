package DBTool;


import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.*;
import javax.servlet.http.*;



public class TestServlet2 extends HttpServlet {

  
    public static void main(HttpServletRequest req, HttpServletResponse res)    throws IOException
    {   
    	
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        try {
        	DBManager dba = null;
        		String sql="call proc2spider('io')";
        		
        		ArrayList resultList=dba.executeSql(sql);

                   for(int i=0;i<resultList.size();i++)
           {
        	   	 HashMap hash=(HashMap)resultList.get(i);
    			 String name=hash.get("RESULT").toString();
    			 out.println("the name is-----"+name);
    	   }           
           
        }
        catch (Exception e) {
            e.printStackTrace(out);
        }
       
    }

    public void destroy() {
        
        super.destroy();
    }
}