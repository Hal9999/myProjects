import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class ServerParcheggioFrame extends JFrame implements ActionListener
{
	private GestoreParcheggi gestore;
	
	private JButton tastoStorico;
/*	private JButton tastoElencoAbbonamenti;
	private JButton tastoAbbonamentoMassimo;*/
	
	public ServerParcheggioFrame(GestoreParcheggi gestore)
	{
		super();
		
		this.gestore = gestore;

		setTitle( "Server Parcheggi ");
		setBounds(200, 200, 500, 500);
		setDefaultCloseOperation( EXIT_ON_CLOSE );
		
		setLayout( new GridLayout(3, 1) );
		
		tastoStorico = new JButton("Stampa storico");
		tastoStorico.addActionListener(this);
		/*tastoElencoAbbonamenti = new JButton("Elenca abbonamenti");
		tastoElencoAbbonamenti.addActionListener(this);
		tastoAbbonamentoMassimo = new JButton("Mostra abbonamento massimo");
		tastoAbbonamentoMassimo.addActionListener(this);*/
		
		Container c = getContentPane();
		c.add(tastoStorico); /* c.add(tastoElencoAbbonamenti); c.add(tastoAbbonamentoMassimo);*/
		
		pack();
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent ev)
	{
		Object s = ev.getSource();
		
		if( s == tastoStorico )
		{
			JOptionPane.showMessageDialog( null, "Elenco: \n" + gestore.elencaStorico(), "Storico", JOptionPane.INFORMATION_MESSAGE );	
		}
/*		else if( s == tastoElencoAbbonamenti )
		{
			JOptionPane.showMessageDialog( null, gestore.elencaAbbonamenti(), "Elenco Abbonamenti", JOptionPane.INFORMATION_MESSAGE);
		}
		else if( s == tastoAbbonamentoMassimo )
		{
			Abbonamento abb = gestore.trovaMassimo();
			JOptionPane.showMessageDialog( null, "Abbonamento massimo " + abb.getTarga() + " " + abb.getSaldo(), "Abbonamento massimo", JOptionPane.INFORMATION_MESSAGE );
		}*/
	}
}



























