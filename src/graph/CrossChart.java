package graph;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

public class CrossChart {
	
	 public static JFreeChart createCrossChart (String exp) {
    	 XYDataset dataset1 = createDataset("CPU", 100.0, new Minute(),
                 200);

         JFreeChart chart = ChartFactory.createTimeSeriesChart(
             exp,
             "Time of Day",
             "Primary Range Axis",
             dataset1,
             true,
             true,
             false
         );

         chart.addSubtitle(new TextTitle("Four datasets and four range axes."));
         XYPlot plot = (XYPlot) chart.getPlot();
         plot.setOrientation(PlotOrientation.VERTICAL);
         plot.setDomainPannable(true);
         plot.setRangePannable(true);
         plot.getRangeAxis().setFixedDimension(15.0);

         // AXIS 2
         NumberAxis axis2 = new NumberAxis("Range Axis 2");
         axis2.setFixedDimension(10.0);
         axis2.setAutoRangeIncludesZero(false);
         plot.setRangeAxis(1, axis2);
         plot.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_LEFT);

         XYDataset dataset2 = createDataset("IO", 1000.0, new Minute(),
                 170);
         plot.setDataset(1, dataset2);
         plot.mapDatasetToRangeAxis(1, 1);
         XYItemRenderer renderer2 = new StandardXYItemRenderer();
         plot.setRenderer(1, renderer2);

         // AXIS 3
         NumberAxis axis3 = new NumberAxis("Range Axis 3");
         plot.setRangeAxis(2, axis3);

         XYDataset dataset3 = createDataset("Comm", 10000.0, new Minute(),
                 170);
         plot.setDataset(2, dataset3);
         plot.mapDatasetToRangeAxis(2, 2);
         XYItemRenderer renderer3 = new StandardXYItemRenderer();
         plot.setRenderer(2, renderer3);

         // AXIS 4
         NumberAxis axis4 = new NumberAxis("Range Axis 4");
         plot.setRangeAxis(3, axis4);

         XYDataset dataset4 = createDataset("GPU", 25.0, new Minute(), 200);
         plot.setDataset(3, dataset4);
         plot.mapDatasetToRangeAxis(3, 3);

         XYItemRenderer renderer4 = new StandardXYItemRenderer();
         plot.setRenderer(3, renderer4);

         ChartUtilities.applyCurrentTheme(chart);

         // change the series and axis colours after the theme has
         // been applied...
         plot.getRenderer().setSeriesPaint(0, Color.black);
         renderer2.setSeriesPaint(0, Color.red);
         axis2.setLabelPaint(Color.red);
         axis2.setTickLabelPaint(Color.red);
         renderer3.setSeriesPaint(0, Color.blue);
         axis3.setLabelPaint(Color.blue);
         axis3.setTickLabelPaint(Color.blue);
         renderer4.setSeriesPaint(0, Color.green);
         axis4.setLabelPaint(Color.green);
         axis4.setTickLabelPaint(Color.green);
         
         return chart;
    }
    
    //createDataset for CrossChart
    private static XYDataset createDataset(String name, double base, RegularTimePeriod start, int count) {

		TimeSeries series = new TimeSeries(name);
		RegularTimePeriod period = start;
		double value = base;
		for (int i = 0; i < count; i++) {
			series.add(period, value);
			period = period.next();
			value = value * (1 + (Math.random() - 0.495) / 10.0);
		}
			
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(series);
			
		return dataset;
			
	}

}
