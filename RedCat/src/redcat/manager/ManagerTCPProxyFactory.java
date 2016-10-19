package redcat.manager;

/**
 * Classe che si occupa gi fornire proxy TCP già configurati
 * 
 * @author Lanzafame Flavio, Russo Giorgia, Spadaro Stefano, Torrelli Denisia
 */
public class ManagerTCPProxyFactory extends ManagerProxyFactory
{
    /**
     * Restituisce un TCPProxy per la comunicazione in TCP.
     * @param address l'indirizzo a cui inviare
     * @param port la porta di comunicazione
     * @return il TCPProxy per la comunicazione
     */
    public ManagerProxy getProxyInstance(String address, int port)
    {
        return new TCPProxy(address, port);
    }
}