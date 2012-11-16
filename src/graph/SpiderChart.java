package graph;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.servlet.ServletUtilities;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleEdge;

import tools.DBManager;

public class SpiderChart {
	

	public static JFreeChart createSpiderChart(String exp) throws Exception{
		JFreeChart chart = createChart(createDataset(exp),exp);     
		return chart;
	  }
		       
			
			
    private static CategoryDataset createDataset(String exp_name) throws Exception   {

        // row keys...
    	DBManager dba=new DBManager();
    	DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    	
    	String sql="SELECT DISTINCT hostname FROM "+exp_name+"_proc;";
   		ArrayList seriesList=dba.executeSql(sql);
   		
    	String series[] = new String[seriesList.size()];   
   		for(int i=0;i<seriesList.size();i++)
   		{
   			HashMap hash=(HashMap)seriesList.get(i);
   			series[i]=hash.get("HOSTNAME").toString();   			
   		}
   		
   		
    	sql="SELECT DISTINCT instance FROM "+exp_name+"_proc WHERE metric='Byte';";
   		ArrayList categoryList=dba.executeSql(sql);
   		
    	String category[] = new String[categoryList.size()];   
   		for(int i=0;i<categoryList.size();i++)
   		{
   			HashMap hash=(HashMap)categoryList.get(i);
   			category[i]=hash.get("INSTANCE").toString();   			
   		}
   		
    	for(int i=0;i<series.length;i++)
    	{
	        for(int j=0;j<category.length;j++)
	        {
	        	 
	        	String query="SELECT SUM(data) as sum FROM "+exp_name+"_proc WHERE hostname='"+series[i]+"' and instance='"+category[j]+"'";
	        	ArrayList resultList=dba.executeSql(query);
	        	   
	       		HashMap hash=(HashMap)resultList.get(0);
	       		
	       		double value=Double.parseDouble(hash.get("SUM").toString()); 
	       		dataset.addValue(category[j].regionMatches(0, "Net", 0, 3)? value/1024:value, series[i], category[j]);
	       		System.out.println(category[j].regionMatches(0, "Net", 0, 3)? value/1024:value);
	       		
	        }      
    	} 

        return dataset;

    }

    private static JFreeChart createChart(CategoryDataset dataset,String exp) {
        SpiderWebPlot plot = new SpiderWebPlot(dataset);
        plot.setStartAngle(0);
        plot.setInteriorGap(0.40);

        plot.setBackgroundPaint(null);
        plot.setOutlineVisible(false);
        plot.setToolTipGenerator(new StandardCategoryToolTipGenerator());
        
        JFreeChart chart = new JFreeChart("",
                TextTitle.DEFAULT_FONT, plot, false);
        LegendTitle legend = new LegendTitle(plot);
        legend.setPosition(RectangleEdge.BOTTOM);
        chart.addSubtitle(legend);
    
        ChartUtilities.applyCurrentTheme(chart);
        return chart;
    }
}
