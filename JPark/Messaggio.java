import java.io.*;
import java.net.*;
import java.util.*;

public class Messaggio implements Serializable
{
	private String testo;
	private String operazione;
	private String targa;
	private float somma;
	private Date data;
	private boolean esito;
	
	public void setTesto(String testo) { this.testo = testo; }
	public void setOperazione(String operazione) { this.operazione = operazione; }	
	public void setTarga(String targa) { this.targa = targa; }	
	public void setSomma(float somma) { this.somma = somma; }
	public void setData(Date data) { this.data = data; }
	public void setEsito(boolean esito) { this.esito = esito; }
	
	public String getTesto() { return testo; }
	public String getOperazione() { return operazione; }	
	public String getTarga() { return targa; }	
	public float getSomma() { return somma; }
	public Date getData() { return data; }
	public boolean getEsito() { return esito; }
}