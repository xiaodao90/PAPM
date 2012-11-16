package tools;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;



public class ConnectionTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		
		
            DBManager dba=new DBManager();
            //String sql="insert into user values('4','dd','ddd')";
            String sql="call proc2spider('io')";
            
            String params[]={"io"};
    		
    		ArrayList resultList=dba.executeSql(sql);
      
    	  	String category[]={"io", "cpu", "mem", "net"};
    	  	
    	  	String test="call proc2spider('" + category[0] + "');";
    	  			
            System.out.println(test);
         
            
           //ArrayList resultList=dba.executeSql(sql);
           
           
           System.out.println(resultList.size());
           
           for(int i=0;i<resultList.size();i++){
        	 HashMap hash=(HashMap)resultList.get(i);
         
         
  			 String name=hash.get("RESULT").toString();
  			 System.out.println("the name is-----"+name);
  
             
           }
            
       

	}

}
