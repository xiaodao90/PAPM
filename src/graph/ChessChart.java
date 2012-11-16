package graph;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;

import tools.ColorScale;
import tools.DBManager;

public class ChessChart {
	public static double maxSum = 0;
	
	public static JFreeChart createChessChart(String exp) throws Exception
	{		
		JFreeChart chart = createChart(createDataset(exp),"");
		return chart;
	}	 
	
	private static JFreeChart createChart(XYZDataset dataset, String exp) {
        NumberAxis xAxis = new NumberAxis("Send");
        xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        xAxis.setLowerMargin(0.0);
        xAxis.setUpperMargin(0.0);
        xAxis.setAxisLinePaint(Color.white);
        xAxis.setTickMarkPaint(Color.white);

        NumberAxis yAxis = new NumberAxis("Receive");
        yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        yAxis.setLowerMargin(0.0);
        yAxis.setUpperMargin(0.0);
        yAxis.setAxisLinePaint(Color.white);
        yAxis.setTickMarkPaint(Color.white);
        XYBlockRenderer renderer = new XYBlockRenderer();
        
        
        
        ColorScale scale = new  ColorScale(1D,maxSum);
        renderer.setPaintScale(scale);
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);

        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5, 5, 5, 5));
        plot.setOutlinePaint(Color.blue);
        JFreeChart chart = new JFreeChart(exp, plot);
        chart.removeLegend();
        NumberAxis scaleAxis = new NumberAxis("Scale");
        scaleAxis.setAxisLinePaint(Color.black);
        scaleAxis.setTickMarkPaint(Color.white);
        scaleAxis.setTickLabelFont(new Font("Dialog", Font.PLAIN, 7));
        
        ColorScale myColorScale = new ColorScale(1D, maxSum);
              
        PaintScaleLegend legend = new PaintScaleLegend(myColorScale,
                scaleAxis);
        legend.setStripOutlineVisible(false);
        legend.setSubdivisionCount(20);
        legend.setAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
        legend.setAxisOffset(5.0);
        legend.setMargin(new RectangleInsets(5, 5, 5, 5));
        legend.setFrame(new BlockBorder(Color.red));
        legend.setPadding(new RectangleInsets(10, 10, 10, 10));
        legend.setStripWidth(10);
        legend.setPosition(RectangleEdge.LEFT);
        //legend.setBackgroundPaint(new Color(120, 120, 180));
        chart.addSubtitle(legend);
        //chart.setBackgroundPaint(new Color(180, 180, 250));
        ChartUtilities.applyCurrentTheme(chart);
        return chart;
    }
    


    /**
     * Creates a sample dataset.
     *
     * @return A dataset.
     * @throws Exception 
     */
    private static XYZDataset createDataset(String exp_name) throws Exception {
    	  
    	DBManager dba=new DBManager();
    	
        double[] xvalues = new double[1024];
    	double[] yvalues = new double[1024];
    	double[] zvalues = new double[1024];
    	double[][] data = new double[][] {xvalues, yvalues, zvalues};

    	
    	DefaultXYZDataset dataset = new DefaultXYZDataset();
    	
 
      	String sql="SELECT  src_rank,dst_rank, SUM(send_size) AS sum from "+exp_name+"_comm WHERE eid =83 GROUP BY src_rank,dst_rank;";
  	    ArrayList resultList=dba.executeSql(sql);
      	
       
  	    for(int i=0;i<resultList.size();i++)
  	    {
     		HashMap hash=(HashMap)resultList.get(i);
     		String x=hash.get("SRC_RANK").toString();
     		String y=hash.get("DST_RANK").toString();
     		String z=hash.get("SUM").toString();
     		
       		data[0][i]=Double.parseDouble(hash.get("SRC_RANK").toString()); 
       		data[1][i]=Double.parseDouble(hash.get("DST_RANK").toString()); 
       		data[2][i]=Double.parseDouble(hash.get("SUM").toString()); 
       		
       		if(data[2][i]>=maxSum)
     			maxSum=data[2][i];
  	    } 
  	    
	      dataset.addSeries("Series 1", data);
	      return dataset;
    }
	    

}
