package CasGrid;

import java.io.*;

public class Hand implements Serializable
{
    private int[] carte;
    private int sostituzione;
    private float media;

    public Hand(int[] carte)
    {
        this.carte=carte;
        sostituzione=0;
        media=0;
    }

    public Hand()
    {
        carte=null;
        sostituzione=0;
        media=0;
    }

    public Hand(String carteStr)
    {
        setCarte(carteStr);
        sostituzione=0;
        media=0;
    }

    public void setSostituzione(int s) { this.sostituzione=s; }
    public void setMedia(float m) { this.media=m; }
    public void setCarte(int[] carte) { this.carte=carte; }
    public void setCarte(String carte)
    {
        String carteStr[] = carte.split(" ");
        int[] carteInt = new int[5];
        for( int i=0; i<5; i++ ) carteInt[i]=Integer.parseInt(carteStr[i]);
        this.carte=carteInt;
    }

    public int getSostituzione() { return sostituzione; }
    public float getMedia() { return media; }
    public int[] getCarte() { return carte; }

    public String printCarteNumeric()
    {
        String s = carte[0] + " " + carte[1] + " " + carte[2] + " " + carte[3] + " " + carte[4];
        return s;
    }
}
