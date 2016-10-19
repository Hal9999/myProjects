import java.io.*;
import java.util.*;
import java.net.*;

public class ServerParcheggioServer extends Thread
{
	private GestoreParcheggi gestore;
	private ServerSocket ss;
	
	public ServerParcheggioServer( GestoreParcheggi gestore , int porta)
	{
		this.gestore = gestore;
		
		try
		{
			ss = new ServerSocket(porta);
		}
		catch(Exception e)
		{
			System.err.println(e);
			System.exit(1);
		}
	}
	
	public void run()
	{
		while(true) try
				{
					Socket s = ss.accept();
					(new ServerParcheggioWorker(s, gestore)).start();
				}
				catch(Exception e)
				{
					System.err.println(e);
				}
	}
}