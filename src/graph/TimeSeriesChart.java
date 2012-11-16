package graph;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.date.MonthConstants;

public class TimeSeriesChart {
	
	public static JFreeChart createTimeSeriesChart(String exp) {

        // here we just populate a series with random data...
        TimeSeries series = new TimeSeries("Random Data");
        Day current = new Day(1, MonthConstants.JANUARY, 2001);
        for (int i = 0; i < 100; i++) {
            series.add(current, Math.random() * 100);
            current = (Day) current.next();
        }
        XYDataset data = new TimeSeriesCollection(series);

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            exp, "Date", "Rate",
            data, true, true, false
        );
        return chart;

    }

}
