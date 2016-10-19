package CasGrid;

import java.net.*;

public class SocketAccepter extends Thread
{
    private Server server;
    private Socket socket;

    public SocketAccepter(Server server, Socket socket)
    {
        this.server=server;
        this.socket=socket;
        this.start();
    }

    @Override
    public void run()
    {
        try
        {
            Dial d = new Dial(socket);
            d.sendAndClose( server.elabMessage( (Message)d.receive() ) );
        }
        catch(Exception e)
        {
            System.err.println(e);
            e.printStackTrace();
            System.exit(1);
        }
    }
}