import java.io.*;
import java.net.*;
import java.util.*;

public class TuplaParcheggio implements Comparable<TuplaParcheggio>
{
	private String targa;
	private Date inizio;
	private Date fine;
	
	public TuplaParcheggio(String targa, Date inizio, Date fine)
	{
		this.targa = targa;
		this.inizio = inizio;
		this.fine = fine;
	}
	
	public TuplaParcheggio(String targa, Date inizio)
	{
		this(targa, inizio, null);
	}
	
	public String getTarga()
	{
		return targa;
	}
	
	public Date getInizio()
	{
		return inizio;
	}
	
	public Date getFine()
	{
		return fine;
	}
	
	public void setFine(Date fine)
	{
		this.fine = fine;
	}
	
	public int compareTo(TuplaParcheggio t)
	{
		long x = t.inizio.getTime() - this.inizio.getTime();
		if ( x>0 ) return 1;
		else if ( x<0 ) return -1;
		return 0;
	}
}