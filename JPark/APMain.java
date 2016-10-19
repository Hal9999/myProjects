import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

public class APMain
{
	//argv[0] è il nome del parcheggio, agrv[1] contiene l'indirizzo del server del parcheggio, argv[2] la porta del server
	//del parcheggio
	public static void main(String argv[])
	{
		Banca cassa = new Banca( 0.74F, 30*60 );
		
		//creo un nuovo oggetto AP che è in grado di interagire col server centrale e col server del parcheggio
		//l'interfaccia grafica userà i servizi di questo oggetti per fornire i suoi servizi.
		AP apoint = new AP( argv[0], "127.0.0.1", 50000, argv[1] , Integer.parseInt(argv[2]), cassa);
		
		//creo l'interfaccia e le passo l'oggetto tramite cui offrirà i suoi servizi
		APFrame apointFrame = new APFrame( apoint );
	}
}