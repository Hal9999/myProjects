import java.io.*;
import java.net.*;
import java.util.*;

public class AP
{
	private String nomeParcheggio;
	private InetSocketAddress addrServerCentrale;
	private InetSocketAddress addrServerParcheggio;
	
	private Ragioniere ragioniere;
	
	
	public AP( String nomeParcheggio, String serverCentrale, int portaCentrale, String serverParcheggio, int portaParcheggio, Ragioniere ragioniere)
	{
		this.nomeParcheggio = nomeParcheggio;
		this.ragioniere = ragioniere;
		
		//risolvo gli indirizzi dei server
		addrServerCentrale = new InetSocketAddress(serverCentrale, portaCentrale);
		addrServerParcheggio = new InetSocketAddress(serverParcheggio, portaParcheggio);
	}
	
	public String getNome()
	{
		return nomeParcheggio;
	}
	
	public Messaggio ricarica( String targa, int somma )
	{
		Messaggio msg = new Messaggio();
			msg.setOperazione("ricarica");
			msg.setTarga( targa );
			msg.setSomma( somma );
		
		try
		{
			msg = Dial.talk( addrServerCentrale, msg );
		}
		catch(Exception e)
		{
			System.err.println(e);
			
			msg.setEsito(false);
			msg.setTesto("Comunicazione col server fallita");
		}
		
		return msg;
	}
	
	public Messaggio entraAuto( String targa )
	{
		Messaggio msg = new Messaggio();
			msg.setOperazione("verificaAbbonamento");
			msg.setTarga( targa );
		
		try
		{
			msg = Dial.talk( addrServerCentrale, msg );
			
			//se la verifica dell'abbonamento è andata a buon fine procediamo alla comunicazione col server del parcheggio
			if( msg.getEsito() )
			{
				msg = new Messaggio();
					msg.setOperazione("ingressoAuto");
					msg.setTarga( targa );
					msg.setData( new Date() );
				
				msg = Dial.talk( addrServerParcheggio, msg );
			}
		}
		catch(Exception e)
		{
			System.err.println(e);
			
			msg.setEsito(false);
			msg.setTesto("Comunicazione col server fallita");
		}
				
		return msg;
	}
	
	public Messaggio esceAuto( String targa )
	{
		Date now = new Date();
		
		Messaggio msg = new Messaggio();
			msg.setOperazione("cercaAuto");
			msg.setTarga( targa );
		
		try
		{
			
			//chiediamo al server del parcheggio a che ora è entrata l'auto
			msg = Dial.talk( addrServerParcheggio, msg );
			System.err.println("p1");
			//se il server del parcheggio ci ha confermato che la macchina è dentro...
			if( msg.getEsito() )
			{
				float costo = ragioniere.prezza( msg.getData(), now );
				
				//tentiamo il pagamento
				msg = new Messaggio();
					msg.setOperazione("pagamento");
					msg.setTarga(targa);
					msg.setSomma(costo);
				
				msg = Dial.talk( addrServerCentrale, msg );
				
				//se il pagamento è andato bene, confermiamo l'uscita dell'auto dal parcheggio al server del parcheggio
				if( msg.getEsito() )
				{
					msg = new Messaggio();
						msg.setOperazione("uscitaAuto");
						msg.setTarga(targa);
						msg.setData(now);
				
					msg = Dial.talk( addrServerParcheggio, msg );
				}
			}
		}
		catch(Exception e)
		{
			System.err.println(e);
			e.printStackTrace();
			
			msg.setEsito(false);
			msg.setTesto("Comunicazione col server fallitax");
		}
				
		return msg;
	}
}











