public class Parcheggio
{
	private String codice;
	private String nome;
	
	public Parcheggio(String codice, String nome)
	{
		this.codice = codice;
		this.nome = nome;
	}
	
	private String getCodice()
	{
		return codice;
	}
	
	private String getNome()
	{
		return nome;
	}
}