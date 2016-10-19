public class Abbonamento
{
	private String targa;
	private float saldo;
	
	public Abbonamento(String targa, float saldo)
	{
		this.targa = targa;
		this.saldo = saldo;
	}
	
	public void ricarica(float somma)
	{
		saldo += somma;
	}
	
	public boolean paga(float somma)
	{
		if( saldo >= somma )
		{
			saldo -= somma;
			return true;
		}
		return false;
	}
	
	public float getSaldo()
	{
		assert saldo >= 0;
		return saldo;
	}
	
	public String getTarga()
	{
		return targa;
	}
}