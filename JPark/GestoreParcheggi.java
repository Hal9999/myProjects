import java.io.*;
import java.net.*;
import java.util.*;

public class GestoreParcheggi
{
	String fileStorico;
	TreeSet<TuplaParcheggio> storico;
	HashMap<String, TuplaParcheggio> parcheggio;
	
	public GestoreParcheggi(String fileStorico)
	{
		this.fileStorico = fileStorico;
		storico = new TreeSet<TuplaParcheggio>();
		parcheggio = new HashMap<String, TuplaParcheggio>();
		
		caricaStorico();
	}
	
	private void caricaStorico()
	{
		try
		{
			ObjectInputStream in = new ObjectInputStream( new FileInputStream(fileStorico) );
			
			TuplaParcheggio t;
			while(true)
			{
				t = (TuplaParcheggio) in.readObject();
				storico.add(t);
			}
		}
		catch(FileNotFoundException e)
		{
			System.err.println(e);
			System.err.println("File storico inesistente");
		}
		catch(Exception e)
		{
			System.err.println(e);
		}
	}
	
	synchronized public boolean entraAuto(String targa, Date inizio)
	{
		if( parcheggio.containsKey(targa) ) return false; //macchina già presente

		parcheggio.put( targa, new TuplaParcheggio(targa, inizio) );
		return true;
	}
	
	synchronized public Date cercaAuto(String targa)
	{
		if( !parcheggio.containsKey(targa) ) return null;
		
		return parcheggio.get(targa).getInizio();
	}
	
	synchronized public boolean esceAuto(String targa, Date fine)
	{
		if( !parcheggio.containsKey(targa) ) return false;
		
		TuplaParcheggio t = parcheggio.get(targa);
		parcheggio.remove(targa);
		t.setFine(fine);
		storico.add(t);
		return true;
	}
	
	synchronized public String elencaStorico()
	{
		String s = "Storico dei parcheggi\n";
		for(TuplaParcheggio t:storico) s += t.getTarga() + " " + t.getInizio() + " " + t.getFine() + "\n";
		return s;
	}
}