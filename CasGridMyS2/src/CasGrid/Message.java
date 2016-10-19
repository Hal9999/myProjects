package CasGrid;

import java.io.*;
import java.util.*;

public class Message implements Serializable
{
    private int msg, nWU;
    private HashSet<Hand> mani;

    static final int REQUEST = 1;
    static final int GO = 2;
    static final int NO_MORE_WUNITS = 3;
    static final int SHUTTINGDOWN = 4;
    static final int RETURN = 5;
    static final int RECEIVED = 6;
    static final int RECEIVED_AND_SHUTTINGDOWN = 7;

    public Message(int msg)
    {
        this.msg=msg;
    }

    public Message()
    {
        this.nWU=0;
        this.msg=0;
        this.mani=null;
    }

    public int getMsg() { return msg; }
    public int getNWU() { return nWU; }
    public HashSet<Hand> getMani() { return mani; }

    public Message setNWU(int nWU) { this.nWU=nWU; return this; }
    public Message setMsg(int msg) { this.msg=msg; return this; }
    public Message setMani(HashSet<Hand> mani) { this.mani=mani; return this; }
}