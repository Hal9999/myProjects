import java.io.*;
import java.util.*;
import java.net.*;

public class ServerParcheggioWorker extends Thread
{
	private GestoreParcheggi gestore;
	private Socket s;
	
	public ServerParcheggioWorker(Socket s, GestoreParcheggi gestore)
	{
		this.gestore = gestore;
		this.s = s;
	}

	public void run()
	{	
		try
		{
			ObjectInputStream in = new ObjectInputStream( s.getInputStream() );
			ObjectOutputStream out = new ObjectOutputStream( s.getOutputStream() );
			
			//legge in messaggio in arrivo
			Messaggio msg = (Messaggio)in.readObject();
			String op = msg.getOperazione();
			
			if( op.equals("ingressoAuto") )
			{
				if( gestore.entraAuto(msg.getTarga(), msg.getData()) )
				{
					msg.setEsito(true);
					msg.setTesto("Auto inserita nel parcheggio");
				}
				else
				{
					msg.setEsito(false);
					msg.setTesto("L'auto è già presente nel parcheggio");
				}
			}
			else if( op.equals("cercaAuto") )
			{
				Date inizio = gestore.cercaAuto(msg.getTarga());
				if( inizio != null )
				{
					msg.setEsito(true);
					msg.setTesto("L'auto è prensente nel parcheggio");
					msg.setData(inizio);
				}
				else
				{
					msg.setEsito(false);
					msg.setTesto("Errore: la macchina non è presente nel parcheggio");
				}
			}
			else if( op.equals("uscitaAuto") )
			{
				if( gestore.esceAuto(msg.getTarga(), msg.getData()) )
				{
					msg.setEsito(true);
					msg.setTesto("L'auto può uscire, transazione conclusa");
				}
				else
				{
					msg.setEsito(false);
					msg.setTesto("Errore: l'auto non è presente nel parcheggio");
				}
			}
			
			out.writeObject( msg );
			out.flush();
			in.close(); out.close(); s.close();
		}
		catch(Exception e)
		{
			System.err.println(e);
		}
	}
}





















