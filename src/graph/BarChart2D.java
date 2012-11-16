package graph;

import java.awt.Color;
import com.jspsmart.upload.SmartUpload;

import java.awt.Paint;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.TextAnchor;

import tools.DBManager;


public class BarChart2D {
	
		static class CustomBarRenderer extends BarRenderer {	
			private Paint[] colors;
	
			public CustomBarRenderer(Paint[] colors) {
				this.colors = colors;
		    }
		
		        /**
		         * Returns the paint for an item.  Overrides the default behaviour
		         * inherited from AbstractSeriesRenderer.
		         *
		         * @param row  the series.
		         * @param column  the category.
		         *
		         * @return The item color.
		         */
			public Paint getItemPaint(int row, int column) {
				return this.colors[column % this.colors.length];
			}
		}
		
		public static JFreeChart createBarChart(String exp,int pid) throws Exception
		{   
			JFreeChart chart = createChart(createDataset(exp, pid), createPaint(exp, pid), exp, pid);
			return chart;
		}


	    private static CategoryDataset createDataset(String exp,int pid) throws Exception {

	        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	        DBManager dba=new DBManager();
	 
	      	String sql="SELECT name,sumTime FROM "+exp+"_profile WHERE pid="+pid+" ORDER BY sumTime DESC;";
	  	    ArrayList resultList=dba.executeSql(sql);
	      	
	       
	  	    for(int i=0;i<resultList.size();i++)
	  	    {
	     		HashMap hash=(HashMap)resultList.get(i);
	     		String name=hash.get("NAME").toString();
	     		double sumTime=Double.parseDouble(hash.get("SUMTIME").toString());
	     		dataset.addValue(sumTime, "S1", name);
	     		
	 
	  	    }
	        return dataset;
	    }
	    
	    
	    private static Paint[] createPaint(String exp, int pid) throws Exception{
	    	DBManager dba = new DBManager ();
	    	String sql = "SELECT color  FROM `"+exp+"_def`, `"+exp+"_profile` WHERE pid="+pid+" AND name = fun_name ORDER BY sumTime DESC; ";
	    	ArrayList res = dba.executeSql(sql);
	        Paint[] colors = new Paint[res.size()];
	        for (int i = 0; i < res.size(); i++)
	        {
	        	HashMap temp = (HashMap)res.get(i);
	        	colors[i] = Color.decode(temp.get("COLOR").toString());
	        }
	        
	        return colors;
	    }

	   
	    private static JFreeChart createChart(CategoryDataset dataset,Paint[] colors,String exp,int pid) {

	        // create the chart...
	        JFreeChart chart = ChartFactory.createBarChart(
	            "pid: " + pid,         // chart title
	            "Functino",               // domain axis label
	            "Time Elapsed",                  // range axis label
	            dataset,                  // data
	            PlotOrientation.HORIZONTAL, // orientation
	            false,                     // include legend
	            true,                     // tooltips?
	            false                     // URLs?
	        );


	        CategoryPlot plot = (CategoryPlot) chart.getPlot();
	        plot.setRangeGridlinePaint(Color.gray);
	        plot.setBackgroundPaint(null);
	        plot.setOutlinePaint(null);
	        
	        CategoryAxis domainAxis = plot.getDomainAxis();
	        domainAxis.setLowerMargin(0.02);//设置距离图片左端距离此时为10%
	        domainAxis.setUpperMargin(0.02);//设置距离图片右端距离此时为百分之10

	        
	        // set the range axis to display integers only...
	        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());        
	      
	        CustomBarRenderer customRenderer = new CustomBarRenderer(colors);
	        plot.setRenderer(customRenderer);
	        
	        BarRenderer renderer = (BarRenderer)plot.getRenderer();        
	        renderer.setBaseItemLabelsVisible(true);        
	        CategoryItemLabelGenerator generator
	        = new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getInstance());
	        
	        renderer.setBaseItemLabelGenerator(generator);        
	        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE3, TextAnchor.CENTER_LEFT));
	        renderer.setItemLabelAnchorOffset(5.0D);
	        
	        renderer.setDrawBarOutline(true);
	        renderer.setShadowVisible(false);
	        renderer.setBarPainter( new StandardBarPainter() );
	  
	        return chart;

	    }

}
