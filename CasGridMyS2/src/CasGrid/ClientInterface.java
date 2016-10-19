package CasGrid;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.*;

public class ClientInterface extends JFrame implements Logger, ActionListener
{
    private Client client;
    private JButton tastoAvviaClient, tastoFermaClient;
    private JTextArea logMessaggi;

    public ClientInterface(Client client)
    {
        super();

        this.client=client;
        client.addLogger(this);

        setTitle("GridClient CAS2");
        setBounds(400, 400, 500, 500);
        setDefaultCloseOperation( EXIT_ON_CLOSE );

        setLayout( new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        tastoAvviaClient = new JButton("Avvia Client");
        tastoAvviaClient.addActionListener(this);
        tastoAvviaClient.setMinimumSize(new Dimension( 50, 80));
        tastoFermaClient = new JButton("Ferma Client");
        tastoFermaClient.addActionListener(this);
        tastoFermaClient.setMinimumSize(new Dimension( 50, 80));
        logMessaggi = new JTextArea();
        logMessaggi.setLineWrap(true);
        logMessaggi.setEditable(false);
        DefaultCaret caret = (DefaultCaret)logMessaggi.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane logscroll = new JScrollPane(logMessaggi);

        Container c = getContentPane();

        JPanel buttons = new JPanel( new FlowLayout(FlowLayout.CENTER) );
        buttons.add(tastoAvviaClient);
        tastoAvviaClient.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttons.add(tastoFermaClient);
        tastoFermaClient.setAlignmentX(Component.RIGHT_ALIGNMENT);
        buttons.setMaximumSize(new Dimension(500 ,200));
        c.add(buttons);
        c.add(logscroll);

        pack();
        setVisible(true);
    }

    public void actionPerformed(ActionEvent event)
    {
        Object source = event.getSource();

        if( source==tastoAvviaClient )
        {
            client.start(); //client avviato su un nuovo thread, altrimenti bloccherei quello dell'interfaccia
            logMessaggi.append("GUI: Client thread avviato...\n");
        }
        else if( source == tastoFermaClient)
        {
            client.pleaseStop(); //dico al client di arrestarsi
            logMessaggi.append("GUI: Segnale d'arresto inviato...\n");
        }
    }

    public void log(String msg)
    {
        logMessaggi.append(msg + "\n");
    }
}