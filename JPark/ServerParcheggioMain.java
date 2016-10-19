import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

public class ServerParcheggioMain
{
	public static void main(String argv[])
	{
		GestoreParcheggi gestore = new GestoreParcheggi("Storico.txt");
		
		(new ServerParcheggioServer(gestore, 50001)).start();
		
		ServerParcheggioFrame frame = new ServerParcheggioFrame(gestore);
	}
}