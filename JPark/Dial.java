import java.io.*;
import java.net.*;
import java.util.*;
import java.math.*;

public class Dial
{
	private Socket s;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	
	public Dial(InetSocketAddress addr) throws Exception
	{
		s = new Socket(); s.connect( addr );
		
		out = new ObjectOutputStream( s.getOutputStream() );
		in = new ObjectInputStream( s.getInputStream() );
	}
	
	public void send(Object x) throws Exception
	{
		out.writeObject(x);
		out.flush();
	}
	
	public Object receive() throws Exception
	{
		return in.readObject();
	}
	
	public void close() throws Exception
	{
		in.close();
		out.close();
		s.close();
	}
	
	public static Object dReceive(InetSocketAddress addr) throws Exception
	{
		Dial d = new Dial(addr);
		Object o = d.receive();
		d.close();
		return o;
	}
		
	public static void dSend(InetSocketAddress addr, Object o) throws Exception
	{
		Dial d = new Dial(addr);
		d.send(o);
		d.close();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T talk(InetSocketAddress addr, T obj) throws Exception
	{
		Dial d = new Dial(addr);
		d.send(obj);
		T o = (T) d.receive();
		d.close();
		return o;
	}
}