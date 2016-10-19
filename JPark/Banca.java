import java.io.*;
import java.net.*;
import java.util.*;
import java.math.*;

public class Banca implements Ragioniere
{
	private float costoScatto;
	private float secondiScatto;
	
	public Banca(float costoOrario, float secondiScatto)
	{
		this.secondiScatto = secondiScatto;
		System.err.println(costoOrario);
		costoScatto = costoOrario*(secondiScatto/3600);
		System.err.println(costoScatto);
	}
	
	public float prezza(Date start, Date end)
	{
		float secondi = (end.getTime()-start.getTime())/1000;
		assert secondi >= 0;
		
		int scatti = (int)Math.ceil( secondi/secondiScatto );
		System.err.println(scatti);
		System.err.println(scatti*costoScatto);
		return scatti*costoScatto;
	}
}