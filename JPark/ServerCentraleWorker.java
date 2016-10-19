import java.io.*;
import java.util.*;
import java.net.*;

public class ServerCentraleWorker extends Thread
{
	private GestoreAbbonamenti gestore;
	private Socket s;
	
	public ServerCentraleWorker(Socket s, GestoreAbbonamenti gestore)
	{
		this.gestore = gestore;
		this.s = s;
	}

	public void run()
	{	
		try
		{
			System.err.println("ricarica -1");
			ObjectOutputStream out = new ObjectOutputStream( s.getOutputStream() );
			
			System.err.println("ricarica A");
			ObjectInputStream in = new ObjectInputStream( s.getInputStream() );
			
			System.err.println("ricarica -1");
			
			Messaggio msg = (Messaggio)in.readObject();
			String op = msg.getOperazione();
			
			System.err.println("ricarica 0");
			
			if( op.equals("ricarica") )
			{
				if( gestore.ricarica(msg.getTarga(), msg.getSomma()) )
				{
					msg.setEsito(true);
					msg.setTesto("Ricarica effettuata con successo");
					System.err.println("ricarica 1");
				}
				else
				{
					msg.setEsito(false);
					msg.setTesto("Ricarica fallita: account non trovato");
					System.err.println("ricarica 2");
				}
			}
			else if( op.equals("verificaAbbonamento") )
			{
				if( gestore.esiste(msg.getTarga()) )
				{
					if( gestore.verifica(msg.getTarga()) )
					{
						msg.setEsito(true);
						msg.setTesto("Disponibilità verificata");
					}
					else
					{
						msg.setEsito(false);
						msg.setTesto("Errore: l'account esiste ma il saldo è zero");
					}
				}
				else
				{
					msg.setEsito(false);
					msg.setTesto("Errore: l'account non esiste");
				}
			}
			else if( op.equals("pagamento") )
			{
				if( gestore.esiste(msg.getTarga()) )
				{
					if( gestore.paga(msg.getTarga(), msg.getSomma()) )
					{
						msg.setEsito(true);
						msg.setTesto("Pagamento effettuato con successo");
					}
					else
					{
						msg.setEsito(false);
						msg.setTesto("Errore: l'account esiste ma il saldo non è sufficiente");
					}
				}
				else
				{
					msg.setEsito(false);
					msg.setTesto("Errore: l'account non esiste");
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





















