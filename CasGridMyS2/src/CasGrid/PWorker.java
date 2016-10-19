package CasGrid;

import java.util.*;
import java.util.concurrent.atomic.*;

public class PWorker extends Thread
{
    private int[][] scm, tcm, vv;
    private int[] space;
    private AtomicInteger[][] finalwin;
    private int start, stop, ii;

    //sezione punteggi
    private int ROYALFLUSH_NOJOLLY = 800;
    private int FIVEOFAKIND_JOLLY = 150;
    private int JOKERROYAL = 80;
    private int STRAIGHTFLUSH_NOJOLLY = 50;
    private int STRAIGHTFLUSH_JOLLY = 50;
    private int FOUROFAKIND_NOJOLLY = 20;
    private int FOUROFAKIND_JOLLY = 20;
    private int FULL_NOJOLLY = 7;
    private int FULL_JOLLY = 7;
    private int FLUSH_NOJOLLY = 5;
    private int FLUSH_JOLLY = 5;
    private int STRAIGHT_NOJOLLY = 3;
    private int STRAIGHT_JOLLY = 3;
    private int THREEOFAKIND_NOJOLLY = 2;
    private int THREEOFAKIND_JOLLY = 2;
    private int TWOPAIR = 1;
    private int KINGSORBETTER_NOJOLLY = 1;
    private int KINGSORBETTER_JOLLY = 1;

    private int[]   seed = new int[4],
            val =  new int[13],
            rseed =  new int[6],
            rval =  new int[6];

    private int jolly, scala, reale, suited, Aending, point;
    private AtomicInteger[][] pt;
    private int[][] ptlocal;
    //fine sezione punteggi

    public PWorker( int[][] scm, int[][] tcm, int[][] vv, int[] space, AtomicInteger[][] finalwin, int start, int stop , AtomicInteger[][] pt)
    {
        this.scm=scm;
        this.tcm=tcm;
        this.finalwin=finalwin;
        this.start=start;
        this.stop=stop;
        this.space=space;
        this.vv=vv;
        
        this.pt=pt;
    }


    @Override
    public void run()
    {
        //qui abbiamo la mano da elaborare, il range di combinazioni da analizzare in tcm
        
        //definiamo un array che contiene la sostituzione e la vincita
        int[][] win = new int[32][2]; for( int i=0; i<32; i++ ) win[i][0]=win[i][1]=0;
        int[] finalhand = new int[5];
        int g=0, j;
        
        /* controllo punti ottenuti*/
        ptlocal = new int[32][19]; //punti da 0 a 17 e nisba il 18
        for( int i=0; i<ptlocal.length; i++ )
            for( int m=0; m<ptlocal[i].length; m++ )
                ptlocal[i][m]=0;
        /**/

        CompT.copy2dnoalloc(scm, tcm, start, stop);

        for( int i=start; i<stop; i++ )
        {
            //per ogni carta da sostituire: tcm[i].length-1 carte da sostituire, sostituiamo le carte della combinazione di tcm[i] con quelle di space
            for( j=0; j<tcm[i].length-1; j++ ) tcm[i][j+1]=space[tcm[i][j+1]];
            //ora in tcm[i] ci stanno le carte della sostituzione da unire con quelle di vv

            g=0;
            for( j=0; j<tcm[i].length-1; j++) finalhand[g++]=tcm[i][j+1];
            for( j=0; j<vv[tcm[i][0]].length; j++) finalhand[g++]=vv[tcm[i][0]][j];
            Arrays.sort(finalhand);
            win[tcm[i][0]][0]+=calcolaPunti(finalhand, tcm[i][0]);
            win[tcm[i][0]][1]++;
        }

        //alla fine uniamo i risultati con l'array che sta nel chiamante (thread safe)
        for( int i=0; i<32; i++)
        {
            finalwin[i][0].addAndGet(win[i][0]);
            finalwin[i][1].addAndGet(win[i][1]);
        }
        
        /*uniamo i punti di ptlocal con quelli di pt del chiamante (che è sync)*/
        for( int i=0; i<pt.length; i++ )
            for( int m=0; m<pt[i].length; m++ )
                pt[i][m].addAndGet(ptlocal[i][m]);
        /**/
    }

