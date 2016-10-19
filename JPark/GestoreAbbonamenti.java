import java.io.*;
import java.util.*;

public class GestoreAbbonamenti
{
	private HashMap<String, Abbonamento> abbonamenti;
	private HashMap<String, Parcheggio> parcheggi;
	
	private String fileAbbonamenti;
	private String fileParcheggi;
	
	public GestoreAbbonamenti(String fileAbbonamenti, String fileParcheggi)
	{
		this.fileAbbonamenti = fileAbbonamenti;
		this.fileParcheggi = fileParcheggi;
		
		abbonamenti = new HashMap<String, Abbonamento>();
		parcheggi = new HashMap<String, Parcheggio>();
		
		caricaAbbonamenti();
		caricaParcheggi();
	}
	
	private void caricaAbbonamenti()
	{
		try
		{
			BufferedReader in = new BufferedReader( new FileReader(fileAbbonamenti) );
			
			String x, y;
			while(true)
			{
				x = in.readLine();
				if( x==null) break; //fine file
				y = in.readLine();
				
				abbonamenti.put(x, new Abbonamento(x, Float.parseFloat(y)));
			}
		}
		catch(Exception e)
		{
			System.err.println(e);
		}
	}
	
	private void caricaParcheggi()
	{
		try
		{
			BufferedReader in = new BufferedReader( new FileReader(fileParcheggi) );
			
			String x, y;
			while(true)
			{
				x = in.readLine();
				if( x==null) break; //fine file
				y = in.readLine();
				
				parcheggi.put(x, new Parcheggio(x, y));
			}
		}
		catch(Exception e)
		{
			System.err.println(e);
		}
	}
	
	synchronized public boolean nuovo(String targa, float somma)
	{
		if( !abbonamenti.containsKey(targa) ) //abbonamento non trovato
		{
			abbonamenti.put(targa, new Abbonamento(targa, somma) );
			return true;
		}
		return false;
	}
	
	synchronized public boolean ricarica(String targa, float somma)
	{
		if( abbonamenti.containsKey(targa) ) //abbonamento trovato
		{
			abbonamenti.get(targa).ricarica(somma);
			return true;
		}
		return false;
	}
	
	synchronized public boolean esiste(String targa)
	{
		return abbonamenti.containsKey(targa);
	}
	
	synchronized public boolean verifica(String targa)
	{
		if( abbonamenti.containsKey(targa) ) //abbonamento trovato
		{
			return abbonamenti.get(targa).getSaldo()>0;
		}
		return false;
	}
	
	synchronized public boolean paga(String targa, float somma)
	{
		//abbonamento trovato e saldo maggiore o uguale al dovuto da pagare
		if( abbonamenti.containsKey(targa) )
		{
			return abbonamenti.get(targa).paga(somma);
		}
		return false;
	}
	
	synchronized public float saldoAbbonamento(String targa)
	{
		return abbonamenti.get(targa).getSaldo();
	}
	
	synchronized public String elencaAbbonamenti()
	{
		String s = "Elenco degli abbonamenti\n\n";
		
		Set<String> set = abbonamenti.keySet();
		for(String a:set)
		{
			s += ( a + "\t" + abbonamenti.get(a).getSaldo() + "\n");
		}
		return s;
	}
	
	synchronized public Abbonamento trovaMassimo()
	{
		int saldo = -1;
		Abbonamento max = new Abbonamento("00000", 0);
		
		Set<String> set = abbonamenti.keySet();
		for(String a:set) if( abbonamenti.get(a).getSaldo()>saldo ) max = abbonamenti.get(a);
		return max;
	}
}

























