package CasGrid;

import java.net.*;

public class ClientMain
{
    public static void main(String[] args) throws UnknownHostException
    {
        try
        {
            //int x=-1;
            if( args.length!=4 ) throw new IllegalArgumentException("4 parametri: url porta nThreads n° Blocchi per WU");
            int port = Integer.parseInt(args[1]);
            int nT = Integer.parseInt(args[2]);
            int nWU = Integer.parseInt(args[3]);
            InetSocketAddress address = new InetSocketAddress(args[0], port);

            Client client = new Client(address, nT, nWU);
            ClientInterface interfaccia = new ClientInterface(client);
        }
        catch(IllegalArgumentException e)
        {
            System.err.println(e);
            System.err.println(args.length);
            for( int i=0; i<args.length; i++ )
            {
                System.err.println(i + " -> " + args[i]);
            }
        }
    }
}