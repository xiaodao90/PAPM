/* ---------------------------
 * StackedBarChart3DDemo4.java
 * ---------------------------
 * (C) Copyright 2002-2007, by Object Refinery Limited.
 *
 */

package graph;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;


import org.jfree.chart.ChartFactory;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;

import tools.DBManager;

/**
 * A simple demonstration application showing how to create a stacked 3D bar
 * chart using data from a {@link CategoryDataset}.
 */
public class StackedBarChart2D {

	public static JFreeChart createStackedBarChart2D(String exp) throws Exception
	{		
		JFreeChart chart = createChart(createDataset(exp),exp);
		return chart;
	}	
	
    private static CategoryDataset createDataset(String exp) throws Exception {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        DBManager dba=new DBManager();
    	
 
      	String sql="SELECT pid,name,sumTime FROM "+exp+"_profile ORDER BY sumTime DESC;";
  	    ArrayList resultList=dba.executeSql(sql);
      	
       
  	    for(int i=0;i<resultList.size();i++)
  	    {
     		HashMap hash=(HashMap)resultList.get(i);
     		String pid=hash.get("PID").toString();
     		String name=hash.get("NAME").toString();
     		double sumTime=Double.parseDouble(hash.get("SUMTIME").toString());
     		dataset.addValue(sumTime, name, pid);
  	    } 
        return dataset;
    }

    /**
     * Creates a chart.
     *
     * @param dataset  the dataset.
     *
     * @return The chart.
     */
    private static JFreeChart createChart(CategoryDataset dataset,String exp) {

        JFreeChart chart = ChartFactory.createStackedBarChart(
            "",  // chart title
            "PID",                   // domain axis label
            "TIME",                      // range axis label
            dataset,                      // data
            PlotOrientation.HORIZONTAL,     // the plot orientation
            false,                         // include legend
            true,                         // tooltips
            false);                       // urls
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        
       plot.setBackgroundPaint(null);
      //  plot.setBackgroundAlpha(0.5f);
      
       
        plot.setRangeGridlinePaint(Color.black);
        
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLowerMargin(0.001);//设置距离图片左端距离此时为10%
        domainAxis.setUpperMargin(0.001);//设置距离图片右端距离此时为百分之10
        domainAxis.setCategoryLabelPositionOffset(10);//图表横轴与标签的距离(10像素)
        domainAxis.setCategoryMargin(0.2);//横轴标签之间的距离20%
        
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setNumberFormatOverride(new DecimalFormat("0%"));
        yAxis.setRange(0, 1.0D);
        StackedBarRenderer renderer =
            (StackedBarRenderer) plot.getRenderer();
        renderer.setRenderAsPercentages(true);
        renderer.setShadowVisible(false);
        renderer.setBarPainter( new StandardBarPainter() );
       
        renderer.setBaseOutlinePaint(Color.black);
        
        renderer.setDrawBarOutline(true);
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator(
                "{2}", NumberFormat.getIntegerInstance(),
                new DecimalFormat("0.0%")));
        renderer.setBaseItemLabelsVisible(true);
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
                ItemLabelAnchor.CENTER, TextAnchor.CENTER));
        renderer.setBaseNegativeItemLabelPosition(new ItemLabelPosition(
                ItemLabelAnchor.CENTER, TextAnchor.CENTER));
        
        LegendTitle legend = new LegendTitle(plot);

        legend.setPosition(RectangleEdge.TOP);
        legend.setHorizontalAlignment(HorizontalAlignment.CENTER);
        
        chart.addSubtitle(legend);
        return chart;

    }

}