    private int calcolaPunti(int[] m, int s)
    {
        //abbiamo 5 carte su 53: da 0 a 52: in modulo 13 le prime 52 sono le carte normali, il cui seme è il rapporto con 13 e il cui valore è il modulo con 13
        //52 è il jolly e 52/13=4

        Arrays.fill( seed, 0 );
        Arrays.fill( val, 0 );
        Arrays.fill( rseed, 0 );
        Arrays.fill( rval, 0 );
        jolly=scala=reale=suited=Aending=point=0;

        //raggruppo nei contatori le carte in base al loro seme e al loro valore, tenendo traccia del jolly in jolly
        for( ii=0; ii<5; ii++)
            if( m[ii]!=52 ) { seed[m[ii]/13]++; val[m[ii]%13]++; }
            else jolly=1;
        for( ii=0; ii<4; ii++) rseed[seed[ii]]++; //scansione delle somme seed
        for( ii=0; ii<13; ii++) rval[val[ii]]++; //scansione delle somme val

        //ogni cella rseed e rval contiene il numero di elementi suited o con lo stesso valore
        //secondo l'indice dell'array stesso: rseed[4] -> n° di carte suited a 4
        //rval[2] -> numero di coppie

        //suited?
        if( (jolly==0 && rseed[5]==1)||(jolly==1 && rseed[4]==1) ) {suited=1;}

        //scala?
        if( jolly==0 && rval[1]==5 )
        {
            for( int i=0; i<9; i++ )
                if( val[i]==1 && val[i+1]==1 && val[i+2]==1 && val[i+3]==1 && val[i+4]==1 ) { scala=1; break; }
                else;
        }
        else if( jolly==1 && rval[1]==4 )
        {
            for( int i=0; i<9; i++ )
                if(( val[i]==0 && val[i+1]==1 && val[i+2]==1 && val[i+3]==1 && val[i+4]==1 ) ||
                   ( val[i]==1 && val[i+1]==0 && val[i+2]==1 && val[i+3]==1 && val[i+4]==1 ) ||
                   ( val[i]==1 && val[i+1]==1 && val[i+2]==0 && val[i+3]==1 && val[i+4]==1 ) ||
                   ( val[i]==1 && val[i+1]==1 && val[i+2]==1 && val[i+3]==0 && val[i+4]==1 ) ||
                   ( val[i]==1 && val[i+1]==1 && val[i+2]==1 && val[i+3]==1 && val[i+4]==0 ) ) { scala=1; break; }
                else;
        }

        //scala che fineisce per Asso?
        if( jolly==0 && rval[1]==5 )
        {
            if( val[9]==1 && val[10]==1 && val[11]==1 && val[12]==1 && val[0]==1 ) { scala=1; Aending=1; }
            else;
        }
        else if( jolly==1 && rval[1]==4 )
        {
            if(( val[9]==0 && val[10]==1 && val[11]==1 && val[12]==1 && val[0]==1 ) ||
               ( val[9]==1 && val[10]==0 && val[11]==1 && val[12]==1 && val[0]==1 ) ||
               ( val[9]==1 && val[10]==1 && val[11]==0 && val[12]==1 && val[0]==1 ) ||
               ( val[9]==1 && val[10]==1 && val[11]==1 && val[12]==0 && val[0]==1 ) ||
               ( val[9]==1 && val[10]==1 && val[11]==1 && val[12]==1 && val[0]==0 )     ) { scala=1; Aending=1; }
            else;
        }

        //stabiliamo le abbiamo una scala reale: scala+suited+Aending
        if( scala==1 && suited==1 && Aending==1) reale=1;

        //ora abbiamo tutti i flag che ci servono per riconoscere i punti

        if (jolly == 0 && scala == 1 && reale == 1 )
        {
            point = ROYALFLUSH_NOJOLLY;
            ptlocal[s][0]++;
        } else if (jolly == 1 && rval[4] == 1)
        {
            point = FIVEOFAKIND_JOLLY;
            ptlocal[s][1]++;
        } else if (jolly == 1 && scala == 1 && reale == 1 && suited == 1)
        {
            point = JOKERROYAL;
            ptlocal[s][2]++;
        } else if (jolly == 0 && scala == 1 && suited == 1 && Aending == 0)
        {
            point = STRAIGHTFLUSH_NOJOLLY;
            ptlocal[s][3]++;
        } else if (jolly == 1 && scala == 1 && suited == 1 && Aending == 0)
        {
            point = STRAIGHTFLUSH_JOLLY;
            ptlocal[s][4]++;
        } else if (jolly == 0 && rval[4] == 1)
        {
            point = FOUROFAKIND_NOJOLLY;
            ptlocal[s][5]++;
        } else if (jolly == 1 && rval[3] == 1)
        {
            point = FOUROFAKIND_JOLLY;
            ptlocal[s][6]++;
        } else if (jolly == 0 && rval[2] == 1 && rval[3] == 1)
        {
            point = FULL_NOJOLLY;
            ptlocal[s][7]++;
        } else if (jolly == 1 && rval[2] == 2)
        {
            point = FULL_JOLLY;
            ptlocal[s][8]++;
        } else if (jolly == 0 && suited == 1)
        {
            point = FLUSH_NOJOLLY;
            ptlocal[s][9]++;
        } else if (jolly == 1 && suited == 1)
        {
            point = FLUSH_JOLLY;
            ptlocal[s][10]++;
        } else if (jolly == 0 && scala == 1 /*&& reale == 0 && suited == 0*/)
        {
            point = STRAIGHT_NOJOLLY;
            ptlocal[s][11]++;
        } else if (jolly == 1 && scala == 1 /*&& reale == 0 && suited == 0*/)
        {
            point = STRAIGHT_JOLLY;
            ptlocal[s][12]++;
        } else if (jolly == 0 && rval[3] == 1)
        {
            point = THREEOFAKIND_NOJOLLY;
            ptlocal[s][13]++;
        } else if (jolly == 1 && rval[2] == 1)
        {
            point = THREEOFAKIND_JOLLY;
            ptlocal[s][14]++;
        } else if (jolly == 0 && rval[2] == 2)
        {
            point = TWOPAIR;
            ptlocal[s][15]++;
        } else if (jolly == 0 && rval[2] == 1 && rval[1] == 3 && (val[12] == 2 || val[0] == 2))
        {
            point = KINGSORBETTER_NOJOLLY;
            ptlocal[s][16]++;
        } else if (jolly == 1 && rval[1] == 4 && (val[12] == 1 || val[0] == 1))
        {
            point = KINGSORBETTER_JOLLY;
            ptlocal[s][17]++;
        } else //nessun punto
        {
            point = 0;
            ptlocal[s][18]++;
        }

        return point;
    }
}