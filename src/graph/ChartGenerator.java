

package graph;


import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import tools.DBManager;
import tools.EpsServletUtilities;


import java.util.ArrayList;

import java.util.HashMap;


/**
 * A servlet that returns one of three charts as a PNG image file.  This servlet is
 * referenced in the HTML generated by ServletDemo2.
 * <P>
 * Three different charts can be generated, controlled by the 'type' parameter.  The possible
 * values are 'pie', 'bar' and 'time' (for time series).
 * <P>
 * This class is described in the JFreeChart Developer Guide.
 */
public class ChartGenerator extends HttpServlet {

    /**
     * Default constructor.
     */
    public ChartGenerator() {
        // nothing required
    }

    /**
     * Process a GET request.
     *
     * @param request  the request.
     * @param response  the response.
     *
     * @throws ServletException if there is a servlet related problem.
     * @throws IOException if there is an I/O problem.
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) 
    		throws ServletException, IOException
    {

        OutputStream out = response.getOutputStream();
        try {
            String type = request.getParameter("type");
            HttpSession session = request.getSession();
			String exp_name = session.getAttribute("exp_name").toString();
      
            String format = request.getParameter("format");
            System.out.println("<-----------"+format+"---------->");
           // String exp_name = request.getParameter("exp_name");
          //  String pid =request.getParameter("pid");
            JFreeChart chart = null;
           
            if (type.equals("pie")) {
                chart = PieChart.createPieChart(exp_name);
            }
            else if (type.equals("bar")) {
            	String pid =request.getParameter("pid");
                chart = BarChart2D.createBarChart(exp_name,Integer.parseInt(pid));
            }
            else if (type.equals("stack2d")) {
                chart = StackedBarChart2D.createStackedBarChart2D(exp_name);
            }
            else if (type.equals("time")) {
                chart = TimeSeriesChart.createTimeSeriesChart(exp_name);
            }
            else if (type.equals("chess")){
            	chart = ChessChart.createChessChart (exp_name);
            }
            else if (type.equals("spider")){
            	chart = SpiderChart.createSpiderChart (exp_name);
            }
            else if (type.equals("cross")){
            	chart = CrossChart.createCrossChart (exp_name);
            }
            
            else if (type.equals("singleline")){
            	String hostname = request.getParameter("host_name");
            	String metric = request.getParameter("metric");
            	String instance = request.getParameter("instance");
            	chart = SingleLineChart.createSingleLineChart (exp_name, hostname, metric, instance);
            }
            
            else if (type.equals("multiline")){
            	String hostname = request.getParameter("host_name");
            	String metric = request.getParameter("metric");
            	String instance = request.getParameter("instance");
            	System.out.println(hostname+metric+instance);
            	chart = MultiLineChart.createMultiLineChart (exp_name, hostname, metric, instance);
            }
            
            if (chart != null && format.equals("png")) {
            	response.setContentType("image/png");
                if (   type.equals("pie") 
                	|| type.equals("time") 
                	|| type.equals("chess")
                	|| type.equals("spider")
                	|| type.equals("cross")
                	|| type.equals("multiline"))
                {
                	ChartUtilities.writeChartAsPNG(out, chart, 800, 500);
                }
                else if (type.equals("singleline"))
                	ChartUtilities.writeChartAsPNG(out, chart, 450, 220);
                else if (  type.equals("bar") ) 
                {
                    ChartUtilities.writeChartAsPNG(out, chart, 800, 30 * getCount(exp_name + "_profile","eid"));
                }
                else if (  type.equals("stack2d") ) 
                {
                    ChartUtilities.writeChartAsPNG(out, chart, 800, 30 * getCount(exp_name + "_profile","pid"));
                    
                }
               
                else
                {
                	System.out.println("Chart is not in ChartGenerator");
                	
                }
                
                
            }
            else if (chart != null && format.equals("eps")) {
            	
        
            	String epsPath=EpsServletUtilities.saveChartAsEPS(chart, 640, 400, request.getSession());
            	response.sendRedirect("/ParaInsight/downloadEPS.jsp?path="+epsPath);
            	
            	
            	//response.setContentType("text/html");
            	//out.write(epsPath.getBytes());            	
            }            
        }
        catch (Exception e) {
        	
            e.printStackTrace();
        }
        finally {
            out.close();           
        }
    }
    
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
    	doGet(request,response);
    }
   
    private int getCount(String tableName,String columnName) throws Exception
    {
    	
    	DBManager dba=new DBManager();
   	
       	String sql="SELECT COUNT(DISTINCT "+ columnName +") AS COUNT FROM " + tableName +";";
  	    ArrayList resultList=dba.executeSql(sql);
      	
       
  	    if(resultList.size() != 0)
  	    {
     		HashMap hash=(HashMap)resultList.get(0);
     		String result=hash.get("COUNT").toString();
     		return Integer.parseInt(result);
     		
  	    } 
  	    else
  	    {
  	    	System.out.println("Cannot Get Count!");
  	    	return -1;
  	    }
    }

}
