package redcat.manager;

/**
 * Classe che si occupa di fornire proxy UDP già configurati
 * 
 * @author Lanzafame Flavio, Russo Giorgia, Spadaro Stefano, Torrelli Denisia
 */
public class ManagerUDPProxyFactory extends ManagerProxyFactory
{
    /**
     * Restituisce un UDPProxy per la comunicazione in UDP.
     * @param address l'indirizzo a cui inviare
     * @param port la porta di comunicazione
     * @return il UDPProxy per la comunicazione
     */
    public ManagerProxy getProxyInstance(String address, int port)
    {
        return new UDPProxy(address, port);
    }
}