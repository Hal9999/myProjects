package it.unict.eve.gui;

import java.awt.Color;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class xy2Chart
{
    private final XYSeries series;
    private final ChartPanel chartPanel;
    private final NumberAxis xAxis, yAxis;

    public xy2Chart(String[] labels, int maxPoints, Color background, Color foreground)
    {
        series = new XYSeries(Double.toString(Math.random()));
        series.setMaximumItemCount(maxPoints);
        
        xAxis = new NumberAxis(labels[0]);
        xAxis.setAutoRange(true);
        xAxis.setAutoRangeIncludesZero(false);
//        xAxis.setNumberFormatOverride(new DecimalFormat("0.##E000"));
        
        yAxis = new NumberAxis(labels[1]);
        yAxis.setAutoRange(true);
        yAxis.setAutoRangeIncludesZero(false);
//        yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
//        yAxis.setNumberFormatOverride(new DecimalFormat("0.##E0"));
        
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(0, background);
        
        XYPlot myPlot = new XYPlot( new XYSeriesCollection(series), xAxis, yAxis, renderer);
        myPlot.setBackgroundPaint(foreground);
        
        JFreeChart myChart = new JFreeChart(myPlot);
        
        myChart.clearSubtitles();
        chartPanel = new ChartPanel(myChart);
    }
    
    public ChartPanel getChartPanel()
    {
        return this.chartPanel;
    }
    
    public void addPoint(final double x, final double y)
    {
        series.addOrUpdate(x, y);
    }
    
    public void setXLabel(String label)
    {
        xAxis.setLabel(label);
    }
    
    public void setYLabel(String label)
    {
        yAxis.setLabel(label);
    }
}
