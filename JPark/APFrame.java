import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class APFrame extends JFrame implements ActionListener
{
	private AP apoint;
	
	private JButton tastoRicarica;
	private JButton tastoEntra;
	private JButton tastoEsce;
	
	public APFrame(AP apoint)
	{
		super();
		
		this.apoint = apoint;

		setTitle( "Punto di accesso del parcheggio " + apoint.getNome() );
		setBounds(200, 200, 500, 500);
		setDefaultCloseOperation( EXIT_ON_CLOSE );
		
		setLayout( new GridLayout(3, 1) );
		
		tastoRicarica = new JButton("Ricarica Abbonamento");
		tastoRicarica.addActionListener(this);
		tastoEntra = new JButton("Entra Auto");
		tastoEntra.addActionListener(this);
		tastoEsce = new JButton("Esce Auto");
		tastoEsce.addActionListener(this);
		
		Container c = getContentPane();
		c.add(tastoRicarica); c.add(tastoEntra); c.add(tastoEsce);
		
		pack();
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent ev)
	{
		Object s = ev.getSource();
		
		       if( s == tastoRicarica )
		{
			String targa = JOptionPane.showInputDialog("Inserisci la targa dell'auto da ricaricare");
			
			int somma;
			while(true) try
					{
						somma = Integer.parseInt( JOptionPane.showInputDialog(null, "Inserisci l'ammontare della ricarica") );
						break;
					}
					catch(Exception e)
					{
						System.err.println(e);
					}
			
			//richiedo la ricarica all'oggetto AP, che chiederà al server centrale e risponderà
			//AP tornerà un Messaggio con dentro l'esito della ricarica.
			Messaggio msg = apoint.ricarica(targa, somma);
			
			//mostro l'esito della ricarica
			JOptionPane.showMessageDialog( null, msg.getTesto(), "Ricarica", (msg.getEsito() ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE) );
		}
		else if( s == tastoEntra )
		{
			String targa = JOptionPane.showInputDialog("Inserisci la targa dell'auto che vuole entrare");
			
			//chiedo, tramite AP, se la macchina in questione ha un abbonamneto con diosponibilià
			Messaggio msg = apoint.entraAuto(targa);
			
			//mostro l'esito della richiesta di parcheggio
			JOptionPane.showMessageDialog( null, msg.getTesto(), "Entra Auto", (msg.getEsito() ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE) );
		}
		else if( s == tastoEsce )
		{
			String targa = JOptionPane.showInputDialog("Inserisci la targa dell'auto che vuole uscire");
			
			//chiedo, tramite AP, se la macchina in questione ha un abbonamneto con diosponibilià
			Messaggio msg = apoint.esceAuto(targa);
			
			//mostro l'esito della richiesta di parcheggio
			JOptionPane.showMessageDialog( null, msg.getTesto(), "Esce Auto", (msg.getEsito() ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE) );
		}
	}
}












