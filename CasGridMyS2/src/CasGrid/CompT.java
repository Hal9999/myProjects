package CasGrid;

import java.util.*;
import java.io.*;
import java.util.concurrent.atomic.*;

public class CompT
{
    private int[][] matrix, scm, tcm, svv, tvv;
    private int[] space;
    private AtomicInteger[][] finalwin;
    private int nThreads;
    
    private boolean loggingEnabled;
    private HashSet<Logger> loggers;

    public CompT(int nThreads, Logger logger)
    {
        loggers = new HashSet<Logger>();
        this.nThreads=nThreads;
        this.addLogger(logger);

        inizializzaMatrici();
    }

    public void calcola(Hand mano) throws IOException
    {
        //long time = System.currentTimeMillis();
        int[] c = mano.getCarte();
        
        /*codice per controllare quali punti sono stati ottenuti*/
        AtomicInteger[][] pt = new AtomicInteger[32][19]; //punti da 0 a 17 e nisba il 18
        for( int i=0; i<pt.length; i++ )
            for( int j=0; j<pt[i].length; j++ )
                pt[i][j]=new AtomicInteger(0);

        //tvv[][] conterrà le carte tenute per ogni sostituzione
        for( int i=0; i<32; i++ )
            for( int j=0; j<tvv[i].length; j++ ) tvv[i][j] = c[tvv[i][j]];

        //l'array delle 48 carte permesse è unico per tutte le combinazioni, le carte della mano sono messe in ordine crescente
        for( int j=0, k=0, s=0; s<53-5; j++)
        {
            if( k<=4 ) //per k da 0 a 4 significa che ancora non abbiamo incontrato tutte le carte vietate
                if( j!=c[k] ) space[s++]=j; else k++;
            else space[s++]=j;
        }

        //Thread Launch///////////////////////////////////////////////////////////////////////////////////////////////////
        int nrows = tcm.length/nThreads;
        Thread[] threads = new Thread[nThreads];
        for( int i=0; i<nThreads-1; i++ )
        {
            PWorker t = new PWorker( scm, tcm, tvv, space, finalwin, nrows*i, nrows*(i+1) , pt);
            threads[i]= t;
            t.start();
        }
        PWorker t = new PWorker( scm, tcm, tvv, space, finalwin, nrows*(nThreads-1), tcm.length , pt);
        threads[nThreads-1]=t;
        t.start();
        try
        {
            for( int i=0; i<nThreads; i++ ) threads[i].join();
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //ora finalwin contiene i valori delle vincite legate alle sostituzioni
        // [i]: è la sostituzione; [i][0] è la vincita; [i][1] è il numero di sostituzioni
        int n=0;
        for( int i=0; i<32; i++ )
            if( finalwin[i][0].get()/(float)finalwin[i][1].get()>finalwin[n][0].get()/(float)finalwin[n][1].get() ) n=i;

        mano.setSostituzione(n);
        mano.setMedia(finalwin[n][0].get()/(float)finalwin[n][1].get());
        
        //System.out.println((System.currentTimeMillis()-time)/(float)1000 + "s");
        

        //log("Rigenerazione matrici base in corso...");
        //rigenero gli array modificati
        //copy2dnoalloc(scm, tcm); non li rigenero ora perchè ora i thread si rigenerano la loro parte concorrentemente
        copy2dnoalloc(svv, tvv);
        for( int i=0; i<32; i++ )
        {
            finalwin[i][0].set(0);
            finalwin[i][1].set(0);
        }
        //log((System.currentTimeMillis()-time)/(float)1000 + "secondi");
        //log("Rigenerazione matrici completata.");
    }
    
    private void inizializzaMatrici()
    {
        matrix = new int[][]{{1, 1, 1, 1, 1}, {1, 1, 1, 1, 0}, {1, 1, 1, 0, 1}, {1, 1, 0, 1, 1},
                             {1, 0, 1, 1, 1}, {0, 1, 1, 1, 1}, {1, 1, 1, 0, 0}, {1, 1, 0, 1, 0},
                             {1, 0, 1, 1, 0}, {0, 1, 1, 1, 0}, {1, 1, 0, 0, 1}, {1, 0, 1, 0, 1},
                             {0, 1, 1, 0, 1}, {1, 0, 0, 1, 1}, {0, 1, 0, 1, 1}, {0, 0, 1, 1, 1},
                             {0, 0, 0, 1, 1}, {0, 0, 1, 0, 1}, {0, 1, 0, 0, 1}, {1, 0, 0, 0, 1},
                             {0, 0, 1, 1, 0}, {0, 1, 0, 1, 0}, {1, 0, 0, 1, 0}, {0, 1, 1, 0, 0},
                             {1, 0, 1, 0, 0}, {1, 1, 0, 0, 0}, {0, 0, 0, 0, 1}, {0, 0, 0, 1, 0},
                             {0, 0, 1, 0, 0}, {0, 1, 0, 0, 0}, {1, 0, 0, 0, 0}, {0, 0, 0, 0, 0}};

        //                          0  1        2       3        4
        int[][] icm = new int[][] {{1, 48,      0,      48,      5  },
                                   {2, 1128,    48,     1176,    10 },
                                   {3, 17296,   1176,   18472,   10 },
                                   {4, 194580,  18472,  213052,  5  },
                                   {5, 1712304, 213052, 1925356, 1  }};
        //col primo indice seleziono il numero di sostituzioni (da 1 a 5), col secondo
        //abbiamo n° di sostituzioni possibili, indice di inizio(incluso) e indice di fine(escluso)
        //delle sostituzioni nell'array cm

        int[][] cm = new int[1925356][];

        for( int x=0, N=48, K; x<icm.length; x++ ) //per ogni numero di sostituzioni da 1 a 5: K
        {
            K=icm[x][0];
            int[] comb = new int[K]; //array di lavoro lungo K
            for( int i=0; i<K; i++ ) comb[i] = i; //costruiamo la prima combinazione
            cm[icm[x][2]] = Arrays.copyOf(comb, K);
            for( int j=1; j<icm[x][1]; j++) //per ogni possibile combinazione
            {
                int i, h, v;
                //inizio cuore
                for( h=1, v=0; h<=K; h++ ) if( comb[K-h]==N-h ) v++; else break; //conto il numero di cifre da cambiare
                if( v==K ) break; //se ci sono v==K cifre massime, allora abbiamo finito le possibili combinazioni e usciamo
                comb[K-v-1]++; //aumento di uno la cifra di limite con le v cifre
                for( h=K-v, i=1; h<K; h++, i++ ) comb[h]=comb[K-v-1]+i;
                //fine cuore
                cm[icm[x][2]+j] = Arrays.copyOf(comb, K);
            }
        }

        scm = new int[2869685][];
        scm[0] = new int[]{0}; //anche la sostituzione z=0 viene inserita
        for( int s=0, ss=1, y=1; s<icm.length; s++ )
            for( int i=0; i<icm[s][4]; i++, ss++ )
                for( int k=icm[s][2]; k<icm[s][3]; k++ )
                {
                    scm[y]=new int[1+icm[s][0]];
                    scm[y][0]=ss;
                    for( int g=0; g<icm[s][0]; g++ ) scm[y][g+1]=cm[k][g];
                    y++;
                }
        tcm=copy2dalloc(scm);

        svv = new int[32][];
        for( int i=0 ; i<32; i++ )
        {
            svv[i]=new int[matrix[i][0]+matrix[i][1]+matrix[i][2]+matrix[i][3]+matrix[i][4]];
            for( int j=0, k=0; j<5; j++ )
                if( matrix[i][j]==1 ) svv[i][k++]=j;
        }
        tvv=copy2dalloc(svv);

        space = new int[48];

        //finalwin è fatta da interi atomici e alla fine dell'esecuzione conterrà tutte le valutazioni tra cui scegliere la migliore
        finalwin = new AtomicInteger[32][2];
        for( int i=0; i<32; i++ )
        {
            finalwin[i][0]=new AtomicInteger(0);
            finalwin[i][1]=new AtomicInteger(0);
        }
    }

    public int getNThreads()
    {
        return nThreads;
    }

    private void scrivi(int[][] data, String nfile)
    {
        try
        {
            PrintWriter file = new PrintWriter(nfile);
            for( int h=0; h<data.length; h++ )
            {
                int b;
                for( b=0; b<data[h].length; b++ ) file.print(data[h][b] + "\t");
                file.println("");
            }
            file.close();
        }
        catch(Exception e){e.printStackTrace();}
    }
    
    public static int[][] copy2dalloc( int[][] from, int start, int end )
    {
        int[][] to = new int[end-start][];
        for( int i=start, m=0; i<end; i++, m++ )
        {
            to[m] = new int[from[i].length];
            for( int k=0; k<from[i].length; k++ )
            {
                to[m][k]=from[i][k];
            }
        }
        return to;
    }

    public static int[][] copy2dalloc( int[][] from )
    {
        int[][] to = new int[from.length][];
        for( int i=0; i<from.length; i++ )
        {
            to[i] = new int[from[i].length];
            for( int k=0; k<from[i].length; k++ ) to[i][k]=from[i][k];
        }
        return to;
    }

    public static void copy2dnoalloc( int[][] from, int[][] to )
    {
        for( int i=0; i<from.length; i++ ) for( int k=0; k<from[i].length; k++ ) to[i][k]=from[i][k];
    }

    public static void copy2dnoalloc( int[][] from, int[][] to, int start, int stop)
    {
        for( int i=start; i<stop; i++ ) for( int k=0; k<from[i].length; k++ ) to[i][k]=from[i][k];
    }

    public void printPoints(AtomicInteger[] pt)
    {
        String res = "\n";
        res += "Royal Flush\t" + pt[0] + "\n";
        res += "Five of a kind\t" + pt[1] + "\n";
        res += "Joker Royal\t" + pt[2] + "\n";
        res += "Straight Flush\t" + (pt[3].get()+pt[4].get()) + "\n";
        res += "Four of a kind\t" + (pt[5].get()+pt[6].get()) + "\n";
        res += "Full\t\t" + (pt[7].get()+pt[8].get()) + "\n";
        res += "Flush\t\t" + (pt[9].get()+pt[10].get()) + "\n";
        res += "Straight\t" + (pt[11].get()+pt[12].get()) + "\n";
        res += "Three of a kind\t" + (pt[13].get()+pt[14].get()) + "\n";
        res += "Two pair\t\t" + pt[15] + "\n";
        res += "Kings or better\t" + (pt[16].get()+pt[17].get()) + "\n";
        res += "Nisba\t\t" + pt[18] + "\n";
        int t;
        res += "Hands number: " + (t= pt[0].get() + pt[1].get() + pt[2].get() + pt[3].get() + pt[4].get() + pt[5].get() +
                pt[6].get() + pt[7].get() + pt[8].get() + pt[9].get() + pt[10].get() + pt[11].get() + pt[12].get() + pt[13].get() +
                pt[14].get() + pt[15].get() + pt[16].get() + pt[17].get() + pt[18].get() ) + "\n";
        float expected = (pt[0].get()*800 +
                          pt[1].get()*150 +
                          pt[2].get()*80 +
                         (pt[3].get()+pt[4].get())*50 +
                         (pt[5].get()+pt[6].get())*20 +
                         (pt[7].get()+pt[8].get())*7 +
                         (pt[9].get()+pt[10].get())*5 +
                         (pt[11].get()+pt[12].get())*3 +
                         (pt[13].get()+pt[14].get())*2 +
                          pt[15].get()*1 +
                         (pt[16].get()+pt[17].get())*1)/(float)t;
        res += "Mean: " + expected + "\n";
        System.out.println(res);
    }

    public void printOtherSubs(AtomicInteger [][] pt)
    {
        for( int i=0; i<32; i++ )
        {
            String res = "Sub n " + i + " \t";
            int t= pt[i][0].get() + pt[i][1].get() + pt[i][2].get() + pt[i][3].get() + pt[i][4].get() + pt[i][5].get() +
                pt[i][6].get() + pt[i][7].get() + pt[i][8].get() + pt[i][9].get() + pt[i][10].get() + pt[i][11].get() + pt[i][12].get() + pt[i][13].get() +
                pt[i][14].get() + pt[i][15].get() + pt[i][16].get() + pt[i][17].get() + pt[i][18].get();
            float expected = (pt[i][0].get()*800 +
                          pt[i][1].get()*150 +
                          pt[i][2].get()*80 +
                         (pt[i][3].get()+pt[i][4].get())*50 +
                         (pt[i][5].get()+pt[i][6].get())*20 +
                         (pt[i][7].get()+pt[i][8].get())*7 +
                         (pt[i][9].get()+pt[i][10].get())*5 +
                         (pt[i][11].get()+pt[i][12].get())*3 +
                         (pt[i][13].get()+pt[i][14].get())*2 +
                          pt[i][15].get()*1 +
                         (pt[i][16].get()+pt[i][17].get())*1)/(float)t;
            res += t + "\t" + expected;
            System.out.println(res);
            /*System.out.println( "- " + i + " --------------------------------------------");
            printPoints(pt[i]);*/
        }
    }
    
    public void addLogger( Logger logger )
    {
        loggingEnabled=true;
        loggers.add(logger);
    }

    public void log(String msg)
    {
        if( loggingEnabled ) for( Logger logger: loggers ) logger.log(msg);
    }
}