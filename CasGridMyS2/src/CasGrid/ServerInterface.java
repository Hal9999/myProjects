package CasGrid;

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.*;

public class ServerInterface extends JFrame implements Logger, ActionListener
{
    private Server server;
    private JButton tastoAvviaServer, tastoFermaServer;
    private JTextArea logMessaggi;

    public ServerInterface(Server server)
    {
        super();

        this.server=server;
        server.addLogger(this);

        setTitle("GridServer CAS2");
        setBounds(200, 200, 500, 500);
        setDefaultCloseOperation( EXIT_ON_CLOSE );

        setLayout( new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        tastoAvviaServer = new JButton("Avvia Server");
        tastoAvviaServer.addActionListener(this);
        tastoAvviaServer.setMinimumSize(new Dimension( 50, 80));
        tastoFermaServer = new JButton("Ferma Server");
        tastoFermaServer.addActionListener(this);
        tastoFermaServer.setMinimumSize(new Dimension( 50, 80));
        logMessaggi = new JTextArea();
        logMessaggi.setLineWrap(true);
        logMessaggi.setEditable(false);
        DefaultCaret caret = (DefaultCaret)logMessaggi.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane logscroll = new JScrollPane(logMessaggi);

        Container c = getContentPane();

        JPanel buttons = new JPanel( new FlowLayout(FlowLayout.CENTER) );
        buttons.add(tastoAvviaServer);
        tastoAvviaServer.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttons.add(tastoFermaServer);
        tastoFermaServer.setAlignmentX(Component.RIGHT_ALIGNMENT);
        buttons.setMaximumSize(new Dimension(500 ,200));
        c.add(buttons);
        c.add(logscroll);

        pack();
        setVisible(true);
    }

    public void actionPerformed(ActionEvent event)
    {
        Object source = event.getSource();

        if( source==tastoAvviaServer )
        {
            server.start(); //server avviato su un nuovo thread, altrimenti bloccherei quello dell'interfaccia
            logMessaggi.append("GUI: Server thread avviato...\n");
        }
        else if( source == tastoFermaServer)
        {
            server.pleaseStop(); //dico al server di arrestarsi
            logMessaggi.append("GUI: Segnale d'arresto inviato...\n");
        }
    }

    public void log(String msg)
    {
        logMessaggi.append(msg + "\n");
    }
}
