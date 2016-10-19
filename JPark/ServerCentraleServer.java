import java.io.*;
import java.util.*;
import java.net.*;

public class ServerCentraleServer extends Thread
{
	private GestoreAbbonamenti gestore;
	private ServerSocket ss;
	
	public ServerCentraleServer( GestoreAbbonamenti gestore , int porta)
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
					(new ServerCentraleWorker(s, gestore)).start();
				}
				catch(Exception e)
				{
					System.err.println(e);
				}
	}
}