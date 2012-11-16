/* ------------------
 * XYSeriesDemo1.java
 * ------------------
 * (C) Copyright 2002-2009, by Object Refinery Limited.
 */

package graph;

import java.awt.Color;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.jfree.chart.ChartFactory;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


import tools.DBManager;


public class SingleLineChart  {
	
	public static JFreeChart createSingleLineChart(String exp, String hostname, String metric, String instance) throws Exception
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
            false,
            false,
            false
        );
        chart.setBorderVisible(false);
        chart.setAntiAlias(true);
        chart.setBackgroundPaint(Color.getHSBColor((float)0, (float)0, (float)0.96));
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setDomainPannable(true);
        plot.setRangePannable(true);
        
        
        
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.WHITE); //网格线颜色
        plot.setNoDataMessage("No data!");
        plot.setOutlinePaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.WHITE);
       
        plot.setBackgroundPaint(Color.getHSBColor((float)0, (float)0.01, (float)0.91));
        
        //xylineandshaperenderer.setShapesFilled(true);
        NumberAxis axis = (NumberAxis)plot.getRangeAxis();	
        axis.setAutoRange(true);
        axis.setPositiveArrowVisible(true);
        axis.setAutoRangeIncludesZero(true);
        axis.setAutoRangeStickyZero(true);
        
        // List axisIndices = Arrays.asList(new Integer[] {new Integer(0),
        //        new Integer(1)});
       // plot.mapDatasetToDomainAxes(0, axisIndices);
        //plot.mapDatasetToRangeAxes(0, axisIndices);
        //ChartUtilities.applyCurrentTheme(chart);
        return chart;
    }


    private static XYDataset createDataset(String expName, String hostName, String metric, String category) throws Exception {
       
        DBManager dba=new DBManager();   		
   		XYSeries series = new XYSeries(category);      
   	
      	String sql="SELECT time_s*100000+time_us as time, data from "+ expName +"_proc WHERE instance = '"+category+"' and metric = '"+ metric +"' and hostname = '"+hostName+"' ORDER BY time_s,time_us;";
  	  //  System.out.println(sql);
      	ArrayList resultList=dba.executeSql(sql);     	
       
  	    for(int i=0;i<resultList.size();i++)
  	    {
     		HashMap hash=(HashMap)resultList.get(i);
     		double time=Double.parseDouble(hash.get("TIME").toString());
     		double data=Double.parseDouble(hash.get("DATA").toString());
     		System.out.println(time+" and "+data);
     		series.add(time,data);
      	}  
  	  
  	    XYSeriesCollection result = new XYSeriesCollection(series);
        return result;
    }


}
