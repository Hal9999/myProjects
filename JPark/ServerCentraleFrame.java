import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class ServerCentraleFrame extends JFrame implements ActionListener
{
	private GestoreAbbonamenti gestore;
	
	private JButton tastoSaldoAbbonamento;
	private JButton tastoElencoAbbonamenti;
	private JButton tastoAbbonamentoMassimo;
	
	public ServerCentraleFrame(GestoreAbbonamenti gestore)
	{
		super();
		
		this.gestore = gestore;

		setTitle( "Server Abbonamenti ");
		setBounds(200, 200, 500, 500);
		setDefaultCloseOperation( EXIT_ON_CLOSE );
		
		setLayout( new GridLayout(3, 1) );
		
		tastoSaldoAbbonamento = new JButton("Cerca abbonamento");
		tastoSaldoAbbonamento.addActionListener(this);
		tastoElencoAbbonamenti = new JButton("Elenca abbonamenti");
		tastoElencoAbbonamenti.addActionListener(this);
		tastoAbbonamentoMassimo = new JButton("Mostra abbonamento massimo");
		tastoAbbonamentoMassimo.addActionListener(this);
		
		Container c = getContentPane();
		c.add(tastoSaldoAbbonamento); c.add(tastoElencoAbbonamenti); c.add(tastoAbbonamentoMassimo);
		
		pack();
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent ev)
	{
		Object s = ev.getSource();
		
		if( s == tastoSaldoAbbonamento )
		{
			String targa = JOptionPane.showInputDialog("Inserisci la targa dell'auto");
			
			if( gestore.esiste(targa) )
			{
				JOptionPane.showMessageDialog( null, "" + "Il saldo è " + gestore.saldoAbbonamento(targa), "Saldo", JOptionPane.INFORMATION_MESSAGE );
			}
			else
			{
				JOptionPane.showMessageDialog( null, "Abbonamento non trovato", "Saldo", JOptionPane.ERROR_MESSAGE );
			}
				
		}
		else if( s == tastoElencoAbbonamenti )
		{
			JOptionPane.showMessageDialog( null, gestore.elencaAbbonamenti(), "Elenco Abbonamenti", JOptionPane.INFORMATION_MESSAGE);
		}
		else if( s == tastoAbbonamentoMassimo )
		{
			Abbonamento abb = gestore.trovaMassimo();
			JOptionPane.showMessageDialog( null, "Abbonamento massimo " + abb.getTarga() + " " + abb.getSaldo(), "Abbonamento massimo", JOptionPane.INFORMATION_MESSAGE );
		}
	}
}



























