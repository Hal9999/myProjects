package it.unict.eve.gui;

import it.unict.eve.*;
import it.unict.eve.EvolutiveIsland.Parameters;
import java.awt.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultCaret;

public class EveGui extends javax.swing.JFrame
{
    private class IslandStatisticsRefresher implements Runnable
    {
        private EvolutiveIsland island;
        private JLabel[] labels;
        private JPanel panel;
        private AtomicBoolean stop;
        private AtomicInteger sleepTime;
        private DecimalFormat decimalFormat;
        private xy2Chart xyChart;
        private String axisLabel;
        private xySpectrumChart spectrum;
        private JTable table;
        
        private Thread thread;
        
        public IslandStatisticsRefresher(
                                            final EvolutiveIsland island,
                                            final JLabel[] labels,
                                            final JPanel panel,
                                            final int sleepTime,
                                            final xy2Chart xyChart,
                                            final String axisLabel,
                                            final xySpectrumChart spectrum,
                                            final JTable table
                                        )
        {
            this.island = island;
            this.labels = labels;
            this.panel = panel;
            this.sleepTime = new AtomicInteger(sleepTime);
            this.stop = new AtomicBoolean(false);
            this.decimalFormat = new DecimalFormat("0.###E0");
            this.xyChart = xyChart;
            this.axisLabel = axisLabel;
            this.spectrum = spectrum;
            this.table = table;
            
            thread = new Thread( this );
            thread.start();
        }
        
        public void requestStop()
        {
            this.stop.set(true);
        }
        
        public void waitForStop() throws InterruptedException
        {
            this.thread.join();
        }
        
        public void changeRefreshTime(int time)
        {
            this.sleepTime.set(time);
        }
        
        @Override
        public void run()
        {
            EvolutiveIsland.Statistics lastStats = null;
            EvolutiveIsland.Statistics statistics = null;
            
            EvolutiveIsland.Solution lastSolution = null;
            EvolutiveIsland.Solution solution = null;
            while(true)
            {
                if( this.stop.get() == true ) return;
                
                panel.setBackground(Color.CYAN);
                xyChart.setYLabel(axisLabel);
                
                statistics = island.getLastGenerationStatistics();
                if( statistics != null && statistics != lastStats )
                {
                    labels[ 0].setText( Integer.toString(statistics.generationNumber) );
                    labels[ 1].setText( decimalFormat.format(statistics.bestFitness) );
                    labels[ 2].setText( decimalFormat.format(statistics.worstFitness) );
                    labels[ 3].setText( decimalFormat.format(statistics.islandMean) );
                    labels[ 4].setText( decimalFormat.format(statistics.islandVariance) );
                    labels[ 5].setText( Integer.toString(statistics.islandSize) );
                    labels[ 6].setText( Integer.toString(statistics.deleted) );
                    labels[ 7].setText( Integer.toString(statistics.threads) );
                    labels[ 8].setText( String.format("%02d:%02d:%02d", ((int)statistics.totalTime)/3600, ((int)statistics.totalTime)/60%60, ((int)statistics.totalTime)%60) );
                    labels[ 9].setText( Integer.toString(statistics.totalIndividuals) );
                    labels[10].setText( Integer.toString((int)statistics.meanSpeed) );
                    labels[11].setText( String.format("%.3f", statistics.generationTime) );
                    labels[12].setText( Integer.toString(statistics.generationSize) );
                    labels[13].setText( Integer.toString((int)statistics.generationSpeed) );
                    labels[14].setText( Integer.toString(statistics.disasters) );
                    labels[15].setText( Integer.toString(statistics.migrations) );
                    
                    if( jCheckBox3.isSelected() ) xyChart.addPoint( statistics.generationNumber, Math.log10(statistics.bestFitness) );
                    lastStats = statistics;
                }
                
                solution = island.getLastBestSolution();
                if( solution != null && solution != lastSolution )
                {
                    if( jCheckBox4.isSelected() ) spectrum.refreshData(ODEModel.errorsFromResiduals(solution.residuals));
                    
                    if( jCheckBox5.isSelected() )
                    {
                        double[][] array = solution.model.getBackingArray();
                        Object[][] arrayObject = new Object[array.length][array[0].length + 1];
                        String[] profileNames = solution.names;

                        for(int i=0; i<array.length; i++)
                            arrayObject[i][0] = profileNames[i];

                        for(int i=0; i<array.length; i++)
                            for(int j=0; j<array[0].length; j++)
                                arrayObject[i][j+1] = array[i][j];

                        String[] columnsHeaders = new String[solution.model.nCols()+1]; //[lockedExperiment.getSamplesNumber() + 1];
                        columnsHeaders[0] = "";
                        for(int i=1; i<columnsHeaders.length; i++)
                            columnsHeaders[i] = solution.names[i-1];//(i * lastExperiment.getSamplingTime()) + timeUnitTextField.getText();
                        DefaultTableModel tableModel = new DefaultTableModel(arrayObject, columnsHeaders);
                        //        tableModel.addColumn("Names", lastExperiment.getProfilesNames());
                        table.setModel(tableModel);
                    }
                    
                    lastSolution = solution;
//                    labels[ 0].setText( Integer.toString(statistics.generationNumber) );
//                    labels[ 1].setText( decimalFormat.format(statistics.bestFitness) );
//                    labels[ 2].setText( decimalFormat.format(statistics.worstFitness) );
//                    labels[ 3].setText( decimalFormat.format(statistics.islandMean) );
//                    labels[ 4].setText( decimalFormat.format(statistics.islandVariance) );
//                    labels[ 5].setText( Integer.toString(statistics.islandSize) );
//                    labels[ 6].setText( Integer.toString(statistics.deleted) );
//                    labels[ 7].setText( Integer.toString(statistics.threads) );
//                    labels[ 8].setText( String.format("%02d:%02d:%02d", ((int)statistics.totalTime)/3600, ((int)statistics.totalTime)/60%60, ((int)statistics.totalTime)%60) );
//                    labels[ 9].setText( Integer.toString(statistics.totalIndividuals) );
//                    labels[10].setText( Integer.toString((int)statistics.meanSpeed) );
//                    labels[11].setText( String.format("%.3f", statistics.generationTime) );
//                    labels[12].setText( Integer.toString(statistics.generationSize) );
//                    labels[13].setText( Integer.toString((int)statistics.generationSpeed) );
//                    labels[14].setText( Integer.toString(statistics.disasters) );
//                    labels[15].setText( Integer.toString(statistics.migrations) );
//                    
//                    xyChart.addPoint( statistics.generationNumber, Math.log10(statistics.bestFitness) );
                }

                panel.setBackground(Color.GREEN);

                try{ Thread.sleep(this.sleepTime.get()); } catch(InterruptedException ex) { Logger.getLogger(EveGui.class.getName()).log(Level.SEVERE, null, ex);}
            }
        }
    }
    
    private class WorldStatisticsRefresher implements Runnable
    {
        private EvolutiveWorld world;
        private JLabel[] labels;
        private AtomicBoolean stop;
        private AtomicInteger sleepTime;
        private DecimalFormat decimalFormat;
        
        private Thread thread;
        
        public WorldStatisticsRefresher(final EvolutiveWorld world, final JLabel[] labels, final int sleepTime)
        {
            this.world = world;
            this.labels = labels;
            this.sleepTime = new AtomicInteger(sleepTime);
            this.stop = new AtomicBoolean(false);
            this.decimalFormat = new DecimalFormat("0.###E0");
            
            thread = new Thread( this );
            thread.start();
        }
        
        public void requestStop()
        {
            this.stop.set(true);
        }
        
        public void waitForStop() throws InterruptedException
        {
            this.thread.join();
        }
        
        public void changeRefreshTime(int time)
        {
            this.sleepTime.set(time);
        }
        
        @Override
        public void run()
        {
            while(true)
            {
                if( this.stop.get() == true ) return;
                
                EvolutiveWorld.Statistics statistics = world.getStatistics();
                if( statistics != null )
                {
                    labels[0].setText( Integer.toString(statistics.islandsN) );
                    labels[1].setText( Integer.toString(statistics.worldSize) );
                    labels[2].setText( Integer.toString(statistics.passedGenerations) );
                    labels[3].setText( Integer.toString(statistics.deleted) );
                    labels[4].setText( Integer.toString(statistics.totalIndividuals) );
                    labels[5].setText( Integer.toString(statistics.disasters) );
                    labels[6].setText( Integer.toString(statistics.migrations) );
                    labels[7].setText( Integer.toString(statistics.threads) );
                    labels[8].setText( String.format("%02d:%02d:%02d", ((int)statistics.totalTime)/3600, ((int)statistics.totalTime)/60%60, ((int)statistics.totalTime)%60) );
                    labels[9].setText( Integer.toString((int)statistics.meanSpeed) );
                }

                try{ Thread.sleep(this.sleepTime.get()); } catch(InterruptedException ex) { Logger.getLogger(EveGui.class.getName()).log(Level.SEVERE, null, ex);}
            }
        }
    }
    
    private Experiment firstExperiment;
    private Experiment undoExperiment;
    private Experiment lastExperiment;
    private Experiment lockedExperiment = null;
    
    private EvolutiveWorld world = null;
    
    private EvolutiveIsland islands[] = new EvolutiveIsland[4];
    private IslandStatisticsRefresher[] islandRefreshers = new IslandStatisticsRefresher[4];
    private final JLabel[][] islandLabels;
    private final JPanel[] islandStatusPanels;
    private WorldStatisticsRefresher worldRefresher;
    private final JLabel[] worldLabels;
    private final xy2Chart[] charters;
    private final String[] fitnessTypes = new String[4];
    private xySpectrumChart[] spectrumCharts = new xySpectrumChart[4];
    private JTable[] solutionTables;
    
    private Matrix.MatrixDistanceCalculator mixedErrorMatrixDistanceCalculator = new MainEVE.MixedErrorMatrixDistanceCalculator();

