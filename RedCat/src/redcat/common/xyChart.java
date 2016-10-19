package redcat.common;

import java.awt.*;
import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.xy.*;

/**
 * Classe rappresentante un grafico cartesiano ortogonale.
 * Il grafico viene disegnato su un JPanel.
 * 
 * @author Lanzafame Flavio, Russo Giorgia, Spadaro Stefano, Torrelli Denisia
 */
public class xyChart
{
    private final XYSeries series;
    private final ChartPanel chartPanel;
    private final NumberAxis xAxis, yAxis;
    
    /**
     * Crea un xyChart.
     * @param labels le etichette degli assi
     * @param maxPoints il massimo numero di punti da visualizzare
     * @param background il colore dello sfondo
     * @param foreground il colore della linea che unisce i punti del grafico
     */
    public xyChart(String[] labels, int maxPoints, Color background, Color foreground)
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
    
    /**
     * Restituisce il ChartPanel di questo xyChart.
     * Dato che ChartPanel è figlio di JPanel, il ritorno di questo metodo è
     * utilizzabile in luogo di un JPanel.
     * @return il ChartPanel di questo xyChart
     */
    public ChartPanel getChartPanel()
    {
        return this.chartPanel;
    }
    
    /**
     * Aggiunge un punto al grafico e aggiorna lo stesso.
     * @param x l'ascissa del nuovo punto da disegnare
     * @param y l'ordinata del nuovo punto da disegnare
     */
    public void addPoint(final double x, final double y)
    {
        series.addOrUpdate(x, y);
    }
    
    /**
     * Imposta l'etichetta per l'asse delle ascisse.
     * @param label l'etichetta da settare
     */
    public void setXLabel(String label)
    {
        xAxis.setLabel(label);
    }
    
    /**
     * Imposta l'etichetta per l'asse delle ordinate.
     * @param label l'etichetta da settare
     */
    public void setYLabel(String label)
    {
        yAxis.setLabel(label);
    }
}
