package CasGrid;

import java.net.*;
import java.util.*;
import java.sql.*;

public class Client extends Thread implements Logger
{
    private boolean stopFlag;
    private boolean loggingEnabled;
    private HashSet<Logger> loggers;

    private InetSocketAddress address;

    private CompT computer;
    private int nWU;

    public Client(InetSocketAddress address, int nThreads, int nWU)
    {
        stopFlag=false;
        loggingEnabled=false;
        loggers = new HashSet<Logger>();
        this.address=address;

        this.nWU=nWU;
        computer = new CompT(nThreads, this);
    }

    @Override
    public void run()
    {
        log("Avvio del client con " + computer.getNThreads() + " Threads.");
        while( stopFlag==false )
        {
            try
            {
                log("Richiesta di WU da " + nWU + " blocchi al server...");

                Message reply=null;
                while( reply==null )
                    reply=Dial.talk(address, new Message(Message.REQUEST).setNWU(nWU));
                    
                
                if( reply.getMsg()==Message.GO )
                {
                    log(new Timestamp(new java.util.Date().getTime()) + ": Ricevuta WU da " + reply.getMani().size() + " blocchi.");
                    long atime = System.currentTimeMillis();
                    log(new Timestamp(new java.util.Date().getTime()) + ": Calcolo della WU in corso...");

                    for(Hand mano:reply.getMani()) computer.calcola(mano);
                    reply.setMsg(Message.RETURN);

                    float time=(System.currentTimeMillis()-atime)/(float)1000;
                    log(new Timestamp(new java.util.Date().getTime()) + ": Calcolo della WU terminato in " + time + "s");
                    log("Alla fantastica velocità di " + (reply.getMani().size()/(float)time) + " Blocchi/s");
                    log(new Timestamp(new java.util.Date().getTime()) + ": Restituzione risultati al server...");
                    
                    Message confirm=null;
                    while( confirm==null )
                        try{ confirm=Dial.talk(address, reply); }
                    catch(ConnectException e) { System.err.println("Errore connessione... Riprovo..."); }

                    if( confirm.getMsg()==Message.RECEIVED_AND_SHUTTINGDOWN )
                    {
                        stopFlag = true;
                        log(new Timestamp(new java.util.Date().getTime()) + ": WU restituite e server in spegnimento.");
                    }
                    else if( confirm.getMsg()==Message.RECEIVED )
                    {
                        log(new Timestamp(new java.util.Date().getTime()) + ": Restituzione andata a buon fine.");
                    }
                    else
                    {
                        log("Messaggio di conferma non compreso: ERRORE!");
                        throw new Exception("Codice non riconosciuto" + confirm.getMsg());
                    }
                }
                else if( reply.getMsg()==Message.NO_MORE_WUNITS )
                {
                    log(new Timestamp(new java.util.Date().getTime()) + ": Il server ha comunicato che non ci sono più WU da elaborare: spegnimento...");
                    stopFlag = true;
                }
                else if( reply.getMsg()==Message.SHUTTINGDOWN )
                {
                    log(new Timestamp(new java.util.Date().getTime()) + ": Server in spegnimento: spegnimento...");
                    stopFlag = true;
                }
                else throw new Exception("Codice non riconosciuto" + reply.getMsg());
            }
            catch(ConnectException e)
            {
                System.err.println("Errore connessione... Riprovo...");
                log(new Timestamp(new java.util.Date().getTime()) + ": Errore connessione... Riprovo...");
            }
            catch(Exception e)
            {
                System.err.println(e);
                e.printStackTrace();
                System.exit(-1);
            }
        }
        log(new Timestamp(new java.util.Date().getTime()) + ": Client terminato.");
    }

    public void pleaseStop()
    {
        stopFlag=true;
        log(new Timestamp(new java.util.Date().getTime()) + ": Segnale d'arresto ricevuto: in attesa del completamento della WorkUnit...");
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