    public EveGui()
    {
        charters = new xy2Chart[]{
                                      new xy2Chart(new String[]{"G", "f"}, 100, Color.WHITE, Color.DARK_GRAY),
                                      new xy2Chart(new String[]{"G", "f"}, 100, Color.WHITE, Color.DARK_GRAY),
                                      new xy2Chart(new String[]{"G", "f"}, 100, Color.WHITE, Color.DARK_GRAY),
                                      new xy2Chart(new String[]{"G", "f"}, 100, Color.WHITE, Color.DARK_GRAY) };
        
        spectrumCharts = new xySpectrumChart[]{
                                    new xySpectrumChart(new String[]{"Samples", "ABS Cumulative Error"}, Color.red, Color.darkGray),
                                    new xySpectrumChart(new String[]{"Samples", "ABS Cumulative Error"}, Color.red, Color.darkGray),
                                    new xySpectrumChart(new String[]{"Samples", "ABS Cumulative Error"}, Color.red, Color.darkGray),
                                    new xySpectrumChart(new String[]{"Samples", "ABS Cumulative Error"}, Color.red, Color.darkGray)};
                
        initComponents();
        islandLabels = new JLabel[][]
        {
            {jLabel104, jLabel105, jLabel106, jLabel107, jLabel108, jLabel109, jLabel110, jLabel111, jLabel112, jLabel113, jLabel114, jLabel115, jLabel116, jLabel117, jLabel118, jLabel119},
            {jLabel136, jLabel137, jLabel138, jLabel139, jLabel140, jLabel141, jLabel142, jLabel143, jLabel144, jLabel145, jLabel146, jLabel147, jLabel148, jLabel149, jLabel150, jLabel151},
            {jLabel168, jLabel169, jLabel170, jLabel171, jLabel172, jLabel173, jLabel174, jLabel175, jLabel176, jLabel177, jLabel178, jLabel179, jLabel180, jLabel181, jLabel182, jLabel183},
            {jLabel200, jLabel201, jLabel202, jLabel203, jLabel204, jLabel205, jLabel206, jLabel207, jLabel208, jLabel209, jLabel210, jLabel211, jLabel212, jLabel213, jLabel214, jLabel215},
        };
        islandStatusPanels = new JPanel[]{jPanel22, jPanel23, jPanel24, jPanel25};
        worldLabels = new JLabel[]{jLabel26, jLabel27, jLabel28, jLabel29, jLabel30, jLabel31, jLabel32, jLabel33, jLabel34, jLabel35};
        solutionTables  = new JTable[]{ jTable1, jTable2, jTable3, jTable4};
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jTabbedPane2 = new javax.swing.JTabbedPane();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel9 = new javax.swing.JPanel();
        experimentPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        samplesFileSelectButton = new javax.swing.JButton();
        samplesFileTextField = new javax.swing.JTextField();
        transposeCheckBox = new javax.swing.JCheckBox();
        namesLabel = new javax.swing.JLabel();
        namesFileTextField = new javax.swing.JTextField();
        autogenerateNamesCheckBox = new javax.swing.JCheckBox();
        samplingTimeSpinner = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        timeUnitTextField = new javax.swing.JTextField();
        disableTimeUnitCheckBox = new javax.swing.JCheckBox();
        loadFilesButton = new javax.swing.JButton();
        experimentNameLabel = new javax.swing.JLabel();
        experimentNameTextField = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        StatisticsPanel = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        stateLabel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        samplingTimeLabel = new javax.swing.JLabel();
        timeUnitLabel = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        profilesNumberLabel = new javax.swing.JLabel();
        samplesNumberLabel = new javax.swing.JLabel();
        workingStatusPanel = new javax.swing.JPanel();
        workingStatusLabel = new javax.swing.JLabel();
        preElaborationPanel = new javax.swing.JPanel();
        selectionPanel = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        minVarianceSelectionSpinner = new javax.swing.JSpinner();
        goSelectionButton = new javax.swing.JButton();
        clustreingPanel = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        distanceTypeComboBox = new javax.swing.JComboBox();
        jLabel18 = new javax.swing.JLabel();
        clusteringRadiusSpinner = new javax.swing.JSpinner();
        goClusteringButton = new javax.swing.JButton();
        interpolatePanel = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        newNumberOfSamplesInterpolateSpinner = new javax.swing.JSpinner();
        goInterpolateButton = new javax.swing.JButton();
        selectionProgressBar = new javax.swing.JProgressBar();
        clusteringProgressBar = new javax.swing.JProgressBar();
        interpolateProgressBar = new javax.swing.JProgressBar();
        operationsPanel = new javax.swing.JPanel();
        discardLastButton = new javax.swing.JButton();
        revoverFirstButton = new javax.swing.JButton();
        saveToDiskButton = new javax.swing.JButton();
        lockExperimentButton = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        lockedPanel = new javax.swing.JPanel();
        lockedLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        messageTextArea = new javax.swing.JTextArea();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();
        jTabbedPane4 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jPanel27 = new javax.swing.JPanel();
        jLabel59 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jCheckBox17 = new javax.swing.JCheckBox();
        jSpinner38 = new javax.swing.JSpinner();
        jSpinner39 = new javax.swing.JSpinner();
        jCheckBox18 = new javax.swing.JCheckBox();
        jLabel61 = new javax.swing.JLabel();
        jPanel28 = new javax.swing.JPanel();
        jLabel62 = new javax.swing.JLabel();
        jLabel63 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        jSpinner40 = new javax.swing.JSpinner();
        jSpinner41 = new javax.swing.JSpinner();
        jSpinner42 = new javax.swing.JSpinner();
        jLabel65 = new javax.swing.JLabel();
        jComboBox6 = new javax.swing.JComboBox();
        jPanel29 = new javax.swing.JPanel();
        jCheckBox19 = new javax.swing.JCheckBox();
        jLabel66 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jSpinner43 = new javax.swing.JSpinner();
        jSpinner44 = new javax.swing.JSpinner();
        jSpinner1 = new javax.swing.JSpinner();
        jPanel12 = new javax.swing.JPanel();
        jPanel30 = new javax.swing.JPanel();
        jLabel68 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jCheckBox20 = new javax.swing.JCheckBox();
        jSpinner45 = new javax.swing.JSpinner();
        jSpinner46 = new javax.swing.JSpinner();
        jCheckBox21 = new javax.swing.JCheckBox();
        jLabel70 = new javax.swing.JLabel();
        jPanel31 = new javax.swing.JPanel();
        jLabel71 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();
        jSpinner47 = new javax.swing.JSpinner();
        jSpinner48 = new javax.swing.JSpinner();
        jSpinner49 = new javax.swing.JSpinner();
        jLabel74 = new javax.swing.JLabel();
        jComboBox7 = new javax.swing.JComboBox();
        jPanel32 = new javax.swing.JPanel();
        jCheckBox22 = new javax.swing.JCheckBox();
        jLabel75 = new javax.swing.JLabel();
        jLabel76 = new javax.swing.JLabel();
        jSpinner50 = new javax.swing.JSpinner();
        jSpinner51 = new javax.swing.JSpinner();
        jSpinner2 = new javax.swing.JSpinner();
        jPanel14 = new javax.swing.JPanel();
        jPanel33 = new javax.swing.JPanel();
        jLabel77 = new javax.swing.JLabel();
        jLabel78 = new javax.swing.JLabel();
        jCheckBox23 = new javax.swing.JCheckBox();
        jSpinner52 = new javax.swing.JSpinner();
        jSpinner53 = new javax.swing.JSpinner();
        jCheckBox24 = new javax.swing.JCheckBox();
        jLabel79 = new javax.swing.JLabel();
        jPanel34 = new javax.swing.JPanel();
        jLabel80 = new javax.swing.JLabel();
        jLabel81 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        jSpinner54 = new javax.swing.JSpinner();
        jSpinner55 = new javax.swing.JSpinner();
        jSpinner56 = new javax.swing.JSpinner();
        jLabel83 = new javax.swing.JLabel();
        jComboBox8 = new javax.swing.JComboBox();
        jPanel35 = new javax.swing.JPanel();
        jCheckBox25 = new javax.swing.JCheckBox();
        jLabel84 = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        jSpinner57 = new javax.swing.JSpinner();
        jSpinner58 = new javax.swing.JSpinner();
        jSpinner3 = new javax.swing.JSpinner();
        jPanel15 = new javax.swing.JPanel();
        jPanel36 = new javax.swing.JPanel();
        jLabel86 = new javax.swing.JLabel();
        jLabel87 = new javax.swing.JLabel();
        jCheckBox26 = new javax.swing.JCheckBox();
        jSpinner59 = new javax.swing.JSpinner();
        jSpinner60 = new javax.swing.JSpinner();
        jCheckBox27 = new javax.swing.JCheckBox();
        jLabel88 = new javax.swing.JLabel();
        jPanel37 = new javax.swing.JPanel();
        jLabel89 = new javax.swing.JLabel();
        jLabel90 = new javax.swing.JLabel();
        jLabel91 = new javax.swing.JLabel();
        jSpinner61 = new javax.swing.JSpinner();
        jSpinner62 = new javax.swing.JSpinner();
        jSpinner63 = new javax.swing.JSpinner();
        jLabel92 = new javax.swing.JLabel();
        jComboBox9 = new javax.swing.JComboBox();
        jPanel38 = new javax.swing.JPanel();
        jCheckBox28 = new javax.swing.JCheckBox();
        jLabel93 = new javax.swing.JLabel();
        jLabel94 = new javax.swing.JLabel();
        jSpinner64 = new javax.swing.JSpinner();
        jSpinner65 = new javax.swing.JSpinner();
        jSpinner4 = new javax.swing.JSpinner();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox6 = new javax.swing.JCheckBox();
        jSpinner5 = new javax.swing.JSpinner();
        jPanel8 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jLabel97 = new javax.swing.JLabel();
        jLabel98 = new javax.swing.JLabel();
        jLabel99 = new javax.swing.JLabel();
        jLabel100 = new javax.swing.JLabel();
        jLabel103 = new javax.swing.JLabel();
        jLabel104 = new javax.swing.JLabel();
        jLabel105 = new javax.swing.JLabel();
        jLabel106 = new javax.swing.JLabel();
        jLabel107 = new javax.swing.JLabel();
        jLabel108 = new javax.swing.JLabel();
        jLabel109 = new javax.swing.JLabel();
        jLabel110 = new javax.swing.JLabel();
        jLabel111 = new javax.swing.JLabel();
        jLabel112 = new javax.swing.JLabel();
        jLabel113 = new javax.swing.JLabel();
        jLabel114 = new javax.swing.JLabel();
        jLabel115 = new javax.swing.JLabel();
        jLabel116 = new javax.swing.JLabel();
        jLabel117 = new javax.swing.JLabel();
        jLabel118 = new javax.swing.JLabel();
        jLabel119 = new javax.swing.JLabel();
        jPanel18 = new javax.swing.JPanel();
        jLabel120 = new javax.swing.JLabel();
        jLabel121 = new javax.swing.JLabel();
        jLabel122 = new javax.swing.JLabel();
        jLabel123 = new javax.swing.JLabel();
        jLabel124 = new javax.swing.JLabel();
        jLabel125 = new javax.swing.JLabel();
        jLabel126 = new javax.swing.JLabel();
        jLabel127 = new javax.swing.JLabel();
        jLabel128 = new javax.swing.JLabel();
        jLabel129 = new javax.swing.JLabel();
        jLabel130 = new javax.swing.JLabel();
        jLabel131 = new javax.swing.JLabel();
        jLabel132 = new javax.swing.JLabel();
        jLabel133 = new javax.swing.JLabel();
        jLabel134 = new javax.swing.JLabel();
        jLabel135 = new javax.swing.JLabel();
        jLabel136 = new javax.swing.JLabel();
        jLabel137 = new javax.swing.JLabel();
        jLabel138 = new javax.swing.JLabel();
        jLabel139 = new javax.swing.JLabel();
        jLabel140 = new javax.swing.JLabel();
        jLabel141 = new javax.swing.JLabel();
        jLabel142 = new javax.swing.JLabel();
        jLabel143 = new javax.swing.JLabel();
        jLabel144 = new javax.swing.JLabel();
        jLabel145 = new javax.swing.JLabel();
        jLabel146 = new javax.swing.JLabel();
        jLabel147 = new javax.swing.JLabel();
        jLabel148 = new javax.swing.JLabel();
        jLabel149 = new javax.swing.JLabel();
        jLabel150 = new javax.swing.JLabel();
        jLabel151 = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        jLabel152 = new javax.swing.JLabel();
        jLabel153 = new javax.swing.JLabel();
        jLabel154 = new javax.swing.JLabel();
        jLabel155 = new javax.swing.JLabel();
        jLabel156 = new javax.swing.JLabel();
        jLabel157 = new javax.swing.JLabel();
        jLabel158 = new javax.swing.JLabel();
        jLabel159 = new javax.swing.JLabel();
        jLabel160 = new javax.swing.JLabel();
        jLabel161 = new javax.swing.JLabel();
        jLabel162 = new javax.swing.JLabel();
        jLabel163 = new javax.swing.JLabel();
        jLabel164 = new javax.swing.JLabel();
        jLabel165 = new javax.swing.JLabel();
        jLabel166 = new javax.swing.JLabel();
        jLabel167 = new javax.swing.JLabel();
        jLabel168 = new javax.swing.JLabel();
        jLabel169 = new javax.swing.JLabel();
        jLabel170 = new javax.swing.JLabel();
        jLabel171 = new javax.swing.JLabel();
        jLabel172 = new javax.swing.JLabel();
        jLabel173 = new javax.swing.JLabel();
        jLabel174 = new javax.swing.JLabel();
        jLabel175 = new javax.swing.JLabel();
        jLabel176 = new javax.swing.JLabel();
        jLabel177 = new javax.swing.JLabel();
        jLabel178 = new javax.swing.JLabel();
        jLabel179 = new javax.swing.JLabel();
        jLabel180 = new javax.swing.JLabel();
        jLabel181 = new javax.swing.JLabel();
        jLabel182 = new javax.swing.JLabel();
        jLabel183 = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        jLabel184 = new javax.swing.JLabel();
        jLabel185 = new javax.swing.JLabel();
        jLabel186 = new javax.swing.JLabel();
        jLabel187 = new javax.swing.JLabel();
        jLabel188 = new javax.swing.JLabel();
        jLabel189 = new javax.swing.JLabel();
        jLabel190 = new javax.swing.JLabel();
        jLabel191 = new javax.swing.JLabel();
        jLabel192 = new javax.swing.JLabel();
        jLabel193 = new javax.swing.JLabel();
        jLabel194 = new javax.swing.JLabel();
        jLabel195 = new javax.swing.JLabel();
        jLabel196 = new javax.swing.JLabel();
        jLabel197 = new javax.swing.JLabel();
        jLabel198 = new javax.swing.JLabel();
        jLabel199 = new javax.swing.JLabel();
        jLabel200 = new javax.swing.JLabel();
        jLabel201 = new javax.swing.JLabel();
        jLabel202 = new javax.swing.JLabel();
        jLabel203 = new javax.swing.JLabel();
        jLabel204 = new javax.swing.JLabel();
        jLabel205 = new javax.swing.JLabel();
        jLabel206 = new javax.swing.JLabel();
        jLabel207 = new javax.swing.JLabel();
        jLabel208 = new javax.swing.JLabel();
        jLabel209 = new javax.swing.JLabel();
        jLabel210 = new javax.swing.JLabel();
        jLabel211 = new javax.swing.JLabel();
        jLabel212 = new javax.swing.JLabel();
        jLabel213 = new javax.swing.JLabel();
        jLabel214 = new javax.swing.JLabel();
        jLabel215 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jPanel21 = new javax.swing.JPanel();
        jPanel22 = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        jPanel23 = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        jPanel24 = new javax.swing.JPanel();
        jLabel38 = new javax.swing.JLabel();
        jPanel25 = new javax.swing.JPanel();
        jLabel39 = new javax.swing.JLabel();
        jPanel26 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jPanel40 = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        jSlider1 = new javax.swing.JSlider();
        jLabel41 = new javax.swing.JLabel();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        jPanel51 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jCheckBox3 = new javax.swing.JCheckBox();
        jCheckBox4 = new javax.swing.JCheckBox();
        jCheckBox5 = new javax.swing.JCheckBox();
        jPanel41 = new javax.swing.JPanel();
        jPanel42 = new javax.swing.JPanel();
        jPanel42 = charters[1].getChartPanel();
        jPanel44 = new javax.swing.JPanel();
        jPanel44 = charters[2].getChartPanel();
        jPanel45 = new javax.swing.JPanel();
        jPanel45 = charters[3].getChartPanel();
        jPanel46 = new javax.swing.JPanel();
        jPanel46 = charters[0].getChartPanel();
        jPanel13 = new javax.swing.JPanel();
        jPanel39 = new javax.swing.JPanel();
        jPanel39 = spectrumCharts[2].getChartPanel();
        jPanel48 = new javax.swing.JPanel();
        jPanel48 = spectrumCharts[0].getChartPanel();
        jPanel49 = new javax.swing.JPanel();
        jPanel49 = spectrumCharts[3].getChartPanel();
        jPanel50 = new javax.swing.JPanel();
        jPanel50 = spectrumCharts[1].getChartPanel();
        jPanel52 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        logTextArea = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTabbedPane2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane2StateChanged(evt);
            }
        });

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 0));

        experimentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Experiment"));

        jLabel3.setText("Samples");

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, experimentPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), jLabel3, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        samplesFileSelectButton.setText("Select");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, experimentPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), samplesFileSelectButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        samplesFileSelectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                samplesFileSelectButtonActionPerformed(evt);
            }
        });

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, experimentPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), samplesFileTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        samplesFileTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                samplesFileTextFieldActionPerformed(evt);
            }
        });

        transposeCheckBox.setText("Transpose");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, experimentPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), transposeCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        namesLabel.setText("Names");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, experimentPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), namesLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, experimentPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), namesFileTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        autogenerateNamesCheckBox.setText("Autogenerate");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, experimentPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), autogenerateNamesCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        autogenerateNamesCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autogenerateNamesCheckBoxActionPerformed(evt);
            }
        });

        samplingTimeSpinner.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(1.0d), Double.valueOf(0.0010d), null, Double.valueOf(0.0010d)));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, experimentPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), samplingTimeSpinner, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel1.setText("Ts");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, experimentPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), jLabel1, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel2.setText("Time Unit");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, experimentPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), jLabel2, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        timeUnitTextField.setText("s");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, experimentPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), timeUnitTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        disableTimeUnitCheckBox.setSelected(true);
        disableTimeUnitCheckBox.setText("Disable TU");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, experimentPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), disableTimeUnitCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        loadFilesButton.setText("Load");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, experimentPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), loadFilesButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        loadFilesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadFilesButtonActionPerformed(evt);
            }
        });

        experimentNameLabel.setText("Name");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, experimentPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), experimentNameLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        experimentNameTextField.setText("Experiment");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, experimentPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), experimentNameTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        experimentNameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                experimentNameTextFieldActionPerformed(evt);
            }
        });

        jButton1.setText("Select");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, experimentPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), jButton1, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout experimentPanelLayout = new javax.swing.GroupLayout(experimentPanel);
        experimentPanel.setLayout(experimentPanelLayout);
        experimentPanelLayout.setHorizontalGroup(
            experimentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(experimentPanelLayout.createSequentialGroup()
                .addGroup(experimentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(experimentPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(experimentNameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(experimentNameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 513, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, experimentPanelLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(experimentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(namesLabel)
                            .addComponent(jLabel3)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(experimentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(experimentPanelLayout.createSequentialGroup()
                                .addGroup(experimentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jButton1, 0, 0, Short.MAX_VALUE)
                                    .addComponent(samplesFileSelectButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(experimentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(namesFileTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
                                    .addComponent(samplesFileTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, experimentPanelLayout.createSequentialGroup()
                                .addComponent(samplingTimeSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(timeUnitTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(experimentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(disableTimeUnitCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(transposeCheckBox)
                            .addComponent(autogenerateNamesCheckBox)))
                    .addGroup(experimentPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(loadFilesButton, javax.swing.GroupLayout.DEFAULT_SIZE, 544, Short.MAX_VALUE)))
                .addContainerGap())
        );
        experimentPanelLayout.setVerticalGroup(
            experimentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(experimentPanelLayout.createSequentialGroup()
                .addGroup(experimentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(experimentNameLabel)
                    .addComponent(experimentNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(experimentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(transposeCheckBox)
                    .addComponent(samplesFileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(samplesFileSelectButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(experimentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(namesFileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(autogenerateNamesCheckBox)
                    .addComponent(namesLabel)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(experimentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(samplingTimeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(timeUnitTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(disableTimeUnitCheckBox)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(loadFilesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        StatisticsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Experiment Statistics"));

        jLabel12.setText("State");

        stateLabel.setBackground(new java.awt.Color(255, 0, 0));
        stateLabel.setText("Non Loaded Yet");

        jLabel5.setText("Ts");

        samplingTimeLabel.setText("0");

        jLabel6.setText("Profiles");

        jLabel7.setText("Samples");

        profilesNumberLabel.setText("0");

        samplesNumberLabel.setText("0");

        javax.swing.GroupLayout StatisticsPanelLayout = new javax.swing.GroupLayout(StatisticsPanel);
        StatisticsPanel.setLayout(StatisticsPanelLayout);
        StatisticsPanelLayout.setHorizontalGroup(
            StatisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(StatisticsPanelLayout.createSequentialGroup()
                .addGroup(StatisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(StatisticsPanelLayout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel12))
                    .addGroup(StatisticsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(StatisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel6)
                            .addComponent(jLabel5))))
                .addGap(18, 18, 18)
                .addGroup(StatisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(StatisticsPanelLayout.createSequentialGroup()
                        .addComponent(samplingTimeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(timeUnitLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE))
                    .addComponent(stateLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
                    .addComponent(profilesNumberLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
                    .addComponent(samplesNumberLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE))
                .addContainerGap())
        );
        StatisticsPanelLayout.setVerticalGroup(
            StatisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(StatisticsPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(StatisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(stateLabel))
                .addGap(17, 17, 17)
                .addGroup(StatisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(samplingTimeLabel)
                    .addComponent(timeUnitLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(StatisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(profilesNumberLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(StatisticsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(samplesNumberLabel)))
        );

        workingStatusPanel.setBackground(new java.awt.Color(255, 0, 0));
        workingStatusPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        workingStatusLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        workingStatusLabel.setText("NOT READY");

        javax.swing.GroupLayout workingStatusPanelLayout = new javax.swing.GroupLayout(workingStatusPanel);
        workingStatusPanel.setLayout(workingStatusPanelLayout);
        workingStatusPanelLayout.setHorizontalGroup(
            workingStatusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(workingStatusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(workingStatusLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
                .addContainerGap())
        );
        workingStatusPanelLayout.setVerticalGroup(
            workingStatusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(workingStatusLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
        );

        preElaborationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Pre-Elaboration"));
        preElaborationPanel.setEnabled(false);

        selectionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Selection"));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, preElaborationPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), selectionPanel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel16.setText("Min Variance");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, selectionPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), jLabel16, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        minVarianceSelectionSpinner.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(0.4d), Double.valueOf(0.0d), null, Double.valueOf(0.0010d)));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, selectionPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), minVarianceSelectionSpinner, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        goSelectionButton.setText("Go");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, selectionPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), goSelectionButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        goSelectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goSelectionButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout selectionPanelLayout = new javax.swing.GroupLayout(selectionPanel);
        selectionPanel.setLayout(selectionPanelLayout);
        selectionPanelLayout.setHorizontalGroup(
            selectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(selectionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(selectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(selectionPanelLayout.createSequentialGroup()
                        .addGroup(selectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(selectionPanelLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(minVarianceSelectionSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE))
                            .addComponent(jLabel16))
                        .addContainerGap())
                    .addComponent(goSelectionButton, javax.swing.GroupLayout.Alignment.TRAILING)))
        );
        selectionPanelLayout.setVerticalGroup(
            selectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(selectionPanelLayout.createSequentialGroup()
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(minVarianceSelectionSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 52, Short.MAX_VALUE)
                .addComponent(goSelectionButton))
        );

        clustreingPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Clustering"));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, preElaborationPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), clustreingPanel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel17.setText("Distance Type");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, clustreingPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), jLabel17, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        distanceTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TotalError", "MaximumError", "MeanError", "MeanSquareError", "RootMeanSquareError" }));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, clustreingPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), distanceTypeComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        distanceTypeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                distanceTypeComboBoxActionPerformed(evt);
            }
        });

        jLabel18.setText("Radius");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, clustreingPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), jLabel18, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        clusteringRadiusSpinner.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(0.0f), null, Float.valueOf(0.0010f)));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, clustreingPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), clusteringRadiusSpinner, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        goClusteringButton.setText("Go");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, clustreingPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), goClusteringButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        goClusteringButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goClusteringButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout clustreingPanelLayout = new javax.swing.GroupLayout(clustreingPanel);
        clustreingPanel.setLayout(clustreingPanelLayout);
        clustreingPanelLayout.setHorizontalGroup(
            clustreingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clustreingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(clustreingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(clustreingPanelLayout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addContainerGap())
                    .addComponent(jLabel18)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, clustreingPanelLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(clusteringRadiusSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                        .addContainerGap())))
            .addGroup(clustreingPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(distanceTypeComboBox, 0, 150, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, clustreingPanelLayout.createSequentialGroup()
                .addContainerGap(135, Short.MAX_VALUE)
                .addComponent(goClusteringButton))
        );
        clustreingPanelLayout.setVerticalGroup(
            clustreingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clustreingPanelLayout.createSequentialGroup()
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(distanceTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(clusteringRadiusSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(goClusteringButton))
        );

        interpolatePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Interpolate"));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, preElaborationPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), interpolatePanel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel19.setText("Samples");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, interpolatePanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), jLabel19, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        newNumberOfSamplesInterpolateSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(20), Integer.valueOf(2), null, Integer.valueOf(1)));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, interpolatePanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), newNumberOfSamplesInterpolateSpinner, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        goInterpolateButton.setText("Go");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, interpolatePanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), goInterpolateButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        goInterpolateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goInterpolateButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout interpolatePanelLayout = new javax.swing.GroupLayout(interpolatePanel);
        interpolatePanel.setLayout(interpolatePanelLayout);
        interpolatePanelLayout.setHorizontalGroup(
            interpolatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(interpolatePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(interpolatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(goInterpolateButton, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, interpolatePanelLayout.createSequentialGroup()
                        .addGroup(interpolatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(interpolatePanelLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(newNumberOfSamplesInterpolateSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE))
                            .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        interpolatePanelLayout.setVerticalGroup(
            interpolatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(interpolatePanelLayout.createSequentialGroup()
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(newNumberOfSamplesInterpolateSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 70, Short.MAX_VALUE)
                .addComponent(goInterpolateButton))
        );

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, selectionPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), selectionProgressBar, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, clustreingPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), clusteringProgressBar, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout preElaborationPanelLayout = new javax.swing.GroupLayout(preElaborationPanel);
        preElaborationPanel.setLayout(preElaborationPanelLayout);
        preElaborationPanelLayout.setHorizontalGroup(
            preElaborationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(preElaborationPanelLayout.createSequentialGroup()
                .addGroup(preElaborationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(selectionProgressBar, javax.swing.GroupLayout.Alignment.LEADING, 0, 0, Short.MAX_VALUE)
                    .addComponent(selectionPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(preElaborationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(clusteringProgressBar, 0, 0, Short.MAX_VALUE)
                    .addComponent(clustreingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(preElaborationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(preElaborationPanelLayout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(interpolateProgressBar, 0, 0, Short.MAX_VALUE))
                    .addGroup(preElaborationPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(interpolatePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        preElaborationPanelLayout.setVerticalGroup(
            preElaborationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(preElaborationPanelLayout.createSequentialGroup()
                .addGroup(preElaborationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(preElaborationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(selectionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(clustreingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(interpolatePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(preElaborationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(preElaborationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(clusteringProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(selectionProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(interpolateProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        operationsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Operations"));
        operationsPanel.setEnabled(false);

        discardLastButton.setText("Discard Last");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, operationsPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), discardLastButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        discardLastButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                discardLastButtonActionPerformed(evt);
            }
        });

        revoverFirstButton.setText("Recover First");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, operationsPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), revoverFirstButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        revoverFirstButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                revoverFirstButtonActionPerformed(evt);
            }
        });

        saveToDiskButton.setText("Save To Disk");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, operationsPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), saveToDiskButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        saveToDiskButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveToDiskButtonActionPerformed(evt);
            }
        });

        lockExperimentButton.setText("Lock Experiment");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, operationsPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), lockExperimentButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        lockExperimentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lockExperimentButtonActionPerformed(evt);
            }
        });

        jButton5.setText("Clusterize Names");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, operationsPanel, org.jdesktop.beansbinding.ELProperty.create("${enabled}"), jButton5, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jLabel8.setText("Se vuoi nomi dei geni semplici");

        javax.swing.GroupLayout operationsPanelLayout = new javax.swing.GroupLayout(operationsPanel);
        operationsPanel.setLayout(operationsPanelLayout);
        operationsPanelLayout.setHorizontalGroup(
            operationsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(operationsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(operationsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                    .addComponent(saveToDiskButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                    .addComponent(revoverFirstButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(operationsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(operationsPanelLayout.createSequentialGroup()
                        .addComponent(discardLastButton, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(operationsPanelLayout.createSequentialGroup()
                        .addGroup(operationsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                            .addComponent(lockExperimentButton, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE))
                        .addGap(11, 11, 11))))
        );
        operationsPanelLayout.setVerticalGroup(
            operationsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(operationsPanelLayout.createSequentialGroup()
                .addGroup(operationsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(discardLastButton, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(revoverFirstButton, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(operationsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveToDiskButton, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lockExperimentButton, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(operationsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lockedPanel.setBackground(new java.awt.Color(255, 255, 0));
        lockedPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lockedLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lockedLabel.setText("UNLOCKED");

        javax.swing.GroupLayout lockedPanelLayout = new javax.swing.GroupLayout(lockedPanel);
        lockedPanel.setLayout(lockedPanelLayout);
        lockedPanelLayout.setHorizontalGroup(
            lockedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lockedPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lockedLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)
                .addContainerGap())
        );
        lockedPanelLayout.setVerticalGroup(
            lockedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lockedLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
        );

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("Log"));
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setAutoscrolls(true);

        messageTextArea.setColumns(20);
        messageTextArea.setEditable(false);
        messageTextArea.setRows(5);
        messageTextArea.setWrapStyleWord(true);
        messageTextArea.setBorder(null);
        DefaultCaret caret = (DefaultCaret)messageTextArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        jScrollPane1.setViewportView(messageTextArea);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1022, Short.MAX_VALUE)
                    .addComponent(lockedPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(preElaborationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(experimentPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(operationsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(workingStatusPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(StatisticsPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel9Layout.createSequentialGroup()
                        .addComponent(StatisticsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(workingStatusPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(experimentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(preElaborationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(operationsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lockedPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Input Data", jPanel9);

        jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable.setColumnSelectionAllowed(true);
        jTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(jTable);

        jButton2.setText("Preview");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, experimentPanel, org.jdesktop.beansbinding.ELProperty.create("${!enabled}"), jButton2, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1022, Short.MAX_VALUE)
                    .addComponent(jButton2))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 605, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Look Experiment", jPanel11);

        jTabbedPane2.addTab("Data", jTabbedPane1);

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Island 1"));
        jPanel10.setPreferredSize(new java.awt.Dimension(350, 238));

        jPanel27.setBorder(javax.swing.BorderFactory.createTitledBorder("Migration Strategy"));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox18, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jPanel27, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel59.setText("Migration Probability");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox18, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel59, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel60.setText("Emigrates Number");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox18, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel60, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jCheckBox17.setSelected(true);
        jCheckBox17.setText("Enable");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox18, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jCheckBox17, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jSpinner38.setModel(new javax.swing.SpinnerNumberModel(0.0050d, 0.0010d, 1.0d, 0.0010d));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox18, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner38, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jSpinner39.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(100), Integer.valueOf(1), null, Integer.valueOf(1)));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox18, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner39, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox17)
                    .addGroup(jPanel27Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel60)
                            .addComponent(jLabel59))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSpinner38, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                    .addComponent(jSpinner39, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)))
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addComponent(jCheckBox17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel60)
                    .addComponent(jSpinner39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel59)
                    .addComponent(jSpinner38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jCheckBox18.setSelected(true);
        jCheckBox18.setText("Enable");
        jCheckBox18.setEnabled(false);
        jCheckBox18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox18ActionPerformed(evt);
            }
        });

        jLabel61.setText("Threads Number");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox18, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel61, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jPanel28.setBorder(javax.swing.BorderFactory.createTitledBorder("Population"));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox18, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jPanel28, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel62.setText("Island Size");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox18, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel62, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel63.setText("Generation Size");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox18, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel63, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel64.setText("Max Worst Fit");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox18, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel64, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jSpinner40.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1000), Integer.valueOf(10), null, Integer.valueOf(10)));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox18, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner40, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jSpinner41.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(100), Integer.valueOf(100), null, Integer.valueOf(10)));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox18, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner41, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jSpinner42.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1000000000), Integer.valueOf(100), null, Integer.valueOf(100)));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox18, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner42, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel65.setText("Fitness Type");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox18, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel65, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TotE", "MeanE", "MaxE", "MSE", "RMSE", "MixedE" }));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox18, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jComboBox6, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jComboBox6.setSelectedIndex(5);

        javax.swing.GroupLayout jPanel28Layout = new javax.swing.GroupLayout(jPanel28);
        jPanel28.setLayout(jPanel28Layout);
        jPanel28Layout.setHorizontalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel62)
                    .addComponent(jLabel64)
                    .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel65)
                        .addComponent(jLabel63)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSpinner41, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                    .addComponent(jSpinner40, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                    .addComponent(jSpinner42, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)))
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBox6, 0, 201, Short.MAX_VALUE)
                .addGap(1, 1, 1))
        );
        jPanel28Layout.setVerticalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel62)
                    .addComponent(jSpinner40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel63)
                    .addComponent(jSpinner41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel64)
                    .addComponent(jSpinner42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel65)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel29.setBorder(javax.swing.BorderFactory.createTitledBorder("Disaster Strategy"));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox18, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jPanel29, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jCheckBox19.setSelected(true);
        jCheckBox19.setText("Enable");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox18, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jCheckBox19, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel66.setText("Disaster Period");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox18, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel66, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel67.setText("Survivors Number");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox18, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel67, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jSpinner43.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1000), Integer.valueOf(10), null, Integer.valueOf(1)));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox18, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner43, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jSpinner44.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(100), Integer.valueOf(1), null, Integer.valueOf(1)));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox18, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner44, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel29Layout = new javax.swing.GroupLayout(jPanel29);
        jPanel29.setLayout(jPanel29Layout);
        jPanel29Layout.setHorizontalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel29Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel66)
                            .addComponent(jLabel67))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSpinner44, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                            .addComponent(jSpinner43, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)))
                    .addGroup(jPanel29Layout.createSequentialGroup()
                        .addComponent(jCheckBox19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 187, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel29Layout.setVerticalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addComponent(jCheckBox19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel66)
                    .addComponent(jSpinner43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel67)
                    .addComponent(jSpinner44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(2), Integer.valueOf(1), null, Integer.valueOf(1)));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox18, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner1, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel61, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(jCheckBox18, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                                .addGap(26, 26, 26)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner1, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel27, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jCheckBox18)
                .addGap(0, 0, 0)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel61)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel27, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel29, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                .addGap(1, 1, 1))
        );

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder("Island 3"));
        jPanel12.setPreferredSize(new java.awt.Dimension(350, 238));

        jPanel30.setBorder(javax.swing.BorderFactory.createTitledBorder("Migration Strategy"));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox21, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jPanel30, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel68.setText("Migration Probability");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox21, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel68, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel69.setText("Emigrates Number");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox21, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel69, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jCheckBox20.setSelected(true);
        jCheckBox20.setText("Enable");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox21, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jCheckBox20, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jSpinner45.setModel(new javax.swing.SpinnerNumberModel(0.0050d, 0.0010d, 1.0d, 0.0010d));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox21, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner45, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jSpinner46.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(100), Integer.valueOf(1), null, Integer.valueOf(1)));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox21, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner46, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel30Layout = new javax.swing.GroupLayout(jPanel30);
        jPanel30.setLayout(jPanel30Layout);
        jPanel30Layout.setHorizontalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox20)
                    .addGroup(jPanel30Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel69)
                            .addComponent(jLabel68))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSpinner45, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                    .addComponent(jSpinner46, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)))
        );
        jPanel30Layout.setVerticalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addComponent(jCheckBox20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel69)
                    .addComponent(jSpinner46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel68)
                    .addComponent(jSpinner45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jCheckBox21.setText("Enable");

        jLabel70.setText("Threads Number");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox21, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel70, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jPanel31.setBorder(javax.swing.BorderFactory.createTitledBorder("Population"));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox21, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jPanel31, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel71.setText("Island Size");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox21, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel71, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel72.setText("Generation Size");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox21, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel72, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel73.setText("Max Worst Fit");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox21, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel73, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jSpinner47.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(10000), Integer.valueOf(10), null, Integer.valueOf(10)));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox21, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner47, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jSpinner48.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1000), Integer.valueOf(100), null, Integer.valueOf(10)));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox21, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner48, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jSpinner49.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1000000000), Integer.valueOf(100), null, Integer.valueOf(100)));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox21, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner49, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel74.setText("Fitness Type");

        jComboBox7.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TotE", "MeanE", "MaxE", "MSE", "RMSE", "MixedE" }));
        jComboBox7.setSelectedIndex(2);

        javax.swing.GroupLayout jPanel31Layout = new javax.swing.GroupLayout(jPanel31);
        jPanel31.setLayout(jPanel31Layout);
        jPanel31Layout.setHorizontalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel31Layout.createSequentialGroup()
                .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel71)
                    .addComponent(jLabel73)
                    .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel74)
                        .addComponent(jLabel72)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSpinner48, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                    .addComponent(jSpinner47, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                    .addComponent(jSpinner49, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)))
            .addGroup(jPanel31Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBox7, 0, 201, Short.MAX_VALUE)
                .addGap(1, 1, 1))
        );
        jPanel31Layout.setVerticalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel31Layout.createSequentialGroup()
                .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel71)
                    .addComponent(jSpinner47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel72)
                    .addComponent(jSpinner48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel73)
                    .addComponent(jSpinner49, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel74)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel32.setBorder(javax.swing.BorderFactory.createTitledBorder("Disaster Strategy"));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox21, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jPanel32, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jCheckBox22.setSelected(true);
        jCheckBox22.setText("Enable");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox21, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jCheckBox22, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel75.setText("Disaster Period");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox21, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel75, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel76.setText("Survivors Number");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox21, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel76, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jSpinner50.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1000), Integer.valueOf(10), null, Integer.valueOf(1)));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox21, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner50, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jSpinner51.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(100), Integer.valueOf(1), null, Integer.valueOf(1)));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox21, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner51, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel32Layout = new javax.swing.GroupLayout(jPanel32);
        jPanel32.setLayout(jPanel32Layout);
        jPanel32Layout.setHorizontalGroup(
            jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel32Layout.createSequentialGroup()
                .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel32Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel75)
                            .addComponent(jLabel76))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSpinner51, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                            .addComponent(jSpinner50, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)))
                    .addGroup(jPanel32Layout.createSequentialGroup()
                        .addComponent(jCheckBox22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 187, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel32Layout.setVerticalGroup(
            jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel32Layout.createSequentialGroup()
                .addComponent(jCheckBox22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel75)
                    .addComponent(jSpinner50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel76)
                    .addComponent(jSpinner51, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jSpinner2.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(2), Integer.valueOf(1), null, Integer.valueOf(1)));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox21, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner2, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel70, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(jCheckBox21, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                                .addGap(26, 26, 26)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner2, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel30, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(jCheckBox21)
                .addGap(0, 0, 0)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel70)
                    .addComponent(jSpinner2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel30, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel32, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                .addGap(1, 1, 1))
        );

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder("Island 4"));
        jPanel14.setPreferredSize(new java.awt.Dimension(350, 238));

        jPanel33.setBorder(javax.swing.BorderFactory.createTitledBorder("Migration Strategy"));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox24, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jPanel33, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel77.setText("Migration Probability");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox24, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel77, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel78.setText("Emigrates Number");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox24, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel78, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jCheckBox23.setSelected(true);
        jCheckBox23.setText("Enable");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox24, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jCheckBox23, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jSpinner52.setModel(new javax.swing.SpinnerNumberModel(0.0050d, 0.0010d, 1.0d, 0.0010d));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox24, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner52, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jSpinner53.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(100), Integer.valueOf(1), null, Integer.valueOf(1)));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox24, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner53, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel33Layout = new javax.swing.GroupLayout(jPanel33);
        jPanel33.setLayout(jPanel33Layout);
        jPanel33Layout.setHorizontalGroup(
            jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel33Layout.createSequentialGroup()
                .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox23)
                    .addGroup(jPanel33Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel78)
                            .addComponent(jLabel77))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSpinner52, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)
                    .addComponent(jSpinner53, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)))
        );
        jPanel33Layout.setVerticalGroup(
            jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel33Layout.createSequentialGroup()
                .addComponent(jCheckBox23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel78)
                    .addComponent(jSpinner53, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel77)
                    .addComponent(jSpinner52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jCheckBox24.setText("Enable");

        jLabel79.setText("Threads Number");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox24, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel79, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jPanel34.setBorder(javax.swing.BorderFactory.createTitledBorder("Population"));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox24, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jPanel34, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel80.setText("Island Size");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox24, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel80, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel81.setText("Generation Size");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox24, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel81, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel82.setText("Max Worst Fit");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox24, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel82, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jSpinner54.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(10000), Integer.valueOf(10), null, Integer.valueOf(10)));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox24, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner54, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jSpinner55.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1000), Integer.valueOf(100), null, Integer.valueOf(10)));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox24, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner55, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jSpinner56.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1000000000), Integer.valueOf(100), null, Integer.valueOf(100)));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox24, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner56, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel83.setText("Fitness Type");

        jComboBox8.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TotE", "MeanE", "MaxE", "MSE", "RMSE", "MixedE" }));
        jComboBox8.setSelectedIndex(4);

        javax.swing.GroupLayout jPanel34Layout = new javax.swing.GroupLayout(jPanel34);
        jPanel34.setLayout(jPanel34Layout);
        jPanel34Layout.setHorizontalGroup(
            jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel34Layout.createSequentialGroup()
                .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel80)
                    .addComponent(jLabel82)
                    .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel83)
                        .addComponent(jLabel81)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSpinner55, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                    .addComponent(jSpinner54, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                    .addComponent(jSpinner56, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)))
            .addGroup(jPanel34Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBox8, 0, 162, Short.MAX_VALUE)
                .addGap(1, 1, 1))
        );
        jPanel34Layout.setVerticalGroup(
            jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel34Layout.createSequentialGroup()
                .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel80)
                    .addComponent(jSpinner54, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel81)
                    .addComponent(jSpinner55, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel34Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel82)
                    .addComponent(jSpinner56, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel83)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel35.setBorder(javax.swing.BorderFactory.createTitledBorder("Disaster Strategy"));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox24, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jPanel35, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jCheckBox25.setSelected(true);
        jCheckBox25.setText("Enable");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox24, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jCheckBox25, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel84.setText("Disaster Period");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox24, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel84, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel85.setText("Survivors Number");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox24, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel85, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jSpinner57.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1000), Integer.valueOf(10), null, Integer.valueOf(1)));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox24, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner57, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jSpinner58.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(100), Integer.valueOf(1), null, Integer.valueOf(1)));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox24, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner58, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel35Layout = new javax.swing.GroupLayout(jPanel35);
        jPanel35.setLayout(jPanel35Layout);
        jPanel35Layout.setHorizontalGroup(
            jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel35Layout.createSequentialGroup()
                .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel35Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel84)
                            .addComponent(jLabel85))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSpinner58, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
                            .addComponent(jSpinner57, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)))
                    .addGroup(jPanel35Layout.createSequentialGroup()
                        .addComponent(jCheckBox25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 226, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel35Layout.setVerticalGroup(
            jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel35Layout.createSequentialGroup()
                .addComponent(jCheckBox25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel84)
                    .addComponent(jSpinner57, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(jPanel35Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel85)
                    .addComponent(jSpinner58, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jSpinner3.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(2), Integer.valueOf(1), null, Integer.valueOf(1)));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox24, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner3, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel34, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel79, javax.swing.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE)
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addComponent(jCheckBox24, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                                .addGap(26, 26, 26)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner3, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel35, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel33, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(jCheckBox24)
                .addGap(0, 0, 0)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel79)
                    .addComponent(jSpinner3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel34, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel33, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel35, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                .addGap(1, 1, 1))
        );

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder("Island 2"));
        jPanel15.setPreferredSize(new java.awt.Dimension(350, 238));

        jPanel36.setBorder(javax.swing.BorderFactory.createTitledBorder("Migration Strategy"));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox27, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jPanel36, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel86.setText("Migration Probability");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox27, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel86, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel87.setText("Emigrates Number");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox27, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel87, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jCheckBox26.setSelected(true);
        jCheckBox26.setText("Enable");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox27, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jCheckBox26, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jSpinner59.setModel(new javax.swing.SpinnerNumberModel(0.0050d, 0.0010d, 1.0d, 0.0010d));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox27, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner59, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jSpinner60.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(100), Integer.valueOf(1), null, Integer.valueOf(1)));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox27, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner60, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel36Layout = new javax.swing.GroupLayout(jPanel36);
        jPanel36.setLayout(jPanel36Layout);
        jPanel36Layout.setHorizontalGroup(
            jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel36Layout.createSequentialGroup()
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox26)
                    .addGroup(jPanel36Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel87)
                            .addComponent(jLabel86))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSpinner59, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                    .addComponent(jSpinner60, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)))
        );
        jPanel36Layout.setVerticalGroup(
            jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel36Layout.createSequentialGroup()
                .addComponent(jCheckBox26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel87)
                    .addComponent(jSpinner60, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel36Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel86)
                    .addComponent(jSpinner59, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jCheckBox27.setText("Enable");

        jLabel88.setText("Threads Number");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox27, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel88, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jPanel37.setBorder(javax.swing.BorderFactory.createTitledBorder("Population"));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox27, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jPanel37, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel89.setText("Island Size");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox27, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel89, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel90.setText("Generation Size");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox27, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel90, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel91.setText("Max Worst Fit");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox27, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel91, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jSpinner61.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(10000), Integer.valueOf(10), null, Integer.valueOf(10)));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox27, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner61, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jSpinner62.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1000), Integer.valueOf(100), null, Integer.valueOf(10)));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox27, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner62, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jSpinner63.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1000000000), Integer.valueOf(100), null, Integer.valueOf(100)));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox27, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner63, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel92.setText("Fitness Type");

        jComboBox9.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TotE", "MeanE", "MaxE", "MSE", "RMSE", "MixedE" }));
        jComboBox9.setSelectedIndex(2);
        jComboBox9.setSelectedIndex(0);

        javax.swing.GroupLayout jPanel37Layout = new javax.swing.GroupLayout(jPanel37);
        jPanel37.setLayout(jPanel37Layout);
        jPanel37Layout.setHorizontalGroup(
            jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel37Layout.createSequentialGroup()
                .addGroup(jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel89)
                    .addComponent(jLabel91)
                    .addGroup(jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel92)
                        .addComponent(jLabel90)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSpinner62, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                    .addComponent(jSpinner61, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                    .addComponent(jSpinner63, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)))
            .addGroup(jPanel37Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBox9, 0, 198, Short.MAX_VALUE)
                .addGap(1, 1, 1))
        );
        jPanel37Layout.setVerticalGroup(
            jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel37Layout.createSequentialGroup()
                .addGroup(jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel89)
                    .addComponent(jSpinner61, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel90)
                    .addComponent(jSpinner62, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel37Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel91)
                    .addComponent(jSpinner63, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel92)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel38.setBorder(javax.swing.BorderFactory.createTitledBorder("Disaster Strategy"));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox27, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jPanel38, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jCheckBox28.setSelected(true);
        jCheckBox28.setText("Enable");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox27, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jCheckBox28, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel93.setText("Disaster Period");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox27, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel93, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jLabel94.setText("Survivors Number");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox27, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jLabel94, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jSpinner64.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1000), Integer.valueOf(10), null, Integer.valueOf(1)));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox27, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner64, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jSpinner65.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(100), Integer.valueOf(1), null, Integer.valueOf(1)));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox27, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner65, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel38Layout = new javax.swing.GroupLayout(jPanel38);
        jPanel38.setLayout(jPanel38Layout);
        jPanel38Layout.setHorizontalGroup(
            jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel38Layout.createSequentialGroup()
                .addGroup(jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel38Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel93)
                            .addComponent(jLabel94))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSpinner65, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                            .addComponent(jSpinner64, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)))
                    .addGroup(jPanel38Layout.createSequentialGroup()
                        .addComponent(jCheckBox28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 190, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel38Layout.setVerticalGroup(
            jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel38Layout.createSequentialGroup()
                .addComponent(jCheckBox28)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel93)
                    .addComponent(jSpinner64, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel94)
                    .addComponent(jSpinner65, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jSpinner4.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(2), Integer.valueOf(1), null, Integer.valueOf(1)));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox27, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jSpinner4, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel88, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addComponent(jCheckBox27, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                                .addGap(26, 26, 26)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner4, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel36, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(jCheckBox27)
                .addGap(0, 0, 0)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel88)
                    .addComponent(jSpinner4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel36, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel38, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                .addGap(1, 1, 1))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Evolutive World"));

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Controls"));

        jButton3.setText("STOP");
        jButton3.setEnabled(false);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("START");
        jButton4.setEnabled(false);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton6.setText("Save ODEs");
        jButton6.setEnabled(false);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE))
            .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)))
        );

        jCheckBox1.setSelected(true);
        jCheckBox1.setText("Override settings from Island 1");

        jCheckBox2.setSelected(true);
        jCheckBox2.setText("but Fitness Type");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jCheckBox1, org.jdesktop.beansbinding.ELProperty.create("${selected}"), jCheckBox2, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jCheckBox6.setSelected(true);
        jCheckBox6.setText("Save also to sif");

        jSpinner5.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(0.5d), Double.valueOf(0.0d), null, Double.valueOf(0.01d)));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jCheckBox2))
                    .addComponent(jCheckBox1)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jCheckBox6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner5, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBox2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox6)
                    .addComponent(jSpinner5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel8.setBackground(new java.awt.Color(255, 102, 0));
        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Status", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(255, 255, 255))); // NOI18N

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("NOT RUNNING");
        Font f = jLabel4.getFont();
        jLabel4.setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 423, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE)
                            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE)
                            .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE))
                .addGap(32, 32, 32))
        );

        jTabbedPane4.addTab("Eve World Settings", jPanel1);

        jPanel17.setBorder(javax.swing.BorderFactory.createTitledBorder("Island 1"));

        jLabel48.setText("Generation");

        jLabel49.setText("Best Fit");

        jLabel50.setText("Worst Fit");

        jLabel51.setText("Gauss Mean");

        jLabel52.setText("Gaus  Variance");

        jLabel53.setText("Size");

        jLabel54.setText("Killed");

        jLabel55.setText("Threads");

        jLabel56.setText("Total Time");

        jLabel57.setText("Tot Individuals");

        jLabel58.setText("Mean Speed");

        jLabel97.setText("Gen Time");

        jLabel98.setText("Gen Size");

        jLabel99.setText("Gen Speed");

        jLabel100.setText("Disasters");

        jLabel103.setText("Migrations");

        jLabel104.setText("0");

        jLabel105.setText("0");

        jLabel106.setText("0");

        jLabel107.setText("0");

        jLabel108.setText("0");

        jLabel109.setText("0");

        jLabel110.setText("0");

        jLabel111.setText("0");

        jLabel112.setText("0");

        jLabel113.setText("0");

        jLabel114.setText("0");

        jLabel115.setText("0");

        jLabel116.setText("0");

        jLabel117.setText("0");

        jLabel118.setText("0");

        jLabel119.setText("0");

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel103)
                    .addComponent(jLabel53)
                    .addComponent(jLabel52)
                    .addComponent(jLabel54)
                    .addComponent(jLabel55)
                    .addComponent(jLabel56)
                    .addComponent(jLabel50)
                    .addComponent(jLabel49)
                    .addComponent(jLabel48)
                    .addComponent(jLabel57)
                    .addComponent(jLabel58)
                    .addComponent(jLabel97)
                    .addComponent(jLabel98)
                    .addComponent(jLabel99)
                    .addComponent(jLabel100)
                    .addComponent(jLabel51))
                .addGap(18, 18, 18)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel105, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel106, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel107, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel108, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel109, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel110, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel111, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel112, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel113, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel114, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel115, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel116, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel117, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel118, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel119, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel104, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel48)
                    .addComponent(jLabel104))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel49)
                    .addComponent(jLabel105))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel50)
                    .addComponent(jLabel106))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel107)
                    .addComponent(jLabel51))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel52)
                    .addComponent(jLabel108))
                .addGap(5, 5, 5)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel53)
                    .addComponent(jLabel109))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel54)
                    .addComponent(jLabel110))
                .addGap(4, 4, 4)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel55)
                    .addComponent(jLabel111))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel56)
                    .addComponent(jLabel112))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel57)
                    .addComponent(jLabel113))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel58)
                    .addComponent(jLabel114))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel97)
                    .addComponent(jLabel115))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel98)
                    .addComponent(jLabel116))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel99)
                    .addComponent(jLabel117))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel100)
                    .addComponent(jLabel118))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel103)
                    .addComponent(jLabel119)))
        );

        jPanel18.setBorder(javax.swing.BorderFactory.createTitledBorder("Island 2"));

        jLabel120.setText("Generation");

        jLabel121.setText("Best Fit");

        jLabel122.setText("Worst Fit");

        jLabel123.setText("Gaus  Mean");

        jLabel124.setText("Gaus  Variance");

        jLabel125.setText("Size");

        jLabel126.setText("Killed");

        jLabel127.setText("Threads");

        jLabel128.setText("Total Time");

        jLabel129.setText("Tot Individuals");

        jLabel130.setText("Mean Speed");

        jLabel131.setText("Gen Time");

        jLabel132.setText("Gen Size");

        jLabel133.setText("Gen Speed");

        jLabel134.setText("Disasters");

        jLabel135.setText("Migrations");

        jLabel136.setText("0");

        jLabel137.setText("0");

        jLabel138.setText("0");

        jLabel139.setText("0");

        jLabel140.setText("0");

        jLabel141.setText("0");

        jLabel142.setText("0");

        jLabel143.setText("0");

        jLabel144.setText("0");

        jLabel145.setText("0");

        jLabel146.setText("0");

        jLabel147.setText("0");

        jLabel148.setText("0");

        jLabel149.setText("0");

        jLabel150.setText("0");

        jLabel151.setText("0");

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel135)
                    .addComponent(jLabel125)
                    .addComponent(jLabel124)
                    .addComponent(jLabel126)
                    .addComponent(jLabel127)
                    .addComponent(jLabel128)
                    .addComponent(jLabel122)
                    .addComponent(jLabel121)
                    .addComponent(jLabel120)
                    .addComponent(jLabel129)
                    .addComponent(jLabel130)
                    .addComponent(jLabel131)
                    .addComponent(jLabel132)
                    .addComponent(jLabel133)
                    .addComponent(jLabel134)
                    .addComponent(jLabel123))
                .addGap(18, 18, 18)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel137, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel138, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel139, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel140, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel141, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel142, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel143, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel144, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel145, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel146, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel147, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel148, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel149, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel150, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel151, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel136, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel120)
                    .addComponent(jLabel136))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel121)
                    .addComponent(jLabel137))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel122)
                    .addComponent(jLabel138))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel139)
                    .addComponent(jLabel123))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel124)
                    .addComponent(jLabel140))
                .addGap(5, 5, 5)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel125)
                    .addComponent(jLabel141))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel126)
                    .addComponent(jLabel142))
                .addGap(4, 4, 4)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel127)
                    .addComponent(jLabel143))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel128)
                    .addComponent(jLabel144))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel129)
                    .addComponent(jLabel145))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel130)
                    .addComponent(jLabel146))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel131)
                    .addComponent(jLabel147))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel132)
                    .addComponent(jLabel148))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel133)
                    .addComponent(jLabel149))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel134)
                    .addComponent(jLabel150))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel135)
                    .addComponent(jLabel151)))
        );

        jPanel19.setBorder(javax.swing.BorderFactory.createTitledBorder("Island 3"));
        jPanel19.setPreferredSize(new java.awt.Dimension(196, 338));

        jLabel152.setText("Generation");

        jLabel153.setText("Best Fit");

        jLabel154.setText("Worst Fit");

        jLabel155.setText("Gaus  Mean");

        jLabel156.setText("Gaus  Variance");

        jLabel157.setText("Size");

        jLabel158.setText("Killed");

        jLabel159.setText("Threads");

        jLabel160.setText("Total Time");

        jLabel161.setText("Tot Individuals");

        jLabel162.setText("Mean Speed");

        jLabel163.setText("Gen Time");

        jLabel164.setText("Gen Size");

        jLabel165.setText("Gen Speed");

        jLabel166.setText("Disasters");

        jLabel167.setText("Migrations");

        jLabel168.setText("0");

        jLabel169.setText("0");

        jLabel170.setText("0");

        jLabel171.setText("0");

        jLabel172.setText("0");

        jLabel173.setText("0");

        jLabel174.setText("0");

        jLabel175.setText("0");

        jLabel176.setText("0");

        jLabel177.setText("0");

        jLabel178.setText("0");

        jLabel179.setText("0");

        jLabel180.setText("0");

        jLabel181.setText("0");

        jLabel182.setText("0");

        jLabel183.setText("0");

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel167)
                    .addComponent(jLabel157)
                    .addComponent(jLabel156)
                    .addComponent(jLabel158)
                    .addComponent(jLabel159)
                    .addComponent(jLabel160)
                    .addComponent(jLabel154)
                    .addComponent(jLabel153)
                    .addComponent(jLabel152)
                    .addComponent(jLabel161)
                    .addComponent(jLabel162)
                    .addComponent(jLabel163)
                    .addComponent(jLabel164)
                    .addComponent(jLabel165)
                    .addComponent(jLabel166)
                    .addComponent(jLabel155))
                .addGap(18, 18, 18)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel169, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel170, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel171, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel172, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel173, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel174, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel175, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel176, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel177, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel178, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel179, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel180, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel181, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel182, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel183, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel168, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel152)
                    .addComponent(jLabel168))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel153)
                    .addComponent(jLabel169))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel154)
                    .addComponent(jLabel170))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel171)
                    .addComponent(jLabel155))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel156)
                    .addComponent(jLabel172))
                .addGap(5, 5, 5)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel157)
                    .addComponent(jLabel173))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel158)
                    .addComponent(jLabel174))
                .addGap(4, 4, 4)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel159)
                    .addComponent(jLabel175))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel160)
                    .addComponent(jLabel176))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel161)
                    .addComponent(jLabel177))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel162)
                    .addComponent(jLabel178))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel163)
                    .addComponent(jLabel179))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel164)
                    .addComponent(jLabel180))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel165)
                    .addComponent(jLabel181))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel166)
                    .addComponent(jLabel182))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel167)
                    .addComponent(jLabel183)))
        );

        jPanel20.setBorder(javax.swing.BorderFactory.createTitledBorder("Island 4"));

        jLabel184.setText("Generation");

        jLabel185.setText("Best Fit");

        jLabel186.setText("Worst Fit");

        jLabel187.setText("Gaus  Mean");

        jLabel188.setText("Gaus  Variance");

        jLabel189.setText("Size");

        jLabel190.setText("Killed");

        jLabel191.setText("Threads");

        jLabel192.setText("Total Time");

        jLabel193.setText("Tot Individuals");

        jLabel194.setText("Mean Speed");

        jLabel195.setText("Gen Time");

        jLabel196.setText("Gen Size");

        jLabel197.setText("Gen Speed");

        jLabel198.setText("Disasters");

        jLabel199.setText("Migrations");

        jLabel200.setText("0");

        jLabel201.setText("0");

        jLabel202.setText("0");

        jLabel203.setText("0");

        jLabel204.setText("0");

        jLabel205.setText("0");

        jLabel206.setText("0");

        jLabel207.setText("0");

        jLabel208.setText("0");

        jLabel209.setText("0");

        jLabel210.setText("0");

        jLabel211.setText("0");

        jLabel212.setText("0");

        jLabel213.setText("0");

        jLabel214.setText("0");

        jLabel215.setText("0");

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel199)
                    .addComponent(jLabel189)
                    .addComponent(jLabel188)
                    .addComponent(jLabel190)
                    .addComponent(jLabel191)
                    .addComponent(jLabel192)
                    .addComponent(jLabel186)
                    .addComponent(jLabel185)
                    .addComponent(jLabel184)
                    .addComponent(jLabel193)
                    .addComponent(jLabel194)
                    .addComponent(jLabel195)
                    .addComponent(jLabel196)
                    .addComponent(jLabel197)
                    .addComponent(jLabel198)
                    .addComponent(jLabel187))
                .addGap(18, 18, 18)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel201, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel202, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel203, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel204, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel205, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel206, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel207, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel208, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel209, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel210, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel211, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel212, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel213, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel214, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel215, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel200, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel184)
                    .addComponent(jLabel200))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel185)
                    .addComponent(jLabel201))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel186)
                    .addComponent(jLabel202))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel203)
                    .addComponent(jLabel187))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel188)
                    .addComponent(jLabel204))
                .addGap(5, 5, 5)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel189)
                    .addComponent(jLabel205))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel190)
                    .addComponent(jLabel206))
                .addGap(4, 4, 4)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel191)
                    .addComponent(jLabel207))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel192)
                    .addComponent(jLabel208))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel193)
                    .addComponent(jLabel209))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel194)
                    .addComponent(jLabel210))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel195)
                    .addComponent(jLabel211))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel196)
                    .addComponent(jLabel212))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel197)
                    .addComponent(jLabel213))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel198)
                    .addComponent(jLabel214))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel199)
                    .addComponent(jLabel215)))
        );

        jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder("World"));

        jPanel21.setBorder(javax.swing.BorderFactory.createTitledBorder("Islands"));

        jPanel22.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel36.setBackground(new java.awt.Color(255, 255, 255));
        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel36.setText("ISLAND #1");

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel36, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel36, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
        );

        jPanel23.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel37.setBackground(new java.awt.Color(255, 255, 255));
        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel37.setText("ISLAND #2");

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel37, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel37, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
        );

        jPanel24.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel38.setBackground(new java.awt.Color(255, 255, 255));
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel38.setText("ISLAND #3");

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel38, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel38, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
        );

        jPanel25.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel39.setBackground(new java.awt.Color(255, 255, 255));
        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel39.setText("ISLAND #4");

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel39, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel39, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel26.setBorder(javax.swing.BorderFactory.createTitledBorder("Global Statistics"));

        jLabel9.setText("Number Of Islands");

        jLabel10.setText("World Size");

        jLabel11.setText("Generations");

        jLabel13.setText("Killed");

        jLabel14.setText("Total Threads");

        jLabel15.setText("Total Time");

        jLabel20.setText("Total Individuals");

        jLabel21.setText("Mean Speed");

        jLabel24.setText("Disasters");

        jLabel25.setText("Migrations");

        jLabel26.setText("0");

        jLabel27.setText("0");

        jLabel28.setText("0");

        jLabel29.setText("0");

        jLabel30.setText("0");

        jLabel31.setText("0");

        jLabel32.setText("0");

        jLabel33.setText("0");

        jLabel34.setText("0");

        jLabel35.setText("0");

        jPanel40.setBorder(javax.swing.BorderFactory.createTitledBorder("Refresh Period"));

        jLabel40.setText("Seconds");

        jSlider1.setMaximum(15000);
        jSlider1.setMinimum(100);
        jSlider1.setSnapToTicks(true);
        jSlider1.setValue(1000);
        jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider1StateChanged(evt);
            }
        });

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, jSlider1, org.jdesktop.beansbinding.ELProperty.create("${value/1000}"), jLabel41, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout jPanel40Layout = new javax.swing.GroupLayout(jPanel40);
        jPanel40.setLayout(jPanel40Layout);
        jPanel40Layout.setHorizontalGroup(
            jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel40Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSlider1, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                    .addGroup(jPanel40Layout.createSequentialGroup()
                        .addComponent(jLabel40)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel40Layout.setVerticalGroup(
            jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel40Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel40)
                    .addComponent(jLabel41))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel25)
                    .addComponent(jLabel24)
                    .addComponent(jLabel20)
                    .addComponent(jLabel13)
                    .addComponent(jLabel11)
                    .addComponent(jLabel10)
                    .addComponent(jLabel9))
                .addGap(18, 18, 18)
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel26Layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel14)
                            .addComponent(jLabel15)
                            .addComponent(jLabel21))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel33, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                            .addComponent(jLabel34, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel35, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel26Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel40, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel26Layout.createSequentialGroup()
                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(jLabel26))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(jLabel27))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(jLabel28))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(jLabel29))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel20)
                            .addComponent(jLabel30))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel24)
                            .addComponent(jLabel31))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel25)
                            .addComponent(jLabel32))
                        .addContainerGap(91, Short.MAX_VALUE))
                    .addGroup(jPanel26Layout.createSequentialGroup()
                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(jLabel33))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(jLabel34))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel21)
                            .addComponent(jLabel35))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 90, Short.MAX_VALUE)
                        .addComponent(jPanel40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel16, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane4.addTab("World Statistics", jPanel2);

        jLabel23.setText("Per le lunghe esecuzioni di Eve senza supervisione, cosiglio di disattivare tutti i grafici in quanto la loro elaborazione ? esosa.");

        jCheckBox3.setSelected(true);
        jCheckBox3.setText("Fitness Graphics");

        jCheckBox4.setSelected(true);
        jCheckBox4.setText("Convergence Graphics");

        jCheckBox5.setSelected(true);
        jCheckBox5.setText("Best Individuals Tables");

        javax.swing.GroupLayout jPanel51Layout = new javax.swing.GroupLayout(jPanel51);
        jPanel51.setLayout(jPanel51Layout);
        jPanel51Layout.setHorizontalGroup(
            jPanel51Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel51Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel51Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox5)
                    .addComponent(jCheckBox4)
                    .addComponent(jCheckBox3)
                    .addComponent(jLabel23))
                .addContainerGap(433, Short.MAX_VALUE))
        );
        jPanel51Layout.setVerticalGroup(
            jPanel51Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel51Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel23)
                .addGap(18, 18, 18)
                .addComponent(jCheckBox3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox5)
                .addContainerGap(510, Short.MAX_VALUE))
        );

        jTabbedPane3.addTab("Settings", jPanel51);

        jPanel42.setBorder(javax.swing.BorderFactory.createTitledBorder("Island 2"));

        javax.swing.GroupLayout jPanel42Layout = new javax.swing.GroupLayout(jPanel42);
        jPanel42.setLayout(jPanel42Layout);
        jPanel42Layout.setHorizontalGroup(
            jPanel42Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 474, Short.MAX_VALUE)
        );
        jPanel42Layout.setVerticalGroup(
            jPanel42Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 280, Short.MAX_VALUE)
        );

        jPanel44.setBorder(javax.swing.BorderFactory.createTitledBorder("Island 3"));

        javax.swing.GroupLayout jPanel44Layout = new javax.swing.GroupLayout(jPanel44);
        jPanel44.setLayout(jPanel44Layout);
        jPanel44Layout.setHorizontalGroup(
            jPanel44Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 513, Short.MAX_VALUE)
        );
        jPanel44Layout.setVerticalGroup(
            jPanel44Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 266, Short.MAX_VALUE)
        );

        jPanel45.setBorder(javax.swing.BorderFactory.createTitledBorder("Island 4"));

        javax.swing.GroupLayout jPanel45Layout = new javax.swing.GroupLayout(jPanel45);
        jPanel45.setLayout(jPanel45Layout);
        jPanel45Layout.setHorizontalGroup(
            jPanel45Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 474, Short.MAX_VALUE)
        );
        jPanel45Layout.setVerticalGroup(
            jPanel45Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 266, Short.MAX_VALUE)
        );

        jPanel46.setBorder(javax.swing.BorderFactory.createTitledBorder("Island 1"));

        javax.swing.GroupLayout jPanel46Layout = new javax.swing.GroupLayout(jPanel46);
        jPanel46.setLayout(jPanel46Layout);
        jPanel46Layout.setHorizontalGroup(
            jPanel46Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 513, Short.MAX_VALUE)
        );
        jPanel46Layout.setVerticalGroup(
            jPanel46Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 280, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel41Layout = new javax.swing.GroupLayout(jPanel41);
        jPanel41.setLayout(jPanel41Layout);
        jPanel41Layout.setHorizontalGroup(
            jPanel41Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel41Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel41Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel46, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel44, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel41Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel42, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel45, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel41Layout.setVerticalGroup(
            jPanel41Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel41Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel41Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel42, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel46, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel41Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel45, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel44, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane3.addTab("Fitness", jPanel41);

        jPanel39.setBorder(javax.swing.BorderFactory.createTitledBorder("Island 3"));

        javax.swing.GroupLayout jPanel39Layout = new javax.swing.GroupLayout(jPanel39);
        jPanel39.setLayout(jPanel39Layout);
        jPanel39Layout.setHorizontalGroup(
            jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 493, Short.MAX_VALUE)
        );
        jPanel39Layout.setVerticalGroup(
            jPanel39Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 273, Short.MAX_VALUE)
        );

        jPanel48.setBorder(javax.swing.BorderFactory.createTitledBorder("Island 1"));

        javax.swing.GroupLayout jPanel48Layout = new javax.swing.GroupLayout(jPanel48);
        jPanel48.setLayout(jPanel48Layout);
        jPanel48Layout.setHorizontalGroup(
            jPanel48Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 493, Short.MAX_VALUE)
        );
        jPanel48Layout.setVerticalGroup(
            jPanel48Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 273, Short.MAX_VALUE)
        );

        jPanel49.setBorder(javax.swing.BorderFactory.createTitledBorder("Island 4"));

        javax.swing.GroupLayout jPanel49Layout = new javax.swing.GroupLayout(jPanel49);
        jPanel49.setLayout(jPanel49Layout);
        jPanel49Layout.setHorizontalGroup(
            jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 494, Short.MAX_VALUE)
        );
        jPanel49Layout.setVerticalGroup(
            jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 273, Short.MAX_VALUE)
        );

        jPanel50.setBorder(javax.swing.BorderFactory.createTitledBorder("Island 2"));

        javax.swing.GroupLayout jPanel50Layout = new javax.swing.GroupLayout(jPanel50);
        jPanel50.setLayout(jPanel50Layout);
        jPanel50Layout.setHorizontalGroup(
            jPanel50Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 494, Short.MAX_VALUE)
        );
        jPanel50Layout.setVerticalGroup(
            jPanel50Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 273, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel39, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel48, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel49, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel50, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel50, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel48, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel39, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel49, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane3.addTab("Residuals", jPanel13);

        jPanel52.setLayout(new java.awt.GridBagLayout());

        jScrollPane5.setBorder(javax.swing.BorderFactory.createTitledBorder("Island 1"));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane5.setViewportView(jTable1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 366;
        gridBagConstraints.ipady = 228;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 10, 0, 0);
        jPanel52.add(jScrollPane5, gridBagConstraints);

        jScrollPane6.setBorder(javax.swing.BorderFactory.createTitledBorder("Island 2"));

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable2.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane6.setViewportView(jTable2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 394;
        gridBagConstraints.ipady = 228;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(11, 6, 0, 10);
        jPanel52.add(jScrollPane6, gridBagConstraints);

        jScrollPane7.setBorder(javax.swing.BorderFactory.createTitledBorder("Island 3"));

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable3.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane7.setViewportView(jTable3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 366;
        gridBagConstraints.ipady = 243;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 10, 11, 0);
        jPanel52.add(jScrollPane7, gridBagConstraints);

        jScrollPane8.setBorder(javax.swing.BorderFactory.createTitledBorder("Island 4"));

        jTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable4.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane8.setViewportView(jTable4);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 394;
        gridBagConstraints.ipady = 243;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 11, 10);
        jPanel52.add(jScrollPane8, gridBagConstraints);

        jTabbedPane3.addTab("Best Individuals", jPanel52);

        jTabbedPane4.addTab("Convergence Graphs", jTabbedPane3);

        jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder("Log"));
        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane3.setAutoscrolls(true);

        logTextArea.setColumns(20);
        logTextArea.setEditable(false);
        logTextArea.setRows(5);
        logTextArea.setWrapStyleWord(true);
        logTextArea.setBorder(null);
        DefaultCaret caret2 = (DefaultCaret)logTextArea.getCaret();
        caret2.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        jScrollPane3.setViewportView(logTextArea);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1042, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel6Layout.createSequentialGroup()
                    .addGap(5, 5, 5)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1032, Short.MAX_VALUE)
                    .addGap(5, 5, 5)))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 656, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE))
        );

        jTabbedPane4.addTab("Eve Log", jPanel6);

        jTabbedPane2.addTab("Eve", jTabbedPane4);

        jPanel4.setBackground(new java.awt.Color(255, 0, 0));

        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("<html><center><font size=+8>Il tuo computer &egrave; andato a donnine?!?</font><br><br><br>Se sei qui per questo e la schermata &egrave; verde allora sappi che Eve &egrave; gi&agrave; stato fermato.<br>Puoi provare con una popolazione pi&ugrave; piccola<br><br><br><br><br></center></html>");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, 1027, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, 662, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Emergency STOP", jPanel4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1052, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 712, Short.MAX_VALUE)
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void samplesFileTextFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_samplesFileTextFieldActionPerformed
    {//GEN-HEADEREND:event_samplesFileTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_samplesFileTextFieldActionPerformed

    private void autogenerateNamesCheckBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_autogenerateNamesCheckBoxActionPerformed
    {//GEN-HEADEREND:event_autogenerateNamesCheckBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_autogenerateNamesCheckBoxActionPerformed

    private void distanceTypeComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_distanceTypeComboBoxActionPerformed
    {//GEN-HEADEREND:event_distanceTypeComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_distanceTypeComboBoxActionPerformed

    private void samplesFileSelectButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_samplesFileSelectButtonActionPerformed
    {//GEN-HEADEREND:event_samplesFileSelectButtonActionPerformed
        JFileChooser jFileChooser = new JFileChooser();
        if( jFileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION )
        {
            File file = jFileChooser.getSelectedFile();
            samplesFileTextField.setText( file.getAbsolutePath() );
            experimentNameTextField.setText( file.getName() );
        }
    }//GEN-LAST:event_samplesFileSelectButtonActionPerformed

    private void experimentNameTextFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_experimentNameTextFieldActionPerformed
    {//GEN-HEADEREND:event_experimentNameTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_experimentNameTextFieldActionPerformed

    private void loadFilesButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_loadFilesButtonActionPerformed
    {//GEN-HEADEREND:event_loadFilesButtonActionPerformed
        workingStatusLabel.setText("LOADING");
        workingStatusPanel.setBackground(Color.cyan);
        stateLabel.setText("Loading");
        new Thread( new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Matrix samplesMatrix = new Matrix(new File( samplesFileTextField.getText() ));

                    if( transposeCheckBox.isSelected() ) samplesMatrix = samplesMatrix.transpose();

                    String[] names;
                    if( !autogenerateNamesCheckBox.isSelected() )
                        names = Experiment.readNamesFromFile( new File( namesFileTextField.getText() ));
                    else 
                    {
                        names = new String[samplesMatrix.nRows()];
                        for(int i=0;  i<names.length; i++) names[i] = "Gene" + i;
                    }
                    if( names.length != samplesMatrix.nRows() ) throw new IllegalArgumentException("nomi e campioni non compatibili" + names.length  + " " + samplesMatrix.nRows());

                    Profile[] profiles = new Profile[samplesMatrix.nRows()];
                    for(int i=0; i<samplesMatrix.nRows(); i++)
                    {
                        profiles[i] = new Profile(names[i], samplesMatrix.getRow(i), (Double)samplingTimeSpinner.getValue());
                        if( !disableTimeUnitCheckBox.isSelected() )
                            profiles[i].setTimeUnit(timeUnitTextField.getText());
                    }

                    firstExperiment = new Experiment( experimentNameTextField.getText() , profiles);
                    lastExperiment = firstExperiment;

                    stateLabel.setText("Loaded");
                    workingStatusLabel.setText("LOADED");
                    workingStatusPanel.setBackground(Color.GREEN);

                    refreshStateInfos(lastExperiment);

                    experimentPanel.setEnabled(false);
                    preElaborationPanel.setEnabled(true);
                    operationsPanel.setEnabled(true);
                    messageTextArea.append(new Date() + ": Experiment Loaded.\n");
                }
                catch(Throwable e)
                {
                    stateLabel.setText("Error While Loading");
                    workingStatusLabel.setText("ERROR");
                    workingStatusPanel.setBackground(Color.red);
                    Logger.getLogger(EveGui.class.getName()).log(Level.SEVERE, null, e);
                    messageTextArea.append(e.toString());
                    messageTextArea.append("\n" + new Date() + ": Error while loading Experiment.\n");
                }
            }
        }).start();
    }//GEN-LAST:event_loadFilesButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton1ActionPerformed
    {//GEN-HEADEREND:event_jButton1ActionPerformed
        JFileChooser jFileChooser = new JFileChooser();
        if( jFileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION )
        {
            namesFileTextField.setText( jFileChooser.getSelectedFile().getAbsolutePath() );
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void goSelectionButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_goSelectionButtonActionPerformed
    {//GEN-HEADEREND:event_goSelectionButtonActionPerformed
        selectionProgressBar.setIndeterminate(true);
        
        workingStatusLabel.setText("WORKING");
        workingStatusPanel.setBackground(Color.cyan);
        stateLabel.setText("Working");
        
        new Thread( new Runnable()
        {
            @Override
            public void run()
            {
                Experiment computedExperiment;
                try
                {
                    computedExperiment = lastExperiment.selectByMinVariance( (Double)minVarianceSelectionSpinner.getValue() );
                    refreshStateInfos(computedExperiment);
                    undoExperiment = lastExperiment;
                    lastExperiment = computedExperiment;

                    selectionProgressBar.setIndeterminate(false);
                    selectionProgressBar.setValue(100);
                    stateLabel.setText("Computed");
                    workingStatusLabel.setText("DONE");
                    workingStatusPanel.setBackground(Color.GREEN);
                    messageTextArea.append(new Date() + ": Selection performed.\n");
                }
                catch(Throwable e)
                {
                    selectionProgressBar.setIndeterminate(false);
                    selectionProgressBar.setValue(50);
                    workingStatusLabel.setText("ERROR");
                    workingStatusPanel.setBackground(Color.red);
                    Logger.getLogger(EveGui.class.getName()).log(Level.SEVERE, null, e);
                    messageTextArea.append(e.toString());
                    messageTextArea.append("\n" + new Date() + ": Error: selection not performed.\n");
                }
            }
        }).start();
    }//GEN-LAST:event_goSelectionButtonActionPerformed

    private void goClusteringButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_goClusteringButtonActionPerformed
    {//GEN-HEADEREND:event_goClusteringButtonActionPerformed
        clusteringProgressBar.setIndeterminate(true);
        workingStatusLabel.setText("WORKING");
        workingStatusPanel.setBackground(Color.cyan);
        stateLabel.setText("Working");
        
        new Thread( new Runnable(){
            @Override
            public void run()
            {
                Experiment computedExperiment;
                try
                {
                    String distanceType = (String)distanceTypeComboBox.getSelectedItem();
                         if( distanceType.equals("TotalError") )
                        lastExperiment.setCluDistType(Matrix.DistanceType.TotalError);
                    else if( distanceType.equals("MaximumError") )
                        lastExperiment.setCluDistType(Matrix.DistanceType.MaximumError);
                    else if( distanceType.equals("MeanError") )
                        lastExperiment.setCluDistType(Matrix.DistanceType.MeanError);
                    else if( distanceType.equals("MeanSquareError") )
                        lastExperiment.setCluDistType(Matrix.DistanceType.MeanSquareError);
                    else if( distanceType.equals("RootMeanSquareError") )
                        lastExperiment.setCluDistType(Matrix.DistanceType.RootMeanSquareError);

                    computedExperiment = lastExperiment.clusterize( (Float)clusteringRadiusSpinner.getValue() );
                    refreshStateInfos(computedExperiment);           
                    undoExperiment = lastExperiment;
                    lastExperiment = computedExperiment;

                    clusteringProgressBar.setIndeterminate(false);
                    clusteringProgressBar.setValue(100);

                    stateLabel.setText("Computed");
                    workingStatusLabel.setText("DONE");
                    workingStatusPanel.setBackground(Color.GREEN);
                    messageTextArea.append(new Date() + ": Clustering performed.\n");
                }
                catch(Throwable e)
                {
                    clusteringProgressBar.setIndeterminate(false);
                    clusteringProgressBar.setValue(50);
                    workingStatusLabel.setText("ERROR");
                    workingStatusPanel.setBackground(Color.red);
                    Logger.getLogger(EveGui.class.getName()).log(Level.SEVERE, null, e);
                    messageTextArea.append(e.toString());
                    messageTextArea.append("\n" + new Date() + ": Error: clustering not performed.\n");
                } 
            }
        }).start();
        
    }//GEN-LAST:event_goClusteringButtonActionPerformed

    private void goInterpolateButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_goInterpolateButtonActionPerformed
    {//GEN-HEADEREND:event_goInterpolateButtonActionPerformed
        interpolateProgressBar.setIndeterminate(true);
        
        workingStatusLabel.setText("WORKING");
        workingStatusPanel.setBackground(Color.cyan);
        stateLabel.setText("Working");
        
        new Thread( new Runnable()
        {
            @Override
            public void run()
            {
                Experiment computedExperiment;
                try
                {
                    computedExperiment = lastExperiment.interpolate( (Integer)newNumberOfSamplesInterpolateSpinner.getValue() );
                    refreshStateInfos(computedExperiment);
                    undoExperiment = lastExperiment;
                    lastExperiment = computedExperiment;

                    interpolateProgressBar.setIndeterminate(false);
                    interpolateProgressBar.setValue(100);

                    stateLabel.setText("Computed");
                    workingStatusLabel.setText("DONE");
                    workingStatusPanel.setBackground(Color.GREEN);
                    messageTextArea.append(new Date() + ": Interpolation performed.\n");
                }
                catch(Throwable e)
                {
                    interpolateProgressBar.setIndeterminate(false);
                    interpolateProgressBar.setValue(50);
                    workingStatusLabel.setText("ERROR");
                    workingStatusPanel.setBackground(Color.red);
                    Logger.getLogger(EveGui.class.getName()).log(Level.SEVERE, null, e);
                    messageTextArea.append(e.toString());
                    messageTextArea.append("\n" + new Date() + ": Error: interpolation not performed.\n");
                }
            }
        }).start();
    }//GEN-LAST:event_goInterpolateButtonActionPerformed

    private void revoverFirstButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_revoverFirstButtonActionPerformed
    {//GEN-HEADEREND:event_revoverFirstButtonActionPerformed
        lastExperiment = firstExperiment;
        refreshStateInfos(lastExperiment);
        messageTextArea.append("Recovered Experiment from file.");
    }//GEN-LAST:event_revoverFirstButtonActionPerformed

    private void discardLastButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_discardLastButtonActionPerformed
    {//GEN-HEADEREND:event_discardLastButtonActionPerformed
        lastExperiment = undoExperiment;
        refreshStateInfos(lastExperiment);
        messageTextArea.append("Undo last operation.");
    }//GEN-LAST:event_discardLastButtonActionPerformed

    private void lockExperimentButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_lockExperimentButtonActionPerformed
    {//GEN-HEADEREND:event_lockExperimentButtonActionPerformed
        workingStatusPanel.setBackground(Color.GRAY);
        workingStatusLabel.setText("LOCKED");
        
        operationsPanel.setEnabled(false);
        preElaborationPanel.setEnabled(false);
        
        lockedPanel.setBackground(Color.blue);
        lockedLabel.setForeground(Color.white);
        lockedLabel.setText("LOCKED");
        messageTextArea.append("\nExperiment LOCKED.");
        messageTextArea.setEnabled(false);
        
        lockedExperiment = lastExperiment;
        jButton4.setEnabled(true);
    }//GEN-LAST:event_lockExperimentButtonActionPerformed

    private void saveToDiskButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_saveToDiskButtonActionPerformed
    {//GEN-HEADEREND:event_saveToDiskButtonActionPerformed
        // TODO add your handling code here:
        
        //nel primo file salviamo i nomi dell'Experiment
        JFileChooser jFileChooser = new JFileChooser();
        if( jFileChooser.showDialog(this, "Save Names File") == JFileChooser.APPROVE_OPTION )
        {
            try
            {
                File file = jFileChooser.getSelectedFile();
                Experiment.writeNamesToFile(lastExperiment.getProfilesNames(), file);
                messageTextArea.append(new Date() + ": Names file saved successfully.\n");
            } catch(IOException e)
            {
                Logger.getLogger(EveGui.class.getName()).log(Level.SEVERE, null, e);
                messageTextArea.append(e.toString());
                messageTextArea.append("\n" + new Date() + ": Error: names file salvation didn't work.\n");
            }
        }
        
        //nel secondo salviamo i campioni dell'experiment
        jFileChooser = new JFileChooser();
        if( jFileChooser.showDialog(this, "Save Samples File") == JFileChooser.APPROVE_OPTION )
        {
            try
            {
                File file = jFileChooser.getSelectedFile();
                //Experiment.writeNamesToFile(lastExperiment.getProfilesNames(), file);
                lastExperiment.toMatrix().toFile(file);
                messageTextArea.append(new Date() + ": Samples file saved successfully.\n");
            } catch(IOException e)
            {
                Logger.getLogger(EveGui.class.getName()).log(Level.SEVERE, null, e);
                messageTextArea.append(e.toString());
                messageTextArea.append("\n" + new Date() + ": Error: samples file salvation didn't work.\n");
            }
        }
        
    }//GEN-LAST:event_saveToDiskButtonActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton2ActionPerformed
    {//GEN-HEADEREND:event_jButton2ActionPerformed
        
        double[][] array = lastExperiment.toMatrix().getBackingArray();
        Object[][] arrayObject = new Object[array.length][array[0].length + 1];
        String[] profileNames = lastExperiment.getProfilesNames();
        for(int i=0; i<array.length; i++)
            arrayObject[i][0] = profileNames[i];

        for(int i=0; i<array.length; i++)
            for(int j=0; j<array[0].length; j++)
                arrayObject[i][j+1] = array[i][j];

        String[] columnsHeaders = new String[lastExperiment.getSamplesNumber() + 1];
        columnsHeaders[0] = "Name";
        for(int i=1; i<columnsHeaders.length; i++)
            columnsHeaders[i] = Double.toString(i * lastExperiment.getSamplingTime()) + timeUnitTextField.getText();
        DefaultTableModel tableModel = new DefaultTableModel(arrayObject, columnsHeaders);
        //        tableModel.addColumn("Names", lastExperiment.getProfilesNames());
        jTable.setModel(tableModel);
            
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jCheckBox18ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jCheckBox18ActionPerformed
    {//GEN-HEADEREND:event_jCheckBox18ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox18ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton4ActionPerformed
    {//GEN-HEADEREND:event_jButton4ActionPerformed
        // TODO add your handling code here:
        
        jButton4.setEnabled(false);
        jPanel8.setBackground(Color.CYAN);
        jLabel4.setText("CREATING");
        
        
        new Thread( new Runnable()
        {
            @Override
            public void run()
            {
                logTextArea.append(new Date() + ": Creating the world...\n");
                try
                {
                    world = new EvolutiveWorld();
                    
                    if( !jCheckBox1.isSelected() ) //ogni isola determina le proprie impostazioni
                    {
                        if( jCheckBox18.isSelected() ) //isola 1
                        {
                            Parameters parameters = new EvolutiveIsland.Parameters();
                            parameters.threadsNumber = (Integer)jSpinner1.getValue();
                            parameters.islandSize = (Integer)jSpinner40.getValue();
                            parameters.generationSize = (Integer)jSpinner41.getValue();
                            parameters.maxWorstFitness = (Integer)jSpinner42.getValue();
                            parameters.migrationStrategy = jCheckBox17.isSelected();
                            parameters.emigratesNumber = (Integer)jSpinner39.getValue();
                            parameters.migrationEventProbability = (Double)jSpinner38.getValue();
                            parameters.disasterStrategy = jCheckBox19.isSelected();
                            parameters.disasterPeriod = (Integer)jSpinner43.getValue();
                            parameters.disasterSurvivorsNumber = (Integer)jSpinner44.getValue();
                            String fitnessType = (String)jComboBox6.getSelectedItem();
                                 
                            Matrix.MatrixDistanceCalculator fitnessCalculator;
                                 if( fitnessType.equals("TotE") )
                                fitnessCalculator = Matrix.totalErrorMatrixDistanceCalculator;
                            else if( fitnessType.equals("MeanE") )
                                fitnessCalculator = Matrix.meanErrorMatrixDistanceCalculator;
                            else if( fitnessType.equals("MaxE") )
                                fitnessCalculator = Matrix.maximumErrorMatrixDistanceCalculator;
                            else if( fitnessType.equals("MSE") )
                                fitnessCalculator = Matrix.meanSquareErrorMatrixDistanceCalculator;
                            else if( fitnessType.equals("RMSE") )
                                fitnessCalculator = Matrix.rootMeanSquareErrorMatrixDistanceCalculator;
                            else if( fitnessType.equals("MixedE") )
                                fitnessCalculator = mixedErrorMatrixDistanceCalculator;
                            else throw new IllegalStateException("cannot select fitness type");
                            fitnessTypes[0] = fitnessCalculator.toString();
                            
                            world.addIsland( islands[0] = new EvolutiveIsland( new Environment(lockedExperiment, fitnessCalculator), parameters ));
                        }
                        if( jCheckBox27.isSelected() ) //isola 2
                        {
                            Parameters parameters = new EvolutiveIsland.Parameters();
                            parameters.threadsNumber = (Integer)jSpinner4.getValue();
                            parameters.islandSize = (Integer)jSpinner61.getValue();
                            parameters.generationSize = (Integer)jSpinner62.getValue();
                            parameters.maxWorstFitness = (Integer)jSpinner63.getValue();
                            parameters.migrationStrategy = jCheckBox26.isSelected();
                            parameters.emigratesNumber = (Integer)jSpinner60.getValue();
                            parameters.migrationEventProbability = (Double)jSpinner59.getValue();
                            parameters.disasterStrategy = jCheckBox28.isSelected();
                            parameters.disasterPeriod = (Integer)jSpinner64.getValue();
                            parameters.disasterSurvivorsNumber = (Integer)jSpinner65.getValue();
                            String fitnessType = (String)jComboBox9.getSelectedItem();
                            
                            Matrix.MatrixDistanceCalculator fitnessCalculator;
                                 if( fitnessType.equals("TotE") )
                                fitnessCalculator = Matrix.totalErrorMatrixDistanceCalculator;
                            else if( fitnessType.equals("MeanE") )
                                fitnessCalculator = Matrix.meanErrorMatrixDistanceCalculator;
                            else if( fitnessType.equals("MaxE") )
                                fitnessCalculator = Matrix.maximumErrorMatrixDistanceCalculator;
                            else if( fitnessType.equals("MSE") )
                                fitnessCalculator = Matrix.meanSquareErrorMatrixDistanceCalculator;
                            else if( fitnessType.equals("RMSE") )
                                fitnessCalculator = Matrix.rootMeanSquareErrorMatrixDistanceCalculator;
                            else if( fitnessType.equals("MixedE") )
                                fitnessCalculator = mixedErrorMatrixDistanceCalculator;
                            else throw new IllegalStateException("cannot select fitness type");
                            fitnessTypes[1] = fitnessCalculator.toString();
                            
                            world.addIsland( islands[1] = new EvolutiveIsland( new Environment(lockedExperiment, fitnessCalculator), parameters ));
                        }
                        if( jCheckBox21.isSelected() ) //isola 3
                        {
                            Parameters parameters = new EvolutiveIsland.Parameters();
                            parameters.threadsNumber = (Integer)jSpinner2.getValue();
                            parameters.islandSize = (Integer)jSpinner47.getValue();
                            parameters.generationSize = (Integer)jSpinner48.getValue();
                            parameters.maxWorstFitness = (Integer)jSpinner49.getValue();
                            parameters.migrationStrategy = jCheckBox20.isSelected();
                            parameters.emigratesNumber = (Integer)jSpinner46.getValue();
                            parameters.migrationEventProbability = (Double)jSpinner45.getValue();
                            parameters.disasterStrategy = jCheckBox22.isSelected();
                            parameters.disasterPeriod = (Integer)jSpinner50.getValue();
                            parameters.disasterSurvivorsNumber = (Integer)jSpinner51.getValue();
                            String fitnessType = (String)jComboBox7.getSelectedItem();
                            
                            Matrix.MatrixDistanceCalculator fitnessCalculator;
                                 if( fitnessType.equals("TotE") )
                                fitnessCalculator = Matrix.totalErrorMatrixDistanceCalculator;
                            else if( fitnessType.equals("MeanE") )
                                fitnessCalculator = Matrix.meanErrorMatrixDistanceCalculator;
                            else if( fitnessType.equals("MaxE") )
                                fitnessCalculator = Matrix.maximumErrorMatrixDistanceCalculator;
                            else if( fitnessType.equals("MSE") )
                                fitnessCalculator = Matrix.meanSquareErrorMatrixDistanceCalculator;
                            else if( fitnessType.equals("RMSE") )
                                fitnessCalculator = Matrix.rootMeanSquareErrorMatrixDistanceCalculator;
                            else if( fitnessType.equals("MixedE") )
                                fitnessCalculator = mixedErrorMatrixDistanceCalculator;
                            else throw new IllegalStateException("cannot select fitness type");
                            fitnessTypes[2] = fitnessCalculator.toString();
                            
                            world.addIsland( islands[2] = new EvolutiveIsland( new Environment(lockedExperiment, fitnessCalculator), parameters ));
                        }
                        if( jCheckBox24.isSelected() ) //isola 4
                        {
                            Parameters parameters = new EvolutiveIsland.Parameters();
                            parameters.threadsNumber = (Integer)jSpinner3.getValue();
                            parameters.islandSize = (Integer)jSpinner54.getValue();
                            parameters.generationSize = (Integer)jSpinner55.getValue();
                            parameters.maxWorstFitness = (Integer)jSpinner56.getValue();
                            parameters.migrationStrategy = jCheckBox23.isSelected();
                            parameters.emigratesNumber = (Integer)jSpinner53.getValue();
                            parameters.migrationEventProbability = (Double)jSpinner52.getValue();
                            parameters.disasterStrategy = jCheckBox25.isSelected();
                            parameters.disasterPeriod = (Integer)jSpinner57.getValue();
                            parameters.disasterSurvivorsNumber = (Integer)jSpinner58.getValue();
                            String fitnessType = (String)jComboBox8.getSelectedItem();
                            
                            Matrix.MatrixDistanceCalculator fitnessCalculator;
                                 if( fitnessType.equals("TotE") )
                                fitnessCalculator = Matrix.totalErrorMatrixDistanceCalculator;
                            else if( fitnessType.equals("MeanE") )
                                fitnessCalculator = Matrix.meanErrorMatrixDistanceCalculator;
                            else if( fitnessType.equals("MaxE") )
                                fitnessCalculator = Matrix.maximumErrorMatrixDistanceCalculator;
                            else if( fitnessType.equals("MSE") )
                                fitnessCalculator = Matrix.meanSquareErrorMatrixDistanceCalculator;
                            else if( fitnessType.equals("RMSE") )
                                fitnessCalculator = Matrix.rootMeanSquareErrorMatrixDistanceCalculator;
                            else if( fitnessType.equals("MixedE") )
                                fitnessCalculator = mixedErrorMatrixDistanceCalculator;
                            else throw new IllegalStateException("cannot select fitness type");
                            fitnessTypes[3] = fitnessCalculator.toString();
                            
                            world.addIsland( islands[3] = new EvolutiveIsland( new Environment(lockedExperiment, fitnessCalculator), parameters ));
                        }
                        //per il caso delle isole autodeterminanti ? tutto pronto per il go()
                    }   
                    else if( jCheckBox1.isSelected() && !jCheckBox2.isSelected() ) //impostazioni dell'isola 1 per tutte le altre
                    {
                        {
                            Parameters parameters = new EvolutiveIsland.Parameters();
                            parameters.threadsNumber = (Integer)jSpinner1.getValue();
                            parameters.islandSize = (Integer)jSpinner40.getValue();
                            parameters.generationSize = (Integer)jSpinner41.getValue();
                            parameters.maxWorstFitness = (Integer)jSpinner42.getValue();
                            parameters.migrationStrategy = jCheckBox17.isSelected();
                            parameters.emigratesNumber = (Integer)jSpinner39.getValue();
                            parameters.migrationEventProbability = (Double)jSpinner38.getValue();
                            parameters.disasterStrategy = jCheckBox19.isSelected();
                            parameters.disasterPeriod = (Integer)jSpinner43.getValue();
                            parameters.disasterSurvivorsNumber = (Integer)jSpinner44.getValue();
                            String fitnessType = (String)jComboBox6.getSelectedItem();
                                 
                            Matrix.MatrixDistanceCalculator fitnessCalculator;
                                 if( fitnessType.equals("TotE") )
                                fitnessCalculator = Matrix.totalErrorMatrixDistanceCalculator;
                            else if( fitnessType.equals("MeanE") )
                                fitnessCalculator = Matrix.meanErrorMatrixDistanceCalculator;
                            else if( fitnessType.equals("MaxE") )
                                fitnessCalculator = Matrix.maximumErrorMatrixDistanceCalculator;
                            else if( fitnessType.equals("MSE") )
                                fitnessCalculator = Matrix.meanSquareErrorMatrixDistanceCalculator;
                            else if( fitnessType.equals("RMSE") )
                                fitnessCalculator = Matrix.rootMeanSquareErrorMatrixDistanceCalculator;
                            else if( fitnessType.equals("MixedE") )
                                fitnessCalculator = mixedErrorMatrixDistanceCalculator;
                            else throw new IllegalStateException("cannot select fitness type");
                            for(int i=0; i<4; i++) fitnessTypes[i] = fitnessCalculator.toString();
                            
                            if( jCheckBox18.isSelected() ) world.addIsland( islands[0] = new EvolutiveIsland( new Environment(lockedExperiment, fitnessCalculator), parameters ));
                            if( jCheckBox27.isSelected() ) world.addIsland( islands[1] = new EvolutiveIsland( new Environment(lockedExperiment, fitnessCalculator), parameters ));
                            if( jCheckBox21.isSelected() ) world.addIsland( islands[2] = new EvolutiveIsland( new Environment(lockedExperiment, fitnessCalculator), parameters ));
                            if( jCheckBox24.isSelected() ) world.addIsland( islands[3] = new EvolutiveIsland( new Environment(lockedExperiment, fitnessCalculator), parameters ));
                        }
                        //per il caso delle isole tutte uguali, anche nella fitness ? tutto pronto per il go()
                    }
                    else if( jCheckBox1.isSelected() && jCheckBox2.isSelected()) //isole uguali alla 1, ma non per la fitness
                    {
                        {
                            Parameters parameters = new EvolutiveIsland.Parameters(); //i parametri valgono per tutte le isole
                            parameters.threadsNumber = (Integer)jSpinner1.getValue();
                            parameters.islandSize = (Integer)jSpinner40.getValue();
                            parameters.generationSize = (Integer)jSpinner41.getValue();
                            parameters.maxWorstFitness = (Integer)jSpinner42.getValue();
                            parameters.migrationStrategy = jCheckBox17.isSelected();
                            parameters.emigratesNumber = (Integer)jSpinner39.getValue();
                            parameters.migrationEventProbability = (Double)jSpinner38.getValue();
                            parameters.disasterStrategy = jCheckBox19.isSelected();
                            parameters.disasterPeriod = (Integer)jSpinner43.getValue();
                            parameters.disasterSurvivorsNumber = (Integer)jSpinner44.getValue();
                            
                            if( jCheckBox18.isSelected() )
                            {
                                String fitnessType = (String)jComboBox6.getSelectedItem();
                                 
                                Matrix.MatrixDistanceCalculator fitnessCalculator;
                                     if( fitnessType.equals("TotE") )
                                    fitnessCalculator = Matrix.totalErrorMatrixDistanceCalculator;
                                else if( fitnessType.equals("MeanE") )
                                    fitnessCalculator = Matrix.meanErrorMatrixDistanceCalculator;
                                else if( fitnessType.equals("MaxE") )
                                    fitnessCalculator = Matrix.maximumErrorMatrixDistanceCalculator;
                                else if( fitnessType.equals("MSE") )
                                    fitnessCalculator = Matrix.meanSquareErrorMatrixDistanceCalculator;
                                else if( fitnessType.equals("RMSE") )
                                    fitnessCalculator = Matrix.rootMeanSquareErrorMatrixDistanceCalculator;
                                else if( fitnessType.equals("MixedE") )
                                    fitnessCalculator = mixedErrorMatrixDistanceCalculator;
                                else throw new IllegalStateException("cannot select fitness type");
                                fitnessTypes[0] = fitnessCalculator.toString();
                                
                                world.addIsland( islands[0] = new EvolutiveIsland( new Environment(lockedExperiment, fitnessCalculator), parameters ));
                            }
                            if( jCheckBox27.isSelected() )
                            {
                                String fitnessType = (String)jComboBox9.getSelectedItem();
                                 
                                Matrix.MatrixDistanceCalculator fitnessCalculator;
                                     if( fitnessType.equals("TotE") )
                                    fitnessCalculator = Matrix.totalErrorMatrixDistanceCalculator;
                                else if( fitnessType.equals("MeanE") )
                                    fitnessCalculator = Matrix.meanErrorMatrixDistanceCalculator;
                                else if( fitnessType.equals("MaxE") )
                                    fitnessCalculator = Matrix.maximumErrorMatrixDistanceCalculator;
                                else if( fitnessType.equals("MSE") )
                                    fitnessCalculator = Matrix.meanSquareErrorMatrixDistanceCalculator;
                                else if( fitnessType.equals("RMSE") )
                                    fitnessCalculator = Matrix.rootMeanSquareErrorMatrixDistanceCalculator;
                                else if( fitnessType.equals("MixedE") )
                                    fitnessCalculator = mixedErrorMatrixDistanceCalculator;
                                else throw new IllegalStateException("cannot select fitness type");
                                fitnessTypes[1] = fitnessCalculator.toString();
                               
                                world.addIsland( islands[1] = new EvolutiveIsland( new Environment(lockedExperiment, fitnessCalculator), parameters ));
                            }
                            if( jCheckBox21.isSelected() )
                            {
                                String fitnessType = (String)jComboBox7.getSelectedItem();
                                 
                                Matrix.MatrixDistanceCalculator fitnessCalculator;
                                     if( fitnessType.equals("TotE") )
                                    fitnessCalculator = Matrix.totalErrorMatrixDistanceCalculator;
                                else if( fitnessType.equals("MeanE") )
                                    fitnessCalculator = Matrix.meanErrorMatrixDistanceCalculator;
                                else if( fitnessType.equals("MaxE") )
                                    fitnessCalculator = Matrix.maximumErrorMatrixDistanceCalculator;
                                else if( fitnessType.equals("MSE") )
                                    fitnessCalculator = Matrix.meanSquareErrorMatrixDistanceCalculator;
                                else if( fitnessType.equals("RMSE") )
                                    fitnessCalculator = Matrix.rootMeanSquareErrorMatrixDistanceCalculator;
                                else if( fitnessType.equals("MixedE") )
                                    fitnessCalculator = mixedErrorMatrixDistanceCalculator;
                                else throw new IllegalStateException("cannot select fitness type");
                                fitnessTypes[2] = fitnessCalculator.toString();
                                     
                                world.addIsland( islands[2] = new EvolutiveIsland( new Environment(lockedExperiment, fitnessCalculator), parameters ));
                            }
                            if( jCheckBox24.isSelected() )
                            {
                                String fitnessType = (String)jComboBox8.getSelectedItem();
                                 
                                Matrix.MatrixDistanceCalculator fitnessCalculator;
                                     if( fitnessType.equals("TotE") )
                                    fitnessCalculator = Matrix.totalErrorMatrixDistanceCalculator;
                                else if( fitnessType.equals("MeanE") )
                                    fitnessCalculator = Matrix.meanErrorMatrixDistanceCalculator;
                                else if( fitnessType.equals("MaxE") )
                                    fitnessCalculator = Matrix.maximumErrorMatrixDistanceCalculator;
                                else if( fitnessType.equals("MSE") )
                                    fitnessCalculator = Matrix.meanSquareErrorMatrixDistanceCalculator;
                                else if( fitnessType.equals("RMSE") )
                                    fitnessCalculator = Matrix.rootMeanSquareErrorMatrixDistanceCalculator;
                                else if( fitnessType.equals("MixedE") )
                                    fitnessCalculator = mixedErrorMatrixDistanceCalculator;
                                else throw new IllegalStateException("cannot select fitness type");
                                fitnessTypes[3] = fitnessCalculator.toString();
                                
                                world.addIsland( islands[3] = new EvolutiveIsland( new Environment(lockedExperiment, fitnessCalculator), parameters ));
                            }
                        }
                        //anche per il caso delle isole uguali meno che per la fitness, ? pronto per il go()
                    }
                    else throw new IllegalStateException("something went wrong");
                    
                    world.go();
                    
                    int isN = 0;
                    for(int i=0; i<4; i++)
                        if( islands[i] != null)
                        {
                            isN++;
                            islandRefreshers[i] = new IslandStatisticsRefresher
                                                        (
                                                            islands[i],
                                                            islandLabels[i],
                                                            islandStatusPanels[i],
                                                            jSlider1.getValue(),
                                                            charters[i],
                                                            fitnessTypes[i],
                                                            spectrumCharts[i],
                                                            solutionTables[i]
                                                        );
                        }
                    
                    worldRefresher = new WorldStatisticsRefresher(world, worldLabels, jSlider1.getValue());
                    
                    jPanel8.setBackground(Color.GREEN);
                    jLabel4.setText("RUNNING ;-)");
                    jButton3.setEnabled(true);
                    jButton6.setEnabled(true);
                    logTextArea.append(new Date() + ": " + isN + " islands were created.\n");
                    logTextArea.append(new Date() + ": Evolution started... good luck new world ;-)\n");
                    jPanel4.setBackground(Color.RED);
                }
                catch(Throwable t)
                {
                    Logger.getLogger(EveGui.class.getName()).log(Level.SEVERE, null, t);
                    logTextArea.append(t.toString());
                    logTextArea.append("\n" + new Date() + ": Error while creating the world.\n");
                    jPanel8.setBackground(Color.RED);
                    jLabel4.setText("ERROR");
                    jButton4.setEnabled(true);
                }
            }
        }).start();
        
        
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton3ActionPerformed
    {//GEN-HEADEREND:event_jButton3ActionPerformed
        // TODO add your handling code here:
        
        jPanel8.setBackground(Color.YELLOW);
        jLabel4.setText("STOPPING");
        
        try
        {
            for(int i=0; i<4; i++)
                if( islandRefreshers[i] != null) islandRefreshers[i].requestStop();
            worldRefresher.requestStop();

            world.stop();
            
            for(int i=0; i<4; i++)
                if( islandRefreshers[i] != null)
                {
                    islandRefreshers[i].waitForStop();
                    islandStatusPanels[i].setBackground(Color.GRAY);
                }
            worldRefresher.waitForStop();
            
            jPanel8.setBackground(Color.BLUE);
            jLabel4.setText("STOPPED");
            jButton3.setEnabled(false);
            jButton4.setEnabled(true);
            logTextArea.append(new Date() + ": Evolution stopped... What is wrong with you men!!?\n");
        }
        catch( Throwable t )
        {
            Logger.getLogger(EveGui.class.getName()).log(Level.SEVERE, null, t);
            logTextArea.append(t.toString());
            logTextArea.append("\n" + new Date() + ": Error while stopping the world.\n");
            jPanel8.setBackground(Color.RED);
            jLabel4.setText("ERROR");
            jButton3.setEnabled(false);
            jButton4.setEnabled(true);
        }
        
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jSlider1StateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_jSlider1StateChanged
    {//GEN-HEADEREND:event_jSlider1StateChanged
        // TODO add your handling code here:
        for(int i=0; i<4; i++)
            if( islandRefreshers[i] != null) islandRefreshers[i].changeRefreshTime(jSlider1.getValue());
        
    }//GEN-LAST:event_jSlider1StateChanged

    private void jTabbedPane2StateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_jTabbedPane2StateChanged
    {//GEN-HEADEREND:event_jTabbedPane2StateChanged
        // TODO add your handling code here:
        
        if( jTabbedPane2.getSelectedComponent() == jPanel4 )
        {
            if( jButton3.isEnabled() )
            {
                logTextArea.append(new Date() + ": Emergency STOP :-P\n");
                jButton3ActionPerformed(null);
                jPanel4.setBackground(new Color(23, 114, 69));
            }
        }
            
        
    }//GEN-LAST:event_jTabbedPane2StateChanged

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton5ActionPerformed
    {//GEN-HEADEREND:event_jButton5ActionPerformed
        // TODO add your handling code here:
        
        saveToDiskButtonActionPerformed(null);
        
//        String[] newNames = new String[lastExperiment.getSamplesNumber()];
//            for(int i=0; i<newNames.length; i++) newNames[i] = "Clu" + i;
//            
//        Profile[] profiles = lastExperiment.getProfiles();
//        for(int i=0; i<profiles.length)
        
        Matrix samplesMatrix = lastExperiment.toMatrix();
        
        String[] newNames = new String[samplesMatrix.nRows()];
            for(int i=0; i<newNames.length; i++) newNames[i] = "clu" + i;
        
        Profile[] profiles = new Profile[samplesMatrix.nRows()];
        for(int i=0; i<samplesMatrix.nRows(); i++)
        {
            profiles[i] = new Profile(newNames[i], samplesMatrix.getRow(i), lastExperiment.getSamplingTime());
            if( !disableTimeUnitCheckBox.isSelected() )
                            profiles[i].setTimeUnit(timeUnitTextField.getText());
        }

        lastExperiment = new Experiment( lastExperiment.getName(), profiles);
        logTextArea.append(new Date() + ": Names cluterized.\n");
        
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton6ActionPerformed
    {//GEN-HEADEREND:event_jButton6ActionPerformed
        // TODO add your handling code here:
        
        for(int i=0; i<4; i++)
            if(islands[i] != null)
            {
                EvolutiveIsland.Solution solution = islands[i].getLastBestSolution();
                if( solution != null)
                {
                    JFileChooser jFileChooser = new JFileChooser();
                    if( jFileChooser.showDialog(this, "Save Best ODEModel from Island " + (i+1)) == JFileChooser.APPROVE_OPTION )
                    {
                        try
                        {
                            File file = jFileChooser.getSelectedFile();
                            solution.model.toFile(file, solution.names);
                            logTextArea.append(new Date() + ": Best ODEModel till now from Island " + i+1 + " saved successfully.\n");
                        } catch(IOException e)
                        {
                            Logger.getLogger(EveGui.class.getName()).log(Level.SEVERE, null, e);
                            logTextArea.append(e.toString());
                            logTextArea.append("\n" + new Date() + ": Error: could not save the best ODEModel of island " + i+1 + " to file.\n");
                        }
                    }
                    jFileChooser = new JFileChooser();
                    if( jCheckBox6.isSelected() )
                    {
                        if(jFileChooser.showDialog(this, "Save Best ODEModel from Island to sif " + (i+1)) == JFileChooser.APPROVE_OPTION)
                        {
                            try
                            {
                                File file = jFileChooser.getSelectedFile();
                                solution.model.toSifFile(file, solution.names, (Double)jSpinner5.getValue());
                                logTextArea.append(new Date() + ": Best ODEModel till now from Island " + i+1 + " saved successfully to sif.\n");
                            } catch(IOException e)
                            {
                                Logger.getLogger(EveGui.class.getName()).log(Level.SEVERE, null, e);
                                logTextArea.append(e.toString());
                                logTextArea.append("\n" + new Date() + ": Error: could not save the best ODEModel of island " + i+1 + " to sif file.\n");
                            }
                        }
                    }
                }
            }
        
        
        
    }//GEN-LAST:event_jButton6ActionPerformed
        
    public static void main(String args[])
    {
        try { javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName()); }
        catch(Exception e) { System.out.println(e); }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                new EveGui().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel StatisticsPanel;
    private javax.swing.JCheckBox autogenerateNamesCheckBox;
    private javax.swing.JProgressBar clusteringProgressBar;
    private javax.swing.JSpinner clusteringRadiusSpinner;
    private javax.swing.JPanel clustreingPanel;
    private javax.swing.JCheckBox disableTimeUnitCheckBox;
    private javax.swing.JButton discardLastButton;
    private javax.swing.JComboBox distanceTypeComboBox;
    private javax.swing.JLabel experimentNameLabel;
    private javax.swing.JTextField experimentNameTextField;
    private javax.swing.JPanel experimentPanel;
    private javax.swing.JButton goClusteringButton;
    private javax.swing.JButton goInterpolateButton;
    private javax.swing.JButton goSelectionButton;
    private javax.swing.JPanel interpolatePanel;
    private javax.swing.JProgressBar interpolateProgressBar;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox17;
    private javax.swing.JCheckBox jCheckBox18;
    private javax.swing.JCheckBox jCheckBox19;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox20;
    private javax.swing.JCheckBox jCheckBox21;
    private javax.swing.JCheckBox jCheckBox22;
    private javax.swing.JCheckBox jCheckBox23;
    private javax.swing.JCheckBox jCheckBox24;
    private javax.swing.JCheckBox jCheckBox25;
    private javax.swing.JCheckBox jCheckBox26;
    private javax.swing.JCheckBox jCheckBox27;
    private javax.swing.JCheckBox jCheckBox28;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JCheckBox jCheckBox6;
    private javax.swing.JComboBox jComboBox6;
    private javax.swing.JComboBox jComboBox7;
    private javax.swing.JComboBox jComboBox8;
    private javax.swing.JComboBox jComboBox9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel103;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel105;
    private javax.swing.JLabel jLabel106;
    private javax.swing.JLabel jLabel107;
    private javax.swing.JLabel jLabel108;
    private javax.swing.JLabel jLabel109;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel110;
    private javax.swing.JLabel jLabel111;
    private javax.swing.JLabel jLabel112;
    private javax.swing.JLabel jLabel113;
    private javax.swing.JLabel jLabel114;
    private javax.swing.JLabel jLabel115;
    private javax.swing.JLabel jLabel116;
    private javax.swing.JLabel jLabel117;
    private javax.swing.JLabel jLabel118;
    private javax.swing.JLabel jLabel119;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel120;
    private javax.swing.JLabel jLabel121;
    private javax.swing.JLabel jLabel122;
    private javax.swing.JLabel jLabel123;
    private javax.swing.JLabel jLabel124;
    private javax.swing.JLabel jLabel125;
    private javax.swing.JLabel jLabel126;
    private javax.swing.JLabel jLabel127;
    private javax.swing.JLabel jLabel128;
    private javax.swing.JLabel jLabel129;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel130;
    private javax.swing.JLabel jLabel131;
    private javax.swing.JLabel jLabel132;
    private javax.swing.JLabel jLabel133;
    private javax.swing.JLabel jLabel134;
    private javax.swing.JLabel jLabel135;
    private javax.swing.JLabel jLabel136;
    private javax.swing.JLabel jLabel137;
    private javax.swing.JLabel jLabel138;
    private javax.swing.JLabel jLabel139;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel140;
    private javax.swing.JLabel jLabel141;
    private javax.swing.JLabel jLabel142;
    private javax.swing.JLabel jLabel143;
    private javax.swing.JLabel jLabel144;
    private javax.swing.JLabel jLabel145;
    private javax.swing.JLabel jLabel146;
    private javax.swing.JLabel jLabel147;
    private javax.swing.JLabel jLabel148;
    private javax.swing.JLabel jLabel149;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel150;
    private javax.swing.JLabel jLabel151;
    private javax.swing.JLabel jLabel152;
    private javax.swing.JLabel jLabel153;
    private javax.swing.JLabel jLabel154;
    private javax.swing.JLabel jLabel155;
    private javax.swing.JLabel jLabel156;
    private javax.swing.JLabel jLabel157;
    private javax.swing.JLabel jLabel158;
    private javax.swing.JLabel jLabel159;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel160;
    private javax.swing.JLabel jLabel161;
    private javax.swing.JLabel jLabel162;
    private javax.swing.JLabel jLabel163;
    private javax.swing.JLabel jLabel164;
    private javax.swing.JLabel jLabel165;
    private javax.swing.JLabel jLabel166;
    private javax.swing.JLabel jLabel167;
    private javax.swing.JLabel jLabel168;
    private javax.swing.JLabel jLabel169;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel170;
    private javax.swing.JLabel jLabel171;
    private javax.swing.JLabel jLabel172;
    private javax.swing.JLabel jLabel173;
    private javax.swing.JLabel jLabel174;
    private javax.swing.JLabel jLabel175;
    private javax.swing.JLabel jLabel176;
    private javax.swing.JLabel jLabel177;
    private javax.swing.JLabel jLabel178;
    private javax.swing.JLabel jLabel179;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel180;
    private javax.swing.JLabel jLabel181;
    private javax.swing.JLabel jLabel182;
    private javax.swing.JLabel jLabel183;
    private javax.swing.JLabel jLabel184;
    private javax.swing.JLabel jLabel185;
    private javax.swing.JLabel jLabel186;
    private javax.swing.JLabel jLabel187;
    private javax.swing.JLabel jLabel188;
    private javax.swing.JLabel jLabel189;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel190;
    private javax.swing.JLabel jLabel191;
    private javax.swing.JLabel jLabel192;
    private javax.swing.JLabel jLabel193;
    private javax.swing.JLabel jLabel194;
    private javax.swing.JLabel jLabel195;
    private javax.swing.JLabel jLabel196;
    private javax.swing.JLabel jLabel197;
    private javax.swing.JLabel jLabel198;
    private javax.swing.JLabel jLabel199;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel200;
    private javax.swing.JLabel jLabel201;
    private javax.swing.JLabel jLabel202;
    private javax.swing.JLabel jLabel203;
    private javax.swing.JLabel jLabel204;
    private javax.swing.JLabel jLabel205;
    private javax.swing.JLabel jLabel206;
    private javax.swing.JLabel jLabel207;
    private javax.swing.JLabel jLabel208;
    private javax.swing.JLabel jLabel209;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel210;
    private javax.swing.JLabel jLabel211;
    private javax.swing.JLabel jLabel212;
    private javax.swing.JLabel jLabel213;
    private javax.swing.JLabel jLabel214;
    private javax.swing.JLabel jLabel215;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JPanel jPanel32;
    private javax.swing.JPanel jPanel33;
    private javax.swing.JPanel jPanel34;
    private javax.swing.JPanel jPanel35;
    private javax.swing.JPanel jPanel36;
    private javax.swing.JPanel jPanel37;
    private javax.swing.JPanel jPanel38;
    private javax.swing.JPanel jPanel39;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel40;
    private javax.swing.JPanel jPanel41;
    private javax.swing.JPanel jPanel42;
    private javax.swing.JPanel jPanel44;
    private javax.swing.JPanel jPanel45;
    private javax.swing.JPanel jPanel46;
    private javax.swing.JPanel jPanel48;
    private javax.swing.JPanel jPanel49;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel50;
    private javax.swing.JPanel jPanel51;
    private javax.swing.JPanel jPanel52;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JSpinner jSpinner2;
    private javax.swing.JSpinner jSpinner3;
    private javax.swing.JSpinner jSpinner38;
    private javax.swing.JSpinner jSpinner39;
    private javax.swing.JSpinner jSpinner4;
    private javax.swing.JSpinner jSpinner40;
    private javax.swing.JSpinner jSpinner41;
    private javax.swing.JSpinner jSpinner42;
    private javax.swing.JSpinner jSpinner43;
    private javax.swing.JSpinner jSpinner44;
    private javax.swing.JSpinner jSpinner45;
    private javax.swing.JSpinner jSpinner46;
    private javax.swing.JSpinner jSpinner47;
    private javax.swing.JSpinner jSpinner48;
    private javax.swing.JSpinner jSpinner49;
    private javax.swing.JSpinner jSpinner5;
    private javax.swing.JSpinner jSpinner50;
    private javax.swing.JSpinner jSpinner51;
    private javax.swing.JSpinner jSpinner52;
    private javax.swing.JSpinner jSpinner53;
    private javax.swing.JSpinner jSpinner54;
    private javax.swing.JSpinner jSpinner55;
    private javax.swing.JSpinner jSpinner56;
    private javax.swing.JSpinner jSpinner57;
    private javax.swing.JSpinner jSpinner58;
    private javax.swing.JSpinner jSpinner59;
    private javax.swing.JSpinner jSpinner60;
    private javax.swing.JSpinner jSpinner61;
    private javax.swing.JSpinner jSpinner62;
    private javax.swing.JSpinner jSpinner63;
    private javax.swing.JSpinner jSpinner64;
    private javax.swing.JSpinner jSpinner65;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JTable jTable;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    private javax.swing.JButton loadFilesButton;
    private javax.swing.JButton lockExperimentButton;
    private javax.swing.JLabel lockedLabel;
    private javax.swing.JPanel lockedPanel;
    private javax.swing.JTextArea logTextArea;
    private javax.swing.JTextArea messageTextArea;
    private javax.swing.JSpinner minVarianceSelectionSpinner;
    private javax.swing.JTextField namesFileTextField;
    private javax.swing.JLabel namesLabel;
    private javax.swing.JSpinner newNumberOfSamplesInterpolateSpinner;
    private javax.swing.JPanel operationsPanel;
    private javax.swing.JPanel preElaborationPanel;
    private javax.swing.JLabel profilesNumberLabel;
    private javax.swing.JButton revoverFirstButton;
    private javax.swing.JButton samplesFileSelectButton;
    private javax.swing.JTextField samplesFileTextField;
    private javax.swing.JLabel samplesNumberLabel;
    private javax.swing.JLabel samplingTimeLabel;
    private javax.swing.JSpinner samplingTimeSpinner;
    private javax.swing.JButton saveToDiskButton;
    private javax.swing.JPanel selectionPanel;
    private javax.swing.JProgressBar selectionProgressBar;
    private javax.swing.JLabel stateLabel;
    private javax.swing.JLabel timeUnitLabel;
    private javax.swing.JTextField timeUnitTextField;
    private javax.swing.JCheckBox transposeCheckBox;
    private javax.swing.JLabel workingStatusLabel;
    private javax.swing.JPanel workingStatusPanel;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    private void refreshStateInfos( Experiment experiment )
    {
        profilesNumberLabel.setText(( Integer.toString( experiment.getSize() )));
        samplingTimeLabel.setText( Double.toString(experiment.getSamplingTime()) );
        samplesNumberLabel.setText( Integer.toString( experiment.getSamplesNumber() ));
        timeUnitLabel.setText( timeUnitTextField.getText() );
    }
}
