package it.unict.eve.gui;

import java.awt.Color;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class xySpectrumChart
{
    private final XYSeries series;
    private final ChartPanel chartPanel;
    private final NumberAxis xAxis, yAxis;
    private int numberOfPoints;
    private boolean firstLoop = true;

    public xySpectrumChart(String[] labels, Color background, Color foreground)
    {
//        this.numberOfPoints = numberOfPoints;
        series = new XYSeries(Double.toString(Math.random()), true, false);
//        for(int i=0; i<this.numberOfPoints; i++)
//            series.add(i, 0, false);
//        series.setMaximumItemCount(maxPoints);
        
        xAxis = new NumberAxis(labels[0]);
//        xAxis.setRange(0, numberOfPoints-1);
//        xAxis.setAutoRange(true);
        xAxis.setAutoRangeIncludesZero(false);
//        xAxis.setNumberFormatOverride(new DecimalFormat("0.##E000"));
        
        yAxis = new NumberAxis(labels[1]);
        yAxis.setAutoRange(true);
        yAxis.setAutoRangeIncludesZero(false);
//        yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
//        yAxis.setNumberFormatOverride(new DecimalFormat("0.##E0"));
        
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(false, true);
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
    
    public void refreshData(double[] data)
    {
        if( this.firstLoop == true)
        {
            this.firstLoop = false;
            this.numberOfPoints = data.length;
            xAxis.setRange(0, numberOfPoints-1);
            for(int i=0; i<this.numberOfPoints-1; i++)
                series.add(i, data[i], false);
            series.add(this.numberOfPoints-1, data[this.numberOfPoints-1], true);
        }
        else
        {
            if( this.numberOfPoints != data.length ) throw new IllegalArgumentException("different lenghts");

            double log;
            for(int i=0; i<this.numberOfPoints; i++)
            {
                log = Math.log10(data[i]);
                if( Double.isInfinite(log) || Double.isNaN(log)) series.updateByIndex(i, null);
                else series.updateByIndex(i, log);
            }
//            series.updateByIndex(this.numberOfPoints-1, data[this.numberOfPoints-1]);
        }
    }
    
//    public void addPoint(final double x, final double y)
//    {
//        series.addOrUpdate(x, y);
//    }
    
    public void setXLabel(String label)
    {
        xAxis.setLabel(label);
    }
    
    public void setYLabel(String label)
    {
        yAxis.setLabel(label);
    }
}
