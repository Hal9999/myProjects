package redcat.manager;

import redcat.common.event.*;

/**
 * Classe astratta che si occupa della scelta del tipo di comunicazione in base
 * alla priorità dell'ExtendedEvent passatogli.
 * 
 * @author Lanzafame Flavio, Russo Giorgia, Spadaro Stefano, Torrelli Denisia
 */
public abstract class ManagerProxyFactory
{
    /**
     * Crea un proxy per la comunicazione in base alla priorità dell'evento
     * passato come argomento.
     * @param address indirizzo del Manager remoto
     * @param event l'evento da controllare per la scelta del tipo di comunicazione
     * @param tcpPort numero della porta per la comunicazione TCP
     * @param udpPort numero della porta per la comunicazione UDP
     * @return il ManagerProxy da usare per eseguire l'effettivo invio dell'evento
     * @see ManagerTCPProxyFactory
     * @see ManagerUDPProxyFactory
     */
    public static ManagerProxy getProxy(String address, int tcpPort, int udpPort, ExtendedEvent event)
    {
        if( event.priority <= 2)
            return new ManagerTCPProxyFactory().getProxyInstance(address, tcpPort);
        else
            return new ManagerUDPProxyFactory().getProxyInstance(address, udpPort);
    }
}
