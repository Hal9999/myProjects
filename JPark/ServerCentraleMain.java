import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

public class ServerCentraleMain
{
	public static void main(String argv[])
	{
		GestoreAbbonamenti gestore = new GestoreAbbonamenti("Abbonamenti.txt", "Parcheggi.txt");
		
		(new ServerCentraleServer(gestore, 50000)).start();
		
		ServerCentraleFrame frame = new ServerCentraleFrame(gestore);
	}
}