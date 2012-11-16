/* ------------------
 * XYSeriesDemo1.java
 * ------------------
 * (C) Copyright 2002-2009, by Object Refinery Limited.
 */

package graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


import org.jfree.chart.ChartFactory;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


import tools.DBManager;

/**
 * A simple demo showing a dataset created using the {@link XYSeriesCollection}
 * class.
 */
public class MultiLineChart {
	public static JFreeChart createMultiLineChart(String exp, String hostname, String metric, String instance) throws Exception
	{		
		 JFreeChart chart = createChart(createDataset(exp, hostname, metric, instance),exp);
		return chart;
	}	 

    private static JFreeChart createChart(XYDataset dataset, String exp) {
        JFreeChart chart = ChartFactory.createXYLineChart(
            "",
            "",
            "",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setDomainPannable(true);
        plot.setRangePannable(true);

        plot.setBackgroundPaint(null);
        List axisIndices = Arrays.asList(new Integer[] {new Integer(0),
                new Integer(1)});
        plot.mapDatasetToDomainAxes(0, axisIndices);
        plot.mapDatasetToRangeAxes(0, axisIndices);
        
        
        
        ChartUtilities.applyCurrentTheme(chart);
        return chart;
    }

    /**
     * Creates a sample dataset.
     *
     * @return A sample dataset.
     * @throws Exception 
     */
    private static XYDataset createDataset(String expName, String hostName, String metric, String category) throws Exception {
       
        DBManager dba=new DBManager();
        XYSeriesCollection result = new XYSeriesCollection();
        
    	String sql="SELECT DISTINCT instance FROM "+expName+"_proc WHERE instance LIKE '"+ category +"%';";
    	System.out.println(sql);
   		ArrayList instanceList=dba.executeSql(sql);
   		
    	String instance[] = new String[instanceList.size()];   
    	System.out.println(instanceList.size());
   		for(int i=0;i<instanceList.size();i++)
   		{
   			HashMap hash=(HashMap)instanceList.get(i);
   			instance[i]=hash.get("INSTANCE").toString();   	
   			
   		}
   
   		XYSeries series[] = new XYSeries[instanceList.size()];
        
   		for(int i=0;i<instanceList.size();i++)
   		{ 
   			series[i] = new XYSeries(instance[i]);
   			sql="SELECT time_s*100000+time_us as time, data from "+ expName 
   					+"_proc WHERE instance = '"+instance[i]
   					+"' and metric = '"+ metric +"' and hostname = '"+hostName+"' ORDER BY time_s,time_us;";
   					
   			ArrayList resultList=dba.executeSql(sql);
	      		       
	  	    for(int j=0;j<resultList.size();j++)
	  	    {
	     		HashMap hash=(HashMap)resultList.get(j);
	     		double time=Double.parseDouble(hash.get("TIME").toString());
	     		double data=Double.parseDouble(hash.get("DATA").toString());
	     		
	     		series[i].add(time,data);
	  	    }  
	  	    result.addSeries(series[i]);
   		}
        return result;
    }
}
