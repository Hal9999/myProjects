package desktopapplication1;

import java.awt.Color;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class xy4Chart
{
    private final XYSeries firstSeries;
    private final XYSeries secondSeries;
    private final ChartPanel chartPanel;

    public xy4Chart(int maxPoints)
    {
        firstSeries = new XYSeries(Double.toString(Math.random()));
        secondSeries = new XYSeries(Double.toString(Math.random()));
//        firstSeries.setMaximumItemCount(maxPoints);
//        secondSeries.setMaximumItemCount(maxPoints);
        
//        JFreeChart chart = ChartFactory.createXYLineChart
//        (
//            "Ciao",
//            "X",
//            "Y",
//            new XYSeriesCollection(series),
//            PlotOrientation.VERTICAL,
//            false,
//            false,
//            false
//        );
//        
//        XYPlot plot = chart.getXYPlot();
        
        NumberAxis xAxis = new NumberAxis("X");
//        xAxis.setNumberFormatOverride(new DecimalFormat("0.##E000"));
        xAxis.setAutoRange(true);
        xAxis.setAutoRangeIncludesZero(false);
        
        NumberAxis yAxis = new NumberAxis("Y");
//        yAxis.setNumberFormatOverride(new DecimalFormat("0.##E0"));
        yAxis.setAutoRange(true);
        yAxis.setAutoRangeIncludesZero(false);
        
//        NumberAxis x2Axis = new NumberAxis("X");
////        xAxis.setNumberFormatOverride(new DecimalFormat("0.##E000"));
//        x2Axis.setAutoRange(true);
//        x2Axis.setAutoRangeIncludesZero(false);
//        
//        NumberAxis y2Axis = new NumberAxis("Y");
////        yAxis.setNumberFormatOverride(new DecimalFormat("0.##E0"));
//        y2Axis.setAutoRange(true);
//        y2Axis.setAutoRangeIncludesZero(false);
        
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);

        XYSeriesCollection seriesCollection = new XYSeriesCollection();
        seriesCollection.addSeries(firstSeries);
        seriesCollection.addSeries(secondSeries);
        XYPlot myPlot = new XYPlot( seriesCollection, xAxis, yAxis, renderer);
//        myPlot.setDataset(seriesCollection);
        myPlot.setRenderer(renderer);
//        myPlot.setDomainAxis(0, xAxis);
//        myPlot.setRangeAxis(0, yAxis);
//        myPlot.setDomainAxis(1, x2Axis);
//        myPlot.setRangeAxis(1, y2Axis);
//        myPlot.setDataset(0, new XYSeriesCollection(firstSeries));
//        myPlot.setDataset(1, new XYSeriesCollection(secondSeries));
        
        renderer.setSeriesPaint(0, Color.BLACK);
        renderer.setSeriesPaint(1, Color.RED);
        
        //XYPlot myPlot = new XYPlot( new XYSeriesCollection(series), xAxis, yAxis, renderer);
        myPlot.setBackgroundPaint(Color.WHITE);
        
        
//        xAxis = new NumberAxis("Z");
//        xAxis.setNumberFormatOverride(new DecimalFormat("0.##E000"));
//        xAxis.setAutoRange(true);
//        xAxis.setAutoRangeIncludesZero(false);
        
//        yAxis = new NumberAxis("T");
//        yAxis.setNumberFormatOverride(new DecimalFormat("0.##E0"));
//        yAxis.setAutoRange(true);
//        yAxis.setAutoRangeIncludesZero(false);
        
        //XYPlot secondPlot = new XYPlot( new XYSeriesCollection(secondSeries), new NumberAxis("Z"), new NumberAxis("T"), null);
//        JFreeChart myChart = new JFreeChart(myPlot);
        
        //CombinedDomainXYPlot myPlot = new CombinedDomainXYPlot(new NumberAxis("A"));
        
//        myPlot.add(firstPlot);
//        myPlot.add(secondPlot);
        //myPlot.setRenderer(new XYLineAndShapeRenderer(true, false));
        
        JFreeChart myChart = new JFreeChart(myPlot);
        
        
//        plot.setDomainAxis(xAxis);
//        plot.setRangeAxis(yAxis);
//        
        myChart.clearSubtitles();
        chartPanel = new ChartPanel(myChart);
    }
    
    public ChartPanel getChartPanel()
    {
        return this.chartPanel;
    }
    
    public void addPoint(final double x, final double y, final double z)
    {
        secondSeries.addOrUpdate(x, z);
        firstSeries.addOrUpdate(x, y);
    }
    
//    public void setXLabel(String label)
//    {
//        xAxis.setLabel(label);
//    }
//    
//    public void setYLabel(String label)
//    {
//        yAxis.setLabel(label);
//    }
}
