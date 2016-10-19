package CasGrid;

import java.sql.*;
import java.net.*;
import java.util.*;

public class Server extends Thread
{
    private int port;
    private Connection connection;
    private Statement statement;

    private boolean stopFlag, dbOpenStatus;
    private int queueCounter; 
    
    private HashSet<Logger> loggers;
    private boolean loggingEnabled;

    public Server(Connection connection, int port)
    {
        this.connection=connection;
        dbOpenStatus=true;
        this.port=port;
        stopFlag=false;
        queueCounter=0;
        loggingEnabled=false;
        loggers = new HashSet<Logger>();
        try
        {
            this.connection.setAutoCommit(true);
            statement=connection.createStatement();
        }
        catch(SQLException e)
        {
            System.err.println(e);
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run()
    {
        try
        {
            ServerSocket ss = new ServerSocket(port);
            log("Server avviato: porta aperta.");

            while( true )
                new SocketAccepter(this, ss.accept());
        }
        catch(Exception e)
        {
            System.err.println(e);
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public void pleaseStop()
    {
        stopFlag=true;
        log(new Timestamp(new java.util.Date().getTime()) + ": Segnale d'arresto ricevuto: in attesa del completamento dei client; Queue = " + queueCounter);
    }

    synchronized public Message elabMessage(Message in) throws SQLException
    {
        if( in.getMsg()==Message.REQUEST && stopFlag==true )
        {
            log(new Timestamp(new java.util.Date().getTime()) + ": Segnalato lo spegnimento ad un client; Queue = " + queueCounter);
            return new Message(Message.SHUTTINGDOWN);
        }
        if( in.getMsg()==Message.REQUEST )
        {
            log(new Timestamp(new java.util.Date().getTime()) + ": Richiesta di WU ricevuta... recupero in corso...");
            return wuTake(in);
        }
        else if( in.getMsg()==Message.RETURN )
        {
            log(new Timestamp(new java.util.Date().getTime()) + ": Ritorno di risultati in corso...");
            Message x = wuReturn(in);
            return x;
        }
        throw new RuntimeException("Ramo non permesso in elabMessage()!");
    }

    synchronized private Message wuTake(Message in) throws SQLException
    {
        if(dispWU()==false)
        {
            log(new Timestamp(new java.util.Date().getTime()) + ": WU terminate... finalizzazione server e DB");
            pleaseStop();
            return new Message(Message.NO_MORE_WUNITS);
        }
        else //ok, c'è almeno una WU disponibile
        {
            ResultSet rs = statement.executeQuery("select Hand, Locked, Done from Hands where Locked=0 AND Done=0 limit " + in.getNWU());

            HashSet<Hand> mani = new HashSet<Hand>();
                while(rs.next())
                    mani.add( new Hand(rs.getString("Hand")) );
            rs.close();

            for(Hand mano:mani)
                statement.executeUpdate("update Hands set Locked=1 where Hand='" + mano.printCarteNumeric() + "'");
            
            log(new Timestamp(new java.util.Date().getTime()) + ": Prodotta WU da " + mani.size() + " blocchi; Queue = " + queueCounter);
            queueCounter++;
            return new Message(Message.GO).setMani(mani);
        }
    }

    synchronized private Message wuReturn(Message in) throws SQLException
    {
        for(Hand mano:in.getMani())
        {
            ResultSet rs = statement.executeQuery("select * from Hands where Hand='" + mano.printCarteNumeric() + "'");

            if( rs.next()==false )       throw new SQLException("Mano non trovata!" + mano.printCarteNumeric());
            if( rs.getInt("Done")==1 )   throw new SQLException("Mano già calcolata!" + mano.printCarteNumeric());
            if( rs.getInt("Locked")==0 ) throw new SQLException("Mano non bloccata!" + mano.printCarteNumeric());

            statement.executeUpdate("update Hands set Locked=0, Done=1, SubNumber="+ mano.getSostituzione()
                    + ", Mean=" + mano.getMedia() + " where Hand='" + mano.printCarteNumeric() + "'");
            rs.close();
        }
        log(new Timestamp(new java.util.Date().getTime()) + ": Ricevute " + in.getMani().size() + " WU elaborate; Queue = " + queueCounter);
        
        queueCounter--;
        assert queueCounter>=0;
        if( stopFlag==true && queueCounter==0) //cioè siamo in chiusura del server e questa era l'ultima WU da ricevere
        {
            log(new Timestamp(new java.util.Date().getTime()) + ": Chiusura del DB...");
            try{ connection.close(); log(new Timestamp(new java.util.Date().getTime()) + ": Chiusura del DB effettuata.");}
            catch(Exception e){ log("Errore nella chiusura del DB!"); e.printStackTrace(); }
        }
        Message out = new Message();
        if( stopFlag==false ) out.setMsg(Message.RECEIVED);
        else out.setMsg(Message.RECEIVED_AND_SHUTTINGDOWN);
        return out;
    }
    
    //controlla se ci sono WU da poter mandare: DONE=0 AND LOCKED=0
    synchronized private boolean dispWU()
    {
        try{
            ResultSet rs = statement.executeQuery("select * from Hands where Locked=0 AND Done=0 limit 1");
            boolean res = rs.next();
            rs.close();
            log("checkWU() ci sono Done=0 AND Locked=0: " + res + " Queue = " + queueCounter);
            return res;
        }
        catch(Exception e)
        {
            System.err.println(e);
            e.printStackTrace();
            return false;
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