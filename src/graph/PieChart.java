package graph;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

public class PieChart {
	
	 public static JFreeChart createPieChart(String exp) {

	        // create a dataset...
	        DefaultPieDataset data = new DefaultPieDataset();
	        data.setValue("One", new Double(43.2));
	        data.setValue("Two", new Double(10.0));
	        data.setValue("Three", new Double(27.5));
	        data.setValue("Four", new Double(17.5));
	        data.setValue("Five", new Double(11.0));
	        data.setValue("Six", new Double(19.4));

	        JFreeChart chart = ChartFactory.createPieChart(
	            exp, data, true, true, false
	        );
	        return chart;

	    }

}
